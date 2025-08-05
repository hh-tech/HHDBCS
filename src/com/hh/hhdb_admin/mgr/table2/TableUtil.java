package com.hh.hhdb_admin.mgr.table2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;

import javax.swing.*;


public class TableUtil {
	public static boolean showPartition(DBTypeEnum dbTypeEnum) {
		return (dbTypeEnum == DBTypeEnum.hhdb || dbTypeEnum == DBTypeEnum.pgsql || dbTypeEnum == DBTypeEnum.tdsql4pg ||
				 dbTypeEnum == DBTypeEnum.gaussdb || dbTypeEnum == DBTypeEnum.db2 || dbTypeEnum == DBTypeEnum.greenplum);
	}

	public static void stopEditing(HTable table) {
		if (table != null) {
			JTable jTable = table.getComp();
			if (jTable.isEditing()) {
				jTable.getCellEditor().stopCellEditing();
			}
		}
	}
	
	public static HBarLayout newBarPan(AlignEnum alignEnum) {
		HBarLayout hBarLayout = new HBarLayout();
		hBarLayout.setAlign(alignEnum);
		hBarLayout.setTopHeight(5);
		hBarLayout.setBottomHeight(5);
		return hBarLayout;
	}

	public static HeaderConfig newHeaderConfig(String name){
		HeaderConfig headerConfig = new HeaderConfig(name);
		headerConfig.setTitleEditable(false);
		headerConfig.setFixTab(true);
		headerConfig.setDetachEnabled(false);
		return headerConfig;
	}
	
	public static String getTableCol(HTabRowBean be, String name) {
		if (be.getCurrRow() != null && be.getCurrRow().get(name) != null) {
			return be.getCurrRow().get(name);
		} else if (be.getOldRow() != null && be.getOldRow().get(name) != null) {
			return be.getOldRow().get(name);
		} else {
			return null;
		}
	}
}
