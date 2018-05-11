package com.hhdb.csadmin.plugin.type_create.component;

import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;


public class MulitCellEditor extends DefaultCellEditor{
	private static final long serialVersionUID = -5595318363965803280L;
	
	private MulitCombobox mulitcell;
	public MulitCellEditor(String[] value) {
		super(new JTextField());
		this.setClickCountToStart(1);  
		mulitcell=new MulitCombobox(value);
	}

    @Override  
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)  
    {  
    	if(value!=null){
    		mulitcell.setValues(value.toString());
    	}else{
    		value="";
    	}
    	return this.mulitcell;  
    } 
    @Override  
    public Object getCellEditorValue()  
    {  
        return this.mulitcell.getValues();  
    }  
    
    public MulitCombobox getCellEditor()  
    {  
        return mulitcell;  
    }  
}
