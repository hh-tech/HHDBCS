package com.hhdb.csadmin.plugin.sql_book.ui.basis;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.ui.SqlOperationPanel;

/**
 * 根节点右键菜单
 * @author hhxd
 *
 */
public class RootMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;

	private BooksPanel book = null;
	
	public RootMenu(BooksPanel book){
		this.book = book;
		add(createMenuItem("新建文件夹", "newfile", this));
		add(createMenuItem("新建SQL", "newSQL", this));
		add(createMenuItem("刷新", "refresh", this));
		
	}
	
	public  RootMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("newfile")) {
			String name = (String)JOptionPane.showInputDialog(null, "输入文件夹名", "名称", JOptionPane.PLAIN_MESSAGE,null,null,null);
			if(null != name && !name.equals("")){
				book.sqls.newFolder(treeNode,name);
			}
		}else if (actionCmd.equals("newSQL")) {
			//打开设置面板
			book.sqlDetail.getViewport().add(new SqlOperationPanel(book,treeNode,"新建SQL"));
		}else if (actionCmd.equals("refresh")) {
			book.sqls.refresh(treeNode,true);
		}
	}
}
