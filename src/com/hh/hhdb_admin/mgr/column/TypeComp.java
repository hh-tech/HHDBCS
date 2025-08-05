package com.hh.hhdb_admin.mgr.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.db_sql.datatype.DbAllTypeUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.comp.AbsDataTypePanel;

import java.awt.event.ItemEvent;

public class TypeComp extends AbsDataTypePanel {
    private Boolean isAutoIncrease = false;

    public TypeComp(AlignEnum labelAlign) {
        this(null, labelAlign);
    }

    public TypeComp(HDivLayout layout, AlignEnum labelAlign) {
        super(layout, labelAlign);

        layout = getLayout();
        layout.setLeftWidth(0);
        layout.setRightWidth(0);
        layout.setBottomHeight(0);
        layout.setTopHeight(0);

        updateTypeItems();
    }

    @Override
    protected void onDataTypeChange(ItemEvent e) {
        typeDefine = e.getItem() == null ? null : DbAllTypeUtil.getDefineType(dbType, version, String.valueOf(e.getItem()));
        HPanel hPanel = TypeComp.this;
        hPanel.removeAll();
        hPanel.add(selectPanel);

        HPanel inputPanel = null;
        if (isTwoParam()) {
            inputPanel = new HPanel(new HDivLayout(GridSplitEnum.C6));
            inputPanel.add(precisionLabel);
            inputPanel.add(scaleLabel);
            inputPanel.add(lengthSpinner);
            inputPanel.add(scaleSpinner);
        }
        if (isOneParam()) {
            inputPanel = new HPanel();
            inputPanel.add(lengthLabel);
            inputPanel.add(lengthSpinner);
        }
        hPanel.add(inputPanel);
        updateUI();
    }

    public Boolean isAutoIncrease() {
        return isAutoIncrease;
    }

    public void setAuto(Boolean isAutoIn) {
        this.isAutoIncrease = isAutoIn;
    }
}
