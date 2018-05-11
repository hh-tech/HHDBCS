package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.dbobj.hhdb.HHdbDatabase;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.service.ScriptService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.ui.script.ExescriptPanel;


/**
 * 数据库实例右键菜单
 * 
 * @author huyuanzhui
 * 
 */
public class DBMenu extends BasePopupMenu {
	private static final long serialVersionUID = 1L;
	private BaseTreeNode treeNode;
	
	private HTree htree = null;
	
	public DBMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("执行脚本", "runScript", this));
		addSeparator();
		add(createMenuItem("新建数据库", "adddb", this));
		add(createMenuItem("删除数据库", "delete", this));
	}

	public DBMenu getInstance(BaseTreeNode node) {
		
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
				String flagName = treeNode.getMetaTreeNodeBean().getName();
				String toId = "com.hhdb.csadmin.plugin.tabpane";
				CmdEvent tabPanelEvent = new CmdEvent(htree.PLUGIN_ID, toId, "AddPanelEvent");
				tabPanelEvent.addProp("TAB_TITLE", "数据库脚本执行("+flagName+")");
				tabPanelEvent.addProp("COMPONENT_ID", "script_"+ScriptService.dbtype+flagName);
				tabPanelEvent.addProp("ICO", "start.png");
				tabPanelEvent.setObj(new ExescriptPanel(htree,treeNode,ScriptService.dbtype));
				htree.sendEvent(tabPanelEvent);
			} catch (SQLException e1) {
				LM.error(LM.Model.CS.name(), e1);
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}		
		else if (actionCmd.equals("adddb")) {
			CmdEvent addevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.database", "add");
			htree.sendEvent(addevent);
		}
		else if (actionCmd.equals("delete")) {
			CmdEvent getconnEvent = new CmdEvent(htree.PLUGIN_ID,"com.hhdb.csadmin.plugin.conn", "GetConn");
			HHEvent refevent = htree.sendEvent(getconnEvent);
			if(!(refevent instanceof ErrorEvent)){
				Connection conn = (Connection)refevent.getObj();
				HHdbDatabase hdb = new HHdbDatabase(conn,treeNode.getMetaTreeNodeBean().getName(), true,StartUtil.prefix);
				try {
					hdb.drop();
					JOptionPane.showMessageDialog(null,"删除成功！", "消息",
							JOptionPane.INFORMATION_MESSAGE);
					htree.treeService.refreshDBCollection(treeNode.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null,e1.getMessage(), "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}else{
				JOptionPane.showMessageDialog(null,((ErrorEvent)refevent).getErrorMessage(), "错误",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
