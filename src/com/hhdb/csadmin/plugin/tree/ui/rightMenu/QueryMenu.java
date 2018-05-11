package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.cmd.console.CommonsHelper;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;

/**
 * 查询下右键菜单
 * @author huyuanzhui
 *
 */
public class QueryMenu extends BasePopupMenu{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;
	private  final String OPENQUERY = "openquery";
	private  final String CREATEQUERY = "createquery";
	private  final String DELQUERY = "delquery";
	
	private HTree htree = null;
	
	public QueryMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("打开查询", OPENQUERY, this));
		add(createMenuItem("新建查询", CREATEQUERY, this));
		add(createMenuItem("删除查询", DELQUERY, this));
	}
	
	//ViewTabPanelHandle vtpl = new ViewTabPanelHandle();
	public QueryMenu getInstance(BaseTreeNode node) {
		treeNode = node;
		return this;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if(actionCmd.equals(OPENQUERY)){
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean)revent.getObj();
			
			File file = new File(CommonsHelper.getClassPath()
					+ "/db/servers/" + serverbean.getHost() + "/"
					+ serverbean.getDBName() + "/"
					+ treeNode
					.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName() + "/"+treeNode.getMetaTreeNodeBean().getName()+".sql");
			try {
				String text = FileUtils.readFileToString(file, "utf-8");
				CmdEvent toevent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.query", "initText");
				toevent.addProp("Text", text);
				htree.sendEvent(toevent);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "错误:",
						JOptionPane.ERROR_MESSAGE);
			}
		}else if(actionCmd.equals(CREATEQUERY)){
			String toID = "com.hhdb.csadmin.plugin.query";
			HHEvent hhEvent = new HHEvent(htree.PLUGIN_ID, toID, EventTypeEnum.COMMON.name());
			htree.sendEvent(hhEvent);
		}else if(actionCmd.equals(DELQUERY)){
			CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
			HHEvent revent = htree.sendEvent(getsbEvent);
			ServerBean serverbean = (ServerBean)revent.getObj();
			
			File file = new File(CommonsHelper.getClassPath()
					+ "/db/servers/" + serverbean.getHost() + "/"
					+ serverbean.getDBName() + "/"
					+ treeNode
					.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName() + "/"+treeNode.getMetaTreeNodeBean().getName()+".sql");
			file.delete();
			try {
				htree.treeService.refreshQuery(treeNode.getParentBaseTreeNode());
			} catch (Exception e1) {
				LM.error(LM.Model.CS.name(), e1);
			}
		}
	}
}
