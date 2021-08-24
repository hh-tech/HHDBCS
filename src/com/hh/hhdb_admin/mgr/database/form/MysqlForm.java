package com.hh.hhdb_admin.mgr.database.form;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.database.DatabaseComp;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class MysqlForm extends AbsForm {

    private static final String LOG_NAME = MysqlForm.class.getSimpleName();

    private final Connection conn;

    private final HPanel formPanel;
    private final TextInput databaseInput;
    private final SelectBox charSetBox;
    private final SelectBox collateBox;

    MysqlForm(Connection conn) throws Exception {
        this.conn = conn;
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        formPanel = new HPanel(divLayout);
        databaseInput = new TextInput("database");
        collateBox = new SelectBox("collateRule");
        collateBox.addOption("", "");
        charSetBox = new SelectBox("charSet") {
            @Override
            protected void onItemChange(ItemEvent e) {
                initCollateBox(this.getValue());
            }
        };
        charSetBox.addOption("", "");
        initSelectBox();
    }

    private void initSelectBox() throws Exception {
        List<Map<String, Object>> maps = SqlQueryUtil.select(conn, "show character set;");
        for (Map<String, Object> map : maps) {
            charSetBox.addOption(map.get("charset") + "", map.get("charset") + "");
        }
    }

    private void initCollateBox(String chatSet) {
        collateBox.removeAllItems();
        collateBox.addOption("", "");
        if (StringUtils.isNotBlank(chatSet)) {
            try {
                List<Map<String, Object>> maps = SqlQueryUtil.select(conn, String.format("show collation where charset = '%s';", chatSet));
                for (Map<String, Object> map : maps) {
                    collateBox.addOption(map.get("collation") + "", map.get("collation") + "");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                logUtil.error(LOG_NAME, e);
            }
        }
    }

    @Override
    public HPanel getPanel() {
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("databaseName"), databaseInput));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("charSet"), charSetBox));
        formPanel.add(getWithLabelInput(DatabaseComp.getLang("collateRule"), collateBox));
        return formPanel;
    }

    @Override
    public JsonObject getData() {
        JsonObject dataJson = new JsonObject();
        dataJson.add("dbName", databaseInput.getValue());
        dataJson.add("charSet", charSetBox.getValue());
        dataJson.add("collate", collateBox.getValue());
        return dataJson;
    }

    @Override
    public void reset() {
        databaseInput.setValue("");
        charSetBox.setValue("");
        collateBox.setValue("");
    }

}
