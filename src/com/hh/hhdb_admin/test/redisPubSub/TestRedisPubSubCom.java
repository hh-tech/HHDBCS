package com.hh.hhdb_admin.test.redisPubSub;

import java.util.HashMap;
import java.util.Map;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.redisPubSub.PubSubComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestRedisPubSubCom {
    public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
        CsVarUtil.jdbc = new JdbcInfo(MgrTestUtil.getJdbcBean(DBTypeEnum.redis).getJobj());
		HBarPanel bar=new HBarPanel();
		Map<String,PubSubComp> map=new HashMap<>();
		HTabPanel tabPanel=new HTabPanel() {
			protected void onClose(String id) {
				map.get(id).close();
				map.remove(id);
			}
		};
		bar.add(new HButton("发布窗口") {
			@Override
			public void onClick() {
				try {
					String id=System.currentTimeMillis()+"";
					PubSubComp pub = new PubSubComp(CsVarUtil.jdbc,true);
					tabPanel.setPanel(id, pub.getLastPanel(), new HeaderConfig("发布窗口"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
		bar.add(new HButton("订阅窗口") {
			@Override
			public void onClick() {
				try {
					String id=System.currentTimeMillis()+"";
					PubSubComp pub = new PubSubComp(CsVarUtil.jdbc,false);
					tabPanel.setPanel(id, pub.getLastPanel(), new HeaderConfig("订阅窗口"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		CsVarUtil.parentFrame.setToolBar(bar);
		MgrTestUtil.showComp(tabPanel);
    }
    
}
