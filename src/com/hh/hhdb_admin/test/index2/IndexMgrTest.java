package com.hh.hhdb_admin.test.index2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.mgr.index2.IndexBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class IndexMgrTest {
	public static void main(String[] args) throws Exception {
		MgrTestUtil.doTestMgr(IndexMgrTest.class);
		
		MgrTestUtil.initCsVarJdbc();
		HBarPanel barPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		barPanel.add(new HButton("发送MGR") {
			@Override
			protected void onClick() {
				IndexBean bean = new IndexBean(IndexBean.OprType.SHOW,null);
				bean.setSchema("HU");
				bean.setTableName("MYTABLE");
				CsVarUtil.eng2.doCall(CsMgrEnum.INDEX, bean);
			}
		});
		MgrTestUtil.showComp(barPanel);
		CsVarUtil.parentFrame.setWindowTitle("索引测试用例");
	}

}
