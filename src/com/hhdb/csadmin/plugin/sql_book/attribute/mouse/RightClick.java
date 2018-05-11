 package com.hhdb.csadmin.plugin.sql_book.attribute.mouse;

import java.awt.event.MouseEvent;

import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.ui.basis.BasePopupMenu;
import com.hhdb.csadmin.plugin.sql_book.ui.basis.FileMenu;
import com.hhdb.csadmin.plugin.sql_book.ui.basis.QuickMenu;
import com.hhdb.csadmin.plugin.sql_book.ui.basis.RootMenu;
import com.hhdb.csadmin.plugin.sql_book.ui.basis.SQLMenu;
import com.hhdb.csadmin.plugin.sql_book.util.TreeNodeUtil;

/**
 * 鼠标右击处理
 * 
 */
public class RightClick {
	private BooksPanel book;
	private RootMenu rootMenu;
	private FileMenu fileMenu;
	private SQLMenu sqlMenu;
	private QuickMenu quickMenu;
	
	
	
	BasePopupMenu popupMenu = null;
	
    public RightClick(BooksPanel book){
    	this.book = book;
    }		
    
    
    /**
     * 右键树节点,弹出菜单栏
     * @param treeNode
     */
	public void rightClickTreeNode(BaseTreeNode treeNode,MouseEvent e) {
		String type = treeNode.getType();
		BasePopupMenu popupMenu = null;
		
		if(type.equals(TreeNodeUtil.ROOT_TYPE)) {
			if(rootMenu==null){
				rootMenu = new RootMenu(book);
			}
			popupMenu =rootMenu.getInstance(treeNode);
		}else if(type.equals(TreeNodeUtil.FILE_TYPE)){
			if(fileMenu==null){
				fileMenu = new FileMenu(book);
			}
			popupMenu =fileMenu.getInstance(treeNode);
		}else if(type.equals(TreeNodeUtil.SQL_TYPE)) {
			if(sqlMenu==null){
				sqlMenu = new SQLMenu(book);
			}
			popupMenu =sqlMenu.getInstance(treeNode);
		}else if(type.equals(TreeNodeUtil.QUICK_TYPE)) {
			if(quickMenu==null){
				quickMenu = new QuickMenu(book);
			}
			popupMenu =quickMenu.getInstance(treeNode);
		}
		if (popupMenu != null) {
			popupMenu.showPopup(e.getComponent(), e.getX(), e.getY());
		}
	}
	

}
