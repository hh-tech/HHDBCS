package com.hhdb.csadmin.plugin.query.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.plugin.query.HQueryPlugin;
import com.hhdb.csadmin.plugin.query.dataselect.SqlOperationService;

/**
 * 表格栏按钮编辑渲染器
 * @author fx
 *
 */
public class ButtonPanelEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
	private static final long serialVersionUID = 1L;
	private HQueryPlugin hquery;
	private JPanel panel = new JPanel();
	private JButton jbt = new JButton("...");
	private JLabel jtex = new JLabel("BYTEA");
	private Object cellValue ;  // 点击单元格的值
//	private Object ob = ""; 		   // 点击行的id
//	private String columnNmne; // 点击列的名称
	
	public ButtonPanelEditorRenderer(HQueryPlugin queryPlugin) {
		this.hquery=queryPlugin;
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS ));
		panel.setBorder(BorderFactory.createLineBorder(Color.red, 0));
		jtex.setBorder(BorderFactory.createLineBorder(Color.red, 0));
		jbt.setBorder(BorderFactory.createLineBorder(Color.red, 0));
//		jbt.setContentAreaFilled(false);  //设置透明
		jbt.setFocusPainted(false);  //去除焦点
		panel.add(Box.createHorizontalGlue ()); 
		panel.add(jtex);
		panel.add(Box.createHorizontalGlue ()); 
		panel.add(jbt);
		jbt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					SqlOperationService  sqls = new SqlOperationService(hquery);
					sqls.getDataFlowPanel(cellValue);
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
				fireEditingStopped();  // 刷新渲染器
			}
		});
		panel.addMouseListener(new MouseAdapter(){
		    public void mousePressed(MouseEvent e) {
		    	fireEditingStopped();  // 刷新渲染器
		    }
		});
		jtex.addMouseListener(new MouseAdapter(){
		    public void mousePressed(MouseEvent e) {
		    	fireEditingStopped();  // 刷新渲染器
		    }
		});
	}
	@Override
	public Component getTableCellRendererComponent(JTable table,Object value, boolean isSelected, boolean hasFocus, int row,int column) {
		panel.setBackground(isSelected ? table.getSelectionBackground(): table.getBackground());
		return panel;
	}
	@Override
	public Component getTableCellEditorComponent(JTable table,Object value, boolean isSelected, int row, int column) {
		fireEditingStopped();
		cellValue = null == value ?  "" : value;
		return panel;
	}
	@Override
	public Object getCellEditorValue() {
		return cellValue;
	}
}
