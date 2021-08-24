package com.hh.hhdb_admin.mgr.database.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj2.hhdb.HHdbTablespace;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.mgr.database.DatabaseComp;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class HhPgForm extends AbsForm {

    private static final String LOG_NAME = HhPgForm.class.getSimpleName();

    private final Connection conn;
    private final DBTypeEnum dbTypeEnum;
    private final String user;

    private final HPanel formPanel;
    private final TextInput databaseInput;
    private final SelectBox userSelectBox;
    private final SelectBox spaceSelectBox;
    private final TextAreaInput descriptionInput;

    HhPgForm(Connection conn, DBTypeEnum dbTypeEnum, String user) throws Exception {
        this.conn = conn;
        this.dbTypeEnum = dbTypeEnum;
        this.user = user;
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        formPanel = new HPanel(divLayout);
        databaseInput = new TextInput("database");
        userSelectBox = new SelectBox("user");
        userSelectBox.addOption("", "");
        spaceSelectBox = new SelectBox("tableSpace");
        spaceSelectBox.addOption("", "");
        descriptionInput = new TextAreaInput("description", "", 5);
        initSelectBox();
    }

    private void initSelectBox() throws Exception {
        String prefix = dbTypeEnum.name().substring(0, 2);
        String sql = "select usesuper from " + prefix + "_user where usename = '%s' ";
        Map<String, String> userMap = SqlQueryUtil.selectOneStrMap(conn, String.format(sql, user));
        if ("t".equals(userMap.get("usesuper"))) {
            sql = "select usename username from " + prefix + "_user";
            List<Map<String, String>> userLists = SqlQueryUtil.selectStrMapList(conn, sql);
            for (Map<String, String> map : userLists) {
                String userName = map.get("username");
                if (!userName.startsWith(prefix)) {
                    userSelectBox.addOption(userName, userName);
                }
            }
        } else {
            userSelectBox.addOption(user, user);
        }
        // 查询所有表空间信息
        HHdbTablespace tablespace = new HHdbTablespace(conn, HHdbPgsqlPrefixEnum.valueOf(prefix));
        tablespace.getAllTableSpace().forEach(spaceMap -> spaceSelectBox.addOption(spaceMap.get("tablespace_name"), spaceMap.get("tablespace_name")));
    }

    @Override
    public HPanel getPanel() {
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("databaseName"), databaseInput));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("dbOwner"), userSelectBox));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("dbSpaceName"), spaceSelectBox));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("dbDescription"), descriptionInput));
        return formPanel;
    }

    @Override
    public JsonObject getData() {
        JsonObject dataJson = new JsonObject();
        dataJson.add("dbName", databaseInput.getValue());
        dataJson.add("owner", userSelectBox.getValue());
        dataJson.add("spaceName", spaceSelectBox.getValue());
        dataJson.add("comment", descriptionInput.getValue());
        return dataJson;
    }

    @Override
    public void reset() {
        databaseInput.setValue("");
        userSelectBox.setValue("");
        spaceSelectBox.setValue("");
        descriptionInput.setValue("");
    }

}
