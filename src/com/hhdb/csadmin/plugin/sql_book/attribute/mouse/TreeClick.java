 package com.hhdb.csadmin.plugin.sql_book.attribute.mouse;

import javax.swing.JLabel;

import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.ui.SqlOperationPanel;
import com.hhdb.csadmin.plugin.sql_book.util.TreeNodeUtil;

/**
 * 鼠标单击处理
 * 
 * @author hyz
 * 
 */
public class TreeClick {
	private BooksPanel book;
    public TreeClick(BooksPanel book){
    	this.book = book;
    }		
	public void execute(BaseTreeNode treeNode,Boolean bool) {
		String type = treeNode.getType();
		if(type.equals(TreeNodeUtil.ROOT_TYPE)) {	
			if(bool){
				book.sqlDetail.getViewport().add(new JLabel());
			}else{
				//打开快捷键设置面板
				book.sqlDetail.getViewport().add(new SqlOperationPanel(book,treeNode.getMetaTreeNodeBean().getName(),treeNode.getMetaTreeNodeBean().getId()));
			}
		}else if(type.equals(TreeNodeUtil.FILE_TYPE)){
			if(bool){
				book.sqlDetail.getViewport().add(new JLabel());
			}else{
				//打开快捷键设置面板
				book.sqlDetail.getViewport().add(new SqlOperationPanel(book,treeNode.getMetaTreeNodeBean().getName(),treeNode.getMetaTreeNodeBean().getId()));
			}
		}else if(type.equals(TreeNodeUtil.QUICK_TYPE)){
			book.sqlDetail.getViewport().add(new SqlOperationPanel(book,treeNode,"查看快捷方式"));
		}else if(type.equals(TreeNodeUtil.SQL_TYPE)){
			book.sqlDetail.getViewport().add(new SqlOperationPanel(book,treeNode,"查看SQL"));
		}
	}
	
}
