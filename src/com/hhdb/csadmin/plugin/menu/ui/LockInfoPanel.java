package com.hhdb.csadmin.plugin.menu.ui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;
import com.hhdb.csadmin.common.util.CSVUtil;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.menu.Hmenu;
import com.hhdb.csadmin.plugin.menu.util.VectorUtil;

/**
 * 数据库锁信息页面
 * @author gd
 *
 */
public class LockInfoPanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 9212763351052106338L;
	private JButton reflashbtn = null, endbtn = null;
	private Hmenu hmenu;

	private TablePanelUtil tablePanel;
	private BaseTable baseTable=null;// 表格
	public LockInfoPanel(Hmenu hmenu, String sertableId) {
		this.hmenu=hmenu;
		setLayout(new GridBagLayout());
		JToolBar jToolBar=new JToolBar();
		reflashbtn = new JButton("刷新", new ImageIcon(
				LockInfoPanel.class.getResource("/icon/reflash.png")));
		endbtn = new JButton("结束进程", new ImageIcon(
				LockInfoPanel.class.getResource("/icon/end.png")));
		jToolBar.add(reflashbtn);
		jToolBar.add(endbtn);
		reflashbtn.addActionListener(this);
		endbtn.addActionListener(this);
		tablePanel=new TablePanelUtil(false);
		baseTable=tablePanel.getBaseTable();
		baseTable.getTableHeader().setReorderingAllowed(false);
		loadTable();
		add(jToolBar, new GridBagConstraints(0, 0, 1, 1,1.0,0.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		add(tablePanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	/**
	 * 加载服务器状态列表
	 */
	public void loadTable() {
		baseTable.removeAll();
		String sql = "select distinct c.pid PID,c.datname 数据库,c.usename 用户,c.client_addr 客户端地址,a.locktype 锁类型,a.mode 模式,c.query_start 开始时间,c.query 执行sql from "+StartUtil.prefix+"_locks a join "+StartUtil.prefix+"_class b on a.relation = b.oid left join "+StartUtil.prefix+"_stat_activity c on a.pid=c.pid;";
		String csv = hmenu.sendToConn(sql);
		List<List<String>> data = null;
		try {
			data = CSVUtil.cSV2List(csv);
		} catch (IOException e) {
			LM.error(LM.Model.CS.name(), e);
			return;
		}
		Vector<Object> data0=VectorUtil.initVectorData(data);
		Vector<Object> colNames=VectorUtil.initVector(data.get(0));
		baseTable.setModel(new DefaultTableModel(data0, colNames));
		setColumnWidth(new int[] {0, 1,2,3,4,5,6,7});
	}
	/**
	 * 监听
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource().equals(reflashbtn)) {
			loadTable();
		} else if (e.getSource().equals(endbtn)) {
			int result=JOptionPane.showConfirmDialog(this, "确定要kill掉此连接?","提示信息",JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION){
				int row=baseTable.getSelectedRow();
				if(row==-1){
					return;
				}
				int pid=Integer.parseInt(baseTable.getValueAt(row, 0).toString());
				if(pid!=0){
					String sql= "SELECT "+StartUtil.prefix+"_terminate_backend('"+pid+"')";
					String fromID = Hmenu.class.getPackage().getName();
					String toID = "com.hhdb.csadmin.plugin.conn";
					CmdEvent executeEvent = new CmdEvent(fromID, toID, "ExecuteCSVBySqlEvent");
					executeEvent.addProp("sql_str", sql);
					hmenu.sendEvent(executeEvent);
				}
				loadTable();
			
			}
		}
	}
	
	
	/**
	 * 建表
	 * @param serpanel 
	 * @param columnList
	 * @param rowlist
	 * @param sertableId
	 */
	public JScrollPane sendToCreateTable() {
		String sql = "select distinct c.pid PID,c.datname 数据库,c.usename 用户,c.client_addr 客户端地址,a.locktype 锁类型,a.mode 模式,c.query_start 开始时间,c.query 执行sql from "+StartUtil.prefix+"_locks a join "+StartUtil.prefix+"_class b on a.relation = b.oid left join "+StartUtil.prefix+"_stat_activity c on a.pid=c.pid;";
		String csv = hmenu.sendToConn(sql);
//		List<String> columnList = hmenu.parseCsvColumn(csv);
//		List<Map<String, Object>> rowlist = hmenu.parseCsvRows(csv, columnList);
//		Vector<Object> rowData = new Vector<Object>();
//		Vector<Object> columnNames = new Vector<Object>();
//		for(String column:columnList){
//			columnNames.add(column);
//		}
//		rowData=VectorUtil.initVector(rowlist, columnList);
		List<List<String>> data = null;
		try {
			data = CSVUtil.cSV2List(csv);
		} catch (IOException e) {
			LM.error(LM.Model.CS.name(), e);
			return null;
		}
		Vector<Object> data0=VectorUtil.initVectorData(data);
		Vector<Object> colNames=VectorUtil.initVector(data.get(0));
		JTable jTable=new JTable(data0, colNames);
		
		JScrollPane jscroll=new JScrollPane(jTable);
		jscroll.getViewport().add(jTable);
		return jscroll;
		
		
	}
	/**
	 * 设置表格列的宽度
	 * 
	 * @param column
	 * @param table
	 */
	private void setColumnWidth(int[] column) {
		for (int i = 0; i < column.length; i++) {
			TableColumn firsetColumn = baseTable.getColumnModel().getColumn(column[i]);
			switch(i){
			case 0:firsetColumn.setPreferredWidth(40);firsetColumn.setMaxWidth(50);firsetColumn.setMinWidth(30);break;
			case 1:firsetColumn.setPreferredWidth(40);firsetColumn.setMaxWidth(50);firsetColumn.setMinWidth(30);break;
			case 2:firsetColumn.setPreferredWidth(80);firsetColumn.setMaxWidth(100);firsetColumn.setMinWidth(30);break;
			case 3:firsetColumn.setPreferredWidth(80);firsetColumn.setMaxWidth(100);firsetColumn.setMinWidth(30);break;
			case 4:firsetColumn.setPreferredWidth(80);firsetColumn.setMaxWidth(100);firsetColumn.setMinWidth(30);break;
			case 5:firsetColumn.setPreferredWidth(80);firsetColumn.setMaxWidth(120);firsetColumn.setMinWidth(30);break;
			case 6:firsetColumn.setPreferredWidth(150);firsetColumn.setMaxWidth(200);firsetColumn.setMinWidth(30);break;
			case 7:firsetColumn.setPreferredWidth(400);firsetColumn.setMaxWidth(1000);firsetColumn.setMinWidth(30);break;
			}
			/*firsetColumn.setPreferredWidth(200);
			firsetColumn.setMaxWidth(450);
			firsetColumn.setMinWidth(30);*/
		}
	//	hideColumn(new int[]{0});
	}
	/*private void hideColumn(int[] cols){
		for(int c:cols){
			TableColumn coln=baseTable.getColumnModel().getColumn(c);
			coln.setMinWidth(0);
			coln.setMaxWidth(0);
			coln.setWidth(0);
			coln.setPreferredWidth(0);
		}
	}*/
}
