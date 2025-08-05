package com.hh.hhdb_admin.comp.test;

import java.util.ArrayList;
import java.util.List;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.container.HFrame;

import com.hh.hhdb_admin.comp.DBTypeComboBox;


/*
* Author:Zhang Dagang
* Date:2024年2月20日
*/
public class TestDbTypeComboBox {
	  private static List<String> dbTypes = new ArrayList<>();
	    static {
	        dbTypes.add(DBTypeEnum.hhdb.name());
	        dbTypes.add(DBTypeEnum.pgsql.name());
	        dbTypes.add(DBTypeEnum.oracle.name());
	        dbTypes.add(DBTypeEnum.mysql.name());
	        dbTypes.add(DBTypeEnum.mariadb.name());
	        dbTypes.add(DBTypeEnum.db2.name());
	        dbTypes.add(DBTypeEnum.sqlserver.name());
	        dbTypes.add(DBTypeEnum.dm.name());
	        dbTypes.add(DBTypeEnum.kingbase.name());
	        dbTypes.add(DBTypeEnum.tdsql4mysql.name());
	        dbTypes.add(DBTypeEnum.tdsql4pg.name());
	        dbTypes.add(DBTypeEnum.gaussdb.name());
	        dbTypes.add(DBTypeEnum.oceanbase.name());
	        dbTypes.add(DBTypeEnum.hive.name());
	    }
	public static void main(String[] args) {
		HFrame frame=new HFrame();
		DBTypeComboBox box=new DBTypeComboBox();
		box.setDataList(dbTypes);
		frame.setRootPanel(box.getHpanel());
		frame.show();
	}

}
