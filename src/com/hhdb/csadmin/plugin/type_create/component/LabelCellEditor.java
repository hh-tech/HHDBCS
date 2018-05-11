package com.hhdb.csadmin.plugin.type_create.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import com.hhdb.csadmin.common.util.IconUtilities;

public class LabelCellEditor extends AbstractCellEditor implements TableCellEditor{
	 private static final long serialVersionUID = 1L;
	 private JLabel jlbel;
	 private final Icon imgicon=IconUtilities.loadIcon("parkey.png");
	 public LabelCellEditor()  
	 {  
		 jlbel=new JLabel();
		 jlbel.addMouseListener(new MouseAdapter(){
	      public void mouseClicked(MouseEvent e){
	    	  if(jlbel.getIcon()==null)
	    	  {
	    		  jlbel.setIcon(imgicon); 
	    	  }else
	    	  {
	    		  jlbel.setIcon(null);
	    	  }
	        }
	      });
		 jlbel.setHorizontalAlignment(SwingConstants.CENTER);
		 jlbel.setBackground(Color.WHITE);
	 }
	@Override
	public Object getCellEditorValue() {
		return jlbel.getIcon();
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		  if(jlbel.getIcon()==null){
			  jlbel.setIcon(imgicon); 
    	  }else{
    		  jlbel.setIcon(null);
    	  }
		return jlbel;
	}
	public JLabel getJlbel() {
		return jlbel;
	}
	public void setJlbel(JLabel jlbel) {
		this.jlbel = jlbel;
	}  
}