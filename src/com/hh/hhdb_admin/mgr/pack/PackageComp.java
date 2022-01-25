package com.hh.hhdb_admin.mgr.pack;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.dbobj2.ora.pack.PackParserTool;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * @author YuSai
 */
public class PackageComp extends CommonComp {

    private final Connection conn;
    private final String schema;
    private HTabPanel tabPane;
    private String selectedItem;
    private DesignComp headComp;
    private DesignComp bodyComp;

    public PackageComp(Connection conn, String schema) {
        this.conn = conn;
        this.schema = schema;
    }

    public HPanel getAddPanel(String packName) throws Exception {
        tabPane = new HTabPanel() {
            @Override
            protected void onSelected(String id) {
                selectedItem = id;
            }
        };
        headComp = new DesignComp(conn, schema, packName, OraSessionEnum.pack, false);
        bodyComp = new DesignComp(conn, schema, packName, OraSessionEnum.packbody, false);
        tabPane.addPanel("head", headComp.getPanel(), new HeaderConfig(getLang("packageHead")));
        tabPane.addPanel("body", bodyComp.getPanel(), new HeaderConfig(getLang("packageBody")));
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(getBarPanel().getComp());
        lastPanel.set(tabPane.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        return rootPanel;
    }

    protected void addPackage() {
        TextInput nameInput = new TextInput("packageName");
        HDialog dialog = new HDialog(StartUtil.parentFrame, 400, 140);
        dialog.setWindowTitle(getLang("addPackage"));
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        HPanel panel = new HPanel(new HDivLayout(0, 10, GridSplitEnum.C12));
        panel.add(getWithLabelInput(getLang("packName"), nameInput));
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton subitBtn = new HButton(getLang("submit")) {
            @Override
            protected void onClick() {
                if (StringUtils.isEmpty(nameInput.getValue())) {
                    PopPaneUtil.info(dialog.getWindow(), getLang("enterPackageName"));
                    return;
                }
                try {
                    add(nameInput.getValue());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                dialog.dispose();
            }
        };
        subitBtn.setIcon(getIcon("submit"));
        HButton cancelBtn = new HButton(getLang("cancel")) {
            @Override
            protected void onClick() {
                dialog.dispose();
            }
        };
        cancelBtn.setIcon(getIcon("cancel"));
        barPanel.add(subitBtn, cancelBtn);
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(panel.getComp());
        lastPanel.setFoot(barPanel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        dialog.setRootPanel(rootPanel);
        dialog.show();
    }

    protected void add(String packName) throws Exception {
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                .add(StartUtil.CMD_ID, StartUtil.eng.push2SharedMap(getAddPanel(packName)))
                .add("title", packName)
                .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.PACKAGE.name()));
    }

    protected HPanel getDesignPanel(String packName, OraSessionEnum sessionEnum) throws Exception {
        return new DesignComp(conn, schema, packName, sessionEnum, true).getPanel();
    }

    protected void openFile(String text, String fileName) throws Exception {
        HPanel panel;
        String[] names = fileName.split("\\.");
        String type = names[names.length - 1];
        String packName = new PackParserTool(text).getName();
        switch (type) {
            case "pck":
                panel = getAddPanel(packName);
                String[] texts = text.split("\n/\n");
                if (texts.length < 2) {
                    DesignComp designComp = new DesignComp(conn, schema, packName, OraSessionEnum.pack, true);
                    designComp.setText(text);
                    panel = designComp.getPanel();
                } else {
                    headComp.setText(texts[0]);
                    bodyComp.setText(texts[1]);
                }
                break;
            case "spc":
                DesignComp designComp = new DesignComp(conn, schema, packName, OraSessionEnum.pack, true);
                designComp.setText(text);
                panel = designComp.getPanel();
                break;
            case "bdy":
                designComp = new DesignComp(conn, schema, packName, OraSessionEnum.packbody, true);
                designComp.setText(text);
                panel =  designComp.getPanel();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                .add(StartUtil.CMD_ID, StartUtil.eng.push2SharedMap(panel))
                .add("title", packName)
                .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.PACKAGE.name()));
    }

    private HBarPanel getBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton execBtn = new HButton(getLang("execute")) {
            @Override
            protected void onClick() {
                if (headComp.execute()) {
                    bodyComp.execute();
                    tabPane.selectPanel("body");
                 } else {
                    tabPane.selectPanel("head");
                }
            }
        };
        execBtn.setIcon(getIcon("execute"));
        HButton formatBtn = new HButton(getLang("format")) {
            @Override
            protected void onClick() {
                if ("head".equals(selectedItem)) {
                    headComp.format();
                } else {
                    bodyComp.format();
                }
            }
        };
        formatBtn.setIcon(getIcon("format"));
        HButton saveBtn = new HButton(getLang("saveToSqlBook")) {
            @Override
            protected void onClick() {
                saveToSqlBook(headComp.getText() + "\n/\n" + bodyComp.getText() + "\n/\n", "pck");
            }
        };
        saveBtn.setIcon(getIcon("save"));
        barPanel.add(execBtn, formatBtn, saveBtn);
        return barPanel;
    }

}
