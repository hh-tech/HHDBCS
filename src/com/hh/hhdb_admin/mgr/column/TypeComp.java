package com.hh.hhdb_admin.mgr.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.column.AbsColumn;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

public class TypeComp extends AbsHComp {
    private final HDialog dialog;
    private TextInput valueInput;
    private TextInput lengthInput;
    private TextInput preInput;
    private TextInput scaleInput;
    private final CheckBoxInput autoInput;
    private final CheckBoxInput zeroBoxInput;
    private final CheckBoxInput unsignedBoxInput;
    private SelectBox dataTypeBox;
    private final HPanel typePanel;
    private final HPanel szPanel;
    private HPanel lengthPanel = new HPanel();
    private HPanel valuePanel = new HPanel();
    private HPanel preInputPanel = new HPanel();

    private OpValuesComp opValuesComp;

    private final List<String> lenNameList;
    private final List<String> preNameList;
    private final List<String> valueNameList;

    private final static String TYPE_TYPE = "type";
    private final static String TYPE_SCALE = "scale";
    private final static String TYPE_PRECISION = "precision";
    private final static String TYPE_LENGTH = "length";
    private final static String TYPE_OPTION_VALUES = "option_values";
    private final static String TYPE_UNSIGNED = "unsigned";
    private final static String TYPE_ZEROFILL = "zero_fill";
    private final AbsColumn absColumn;

    private enum typeEnum {
        option_values, precision, precisionWithScale, length, unsigned, zero_fill, autoIncrease
    }

    public TypeComp(HDialog dialog, AbsColumn absColumn) {
        this.dialog = dialog;
        this.absColumn = absColumn;
        typePanel = new HPanel();
        szPanel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        szPanel.setBorderColor(Color.lightGray);
        autoInput = new CheckBoxInput("");
        zeroBoxInput = new CheckBoxInput("");
        unsignedBoxInput = new CheckBoxInput("");
        lenNameList = absColumn.getNeedLengthType();
        preNameList = absColumn.getNeedPsType();
        valueNameList = absColumn.getNeedValueType();
        initDataTypeBox();
        initLengthPanel();
        initPrePanel();
        initValuePanel();
        this.comp = typePanel.getComp();
    }

    public void setType(String typeWithInfo) {
        HashMap<String, String> map = typeHandler(typeWithInfo);
        initTypeData(map);
    }

