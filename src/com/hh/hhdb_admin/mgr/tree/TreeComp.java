package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.handler.LeftDoubleClickHandler;
import com.hh.hhdb_admin.mgr.tree.handler.RightClickHandler;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Jiang
 * @date: 2020/9/10
 */

public class TreeComp extends HPanel {

    public static final String DOMAIN_NAME = TreeComp.class.getName();

    static {
        LangMgr.merge(DOMAIN_NAME, LangUtil.loadLangRes(TreeComp.class));
    }

    private CsTree tree;
    private CsTree schemaTree;
    private boolean isSchemaSearch = false;

    /**
     * 获取树实例
     *
     * @param loginBean 登录信息
     * @return 树实例
     */
    public static TreeComp newTreeInstance(LoginBean loginBean) {
        TreeComp treeComp = new TreeComp();
        AbsTreeMr.genTreeMr(loginBean.getJdbc()).ifPresent(treeMr -> {
            TreeMrNode rootNodeMr = treeMr.getRootNode(loginBean.getConnName(), loginBean.getViewType());
            HTreeNode rootNode = new HTreeNode();
            rootNode.setName(rootNodeMr.getName());
            rootNode.setType(rootNodeMr.getType().name());
            rootNode.setOpenIcon(IconFileUtil.getIcon(new IconBean(TreeUtil.ICON_CONTEXT, rootNodeMr.getIcon())));
            treeComp.tree = new CsTree(rootNode);
            treeComp.tree.setRightHandler(new RightClickHandler(treeComp.tree, loginBean))
                    .setLeftDoubleHandler(new LeftDoubleClickHandler(treeComp.tree, loginBean));

            TreeMrType[] nodeArr = {TreeMrType.COLUMN_GROUP, TreeMrType.CONSTRAINT_GROUP, TreeMrType.FOREIGN_KEY_GROUP,
                    TreeMrType.CHECK_KEY_GROUP, TreeMrType.PRIMARY_KEY_GROUP, TreeMrType.UNIQUE_KEY_GROUP,
                    TreeMrType.INDEX_GROUP, TreeMrType.RULE_GROUP, TreeMrType.TRIGGER_GROUP, TreeMrType.COLUMN,
                    TreeMrType.INDEX, TreeMrType.RULE, TreeMrType.TRIGGER, TreeMrType.FOREIGN_KEY, TreeMrType.CHECK_KEY,
                    TreeMrType.PRIMARY_KEY, TreeMrType.UNIQUE_KEY
            };
            Arrays.asList(nodeArr).forEach(item -> treeComp.tree.addExcSechType(item.name()));
            treeComp.initMainTreePanel(loginBean, treeComp.tree);
        });
        return treeComp;
    }

    private void initMainTreePanel(LoginBean loginBean, CsTree tree) {
        LastPanel lastPanel = new LastPanel();
        lastPanel.setWithScroll(tree.getComp());
        if (isShowSchemaChange(loginBean)) {
            HPanel panel = new HPanel();
            panel.add(getSearchPanel());
            panel.setLastPanel(lastPanel);
            HTabPane tabPane = new HTabPane() {
                @Override
                public void onTabChange(String id) {
                    isSchemaSearch = id.equals("schema");
                }
            };
            tabPane.setCloseBtn(false);
            tabPane.addPanel("database", getLang("database"), panel);
            tabPane.addPanel("schema", getLang("schema"), getSchemaPanel(loginBean));
            LastPanel last = new LastPanel();
            last.set(tabPane.getComp());
            setLastPanel(last);
        } else {
            add(getSearchPanel());
            setLastPanel(lastPanel);
        }
    }

    private HPanel getSearchPanel() {
        HPanel searchPanel = new HPanel(new HDivLayout(GridSplitEnum.C9));
        searchPanel.getComp().setMaximumSize(new Dimension(0, 300));
        TextInput searchInput = new TextInput();
        searchPanel.add(searchInput);
        searchPanel.getComp().setBackground(Color.white);
        searchPanel.add(new HButton(getLang("search")) {
            @Override
            protected void onClick() {
                if (isSchemaSearch) {
                    schemaTree.searchNode(searchInput.getValue());
                    schemaTree.openAllRootNode();
                } else {
                    tree.searchNode(searchInput.getValue());
                    tree.openAllRootNode();
                }
            }
        });
        return searchPanel;
    }

