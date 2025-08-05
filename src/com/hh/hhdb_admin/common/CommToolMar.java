package com.hh.hhdb_admin.common;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.bean.QueryBean;
import com.hh.hhdb_admin.common.bean.VmBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function2.FunctionBean;

import java.util.*;

/**
 * 工具栏共用方法
 *
 * @author hyz
 */
public class CommToolMar {
	public static final String SWITCH_CONN = "switch_conn";
	public static final String QUERY = "query";
	public static final String SQL_BOOK = "sql_book";
	public static final String GIS = "gis";
	public static final String DB_MONITOR = "db_monitor";
	public static final String DB_TASK = "db_task";
	public static final String QUICK_CMD = "quick_cmd";
	public static final String VM = "vm";
	public static final String DEBUG = "debug";
	public static final String OBJ_QUERY = "obj_query";
	public static final String CMD = "cmd";
	public static final String MULTI_WINDOW = "multi_window";
	public static final String REDISPUB = "publisher";
	public static final String REDISSUB = "subscriber";
	private DBTypeEnum dbtype;

	public CommToolMar() {
		dbtype = CsVarUtil.getDbtype();
	}

	/**
	 * 窗口
	 *
	 */
	public Map<String, Map<String, Enum<?>>> genWindowNames() {
		if (dbtype == DBTypeEnum.redis) return initButtons(Arrays.asList(CMD, VM,REDISPUB,REDISSUB));
		if (dbtype == DBTypeEnum.clickhouse || dbtype == DBTypeEnum.hive || dbtype == DBTypeEnum.databricks || dbtype == DBTypeEnum.hana
				||dbtype == DBTypeEnum.derby ) return initButtons(Arrays.asList(QUERY, CMD, VM, MULTI_WINDOW));
		if (dbtype == DBTypeEnum.mongodb) return initButtons(Collections.singletonList(CMD));
		if (dbtype == DBTypeEnum.others || dbtype == DBTypeEnum.cache) return initButtons(Arrays.asList(QUERY, CMD));
		return initButtons(Arrays.asList(QUERY, CMD, QUICK_CMD, VM, MULTI_WINDOW));
	}

	/**
	 * 其他窗口
	 *
	 */
	public Map<String, Map<String, Enum<?>>> genOtherNames() {
		List<String> otherList = new ArrayList<>();
		if (DBTypeEnum.isSupportObjSearch(dbtype) || dbtype == DBTypeEnum.redis) {
			otherList.add(OBJ_QUERY);
		}
		if (CsVarUtil.jdbc.getView() == JdbcInfo.View.USER && dbtype == DBTypeEnum.oracle) {
			otherList.add(DEBUG);
		}
		return initButtons(otherList);
	}

	/**
	 * 管理
	 *
	 */
	public Map<String, Map<String, Enum<?>>> genManageNames() {
		if (dbtype == DBTypeEnum.clickhouse || dbtype == DBTypeEnum.mongodb || dbtype == DBTypeEnum.others || dbtype == DBTypeEnum.cache
				|| dbtype == DBTypeEnum.databricks || dbtype == DBTypeEnum.derby || dbtype == DBTypeEnum.hive || dbtype == DBTypeEnum.sqlite)
			return initButtons(Arrays.asList(SWITCH_CONN, SQL_BOOK));
		List<String> manageNamesList = new ArrayList<>(Arrays.asList(SWITCH_CONN, SQL_BOOK,DB_MONITOR));
		if (dbtype != DBTypeEnum.hive && dbtype != DBTypeEnum.hana) {
			manageNamesList.add(DB_TASK);
		}
		if (CsVarUtil.jdbc.getView() == JdbcInfo.View.USER && (dbtype == DBTypeEnum.hhdb || dbtype == DBTypeEnum.pgsql)) {
			manageNamesList.add(GIS);
		}
		return initButtons(manageNamesList);
	}

