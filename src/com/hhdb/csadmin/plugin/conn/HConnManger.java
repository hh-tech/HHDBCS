package com.hhdb.csadmin.plugin.conn;

import java.awt.Component;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.CSVUtil;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.conn.dao.ConnFactory;

public class HConnManger extends AbstractPlugin {

	static final String PLUGIN_ID = HConnManger.class.getPackage().getName();
	private ConnFactory connFactory = null;

	public HConnManger() {
		connFactory = new ConnFactory();
	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE = EventUtil.getReplyEvent(HConnManger.class, event);
		if (event.getType().equals(EventTypeEnum.CMD.name())) {
			CmdEvent cmdevent = (CmdEvent) event;
			if (cmdevent.getCmd().equals("ExecuteCSVBySqlEvent")) {
				String sql = event.getValue("sql_str");
				try {
					List<List<Object>> list = SqlQueryUtil.selectList(
							connFactory.getConnection(), sql);
					replyE.addProp("csv", CSVUtil.List2CSV(list));
					return replyE;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage("异常:" + e.getMessage());
					return errorEvent;
				}
			} 
			else if (cmdevent.getCmd().equals("ExecuteListBySqlEvent")) {
				String sql = event.getValue("sql_str");
				try {
					List<List<Object>> list = SqlQueryUtil.selectList(
							connFactory.getConnection(), sql);
					replyE.setObj(list);;
					return replyE;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage("异常:" + e.getMessage());
					return errorEvent;
				}
			}
			else if (cmdevent.getCmd().equals("ExecuteListMapBySqlEvent")) {
				String sql = event.getValue("sql_str");
				try {
					List<Map<String, Object>> list = SqlQueryUtil.select(
							connFactory.getConnection(), sql);
					replyE.setObj(list);;
					return replyE;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage("异常:" + e.getMessage());
					return errorEvent;
				}
			}
			else if (cmdevent.getCmd().equals("ExecuteUpdateBySqlEvent")) {
				String sql = event.getValue("sql_str");
				try {
					int res = SqlExeUtil.executeUpdate(
							connFactory.getConnection(), sql);
					replyE.addProp("res", res + "");
					return replyE;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage("异常:" + e.getMessage());
					return errorEvent;
				}
			} else if (cmdevent.getCmd().equals("ExecuteQueryDQLBySqlEvent")) {
				String sql = event.getValue("sql_str");
				PreparedStatement ps = null;
				try {
					ps = connFactory.getConnection().prepareStatement(sql);
					ps.execute();
					replyE.addProp("res", "true");
					return replyE;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage("异常:" + e.getMessage());
					return errorEvent;
				} finally {
					try {
						if (ps != null) {
							ps.close();
						}
					} catch (Exception e) {
						LM.error(LM.Model.CS.name(), e);
					}
				}
			} else if (cmdevent.getCmd().equals("ObtainDataDetails")) {   //查询出来的数据包括字段名、字段类、字段数据
				String sql = event.getValue("sql_str");
				try {
					List<List<Object>> list = SqlQueryUtil.selectColumnAndDataList(connFactory.getConnection(), sql);
					replyE.setObj(list);;
					return replyE;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage("异常:" + e.getMessage());
					return errorEvent;
				}
			} else if (cmdevent.getCmd().equals("GetServerBean")) {
				replyE.setObj(connFactory.getServerBean().clone());
				return replyE;
			} else if (cmdevent.getCmd().equals("setSuperuser")) {
				String value = cmdevent.getValue("superuser_value");
				if (value.equals("true")) {
					connFactory.superuser = true;
				} else {
					connFactory.superuser = false;
				}
				return replyE;
			} else if (cmdevent.getCmd().equals("GetConn")) {
				try {
					replyE.setObj(connFactory.getConnection());
					return replyE;
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage(e.toString());
					return errorEvent;
				}
				
			}  
			else if (cmdevent.getCmd().equals("getSuperuser")) {
				String value = "false";
				if (connFactory.superuser) {
					value = "true";
				}
				replyE.addProp("superuser_value", value);
				return replyE;
			} else if (cmdevent.getCmd().equals("SetConn")) {
				String host_str = event.getValue("host_str");
				String port_str = event.getValue("port_str");
				String dbname_str = event.getValue("dbname_str");
				String username_str = event.getValue("username_str");
				String pass_str = event.getValue("pass_str");
				String superuser_value = event.getValue("superuser_value");
				connFactory.getServerBean().setHost(host_str);
				connFactory.getServerBean().setPort(port_str);
				connFactory.getServerBean().setDBName(dbname_str);
				connFactory.getServerBean().setUserName(username_str);
				connFactory.getServerBean().setPassword(pass_str);
				if (superuser_value.equals("true")) {
					connFactory.superuser = true;
				} else {
					connFactory.superuser = false;
				}
				connFactory.closeConn();
			}
			return replyE;
		} else {
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
					event.getFromID(),
					ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"
					+ event.toString());
			return errorEvent;
		}
	}

	@Override
	public Component getComponent() {
		return null;
	}

}
