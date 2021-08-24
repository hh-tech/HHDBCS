package com.hh.hhdb_admin.mgr.view.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.viewMr.mr.AbsViewMr;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.tree.HTree;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.TreeUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

/**
 * 选择表列来可视化生成select语句用来创建视图
 * author:yangxianhui
 * date:2020/10/15
 */
public abstract class ChooseTableComp {
    private final HSplitPanel splitPanel;
    private HTree colTree;
    private HTable table;
    private String schemaName;
    private String tableName;
    private final LastPanel colPanel;
    private final LastPanel leftPanel;
    private final AbsViewMr viewMr;
    private final Connection conn;
    private final DBTypeEnum dbTypeEnum;

    private static HashMap<String, List<Map<String, String>>> maps;
    private static final String domainName = ChooseTableComp.class.getName();

    private final static String COL_NAME = "column_name";
    private final static String COL_SELECT = "select";

    private final static String LK_COLUMN_NAME = "COLUMN_NAME";
    private final static String LK_SAVE = "SAVE";
    private final static String LK_SELECTED = "SELECTED";
    private final static String LK_SELECT_TABLE_COLUMN = "SELECT_TABLE_COLUMN";
    private HDialog dialog;

    static {
        LangMgr.merge(domainName, com.hh.frame.lang.LangUtil.loadLangRes(ChooseTableComp.class));
    }


    public ChooseTableComp(LoginBean loginBean) {
        splitPanel = new HSplitPanel();
        colPanel = getRightPanel();
        maps = new HashMap<>();
        HPanel rightPanel = new HPanel();
        rightPanel.setLastPanel(colPanel);
        colPanel.getComp().setVisible(false);
        leftPanel = new LastPanel(false);
        splitPanel.setLastComp4One(leftPanel);
        splitPanel.setPanelTwo(rightPanel);
        splitPanel.setSplitWeight(0.3);
        viewMr = AbsViewMr.genViewMr(loginBean.getJdbc());
        this.conn = loginBean.getConn();
        this.dbTypeEnum = DriverUtil.getDbType(loginBean.getJdbc());
    }

    /**
     * 显示插件
     */
    public void show(HDialog parentDialog) {
        this.dialog = new HDialog(parentDialog, 700);
        maps.clear();
        this.dialog.setWindowTitle(LangMgr.getValue(domainName, LK_SELECT_TABLE_COLUMN));
        colPanel.getComp().setVisible(false);
        if (colTree == null) {
            colTree = getLeftTree();
            leftPanel.setWithScroll(colTree.getComp());
        } else {
            Container container = colTree.getComp().getParent();
            colTree = null;
            container.removeAll();
            colTree = getLeftTree();
            container.add(colTree.getComp());
        }
        this.dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        this.dialog.setRootPanel(splitPanel);
        this.dialog.show();
    }

    /**
     * 设置文本框中显示的sql
     *
     * @param sql 查询语句
     */
    protected void setTextEditorValue(String sql) {

    }

