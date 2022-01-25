package com.hh.hhdb_admin.mgr.login.base;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;

import javax.swing.*;

public class TestInput extends HGridPanel {

    private final LabelInput labelInput;

    private final AbsInput absInput;

    public TestInput(String label, AbsInput absInput) {
        super(new HGridLayout(GridSplitEnum.C3));
        labelInput = new LabelInput(label, AlignEnum.CENTER);
        labelInput.getComp().setToolTipText(label);
        this.absInput = absInput;
        getComp().setBorder(BorderFactory.createEmptyBorder(0,0,10,15));
        setComp(1, labelInput);
        setComp(2, absInput);
    }

    public TestInput(String label, AbsInput absInput, CheckBoxInput checkBoxInput) {
        super(new HGridLayout(GridSplitEnum.C3, GridSplitEnum.C8));
        labelInput = new LabelInput(label, AlignEnum.CENTER);
        labelInput.getComp().setToolTipText(label);
        this.absInput = absInput;
        getComp().setBorder(BorderFactory.createEmptyBorder(0,0,10,15));
        setComp(1, labelInput);
        setComp(2, absInput);
        setComp(3, checkBoxInput);
    }

    public void setLabel(String label) {
        labelInput.getComp().setText(label);
        labelInput.getComp().setToolTipText(label);
    }

    public void setValue(String value) {
        absInput.setValue(value);
    }

    public String getValue() {
        return absInput.getValue();
    }

    public void setEnable(boolean enable) {
        labelInput.setEnabled(enable);
        absInput.setEnabled(enable);
    }

    public AbsInput getInput() {
        return absInput;
    }

}
