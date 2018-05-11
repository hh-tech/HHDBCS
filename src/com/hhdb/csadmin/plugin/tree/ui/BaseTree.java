package com.hhdb.csadmin.plugin.tree.ui;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


public class BaseTree extends JTree{
	private static final long serialVersionUID = 1L;
	private DefaultTreeModel treeModel;

	public BaseTree(DefaultMutableTreeNode root) {
		super(root);
		init();
	}

	private void init() {
		treeModel = (DefaultTreeModel) this.getModel();
		setModel(treeModel);
		putClientProperty("JTree.lineStyle", "Angled");
		setRootVisible(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		setAutoscrolls(true);
	}

	public BaseTreeNode getRootTreeNode() {
		return (BaseTreeNode) this.getModel().getRoot();
	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}
}
