package com.hh.hhdb_admin.mgr.status;

import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;

/**
 * @author: Jiang
 * @date: 2020/10/13
 */

public class StatusComp extends HPanel {

    public StatusComp() {
        super(new HDivLayout(GridSplitEnum.C2, GridSplitEnum.C2, GridSplitEnum.C2, GridSplitEnum.C2, GridSplitEnum.C2));
        add(new LabelInput("状态1"));
        add(new LabelInput("状态2"));
    }
}
