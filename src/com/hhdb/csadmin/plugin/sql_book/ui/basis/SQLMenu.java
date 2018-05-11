package com.hhdb.csadmin.plugin.sql_book.ui.basis;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.util.VmUtil;
import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.ui.ShortcutPanel;

/**
 * sql节点右键菜单
 * @author hhxd
 *
 */
public class SQLMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;

	private BooksPanel book = null;
	
	public SQLMenu(BooksPanel book){
		this.book = book;
		add(createMenuItem("删除", "delete", this));
		add(createMenuItem("在查询器打开", "query", this));
		add(createMenuItem("设置快捷方式", "shortcut", this));
	}
	
	public  SQLMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("delete")) {
			int n = JOptionPane.showConfirmDialog(null, "删除SQL语句将一并删除对应的快捷方式，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				//删除sql
				synchronized (treeNode) {
					book.sqls.deleteSql(treeNode);
				}
			}
		} else if (actionCmd.equals("query")) {
			try {
				//判断是否需要替换参数
				Set<String> set = VmUtil.getProNameByVmStr(treeNode.getMetaTreeNodeBean().getTxt());
				if(set.size() != 0){
					new ShortcutPanel("参数替换",book.sqls.getBaseFrame(),book,500,300,set,treeNode.getMetaTreeNodeBean().getTxt());
				}else{
					book.sqls.getQuery(treeNode.getMetaTreeNodeBean().getTxt());
				}
			} catch (Exception e1) {
				LM.error(LM.Model.CS.name(), e1);
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		} else if (actionCmd.equals("shortcut")) {
			new ShortcutPanel(book,treeNode.getMetaTreeNodeBean().getId(),"选择快捷方式设置地址",800,400,book.sqls.getBaseFrame());
		}
	}
}
