package com.hh.hhdb_admin.mgr.login;

import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;

/**
 * @author YuSai
 */
public class LoginInput extends HGridPanel {

    private final LabelInput labelInput;

    private final AbsInput absInput;

    public LoginInput(String label, AbsInput absInput) {
        super(new HGridLayout(GridSplitEnum.C2));
        labelInput = new LabelInput(label);
        this.absInput = absInput;
        setComp(1, labelInput);
        setComp(2, absInput);
    }

    public LoginInput(String label, AbsInput absInput, HButton button) {
        super(new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C8, GridSplitEnum.C2));
        labelInput = new LabelInput(label);
        this.absInput = absInput;
        setComp(1, labelInput);
        setComp(2, absInput);
        setComp(3, button);
    }

    public void setLabel(String label) {
        labelInput.getComp().setText(label);
    }

    public void setValue(String value) {
        absInput.setValue(value);
    }

    public String getValue() {
        return absInput.getValue();
    }

    public AbsInput getInput() {
        return absInput;
    }

}
