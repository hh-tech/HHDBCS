package com.hhdb.csadmin.plugin.table_operate.component;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.table.TableColumn;

import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hh.frame.swingui.swingcontrol.displayTable.basis.BaseTable;
import com.hhdb.csadmin.plugin.table_operate.component.button.BaseButton;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxCellEditor;
import com.hhdb.csadmin.plugin.table_operate.component.checkbox.CheckBoxRender;

/**
 * @author zl
 *
 */
public class BaseDialogKey extends JDialog {

	private static final long serialVersionUID = 1L;
	private BaseTable baseTable;
	private TablePanelUtil tablePanel;
	private static JPanel mainPanel;
	private static BaseButton OK;
	private static BaseButton cancle;
	private static List<String> lists = new ArrayList<String>();
	static {
		lists.add("选择");
		lists.add("名");
	}

	public BaseDialogKey() {
		// super(ApplicationLauncher.getFrame(),"");
		setPreferredSize(new Dimension(200, 200));
		tablePanel = new TablePanelUtil(new int[] { 1 });
		baseTable = tablePanel.getBaseTable();
		tablePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));
		tablePanel.getViewport().setBackground(Color.WHITE);
		mainPanel = new JPanel();
		OK = new BaseButton("确定");
		cancle = new BaseButton("取消");
		mainPanel.setLayout(new GridBagLayout());
		mainPanel.setBorder(BorderFactory.createLineBorder(new Color(214, 221, 239), 4));
		mainPanel.setBackground(Color.WHITE);
		mainPanel.add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		mainPanel.add(OK, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE, new Insets(3, 26, 3, 3), 0, 0));
		mainPanel.add(cancle, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST,
				GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
		setModal(true);
		setUndecorated(true);
		setLayout(new BorderLayout());
		add(mainPanel);
		pack();
	}

	public void showDialog(int x, int y) {
		setLocation(x, y);
		setVisible(true);
		toFront();
		requestFocus();
	}

	public static void colseDialog() {
		// setVisible(false);
	}

	public void setData(String[] values) {
		baseTable.repaint();
		List<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
		for (String str : values) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("选择", false);
			map.put("名", str);
			li.add(map);
		}
		tablePanel.setData(lists, li);
		TextCellEditor textcell = new TextCellEditor();
		CheckBoxCellEditor checkboxcell = new CheckBoxCellEditor();
		CheckBoxRender checkboxRender = new CheckBoxRender();
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellEditor(checkboxcell);
		tablePanel.getBaseTable().getColumnModel().getColumn(0).setCellRenderer(checkboxRender);
		tablePanel.getBaseTable().getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(textcell));
		TableColumn firsetColumn = baseTable.getColumnModel().getColumn(0);
		firsetColumn.setPreferredWidth(60);
		firsetColumn.setMaxWidth(60);
	}

	public BaseTable getBaseTable() {
		return baseTable;
	}

	public BaseButton getOkBtn() {
		return OK;
	}

	public BaseButton getCancleBtn() {
		return cancle;
	}

	public TablePanelUtil getTablePanel() {
		return tablePanel;
	}

	public void setTablePanel(TablePanelUtil tablePanel) {
		this.tablePanel = tablePanel;
	}
	
}
