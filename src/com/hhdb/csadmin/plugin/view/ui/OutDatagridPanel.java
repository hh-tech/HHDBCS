package com.hhdb.csadmin.plugin.view.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.table_open.ui.HHTableColumnCellRenderer;
import com.hhdb.csadmin.plugin.view.ViewOpenPanel;
import com.hhdb.csadmin.plugin.view.util.ButtonPanelEditorRenderer;

/**
 * 视图运行结果集
 * @author hhxd
 *
 */
public class OutDatagridPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	public ViewOpenPanel vop;
	//按钮栏
	public JToolBar toolBar = new JToolBar();
	private TablePanelUtil table;
	private String excutesql = "";  //sql语句
	private BaseButton nextpage = createBtn("下一页",IconUtilities.loadIcon("next.png"), "nextpage", "下一页");
	private BaseButton uppage = createBtn("上一页",IconUtilities.loadIcon("pre.png"), "uppage", "上一页");
	/***每页数量*/
	private int position;   
	/***当前页码数*/
	public int pageNum = 0;
	/***总页数*/
	public int pageSum = 0;
	private JLabel msglabel = new JLabel();   //状态条

	public OutDatagridPanel(String sqls,ViewOpenPanel vop) {
		this.vop=vop;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		excutesql = sqls;
		position = vop.inNum;
		init();
	}

	public void init() {
		//获取第一页数据
		List<List<Object>> dbtable = loadData();
		table = new TablePanelUtil(vop.sqsv.getUneditable(dbtable, true, "bytea"));
		table.getBaseTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.nterlacedDiscoloration(true,null,null); 
		
		toolBar.setFloatable(false);
		toolBar.setLayout(new GridBagLayout());
		toolBar.add(uppage, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
		toolBar.add(nextpage, new GridBagConstraints(1, 0, 1, 1, 0.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
		toolBar.add(new JPanel(), new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(toolBar, BorderLayout.NORTH);

		setBorder(null);
		add(table, BorderLayout.CENTER);
		//下方状态栏
		JPanel status = new JPanel();
		status.setPreferredSize(new Dimension(350, 22));
		status.setLayout(new GridBagLayout());
		status.add(msglabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		status.add(new JPanel(), new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(status, BorderLayout.SOUTH);
		excutepage(dbtable);
		showPageCount();
	}

	/**
	 * 处理数据显示
	 * @param pageTables
	 */
	public void excutepage(List<List<Object>> lis) {
		//表头数据
		Vector<Object> colname = new Vector<Object>();
		colname.add("");
		for (Object field : lis.get(0)) {
			colname.add(field);
		}
		Vector<Object> vect = new Vector<Object>();
		//将查询出来的数据分出来
		for(int j=2;j<lis.size();j++){
			List<Object> l = lis.get(j);
			Vector<Object> rowsLine = new Vector<Object>();
			rowsLine.add(j-1+"");   //行号
			for(Object value:l){
				rowsLine.add(value);
			}
			vect.add(rowsLine);
		}
		//获取字段类型
		List<Object> typelist = new ArrayList<Object>();
		typelist.add("");
		for (Object field : lis.get(1)) {
			typelist.add(field);
		}
		//添加数据
		table.getTableDataModel().setDataVector(vect,colname);
		//判断哪些列需要加入流操作按钮
		ButtonPanelEditorRenderer er =  new ButtonPanelEditorRenderer(vop);
		for(int i=0;i<colname.size();i++){
			Object name = colname.get(i);
			if(!"".equals(name)){
			Object type=typelist.get(i);
				if(type.equals("bytea")){
					table.getBaseTable().getColumn(name).setCellRenderer(er);
					table.getBaseTable().getColumn(name).setCellEditor(er);
				}
			}
		}
		//设置行号列样式
		TableColumn index = table.getBaseTable().getColumnModel().getColumn(0);
		index.setMaxWidth(25);
		index.setMinWidth(25);
		index.setCellRenderer(new HHTableColumnCellRenderer());
	}
	
	
	/**
	 * 查询数据
	 */
	public List<List<Object>> loadData() {
		List<List<Object>> dbtable = new ArrayList<>();
		String ssql = "";
		String trimExcutesql = excutesql.trim();
		if (trimExcutesql.toUpperCase().startsWith("SELECT") || (trimExcutesql.toUpperCase().startsWith("WITH") && trimExcutesql.toUpperCase().indexOf(" RECURSIVE") != -1)) {
			if (trimExcutesql.endsWith(";")) {
				trimExcutesql = trimExcutesql.substring(0,trimExcutesql.lastIndexOf(";"));
			}
			ssql = "select * from (" + trimExcutesql + ") a limit " + position + " offset " + position*pageNum;
		} else {
			ssql = excutesql;
		}
		long btime = System.currentTimeMillis();
		try {
			dbtable = vop.sqsv.getListType(ssql);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		long etime = System.currentTimeMillis();
		msglabel.setText("查询总耗时：" + (etime - btime) + " ms, 检索到: "+ (dbtable.size()-2) + " 行");
		return dbtable;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if ("nextpage".equals(actionCmd)) {    //下一页
			if(pageSum-1 > pageNum ){
				pageNum ++;
			}
			excutepage(loadData());
			
			uppage.setEnabled(true);
			if(pageSum-1 > pageNum ){
				nextpage.setEnabled(true);
			}else{
				nextpage.setEnabled(false);
			}
		} else if ("uppage".equals(actionCmd)) {  //上一页
			if(pageNum != 0){
				pageNum --;
			}
			excutepage(loadData());
			
			nextpage.setEnabled(true);
			if(pageNum > 0){
				uppage.setEnabled(true);
			}else{
				uppage.setEnabled(false);
			}
		} 
	}
	
	
	/**
	 * 计算中页数
	 */
	public void showPageCount() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 先获取表总行数
				List<List<Object>> dbtable = new ArrayList<>();
				String ssql = "";
				String trimExcutesql = excutesql.trim();
				if (trimExcutesql.toUpperCase().startsWith("SELECT") || (trimExcutesql.toUpperCase().startsWith("WITH") && trimExcutesql.toUpperCase().indexOf(" RECURSIVE") != -1)) {
					if (trimExcutesql.endsWith(";")) {
						trimExcutesql = trimExcutesql.substring(0,trimExcutesql.lastIndexOf(";"));
					}
					ssql = "select count(1) from (" + trimExcutesql + ") a " ;
				} else {
					ssql = excutesql;
				}
				try {
					dbtable = vop.sqsv.getListList(ssql);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
				}
				List<Object> rowStrList = dbtable.get(1);
				int rowCount = Integer.parseInt(rowStrList.get(0)+"");
				int intCount = rowCount / position;
				int modCount = rowCount % position;
				if (modCount != 0) {
					pageSum = intCount + 1;
				} else {
					pageSum = intCount;
				}
				uppage.setEnabled(false);
				nextpage.setEnabled(false);
				if(pageSum >1){
					nextpage.setEnabled(true);
				}
			}
		}).start();
	}
	
	
	/**
	 * 创建按钮
	 * @return
	 */
	private BaseButton createBtn(String text, Icon icon, String comand,String prompt) {
		BaseButton basebtn = new BaseButton(text, icon);
		basebtn.setActionCommand(comand);
		basebtn.addActionListener(this);
		basebtn.setToolTipText(prompt);
		return basebtn;
	}

}
