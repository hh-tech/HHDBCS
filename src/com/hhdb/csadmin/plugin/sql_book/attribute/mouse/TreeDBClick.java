package com.hhdb.csadmin.plugin.sql_book.attribute.mouse;

import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.util.TreeNodeUtil;

/**
 * 鼠标双击处理
 */
public class TreeDBClick {
	private BooksPanel book;
	public TreeDBClick(BooksPanel book){
		this.book = book;
	}
	public void execute(BaseTreeNode treeNode,Boolean bool) {
		if(treeNode.getChildCount()==0){
			String type = treeNode.getType();
			if(type.equals(TreeNodeUtil.ROOT_TYPE)) {
				book.sqls.refresh(treeNode,bool);
			}else if(type.equals(TreeNodeUtil.FILE_TYPE)){
				book.sqls.refresh(treeNode,bool);
			}
		}
	}
}
