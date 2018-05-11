package com.hhdb.csadmin.plugin.tree.ui;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj.hhdb.HHdbCk;
import com.hh.frame.dbobj.hhdb.HHdbDatabase;
import com.hh.frame.dbobj.hhdb.HHdbFk;
import com.hh.frame.dbobj.hhdb.HHdbFun;
import com.hh.frame.dbobj.hhdb.HHdbIndex;
import com.hh.frame.dbobj.hhdb.HHdbPk;
import com.hh.frame.dbobj.hhdb.HHdbSchema;
import com.hh.frame.dbobj.hhdb.HHdbSeq;
import com.hh.frame.dbobj.hhdb.HHdbTabSp;
import com.hh.frame.dbobj.hhdb.HHdbTable;
import com.hh.frame.dbobj.hhdb.HHdbUk;
import com.hh.frame.dbobj.hhdb.HHdbUser;
import com.hh.frame.dbobj.hhdb.HHdbView;
import com.hh.frame.dbobj.hhdb.HHdbXk;
import com.hh.frame.dbobj.base.AbsBase;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.been.MetaTreeNodeBean;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;


public class BaseTreeNode extends DefaultMutableTreeNode implements Cloneable{
	private static final long serialVersionUID = 1L;
	private MetaTreeNodeBean nodeBean;
	private Map<String, Object> attrMap = new HashMap<String, Object>();
	protected boolean isSelected; 
	protected boolean isEnabled=true;
	private List<BaseTreeNode> nodes = new ArrayList<BaseTreeNode>();
	private BaseTreeNode parentBaseTreeNode = null;
	private HTree htree;
	public HTree getHtree() {
		return htree;
	}
	private Object nodeObject;
	Connection conn = null;

	public BaseTreeNode(HTree htree) {
		this.setUserObject(new JLabel());
		this.htree = htree;
	}

    public BaseTreeNode(Object userObject , HTree htree)  
    {  
        this(userObject, true, false , htree);
        this.htree = htree;
    }  
      
    public BaseTreeNode(Object userObject, boolean allowsChildren, boolean isSelected , HTree htree)  
    {  
        super(userObject, allowsChildren);  
        this.isSelected = isSelected;
        this.htree = htree;
    }  
	public BaseTreeNode(MetaTreeNodeBean nodeBean , HTree htree) {
		this.setUserObject(new JLabel());
		this.nodeBean = nodeBean;
		this.htree = htree;
	}
	
