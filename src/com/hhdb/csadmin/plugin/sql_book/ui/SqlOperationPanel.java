package com.hhdb.csadmin.plugin.sql_book.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.collections4.map.HashedMap;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.TemplateUtil;
import com.hhdb.csadmin.common.ui.textEdit.QueryEditorUi2;
import com.hhdb.csadmin.common.util.HSQL_Util;
import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.util.TreeNodeUtil;

/**
 * sql操作面板
 * @author hhxd
 * 
 */
public class SqlOperationPanel extends JPanel {
	private static final long serialVersionUID = 2805619451637353405L;
	private BooksPanel book;
	private  BaseTreeNode treeNode;
	
//	private JLabel title;   //标题
	private JTextField name;
	private JTextField wz;
	private QueryEditorUi2 jtp; // sql编辑面板
	private JButton jb;
	
	public Map<String, Object> map = new HashedMap<>();
	public String txt;
	
	private Integer id;	
	
	/**
	 * sql详情设置界面
	 * @param bp
	 * 树状图
	 * @param tn
	 * 选择的节点
	 */
	public SqlOperationPanel(BooksPanel bp,BaseTreeNode tn,String titl) {
		this.treeNode = tn;
		this.book = bp;
//		title = new JLabel(titl);
//		title.setFont(new Font("Dialog", 1, 18));   
		jtp = new QueryEditorUi2();
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		
		if( treeNode.getMetaTreeNodeBean().getType().equals(TreeNodeUtil.FILE_TYPE) || treeNode.getMetaTreeNodeBean().getType().equals(TreeNodeUtil.ROOT_TYPE) ){  //保存sql
			jb = new JButton("保存");
			name = new JTextField();
			jb.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	try {
	            		if(null == name.getText() || name.getText().equals("")){
	            			JOptionPane.showMessageDialog(null, "名称非法", "消息",JOptionPane.ERROR_MESSAGE);
	            			return;
	            		}
	            		//判断名称是否有重复的
	            		if(book.objtool.nameExist(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId(), name.getText())){
	            			JOptionPane.showMessageDialog(null, "名称不能重复", "消息",JOptionPane.ERROR_MESSAGE);
	            		}else{
	            			String sql = jtp.getText().replace("'", "''");
	            			if(book.objtool.create(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId(), name.getText(), sql) != -1){
								book.sqls.refresh(treeNode,true);
								jb.setEnabled(false);
							}else{
								JOptionPane.showMessageDialog(null, "添加失败", "消息",JOptionPane.ERROR_MESSAGE);
							}
	            		}
					} catch (Exception e1) {
						LM.error(LM.Model.CS.name(), e1);
						JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
					} 
	            }
	        });
		}else if(treeNode.getMetaTreeNodeBean().getType().equals(TreeNodeUtil.SQL_TYPE)){
			name = new JTextField(treeNode.getMetaTreeNodeBean().getName());
			jtp.setText(treeNode.getMetaTreeNodeBean().getTxt());
			if(book.source){   						//修改sql
				jb = new JButton("保存");
				jb.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	try {
		            		if(null == name.getText() || name.getText().equals("")){
		            			JOptionPane.showMessageDialog(null, "名称非法", "消息",JOptionPane.ERROR_MESSAGE);
		            			return;
		            		}
		            		String sql = jtp.getText().replace("'", "''");
		            		//是否没有改名称
		            		if(name.getText().equals(treeNode.getMetaTreeNodeBean().getName())){
		            			book.objtool.updateTxt(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId(), sql);  //只改内容
		            			book.sqls.refresh(treeNode.getParentBaseTreeNode(),true);  //刷新父节点
								jb.setEnabled(false);
		            		}else{
		            			//排除重复
			            		if(book.objtool.nameExist(HSQL_Util.getConnection(), treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId(), name.getText())){
			            			JOptionPane.showMessageDialog(null, "名称不能重复", "消息",JOptionPane.ERROR_MESSAGE);
			            		}else{
			            			book.objtool.updateNameTxt(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId(), name.getText(), sql);
				            		book.sqls.refresh(treeNode.getParentBaseTreeNode(),true);  //刷新父节点
									jb.setEnabled(false);
			            		}
		            		}
						} catch (Exception e1) {
							LM.error(LM.Model.CS.name(), e1);
							JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
						} 
		            }
		        });
			}else{    //加载sql到查询器
				jb = new JButton("加载");
				name.setEditable(false);
				jtp.getTextArea().setEditable(false);
				jb.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	try {
		            		String text = "";
		            		if(null == jtp.getSelectedText()){
		            			text = jtp.getText();
		            		}else{
		            			text = jtp.getSelectedText();
		            		}
		            		book.jtext.setText(text);
		            		JOptionPane.showMessageDialog(null, "加载成功", "消息",JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e1) {
							LM.error(LM.Model.CS.name(), e1);
							JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
						} 
		            }
		        });
			}
		}else if(treeNode.getMetaTreeNodeBean().getType().equals(TreeNodeUtil.QUICK_TYPE)){   
			name = new JTextField(treeNode.getMetaTreeNodeBean().getName());
			jtp.setText(treeNode.getMetaTreeNodeBean().getTxt());
			name.setEditable(false);
			jtp.getTextArea().setEditable(false);
			if(book.source){   				//查看快捷方式
				jb = new JButton("保存");
				jb.setEnabled(false);
			}else{    						//加载快捷方式到查询器
				jb = new JButton("加载");
				jb.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	try {
		            		String text = "";
		            		if(null == jtp.getSelectedText()){
		            			text = jtp.getText();
		            		}else{
		            			text = jtp.getSelectedText();
		            		}
		            		book.jtext.setText(text);
		            		JOptionPane.showMessageDialog(null, "加载成功", "消息",JOptionPane.INFORMATION_MESSAGE);
						} catch (Exception e1) {
							LM.error(LM.Model.CS.name(), e1);
							JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
						} 
		            }
		        });
			}
		}
		name.setPreferredSize(new Dimension(300, 20));
		jtp.getContentPane().setBorder(BorderFactory.createLineBorder(new Color(172, 173, 179), 1));
		jtp.getContentPane().setPreferredSize(new Dimension(800, 300));
		
