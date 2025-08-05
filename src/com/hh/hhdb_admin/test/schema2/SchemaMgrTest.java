package com.hh.hhdb_admin.test.schema2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.schema2.SchemaBean;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class SchemaMgrTest {
	
	
	public static void main(String[] args) throws Exception {
		
		//请使用postgreSql，sqlserver等数据库
		
		CsVarUtil.jdbc=new JdbcInfo(MgrTestUtil.getJdbcBean(DBTypeEnum.sqlserver).getJobj());
		CsVarUtil.conn=ConnUtil.getConn(CsVarUtil.jdbc);
		CsVarUtil.init(SchemaMgrTest.class,"conf.json");
        CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.INIT));		
		
		HBarPanel barPanel2 = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		
		barPanel2.add(new HButton("ADD") {
			@Override
			protected void onClick() {
				SchemaBean csGuiBean = new SchemaBean(SchemaBean.OprType.ADD,null);
			
				CsVarUtil.eng2.doCall(CsMgrEnum.SCHEMA, csGuiBean);
			}
		});
		
		barPanel2.add(new HButton("UPDATE") {
			@Override
			protected void onClick() {
				SchemaBean csGuiBean = new SchemaBean(SchemaBean.OprType.UPDATE,null);
				CsVarUtil.eng2.doCall(CsMgrEnum.SCHEMA, csGuiBean);
			}
		});
		
		
		HPanel p = new HPanel();
		p.add(barPanel2);
		MgrTestUtil.showComp(p);
		CsVarUtil.parentFrame.setWindowTitle("添加测试用例");
	}

}
