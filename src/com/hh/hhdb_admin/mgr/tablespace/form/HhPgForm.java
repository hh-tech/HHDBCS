package com.hh.hhdb_admin.mgr.tablespace.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class HhPgForm extends AbsForm {

    private static final String LOG_NAME = HhPgForm.class.getSimpleName();

    private final Connection conn;
    private final DBTypeEnum dbTypeEnum;

    private final HPanel formPanel;

    private final TextInput spaceNameInput;
    private final TextInput locationInput;
    private final SelectBox userSelectBox;

    public HhPgForm(Connection conn, DBTypeEnum dbTypeEnum) {
        this.conn = conn;
        this.dbTypeEnum = dbTypeEnum;
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        formPanel = new HPanel(divLayout);
        spaceNameInput = new TextInput("spaceName");
        locationInput = new TextInput("location");
        userSelectBox = new SelectBox("user");
        userSelectBox.addOption("", "");
        initSelectBox();
    }

    private void initSelectBox() {
        try {
            List<Map<String, String>> userMaps = TableSpaceUtil.getUsers(conn, dbTypeEnum);
            for (Map<String, String> userMap : userMaps) {
                userSelectBox.addOption(userMap.get("username"), userMap.get("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logUtil.error(LOG_NAME, e);
        }
    }

    @Override
    public HPanel getPanel() {
        formPanel.add(getGridPanel(getLang("tableSpaceName"), spaceNameInput));
        formPanel.add(getGridPanel(getLang("tableSpaceLocation"), locationInput));
        formPanel.add(getGridPanel(getLang("tableSpaceOwner"), userSelectBox));
        return formPanel;
    }

    @Override
    public JsonObject getData() {
        JsonObject dataJson = new JsonObject();
        dataJson.add("spaceName", spaceNameInput.getValue());
        dataJson.add("location", locationInput.getValue());
        dataJson.add("owner", userSelectBox.getValue());
        return dataJson;
    }

    @Override
    public void reset() {
        spaceNameInput.setValue("");
        locationInput.setValue("");
        userSelectBox.setValue("");
    }

}