	public void onBtnClick(String item) {
		switch (item) {
			case SWITCH_CONN:
				CsVarUtil.eng2.doCall(CsMgrEnum.LOGIN, new BaseOpBean(BaseOpEnum.LOGIN_SWITCH));
				break;
			case QUERY:
				CsVarUtil.eng2.doCall(CsMgrEnum.QUERY, new QueryBean());
				break;
			case SQL_BOOK:
				CsVarUtil.eng2.doCall(CsMgrEnum.SQL_BOOK, new BaseOpBean(BaseOpEnum.SHOW));
				break;
			case DB_MONITOR:
				CsVarUtil.eng2.doCall(CsMgrEnum.MONITOR, new BaseOpBean(BaseOpEnum.SHOW));
				break;
			case DB_TASK:
				CsVarUtil.eng2.doCall(CsMgrEnum.DB_TASK, new BaseOpBean(BaseOpEnum.INIT));
				break;
			case QUICK_CMD:
				CsVarUtil.eng2.doCall(CsMgrEnum.QUICK_CMD, new BaseOpBean(BaseOpEnum.SHOW));
				break;
			case VM:
				CsVarUtil.eng2.doCall(CsMgrEnum.VM, new VmBean());
				break;
			case GIS:
				CsVarUtil.eng2.doCall(CsMgrEnum.GIS, new BaseOpBean(BaseOpEnum.SHOW));
				break;
			case DEBUG:
				CsVarUtil.eng2.doCall(CsMgrEnum.FUNCTION, new FunctionBean(FunctionBean.OprType.DEBUG,null));
				break;
			case OBJ_QUERY:
				CsVarUtil.eng2.doCall(CsMgrEnum.OBJ_QUERY, new BaseOpBean(BaseOpEnum.SHOW));
				break;
			case CMD:
				CsVarUtil.eng2.doCall(CsMgrEnum.CMD, new BaseOpBean(BaseOpEnum.SHOW));
				break;
			case MULTI_WINDOW:
				CsVarUtil.eng2.doCall(CsMgrEnum.MULTI_WINDOW, new BaseOpBean(BaseOpEnum.SHOW));
				break;
			case REDISPUB:
				CsVarUtil.eng2.doCall(CsMgrEnum.REDISPUBSUB, new BaseOpBean(BaseOpEnum.PUBLISHER));
				break;
			case REDISSUB:
				CsVarUtil.eng2.doCall(CsMgrEnum.REDISPUBSUB, new BaseOpBean(BaseOpEnum.SUBSCRIBER));
				break;
			default:
		}
	}

	/**
	 * 初始化按钮与图片
	 * @param item
	 * @return
	 */
	private Map<String, Map<String, Enum<?>>> initButtons(List<String> item) {
		Map<String, Map<String, Enum<?>>> map = new LinkedHashMap<>();
		for (String s : item) {
			Map<String, Enum<?>> valMap = new LinkedHashMap<>();
			String name = null;
			Enum<?> font = null;
			switch (s) {
				case CommToolMar.SWITCH_CONN:
					font = FaSolidEnum.REPEAT;
					name = LangMgr.lang(Word4.CONNECT_MANAGER);
					break;
				case CommToolMar.QUERY:
					font = FaSolidEnum.MAGNIFYING_GLASS;
					name = LangMgr.lang(Word4.QUERY_WINDOW);
					break;
				case CommToolMar.SQL_BOOK:
					font = FaSolidEnum.BOOK;
					name = LangMgr.lang(Word4.FILE_MANAGER);
					break;
				case CommToolMar.DB_MONITOR:
					font = FaSolidEnum.COMPUTER;
					name = LangMgr.lang(Word4.MONITORING_MANAGEMENT);
					break;
				case CommToolMar.DB_TASK:
					font = FaSolidEnum.CALENDAR_CHECK;
					name = LangMgr.lang(Word4.TASK_MANAGE);
					break;
				case CommToolMar.QUICK_CMD:
					font = FaSolidEnum.BOLT_LIGHTNING;
					name = LangMgr.lang(Word4.QUICK_WINDOWS);
					break;
				case CommToolMar.VM:
					font = FaSolidEnum.TABLE_COLUMNS;
					name = LangMgr.lang(Word4.VM_WINDOW);
					break;
				case CommToolMar.GIS:
					font = FaSolidEnum.EARTH_AMERICAS;
					name = LangMgr.lang(Word4.GIS);
					break;
				case CommToolMar.DEBUG:
					font = FaSolidEnum.BUG;
					name = LangMgr.lang(Word4.DEBUG);
					break;
				case CommToolMar.OBJ_QUERY:
					font = FaSolidEnum.MAGNIFYING_GLASS_CHART;
					name = LangMgr.lang(Word4.OBJECT_QUERY);
					break;
				case CommToolMar.CMD:
					font = FaSolidEnum.LAPTOP_CODE;
					name = LangMgr.lang(Word4.COMMAND_WINDOW);
					break;
				case CommToolMar.MULTI_WINDOW:
					font = FaSolidEnum.TABLE_CELLS_LARGE;
					name = LangMgr.lang(Word4.TILE_WINDOWS);
					break;
				case CommToolMar.REDISPUB:
					font = FaSolidEnum.PAPER_PLANE;
					name = LangMgr.lang(Word4.PUBLISH_WINDOW);
					break;
				case CommToolMar.REDISSUB:
					font = FaSolidEnum.SQUARE_RSS;
					name = LangMgr.lang(Word4.SUBSCRIBE_WINDOW);
					break;
				default:
			}
			valMap.put(name,font);
			map.put(s, valMap);
		}
		return map;
	}
}