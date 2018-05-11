package com.hhdb.csadmin.plugin.sql_book.attribute.tree;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.sql_book.bean.MetaTreeNodeBean;

/**
 * 树状图渲染器
 * @author hhxd
 *
 */
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
	}

	public ImageIcon loadIcon(String name) {
		return IconUtilities.loadIcon(ICON_PATH + name);
	}

	/**
	 * 设置数据显示
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus) {
		this.hasFocus = hasFocus;
		BaseTreeNode treeNode = (BaseTreeNode) value;
		MetaTreeNodeBean metaTreeNodeBean = treeNode.getMetaTreeNodeBean();
		ImageIcon imageIcon = loadIcon(metaTreeNodeBean.getOpenIcon());
		String text = metaTreeNodeBean.getName();
		this.selected = isSelected;
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
			setBackgroundSelectionColor(background);
			this.selected = true;
		}
		return this;
	}
}
