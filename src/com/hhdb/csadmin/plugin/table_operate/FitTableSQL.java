package com.hhdb.csadmin.plugin.table_operate;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.dbobj.hhdb.HHdbTable;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleCheckPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleCommentPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleForeignPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleIndexPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleRulePanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleTablePanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleTriggerPanel;
import com.hhdb.csadmin.plugin.table_operate.handle.HandleUniquePanel;

/**
 * 
 * @Description: 创建表SQL 预览
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class FitTableSQL extends JPanel  {
	private static final long serialVersionUID = 1L;
	private TableEditPanel tbp;
	
	private Connection conn = null;
	private HandleTablePanel tablePanel;
	private HandleIndexPanel indexPanel;
	private HandleUniquePanel uniquePanel;
	private HandleCheckPanel checkPanel;
	private HandleRulePanel rulePanel;
	private HandleForeignPanel foreignPanel;
	private HandleTriggerPanel triggerPanel;
	private HandleCommentPanel commentPanel;
	
	private QueryTextPane queryTextPane;

	public FitTableSQL(TableEditPanel tableeditpanel) {
		setLayout(new BorderLayout());
		queryTextPane = new QueryTextPane();
		add(queryTextPane);
		this.tbp = tableeditpanel;
	}

	/**
	 * 组装设计表格数据为sql语句
	 * 
	 * @param row
	 * @param col
	 */
	protected String tableEditChanged() {
		// 取消表格编辑状态
		tablePanel.cancleEdit();
		indexPanel.cancleEdit();
		foreignPanel.cancleEdit();
		uniquePanel.cancleEdit();
		checkPanel.cancleEdit();
		rulePanel.cancleEdit();
		triggerPanel.cancleEdit();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(tablePanel.editTableSql());
		sqlBuffer.append(indexPanel.editIndexSql());
		sqlBuffer.append(foreignPanel.editForeignSql());
		sqlBuffer.append(uniquePanel.editUniqueSql());
		sqlBuffer.append(checkPanel.editCheckSql());
		sqlBuffer.append(rulePanel.editRuleSql());
		sqlBuffer.append(triggerPanel.editTriggerSql());
		sqlBuffer.append(commentPanel.createCommentSql());
		return sqlBuffer.toString();
	}

	/**
	 * 保存表格
	 * 
	 * @return
	 * @throws Exception
	 */
	public void saveTable(HandleTablePanel tablePanel,TableEditPanel tep) throws Exception {
		//终止对表格的编辑固定值
		tablePanel.getBaseTable().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		String tableSql;
		if(null != tbp.getTableName() && !tbp.getTableName().equals("")){     //修改
			tableSql = tableEditChanged();
		}else{    //新建
			tbp.setTableName((String) JOptionPane.showInputDialog(tablePanel, "输入表名", "表名", JOptionPane.PLAIN_MESSAGE));
			if ( null != tbp.getTableName() && !tbp.getTableName().equals("")) {
				tableSql = tableEditChanged();
			}else{
				return;
			}
		}
		if(null != tableSql && !tableSql.equals("")){
			try {
				if(conn == null || conn.isClosed()){
					conn = ConnService.createConnection(tep.sqls.getServerBean());
					conn.setAutoCommit(false);   //设置为手动提交
				}
				String[] sqls = tableSql.split(";");
				for (String sql : sqls) {
					if (sql.trim().length() > 0) {
						SqlExeUtil.executeUpdate(conn, sql);
					}
				}
				if (!conn.isClosed()) {
					conn.commit();   //提交
					if(tbp.getSign()){
						JOptionPane.showMessageDialog(tablePanel, "新增成功");
						tbp.sqls.refresh(tbp.getSchemaName());
						//获取新建表id
						HHdbTable htable = new HHdbTable(conn, tbp.getSchemaName(), tbp.getTableName(), false, "hh");
						//打开修改页面
						TableEditPanel tab = new TableEditPanel(tbp.tbe,tbp.getSchemaName(),tbp.getTableName(),htable.getOid(),false);
						JPanel jp = new JPanel();
						jp.setLayout(new GridBagLayout());
						jp.add(tab,new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
						String id = tbp.getTableName()+tbp.getSchemaName() + "edit";
						tab.sqls.getTabPanelTable(id,"设计表("+tbp.getTableName()+" "+tbp.getSchemaName()+")", jp);
						tbp.tbe.map.put(id, jp);
						//关闭新增页面
						tbp.sqls.closePane("null" + tbp.getSchemaName() + "create");
					}else{
						JOptionPane.showMessageDialog(tablePanel, "修改成功");
						tbp.tbe.refresh(tbp.getTableName(),tbp.getSchemaName(),tbp.getTableoId(),"edit",false);
					}
				}
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(tablePanel, e.getMessage(), "提示",JOptionPane.ERROR_MESSAGE);
				if (!conn.isClosed()) {
					try {
						conn.rollback();  //执行失败，回滚
					} catch (Exception e2) {
						LM.error(LM.Model.CS.name(), e2);
					}
				}
			}finally{
				ConnService.closeConn(conn);  //关闭连接
			}
		}
	}

	protected void sqlChange() {
		queryTextPane.setText(tableEditChanged());
	}

	public HandleTablePanel getTablePanel() {
		return tablePanel;
	}

	public void setTablePanel(HandleTablePanel tablePanel) {
		this.tablePanel = tablePanel;
	}

	public HandleIndexPanel getIndexPanel() {
		return indexPanel;
	}

	public void setIndexPanel(HandleIndexPanel indexPanel) {
		this.indexPanel = indexPanel;
	}

	public HandleUniquePanel getUniquePanel() {
		return uniquePanel;
	}

	public void setUniquePanel(HandleUniquePanel uniquePanel) {
		this.uniquePanel = uniquePanel;
	}

	public HandleCheckPanel getCheckPanel() {
		return checkPanel;
	}

	public void setCheckPanel(HandleCheckPanel checkPanel) {
		this.checkPanel = checkPanel;
	}

	public HandleRulePanel getRulePanel() {
		return rulePanel;
	}

	public void setRulePanel(HandleRulePanel rulePanel) {
		this.rulePanel = rulePanel;
	}

	public HandleTriggerPanel getTriggerPanel() {
		return triggerPanel;
	}

	public void setTriggerPanel(HandleTriggerPanel triggerPanel) {
		this.triggerPanel = triggerPanel;
	}

	public HandleForeignPanel getForeignPanel() {
		return foreignPanel;
	}

	public void setForeignPanel(HandleForeignPanel foreignPanel) {
		this.foreignPanel = foreignPanel;
	}

	public HandleCommentPanel getCommentPanel() {
		return commentPanel;
	}

	public void setCommentPanel(HandleCommentPanel commentPanel) {
		this.commentPanel = commentPanel;
	}

}
