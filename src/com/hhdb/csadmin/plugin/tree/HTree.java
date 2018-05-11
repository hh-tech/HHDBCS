package com.hhdb.csadmin.plugin.tree;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreeSelectionModel;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.tree.been.MetaTreeNodeBean;
import com.hhdb.csadmin.plugin.tree.service.TreeService;
import com.hhdb.csadmin.plugin.tree.ui.BaseTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeCellRenderer;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.ui.MouseHandler;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;
/**
 * 
 * @author hyz
 *
 */
public class HTree extends AbstractPlugin {

	BaseTree tree;
	JScrollPane scrollPane;
	public TreeService treeService;
	public String PLUGIN_ID = HTree.class.getPackage().getName();

	public HTree() {
		treeService = new TreeService(this);
	}
	
	private void initTree() throws Exception{
		MetaTreeNodeBean mtn = new MetaTreeNodeBean();
		
		CmdEvent getsbEvent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean)revent.getObj();
		
		mtn.setName("服务器:" + serverbean.getHost());
		mtn.setOpenIcon("localhost.png");
		mtn.setType(TreeNodeUtil.SERVER_TYPE);
		BaseTreeNode root = new BaseTreeNode(mtn,this);
		tree = new BaseTree(root);
		scrollPane=new JScrollPane();
		
		CmdEvent event = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "getSuperuser");
		HHEvent ev = sendEvent(event);
		String superuser_value = ev.getValue("superuser_value");
		
		BaseTreeNode[] btns = null;
		if(superuser_value.equals("true")){
			btns = treeService.InitTreeNode("com/hhdb/csadmin/plugin/tree/xml/tree_super.xml");
		}else{
			btns = treeService.InitTreeNode("com/hhdb/csadmin/plugin/tree/xml/tree_comm.xml");
		}
		for (BaseTreeNode btn : btns) {
			tree.getRootTreeNode().add(btn);
			root.getChildNode().add(btn);
		}
		tree.setCellRenderer(new BaseTreeCellRenderer());
		tree.addMouseListener(new MouseHandler(tree,this));
		tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		scrollPane.setViewportView(tree);
	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent relEvent= EventUtil.getReplyEvent(HTree.class, event);
		if(event.getType().equals(EventTypeEnum.GET_OBJ.name())){
			try {
				initTree();
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
			relEvent.setObj(scrollPane);
			return relEvent;
		}else if(event.getType().equals(EventTypeEnum.COMMON.name())){
			String oid = "";
			String name = "";
			try{
				BaseTreeNode treeNode = (BaseTreeNode) tree.getLastSelectedPathComponent();
				MetaTreeNodeBean metatreenodebean = treeService.getSchemaMetaTreeNodeBean(treeNode);
				if(metatreenodebean!=null){
					oid = metatreenodebean.getId()+"";
					name = metatreenodebean.getName();
				}
				relEvent.addProp("schemaid_str", oid);
				relEvent.addProp("schemaname_str", name);
				return relEvent;
			}catch(Exception e){
				LM.error(LM.Model.CS.name(), e);
				ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
						event.getFromID(),
						ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
				errorEvent.setErrorMessage(PLUGIN_ID + "异常:\n"
						+ e.getMessage());
				return errorEvent;
			}
		}else if(event.getType().equals(EventTypeEnum.CMD.name())){
			if(event.getValue(EventTypeEnum.CMD.name()).equals("ChangeTreeEvent")){
				String host_str = event.getValue("host_str");
				String port_str = event.getValue("port_str");
				String dbname_str = event.getValue("dbname_str");
				String username_str = event.getValue("username_str");
				String pass_str = event.getValue("pass_str");
				String superuser_value = event.getValue("superuser_value");
				
				CmdEvent toEvent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "SetConn");
				toEvent.addProp("host_str", host_str);
				toEvent.addProp("port_str", port_str);
				toEvent.addProp("dbname_str", dbname_str);
				toEvent.addProp("username_str", username_str);
				toEvent.addProp("pass_str", pass_str);
				toEvent.addProp("superuser_value", superuser_value);
				sendEvent(toEvent);
				try {
					initTree();
					CmdEvent cmdevent = new CmdEvent(PLUGIN_ID, "com.hhdb.csadmin.plugin.main", "getLeftPane");
					HHEvent getLeftPaneEvent = sendEvent(cmdevent);
					JPanel leftJpanel = (JPanel)getLeftPaneEvent.getObj();
					leftJpanel.removeAll();
					leftJpanel.add(scrollPane);
					leftJpanel.updateUI();
					String toID = "com.hhdb.csadmin.plugin.tabpane";
					CmdEvent cleanEvent = new CmdEvent(PLUGIN_ID, toID, "CleanEvent");
					CmdEvent flushAttributeEvent = new CmdEvent(PLUGIN_ID, toID, "flushAttributeEvent");
					sendEvent(flushAttributeEvent);
					sendEvent(cleanEvent);
					treeService.sendAttr("","");
					treeService.refreshToolBar(superuser_value);
					
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				}
				
			}
			else if(event.getValue(EventTypeEnum.CMD.name()).equals("RefreshAddTreeNodeEvent")){
				String schemaName = event.getValue("schemaName");
				String treenode_type = event.getValue("treenode_type");
				try{
					treeService.refreshTreeNodeBySchemaName(schemaName, treenode_type);
				}catch(Exception e){
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage(PLUGIN_ID + "异常:\n"
							+ e.getMessage());
					return errorEvent;
				}
			}else if(event.getValue(EventTypeEnum.CMD.name()).equals("RefreshTreeNodeByQueryDropEvent")){
				//String treenode_type = event.getValue("treenode_type");
				try{
					treeService.refreshTreeNodeByQueryDrop();
				}catch(Exception e){
					LM.error(LM.Model.CS.name(), e);
					ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
							event.getFromID(),
							ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
					errorEvent.setErrorMessage(PLUGIN_ID + "异常:\n"
							+ e.getMessage());
					return errorEvent;
				}
			}
			return relEvent;
		}
		else{
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,
					event.getFromID(),
					ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下类型的事件:\n"
					+ event.toString());
			return errorEvent;
		}
	}

	@Override
	public Component getComponent() {
		return scrollPane;
	}
	
	public BaseTree getTree(){
		return tree;
	}
	
}
