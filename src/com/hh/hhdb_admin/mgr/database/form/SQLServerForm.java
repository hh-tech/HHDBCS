package com.hh.hhdb_admin.mgr.database.form;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.mgr.database.DatabaseComp;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class SQLServerForm extends AbsForm {

    private static final String LOG_NAME = SQLServerForm.class.getSimpleName();

    private final Connection conn;
    private final HPanel formPanel;
    public final HTable table;
    private final TextInput databaseInput;
    private final SelectBox userSelectBox;

    public SQLServerForm(Connection conn, String user) throws Exception {
        this.conn = conn;
        table = new HTable();
        table.setRowHeight(25);
        table.hideSeqCol();
        table.setEvenBgColor(table.getOddBgColor());
        table.addCols(new DataCol("logic_name", DatabaseComp.getLang("logic_name")),
                new DataCol("file_type", DatabaseComp.getLang("file_type")),
                new DataCol("file_group", DatabaseComp.getLang("file_group")),
                new DataCol("init_size", DatabaseComp.getLang("init_size")),
                new ChooseParamPanel("auto_incr", DatabaseComp.getLang("auto_incr"), DatabaseComp.getLang("auto_incr_setting")),
                new DataCol("file_path", DatabaseComp.getLang("file_path")),
                new DataCol("file_name", DatabaseComp.getLang("file_name")));
        table.load(getTableData("", "_log"), 1);
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        formPanel = new HPanel(divLayout);
        databaseInput = new TextInput("database") {
            @Override
            protected void doChange() {
                String name = this.getValue();
                table.load(getTableData(name, name + "_log"), 1);
            }
        };
        userSelectBox = new SelectBox("user");
        userSelectBox.addOption("", "");
        initSelectBox(user);
    }

    public List<Map<String, String>> getTableData(String logName1, String logName2) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> rowOneMap = new HashMap<>();
        rowOneMap.put("logic_name", logName1);
        rowOneMap.put("file_type", DatabaseComp.getLang("row_data"));
        rowOneMap.put("file_group", "PRIMARY");
        rowOneMap.put("init_size", "3");
        JsonObject json = new JsonObject();
        json.add(JsonCol.__TEXT, DatabaseComp.getLang("value1"));
        json.add("fileGrowth", "1024kb");
        json.add("maxSize", "");
        rowOneMap.put("auto_incr", json.toPrettyString());
        rowOneMap.put("file_name", "");
        Map<String, String> rowTwoMap = new HashMap<>();
        rowTwoMap.put("logic_name", logName2);
        rowTwoMap.put("file_type", DatabaseComp.getLang("notApplicable"));
        rowTwoMap.put("file_group", DatabaseComp.getLang("log"));
        rowTwoMap.put("init_size", "1");
        json = new JsonObject();
        json.add(JsonCol.__TEXT, DatabaseComp.getLang("value2"));
        json.add("fileGrowth", "10%");
        json.add("maxSize", "");
        rowTwoMap.put("auto_incr", json.toPrettyString());
        rowTwoMap.put("file_name", "");
        try {
            List<Map<String, Object>> lists = SqlQueryUtil.select(conn, "select * from sysfiles");
            String fileName = (lists.get(0).get("filename") + "");
            int n = fileName.indexOf("DATA");
            fileName = fileName.substring(0, n + 4);
            rowOneMap.put("file_path", fileName);
            rowTwoMap.put("file_path", fileName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        list.add(rowOneMap);
        list.add(rowTwoMap);
        return list;
    }

    private void initSelectBox(String user) throws Exception {
        String sql = "select g.name sysadmin, u.name, u.sid MemberSID  \n" +
                "from sys.server_principals u, sys.server_principals g, sys.server_role_members m  \n" +
                "where g.principal_id = m.role_principal_id  \n" +
                "and u.principal_id = m.member_principal_id  \n" +
                "and g.name = 'sysadmin'\n" +
                "and u.name = '%s'\n" +
                "order by 1, 2";
        Map<String, String> userMap = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, user));
        if (userMap.size() == 0) {
            userMap = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, "sa"));
            if (StringUtils.isNotEmpty(userMap.get("name"))) {
                userSelectBox.addOption(userMap.get("name"), userMap.get("name"));
            }
            userSelectBox.addOption(user, user);
        } else {
            String queryUserSql = "select name from sys.server_principals where type_desc in ('SQL_LOGIN', 'WINDOWS_LOGIN', 'WINDOWS_GROUP') ORDER BY NAME";
            List<String> userMaps = SqlQueryUtil.selectOneColumn(conn, queryUserSql);
            for (String name : userMaps) {
                userSelectBox.addOption(name, name);
            }
        }
    }

    @Override
    public HPanel getPanel() {
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("databaseName"), databaseInput));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("dbOwner"), userSelectBox));
        HPanel tablePanel = new HPanel(new HDivLayout(GridSplitEnum.C3));
        tablePanel.add(new LabelInput(DatabaseComp.getLang("databaseFile")));
        JScrollPane scrollPane = new JScrollPane(table.getComp());
        scrollPane.setPreferredSize(new Dimension(scrollPane.getWidth(), 250));
        AbsHComp tableComp = new AbsHComp() {
            @Override
            public Component getComp() {
                return scrollPane;
            }
        };
        tablePanel.add(tableComp);
        formPanel.add(tablePanel);
        return formPanel;
    }

    @Override
    public JsonObject getData() {
        JsonObject data = new JsonObject();
        data.add("dbName", databaseInput.getValue());
        data.add("owner", userSelectBox.getValue());
        List<HTabRowBean> tabRowBeans = table.getRowBeans(RowStatus.UPDATE);
        if (tabRowBeans.size() == 0) {
            tabRowBeans = table.getRowBeans(RowStatus.OLD);
        } else {
            tabRowBeans.addAll(table.getRowBeans(RowStatus.OLD));
        }
        JsonArray array = new JsonArray();
        if ("PRIMARY".equals(tabRowBeans.get(0).getOldRow().get("file_group"))) {
            array.add(parseRowData(tabRowBeans.get(0), true));
            array.add(parseRowData(tabRowBeans.get(1), false));
        } else {
            array.add(parseRowData(tabRowBeans.get(1), false));
            array.add(parseRowData(tabRowBeans.get(0), true));
        }
        data.add("data", array.toPrettyString());
        return data;
    }

    private JsonObject parseRowData(HTabRowBean rowOneBean, boolean flag) {
        JsonObject data = new JsonObject();
        Map<String, String> oldMap = rowOneBean.getOldRow();
        Map<String, String> newMap = rowOneBean.getCurrRow() != null ? rowOneBean.getCurrRow() : new HashMap<>();
        String fileName, initSize, filePath, autoIncr;
        if (StringUtils.isEmpty(newMap.get("file_name"))) {
            fileName = oldMap.get("logic_name");
        } else {
            fileName = newMap.get("file_name");
        }
        if (flag) {
            fileName = StringUtils.isEmpty(fileName) ? databaseInput.getValue() : fileName;
        } else {
            fileName = StringUtils.isEmpty(fileName) ? databaseInput.getValue() + "_log" : fileName;
        }
        data.add("file_name", fileName);
        if (StringUtils.isEmpty(newMap.get("init_size"))) {
            initSize = oldMap.get("init_size");
        } else {
            initSize = newMap.get("init_size");
        }
        if (StringUtils.isEmpty(newMap.get("file_path"))) {
            filePath = oldMap.get("file_path");
        } else {
            filePath = newMap.get("file_path");
        }
        if (StringUtils.isEmpty(newMap.get("auto_incr"))) {
            autoIncr = oldMap.get("auto_incr");
        } else {
            autoIncr = newMap.get("auto_incr");
        }
        data.add("init_size", initSize);
        data.add("file_path", filePath);
        data.add("auto_incr", autoIncr);
        return data;
    }

    @Override
    public void reset() {
        databaseInput.setValue("");
        userSelectBox.setValue("");
        table.load(getTableData("", "_log"), 1);
    }

}
