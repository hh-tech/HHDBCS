





package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.ui.script.ExescriptPanel;

/**
 * 表右键菜单
 * 
 * @author hyz
 * 
 */
public class TableMenu extends BasePopupMenu  implements TableInterface{
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public TableMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("执行脚本", "runScript", this));
		addSeparator();
		add(createMenuItem("打开表", TABLEDATA, this));
		add(createMenuItem("设计表", DESIGNDATA,  this));
		add(createMenuItem("新建表", CREATETABLE, this));
		addSeparator();
		add(createMenuItem("清空表", CLEARDATA, this));
		add(createMenuItem("级联清空表", "CASCACLEARDATA", this));
		add(createMenuItem("删除表", DELETETABLE, this));
		add(createMenuItem("强制删除表", CASCADEDELETETABLE, this));
		addSeparator();
		add(createMenuItem("复制表", COPYTABLE, this));
		add(createMenuItem("粘贴表", PASTETABLE, this));
//		BaseMenuItem showmap=createMenuItem("查看GIS地图", SHOWGISMAP, this);
////		if(!CommonDAO.isGisMap(treeNode)){
////			showmap.setEnabled(false);
////		}
//		add(showmap);
		
//		add(createMenuItem("导入向导", IMPBACKUP, this));
//		add(createMenuItem("导出向导", IMPRESTORE, this));
		addSeparator();
		add(createMenuItem("重命名", RENAME, this));
	}
	
	public  TableMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		String actionCmd = e.getActionCommand();
		if(actionCmd.equals("runScript")){
			try {
				if(!ScriptService.checkTableEst()){
					ScriptService.initTable();
				}
				String flagName = treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()
						+"."+treeNode.getMetaTreeNodeBean().getName();
				String toId = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent tabPanelEvent = new CmdEvent(htree.PLUGIN_ID, toId, "AddPanelEvent");
				tabPanelEvent.addProp("TAB_TITLE", "表脚本执行("+flagName+")");
				tabPanelEvent.addProp("COMPONENT_ID", "script_"+ScriptService.tabletype+flagName);
				tabPanelEvent.addProp("ICO", "start.png");
				tabPanelEvent.setObj(new ExescriptPanel(htree,treeNode,ScriptService.tabletype));
				htree.sendEvent(tabPanelEvent);
			} catch (SQLException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}		
		else if(actionCmd.equals(TABLEDATA)){
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.table_open", "tableOpenEvent");
			
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean)revent.getObj();
			
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().
					getMetaTreeNodeBean().getName());
			event.addProp("tableName", treeNode.getMetaTreeNodeBean().getName());
			event.addProp("ico", "");
			HHEvent re = htree.sendEvent(event);
			if(re instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent) re).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(DESIGNDATA)){
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.table_operate", "TableEditMainEvent");
			event.addProp("tableoId", treeNode.getMetaTreeNodeBean().getId()+"");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().
					getMetaTreeNodeBean().getName());
			event.addProp("tableName", treeNode.getMetaTreeNodeBean().getName());
			HHEvent re = htree.sendEvent(event);
			if(re instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent) re).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(actionCmd.equals(CREATETABLE)){
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.table_operate", "TableCreateEvent");
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			htree.sendEvent(event);
		}
		else if(actionCmd.equals(CLEARDATA)){

			int n = JOptionPane.showConfirmDialog(null, "这将清空表数据，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				String sql = "TRUNCATE TABLE \""+
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()+
						"\".\""+treeNode.getMetaTreeNodeBean().getName()+"\"";
				try {
					htree.treeService.executeSql(sql);
					JOptionPane.showMessageDialog(null, "数据已清空!", "消息", JOptionPane.INFORMATION_MESSAGE);
				}
				 catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(actionCmd.equals("CASCACLEARDATA")){
			int n = JOptionPane.showConfirmDialog(null, "这将清空表数据以及外键连接的表数据，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				String sql = "TRUNCATE TABLE \""+
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()+
						"\".\""+treeNode.getMetaTreeNodeBean().getName()+"\" CASCADE";
				try {
					htree.treeService.executeSql(sql);
					JOptionPane.showMessageDialog(null, "数据已清空!", "消息", JOptionPane.INFORMATION_MESSAGE);
				}
				 catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(actionCmd.equals(DELETETABLE)){
			int n = JOptionPane.showConfirmDialog(null, "这将删除表数据及结构，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				String sql = "drop TABLE \""+
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()+
						"\".\""+treeNode.getMetaTreeNodeBean().getName()+"\"";
				try {
					htree.treeService.executeSql(sql);
					JOptionPane.showMessageDialog(null, "表已经删除!", "消息", JOptionPane.INFORMATION_MESSAGE);
					htree.treeService.refreshTable(treeNode.getParentBaseTreeNode());
				}
				 catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(actionCmd.equals(CASCADEDELETETABLE)){
			int n = JOptionPane.showConfirmDialog(null, "这将强制删除表数据及结构并且忽略外键连接的表，你确定想要继续吗？", "温馨提示", JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				String sql = "drop TABLE \""+
						treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()+
						"\".\""+treeNode.getMetaTreeNodeBean().getName()+ "\" CASCADE";
				try {
					htree.treeService.executeSql(sql);
					JOptionPane.showMessageDialog(null, "表已经删除!", "消息", JOptionPane.INFORMATION_MESSAGE);
					htree.treeService.refreshTable(treeNode.getParentBaseTreeNode());
				}
				 catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		else if(actionCmd.equals(COPYTABLE)){
			htree.treeService.copySchemaName = treeNode.getParentBaseTreeNode().
					getParentBaseTreeNode().getMetaTreeNodeBean().getName();
			htree.treeService.copyTableName = treeNode.getMetaTreeNodeBean().getName();
			JOptionPane.showMessageDialog(null, htree.treeService.copyTableName+"表已复制", "消息", JOptionPane.INFORMATION_MESSAGE);
		}
		else if(actionCmd.equals(PASTETABLE)){
			if(htree.treeService.copyTableName==null||htree.treeService.copyTableName.trim().equals("")){
				JOptionPane.showMessageDialog(null, "没有复制的表", "消息", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			String tablename = htree.treeService.copyTableName;
			int schemaid = 	treeNode.getParentBaseTreeNode().
					getParentBaseTreeNode().getMetaTreeNodeBean().getId();
			String schemaName = treeNode.getParentBaseTreeNode().
					getParentBaseTreeNode().getMetaTreeNodeBean().getName();
			
			String sql;
			try {
				String tabnameNew = htree.treeService.pdTableName(tablename,schemaid,0);
				sql = "select * into \""+schemaName+"\".\""+tabnameNew+"\" from \""+htree.treeService.copySchemaName+"\".\""+tablename+"\"";
				htree.treeService.executeDQL(sql);
				JOptionPane.showMessageDialog(null, "粘贴成功！", "消息", JOptionPane.INFORMATION_MESSAGE);
				htree.treeService.refreshTable(treeNode.getParentBaseTreeNode());
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(null, e2.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		else if(actionCmd.equals(SHOWGISMAP)){
			
		}
		else if(actionCmd.equals(IMPBACKUP)){
			
		}
		else if(actionCmd.equals(IMPRESTORE)){
			
		}
		else if(actionCmd.equals(RENAME)){
			String initialName = treeNode.getMetaTreeNodeBean().getName();
			String tableName = (String)JOptionPane.showInputDialog(null, "输入表名", "表名", JOptionPane.PLAIN_MESSAGE,null,null,initialName);
			System.out.println(tableName);
			if(tableName!=null){
					if(tableName.equals(treeNode.getMetaTreeNodeBean().getName())){
						return;
					}
					String sql="ALTER TABLE \""+treeNode.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName()
							+"\".\""+treeNode.getMetaTreeNodeBean().getName()+"\" RENAME TO \""+tableName+"\"";
					try {
						htree.treeService.executeSql(sql);
						treeNode.getMetaTreeNodeBean().setName(tableName);
						// 重新加载节点
						htree.getTree().getTreeModel().reload(treeNode);
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
					}
			}
		}
	}
	
}
