package com.hh.hhdb_admin.mgr.sequence.common;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.mgr.sequence.comp.Db2SeqComp;
import com.hh.hhdb_admin.mgr.sequence.comp.DmSeqComp;
import com.hh.hhdb_admin.mgr.sequence.comp.HHdbSeqComp;
import com.hh.hhdb_admin.mgr.sequence.comp.OraSeqComp;
import com.hh.hhdb_admin.mgr.sequence.comp.SqlServerSeqComp;

public class SeqCompUtil {
	
	/**
	 * 根据jdbc获取不同的序列界面
	 * @author HuBingBing
	 * @date 2020年12月22日上午10:26:06
	 * @param type
	 * @param schema
	 * @param seq
	 * @param flag
	 * @return
	 * AbsSeqComp
	 *
	 */
	public static AbsSeqComp getSeqBaseComp(DBTypeEnum type,String schema,String seq, boolean flag) {
		switch(type) {
		case hhdb:
		case pgsql:
			return new HHdbSeqComp(type,schema,seq,flag);
		case oracle:
			return new OraSeqComp(schema,seq,flag);
		case sqlserver:
			return new SqlServerSeqComp(schema,seq,flag);
		case db2:
			return new Db2SeqComp(schema,seq,flag);
		case dm:
			return new DmSeqComp(schema,seq,flag);
		default:
			return null;
		}
	} 
	
	/**
	 * 根据不同数据库设置合适界面大小
	 * @author HuBingBing
	 * @date 2020年12月22日上午10:38:29
	 * @param diaLog
	 * @param type
	 * void
	 *
	 */
	public static void setDiaLogSize(HDialog diaLog,DBTypeEnum type) {
		switch(type) {
		case hhdb:
		case pgsql:
			diaLog.setSize(500, 600);
			break;
		case oracle:
		case sqlserver:

		default:
			diaLog.setSize(500, 500);
		}
	}
		
}