    private boolean isShowSchemaChange(LoginBean loginBean) {
        if (loginBean.getViewType() == ViewType.DBA) {
            return false;
        }
        DBTypeEnum dbTypeEnum = DriverUtil.getDbType(loginBean.getJdbc());
        if (dbTypeEnum == null) {
            return false;
        }
        switch (dbTypeEnum) {
            case mysql:
            case oracle:
                return true;
            default:
                return false;
        }
    }

    /**
     * 生成模式切换面板
     *
     * @param loginBean 登录信息
     * @return 模式选择面板
     */
    private HPanel getSchemaPanel(LoginBean loginBean) {
        HTreeNode treeNode = new HTreeNode();
        treeNode.setName(getLang("schema"));
        treeNode.setType(TreeMrType.DATA_MODEL_SCHEMA_GROUP.name());
        treeNode.setOpenIcon(IconFileUtil.getIcon(new IconBean(TreeUtil.ICON_CONTEXT, "schemaindex.png")));
        schemaTree = CsTree.newSchemaChangeTree(treeNode);
        AbsTreeMr.genTreeMr(loginBean.getJdbc()).ifPresent(treeMr -> {
            List<HTreeNode> treeNodes = treeMr.getSchemas(loginBean.getConn()).stream().map(item -> {
                String schemaName = item.get("schema_name").trim();
                String iconName = loginBean.getJdbc().getSchema().equals(schemaName) ? "schema_choose.png" : "schema.png";
                HTreeNode node = new HTreeNode();
                node.setName(schemaName);
                node.setOpenIcon(IconFileUtil.getIcon(new IconBean(TreeUtil.ICON_CONTEXT, iconName)));
                return node;
            }).collect(Collectors.toList());
            schemaTree.addHTreeNode(treeNode, treeNodes, false);
        });
        schemaTree.setRightHandler(new RightClickHandler(schemaTree, loginBean));
        TreeUtil.refreshSchemaTree(loginBean, schemaTree, treeNode);
        HPanel panel = new HPanel();
        panel.add(getSearchPanel());
        LastPanel lastPanel = new LastPanel();
        lastPanel.setWithScroll(schemaTree.getComp());
        panel.setLastPanel(lastPanel);
        return panel;
    }

    /**
     * 刷新指定节点
     *
     * @param schemaName 模式名
     * @param tableName  表名
     * @param nodeType   节点类型
     */
    public void refreshNode(String schemaName, String tableName, TreeMrType nodeType) {
        HTreeNode targetNode = getNode(tree.getRootNode(), schemaName, tableName, nodeType);
        if (targetNode == null) {
            return;
        }
        tree.getLeftDoubleHandler().refreshNode(targetNode);
    }

    public void refreshNode(String name, String nodeType) {
    }

    private HTreeNode getNode(HTreeNode treeNode, String schema, String table, TreeMrType type) {
        if (StringUtils.isNotBlank(schema)) {
            if (treeNode.getType().equals(TreeMrType.SCHEMA.name()) && !treeNode.getName().equals(schema)) {
                return null;
            }
        }
        if (StringUtils.isNotBlank(table)) {
            if (treeNode.getType().equals(TreeMrType.TABLE.name()) && !treeNode.getName().equals(table)) {
                return null;
            }
        }
        for (HTreeNode child : treeNode.getChildNode()) {
            if (child.getType().equals(type.name())) {
                return child;
            }
            if (child.getChildNode().size() > 0) {
                HTreeNode node = getNode(child, schema, table, type);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    public static String getLang(String key) {
        return LangMgr.getValue(DOMAIN_NAME, key);
    }


    public CsTree getTree() {
        return tree;
    }
}

