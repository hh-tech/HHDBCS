package com.hh.hhdb_admin.mgr.gis.util;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 导入GIS数据到数据库
 */
public class ImpGis {
    private static String logName = ImpGis.class.getSimpleName();
    private static Properties typepr = new Properties();
    private Connection conn;
    
    static {
        String typepath = "etc/gisFile/type.properties";
        try {
            InputStream typeis = new FileInputStream(typepath);
            typepr.load(typeis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ImpGis(JdbcBean jdbcBean) {
        try {
            this.conn = ConnUtil.getConn(jdbcBean);
            
            //选择导入到那个模式
            Object[] schemaName = SqlWinUtil.getSchemaNameList(conn).toArray();
            Object schema = JOptionPane.showInputDialog(null, GisMgr.getLang("schema"), GisMgr.getLang("storageLocation"), JOptionPane.PLAIN_MESSAGE, null, schemaName, schemaName[0]);
            if (schema != null) {
                JFileChooser chooser = new JFileChooser();
                chooser.setCurrentDirectory(chooser.getSelectedFile());
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式
                chooser.setMultiSelectionEnabled(true);//可多选
                FileNameExtensionFilter filter = new FileNameExtensionFilter("SHP(*.shp)", "shp");
                chooser.setFileFilter(filter);
                int resultFile = chooser.showSaveDialog(null);
                if (resultFile == JFileChooser.APPROVE_OPTION) {
                    File[] files = chooser.getSelectedFiles();
                    if (files != null) execute(schema.toString(), files);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void execute(String schema, File[] files) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (File file : files) {
                        if (!file.isFile()) {
                            JOptionPane.showMessageDialog(null, GisMgr.getLang("PathError"), GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        FileDataStore store = FileDataStoreFinder.getDataStore(file);
                        SimpleFeatureSource featureSource = store.getFeatureSource();
                        SimpleFeatureCollection features = featureSource.getFeatures();
                        Map<String, String> parmap = new HashMap<String, String>();
                        ArrayList<String> parmapkeys = new ArrayList<String>();
                        parmap.put("gid", "serial");
                        parmapkeys.add("gid");
                        
                        SimpleFeatureType sft = features.getSchema();
                        for (int i = 0; i < sft.getAttributeCount(); i++) {
                            String tagstr = sft.getType(i).toString().split(" ")[0];
                            String parname = sft.getType(i).getName().toString();
                            String typestr = sft.getType(i).toString().split(" ")[1];
                            String type = typestr.substring(typestr.indexOf("<") + 1, typestr.indexOf(">"));
                            if (tagstr.equals("GeometryTypeImpl")) {
                                parname = "geom";
                            }
                            parmapkeys.add(parname);
                            parmap.put(parname, type2hhdb(type));
                        }
                        String prefix = DriverUtil.getDbType(conn).equals(DBTypeEnum.hhdb) ? "hh" : "pg";
                        String tableName = prefix + "_" + sft.getTypeName();
                        //创建数据库表sql
                        StringBuilder sbd = new StringBuilder();
                        for (String key : parmapkeys) {
                            if (sbd.length() != 0) sbd.append(",");
                            if (key.equals("geom")) {
                                sbd.append(key + " geometry(" + parmap.get(key) + ")");
                            } else {
                                sbd.append(key + " " + parmap.get(key));
                            }
                        }
                        
                        conn.setAutoCommit(false);
                        exesql("CREATE TABLE " + schema + "." + tableName + " (" + sbd.toString() + ");");
                        exesql("ALTER TABLE " + schema + "." + tableName + " ADD PRIMARY KEY (gid);");
                        //添加数据
                        StringBuilder insertsb = new StringBuilder();
                        insertsb.append("insert into " + schema + "." + tableName + " (");
                        for (int i = 1; i < parmapkeys.size(); i++) {
                            insertsb.append(parmapkeys.get(i));
                            if (i < parmapkeys.size() - 1) {
                                insertsb.append(",");
                            }
                        }
                        insertsb.append(") values(");
                        String insertstr = insertsb.toString();
                        try (SimpleFeatureIterator iterator = features.features()) {
                            while (iterator.hasNext()) {
                                SimpleFeature c = iterator.next();
                                StringBuilder sb = new StringBuilder();
                                sb.append(insertstr);
                                for (int i = 0; i < c.getAttributeCount(); i++) {
                                    String type = parmap.get(parmapkeys.get(i + 1));
                                    String value = c.getAttribute(i).toString();
                                    if (type.equals("smallint") || type.equals("integer") || type.equals("bigint") || type.equals("float8")) {
                                        sb.append(value + ",");
                                    } else {
                                        if (value.contains("'")) {
                                            value = value.replaceAll("'", "''");
                                        }
                                        sb.append("'" + value + "',");
                                    }
                                }
                                String insertsql = sb.toString().substring(0, sb.toString().length() - 1) + ");";
                                exesql(insertsql);
                            }
                        }
                        store.dispose();
                    }
                    conn.commit();
                    JOptionPane.showMessageDialog(null, GisMgr.getLang("ImportSuccess"), GisMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    logUtil.error(logName, e);
                    JOptionPane.showMessageDialog(null, e, GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
                } finally {
                    ConnUtil.close(conn);
                }
            }
        }).start();
    }
    
    private void exesql(String sql) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.execute();
    }
    
    private String type2hhdb(String typestr) {
        String str = typestr.toLowerCase();
        if (typepr.get(str) == null) {
            return typestr;
        } else {
            return typepr.get(str).toString();
        }
    }
}
