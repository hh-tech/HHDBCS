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
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;
import com.hhdb.csadmin.common.util.CSVUtil;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.menu.Hmenu;
import com.hhdb.csadmin.plugin.menu.util.VectorUtil;


public class CurrentDealPanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2030610067777006366L;
	private JButton reflashbtn = null, endbtn = null;
	protected BaseTable table=null;// 表格
	private Hmenu hmenu;

	public CurrentDealPanel(Hmenu hmenu, String dealTableId) {
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
		add(jToolBar, new GridBagConstraints(0, 0, 1, 1,1.0,0.0,
				GridBagConstraints.SOUTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		
		//table
		table = new BaseTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 5012232723792847840L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		table.getTableHeader().setReorderingAllowed(false);
		loadTable();
		add(new JScrollPane(table), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(reflashbtn)) {
			loadTable();
		} else if (e.getSource().equals(endbtn)) {
			int result=JOptionPane.showConfirmDialog(this, "确定要kill掉此连接?","提示信息",JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION){
				int rows []=table.getSelectedRows();
				if(rows==null||rows.length==0){
					return;
				}else{
					for(int i=0;i<rows.length;i++){
						int pid=Integer.parseInt(table.getValueAt(rows[i], 0).toString());
						String sql= "SELECT "+StartUtil.prefix+"_terminate_backend('"+pid+"')";
						String fromID = Hmenu.class.getPackage().getName();
						String toID = "com.hhdb.csadmin.plugin.conn";
						CmdEvent executeEvent = new CmdEvent(fromID, toID, "ExecuteCSVBySqlEvent");
						executeEvent.addProp("sql_str", sql);
						hmenu.sendEvent(executeEvent);
						loadTable();
					}
				}
				
				loadTable();
			
			}
		}
	}
	
	private void loadTable(){
		//刷新列表
		String sql = "select   pid \"PID\",client_addr 客户端, datname 目标数据库,state 状态, now() - "+StartUtil.prefix+"_stat_activity.query_start as 已执行时间,  query 执行sql from  "+StartUtil.prefix+"_stat_activity  where query <> ' '::text  and now() - "+StartUtil.prefix+"_stat_activity.query_start > interval '1 second'  and state <> 'idle';";
		String csv = hmenu.sendToConn( sql);
		List<List<String>> data = null;
		try {
			data = CSVUtil.cSV2List(csv);
		} catch (IOException e) {
			LM.error(LM.Model.CS.name(), e);
			return;
		}
		Vector<Object> data0=VectorUtil.initVectorData(data);
		Vector<Object> colNames=VectorUtil.initVector(data.get(0));
		table.setModel(new DefaultTableModel(data0, colNames));
		setColumnWidth(new int[] {0, 1,2,3,4,5});
	}

	/**
	 * 设置表格列的宽度
	 * 
	 * @param column
	 * @param table
	 */
	private void setColumnWidth(int[] column) {
		for (int i = 0; i < column.length; i++) {
			TableColumn firsetColumn = table.getColumnModel().getColumn(column[i]);
			switch(i){
			case 0:firsetColumn.setPreferredWidth(40);firsetColumn.setMaxWidth(50);firsetColumn.setMinWidth(30);break;
			case 1:firsetColumn.setPreferredWidth(80);firsetColumn.setMaxWidth(100);firsetColumn.setMinWidth(30);break;
			case 2:firsetColumn.setPreferredWidth(80);firsetColumn.setMaxWidth(100);firsetColumn.setMinWidth(30);break;
			case 3:firsetColumn.setPreferredWidth(80);firsetColumn.setMaxWidth(200);firsetColumn.setMinWidth(30);break;
			case 4:firsetColumn.setPreferredWidth(150);firsetColumn.setMaxWidth(300);firsetColumn.setMinWidth(30);break;
			case 5:firsetColumn.setPreferredWidth(400);firsetColumn.setMaxWidth(2000);firsetColumn.setMinWidth(30);break;
			
			}
			
		}
	
	}
}
