package com.hh.hhdb_admin.mgr.column;

import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.input.SelectBox;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认值组件
 */
public class DefaultValueComp extends AbsHComp {

    private SelectBox defaultBox;
    private final List<String> editableDefaultList = new ArrayList<>();
    private final static String V_EMPTY_STR = "''";
    private final static String V_NULL = "null";
    private final static String V_EMPTY = "";

    public DefaultValueComp() {
        initEditableDefaultList();
        initDefaultBox();
        this.comp = defaultBox.getComp();
    }

    /**
     * 初始化默认值选择框
     */
    private void initDefaultBox() {
        defaultBox = new SelectBox();
        initDefaultBoxData();
        JComboBox<?> defaultBoxComp = defaultBox.getComp();
        defaultBoxComp.setEditable(true);
        defaultBoxComp.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                defaultBoxComp.setEditable(editableDefaultList.contains(e.getItem().toString()));
            }
        });
    }

    /**
     * 初始化默认可编辑的选项列表
     */
    private void initEditableDefaultList() {
        editableDefaultList.clear();
        editableDefaultList.add(V_EMPTY);
    }

    /**
     * 初始化默认值选择框
     */
    private void initDefaultBoxData() {
        defaultBox.removeAllItems();
        defaultBox.addOption("", V_EMPTY);
        defaultBox.addOption(ColumnComp.getLang("EMPTY_STR"), V_EMPTY_STR);
        defaultBox.addOption(ColumnComp.getLang("NULL_OBJ"), V_NULL);
    }

    /**
     * 修改时的默认值处理
     */
    public void setDefaultValue(String defaultValue) {
        initEditableDefaultList();
        initDefaultBoxData();
        if (defaultValue == null) {
            this.defaultBox.setValue(V_EMPTY);
        } else if ("''".equals(defaultValue.trim()) || "".equals(defaultValue.trim())) {
            this.defaultBox.setValue(V_EMPTY_STR);
        } else if ("null".equals(defaultValue.trim())) {
            this.defaultBox.setValue(V_EMPTY);
        } else {
            this.defaultBox.addOption(defaultValue, defaultValue);
            editableDefaultList.add(defaultValue);
            this.defaultBox.setValue(defaultValue);
        }
    }

    public String getValue() {
        JComboBox<?> defaultBoxComp = defaultBox.getComp();
        String defaultBoxCompValue = defaultBoxComp.getEditor().getItem().toString();
        String value = StringUtils.isBlank(defaultBox.getValue()) ? defaultBoxCompValue : defaultBox.getValue();
        if ("null".equals(value)) {
            return null;
        }
        if (value.startsWith("'") && value.endsWith("'")) {
            return value;
        } else if (StringUtils.isNumeric(value)) {
            return value;
        } else if (isMethod(value)) {
            return value;
        } else if (StringUtils.isBlank(value)) {
            return value;
        } else {
            return "'" + value + "'";
        }
    }

    private boolean isMethod(String value) {
        if (value.startsWith("CURRENT_TIMESTAMP")) {
            return true;
        } else {
            return value.contains("::");
        }
    }
}
