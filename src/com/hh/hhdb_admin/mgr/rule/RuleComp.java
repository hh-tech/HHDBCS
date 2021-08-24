package com.hh.hhdb_admin.mgr.rule;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: Jiang
 * @date: 2020/7/24
 */

public class RuleComp {

    private static final String DOMAIN_NAME = RuleComp.class.getName();

    static {
        LangMgr.merge(DOMAIN_NAME, LangUtil.loadLangRes(RuleComp.class));
    }

    private final HDialog dialog;
    private JsonObject msg;
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
        dialog = StartUtil.getMainDialog();
        dialog.setSize(600, 600);
        dialog.setWindowTitle(getLang("add_rule"));
        dialog.setRootPanel(rootPanel);
        dialog.setIconImage(IconFileUtil.getLogo().getImage());
        dialog.setStatusBar(optionPanel);
    }

    public void add(JsonObject msg) {
        this.msg = msg;
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
                    String tableName = msg.getString(StartUtil.PARAM_TABLE);
                    String schemaName = msg.getString(StartUtil.PARAM_SCHEMA);
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
                    SqlExeUtil.executeUpdate(RuleMgr.conn, sql);
                    refresh();
                    dialog.dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
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
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput(getLang("name")), nameTextInput));
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput(getLang("event")), eventSelectBox));
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput(getLang("instead_run")), insteadCheckBoxInput));
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput(getLang("condition")), conditionTextInput));
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput(getLang("definition")), definitionTextAreaInput));
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput(getLang("comment")), commentTextInput));
    }

    private void refresh() {
        StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.RULE_GROUP.name())
                .add(StartUtil.PARAM_SCHEMA, msg.getString(StartUtil.PARAM_SCHEMA))
                .add(StartUtil.PARAM_TABLE, msg.getString(StartUtil.PARAM_TABLE)));
    }

    public static String getLang(String key) {
        return LangMgr.getValue(DOMAIN_NAME, key);
    }

}
