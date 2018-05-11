package com.hhdb.csadmin.plugin.table_operate.handle;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.plugin.table_operate.TableEditPanel;

/**
 * 
 * @Description: 注释
 * @Copyright: Copyright (c) 2017年10月25日
 * @Company:H2 Technology
 * @author zhipeng.zhang
 * @version 1.0
 */
public class HandleCommentPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private TableEditPanel tabp;
	private JTextArea comment;
	private String text = "";

	public HandleCommentPanel(TableEditPanel tableeditpanel, HandleTablePanel tabsPanel) {
		this.tabp = tableeditpanel;
		comment = new JTextArea();
		initObj();
		JScrollPane jsol = new JScrollPane(comment);
		setLayout(new GridBagLayout());
		add(jsol, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		
		comment.addCaretListener(new CaretListener() {  
	         public void caretUpdate(CaretEvent e) {  
	        	 if(!text.equals(comment.getText())){
	        		 tabp.getToolBar().getComponentAtIndex(0).setEnabled(tabp.controlButton = true);
	        	 }
	         }  
	     });  
//		comment.addMouseListener(new MouseAdapter() {  
//	         @Override  
//	         public void mouseClicked(MouseEvent e) {  
//	        	 if(!text.equals(comment.getText())){
//	        		 tabp.getToolBar().getComponentAtIndex(0).setEnabled(tabp.controlButton = true);
//	        	 }
//	         }  
//	     });  
	}
	
	private void initObj(){
		try {
			String sql="select  obj_description("+tabp.getTableoId()+");";
			List<Map<String, Object>> map =	tabp.sqls.getListMap(sql);
			for (Map<String, Object> map2 : map) {
				if(null != map2.get("obj_description") && map2.get("obj_description") != ""){
					comment.setText(map2.get("obj_description").toString());
					text = map2.get("obj_description").toString();
				}else{
					comment.setText("");
				}
			}
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
	}
	
	public String createCommentSql() {
		String str = comment.getText();
		if(tabp.getSign()){   //新建
			if(null != str && !str.equals("")){
				String sql = "\n COMMENT ON TABLE \"" + tabp.getSchemaName() + "\".\"" + tabp.getTableName() + "\" IS '" + str + "';";
				return sql;
			}
		}else{   //修改
			if(!text.equals(str)){
				String sql = "\n COMMENT ON TABLE \"" + tabp.getSchemaName() + "\".\"" + tabp.getTableName() + "\" IS '" + str + "';";
				return sql;
			}
		}
		return "";
	}  
	
	
	public JTextArea getComment() {
		return comment;
	}
}