package com.hh.hhdb_admin.test.vm_editor;

import java.io.File;
import java.io.IOException;

import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.vm_editor.VmComp;
import com.hh.hhdb_admin.mgr.vm_editor.VmMgr;

public class VMCompTest {

	public static void main(String[] args) throws Exception {
		try {
            LangMgr2.loadMerge(VmMgr.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
		IconFileUtil.setIconBaseDir(new File("etc/icon/"));
		HHSwingUi.init();

		HFrame frame = new HFrame();
		HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
		layout.setxBorderWidth(20);

		HPanel panel = new HPanel(layout);
		HButton createUsrPanel = new HButton() {
			@Override
			protected void onClick() {
				try {
					VmComp vmComp = new VmComp("");
					HDialog dialog = StartUtil.getMainDialog();
					dialog.setSize(1000, 800);
					HPanel hPanel = new HPanel();
					hPanel.setLastPanel(vmComp.getLastPanel());
					dialog.setRootPanel(hPanel);
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		createUsrPanel.setText("打开模板编辑器");
		panel.add(createUsrPanel);

		frame.setRootPanel(panel);
		frame.show();
	}
}
