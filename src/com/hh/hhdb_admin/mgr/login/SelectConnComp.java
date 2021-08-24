package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public class SelectConnComp {

    JsonObject loginData;
    private HTable table;
    private HDialog selectDialog;
    private final LoginForm loginForm;
    private final SshLoginComp sshLoginComp;

    public SelectConnComp(LoginForm loginForm, SshLoginComp sshLoginComp) {
        this.loginForm = loginForm;
        this.sshLoginComp = sshLoginComp;
        loginData = LoginUtil.readConnFile();
    }

    public void show(HFrame frame, HDialog dialog, boolean isSwitch) {
        selectDialog = new HDialog(isSwitch ? dialog : frame, 840, 600);
        selectDialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        selectDialog.setWindowTitle(LoginComp.getLang("connect"));
        HPanel panel = new HPanel();
        panel.setLastPanel(initTable());
        selectDialog.setRootPanel(panel);
        table.load(getData(), 1);
        selectDialog.show();
    }

    private DataCol createCol(String name, String value, int width) {
        DataCol dataCol = new DataCol(name, value);
        dataCol.setWidth(width);
        return dataCol;
    }

    private LastPanel initTable() {
        table = new HTable();
        table.setCellEditable(false);
        DataCol passCol = new DataCol("db_pass", LoginComp.getLang("tPass"));
        passCol.setShow(false);
        table.addCols(createCol("conn_name", LoginComp.getLang("tName"), 160),
                createCol("db_type", LoginComp.getLang("tType"), 100),
                createCol("db_user", LoginComp.getLang("tUser"), 100), passCol,
                createCol("db_schema", LoginComp.getLang("tSchema"), 100),
                createCol("db_url", LoginComp.getLang("tUrl"), 400));
        table.setRowHeight(25);
        HButton addBtn = new HButton(LoginComp.getLang("add")) {
            @Override
            public void onClick() {
                addConn(false, "", "", "", "", "", "");
            }
        };
        addBtn.setIcon(LoginComp.getIcon("add"));
        HButton editBtn = new HButton(LoginComp.getLang("edit")) {
            @Override
            public void onClick() {
                Map<String, String> map = getSelectRowData();
                if (null != map) {
                    addConn(true, map.get("conn_name"), map.get("db_type"), map.get("db_url"), map.get("db_user"), map.get("db_pass"), map.get("db_schema"));
                }
            }
        };
        editBtn.setIcon(LoginComp.getIcon("edit"));
        HButton selBtn = new HButton(LoginComp.getLang("select"));
        selBtn.addActionListener(e -> select());
        table.getComp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    select();
                }
            }
        });
        selBtn.setIcon(LoginComp.getIcon("select"));
        HButton delBtn = new HButton(LoginComp.getLang("delete")) {
            @Override
            public void onClick() {
                List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
                if (rowBeans.size() == 0) {
                    PopPaneUtil.info(selectDialog.getWindow(), LoginComp.getLang("pleaseSelectData"));
                    return;
                }
                if (PopPaneUtil.confirm(StartUtil.parentFrame.getWindow(), LoginComp.getLang("sureDelete"))) {
                    for (HTabRowBean rowBean : rowBeans) {
                        Map<String, String> map = rowBean.getOldRow();
                        loginData.remove(map.get("conn_name"));
                        LoginUtil.saveConnFile(loginData);
                    }
                    PopPaneUtil.info(selectDialog.getWindow(), LoginComp.getLang("deleteSuccess"));
                    table.load(getData(), 0);
                }
            }
        };
        delBtn.setIcon(LoginComp.getIcon("delete"));

        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(addBtn, editBtn, delBtn, selBtn);
        HPanel panel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        SearchToolBar toolBar = new SearchToolBar(table);
        panel.add(barPanel, toolBar);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(panel.getComp());
        lastPanel.setWithScroll(table.getComp());
        return lastPanel;
    }

    private Map<String, String> getSelectRowData() {
        List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
        if (rowBeans.size() == 0) {
            PopPaneUtil.info(selectDialog.getWindow(), LoginComp.getLang("pleaseSelectOneData"));
            return null;
        }
        if (rowBeans.size() > 1) {
            PopPaneUtil.info(selectDialog.getWindow(), LoginComp.getLang("onlySelectOne"));
            return null;
        }
        return rowBeans.get(0).getOldRow();
    }

    private List<Map<String, String>> getData() {
        List<Map<String, String>> tableDataList = new ArrayList<>();
        for (String name : loginData.names()) {
            JsonObject data = loginData.get(name).asObject();
            Map<String, String> map = new HashMap<>();
            String dbType = data.getString("db_type");
            map.put("conn_name", data.getString("conn_name"));
            map.put("db_type", dbType);
            map.put("db_user", data.getString("db_user"));
            map.put("db_pass", data.getString("db_pass"));
            map.put("db_schema", data.getString("db_schema"));
            map.put("db_url", data.getString("db_url"));
            tableDataList.add(map);
        }
        return tableDataList;
    }

    private void addConn(boolean bool, String... param) {
        HDialog addOrEditDialog = new HDialog(selectDialog, 720, 340);
        addOrEditDialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        addOrEditDialog.setWindowTitle(LoginComp.getLang(StringUtils.isEmpty(param[0]) ? "add" : "Edit"));
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setTopHeight(10);
        HPanel panel = new HPanel(divLayout);
        TextInput connNameInput = new TextInput("connName", param[0]);
        SelectBox connTypeBox = new SelectBox("connType");
        StartUtil.supportDbTypeList.forEach(item -> connTypeBox.addOption(LoginComp.getLang(item), item));
        connTypeBox.setValue(param[1]);
        TextInput connUrlInput = new TextInput("connUrl", param[2]);
        TextInput connUserInput = new TextInput("connName", param[3]);
        PasswordInput connPassInput = new PasswordInput("conn_pass");
        connPassInput.setValue(param[4]);
        TextInput connScheInput = new TextInput("connSche", param[5]);
        HButton tempBtn = new HButton(LoginComp.getLang("template")) {
            @Override
            public void onClick() {
                String dbType = connTypeBox.getValue();
                connUrlInput.setValue(DriverUtil.getDriverUrl(DBTypeEnum.valueOf(dbType)));
            }
        };
        tempBtn.setIcon(LoginComp.getIcon("template"));
        panel.add(new LoginInput(LoginComp.getLang("conn_name"), connNameInput));
        panel.add(new LoginInput(LoginComp.getLang("db_type"), connTypeBox));
        panel.add(new LoginInput(LoginComp.getLang("db_url"), connUrlInput, tempBtn));
        panel.add(new LoginInput(LoginComp.getLang("db_user"), connUserInput));
        panel.add(new LoginInput(LoginComp.getLang("db_pass"), connPassInput));
        panel.add(new LoginInput(LoginComp.getLang("db_schema"), connScheInput));

        HButton saveBtn = new HButton(LoginComp.getLang("save")) {
            @Override
            public void onClick() {
                String connName = connNameInput.getValue();
                if (StringUtils.isEmpty(connName)) {
                    PopPaneUtil.info(addOrEditDialog.getWindow(), LoginComp.getLang("connNameNotNull"));
                    return;
                }
                if (bool) {
                    loginData.remove(param[0]);
                }
                JsonObject data = new JsonObject();
                data.add("conn_name", connName)
                        .add("db_type", connTypeBox.getValue())
                        .add("db_url", connUrlInput.getValue())
                        .add("db_user", connUserInput.getValue())
                        .add("db_pass", connPassInput.getValue())
                        .add("db_schema", connScheInput.getValue());
                loginData.set(connName, data);
                LoginUtil.saveConnFile(loginData);
                PopPaneUtil.info(addOrEditDialog.getWindow(), LoginComp.getLang("saveSuccess"));
                table.load(getData(), 1);
                addOrEditDialog.dispose();
            }
        };
        saveBtn.setIcon(LoginComp.getIcon("submit"));
        HButton cancelBtn = new HButton(LoginComp.getLang("cancel")) {
            @Override
            public void onClick() {
                addOrEditDialog.dispose();
            }
        };
        cancelBtn.setIcon(LoginComp.getIcon("cancel"));
        HBarLayout centerBarLayout = new HBarLayout();
        centerBarLayout.setAlign(AlignEnum.CENTER);
        HBarPanel centerBarPanel = new HBarPanel(centerBarLayout);
        centerBarPanel.add(saveBtn, cancelBtn);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(panel.getComp());
        lastPanel.setFoot(centerBarPanel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        addOrEditDialog.setRootPanel(rootPanel);
        addOrEditDialog.show();
    }

    private void select() {
        Map<String, String> map = getSelectRowData();
        if (null != map) {
            JsonObject jsonObject = Json.parse(loginData.get(map.get("conn_name")).toString()).asObject();
            loginForm.updateFormData(jsonObject);
            if (jsonObject.get("ssh") != null) {
                sshLoginComp.initFromJson(jsonObject.get("ssh").asObject());
                LoginUtil.sshLoginBen = sshLoginComp.getSshLoginBen();
            } else {
                sshLoginComp.initFromJson(null);
            }
            selectDialog.dispose();
        }
        selectCallback();
    }

    protected void selectCallback() {

    }

}
