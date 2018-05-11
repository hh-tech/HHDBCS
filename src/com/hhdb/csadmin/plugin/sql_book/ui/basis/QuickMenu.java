package com.hhdb.csadmin.plugin.sql_book.ui.basis;

import java.awt.event.ActionEvent;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hhdb.csadmin.common.util.HSQL_Util;
import com.hhdb.csadmin.common.util.VmUtil;
import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.ui.ShortcutPanel;

/**
 * 快捷方式节点右键菜单
 * @author hhxd
 *
 */
public class QuickMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;

	private BooksPanel book = null;
	
	public QuickMenu(BooksPanel book){
		this.book = book;
		add(createMenuItem("重命名", "update", this));
		add(createMenuItem("在查询器打开", "query", this));
		add(createMenuItem("删除", "delete", this));
	}
	
	public  QuickMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		try {
			if (actionCmd.equals("update")) {
				String initialName = treeNode.getMetaTreeNodeBean().getName();
				String name = (String)JOptionPane.showInputDialog(null, "输入快捷方式名", "名称", JOptionPane.PLAIN_MESSAGE,null,null,initialName);
				if(null == name || name.equals("")){
        			JOptionPane.showMessageDialog(null, "名称非法", "消息",JOptionPane.ERROR_MESSAGE);
        			return;
        		}
				if(!name.equals(initialName)){
					//验证重复
					if (book.objtool.nameExist(HSQL_Util.getConnection(), treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId(), name)){
						JOptionPane.showMessageDialog(null, "名称重复", "错误", JOptionPane.ERROR_MESSAGE);
					}else{
						book.linkTool.updateNameObjLink(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId(), name,treeNode.getMetaTreeNodeBean().getOriginalId());
						//刷新父节点
						book.sqls.refresh(treeNode.getParentBaseTreeNode(),true);
						//关闭详情面板
						book.sqlDetail.getViewport().add(new JLabel());
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
			}else if(actionCmd.equals("delete")){
				int n = JOptionPane.showConfirmDialog(null, "删除快捷方式，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					book.linkTool.forceDelete(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId());
					//刷新父节点
					book.sqls.refresh(treeNode.getParentBaseTreeNode(),true);
					//关闭详情面板
					book.sqlDetail.getViewport().add(new JLabel());
				}
			}
		} catch (Exception e1) {
			LM.error(LM.Model.CS.name(), e1);
			JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
		}
	}
}