	public Object getNodeObject(){		
		if(nodeObject==null){
			CmdEvent getconnEvent = new CmdEvent(htree.PLUGIN_ID,"com.hhdb.csadmin.plugin.conn", "GetConn");
			HHEvent refevent = htree.sendEvent(getconnEvent);
			if(!(refevent instanceof ErrorEvent)){
				conn = (Connection)refevent.getObj();
			}
			getNodeObjectLj();

		}else{
			try {
				if(conn==null||conn.isClosed()||!ConnUtil.isConnected(conn)){
					CmdEvent getconnEvent = new CmdEvent(htree.PLUGIN_ID,"com.hhdb.csadmin.plugin.conn", "GetConn");
					HHEvent refevent = htree.sendEvent(getconnEvent);
					if(!(refevent instanceof ErrorEvent)){
						conn = (Connection)refevent.getObj();
					}
					((AbsBase)nodeObject).setConn(conn);
//					getNodeObjectLj();
				}
			} catch (SQLException e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
		return nodeObject;
	}
	
	private void getNodeObjectLj(){
		if(nodeBean.getType().equals(TreeNodeUtil.DB_ITEM_TYPE)) {
			nodeObject = new HHdbDatabase(conn, nodeBean.getName(), true,StartUtil.prefix);
		}else if(nodeBean.getType().equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
			
			nodeObject = new HHdbSchema(conn, nodeBean.getName(), true,StartUtil.prefix);
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
			
			nodeObject = new HHdbTable(conn, 
					parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(), true,StartUtil.prefix);

		}else if(nodeBean.getType().equals(TreeNodeUtil.COL_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.CONSTRAINT_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.CONSTRAINT_PK_ITEM_TYPE)) {
			nodeObject = new HHdbPk(conn, 
					parentBaseTreeNode.getParentBaseTreeNode().getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(),true,StartUtil.prefix);
		}else if(nodeBean.getType().equals(TreeNodeUtil.CONSTRAINT_FK_ITEM_TYPE)) {
			nodeObject = new HHdbFk(conn, 
					parentBaseTreeNode.getParentBaseTreeNode().getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(),true,StartUtil.prefix);
		}else if(nodeBean.getType().equals(TreeNodeUtil.CONSTRAINT_UK_ITEM_TYPE)) {
			nodeObject = new HHdbUk(conn, 
					parentBaseTreeNode.getParentBaseTreeNode().getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(),true,StartUtil.prefix);
		}else if(nodeBean.getType().equals(TreeNodeUtil.CONSTRAINT_CK_ITEM_TYPE)) {
			nodeObject = new HHdbCk(conn, 
					parentBaseTreeNode.getParentBaseTreeNode().getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(),true,StartUtil.prefix);
		}else if(nodeBean.getType().equals(TreeNodeUtil.CONSTRAINT_XK_ITEM_TYPE)) {
			nodeObject = new HHdbXk(conn, 
					parentBaseTreeNode.getParentBaseTreeNode().getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(),true,StartUtil.prefix);
		}else if(nodeBean.getType().equals(TreeNodeUtil.INDEX_ITEM_TYPE)) {
			
			nodeObject = new HHdbIndex(conn, 
					parentBaseTreeNode.getParentBaseTreeNode().getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(),true,StartUtil.prefix);
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.INDEX_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.RULE_TYPE)) {			
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.TABLE_TRIGGER_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.VIEW_TYPE)) {
			

		}else if(nodeBean.getType().equals(TreeNodeUtil.VIEW_ITEM_TYPE)) {
			nodeObject = new HHdbView(conn, parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(), true,StartUtil.prefix);

		}else if(nodeBean.getType().equals(TreeNodeUtil.SEQ_TYPE)) {			

		}else if(nodeBean.getType().equals(TreeNodeUtil.SEQ_ITEM_TYPE)) {
			nodeObject = new HHdbSeq(conn, parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getName(), true,StartUtil.prefix);

		}else if(nodeBean.getType().equals(TreeNodeUtil.FUN_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.FUN_ITEM_TYPE)) {
			nodeObject = new HHdbFun(conn, parentBaseTreeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getName(),
					nodeBean.getId()+"", true,StartUtil.prefix);

		}else if(nodeBean.getType().equals(TreeNodeUtil.TYPE_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.TYPE_ITEM_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.SELECT_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.SELECT_ITEM_TYPE)) {

		}else if(nodeBean.getType().equals(TreeNodeUtil.EXTENSION_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.EXTENSION_PLUGIN_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.PERFORMANCE_MONITORING_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.TAB_SPACE_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.TAB_SPACE_ITEM_TYPE)) {
			
			nodeObject = new HHdbTabSp(conn,nodeBean.getName(),true,StartUtil.prefix);
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.LOGIN_ROLE_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.LOGIN_ROLE_ITEM_TYPE)) {
			
			nodeObject = new HHdbUser(conn,nodeBean.getName(),true,StartUtil.prefix);
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.GROUP_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.CPU_MONITORING_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.DISK_MONITORING_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.MEMORY_MONITORING_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.COURSE_MONITORING_TYPE)) {
			
		}else if(nodeBean.getType().equals(TreeNodeUtil.NETWORK_MONITORING_TYPE)) {
			
		}
	}
		
	public BaseTreeNode getParentBaseTreeNode() {
		return parentBaseTreeNode;
	}

	public void setParentBaseTreeNode(BaseTreeNode parentBaseTreeNode) {
		this.parentBaseTreeNode = parentBaseTreeNode;
	}

	public MetaTreeNodeBean getMetaTreeNodeBean() {
		return nodeBean;
	}

	public void setMetaTreeNodeBean(MetaTreeNodeBean nodeBean) {
		this.nodeBean = nodeBean;
	}

	public void addAttr(String key, String value) {
		attrMap.put(key, value);
	}

	public void addAllAttr(Map<String, Object> aMap) {
		attrMap.putAll(aMap);
	}

	public Map<String, Object> getAttrMap() {
		return attrMap;
	}

	public void setAttrMap(Map<String, Object> attrMap) {
		this.attrMap = attrMap;
	}

	public void addChildNode(BaseTreeNode childNode) {
		nodes.add(childNode);
		this.add(childNode);
	}

	public List<BaseTreeNode> getChildNode() {
		return nodes;
	}

	public String getType() {
		return this.getMetaTreeNodeBean().getType();
	}

	@Override
	public String toString(){
		return nodeBean.getName();
	}
	
 
    public boolean isSelected()  
    {  
        return isSelected;  
    }  
      
    public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	public void setSelected(boolean _isSelected)  
    {  
        this.isSelected = _isSelected;  
          
        if(_isSelected)  
        {  
            // 如果选中，则将其所有的子结点都选中  
            if(children != null)  
            {  
                for(Object obj : children)  
                {  
                	BaseTreeNode node = (BaseTreeNode)obj;  
                    if(_isSelected != node.isSelected()&&node.isEnabled)  
                        node.setSelected(_isSelected);  
                }  
            }  
            // 向上检查，如果父结点的所有子结点都被选中，那么将父结点也选中  
            BaseTreeNode pNode = (BaseTreeNode)parent;  
            // 开始检查pNode的所有子节点是否都被选中  
            if(pNode != null)  
            {  
                int index = 0;  
                for(; index < pNode.children.size(); ++ index)  
                {  
                	BaseTreeNode pChildNode = (BaseTreeNode)pNode.children.get(index);  
                    if(!pChildNode.isSelected())  
                        break;  
                }  
                /*   
                 * 表明pNode所有子结点都已经选中，则选中父结点，  
                 * 该方法是一个递归方法，因此在此不需要进行迭代，因为  
                 * 当选中父结点后，父结点本身会向上检查的。  
                 */ 
                if(index == pNode.children.size())  
                {  
                    if(pNode.isSelected() != _isSelected&&pNode.isEnabled)  
                        pNode.setSelected(_isSelected);  
                }  
            }  
        }  
        else   
        {  
            /*  
             * 如果是取消父结点导致子结点取消，那么此时所有的子结点都应该是选择上的；  
             * 否则就是子结点取消导致父结点取消，然后父结点取消导致需要取消子结点，但  
             * 是这时候是不需要取消子结点的。  
             */ 
            if(children != null)  
            {  
                int index = 0;  
                for(; index < children.size(); ++ index)  
                {  
                	BaseTreeNode childNode = (BaseTreeNode)children.get(index);  
                    if(!childNode.isSelected())  
                        break;  
                }  
                // 从上向下取消的时候  
                if(index == children.size())  
                {  
                    for(int i = 0; i < children.size(); ++ i)  
                    {  
                    	BaseTreeNode node = (BaseTreeNode)children.get(i);  
                        if(node.isSelected() != _isSelected&&node.isEnabled)  
                            node.setSelected(_isSelected);  
                    }  
                }  
            }  
              
            // 向上取消，只要存在一个子节点不是选上的，那么父节点就不应该被选上。  
            BaseTreeNode pNode = (BaseTreeNode)parent;  
            if(pNode != null && pNode.isSelected() != _isSelected&&pNode.isEnabled)  
                pNode.setSelected(_isSelected);  
        }  
    }  
}
