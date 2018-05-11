package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


public class ViewsMenu extends BasePopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  final String CREATEVIEW = "createView";
	private  final String REFRESH = "refresh";
	private  final String PASTEVIEW = "pasteview";
	private   BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public ViewsMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建视图", CREATEVIEW, this));
		add(createMenuItem("粘贴视图", PASTEVIEW, this));
		add(createMenuItem("刷新",REFRESH,this));
	}
	
	public   ViewsMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent rsbevent = htree.sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean)rsbevent.getObj();
		
		if (actionCmd.equals(REFRESH)) {
			try {
				htree.treeService.refreshView(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		} else if (actionCmd.equals(CREATEVIEW)) {
			String toID = "com.hhdb.csadmin.plugin.view";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "viewAddEvent");
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(PASTEVIEW)){
			if(htree.treeService.copyViewName==null||htree.treeService.copyViewName.trim().equals("")){
				JOptionPane.showMessageDialog(null, "没有复制的视图", "消息", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String viewname = htree.treeService.copyViewName;
			int schemaid = 	treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId();
			String schemaName = treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName();
			
			try {
				String viewnameNew = htree.treeService.pdTableName(viewname,schemaid,0);
				
				Object[] params = new Object[1];
				params[0] = htree.treeService.copyViewId;

				List<Map<String, Object>> list = htree.treeService.getListMapBySql
						(HHSqlUtil.getSqlBean(HHSqlUtil.ITEM_TYPE.VIEW, "prop")
								.replaceParams(params));
				StringBuffer sb = new StringBuffer();
				if(list.size()>0){
					Map<String, Object> valueMap = list.get(0);
					String definition = valueMap.get("definition") + "";
					definition = definition.substring(definition.indexOf("SELECT",definition.indexOf("SELECT")+6));
					sb.append(String.format("CREATE OR REPLACE VIEW %s AS \n", "\""+schemaName+"\".\""+viewnameNew+"\""));
					sb.append(definition);
				}
				if(sb.length()==0){
					JOptionPane.showMessageDialog(null, "粘贴失败", "消息", JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				htree.treeService.executeSql(sb.toString());;
				JOptionPane.showMessageDialog(null, "粘贴成功！", "消息", JOptionPane.INFORMATION_MESSAGE);
				htree.treeService.refreshView(treeNode);
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, e2.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

}
