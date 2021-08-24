package com.hh.hhdb_admin.mgr.gis.util;

import com.hh.frame.common.base.JdbcBean;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import com.vividsolutions.jts.geom.Geometry;
import org.geotools.data.*;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * 地图导出
 */
public class ExpGis {
    private static String logName = ExpGis.class.getSimpleName();
    private JdbcBean jdbcBean;
    
    public ExpGis(JdbcBean jdbcBean, final Set<String> set, final File file) {
        this.jdbcBean = jdbcBean;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    exportToShapefile(file, set);
                    JOptionPane.showMessageDialog(null, GisMgr.getLang("ExportSuccess"), GisMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, GisMgr.getLang("CancelSuccess"), GisMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    logUtil.error(logName, e);
                    JOptionPane.showMessageDialog(null, e, GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
    }
    
    private void exportToShapefile(File file, Set<String> set) throws IOException {
        for (String str : set) {
            int o = str.indexOf(".");
            String schemaname = str.substring(0, o);
            String tableName = str.substring(o + 1);
            File newfile = new File(file + "\\" + tableName + ".shp");
            DataStore dataStore = DataStoreFinder.getDataStore(GisUtil.getLogInfo(jdbcBean, schemaname));
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(tableName);
            SimpleFeatureCollection featureCollection = featureSource.getFeatures();
            DataStoreFactorySpi factory = new ShapefileDataStoreFactory();
            Map<String, Serializable> create = new HashMap<>();
            create.put("url", newfile.toURI().toURL());
            create.put("create spatial index", Boolean.TRUE);
            dataStore = factory.createNewDataStore(create);
            SimpleFeatureType featureType = featureCollection.getSchema();
            //空间字段所在的位置
            int geomindex = 0;
            //判断空间字段所在的位置
            for (int i = 0; i < featureType.getAttributeCount(); i++) {
                String tagstr = featureType.getType(i).toString().split(" ")[0];
                if (tagstr.equals("GeometryTypeImpl")) {
                    //空间数据
                    geomindex = i;
                    break;
                }
            }
            dataStore.createSchema(featureType);
            
            // 得到新的Shapefile的名称,这将被用来打开FeatureWriter
            String createdName = dataStore.getTypeNames()[0];
            
            Transaction transaction = new DefaultTransaction("Reproject");
            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer = dataStore
                    .getFeatureWriterAppend(createdName, transaction);
                 SimpleFeatureIterator iterator = featureCollection.features()) {
                while (iterator.hasNext()) {
                    SimpleFeature feature = iterator.next();
                    Geometry geometry = (Geometry) feature.getDefaultGeometry();
                    
                    //更换参数位置，将空间参数放在第一个。
                    if (geomindex != 0) {
                        List<Object> pars = new ArrayList<>();
                        pars.add(feature.getAttribute(geomindex));
                        for (int i = 0; i < feature.getAttributeCount(); i++) {
                            if (geomindex != i) {
                                pars.add(feature.getAttribute(i));
                            }
                        }
                        feature.setAttributes(pars);
                    }
                    SimpleFeature copy = writer.next();
                    copy.setAttributes(feature.getAttributes());
                    
                    copy.setDefaultGeometry(geometry);
                    writer.write();
                }
                transaction.commit();
                writer.close();
            } catch (IOException problem) {
                logUtil.error(logName, problem);
                transaction.rollback();
                JOptionPane.showMessageDialog(null, problem, GisMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
            } finally {
                dataStore.dispose();
                transaction.close();
            }
        }
    }
}
