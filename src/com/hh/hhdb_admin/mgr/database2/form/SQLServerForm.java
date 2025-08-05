package com.hh.hhdb_admin.mgr.database2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.DatabaseSqlUtil;
import com.hh.frame.db_sql.sqlserver.SqlserverUserSqlUtil;
import com.hh.frame.db_sql.sqlserver.info.SqlserverDataBaseBean;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SQLServerForm extends AbsForm {
    public final HTable table;
    private final SelectBox userSelectBox;

    public SQLServerForm(JdbcBean jdbc, Connection conn) throws Exception {
        super(jdbc,conn);
        nameInput = new TextInput(){
            @Override
            protected void doChange() {
                table.load(getTableData(getValue(), getValue() + "_log"), 1);
            }
        };
        nameInput.getComp().setColumns(20);
        
        table = new HTable();
        table.setRowHeight(25);
        table.hideSeqCol();
        DataCol typeCol = new DataCol("file_type", LangMgr.lang(Word4.FILE_TYPE));
        typeCol.setCellEditable(false);
        table.addCols(new DataCol("logic_name", LangMgr.lang(Word4.FILE_NAME)),typeCol,
                new DataCol("init_size", LangMgr.sLang(CsL.INIT_SIZE)),
                new GrowthSettingPanel("auto_incr", LangMgr.lang(Word4.AUTO_INCREMENT)),
                new DataCol("file_path", LangMgr.lang(Word2.PATH)));
        table.load(getTableData("", "_log"), 1);
        userSelectBox = new SelectBox("user");
        userSelectBox.addOption("", "");
        List<String> userMaps = SqlQueryUtil.selectOneColumn(conn, SqlserverUserSqlUtil.getSelUserNamesSql(version));
        for (String name : userMaps) {
            userSelectBox.addOption(name, name);
        }
    }

    public List<Map<String, String>> getTableData(String logName1, String logName2) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> rowOneMap = new HashMap<>();
        rowOneMap.put("logic_name", logName1);
        rowOneMap.put("file_type", "ROWS");
        rowOneMap.put("init_size", "10");
        JsonObject json = new JsonObject();
        json.add(JsonCol.__TEXT, LangMgr.sLang(CsL.UNLIMITED_GROWTH));
        json.add("fileGrowth", "50");
        json.add("fileGrowthUnit","MB");
        json.add("maxSize", "UNLIMITED");
        json.add("maxSizeUnit","KB");
        rowOneMap.put("auto_incr", json.toPrettyString());
        Map<String, String> rowTwoMap = new HashMap<>();
        rowTwoMap.put("logic_name", logName2);
        rowTwoMap.put("file_type", "LOG");
        rowTwoMap.put("init_size", "1");
        json = new JsonObject();
        json.add(JsonCol.__TEXT, LangMgr.sLang(CsL.UNLIMITED_GROWTH2));
        json.add("fileGrowth", "10");
        json.add("fileGrowthUnit","%");
        json.add("maxSize", "UNLIMITED");
        json.add("maxSizeUnit","KB");
        rowTwoMap.put("auto_incr", json.toPrettyString());
        try {
            List<String> lists = SqlQueryUtil.selectOneColumn(conn, "select filename from sysfiles");
            String fileName = (lists.get(0));
            fileName = fileName.substring(0, fileName.indexOf("DATA") + 4);
            rowOneMap.put("file_path", fileName);
            rowTwoMap.put("file_path", fileName);
        } catch (SQLException e) {
            PopDlgUtil.logErr(getClass(),table.getComp(), e);
        }
        list.add(rowOneMap);
        list.add(rowTwoMap);
        return list;
    }

    @Override
    public HPanel getPanel() {
        hPanel.add(new LabelInput(LangMgr.lang(Word3.OWNER)+":"), userSelectBox);
        LastPanel lastPanel = new LastPanel();
        lastPanel.getComp().setPreferredSize(new Dimension(lastPanel.getWidth(), 150));
        lastPanel.setWithScroll(table.getComp());
        hPanel.add(new LabelInput(LangMgr.sLang(CsL.DATABASE_FILE)+":"), lastPanel);
        return hPanel;
    }
    
    @Override
    public String getSql() {
        HTabRowBean fileRowBean = new HTabRowBean();
        HTabRowBean logRowBean = new HTabRowBean();
        List<HTabRowBean> tabRowBeans = table.getRowBeans(RowStatus.UPDATE);
        if (tabRowBeans.size() == 1) {
            boolean bool = getValue(tabRowBeans.get(0),"file_type").equals("LOG");
            fileRowBean = bool ? table.getRowBeans(RowStatus.OLD).get(0) : tabRowBeans.get(0);
            logRowBean = bool ? tabRowBeans.get(0) : table.getRowBeans(RowStatus.OLD).get(0);
        } else {
            tabRowBeans = tabRowBeans.size() == 0 ? table.getRowBeans(RowStatus.OLD) : tabRowBeans;
            fileRowBean = tabRowBeans.get(0);
            logRowBean = tabRowBeans.get(1);
        }
        
        SqlserverDataBaseBean dataBaseBean = new SqlserverDataBaseBean(nameInput.getValue());
        dataBaseBean.setOwner(userSelectBox.getValue());
        dataBaseBean.setFileName(getValue(fileRowBean,"logic_name"));
        dataBaseBean.setLogFileName(getValue(logRowBean,"logic_name"));
        dataBaseBean.setFilePath(getValue(fileRowBean,"file_path"));
        dataBaseBean.setLogFilePath(getValue(logRowBean,"file_path"));
        dataBaseBean.setFileSize(getValue(fileRowBean,"init_size")+"MB");
        dataBaseBean.setLogFileSize(getValue(logRowBean,"init_size")+"MB");
    
        JsonObject fileJson = Json.parse(getValue(fileRowBean,"auto_incr")).asObject();
        JsonObject logJson = Json.parse(getValue(logRowBean,"auto_incr")).asObject();
        if (fileJson.getString("maxSize").equals("UNLIMITED")) {
            dataBaseBean.setMaxSize(fileJson.getString("maxSize"));
        } else {
            dataBaseBean.setMaxSize(fileJson.getString("maxSize")+fileJson.getString("maxSizeUnit"));
        }
        if (logJson.getString("maxSize").equals("UNLIMITED")) {
            dataBaseBean.setLogMaxSize(logJson.getString("maxSize"));
        } else {
            dataBaseBean.setLogMaxSize(logJson.getString("maxSize")+logJson.getString("maxSizeUnit"));
        }
        dataBaseBean.setFileGrowth(fileJson.getString("fileGrowth")+fileJson.getString("fileGrowthUnit"));
        dataBaseBean.setLogFileGrowth(logJson.getString("fileGrowth")+logJson.getString("fileGrowthUnit"));
        return DatabaseSqlUtil.getCreateDbSql(dbTypeEnum,version,dataBaseBean);
    }
    
    public void inform() {
        if (table.getComp().isEditing()) {
            table.getComp().getCellEditor().stopCellEditing();
        }
    }
    
    private String getValue(HTabRowBean rowOneBean,String name) {
        Map<String, String> oldMap = rowOneBean.getOldRow();
        Map<String, String> newMap = rowOneBean.getCurrRow() != null ? rowOneBean.getCurrRow() : new HashMap<>();
        if (StringUtils.isEmpty(newMap.get(name))) {
            return oldMap.get(name);
        } else {
            return newMap.get(name);
        }
    }
    
}
