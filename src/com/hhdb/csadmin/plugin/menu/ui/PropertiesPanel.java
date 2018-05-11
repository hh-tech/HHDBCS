package com.hhdb.csadmin.plugin.menu.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;
import com.hhdb.csadmin.plugin.menu.util.PropertyNode;



public class PropertiesPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3693930510357835201L;
	public QuerySetPanel queryset=null;
	public CmdSetPanel  cmdpanel=null;
	public PropertiesPanel() {
		super();
		queryset=new QuerySetPanel();
		cmdpanel=new CmdSetPanel();
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		splitPane.setDividerSize(6);
		int panelWidth = 650;
		int panelHeight = 400;
		setPreferredSize(new Dimension(panelWidth, panelHeight));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));

		CardLayout cardLayout = new CardLayout();
		JPanel rightPanel = new JPanel(cardLayout);
		splitPane.setRightComponent(rightPanel);

		// 选项 左边树
		List<PropertyNode> branches = new ArrayList<PropertyNode>();
		PropertyNode node = new PropertyNode(2, "外观","wg");
		node.addChild(new PropertyNode(3, "查询器","textpanel"));
		node.addChild(new PropertyNode(4, "命令列","cmdpanel"));
		branches.add(node);
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new PropertyNode(0, "Preferences","parent"));

		List<PropertyNode> children = null;
		DefaultMutableTreeNode treeNode = null;

		for (int i = 0, k = branches.size(); i < k; i++) {
			node = (PropertyNode) branches.get(i);
			treeNode = new DefaultMutableTreeNode(node);
			root.add(treeNode);

			if (node.hasChildren()) {
				children = node.getChildren();
				int count = children.size();
				for (int j = 0; j < count; j++) {
					treeNode.add(new DefaultMutableTreeNode(children.get(j)));
				}
			}
		}

		final JTree tree = new JTree(root);
		tree.setRowHeight(22);
		tree.putClientProperty("JTree.lineStyle", "Angled");
		//tree.setCellRenderer(new BaseTreeCellRenderer());
		tree.setRootVisible(false);

		// expand all rows
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}

		Dimension leftPanelDim = new Dimension(200, 350);
		JScrollPane js = new JScrollPane(tree);
		js.setPreferredSize(leftPanelDim);

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBackground(Color.white);
		leftPanel.setMinimumSize(leftPanelDim);
		leftPanel.setMaximumSize(leftPanelDim);
		leftPanel.add(js, BorderLayout.CENTER);
		splitPane.setLeftComponent(leftPanel);

		mainPanel.add(splitPane, BorderLayout.CENTER);
		add(mainPanel, BorderLayout.CENTER);
		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent e) {
				//int selRow = tree.getRowForLocation(e.getX(), e.getY());
				if (e.getClickCount() == 1) {
					String name=tree.getLastSelectedPathComponent().toString();
					if(name==null){
						return;
					}
					if(name.equals("查询器")){
						splitPane.getRightComponent().repaint();
						splitPane.setRightComponent(queryset);
					}else if(name.equals("命令列")){
						splitPane.getRightComponent().repaint();
						splitPane.setRightComponent(cmdpanel);
					}
				}
			}
		});
	
		
	}
	public boolean execute() {
		DefaultSet set=new DefaultSet();
		if(queryset!=null){
			set.setBackground(queryset.getBackColor());
			set.setFontSize(queryset.getFontsize());
			set.setLinunumber(queryset.getIsLine() + "");
		}
		if(cmdpanel!=null){
			set.setCmdbackcolor(cmdpanel.getBackcolor());
			set.setCmdfontcolor(cmdpanel.getFontcolor());
		}
		DefaultSetting.updateSettings(set);
		return true;
	}
}
