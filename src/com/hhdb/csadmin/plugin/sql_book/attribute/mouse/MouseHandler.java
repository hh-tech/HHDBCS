package com.hhdb.csadmin.plugin.sql_book.attribute.mouse;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.tree.TreePath;

import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.ui.BaseTree;
import com.hhdb.csadmin.plugin.sql_book.util.ThreadUtils;

/**
 * 树状图鼠标事件
 * @author hhxd
 *
 */
public class MouseHandler extends MouseAdapter {
	private BaseTree tree = null;
	private RightClick rightClick;
	private TreeDBClick treedb;
	private TreeClick treeclick;
	private BooksPanel book;
	private Boolean bool = true;   //控制鼠标点击
	
	public MouseHandler(BaseTree tree,BooksPanel book,Boolean bool){
//		this.book = book;
		this.tree = tree;
		this.bool = bool;
		this.book = book;
		rightClick = new RightClick(book);
		treedb = new TreeDBClick(book);
		treeclick = new TreeClick(book);
	}
	
	@Override
	public void mouseClicked(final MouseEvent e) {
		int selRow = tree.getRowForLocation(e.getX(), e.getY());   //获取点击的行
		boolean condition = true;
		condition = condition && (selRow != -1); 		// 如果选中
		if(bool && book.source){
			//右键
			if (condition && e.getButton() == 3) {
				int count = tree.getSelectionCount();
				TreePath selPath = tree.getPathForLocation(e.getX(), e.getY()); // 返回指定节点的树路径
					
				TreePath[] ts = tree.getSelectionPaths();
				boolean flag = false;
				if( null != ts ){
					for(TreePath flagtp:ts){
						if(selPath.equals(flagtp)){
							flag = true;
							break;
						}
					}
				}else{
					tree.setSelectionPath(selPath);  //显示右击行
				}
				
				if(count>1&&flag){  //选择多个目标时
				
				}else{
					BaseTreeNode treeNode = (BaseTreeNode)selPath.getLastPathComponent();
					tree.setSelectionPath(selPath);  //显示右击行
					rightClick.rightClickTreeNode(treeNode,e);
				}
				return;
			}
		}
		//双击
		if (condition && e.getClickCount() == 2) {
			TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			final BaseTreeNode treeNode = (BaseTreeNode)selPath.getLastPathComponent();
			if(treeNode != null){
				treedb.execute(treeNode,bool);
			}
			return;
		}
		//单击
		if (condition && e.getClickCount() == 1) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					TreePath selPath = tree.getPathForLocation(e.getX(),e.getY());
					final BaseTreeNode treeNode = (BaseTreeNode)selPath.getLastPathComponent();
					treeclick.execute(treeNode,bool);
				}
			});
			
		}
	}

}
