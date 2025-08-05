package com.hh.hhdb_admin.mgr.function2.debug.base;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.DebugSqlUtil;
import com.hh.frame.db_sql.DebugSqlUtil.DebugType;
import com.hh.frame.db_sql.conn.DebugConnUtil;
import com.hh.frame.db_sql.ora.OraSqlUtil;
import com.hh.frame.lg.Lg;
import com.hh.hhdb_admin.common.util.CsVarUtil;

public class DebugTool {

	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	private Connection connRun;
	private Connection connCrtl;

	private String portNum;
	private JdbcBean b;

	private String procID;
	private String sessionID;

	private DBTypeEnum type;
	private DBTypeEnum.Version ver;

	private Statement stmt;
	private boolean hasResult;
	private boolean isEnd;
	public static String P_END = "isEnd";

	private String warningStr;
	private Map<String, String> resultMap;

	private Exception error;
	private Thread t;
	private DebugType debugtype;

	public DebugTool(JdbcBean b, String procID) {
		this.b = b;
		this.procID = procID;
		this.type = CsVarUtil.getDbtype();
		this.ver = CsVarUtil.jdbc.getCurrentVersion();
		this.debugtype = DebugSqlUtil.getDebugType(type, ver);
	}

	public DebugType getDebugtype() {
		return debugtype;
	}

	public void init() throws SQLException {
		resultMap = null;
		warningStr = "";
		error=null;
		if (debugtype == DebugType.adp_debug) {
			SqlExeUtil.execute(connRun, DebugSqlUtil.getEnableDebugSql(type, ver));
		}
	}

	/**
	 * 初始化连接
	 * 
	 * @throws Exception
	 */
	public void initConn() throws Exception {

		if (connRun == null || this.connRun.isClosed()) {
			this.connRun = CsVarUtil.newConn(b);

		}
		if (connCrtl == null || this.connCrtl.isClosed()) {
			this.connCrtl = CsVarUtil.newConn(b);
		}

	}

	/**
	 * 启动debug
	 * 
	 * @param sql debug的sql
	 * @throws Exception
	 */
	public void runProc(String sql) throws Exception {
		String sql2 = DebugSqlUtil.getStartDebugeeSql(type, ver, procID);
		Lg.info(getClass(), "start_debugee sql=>" + sql2);
		if (debugtype == DebugType.adp_debug) {

			portNum = SqlQueryUtil.selectOneColumn(connRun, sql2).get(0);
			t = new Thread() {
				@Override
				public void run() {
					try {
						isEnd = false;
						SqlExeUtil.execute(connRun, sql);
						sessionID = null;
						warningStr=DebugConnUtil.getOutput(type, ver, connRun);
					} catch (Exception e) {
						SleepUtil.sleep100();
						if (connRun != null) {
							error = e;
							close();
						}
					} finally {
						isEnd = true;
						support.firePropertyChange(P_END, false, true);
					}
				}
			};

			t.start();
			Thread.sleep(500);
			Lg.debug(getClass(), "portNum:" + portNum);

			Map<String, String> map = SqlQueryUtil.selectOneStrMap(connCrtl,
					OraSqlUtil.getSessionSerialSql(portNum));
			sessionID = String.format("%s,%s", map.get("sid"), map.get("audsid"));
			Lg.debug(getClass(), "sessionID:" + sessionID);

			//enable dbms_output before start debugger
	        DebugConnUtil.setOutputEnabled(type, ver, connCrtl, true);
			DebugConnUtil.startDebugger(type, ver, connCrtl, portNum);

		} else if (debugtype == DebugType.pldbgapi) {
			List<String> list = SqlQueryUtil.selectOneColumn(connRun, sql2);
			String ret = list.get(0);
			if (ret.equals("0")) {
				Lg.debug(getClass(), "建立监听成功：" + ret);
			}
			stmt = connRun.createStatement();
			new Thread() {
				@Override
				public void run() {
					try {
						isEnd = false;
						hasResult = stmt.execute(sql);
						
						refreshWarning();
						resultMap = new LinkedHashMap<>();
						if (hasResult) {
							ResultSet rs = stmt.getResultSet();
							while (rs.next()) {
								ResultSetMetaData rsmd = rs.getMetaData();
								for (int i = 0; i < rsmd.getColumnCount(); i++) {
									resultMap.put(rsmd.getColumnLabel(i + 1), rs.getString(i + 1));
								}
							}
						}
						close();
					} catch (Exception e) {
						error = e;
					} finally {
						isEnd = true;
						support.firePropertyChange(P_END, false, true);
					}

				}
			}.start();
			Thread.sleep(500);
			SQLWarning warning = stmt.getWarnings();
			long time = System.currentTimeMillis();
			while (warning == null) {
				warning = stmt.getWarnings();
				SleepUtil.sleep100();
				if ((System.currentTimeMillis() - time) >= 5000) {
					throw new Exception("不能获取运行窗口");
				}
			}
			portNum = warning.getMessage().substring(11);
			Lg.debug(getClass(), "debugee_id:" + portNum);
			sessionID = DebugConnUtil.startDebugger(type, ver, connCrtl, portNum);
			Lg.debug(getClass(), "sessionID:" + sessionID);
		} else {
			throw new Exception("暂不支持");
		}
	};

