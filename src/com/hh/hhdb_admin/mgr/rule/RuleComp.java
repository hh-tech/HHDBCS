package com.hh.hhdb_admin.mgr.rule;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/7/24
 */

public class RuleComp {
    private final HDialog dialog;
    private RuleBean bean;
    private HPanel rootPanel;
    private HBarPanel optionPanel;
    private TextInput nameTextInput;
    private SelectBox eventSelectBox;
    private CheckBoxInput insteadCheckBoxInput;
    private TextInput conditionTextInput;
    private TextAreaInput definitionTextAreaInput;
    private TextInput commentTextInput;

    public RuleComp() {
        initAddPanel();
        dialog = new HDialog(CsVarUtil.parentFrame, 600, 600);
        dialog.setWindowTitle(LangMgr.lang(Word2.ESTABLISH));
        dialog.setRootPanel(rootPanel);
        dialog.setIconImage(IconFileUtil.getLogo().getImage());
        dialog.setStatusBar(optionPanel);
    }

    public void add(RuleBean bean) {
        this.bean = bean;
        dialog.show();
    }

    private void initAddPanel() {
        HBarLayout layout = new HBarLayout();
        layout.setAlign(AlignEnum.CENTER);
        optionPanel = new HBarPanel(layout);
        optionPanel.add(new HButton("确认") {
            @Override
            protected void onClick() {
                try {
                    String tableName = bean.getTable();
                    String schemaName = bean.getSchema();
                    String sql = "CREATE RULE %s AS ON %s TO \"%s\".\"%s\" WHERE %s DO %s %s;";
                    String name = nameTextInput.getValue();
                    String event = eventSelectBox.getValue();
                    String instead = insteadCheckBoxInput.getValue();
                    String condition = conditionTextInput.getValue();
                    String definition = definitionTextAreaInput.getValue();
                    String comment = commentTextInput.getValue();
                    if (StringUtils.isBlank(name)) {
                        throw new Exception("名称不能为空");
                    }
                    if (StringUtils.isBlank(condition)) {
                        throw new Exception("条件不能为空");
                    }
                    instead = StringUtils.isBlank(instead) ? "" : " INSTEAD ";
                    definition = StringUtils.isBlank(definition) ? " NOTHING " : definition;
                    sql = String.format(sql, name, event, schemaName, tableName, condition, instead, definition);

                    if (StringUtils.isNotBlank(comment)) {
                        sql += String.format("COMMENT ON RULE %s ON \"%s\".\"%s\" IS '%s';", name, schemaName, tableName, comment);
                    }
                    SqlExeUtil.executeUpdate(CsVarUtil.conn, sql);
                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(bean.getRefreshNodeId()));
                    dialog.dispose();
                } catch (Exception e) {
                    Lg.error(RuleComp.class, e);
                    PopDlgUtil.error(CsVarUtil.parentFrame,e);
                }
            }
        });
        optionPanel.add(new HButton("取消") {
            @Override
            protected void onClick() {
                dialog.dispose();
            }
        });

        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        rootPanel = new HPanel(divLayout);
        nameTextInput = new TextInput("name");
        eventSelectBox = new SelectBox("event");
        eventSelectBox.addOption("Select", "select");
        eventSelectBox.addOption("Update", "update");
        eventSelectBox.addOption("Insert", "insert");
        eventSelectBox.addOption("Delete", "delete");
        insteadCheckBoxInput = new CheckBoxInput("instead");
        conditionTextInput = new TextInput("condition");
        definitionTextAreaInput = new TextAreaInput("definition");
        commentTextInput = new TextInput("comment");

        rootPanel.add(new HeightComp(20));
        rootPanel.add(getLabelInput(LangMgr.lang(Word2.NAME), nameTextInput));
        rootPanel.add(getLabelInput(LangMgr.lang(Word2.INCIDENT), eventSelectBox));
        rootPanel.add(getLabelInput(LangMgr.lang(Word4.SUBSTITUTE_RUN), insteadCheckBoxInput));
        rootPanel.add(getLabelInput(LangMgr.lang(Word2.CONDITION), conditionTextInput));
        rootPanel.add(getLabelInput(LangMgr.lang(Word2.DEFINITION), definitionTextAreaInput));
        rootPanel.add(getLabelInput(LangMgr.lang(Word2.NOTES), commentTextInput));
    }
    
    protected WithLabelInput getLabelInput(String label, AbsInput input) {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C2, AlignEnum.RIGHT,AlignEnum.LEFT);
        return new WithLabelInput(labelConf, new LabelInput(label), input);
    }
}
