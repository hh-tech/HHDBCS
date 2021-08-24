package com.hh.hhdb_admin.test.trigger;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.trigger.comp.TriggerComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.io.File;
import java.sql.Connection;

/**
 *触发器comp测试
 */
public class TriggerCompTest {
//    private static final HFrame frame = new HFrame();
    public static void main(String[] args) {
        try {
            HHSwingUi.init();

            IconFileUtil.setIconBaseDir(new File("etc/icon/"));

            HFrame frame = new HFrame();
            HDivLayout layout = new HDivLayout(20, 30, GridSplitEnum.C12);
            layout.setxBorderWidth(20);


            HPanel panel = new HPanel(layout);
            HButton createTriggerBtn = getCreateTriggerButton();
            createTriggerBtn.setText("创建触发器panel");





            panel.add(createTriggerBtn);

            frame.setRootPanel(panel);
            frame.show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }


    /**
     * 修改角色
     * @return
     */
    private static HButton getCreateTriggerButton(){
        return new HButton() {
            @Override
            protected void onClick() {
                Connection conn = null;
                try {
                    JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
                    conn = ConnUtil.getConn(jdbcBean);
                    TriggerComp comp = new TriggerComp(conn, jdbcBean, TriggerTestUtil.getTestSchema(), "") {
                        @Override
                        protected void refreshTreeData(String schemaName, String tabName) {
                            System.out.println("通知刷新树节点的触发器集合");
                        }
                    };
                    comp.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(e);
                }finally {
                    ConnUtil.close(conn);
                }
            }
        };
    }

}
