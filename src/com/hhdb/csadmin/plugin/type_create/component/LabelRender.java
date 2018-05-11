
package com.hhdb.csadmin.plugin.type_create.component;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class LabelRender extends JLabel implements TableCellRenderer, MouseListener {
	private static final long serialVersionUID = 3543042197712807784L;

	public LabelRender() {
		super();
		setOpaque(true);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (isSelected) {
			setForeground(table.getForeground());
			super.setBackground(table.getBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}
		System.out.println(value + "------");
		return this;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("-------------hh");
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
