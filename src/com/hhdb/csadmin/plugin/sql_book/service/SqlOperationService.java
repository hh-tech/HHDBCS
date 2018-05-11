package com.hhdb.csadmin.plugin.sql_book.service;

import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.hh.frame.common.log.LM;
import com.hh.frame.dirobj.bean.DbObjBean;
import com.hh.frame.dirobj.bean.LinkBean;
import com.hh.frame.dirobj.bean.ObjBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.common.util.HSQL_Util;
import com.hhdb.csadmin.plugin.sql_book.BooksPanel;
import com.hhdb.csadmin.plugin.sql_book.HSqlBook;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.bean.MetaTreeNodeBean;
import com.hhdb.csadmin.plugin.sql_book.util.TreeNodeUtil;

/**
 * 处理数据
 * 
 */
public class SqlOperationService {
	
	public HSqlBook hbook;
	private BooksPanel bookp;
	

	public SqlOperationService(HSqlBook hbook, BooksPanel bookp) {
		this.hbook = hbook;
		this.bookp = bookp;
	}
	
	/**
	 * 获取Serverbean
	 */
	public ServerBean getServerbean() {
		ServerBean sb = null;
		try {
			String toID = "com.hhdb.csadmin.plugin.conn";       
			CmdEvent event = new CmdEvent(hbook.PLUGIN_ID, toID, "GetServerBean");
			HHEvent ev = hbook.sendEvent(event);
			if (ev instanceof ErrorEvent) {
					throw new Exception(((ErrorEvent) ev).getErrorMessage());
			}
			sb = (ServerBean)ev.getObj();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		return sb;
	}
	
	/**
	 * 获取BaseFrame
	 */
	public BaseFrame getBaseFrame() {
		BaseFrame bf = null;
		try {
			String toID = "com.hhdb.csadmin.plugin.main";       
			HHEvent event = new HHEvent(hbook.PLUGIN_ID, toID, EventTypeEnum.GET_OBJ.name());
			HHEvent ev = hbook.sendEvent(event);
			if (ev instanceof ErrorEvent) {
				throw new Exception(((ErrorEvent) ev).getErrorMessage());
			}
			bf = (BaseFrame)ev.getObj();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		return bf;
	}
	
	
	/**
	 * 发送事件获取分页面板
	 * 
	 * @param tabName
	 */
	public void getTabPanelTable(String tabName) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.tabpane";
		CmdEvent tabPanelEvent = new CmdEvent(hbook.PLUGIN_ID, toId,"AddPanelEvent");
		tabPanelEvent.setObj(bookp);
		tabPanelEvent.addProp("ICO", "book.png");
		tabPanelEvent.addProp("TAB_TITLE", tabName);
		tabPanelEvent.addProp("COMPONENT_ID", tabName);
		HHEvent ev = hbook.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
	
	/**
	 * 在查询器中打开
	 * @param sql
	 */
	public void getQuery(String sql) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.query";
		CmdEvent tabPanelEvent = new CmdEvent(hbook.PLUGIN_ID, toId,"initText");
		tabPanelEvent.addProp("Text", sql);
		HHEvent ev = hbook.sendEvent(tabPanelEvent);
		if (ev instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) ev).getErrorMessage());
		}
	}
		

	/**
	 * 发送事件得到List< Map< String, Object > >格式数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getListMap(String sql) throws Exception {
		String toId = "com.hhdb.csadmin.plugin.conn";
		CmdEvent obtainRowsEvent = new CmdEvent(hbook.PLUGIN_ID, toId,"ExecuteListMapBySqlEvent");
		obtainRowsEvent.addProp("sql_str", sql);
		HHEvent rowEvent = hbook.sendEvent(obtainRowsEvent);
		List<Map<String, Object>> rowStr = null;
		if (rowEvent instanceof ErrorEvent) {
			throw new Exception(((ErrorEvent) rowEvent).getErrorMessage());
		} else {
			rowStr = (List<Map<String, Object>>) rowEvent.getObj();
		}
		return rowStr;
	}
	
	
	
	
	/**
	 * 新建文件夹
	 * @param treeNode
	 * @param name
	 */
	public void newFolder(BaseTreeNode treeNode,String name) {
		try {
			int dirId = treeNode.getMetaTreeNodeBean().getId(); 
			//判断重复
			if(bookp.dirtool.nameExist(HSQL_Util.getConnection(), dirId, name)){
				JOptionPane.showMessageDialog(null, "名称重复", "消息",JOptionPane.ERROR_MESSAGE);
			}else{
				if(bookp.dirtool.create(HSQL_Util.getConnection(),dirId,name) != -1){
					//刷新
					refresh(treeNode,true);
				}else{
					JOptionPane.showMessageDialog(null, "新建失败", "消息",JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 文件夹重命名
	 * @param treeNode
	 * @param name
	 */
	public void ren(BaseTreeNode treeNode,String name) {
		try {
			Integer dirId = treeNode.getMetaTreeNodeBean().getId(); 
			//验证重复
			if(bookp.dirtool.nameExist(HSQL_Util.getConnection(), treeNode.getParentBaseTreeNode().getMetaTreeNodeBean().getId(), name)){
				JOptionPane.showMessageDialog(null, "名称重复", "消息",JOptionPane.ERROR_MESSAGE);
			}else{
				bookp.dirtool.updateName(HSQL_Util.getConnection(), dirId, name);
				//刷新父节点
				refresh(treeNode.getParentBaseTreeNode(),true);
			}
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 删除文件夹
	 * @param treeNode
	 */
	public void deleteFolder(BaseTreeNode treeNode) {
		try {
			bookp.dirtool.forceDelete(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId());
			//需要刷新父节点
			refresh(treeNode.getParentBaseTreeNode(),true);
			//关闭详情面板
			bookp.sqlDetail.getViewport().add(new JLabel());
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 删除sql
	 * @param treeNode
	 * @throws Exception
	 */
	public void deleteSql(BaseTreeNode treeNode) {
		try {
			bookp.objtool.forceDelete(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId());
			//需要刷新父节点
			refresh(treeNode.getParentBaseTreeNode(),true);
			//关闭详情面板
			bookp.sqlDetail.getViewport().add(new JLabel());
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 刷新
	 * @param treeNode
	 * @param bool
	 * false为快捷键选择树数据
	 */
	public void refresh(BaseTreeNode treeNode,Boolean bool) {
		try {
			//获取文件夹
			List<DbObjBean> listdir = bookp.dirtool.getAll(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId());
			//删除原来的子节点
			treeNode.removeAllChildren(); 
			//文件夹
			for (DbObjBean dirbean : listdir) {
				if(dirbean.getId() != 0){   //排除根节点
					BaseTreeNode viewtreenode = new BaseTreeNode();
					MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
					tablemtn.setName(dirbean.getName());
					tablemtn.setOpenIcon("schema.png");
					tablemtn.setType(TreeNodeUtil.FILE_TYPE);
					tablemtn.setId(dirbean.getId());
					tablemtn.setParentId(dirbean.getParentId());
					viewtreenode.setMetaTreeNodeBean(tablemtn);
					viewtreenode.setParentBaseTreeNode(treeNode);
					treeNode.addChildNode(viewtreenode);
				}
			}
			if(bool){
				//sql文件
				List<ObjBean> lisrobj = bookp.objtool.getAll(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId());
				for (ObjBean objBean : lisrobj) {
					BaseTreeNode viewtreenode = new BaseTreeNode();
					MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
					tablemtn.setName(objBean.getName());
					tablemtn.setId(objBean.getId());
					tablemtn.setParentId(objBean.getParentId());
					tablemtn.setOpenIcon("formatsql.png");
					tablemtn.setType(TreeNodeUtil.SQL_TYPE);
					tablemtn.setTxt(objBean.getTxt());
					viewtreenode.setMetaTreeNodeBean(tablemtn);
					viewtreenode.setParentBaseTreeNode(treeNode);
					treeNode.addChildNode(viewtreenode);
				}
				//快捷方式
				List<LinkBean> list = bookp.linkTool.getAllLink(HSQL_Util.getConnection(), treeNode.getMetaTreeNodeBean().getId());
				for (LinkBean linkbean : list) {
					BaseTreeNode viewtreenode = new BaseTreeNode();
					MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
					tablemtn.setName(linkbean.getName());
					tablemtn.setId(linkbean.getId());
					tablemtn.setParentId(linkbean.getParentId());
					tablemtn.setOpenIcon("addforeign_key.png");
					tablemtn.setType(TreeNodeUtil.QUICK_TYPE);
					int id = Integer.valueOf(linkbean.getTxt().split(":")[1]);  //截取出id
					tablemtn.setTxt(bookp.objtool.get(HSQL_Util.getConnection(),id).getTxt()); //查出来的快捷方式的内容为来源的id
					tablemtn.setOriginalId(id);   
					viewtreenode.setMetaTreeNodeBean(tablemtn);
					viewtreenode.setParentBaseTreeNode(treeNode);
					treeNode.addChildNode(viewtreenode);
				}
			}
			// 加上这两句是可以让节点刷新后直接展开
			TreePath path = new TreePath(bookp.getTree().getTreeModel().getPathToRoot(treeNode));
			bookp.getTree().expandPath(path);
			// 重新加载节点
			bookp.getTree().getTreeModel().reload(treeNode);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "消息",JOptionPane.ERROR_MESSAGE);
		}
	}

}
