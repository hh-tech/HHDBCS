package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: Jiang
 * @date: 2020/9/15
 */

public class AddToPartitionHandler extends AbsHandler {

    private HTreeNode treeNode;

    public static final String ADD_TO_PARTITION = "ALTER TABLE \"%s\".\"%s\" ATTACH PARTITION %s.%s ";
    public static final String ADD_PARTITION = "CREATE TABLE \"%s\".%s PARTITION OF %s.%s ";

    private String masterType = "RANGE";
    private boolean isAddTo = true;

    private HPanel secondPanel = new HPanel();
    private final TextInput nameTextInput = new TextInput();
    private LabelInput firstLabelInput = new LabelInput();
    private LabelInput secondLabelInput = new LabelInput();
    private TextInput firstTextInput;
    private TextInput secondTextInput;
    private RadioGroupInput typeGroupInput;

    @Override
    public void resolve(HTreeNode treeNode) {
        this.treeNode = treeNode;
        nameTextInput.setValue("");

        HDialog dialog = new HDialog(StartUtil.getMainDialog(), 600, 400) {
            @Override
            protected void onConfirm() {
                try {
                    confirm();
                    dispose();
                } catch (Exception e) {
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
                }
                isAddTo = true;
            }

            @Override
            protected void onCancel() {
                super.onCancel();
                isAddTo = true;
            }
        };
        dialog.setWindowTitle(isAddTo ? "添加到分区表" : "添加分区表子表");
        dialog.setIconImage(IconFileUtil.getLogo());

        HDivLayout divLayout = new HDivLayout();
        divLayout.setyGap(10);
        HPanel panel = new HPanel(divLayout);
        panel.add(new HeightComp(30));
        panel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput(isAddTo ? "主表名称：" : "子表名称："), nameTextInput));

        typeGroupInput = new RadioGroupInput("partitionType") {
            @Override
            protected void stateChange(ItemEvent e) {
                if (e.getStateChange() != ItemEvent.SELECTED) {
                    return;
                }
                initLabel(((JRadioButton) e.getItem()).getText());
            }
        };
        typeGroupInput.add("RANGE", "RANGE");
        typeGroupInput.add("LIST", "LIST");
        typeGroupInput.add("HASH", "HASH");
        typeGroupInput.setSelected(isAddTo ? "RANGE" : masterType.toUpperCase());
        typeGroupInput.setEnabled(isAddTo);
        panel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), new LabelInput("分区类型"), typeGroupInput));

        firstLabelInput = new LabelInput();
        secondLabelInput = new LabelInput();
        firstTextInput = new TextInput("first");
        secondTextInput = new TextInput("second");
        HPanel firstPanel = new HPanel(new HDivLayout(GridSplitEnum.C2));
        firstPanel.add(firstLabelInput);
        firstPanel.add(firstTextInput);
        secondPanel = new HPanel(new HDivLayout(GridSplitEnum.C2));
        secondPanel.add(secondLabelInput);
        secondPanel.add(secondTextInput);
        panel.add(firstPanel);
        panel.add(secondPanel);

        initLabel(null);

        dialog.setOption();
        dialog.setRootPanel(panel);
        dialog.show();
    }


    private void confirm() throws Exception {
        String schemaName = getSchemaName();
        String nodeName = treeNode.getName();
        String inputName = nameTextInput.getValue();
        if (StringUtils.isBlank(inputName)) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), "请输入表名");

            return;
        }
        String masterTableName = isAddTo ? inputName : nodeName;
        String childTableName = isAddTo ? nodeName : inputName;

        String partitionType = typeGroupInput.getValue();
        String firstParam = firstTextInput.getValue();
        String secondParam = secondTextInput.getValue();

        String sql = isAddTo ? ADD_TO_PARTITION : ADD_PARTITION;
        List<String> paramList = new ArrayList<>();
        paramList.add(schemaName);
        paramList.add(isAddTo ? masterTableName : childTableName);
        paramList.add(schemaName);
        paramList.add(isAddTo ? childTableName : masterTableName);
        switch (partitionType) {
            case "RANGE":
                boolean isDefault = ("DEFAULT".equalsIgnoreCase(firstParam))
                        || ("DEFAULT".equalsIgnoreCase(secondParam));
                sql += isDefault ? " DEFAULT;" : " FOR VALUES FROM (%s) TO (%s);";
                if (!isDefault) {
                    paramList.add(firstParam);
                    paramList.add(secondParam);
                }
                break;
            case "LIST":
                if ("DEFAULT".equalsIgnoreCase(firstParam)) {
                    sql += " DEFAULT;";
                } else {
                    sql += "FOR VALUES IN (%s);";
                    paramList.add(firstParam);
                }
                break;
            case "HASH":
                sql += "FOR VALUES WITH (MODULUS %s, REMAINDER %s);";
                break;
        }
        SqlExeUtil.executeUpdate(loginBean.getConn(), String.format(sql, paramList.toArray()));
        sendMsg(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                .add(StartUtil.PARAM_SCHEMA, schemaName)
                .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.TABLE_GROUP.name()));
        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), "添加成功");
    }

    public void resolve(HTreeNode treeNode, String partitionType) {
        isAddTo = false;
        masterType = partitionType;
        resolve(treeNode);
        initLabel(null);
    }

    private void initLabel(String type) {
        switch (type == null ? masterType : type) {
            case "RANGE":
                secondPanel.setVisible(true);
                firstLabelInput.setValue("下限：");
                secondLabelInput.setValue("上限：");
                break;
            case "LIST":
                secondPanel.setVisible(false);
                firstLabelInput.setValue("条件：");
                break;
            case "HASH":
                secondPanel.setVisible(true);
                firstLabelInput.setValue("分区总数：");
                secondLabelInput.setValue("分区键：");
                break;
        }
    }
}
