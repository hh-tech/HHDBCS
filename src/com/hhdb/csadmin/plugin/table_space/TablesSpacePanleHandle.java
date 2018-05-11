package com.hhdb.csadmin.plugin.table_space;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.user_create.ui.BaseLabel;
import com.hhdb.csadmin.plugin.user_create.ui.UIUtils;

public class TablesSpacePanleHandle  extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField wzName;
	ComboBoxCellEditor fh;
	String viewName;
	private TableSpace tableSpace;
	 
	public TablesSpacePanleHandle(TableSpace tableSpace){
		this.tableSpace = tableSpace;
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		BaseLabel label=new BaseLabel("位置:");
		
		fh=new ComboBoxCellEditor();
		getDataTypes(fh);
		fh.setPreferredSize(new Dimension(80,20));
		label.setPreferredSize(new Dimension(80,20));
		wzName = new JTextField(20);//位置
		add(label, new GridBagConstraints(0 , 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(wzName, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
		add(new BaseLabel("拥有者:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 10, 0, 0), 0, 0));
		add(fh, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 30, 0, 0), 0, 0));
		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		add(jpl, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
//		add(jpl, new GridBagConstraints(0, 27, 4, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0));

	}
	
	//sql预览
	public void sqlPreview(QueryTextPane querytext){
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" CREATE TABLESPACE \"NewTablespace\"");
		if(!"".equals(fh.getStringValue().trim())){
			sqlBuffer.append("  OWNER "+fh.getStringValue()+""); 
		}
		if(!"".equals(wzName.getText().trim())){
			sqlBuffer.append("  LOCATION '"+wzName.getText()+"'");
		}
		sqlBuffer.append(";");
		
		querytext.setText(sqlBuffer.toString());
	}
	
	//保存表空间
	public void saveTab(){
		viewName = JOptionPane.showInputDialog(null, "输入表空间名", "表空间名",JOptionPane.PLAIN_MESSAGE);
		boolean  isHave = getNameList(viewName);
		if(isHave){//判断表空间名是否存在或者是否为空
			return;
		}
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" CREATE TABLESPACE "+viewName+" ");
		if(!"".equals(fh.getStringValue().trim())){
			sqlBuffer.append("  OWNER "+fh.getStringValue()+""); 
		}
		if(!"".equals(wzName.getText().trim())){
			sqlBuffer.append("  LOCATION '"+wzName.getText()+"'");
		}
		sqlBuffer.append(";");
		try {
			boolean b =tableSpace.SaveData(sqlBuffer.toString());
			if(b){
				JOptionPane.showMessageDialog(null, "添加表空间成功");
				//刷新节点
				tableSpace.refreshData();
				wzName.setText("");
			}
		} catch (Exception e) {
			
			UIUtils.showErrorBox(e.getMessage());
		}
		
	}
	
	/**
	 * 判断表空间名是否存在或者是否为空
	 * @param node
	 * @return
	 */
	public boolean getNameList(String viewName){
		boolean flag = false;
		if("".equals(viewName)){
			JOptionPane.showMessageDialog(null, "表空间名不能为空");
			flag = true;
			return flag;
		}else if(null == viewName ){//点击取消按钮返回null
			flag = true;
			return flag;
		}
//		String sql="select spcname from "+StartUtil.prefix+"_tablespace; ";
//		List<Map<String,Object>>list = tableSpace.FindData(sql);
//		for (Map<String, Object> map : list) {
//			if(map.get("spcname").toString().equals(viewName)){
//				JOptionPane.showMessageDialog(null, "表空间名已经存在");
//				flag = true;
//				return flag;
//			}
//		}
		return flag;
	}
	
	/**
	 * 获取数据库所有用户填充至下拉列表
	 */
	void getDataTypes(ComboBoxCellEditor cell) {
		String sql = "select usename from "+StartUtil.prefix+"_user";
		List<Map<String, Object>> list = tableSpace.FindData(sql);
		
		for (Map<String, Object> map : list) {
			cell.addItem(map.get("usename").toString());
		}
	}
}
