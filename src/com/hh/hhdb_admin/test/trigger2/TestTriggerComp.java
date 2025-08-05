package com.hh.hhdb_admin.test.trigger2;

import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.trigger2.TriggerPanel;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

/**
 *触发器comp测试
 */
public class TestTriggerComp {
    public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.initCsVarJdbc();
		
		HBarPanel bar=new HBarPanel();
		bar.add(new HButton("新建触发器") {
			@Override
			public void onClick() {
				String schema = StringUtils.isBlank(CsVarUtil.jdbc.getSchema()) ? CsVarUtil.jdbc.getUser() : CsVarUtil.jdbc.getSchema();
				
				TriggerPanel comp = new TriggerPanel(CsVarUtil.conn,CsVarUtil.jdbc, schema, "") {
                    @Override
                    protected void inform() {
                        System.out.println("通知刷新树节点的触发器集合");
                    }
                };
                comp.show();
			}
		});
		CsVarUtil.parentFrame.setToolBar(bar);
		CsVarUtil.parentFrame.show();
		
    }


   

}
