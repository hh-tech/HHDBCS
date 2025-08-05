package com.hh.hhdb_admin.mgr.tree.handler;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;

import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class RightClickHandler {

	private final JdbcBean jdbcBean;
	private final CsTree tree;

	public RightClickHandler(CsTree tree, JdbcBean jdbcBean) {
		this.tree = tree;
		this.jdbcBean = jdbcBean;
	}

	/**
	 * 多选点击事件
	 *  @param treeNodeList 节点列表
	 * @param e            e
     */
	public void onClick(List<LeafBean> treeNodeList, MouseEvent e) throws Exception {
		TreeMrType type = null;
        List<CsFolderBean> list = new LinkedList<>();
		for (LeafBean treeNode : treeNodeList) {
		    list.add((CsFolderBean) treeNode);
			if (type == null) {
				type = ((CsFolderBean)treeNode).getType();
				continue;
			}
			if (CsVarUtil.getDbtype() != DBTypeEnum.redis && ((CsFolderBean)treeNode).getType() != type) {
				return;
			}
		}
		showPopup(e, list.toArray(new CsFolderBean[0]));
	}

	public void showPopup(MouseEvent e, CsFolderBean... treeNodes) {
		CsFolderBean treeNode = treeNodes[0];
		TreeMrNode treeMrNode = new TreeMrNode(treeNode.getName(), treeNode.getType(), null);
		treeMrNode.setTableName(CsTreeUtil.getTableName(treeNode));
		treeMrNode.setPackageName(CsTreeUtil.getPackageName(treeNode));
		
		DBTypeEnum dbType= DriverUtil.getDbType(jdbcBean);
		treeMrNode.setSchemaName(CsTreeUtil.getSchemaName(treeNode, dbType));
		
		AbsTreeMr treeMr=AbsTreeMr.genTreeMgr(jdbcBean, LangMgr.langType);
		treeMr.setConn(CsVarUtil.conn);
		
		HPopMenu hp = new HPopMenu();
		Map<String, EventType> map = treeNodes.length == 1 ? treeMr.getRightMenu(treeMrNode) : treeMr.getMultiRightMenu(treeMrNode);
		for (String key : map.keySet()) {
			EventType value = map.get(key);
			if (value == EventType.SEP) {
				hp.addSeparator();
			} else {
//				if (key.equals(LangMgr.lang(Word2.FILTER))) {	//筛选
//					String filterVal = null;
//					try {
//						Map<String, String> fimap = CsTreeMgr.filterData.get(treeMrNode.getSchemaName());
//						filterVal = null != fimap ? fimap.get(treeNode.getType().name()) : null;
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					}
//					if (StringUtils.isNotBlank(filterVal)) {
//						key += ("(" + filterVal + ")");
//					}
//				}
				HMenuItem item = new HMenuItem(key) {
					@Override
					protected void onAction() {
						RightMenuUtil.doRMenuAction(value, jdbcBean, tree, treeNodes);
						
						boolean connected = CsVarUtil.isConnected();
						if (value == EventType.CONNECT) {
							item.setEnabled(!connected);
						}
						if (value == EventType.DISCONNECT) {
							item.setEnabled(connected);
						}
					}
				};
				
				if (map.containsValue(EventType.CONNECT)) {
					boolean connected = CsVarUtil.isConnected();
					if (value == EventType.CONNECT) {
						item.setEnabled(!connected);
						item.setFontIcon(FaSolidEnum.LINK);
					}
					if (value == EventType.DISCONNECT) {
						item.setEnabled(connected);
						item.setFontIcon(FaSolidEnum.LINK_SLASH);
					}
				}
				hp.addItem(item);
			}
			
			
		}
		
		if(treeNodes.length>1) {
			HMenuItem countItem = new HMenuItem(LangMgr.lang(Word2.SELECTED) +"["+ treeNodes.length+"]");
			countItem.setEnabled(false);
			hp.addItem(countItem);
		}
		hp.showPopup(e);
	}

}
