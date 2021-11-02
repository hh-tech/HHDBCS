package com.hh.hhdb_admin.mgr.type;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
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

public class TypeComp extends CommonComp {

    private final Connection conn;
    private final String schema;
    private final OraSessionEnum sessionEnum;

    public TypeComp(Connection conn, String schema, OraSessionEnum sessionEnum)  {
        this.conn = conn;
        this.schema = schema;
        this.sessionEnum = sessionEnum;
    }

    public void add() {
        TextInput nameInput = new TextInput("name");
        HDialog dialog = new HDialog(StartUtil.parentFrame, 400, 120);
        dialog.setWindowTitle(getLang("add"));
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setTopHeight(10);
        HPanel panel = new HPanel(divLayout);
        panel.add(getWithLabelInput(getLang("name"), nameInput));
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

    public HPanel getPanel(String name) throws Exception {
        return new DesignComp(conn, schema, name, sessionEnum).getPanel();
    }

    protected void openFile(String text, String fileName, OraSessionEnum sessionEnum) throws Exception {
        // todo type解析功能暂未实现，先取文件名
//        String name = new PackParserTool(text).getName();
        DesignComp designComp = new DesignComp(conn, schema, fileName, sessionEnum);
        designComp.setText(text);
        HPanel panel = designComp.getPanel();
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                .add(StartUtil.CMD_ID, StartUtil.eng.push2SharedMap(panel))
                .add("title", fileName)
                .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.TYPE.name()));
    }

    private void add(String name) throws Exception {
        StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                .add(StartUtil.CMD_ID, StartUtil.eng.push2SharedMap(getPanel(name)))
                .add("title", name)
                .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.TYPE.name()));
    }

}
