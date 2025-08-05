package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.db_sql.TableSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;



public class TruncateHandler extends AbsHandler {

    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        if (PopDlgUtil.confirm(CsVarUtil.parentFrame, LangMgr.lang(Word4.SURE_EMPTY))) {
        	String schema=getSchemaName();
        	String tabName=getTableName();
			DBTypeEnum dbtype = CsVarUtil.getDbtype();
			DBTypeEnum.Version ver = CsVarUtil.jdbc.getCurrentVersion();
			boolean suc=false;
			if (dbtype == DBTypeEnum.mongodb) {
				long count = new MongoColl(CsVarUtil.conn, schemaName).clean(tabName);
				if(count>0) {
					PopDlgUtil.info("清空对象:"+count);
				}else {
					PopDlgUtil.info("清空对象失败");
				}
				CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(treeNode.getId()));
				return;
			} else {
				String sql = TableSqlUtil.getTrunTabSql(dbtype, ver, schema, tabName);
				suc = GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, sql);
			}
        	if(suc) {
        		PopDlgUtil.info("清空表格:"+tabName+"成功");
        	}
        }
    }

}
