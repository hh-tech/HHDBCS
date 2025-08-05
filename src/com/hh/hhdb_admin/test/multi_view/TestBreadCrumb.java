package com.hh.hhdb_admin.test.multi_view;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.hhdb_admin.mgr.multi_view.comp.BreadCrumb;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBreadCrumb {
    public static void main(String[] args) {
        HDialog hDialog = new HDialog();
        HPanel hPanel = new HPanel();

        LabelInput labelInput = new LabelInput();
        labelInput.setAlign(AlignEnum.LEFT);

        BreadCrumb<Void> breadCrumb = new BreadCrumb<Void>("1", null) {
            @Override
            protected void onChange(List<String> oldNavi, List<String> newNavi) {
                labelInput.setValue(StringUtils.join(newNavi, "."));
            }
        };
        for (int i = 0; i < 10; i++) {
            breadCrumb.forward("Label " + i, null);
        }

        AtomicInteger x = new AtomicInteger();
        hPanel.add(breadCrumb);
        HButton fBtn = new HButton("前进") {
            @Override
            protected void onClick() {
                breadCrumb.forward(x.getAndIncrement() + "", null);
            }
        };
        HButton bBtn = new HButton("后退") {
            @Override
            protected void onClick() {
                breadCrumb.back();
            }
        };
        hPanel.add(fBtn, bBtn);
        hPanel.add(labelInput);

        hDialog.setRootPanel(hPanel);
        hDialog.show();
    }
}
