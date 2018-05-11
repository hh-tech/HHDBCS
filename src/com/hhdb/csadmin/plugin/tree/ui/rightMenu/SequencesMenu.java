package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;


/**
 * 序列右键菜单
 * 
 * @author hyz
 * 
 */
public class SequencesMenu extends BasePopupMenu {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private  BaseTreeNode treeNode;
    private  final String REFRESH = "refresh";
    private  final String CREATESEQUENCE = "createSequence";
   
	private HTree htree = null;
	
	public SequencesMenu(HTree htree){
		this.htree = htree;
		add(createMenuItem("新建序列", CREATESEQUENCE, this));
        add(createMenuItem("刷新", REFRESH, this));
	}
    
    public  SequencesMenu getInstance(BaseTreeNode node) {
        treeNode = node;
        return this;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCmd = e.getActionCommand();
        CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent rsbevent = htree.sendEvent(getsbEvent);
		ServerBean serverbean = (ServerBean)rsbevent.getObj();
        
        if (actionCmd.equals(REFRESH)) {// 刷新
        	try {
				htree.treeService.refreshSequence(treeNode);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), "消息",
						JOptionPane.ERROR_MESSAGE);
			}
        } else if (actionCmd.equals(CREATESEQUENCE)) {// 新建序列
        	String toID = "com.hhdb.csadmin.plugin.sequence";
			CmdEvent event = new CmdEvent(htree.PLUGIN_ID, toID, "createSequenceEvent");
			event.addProp("databaseName", serverbean.getDBName());
			event.addProp("schemaName", treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName());
			HHEvent revent = htree.sendEvent(event);
			if(revent instanceof ErrorEvent){
				JOptionPane.showMessageDialog(null, ((ErrorEvent)revent).getErrorMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
        }
    }

}
