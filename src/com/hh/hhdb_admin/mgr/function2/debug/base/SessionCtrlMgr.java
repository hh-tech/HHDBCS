package com.hh.hhdb_admin.mgr.function2.debug.base;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.hh.frame.common.base.BaseJob;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.InstanceSqlUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

public class SessionCtrlMgr extends BaseJob{
	
	private static Map<String,Long> sessions = new HashMap<>();
	
	
	public static void addKillSession(String s) {
		sessions.put(s,System.currentTimeMillis());
	}
	
	
	static{
		
		Thread t = new Thread() {
			public void run() {
				if(CsVarUtil.getDbtype()!=DBTypeEnum.oracle) {
					return;
				}
				List<String> list = new ArrayList<String>();
				Long current = System.currentTimeMillis();
				for(Entry<String, Long> s:sessions.entrySet()) {
					if(current - s.getValue()>10000) {
						list.add(s.getKey());
					}
				}
				for(String s:list) {
					String sql=InstanceSqlUtil.getEndSessionSql(DBTypeEnum.oracle, null, s);
					String sql2=InstanceSqlUtil.getSessionInfo(DBTypeEnum.oracle, null, s);
					try {
						boolean exist = SqlQueryUtil.existData(CsVarUtil.conn, sql2);
						if(exist) {
							SqlExeUtil.execute(CsVarUtil.conn, sql);
						}
						sessions.remove(s);
					} catch (SQLException e1) {
						if(e1.getMessage()!=null&&
								(e1.getMessage().contains("ORA-00030")||e1.getMessage().contains("ORA-01031"))) {
							sessions.remove(s);
							
						}
					}
				}
			};
		};
		
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleWithFixedDelay(t, 0, 5, TimeUnit.SECONDS);
	}

	

	
	
}