	public Exception getError() {
		return error;
	}

	public boolean isEnd() {
		return isEnd;
	}

	
    public void refreshOutput() {
    	if (DebugSqlUtil.getDebugType(type, ver) == DebugType.adp_debug) {
	    	try {
	    		
	    		DebugConnUtil.setOutputEnabled(type, ver, connCrtl, true);
	    		String s = DebugConnUtil.getCurrentProc(type, ver, connCrtl);
	    		Lg.debug(getClass(), s);
	//    		warningStr=DebugConnUtil.getOutput(type, ver, connCrtl);
			} catch (Exception e1) {
				Lg.error(getClass(), e1);
			}
    	}
    }
	
 
    

    
    
    
	
	/**
	 * 下一步
	 * 
	 * @return 返回当前行号，-1为获取不到行数
	 * @throws Exception
	 */
	public int step() throws Exception {
		
		List<String> list = SqlQueryUtil.selectOneColumn(connCrtl, DebugSqlUtil.getStepOverSql(type, ver, sessionID));
		if (null == list || list.get(0) == null)
			return -1;
		return Integer.parseInt(list.get(0).trim());
	}

	/**
	 * 下一断点
	 * 
	 * @return 返回当前行号，-1为获取不到行数
	 * @throws SQLException
	 */
	public int continueGo() throws SQLException {
		List<String> list = SqlQueryUtil.selectOneColumn(connCrtl, DebugSqlUtil.getContineGoSql(type, ver, sessionID));
		if (null == list)
			return -1;
		return Integer.parseInt(list.get(0).trim());
	}

	/**
	 * 进入
	 * 
	 * @return
	 * @throws Exception
	 */
	public int stepInto() throws Exception {
		List<String> list = SqlQueryUtil.selectOneColumn(connCrtl, DebugSqlUtil.getStepIntoSql(type, ver, sessionID));
		int ret = -1;
		if (null == list || list.get(0) == null)
			ret = -1;

		ret = Integer.parseInt(list.get(0).trim());
		return ret;
	}

	/**
	 * 获取堆载信息
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, String>> getStackData() throws Exception {
		if (DebugSqlUtil.getDebugType(type, ver) == DebugType.adp_debug) {
			List<Map<String, String>> liMap = new LinkedList<>();
			List<String> vl = Arrays.asList(getStack().split(":"));
			for (int i = 1; i < vl.size(); i++) {
				Map<String, String> map = new LinkedHashMap<>();
				String ss = vl.get(i).split(" {2}")[0].trim();
				String[] list = ss.split(",");
				String[] name = list[0].split("=");

				map.put(ColNameCons.NAME, name.length > 1 ? name[1].trim() : "");
				map.put(ColNameCons.TYPE, list[1].split("=")[1].trim());
				map.put(ColNameCons.ROW, list[2].split("=")[1].trim());
				map.put(ColNameCons.VALUE, "");
				liMap.add(map);
			}
			return liMap;
		} else if (DebugSqlUtil.getDebugType(type, ver) == DebugType.pldbgapi) {
			List<Map<String, String>> li = new LinkedList<>();
			li = SqlQueryUtil.selectStrMapList(connCrtl, DebugSqlUtil.getStackSql(type, ver, sessionID));
			Collections.reverse(li);
			return li;
		}

		return null;

	}

	/**
	 * 堆载信息
	 * 
	 * @return
	 * @throws Exception
	 */
	private String getStack() throws Exception {
		List<String> list = SqlQueryUtil.selectOneColumn(connCrtl, DebugSqlUtil.getStackSql(type, ver, sessionID));
		Lg.debug(getClass(), "stack->" + list.get(0));
		if (null == list || list.get(0) == null) {
			return "";
		} else {
			return list.get(0);
		}
	}

	/**
	 * 获取变量值
	 * 
	 * @param name 变量名称
	 * @return
	 * @throws Exception
	 */
	public String getVarValue(String name) throws Exception {
		if(connCrtl==null) {
			return "";
		}
		List<String> l = SqlQueryUtil.selectOneColumn(connCrtl,
				DebugSqlUtil.getVarValueSql(type, ver, name, sessionID));
		if (l.isEmpty()) {
			return null;
		}
		return l.get(0);
	}

