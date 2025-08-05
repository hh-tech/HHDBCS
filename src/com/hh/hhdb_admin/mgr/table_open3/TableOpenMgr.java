package com.hh.hhdb_admin.mgr.table_open3;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.db_sql.OpenTabSqlUtil;
import com.hh.frame.db_sql.dm.DmObjectsSqlUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.bean.TableOpenBean;
import com.hh.hhdb_admin.common.bean.TableOpenBean.OpenObjType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;
import com.hh.hhdb_admin.mgr.table_open3.gui.OpenTableComp;

public class TableOpenMgr extends AbsGuiMgr {
	private Map<String, OpenTableComp> map = new HashMap<>();
	private Map<String, TableOpenBean> dataMap = new HashMap<>();


	
	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.TABLE_OPEN;
	}
	
	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		String openId = "";
		
		File tmpFile = CsVarUtil.openDataFile;
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		
		if (bj instanceof CloseBean) {
			CloseBean closeBean = new CloseBean(bj.getJobj());
			OpenTableComp tabDataComp = map.get(closeBean.getId());
			tabDataComp.close();
			map.remove(closeBean.getId());
		} else if(bj instanceof TableOpenBean){
			TableOpenBean csGuiBean = new TableOpenBean(bj.getJobj());
			String pTableName = csGuiBean.getPartitionTableName();//子表名称
			JdbcBean jdbcBean = CsVarUtil.jdbcCloneWithSsh();
			switch (csGuiBean.getOprType()) {
				case SHOW:
					String schemaName = csGuiBean.getSchema();
					jdbcBean.setSchema(schemaName);
					String table = csGuiBean.getTableName();
					OpenObjType openObjType = csGuiBean.getOpenObjType();
					DBTypeEnum dbtype=CsVarUtil.getDbtype();

					if (dbtype == DBTypeEnum.dm) {
						boolean hasrowid= DmObjectsSqlUtil.hasRowIdTab(CsVarUtil.conn, schemaName, table);
						if(!hasrowid) {
							openObjType = OpenObjType.VIEW;
						}
					}
					OpenTableComp tablePanel = null;
					//判断是否可以打开新的tab
					boolean bool = true;
					for (String string : map.keySet()) {
						tablePanel = map.get(string);
						TableOpenBean obj = dataMap.get(string);
						if (table.equals(obj.getTableName()) && schemaName.equals(obj.getSchema())&&pTableName.equals(obj.getPartitionTableName())) {
							openId = string;
							bool = false;
							break;
						}
					}
					if (bool) { //打开新的
						if(openObjType == OpenObjType.TABLE) {
							//打开表
							tablePanel = new OpenTableComp(jdbcBean, table, tmpFile,false);
							tablePanel.loadData();
						}else if(openObjType == OpenObjType.PATITION){
							//打开子表
							tablePanel = new OpenTableComp(jdbcBean, table, tmpFile,true);
							tablePanel.loadSql(OpenTabSqlUtil.getOpenPartitionSql(dbtype, jdbcBean.getCurrentVersion(), schemaName, table,pTableName));
						}else if(openObjType == OpenObjType.VIEW) {
							//打开视图
							tablePanel = new OpenTableComp(jdbcBean, table, tmpFile,true);
							tablePanel.loadSql(OpenTabSqlUtil.getOpenViewSql(dbtype, jdbcBean.getCurrentVersion(), schemaName, table));
						}
						map.put(openId = RandomUtil.getUUID(), tablePanel);
						dataMap.put(openId, csGuiBean);
					}
					share2MainFrame(openId,schemaName,table,tablePanel);
					break;
				default:
					break;
			}
		}
		return null;
	}

	
	private static void share2MainFrame(String openId,String schemaName,String table,OpenTableComp tablePanel) {
		SharePaneObj obj = new SharePaneObj();
		obj.setId(openId);
		obj.setTitle(CsVarUtil.getDbtype() == DBTypeEnum.sqlite ? table : (schemaName + "." + table));
		obj.setCsMgrEnum(CsMgrEnum.TABLE_OPEN);
		obj.setFixTab(false);
		obj.setObj(tablePanel);
		CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
	}

}