//		add(title, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 5, 0, 0), 0, 0));
		add(new JLabel("名称:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		add(name, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 30, 0, 0), 0, 0));
		add(new JLabel("定义:"), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		add(jtp.getContentPane(), new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 30, 0, 0), 0, 0));
		add(new JLabel("*需要替换的参数必须写成${xxx}否则不能正常替换，不能使用中文。"), new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(5, 30, 0, 0), 0, 0));
		add(jb, new GridBagConstraints(1, 5, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 300, 0, 0), 0, 0));
	}
	
	/**
	 * 路径选择页面
	 * @param bp
	 * 路径选择树状图
	 * @param wjname
	 * 选择的文件夹名称
	 * @param wjid
	 * 选择的文件夹id
	 */
	public SqlOperationPanel(BooksPanel bp,String wjname, final Integer wjid) {
		this.book = bp;
		this.id = wjid;
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		name = new JTextField();
		wz = new JTextField(wjname);
		jb = new JButton("保存");
		wz.setEditable(false);
		name.setPreferredSize(new Dimension(300, 20));
		wz.setPreferredSize(new Dimension(300, 20));
		add(new JLabel("名称:"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(50, 10, 0, 0), 0, 0));
		add(name, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(50, 30, 0, 0), 0, 0));
		add(new JLabel("位置:"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 10, 0, 0), 0, 0));
		add(wz, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 30, 0, 0), 0, 0));
		add(jb, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 150, 0, 0), 0, 0));
		
		jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(book.source){  //保存快捷方式
            		try {
            			if(null == name.getText() || name.getText().equals("")){
	            			JOptionPane.showMessageDialog(null, "名称非法", "消息",JOptionPane.ERROR_MESSAGE);
	            			return;
	            		}
                		//验证重复
                		if(book.objtool.nameExist(HSQL_Util.getConnection(),id,name.getText())){
                			JOptionPane.showMessageDialog(null, "名称不能重复", "消息",JOptionPane.ERROR_MESSAGE);
                		}else{
                			if(book.linkTool.createObjLink(HSQL_Util.getConnection(), id,name.getText(),book.myDirId) != -1 ){
        						book.shp.dispose();   //关闭面板
        						JOptionPane.showMessageDialog(null, "添加成功", "消息",JOptionPane.INFORMATION_MESSAGE);
        					}else{
        						JOptionPane.showMessageDialog(null, "添加失败", "消息",JOptionPane.ERROR_MESSAGE);
        					}
                		}
    				} catch (Exception e1) {
    					LM.error(LM.Model.CS.name(), e1);
    					JOptionPane.showMessageDialog(null, e1.getMessage(),  "消息",JOptionPane.ERROR_MESSAGE);
    				} 
            	}else{   //查询器保存sql
        			try {
        				if(null == name.getText() || name.getText().equals("")){
	            			JOptionPane.showMessageDialog(null, "名称非法", "消息",JOptionPane.ERROR_MESSAGE);
	            			return;
	            		}
                		//判断名称是否有重复的
                		if(book.objtool.nameExist(HSQL_Util.getConnection(), id, name.getText())){
                			JOptionPane.showMessageDialog(null, "名称不能重复", "消息",JOptionPane.ERROR_MESSAGE);
                		}else{
                			String sql = book.sql.replace("'", "''");
                			if(book.objtool.create(HSQL_Util.getConnection(), id, name.getText(), sql) != -1){
                				book.shp.dispose();   //关闭面板
                				JOptionPane.showMessageDialog(null, "添加成功", "消息",JOptionPane.INFORMATION_MESSAGE);
        					}else{
        						JOptionPane.showMessageDialog(null, "添加失败", "消息",JOptionPane.ERROR_MESSAGE);
        					}
                		}
        			} catch (Exception e1) {
        				LM.error(LM.Model.CS.name(), e1);
        				JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
        			} 
        		}
            }
        });
		
	}
	
	/**
	 * 参数替换页面
	 * @param bp
	 * @param setstr
	 * 替换的参数
	 * @param txts
	 * sql语句
	 */
	public SqlOperationPanel(BooksPanel bp,Set<String> setstr,String txts) {
		this.book = bp;
		this.txt = txts;
		setBackground(Color.WHITE);
		setLayout(new GridBagLayout());
		int i = 0;
		for (String string : setstr) {
			JTextField jte = new JTextField();
			jte.setPreferredSize(new Dimension(300, 20));
			add(new JLabel(string+":"), new GridBagConstraints(0, i+1, 1, 1, 0.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(30, 10, 0, 0), 0, 0));
			add(jte, new GridBagConstraints(1, i+1, 1, 1, 1.0, 0.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(30, 30, 0, 0), 0, 0));
			map.put(string, jte);
			i++;
		}
		jb = new JButton("保存");
		add(jb, new GridBagConstraints(1, i+1, 1, 1, 1.0, 1.0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,new Insets(20, 130, 0, 0), 0, 0));
		//按钮保存
		jb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//在查询器打开sql
            	try {
            		Map<String,Object> val = new HashedMap<>();
            		for (String string : map.keySet()) {
            			JTextField jt = (JTextField)map.get(string);
            			val.put(string, jt.getText());
            		}
            		String sql = TemplateUtil.strVm2str(val,txt);
            		book.sqls.getQuery(sql);
            		book.shp.dispose();   //关闭面板
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
				} 
            }
        });
	}
}
