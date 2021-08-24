package com.hh.hhdb_admin.mgr.trigger.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;


/**
 * 创建触发器，设置好基本条件之后打开的一个编辑sql的面板
 */
public class TriggerEditDialog {

    private final QueryEditorTextArea textArea;
    private final HDialog dialog;
    private final HBarPanel bar;
    private static final String domainName = TriggerEditDialog.class.getName();

    static {
        try {
            LangMgr.merge(domainName, LangUtil.loadLangRes(TriggerEditDialog.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TriggerEditDialog(String initText) {
        this.textArea = new QueryEditorTextArea(true);
        dialog = new HDialog(StartUtil.parentFrame, 800, 600) {
            @Override
            protected void closeEvent() {
                this.dispose();
            }
        };
        LastPanel panel = new LastPanel();
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        l.setxGap(2);
        bar = new HBarPanel(l);
        panel.setWithScroll(this.textArea.getComp());
        panel.setHead(bar.getComp());
        textArea.setText(initText);
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        dialog.setWindowTitle(LangMgr.getValue(domainName, "EDIT"));
        HPanel hPanel = new HPanel();
        hPanel.setLastPanel(panel);
        dialog.setRootPanel(hPanel);
    }

    public HBarPanel getToolBar() {
        return bar;
    }

    /**
     * 窗口显示
     */
    public void show() {
        dialog.show();
    }

    /**
     * 窗口关闭
     */
    public void close() {
        dialog.dispose();
    }

    /**
     * 获取编辑框的sql
     */
    public String getTriggerSql() {
        return textArea.getTextArea().getText();
    }

}
