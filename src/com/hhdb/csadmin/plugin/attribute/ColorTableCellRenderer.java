package com.hhdb.csadmin.plugin.attribute;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.hhdb.csadmin.common.util.StringUtil;


public class ColorTableCellRenderer extends DefaultTableCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		String icoPaht="";
		if(column==0){
				icoPaht = StringUtil.getProIcoPath("poptables.png");
				Icon icon=new ImageIcon(icoPaht);
            return new JLabel(icon,JLabel.CENTER);
        }else{
            return super.getTableCellRendererComponent(table, value, isSelected,hasFocus, row, column);
        }
	}

	
	
}