    public void setType(String typeInDb, String length, String precision, String scale, String values) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TYPE_TYPE, typeInDb.toUpperCase());
        map.put(TYPE_LENGTH, length);
        map.put(TYPE_PRECISION, precision);
        map.put(TYPE_SCALE, scale);
        map.put(TYPE_OPTION_VALUES, values);
        initTypeData(map);
    }

    /**
     * 组合sql语句中的类型
     */
    public String getType() {
        String type = dataTypeBox.getValue();
        String returnType = type;
        if (lenNameList.contains(type)) {
            if (!StringUtils.isBlank(lengthInput.getValue())) {
                returnType = type + "(" + lengthInput.getValue() + ")";
            }
        } else if (preNameList.contains(type)) {
            if (!StringUtils.isBlank(preInput.getValue())) {
                if (StringUtils.isBlank(scaleInput.getValue())) {
                    returnType = type + "(" + preInput.getValue() + ")";
                } else {
                    returnType = type + "(" + preInput.getValue() + "," + scaleInput.getValue() + ")";
                }
            }
        } else if (absColumn.getNeedValueType().contains(type)) {
            returnType = type + "(" + valueInput.getValue() + ")";
        }
        if (absColumn.getUnsignedType().contains(type)) {
            returnType = Boolean.parseBoolean(unsignedBoxInput.getValue()) ? returnType + " UNSIGNED " : returnType;
        }
        if (absColumn.getZeroFillType().contains(type)) {
            returnType = Boolean.parseBoolean(zeroBoxInput.getValue()) ? returnType + " ZEROFILL " : returnType;
        }
        return returnType;
    }

    protected void validate() {

    }

    private void showType(List<typeEnum> typeEnums) {
        removeElsePanel();
        szPanel.getComp().removeAll();
        if (typeEnums.contains(typeEnum.autoIncrease)) {
            initAutoBox();
            szPanel.add(autoInput);
        }
        if (typeEnums.contains(typeEnum.unsigned)) {
            initUnSignedBox();
            szPanel.add(unsignedBoxInput);
        }
        if (typeEnums.contains(typeEnum.zero_fill)) {
            initZeroFillBox();
            szPanel.add(zeroBoxInput);
        }
        if (szPanel.getComp().getComponents().length > 0) {
            typePanel.add(szPanel);
        }
        if (typeEnums.contains(typeEnum.length)) {
            typePanel.add(lengthPanel);
        }

        if (typeEnums.contains(typeEnum.precision)) {
            typePanel.add(preInputPanel);
            scaleInput.setEnabled(false);
        }
        if (typeEnums.contains(typeEnum.precisionWithScale)) {
            typePanel.add(preInputPanel);
            scaleInput.setEnabled(true);
        }
        if (typeEnums.contains(typeEnum.option_values)) {
            typePanel.add(valuePanel);
        }
        validate();
    }

    /**
     * 是否自动增长
     */
    public boolean isAutoIncrease(DBTypeEnum dbTypeEnum) {
        if (DBTypeEnum.mysql.equals(dbTypeEnum) || DBTypeEnum.sqlserver.equals(dbTypeEnum)) {
            for (String type : absColumn.getAutoIncreaseType()) {
                if (getType().contains(type)) {
                    return Boolean.parseBoolean(autoInput.getValue());
                }
            }
        }
        return false;
    }

    public void setAuto(boolean auto) {
        autoInput.setValue(String.valueOf(auto));
    }

    private void removeElsePanel() {
        typePanel.remove(szPanel);
        typePanel.remove(lengthPanel);
        typePanel.remove(preInputPanel);
        typePanel.remove(valuePanel);
    }

    /**
     * 添加选项
     */
    private void refreshDataTypeOption() {
        dataTypeBox.removeAllItems();
        List<String> types = absColumn.getColTypes();
        for (String type : types) {
            dataTypeBox.addOption(type, type);
        }
    }

    /**
     * 初始化数据类型选择框
     */
    private void initDataTypeBox() {
        dataTypeBox = new SelectBox() {
            @Override
            protected void onItemChange(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    List<typeEnum> typeEnums = new ArrayList<>();
                    if (lenNameList.contains(e.getItem().toString())) {
                        typeEnums.add(typeEnum.length);
                    }
                    if (preNameList.contains(e.getItem().toString())) {
                        typeEnums.add(absColumn.getNoScaleType().contains(e.getItem().toString()) ? typeEnum.precision : typeEnum.precisionWithScale);
                    }
                    if (absColumn.getNeedValueType().contains(e.getItem().toString())) {
                        typeEnums.add(typeEnum.option_values);
                    }
                    if (absColumn.getUnsignedType().contains(e.getItem().toString())) {
                        typeEnums.add(typeEnum.unsigned);
                    }
                    if (absColumn.getZeroFillType().contains(e.getItem().toString())) {
                        typeEnums.add(typeEnum.zero_fill);
                    }
                    if (absColumn.getAutoIncreaseType().contains(e.getItem().toString())) {
                        typeEnums.add(typeEnum.autoIncrease);
                    }
                    showType(typeEnums);
                }
            }
        };
        typePanel.add(dataTypeBox);
        refreshDataTypeOption();
    }

    private void initAutoBox() {
        autoInput.setText(ColumnComp.getLang("autoIncr"));
        autoInput.setValue(String.valueOf(Boolean.FALSE));
    }

    private void initZeroFillBox() {
        zeroBoxInput.setText(ColumnComp.getLang("zeroFill"));
        zeroBoxInput.setValue(String.valueOf(Boolean.FALSE));
    }

    private void initUnSignedBox() {
        unsignedBoxInput.setText(ColumnComp.getLang("noSign"));
        unsignedBoxInput.setValue(String.valueOf(Boolean.FALSE));
    }

    /**
     * 设置可选值的面板
     */
    private void initValuePanel() {
        valuePanel = new HPanel();
        LabelInput input = new LabelInput();
        input.setValue(ColumnComp.getLang("TIP_VALUE"));
        input.setAlign(AlignEnum.LEFT);
        valueInput = new TextInput();
        HPanel valueInputPanel = new HPanel(new HDivLayout(GridSplitEnum.C10));
        valueInputPanel.add(valueInput);
        if (opValuesComp == null) {
            opValuesComp = new OpValuesComp(dialog) {
                @Override
                protected void clickQrBtn() {
                    super.clickQrBtn();
                    StringJoiner joiner = new StringJoiner("','", "'", "'");
                    for (String value : this.getValues()) {
                        joiner.add(value);
                    }
                    valueInput.setValue(joiner.toString());
                }
            };
        }
        HButton button = new HButton("...") {
            @Override
            protected void onClick() {
                String[] values = valueInput.getValue().split(",");
                ArrayList<String> list = new ArrayList<>();
                for (String str : values) {
                    list.add(str.replace("'", ""));
                }
                opValuesComp.loadData(list);
                opValuesComp.show();
            }
        };
        valueInputPanel.add(button);
        valuePanel.add(input);
        valuePanel.add(valueInputPanel);
    }

    /**
     * 设置长度的面板
     */
    private void initLengthPanel() {
        lengthPanel = new HPanel();
        LabelInput input = new LabelInput();
        input.setValue(ColumnComp.getLang("TIP_LENGTH"));
        input.setAlign(AlignEnum.LEFT);
        lengthPanel.add(input);
        lengthInput = new TextInput();
        lengthPanel.add(lengthInput);
    }

    /**
     * 设置精度的面板
     */
    private void initPrePanel() {
        LabelInput preLabelInput = new LabelInput();
        preLabelInput.setValue(ColumnComp.getLang("TIP_PRECISION"));
        preLabelInput.setAlign(AlignEnum.LEFT);
        LabelInput scaleLabelInput = new LabelInput();
        scaleLabelInput.setValue(ColumnComp.getLang("TIP_SCALE"));
        scaleLabelInput.setAlign(AlignEnum.LEFT);
        preInputPanel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        preInputPanel.add(preLabelInput);
        preInputPanel.add(scaleLabelInput);
        preInput = new TextInput();
        scaleInput = new TextInput();
        preInputPanel.add(preInput);
        preInputPanel.add(scaleInput);
    }

    private HashMap<String, String> typeHandler(String typeWithInfo) {
        HashMap<String, String> map = new HashMap<>();
        typeWithInfo = typeWithInfo.trim().toUpperCase();
        if (typeWithInfo.contains("(")) {
            map.put(TYPE_TYPE, typeWithInfo.substring(0, typeWithInfo.indexOf("(")));
        } else {
            map.put(TYPE_TYPE, typeWithInfo);
        }
        if (typeWithInfo.contains("(")) {
            String typeStr = typeWithInfo.substring(typeWithInfo.indexOf("(") + 1, typeWithInfo.indexOf(")"));
            if (lenNameList.contains(map.get(TYPE_TYPE))) {
                map.put(TYPE_LENGTH, typeStr);
            } else if (preNameList.contains(map.get(TYPE_TYPE))) {
                String[] arr = typeStr.split(",");
                if (arr.length > 1) {
                    map.put(TYPE_PRECISION, arr[0]);
                    map.put(TYPE_SCALE, arr[1]);
                } else {
                    map.put(TYPE_PRECISION, arr[0]);
                    map.put(TYPE_SCALE, "");
                }
            } else if (valueNameList.contains(map.get(TYPE_TYPE))) {
                map.put(TYPE_OPTION_VALUES, typeStr);
            }
        }
        if (absColumn.getUnsignedType().contains(map.get(TYPE_TYPE)) && typeWithInfo.contains(" UNSIGNED")) {
            map.put(TYPE_UNSIGNED, "true");
        } else {
            map.put(TYPE_UNSIGNED, "false");
        }
        if (absColumn.getUnsignedType().contains(map.get(TYPE_TYPE)) && typeWithInfo.contains(" ZEROFILL")) {
            map.put(TYPE_ZEROFILL, "true");
        } else {
            map.put(TYPE_ZEROFILL, "false");
        }
        return map;
    }

    private void initTypeData(HashMap<String, String> map) {
        String type = map.get(TYPE_TYPE);
        if (!absColumn.getColTypes().contains(type)) {
            dataTypeBox.addOption(type, type);
        }
        dataTypeBox.setValue(type);
        if (valueNameList.contains(type)) {
            valueInput.setValue(map.get(TYPE_OPTION_VALUES));
        }
        if (lenNameList.contains(type)) {
            lengthInput.setValue(map.get(TYPE_LENGTH));
        }
        if (preNameList.contains(type) && absColumn.getNoScaleType().contains(type)) {
            preInput.setValue(map.get(TYPE_PRECISION));
            scaleInput.setValue("");
        }
        if (preNameList.contains(type) && !absColumn.getNoScaleType().contains(type)) {
            preInput.setValue(map.get(TYPE_PRECISION));
            scaleInput.setValue(map.get(TYPE_SCALE));
        }
        if (absColumn.getUnsignedType().contains(type)) {
            unsignedBoxInput.setValue(map.get(TYPE_UNSIGNED));
        }
        if (absColumn.getZeroFillType().contains(type)) {
            zeroBoxInput.setValue(map.get(TYPE_ZEROFILL));
        }
    }

}
