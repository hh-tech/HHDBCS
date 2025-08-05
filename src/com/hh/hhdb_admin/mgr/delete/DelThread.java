package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.DropSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.mongo.obj.MongoDb;
import com.hh.frame.redis.base.RedisAbsKeyObj;
import com.hh.frame.redis.base.RedisObjType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseRefreshBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.RedisRmPreLoadKeysBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
public class DelThread implements Runnable {

	private final JdbcBean jdbc;
	private final DeleteComp deleteComp;
	private final DeleteBean deleteBean;

	public DelThread(DeleteBean deleteBean, JdbcBean jdbc, DeleteComp deleteComp) {
		this.jdbc = jdbc;
		this.deleteComp = deleteComp;
		this.deleteBean=deleteBean;
	}

	@Override
	public void run() {
		DBTypeEnum dbtype = DriverUtil.getDbType(jdbc);
		DBTypeEnum.Version ver = jdbc.getCurrentVersion();
		List<String> nodeInfoList = deleteBean.getObjNames();
		for (int i = 0; i < nodeInfoList.size(); i++) {
			String name=nodeInfoList.get(i);
			if (deleteComp.isStop) {
				break;
			}
			String res = LangMgr.lang(Word2.SUCCESSFULLY);
			try {
				if (dbtype == DBTypeEnum.mongodb)
					delMongo(deleteComp.conn, deleteBean);
				else if (dbtype == DBTypeEnum.redis)
					delRedis(deleteComp.conn, deleteBean);
				else
					delRdbmsObj(dbtype, ver,deleteComp.stat, deleteBean.getNodeType(),deleteBean.getSchema(),deleteBean.getTableName(),name);
			} catch (Exception e) {
				Lg.error(getClass(), e);
				res = LangMgr.lang(Word2.FAILED) + "：" + e.getMessage();
			}

			for (Map<String, String> datum : deleteComp.data) {
				if (datum.get("name").equals(name)) {
					datum.put("res", res);
				}
			}
			deleteComp.loadData();
			deleteComp.progressBarInput.setValue(i + 1);
			if (i == nodeInfoList.size() - 1) {
				deleteComp.isStop = true;
				deleteComp.stopBtn.setEnabled(false);
			}
		}
		BaseRefreshBean bean = null;
		if(dbtype==DBTypeEnum.redis) {
			RedisRmPreLoadKeysBean  rBean=new RedisRmPreLoadKeysBean(deleteBean.getRefreshNodeId());
			for(String name:nodeInfoList)
				rBean.addKey(name);
			bean=rBean;
		}else {
			bean=new RefreshNodeBean(deleteBean.getRefreshNodeId());
		}
		CsVarUtil.eng2.doCall(CsMgrEnum.TREE, bean);
		ConnUtil.close(deleteComp.conn);
	}


	private void delMongo(Connection conn, DeleteBean nodeInfo) throws Exception {
		for(String name:nodeInfo.getObjNames()) {
			String schemaName = nodeInfo.getSchema();
			if (nodeInfo.getNodeType() == TreeMrType.ROOT_SCHEMA_OR_DB) {
				new MongoDb(conn).drop(name);
			} else if (nodeInfo.getNodeType() == TreeMrType.TABLE || nodeInfo.getNodeType() == TreeMrType.VIEW) {
				new MongoColl(conn, schemaName).dropColl(name);
			} else if (nodeInfo.getNodeType() == TreeMrType.COLUMN) {
				new MongoColl(conn, schemaName).delete(nodeInfo.getTableName(),name);
			} else if (nodeInfo.getNodeType() == TreeMrType.INDEX) {
				new MongoColl(conn, schemaName).dropIndex(nodeInfo.getTableName(),name);
			}
		}
	}

	private void delRedis(Connection conn, DeleteBean nodeInfo) throws Exception {
		for(String name:nodeInfo.getObjNames()) {
			RedisAbsKeyObj redisAbsKeyObj=new RedisAbsKeyObj(conn, name) {
				@Override
				public RedisObjType getType() {
					return null;
				}

				@Override
				public String getAddCmd() {
					// TODO Auto-generated method stub
					return null;
				}
			};
			redisAbsKeyObj.delete();
		}
	}

	private void delRdbmsObj(DBTypeEnum dbtype,Version ver, Statement stat, TreeMrType type,String schema, String tabName, String objName) throws Exception {
//		String sql = null;
//		if(dbtype==DBTypeEnum.oracle && type==TreeMrType.DBLINK ) {
//			String[] a=StringUtils.split(objName, ".");
//			schema=a[0];
//			objName=a[1];
//		}
//		
//		if((DBTypeEnum.isPgSeries(dbtype, ver)||dbtype==DBTypeEnum.db2|| DBTypeEnum.isInformixSeries(dbtype, ver))
//				&& (type==TreeMrType.FUNCTION || type==TreeMrType.PROCEDURE) ) {
//			//由于pg和db2删除函数的时候需要指定参数所以不能把带参数的函数名称直接添加双引号
//		} else if(DBTypeEnum.isMysqlSeries(dbtype, ver) && type==TreeMrType.USER  ){
//			//skip
//		}else{
//			objName = SqlStrUtil.addQuote4DDL(objName, dbtype, ver);
//		}
//		
//		if(dbtype==DBTypeEnum.db2 &&type==TreeMrType.SCHEMA)
//			schema=objName;
//			
//		schema = SqlStrUtil.addQuote4DDL(schema, dbtype, ver);
//		tabName = SqlStrUtil.addQuote4DDL(tabName, dbtype, ver);
		String sql =DropSqlUtil.getDropSql(dbtype, ver, type, schema, tabName, objName);
		if (sql == null)
			throw new Exception(LangMgr.sLang(CsL.DB_NOT_SUPPORTED));
		for (String s : sql.split(";")) {
			Lg.debug(getClass(), s);
			stat.execute(s);
		}
	}


}
