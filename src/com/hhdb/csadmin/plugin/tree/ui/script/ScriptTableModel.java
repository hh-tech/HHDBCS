package com.hhdb.csadmin.plugin.tree.ui.script;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class ScriptTableModel extends JTable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScriptTableModel(List<List<String>> dataModes){
		DefaultTableModel model = new DefaultTableModel();
		Vector<String> columName = new Vector<String>();
		Vector<String> rowsLine;
		for (String colunm : dataModes.get(0)) {
			columName.add(colunm);
		}
		model.setColumnIdentifiers(columName);
		for(int j=1;j<dataModes.size();j++){
			rowsLine = new Vector<>();
			List<String> l = dataModes.get(j);
			for(String value:l){
				rowsLine.add(value);
			}
			model.addRow(rowsLine);
		}
		
		setModel(model);		
		setRowHeight(25);  								//行距
		setGridColor(new Color(240, 240, 240));  		//线条颜色
		getTableHeader().setResizingAllowed(false);   	//表格长度不可变
		getTableHeader().setReorderingAllowed(false);	//表格列不可拖动
		setCellSelectionEnabled(true);  					//选择单元格
		setDefaultRenderer(Object.class,new HHTableSelectedRowRenderer());  //设置隔行变色
		//排序
		RowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
		setRowSorter(sorter);
		
		addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e) {
				int row = getSelectedRow();
				setDefaultRenderer(Object.class,new HHTableSelectedRowRenderer(row));
			}
		});
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {   //不可编辑
		return false;
	}
	
	public static void main(String[] args) {
		
		JFrame jf = new JFrame("JTable的排序测试");
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// 表格中显示的数据、
		Object rows[][] = { { "周丹", "江西", "43" }, { "张三", "四川", "25" },
				{ "李四", "贵州", "32" }, { "王五", "新疆", "24" },
				{ "马六", "江苏", "45" }, { "赵七", "广东", "33" } };
		String columns[] = { "姓名", "籍贯", "年龄" };
		
		List<List<String>> list= new ArrayList<List<String>>();
		List<String> cu = new ArrayList<String>();
		for(String c:columns){
			cu.add(c);
		}
		list.add(cu);
		for(Object[] oo:rows){
			List<String> r = new ArrayList<String>();
			for(Object o:oo){
				r.add(o.toString());
			}
			list.add(r);
		}
		
		final JTable tableModel = new ScriptTableModel(list);
		
		JScrollPane pane = new JScrollPane(tableModel);
		jf.add(pane, BorderLayout.CENTER);
		jf.setSize(300, 150);
		jf.setVisible(true);

		tableModel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// 获取多少列
				int q = tableModel.getColumnCount();
				// 得到选中的行列的索引值
				int r = tableModel.getSelectedRow();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < q; i++) {
					sb.append(tableModel.getValueAt(r, i));
				}
				System.out.println(sb.toString());
			}
			@Override
			public void mousePressed(MouseEvent e) {
			}
		});
	}
	
	public class HHTableSelectedRowRenderer extends DefaultTableCellRenderer{
		private static final long serialVersionUID = 1L;
		private Color rowColor = new Color(70, 150, 255);//蓝色
		private Color evenRowColor = new Color(210, 250, 210);// 偶数行颜色
		private Color oddRowColor = new Color(255, 255, 255);// 奇数行颜色
		private int rows = -1;
		
		public HHTableSelectedRowRenderer() {
			
		}
		public HHTableSelectedRowRenderer(int row) {
			this.rows = row;
		}
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if(row == rows){
				comp.setBackground(rowColor);
				comp.setForeground(Color.WHITE);
			}else{
				if (row % 2 == 0) {
					comp.setBackground(oddRowColor);
				} else {
					comp.setBackground(evenRowColor);
				}
				comp.setForeground(Color.BLACK);
			}
			return comp;
		}
	}

}
