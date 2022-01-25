package com.hh.hhdb_admin.common.util.textEditor.base;


import javax.swing.*;

import com.hh.frame.swingui.view.ui.HHSwingUi;

import java.awt.*;

/**
 * 弹出框渲染器
 * @author hhxd
 */
public class IconListCellRenderer implements ListCellRenderer<Object> {
	private JPanel jPanel = new JPanel(new GridBagLayout());
	private JLabel jt= new JLabel();
	
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		Keyword key = (Keyword) value;
		jt.setText(key.getName());
		jt.setIcon(key.getIcon());

		jPanel.add(jt,new GridBagConstraints(0, 0, 1, 1, 1.0,0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,new Insets(0, 5, 0,0), 0, 0));
		
		// 设置选取与取消选取的前景与背景颜色.
		if (isSelected) {
			if(HHSwingUi.isDarkSkin()) {
				jPanel.setBackground(new Color(10, 10, 100));
			}else {
				jPanel.setBackground(new Color(255, 255, 170));				
			}
			jPanel.setForeground(list.getSelectionForeground());
		} else {
			jPanel.setBackground(list.getBackground());
			jPanel.setForeground(list.getForeground());
		}
 		return jPanel;
	}
}
