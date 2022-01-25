package com.hh.hhdb_admin.mgr.cmd.ui;

import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.cmd.CmdMgr;

import javax.swing.*;

/**
 * 查询器设置面板
 *
 * @author hexu
 */
public class SettingsPanel {
    private HDialog dialog;
    private TextInput rowInput;
    private TextInput nullInput;
    private final HPanel generalPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));


    public SettingsPanel(int row, String nullSign) {
        try {
            rowInput = new TextInput("varPageSize", row + "");
            rowInput.setInputVerifier(VerifyUtil.getTextIntVerifier(CmdMgr.getLang("rowsnumber"), 1, 2147483647));
            nullInput = new TextInput("null",nullSign);
            dialog = new HDialog(StartUtil.parentFrame,500);
            dialog.setIconImage(CmdMgr.getIcon("key"));
            dialog.setWindowTitle(CmdMgr.getLang("current-settings"));
            HPanel hPanel = init();
            dialog.setRootPanel(hPanel);
            dialog.setSize(500,hPanel.getHeight()+50);      //根据实际大小设置弹出框大小
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    private HPanel init() throws Exception {
        generalPanel.add(null,null);
        generalPanel.add(getWithLabelInput("varPageSize", CmdMgr.getLang("rowsnumber") + ":", rowInput));
        generalPanel.add(getWithLabelInput("null", CmdMgr.getLang("Null")+":", nullInput));

        HPanel hPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
        hPanel.add(generalPanel);
        hPanel.add(initHButton());
        return hPanel;
    }


    private HPanel initHButton() throws Exception {
        HButton savebtn = new HButton(CmdMgr.getLang("determine")) {
            @Override
            public void onClick() {
                //临时保存到当前查询器
                save(Integer.parseInt(rowInput.getValue()), nullInput.getValue());
                JOptionPane.showMessageDialog(null, CmdMgr.getLang("success"), CmdMgr.getLang("message"), JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        };

        HPanel hPanel = new HPanel(new HDivLayout(GridSplitEnum.C4, GridSplitEnum.C4));
        hPanel.add(new HeightComp(10), savebtn, new HeightComp(10));
        return hPanel;
    }

    private static WithLabelInput getWithLabelInput(String id, String label, AbsInput intput) {
        HPanel hPanel = new HPanel(new HDivLayout(LangMgr2.getDefaultLang() == LangEnum.ZH ? GridSplitEnum.C3 : GridSplitEnum.C4));
        WithLabelInput wli = new WithLabelInput(hPanel, label, intput);
        wli.setId(id);
        return wli;
    }

    protected void save(int row, String nullSigns) {

    }
}