    /**
     * 获取左侧树
     */
    private HTree getLeftTree() {
        return new HTree(transTreeNode(Objects.requireNonNull(viewMr).getRootNode(this.conn))) {
            @Override
            protected void dbClickTreeNode(HTreeNode treeNode) {
                try {
                    if (treeNode.getType().equals(TreeMrType.TABLE.name())) {
                        return;
                    }
                    refreshPanel(treeNode);

                    TreeMrNode treeMrNode = new TreeMrNode(treeNode.getName(), TreeMrType.valueOf(treeNode.getType()), "");
                    if (treeNode.getType().equals(TreeMrType.MORE.name())) {
                        treeMrNode.setChildSize(treeNode.getParentHTreeNode().getChildSize());
                    }
                    treeMrNode.setTableName(getNodeName(treeNode, TreeMrType.TABLE));
                    treeMrNode.setSchemaName(getNodeName(treeNode, TreeMrType.SCHEMA));
                    if (treeNode.getType().equals(TreeMrType.MORE.name())) {
                        List<HTreeNode> treeNodes = new ArrayList<>();
                        viewMr.getChildNode(treeMrNode, conn).forEach(t -> {
                            HTreeNode t2 = new HTreeNode();
                            t2.setName(t.getName());
                            t2.setType(t.getType().name());
                            t2.setOpenIcon(IconFileUtil.getIcon(new IconBean(TreeUtil.ICON_CONTEXT, t.getIcon())));
                            treeNodes.add(t2);
                        });
                        this.removeHTreeNode(treeNode);
                        this.addHTreeNode(treeNode.getParentHTreeNode(), treeNodes, false);
                        this.openAllByNode(treeNode.getParentHTreeNode());
                    } else {
                        viewMr.getChildNode(treeMrNode, conn).stream().map(item -> transTreeNode(item)).forEach(treeNode::add);
                        TreePath path = new TreePath(((DefaultTreeModel) ((JTree) this.getComp()).getModel()).getPathToRoot(treeNode.getBaseNode()));
                        ((JTree) this.getComp()).expandPath(path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                }
            }

            @Override
            protected void selectTreeNode(HTreeNode treeNode) {
                try {
                    if (!treeNode.getType().equals(TreeMrType.TABLE.name())) {
                        return;
                    }
                    refreshPanel(treeNode);
                    loadTable(getNodeName(treeNode, TreeMrType.SCHEMA), getNodeName(treeNode, TreeMrType.TABLE));
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                }
            }
        };
    }

    /**
     * 获取右侧表格面板
     */
    private LastPanel getRightPanel() {
        LastPanel lastPanel = new LastPanel(false);
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        l.setxGap(2);
        HBarPanel toolBar = new HBarPanel(l);
        HButton saveButton = new HButton(LangMgr.getValue(domainName, LK_SAVE)) {
            @Override
            protected void onClick() {
                clickSaveBtn(getSql());
            }
        };
        saveButton.setIcon(AddUpdViewComp.getIcon("save"));
        toolBar.add(saveButton);
        lastPanel.setHead(toolBar.getComp());
        initTable();
        lastPanel.setWithScroll(table.getComp());
        return lastPanel;
    }

    /**
     * 初始化表格
     */
    private void initTable() {
        table = new HTable();
        DataCol colCol = new DataCol(COL_NAME, LangMgr.getValue(domainName, LK_COLUMN_NAME));
        BoolCol selCol = new BoolCol(COL_SELECT, LangMgr.getValue(domainName, LK_SELECTED));
        selCol.setWidth(100);
        table.addCols(colCol,selCol);
        table.setRowHeight(30);
    }

    /**
     * 按照树节点的类型获取树节点的值
     */
    private String getNodeName(HTreeNode treeNode, TreeMrType type) {
        HTreeNode node;
        if (treeNode.getType().equals(type.name())) {
            node = treeNode;
        } else {
            node = TreeUtil.getTargetParentNode(treeNode, type);
        }
        return node == null ? "" : node.getName();
    }

    /**
     * 将TreeMrNode转成HTreeNode
     */
    private HTreeNode transTreeNode(TreeMrNode item) {
        HTreeNode newNode = new HTreeNode();
        newNode.setId(item.getId());
        newNode.setName(item.getName());
        newNode.setType(item.getType().name());
        newNode.setOpenIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.VIEW.name(), item.getIcon(), IconSizeEnum.SIZE_16)));
        return newNode;
    }

    /**
     * 拼接创建view的sql
     */
    private String getSql() {
        List<HTabRowBean> data = this.table.getRowBeans(RowStatus.UPDATE);
        List<String> colNames = new ArrayList<>();
        for (HTabRowBean bean : data) {
            colNames.add(DbCmdStrUtil.toDbCmdStr(bean.getOldRow().get(COL_NAME), this.dbTypeEnum));
        }
        String colString = "*";
        if(colNames.size() > 0) {
        	colString = String.join(",", colNames);
        }
        return String.format("SELECT %s FROM %s.%s", colString, DbCmdStrUtil.toDbCmdStr(schemaName, this.dbTypeEnum), DbCmdStrUtil.toDbCmdStr(tableName, this.dbTypeEnum));
    }


    /**
     * 加载数据
     */
    private void loadTable(String schemaName, String tableName) throws SQLException {
        this.schemaName = schemaName;
        this.tableName = tableName;
        String key = String.format("%s##%s", this.schemaName, this.tableName);
        List<Map<String, String>> columns;
        if (maps.containsKey(key)) {
            columns = maps.get(key);
        } else {
            columns = viewMr.getCols(schemaName, tableName, this.conn);
            maps.put(key, columns);
        }
        table.load(columns, 1);
    }

    /**
     * 刷新面板
     *
     * @param treeNode 如果节点不是table就隐藏
     */
    private void refreshPanel(HTreeNode treeNode) {
        colPanel.getComp().setVisible(treeNode.getType().equals(TreeMrType.TABLE.name()));
    }

    /**
     * 点击保存按钮
     *
     * @param sql view sql
     */
    private void clickSaveBtn(String sql) {
        if (StringUtils.isBlank(sql)) {
            return;
        }
        maps.clear();
        this.dialog.hide();
        setTextEditorValue(sql);
    }

}