	/**
	 * 获取所有的断点Map
	 * 
	 * @return
	 * @throws Exception
	 */
	public LinkedHashMap<String, Map<String, String>> getBreakPointsMap() throws Exception {
		return DebugConnUtil.getBreakPointsMap(type, ver, connCrtl, sessionID);

	}

	public String getSource(String objId,String schema,String name,String ctype) throws Exception {
		String ret = DebugConnUtil.getSource(type, ver, connCrtl, sessionID, objId,schema,name,ctype);
		Lg.debug(getClass(), "getSource==>" + ret);
		return ret;
	}

	/**
	 * adp_debug abort
	 * 
	 * @throws Exception
	 */
	public void abort() throws Exception {
		String ret = DebugConnUtil.abortDebug(type, ver, connCrtl, sessionID);
		Lg.debug(getClass(), "abort==>" + ret);
	}

	/**
	 * 关闭连接
	 */
	public void close() {

		if (t != null) {
			t.interrupt();
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				Lg.error(getClass(), e);
			}
		}
		stmt = null;
		if (connCrtl != null) {
			ConnUtil.close(connCrtl);
			connCrtl = null;
		}
		if (connRun != null) {
			ConnUtil.close(connRun);
			connRun = null;
		}

		killSession();

	}
	
	
	
	
	public void killSession() {
		// oracle如果调试暂停或者调试有问题，挂起的连接杀不掉
		if (debugtype == DebugType.adp_debug) {
			if (sessionID != null) {
				SessionCtrlMgr.addKillSession(sessionID);
			}
		}
	}

	/**
	 * 设置断点
	 * 
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	public List<Integer> setBreakPoints(List<Integer> lines) throws Exception {
		return setBreakPoints(lines, procID);
	}

	/**
	 * 设置断点
	 * 
	 * @param lines
	 * @return
	 * @throws Exception
	 */
	public List<Integer> setBreakPoints(List<Integer> lines, String procID) throws Exception {
		List<Integer> errorList = new ArrayList<>();
		for (Integer line : lines) {
			String info = SqlQueryUtil
					.selectOneColumn(connCrtl, DebugSqlUtil.setBreakPointsSql(type, ver, line, sessionID, procID))
					.get(0);
			if (info.contains("set_breakpoint:"))
				errorList.add(line);
			Lg.debug(getClass(), "error breakpoint:" + errorList);
		}
		return errorList;

	}

	/**
	 * 删除一个断点
	 * 
	 * @param pointId 断点ID
	 * @throws SQLException
	 */
	public void rmBreakPoint(String pointId, Integer line) throws SQLException {
		String info = SqlQueryUtil
				.selectOneColumn(connCrtl, DebugSqlUtil.rmBreakPointsSql(type, ver, pointId, line, sessionID)).get(0);
		Lg.debug(getClass(), info);
	}

	public String stepIntoStr() throws Exception {
		String ret = DebugConnUtil.stepIntoStr(type, ver, connCrtl, sessionID);
		Lg.debug(getClass(), "stepIntoStr==>" + ret);
		return ret;
	}

	/**
	 * 获取stmt中的提示信息
	 * 
	 * @return
	 */
	private void refreshWarning() {
		try {
			if (stmt == null || stmt.isClosed()) {
				return;
			}
			SQLWarning warn = stmt.getWarnings();
			StringBuffer sb = new StringBuffer();
			if (warn != null)
				warn.forEach(a -> sb.append(a.getMessage() + "\n"));
			warningStr = sb.toString();
		} catch (Exception e) {
			Lg.error(getClass(), e);
		}
	}

	public String getWarningStr() {
		refreshWarning();
		return warningStr;
	}

	public Map<String, String> getResultMap() {
		return resultMap;
	}

	public List<Integer> setBreakPoints(List<Integer> lines, String name, String owner, String ptype, String ns)
			throws Exception {
		List<Integer> errorList = new ArrayList<>();
		for (Integer line : lines) {
			CallableStatement sta = null;
			try {
				String sql = DebugSqlUtil.setBreakPointsSql2(type, ver, line, name, owner, ptype, ns);
				Lg.debug(getClass(), "setBreakPoints sql=>" + sql);
				sta = connCrtl.prepareCall(sql);
				sta.registerOutParameter(1, java.sql.Types.VARCHAR);
				sta.execute();
				String info = sta.getString(1);
				Lg.debug(getClass(), info);
				if (info.contains("set_breakpoint:")) {
					errorList.add(line);
					Lg.debug(getClass(), "error breakpoint:" + errorList);
				}
			} catch (Exception e) {
				Lg.error(getClass(), e);
			} finally {
				if (sta != null) {
					sta.close();
				}
			}

			
		}
		return errorList;
	}

	public void addProperyListenner(PropertyChangeListener lister) {
		support.addPropertyChangeListener(lister);
	}

}
