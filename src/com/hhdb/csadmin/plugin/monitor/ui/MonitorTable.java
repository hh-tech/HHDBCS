package com.hhdb.csadmin.plugin.monitor.ui;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.TableModel;

public class MonitorTable extends JTable {
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_ROW_HEIGHT = 22;

	public MonitorTable() {
		init();
	}

	public MonitorTable(TableModel model) {
		super(model);
		init();
	}

	private void init() {
		setRowHeight(Math.max(getRowHeight(), DEFAULT_ROW_HEIGHT));
		setGridColor(new Color(240, 240, 240));
		setShowVerticalLines(true);
		getTableHeader().setReorderingAllowed(true);
		setColumnSelectionAllowed(true);
		setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		repaint();
	}

}