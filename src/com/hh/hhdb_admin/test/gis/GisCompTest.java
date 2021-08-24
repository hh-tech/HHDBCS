package com.hh.hhdb_admin.test.gis;

import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.gis.GisComp;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;

public class GisCompTest {
    private static HButton createUsrPanel;

	public static void main(String[] args) throws Exception {
        LangMgr.merge(GisMgr.class.getName(), LangUtil.loadLangRes(GisMgr.class));
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        HHSwingUi.init();
        
        HFrame frame = new HFrame();
        HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
        layout.setxBorderWidth(20);
        
        HPanel panel = new HPanel(layout);
        createUsrPanel = new HButton() {
            @Override
            protected void onClick() {
                try {
                    setEnabled(false);
                    GisComp gis = new GisComp(MgrTestUtil.getJdbcBean());
                    HDialog dialog = new HDialog(frame, 1200, 800,false) {
                        @Override
                        public void closeEvent() {
                            gis.closeconn();
                            createUsrPanel.setEnabled(true);
                        }
                    };
                    HPanel hPanel = new HPanel();
                    hPanel.setLastPanel(gis.getLastPanel());
                    dialog.setRootPanel(hPanel);
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        createUsrPanel.setText("打开GIS");
        panel.add(createUsrPanel);
        
        frame.setRootPanel(panel);
        frame.show();
	}
}
