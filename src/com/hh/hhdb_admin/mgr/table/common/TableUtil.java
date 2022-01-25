package com.hh.hhdb_admin.mgr.table.common;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/**
 * @author ouyangxu
 * @date 2020-12-22 16:35:55
 */
public class TableUtil {
	public static String save_icon = "save";
	public static String sql_view_icon = "sql_view";

	public static String add_col_icon = "add_col";
	public static String del_col_icon = "del_col";

	/**
	 * 获取默认模式名
	 *
	 * @param dbTypeEnum 数据库类型
	 * @param jdbcBean   jdbc
	 * @return 模式名
	 */
	public static String getDefSchema(DBTypeEnum dbTypeEnum, JdbcBean jdbcBean) {
		if (jdbcBean == null || dbTypeEnum == null) {
			return null;
		}
		String schema = jdbcBean.getSchema();
		if (StringUtils.isBlank(schema)) {
			switch (dbTypeEnum) {
				case hhdb:
				case pgsql:
					schema = "public";
					break;
				default:
					schema = jdbcBean.getUser();
			}
		}
		return schema;
	}

	public static boolean comboBoxIsEnable(DBTypeEnum dbTypeEnum) {
		switch (dbTypeEnum) {
			case hhdb:
			case db2:
			case pgsql:
				return true;
			default:
				return false;
		}
	}

	public static boolean showPartition(DBTypeEnum dbTypeEnum) {
		return (dbTypeEnum == DBTypeEnum.hhdb || dbTypeEnum == DBTypeEnum.pgsql);
	}

	public static void stopEditing(HTable table) {
		if (table != null) {
			JTable jTable = table.getComp();
			if (jTable.isEditing()) {
				jTable.getCellEditor().stopCellEditing();
			}
		}
	}

	public static ImageIcon getIcon(String name) {
		return IconFileUtil.getIcon(new IconBean(CsMgrEnum.TABLE.name(), name, IconSizeEnum.SIZE_16));
	}

	public static HBarLayout newBarPan() {
		return newBarPan(AlignEnum.LEFT);
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
}
