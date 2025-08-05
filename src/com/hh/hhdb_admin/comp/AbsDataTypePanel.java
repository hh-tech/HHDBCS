package com.hh.hhdb_admin.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.db_sql.datatype.DbAllTypeUtil;
import com.hh.frame.db_sql.datatype.TypeDefineEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.IntSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.List;

public abstract class AbsDataTypePanel extends HPanel {
    protected HBarPanel selectPanel;
    protected DBTypeEnum dbType = CsVarUtil.getDbtype();
    protected DBTypeEnum.Version version = CsVarUtil.jdbc.getCurrentVersion();
    protected AlignEnum labelAlign;
    protected TypeDefineEnum typeDefine = null;
    protected boolean oftenUsed = true;
    protected CheckBoxInput oftenUsedCheckBox;
    protected SelectBox dataTypeSelectBox;
    protected IntSpinner lengthSpinner;
    protected IntSpinner scaleSpinner;
    protected LabelInput typeLabel;
    protected LabelInput lengthLabel;
    protected LabelInput precisionLabel;
    protected LabelInput scaleLabel;
    private final List<String> ofenUsedTypes;
    private final List<String> otherTypes;

    public AbsDataTypePanel() {
        this(null, AlignEnum.LEFT);
    }

    public AbsDataTypePanel(HDivLayout layout, AlignEnum labelAlign) {
        super(layout);
        this.labelAlign = labelAlign;

        oftenUsedCheckBox = new CheckBoxInput("oftenUsed", LangMgr.lang(Word2.OFTEN_USED)) {
            @Override
            protected void onClick(ActionEvent e) {
                oftenUsed = isChecked();
                updateTypeItems();
            }
        };
        oftenUsedCheckBox.setValue(true);

        lengthSpinner = new IntSpinner(Integer.MIN_VALUE, null, null, 1);
        scaleSpinner = new IntSpinner(Integer.MIN_VALUE, null, null, 1);

        dataTypeSelectBox = new SelectBox() {
            @Override
            protected void onItemChange(ItemEvent e) {
                int[] defaultNumValues = DbAllTypeUtil.getDefaultNumValues(dbType, version, String.valueOf(e.getItem()));
                if (defaultNumValues != null) {
                    lengthSpinner.setValue(defaultNumValues[0]);
                    if (defaultNumValues.length > 1)
                        scaleSpinner.setValue(defaultNumValues[1]);
                }

                onDataTypeChange(e);
            }
        };

        typeLabel = new LabelInput(LangMgr.lang(Word2.TYPE) + ":", labelAlign);
        lengthLabel = new LabelInput(LangMgr.sLang(CsL.TIP_LENGTH) + ":", labelAlign);
        precisionLabel = new LabelInput(LangMgr.sLang(CsL.TIP_PRECISION) + ":", labelAlign);
        scaleLabel = new LabelInput(LangMgr.sLang(CsL.TIP_SCALE) + ":", labelAlign);

        selectPanel = new HBarPanel();
        selectPanel.setLastHComp(dataTypeSelectBox);
        selectPanel.add(oftenUsedCheckBox);
        ofenUsedTypes = DbAllTypeUtil.getTypes(dbType, version, null, true);
        otherTypes = DbAllTypeUtil.getTypes(dbType, version, null, false);
    }

    protected void onDataTypeChange(ItemEvent e) {
    }

    protected void updateTypeItems() {
        dataTypeSelectBox.removeAllItems();
        ofenUsedTypes.forEach(type -> dataTypeSelectBox.addOption(type, type));

        if (!oftenUsed)
            otherTypes.forEach(type -> dataTypeSelectBox.addOption(type, type));
    }

    public String getType() {
        return String.valueOf(dataTypeSelectBox.getComp().getSelectedItem());
    }

    public int getLength() {
        return lengthSpinner.getInt();
    }

    public int getScale() {
        return scaleSpinner.getInt();
    }

    public String getFullType() {
        int[] ints = new int[]{};
        if (isOneParam())
            ints = new int[]{getLength()};
        if (isTwoParam())
            ints = new int[]{getLength(), getScale()};
        return DbAllTypeUtil.toTypeWithValue(dbType, version, getType(), ints);
    }

    public void setByFullType(String fullType) {
        if (StringUtils.isBlank(fullType))
            return;

        oftenUsedCheckBox.setValue(false);
        oftenUsed = false;
        updateTypeItems();

        lengthSpinner.setValue(Integer.MIN_VALUE);
        scaleSpinner.setValue(Integer.MIN_VALUE);
        String datatype = StringUtils.substringBefore(fullType, "(");
        if (!(setBoxValue(datatype, ofenUsedTypes) || (!oftenUsed && setBoxValue(datatype, otherTypes)))) {
            ofenUsedTypes.add(datatype);
            dataTypeSelectBox.addOption(datatype, datatype);
            dataTypeSelectBox.setValue(datatype);
        }
        String intString = StringUtils.substringBetween(fullType, "(", ")");
        if (StringUtils.isNotBlank(intString)) {
            if (intString.contains(",")) {
                String[] ints = StringUtils.splitPreserveAllTokens(intString, ",");
                lengthSpinner.setValue(NumberUtils.toInt(ints[0], Integer.MIN_VALUE));
                scaleSpinner.setValue(NumberUtils.toInt(ints[1], Integer.MIN_VALUE));
            } else {
                lengthSpinner.setValue(NumberUtils.toInt(intString, Integer.MIN_VALUE));
            }
        }
    }

    public void setEnabled(boolean enabled){
        dataTypeSelectBox.setEnabled(enabled);
        oftenUsedCheckBox.setEnabled(enabled);
        lengthSpinner.setEnabled(enabled);
        scaleSpinner.setEnabled(enabled);
    }
    
    private boolean setBoxValue(String datatype, List<String> types) {
        for (String type : types) {
            if (StringUtils.equalsIgnoreCase(datatype, type)) {
                dataTypeSelectBox.setValue(type);
                return true;
            }
        }
        return false;
    }

    protected boolean isOneParam() {
        return typeDefine == TypeDefineEnum.FORCE_ONE_NUM || typeDefine == TypeDefineEnum.CAN_HAS_ONE_NUM || (typeDefine == null && getLength() != Integer.MIN_VALUE && getScale() == Integer.MIN_VALUE);
    }

    protected boolean isTwoParam() {
        return typeDefine == TypeDefineEnum.CAN_HAS_TWO_NUM || (typeDefine == null && getLength() != Integer.MIN_VALUE && getScale() != Integer.MIN_VALUE);
    }
}