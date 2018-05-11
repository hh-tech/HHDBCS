package com.hhdb.csadmin.plugin.sequence.utils;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.dbobj.bean.SeqBean;
import com.hh.frame.dbobj.hhdb.HHdbSchema;
import com.hh.frame.dbobj.hhdb.HHdbSeq;
import com.hh.frame.dbobj.hhdb.HHdbTable;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.sequence.SequenceManager;

public class SequenceDao {
	private SequenceManager sequenceManager;
	public SequenceDao(SequenceManager manager){
		sequenceManager=manager;
	}
   
	/*
	 * 获取当前模式下所有的表名
	*/ 
	public Set<String>  getTables(String schemaName){
		Set<String> tableNames=new HashSet<>();
		try {
			  HHdbSchema schema=new HHdbSchema(getConn(), schemaName, true,StartUtil.prefix);
		      tableNames = schema.getTableNameSet();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
		}
		
		  return  tableNames;
	}
	/*
	 * 根据表名获得列名
	*/
	public List<String>  getColumnList(String schemaName,String tableName){
		List<String> columnNameList=new ArrayList<>();
		try {
			HHdbTable table=new HHdbTable(getConn(), schemaName, tableName, true,StartUtil.prefix);
		    columnNameList = table.getColumnNameList();		
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
			
		}
		  return  columnNameList;
	}
	
	/*
	 * 
	 * 保存序列
	 */
	public  Boolean  saveSeq(String sql){
		try {
			 SqlExeUtil.executeUpdate(getConn(), sql);
			 JOptionPane.showMessageDialog(null,"新增序列成功");
			return true;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"新增序列失败！", "错误",JOptionPane.ERROR_MESSAGE);

			return false;
		}
		
	}
	
	/*
	 * 根据模式名和序列名查询序列信息
	*/
	public SeqBean getSequenceInfo(String schemaName,String seqName){
		SeqBean seqBean =null;
		try {
			HHdbSeq seq=new HHdbSeq(getConn(), schemaName, seqName, true,StartUtil.prefix);
			 seqBean = seq.getSeqBean();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
	
		}
		  return  seqBean;
	}
	//查询序列描述
	public String getDescription(String seqName,String schemaName){
		String comment="";
		try {
			HHdbSeq seq=new HHdbSeq(getConn(), schemaName, seqName, true,StartUtil.prefix);
			 comment = seq.getComment();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
	
		}
		  return  comment;
	}
	
	
	/*
	 *  查询 序列引用表和列的名字
	*/
	public Map<String, String>  getSeqTnameandCname(String schemaName, String seqName){
		Map<String, String>  seqTnameandCname =new HashMap<String, String>();
		try {
			HHdbSeq seq=new HHdbSeq(getConn(), schemaName, seqName, true,StartUtil.prefix);
			seqTnameandCname = seq.getSeqTnameandCname();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage()+"！", "提示",JOptionPane.ERROR_MESSAGE);
			
		}
		  return  seqTnameandCname;
	}
	
	public boolean updateSeq(String designsql,String selectsql){
		boolean flag=true;
		try {
			if(!designsql.equals("")){
				SqlExeUtil.executeUpdate(getConn(), designsql);
			}
			if(!selectsql.equals("")){
				 dataSelect1(selectsql);
			}
			JOptionPane.showMessageDialog(null,"序列已更新");
		} catch (Exception e) {
			flag=false;
			JOptionPane.showMessageDialog(null, e.getMessage()+"序列更新失败！", "错误",JOptionPane.ERROR_MESSAGE);
		}
		return flag;
	}
	/***
	 * 查询单不需要返回值
	 * @param sql
	 * @return
	 */
	public String dataSelect1(String sql){
		CmdEvent getcfEvent = new CmdEvent(sequenceManager.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "ExecuteQueryDQLBySqlEvent");
		getcfEvent.addProp("sql_str", sql);
		HHEvent ev = sequenceManager.sendEvent(getcfEvent);
		if(ev instanceof ErrorEvent){
			throw new RuntimeException(((ErrorEvent) ev).getErrorMessage());
		}
		return ev.getValue("res");
	}
	
	/**
	 * 发送事件获得连接
	 */
	private Connection getConn(){
		CmdEvent cmd=new CmdEvent(sequenceManager.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetConn");
		HHEvent replyE = sequenceManager.sendEvent(cmd);
		return (Connection) replyE.getObj();
	}
}
