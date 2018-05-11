package com.hhdb.csadmin.plugin.tree.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.tree.been.MetaTreeNodeBean;


public class BaseTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;
	private static final String ICON_PATH = "";
	private Color textForeground;
	private Color selectedTextForeground;
	private Color selectedBackground;

	public BaseTreeCellRenderer() {
		textForeground = UIManager.getColor("Tree.textForeground");
		selectedTextForeground = UIManager.getColor("Tree.selectionForeground");
		selectedBackground = UIManager.getColor("Tree.selectionBackground");
		setIconTextGap(6);
		setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
//		if (UIUtils.isGtkLookAndFeel()) {
//			setBorderSelectionColor(null);
//		}
	}

	public ImageIcon loadIcon(String name) {
		return IconUtilities.loadIcon(ICON_PATH + name);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {
		this.hasFocus = hasFocus;
		BaseTreeNode treeNode = (BaseTreeNode) value;
		MetaTreeNodeBean metaTreeNodeBean = treeNode.getMetaTreeNodeBean();
		ImageIcon imageIcon = loadIcon(metaTreeNodeBean.getOpenIcon());
		String text = metaTreeNodeBean.getName();
		this.selected = isSelected;
//		if (metaTreeNodeBean.getType().equals(TreeNodeType.CONN_TYPE)) {
//			//text = "[" + serverBean.getHost() + ":" + serverBean.getPort() + "]";
//			text = "[" + serverBean.getName() + "]";
//		}
//		if (metaTreeNodeBean.getType().equals(TreeNodeType.CONN_TYPE) || metaTreeNodeBean.getType().equals(TreeNodeType.DB_ITEM_TYPE)) {
//			if (treeNode.getChildCount() == 0) {
//				imageIcon = loadIcon(metaTreeNodeBean.getCloseIcon());
//			}
//		}
		setIcon(imageIcon);
		setText(text);
		setBackgroundSelectionColor(selectedBackground);
		if (!isSelected) {
			setForeground(textForeground);
		} else {
			setForeground(selectedTextForeground);
		}
		JTree.DropLocation dropLocation = tree.getDropLocation();
		if (dropLocation != null && dropLocation.getChildIndex() == -1 && tree.getRowForPath(dropLocation.getPath()) == row) {
			setForeground(selectedTextForeground);
			Color background = UIManager.getColor("Tree.dropCellBackground");
//			if (background == null) {
//				background = UIUtils.getBrighter(getBackgroundSelectionColor(), 0.87);
//			}
			setBackgroundSelectionColor(background);
			this.selected = true;
		}
		return this;
	}
}
