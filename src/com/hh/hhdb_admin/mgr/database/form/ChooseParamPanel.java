package com.hh.hhdb_admin.mgr.database.form;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.mgr.database.DatabaseComp;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;

/**
 * @author yusai
 */
public class ChooseParamPanel extends JsonCol {

    private final String title;
    private ButtonGroup buttonGroup1;
    private ButtonGroup buttonGroup2;
    private TextInput bfbInput;
    private TextInput mbInput;
    private TextInput zzInput;

    public ChooseParamPanel(String name, String value, String title) {
        super(name, value);
        this.title = title;
    }

    @Override
    public JsonObject onClick(JsonObject json, int row, int column) {
        buttonGroup1 = new ButtonGroup();
        buttonGroup2 = new ButtonGroup();
        JRadioButton bfbRadio = new JRadioButton(DatabaseComp.getLang("byPercentage"));
        bfbRadio.setName("bfb");
        bfbInput = new TextInput("", "10");
        JRadioButton mbRadio = new JRadioButton(DatabaseComp.getLang("byMB"));
        mbRadio.setName("mb");
        mbInput = new TextInput("", "10");
        JRadioButton zzRadio = new JRadioButton(DatabaseComp.getLang("LimitFileGrowth"));
        zzRadio.setName("limit");
        zzInput = new TextInput("", "100");
        JRadioButton noRadio = new JRadioButton();
        noRadio.setName("noLimit");
        HDialog dialog = new HDialog(DatabaseComp.dialog, 400, 320);
        HPanel panel = new HPanel();
        CheckBoxInput checkBoxInput = new CheckBoxInput("xxx", DatabaseComp.getLang("enableAutoGrowth"));
        checkBoxInput.setValue("true");
        panel.add(checkBoxInput);
        HPanel incrementPanel = new HPanel();
        incrementPanel.setTitle(DatabaseComp.getLang("fileGrowth"));
        incrementPanel.add(getGridPanel(bfbRadio, bfbInput, buttonGroup1, true, true));
        incrementPanel.add(getGridPanel(mbRadio, mbInput, buttonGroup1, false, true));
        panel.add(incrementPanel);
        HPanel maxFileSizePanel = new HPanel();
        maxFileSizePanel.setTitle(DatabaseComp.getLang("maximumFileSize"));
        maxFileSizePanel.add(getGridPanel(zzRadio, zzInput, buttonGroup2, false, false));
        maxFileSizePanel.add(getGridPanel(noRadio, new LabelInput(), buttonGroup2, true, false));
        panel.add(maxFileSizePanel);
        JsonObject res = new JsonObject();
        checkBoxInput.addListen(e -> {
            if (checkBoxInput.isChecked()) {
                stateChange(true);
                stateChange(false);
                bfbRadio.setEnabled(true);
                mbRadio.setEnabled(true);
                zzRadio.setEnabled(true);
                noRadio.setEnabled(true);
            } else {
                bfbInput.setEnabled(false);
                mbInput.setEnabled(false);
                zzInput.setEnabled(false);
                bfbRadio.setEnabled(false);
                mbRadio.setEnabled(false);
                zzRadio.setEnabled(false);
                noRadio.setEnabled(false);
            }
        });
        HButton saveBtn = new HButton(DatabaseComp.getLang("submit")) {
            @Override
            public void onClick() {
                submitClick(checkBoxInput.isChecked(), res);
                dialog.dispose();
            }
        };
        HButton cancelBtn = new HButton(DatabaseComp.getLang("cancel")) {
            @Override
            public void onClick() {
                if (row == 0) {
                    res.add(JsonCol.__TEXT, DatabaseComp.getLang("value1"));
                    res.add("fileGrowth", "1024kb");
                    res.add("maxSize", "");
                } else {
                    res.add(JsonCol.__TEXT, DatabaseComp.getLang("value2"));
                    res.add("fileGrowth", "10%");
                    res.add("maxSize", "");
                }
                dialog.dispose();
            }
        };

        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(saveBtn);
        barPanel.add(cancelBtn);
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        dialog.setWindowTitle(title);
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(panel.getComp());
        lastPanel.setFoot(barPanel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        dialog.setRootPanel(rootPanel);
        dialog.show();
        return res;
    }

    public HGridPanel getGridPanel(JRadioButton rBtn, AbsInput input, ButtonGroup buttonGroup, boolean selected, boolean flag) {
        HGridPanel gridPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C6, GridSplitEnum.C6));
        rBtn.setSelected(selected);
        input.setEnabled(selected);
        rBtn.addItemListener(e -> stateChange(flag));
        buttonGroup.add(rBtn);
        AbsHComp radioComp = new AbsHComp() {
            @Override
            public Component getComp() {
                return rBtn;
            }
        };
        gridPanel.setComp(1, radioComp);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    protected void stateChange(boolean flag) {
        String value = getCheckValue(flag);
        if (StringUtils.isNotEmpty(value)) {
            System.out.println(value);
            switch (value) {
                case "bfb":
                    bfbInput.setEnabled(true);
                    mbInput.setEnabled(false);
                    break;
                case "mb":
                    bfbInput.setEnabled(false);
                    mbInput.setEnabled(true);
                    break;
                case "limit":
                    zzInput.setEnabled(true);
                    break;
                case "noLimit":
                    zzInput.setEnabled(false);
                    break;
                default:
            }
        }
    }

    public String getCheckValue(boolean flag) {
        Enumeration<AbstractButton> buttons;
        if (flag) {
            buttons = buttonGroup1.getElements();
        } else {
            buttons = buttonGroup2.getElements();
        }
        AbstractButton btn;
        while (buttons.hasMoreElements()) {
            btn = buttons.nextElement();
            if (btn.isSelected()) {
                return btn.getName();
            }
        }
        return null;
    }

    public void submitClick(boolean bool, JsonObject data) {
        StringBuilder text = new StringBuilder(DatabaseComp.getLang("Increments"));
        if (bool) {
            String increment = getCheckValue(true);
            if ("bfb".equals(increment)) {
                data.add("fileGrowth", bfbInput.getValue() + "%");
                text.append(bfbInput.getValue()).append("%");
            } else {
                data.add("fileGrowth", 1024 * Integer.parseInt(mbInput.getValue()) + "kb");
                text.append(mbInput.getValue()).append(" MB");
            }
            String maxFileSize = getCheckValue(false);
            if ("limit".equals(maxFileSize)) {
                data.add("maxSize", 1024 * Integer.parseInt(zzInput.getValue()) + "kb");
                text.append(DatabaseComp.getLang("maximumGrowthLimit")).append(zzInput.getValue()).append(" MB");
            } else {
                data.add("maxSize", "");
                text.append("，").append(DatabaseComp.getLang("unlimitedFileGrowth"));
            }
            data.add(JsonCol.__TEXT, text.toString());
        }
    }
}
