package com.hhdb.csadmin.plugin.tree.ui.rightMenu;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;

import com.hh.frame.common.log.LM;
import com.hh.frame.dbobj.hhdb.HHdbFun;
import com.hh.frame.dbobj.hhdb.HHdbSchema;
import com.hh.frame.dbobj.hhdb.HHdbSeq;
import com.hh.frame.dbobj.hhdb.HHdbTable;
import com.hh.frame.dbobj.hhdb.HHdbView;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.cmd.console.CommonsHelper;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.BaseTreeNode;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;

public class MoreTreeNodeMenu extends BasePopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String DELSCHEMA = "delschema";
	private final String DELTABLE = "deltable";
	private final String CASDELTABLE = "casdeltable";
	private final String DELVIEW = "delview";
	private final String DELFUCTION = "delfuction";
	private final String DELSEQ = "delseq";
	private final String DELTYPE = "deltype";
	private final String DELQUERY = "delquery";
	private List<BaseTreeNode> treeNodes;
	// private String type = "";
	private HTree htree = null;

	public MoreTreeNodeMenu(HTree htree) {
		this.htree = htree;
	}

	public MoreTreeNodeMenu getInstance(List<BaseTreeNode> treeNodes,
			String type) {
		this.treeNodes = treeNodes;
		// this.type = type;
		if (type.equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
			add(createMenuItem("删除模式", DELSCHEMA, this));
		} else if (type.equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
			add(createMenuItem("删除表", DELTABLE, this));
			add(createMenuItem("强制删除表", CASDELTABLE, this));
		} else if (type.equals(TreeNodeUtil.VIEW_ITEM_TYPE)) {
			add(createMenuItem("删除视图", DELVIEW, this));
		} else if (type.equals(TreeNodeUtil.FUN_ITEM_TYPE)) {
			add(createMenuItem("删除函数", DELFUCTION, this));
		} else if (type.equals(TreeNodeUtil.SEQ_ITEM_TYPE)) {
			add(createMenuItem("删除序列", DELSEQ, this));
		} else if (type.equals(TreeNodeUtil.TYPE_ITEM_TYPE)) {
			add(createMenuItem("删除类型", DELTYPE, this));
		} else if (type.equals(TreeNodeUtil.SELECT_ITEM_TYPE)) {
			add(createMenuItem("删除查询", DELQUERY, this));
		} else {
			add(createMenuItem("无多选操作", "other", this));
		}
		return this;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals(DELSCHEMA)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除选中的模式？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					if (treeNode.getMetaTreeNodeBean().getName()
							.equals("public")) {
						JOptionPane.showMessageDialog(null, "public模式不能删除",
								"警告", JOptionPane.WARNING_MESSAGE);
						continue;
					}
					try {
						((HHdbSchema) treeNode.getNodeObject()).drop();
					} catch (Exception ee) {
						LM.error(LM.Model.CS.name(), ee);
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshSchemaCollection(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals(DELTABLE)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除选中的表？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					try {
						((HHdbTable) treeNode.getNodeObject()).drop();
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshTable(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals(CASDELTABLE)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要强制删除选中的表？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					try {
						((HHdbTable) treeNode.getNodeObject()).dropCascade();
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshTable(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals(DELVIEW)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除选中的视图？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					try {
						((HHdbView) treeNode.getNodeObject()).drop();
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshView(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals(DELFUCTION)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除选中的函数？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					try {
						((HHdbFun) treeNode.getNodeObject()).drop();
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshFunction(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals(DELSEQ)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除选中的序列？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					try {
						((HHdbSeq) treeNode.getNodeObject()).drop();
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshSequence(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals(DELTYPE)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除选中的类型？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					try {
						StringBuffer command = new StringBuffer("DROP TYPE \""
								+ treeNode.getParentBaseTreeNode()
										.getParentBaseTreeNode()
										.getMetaTreeNodeBean().getName()
								+ "\".\""
								+ treeNode.getMetaTreeNodeBean().getName()
								+ "\"");

						command.append(" CASCADE");
						htree.treeService.executeSql(command.toString());
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshType(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		} else if (actionCmd.equals(DELQUERY)) {
			int n = JOptionPane.showConfirmDialog(null, "您确定要删除选中的查询文件？", "消息",
					JOptionPane.YES_NO_OPTION);
			if (n == 0) {
				for (BaseTreeNode treeNode : treeNodes) {
					try {
						CmdEvent getsbEvent = new CmdEvent(htree.PLUGIN_ID, "com.hhdb.csadmin.plugin.conn", "GetServerBean");
						HHEvent revent = htree.sendEvent(getsbEvent);
						ServerBean serverbean = (ServerBean)revent.getObj();
						
						File file = new File(CommonsHelper.getClassPath()
								+ "/db/servers/" + serverbean.getHost() + "/"
								+ serverbean.getDBName() + "/"
								+ treeNode
								.getParentBaseTreeNode().getParentBaseTreeNode().getMetaTreeNodeBean().getName() + "/"+treeNode.getMetaTreeNodeBean().getName()+".sql");
						file.delete();
					} catch (Exception ee) {
						JOptionPane.showMessageDialog(null,
								treeNode.getMetaTreeNodeBean().getName()
										+ "删除失败：" + ee.getMessage(), "错误",
								JOptionPane.ERROR_MESSAGE);
					}
				}
				try {
					htree.treeService.refreshQuery(treeNodes.get(0)
							.getParentBaseTreeNode());
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
		}
	}

}
