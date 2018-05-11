package com.hhdb.csadmin.plugin.type_create.component;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicArrowButton;

import com.hhdb.csadmin.plugin.function.component.BaseDialogKey;

public class MulitCombobox extends JPanel {
	private static final long serialVersionUID = -2248098799549252863L;

	protected String[] values;

	protected List<ActionListener> listeners = new ArrayList<ActionListener>();

	protected JTextField editor;

	protected String valueSperator;
	final BaseDialogKey instance;
	protected static final String DEFAULT_VALUE_SPERATOR = ",";

	public MulitCombobox(String[] value) {
		this(value, DEFAULT_VALUE_SPERATOR);
	}

	public void MulitCombobox_all(String[] value) {
		this.repaint();
		values = value;
	}

	public MulitCombobox(String[] value, String valueSperator) {
		values = value;
		this.valueSperator = valueSperator;
		instance = new BaseDialogKey();
		initComponent();
	}

	private void initComponent() {
		instance.setData(values);
		setLayout(new GridBagLayout());
		this.repaint();
		editor = new JTextField();
		editor.setEditable(false);
		editor.setBorder(null);
		editor.setBackground(Color.WHITE);
		JButton arrowButton = createArrowButton();
		arrowButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				/**
				 * 设置已选择的字段
				 * 
				 * @return
				 */
				if (instance.getBaseTable().isEditing()) {
					instance.getBaseTable().getCellEditor().stopCellEditing();
				}
				int rows = instance.getBaseTable().getRowCount();
				for (int i = 0; i < rows; i++) {
					if (editor.getText().indexOf(instance.getBaseTable().getValueAt(i, 1).toString().trim()) > -1) {
						instance.getBaseTable().setValueAt(true, i, 0);
					} else {
						instance.getBaseTable().setValueAt(false, i, 0);
					}
				}
				instance.showDialog(e.getLocationOnScreen().x - 190, e.getLocationOnScreen().y + 12);
			}
		});
		instance.getOkBtn().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				/**
				 * 获取已选择的字段
				 * 
				 * @return
				 */
				int rows = instance.getBaseTable().getRowCount();
				if (instance.getBaseTable().isEditing()) {
					instance.getBaseTable().getCellEditor().stopCellEditing();
				}
				String result = "";
				for (int i = 0; i < rows; i++) {
					if (Boolean.parseBoolean(instance.getBaseTable().getValueAt(i, 0).toString())) {
						result += instance.getBaseTable().getValueAt(i, 1).toString() + ",";
					}
				}
				if (result.length() > 0) {
					result = result.substring(0, result.length() - 1);
				}
				editor.setText(result);
				instance.setVisible(false);
			}
		});
		instance.getCancleBtn().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				instance.setVisible(false);
			}
		});
		add(editor, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		add(arrowButton, new GridBagConstraints(1, 0, 0, 0, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
	}

	public void setData(String[] vls) {
		int rows = instance.getBaseTable().getRowCount();
		for (int i = (rows - 1); i >= 0; i--) {
			instance.getTablePanel().getTableDataModel().removeRow(i);
		}			 
		instance.setData(vls);
	}

	public void addActionListener(ActionListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	protected void fireActionPerformed(ActionEvent e) {
		for (ActionListener l : listeners) {
			l.actionPerformed(e);
		}
	}

	protected String getValues() {
		return editor.getText();
	}

	public void setValues(String str) {
		editor.setText(str);
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	protected JButton createArrowButton() {
		JButton button = new BasicArrowButton(BasicArrowButton.SOUTH, UIManager.getColor("ComboBox.buttonBackground"),
				UIManager.getColor("ComboBox.buttonShadow"), UIManager.getColor("ComboBox.buttonDarkShadow"),
				UIManager.getColor("ComboBox.buttonHighlight"));
		button.setName("ComboBox.arrowButton");
		return button;
	}

//	private class MulitComboboxLayout implements LayoutManager {
//
//		@Override
//		public void addLayoutComponent(String name, Component comp) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void removeLayoutComponent(Component comp) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public Dimension preferredLayoutSize(Container parent) {
//			return parent.getPreferredSize();
//		}
//
//		@Override
//		public Dimension minimumLayoutSize(Container parent) {
//			return parent.getMinimumSize();
//		}
//
//		@Override
//		public void layoutContainer(Container parent) {
//			int w = parent.getWidth();
//			int h = parent.getHeight();
//			Insets insets = parent.getInsets();
//			h = h - insets.top - insets.bottom;
//		}
//	}
}
