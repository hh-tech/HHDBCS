package com.hh.hhdb_admin.mgr.obj_query;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.SwingWorker;

import com.hh.frame.swingui.view.tab.HTabRowBean;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.obj_query.AbsObjQuery;
import com.hh.frame.create_dbobj.obj_query.ObjQueryUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.CheckGroupInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.obj_query.handler.ObjRightMenuActionHandler;

/**
 * @author Jiang
 * @date 2021/7/9 10:53
 */
public class ObjQueryComp {

    private static final String DOMAIN_NAME = ObjQueryComp.class.getName();

    static {
        try {
            LangMgr2.loadMerge(ObjQueryComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final String preKeyWord;
    private final LoginBean loginBean;
    private final DBTypeEnum dbTypeEnum;
    private final TextInput keyWordInput = new TextInput();
    private HButton searchBtn;
    private HButton stopBtn;
    private final SelectBox schemaSelectBox = new SelectBox();
    private CheckBoxInput ignoreCaseBox;
    private HTable resTable;
    private CheckGroupInput typeGroup;
    private AbsObjQuery query;

    private HDialog dialog;
    SwingWorker<String, List<Map<String, String>>> worker;

    public ObjQueryComp(LoginBean loginBean) {
        this(loginBean, "");
    }

    public ObjQueryComp(LoginBean loginBean, String preKeyWord) {
        this.preKeyWord = preKeyWord;
        LoginBean newLoginBean = new LoginBean();
        newLoginBean.setOriginalJdbc(loginBean.getJdbc());
        newLoginBean.setJdbc(newLoginBean.getOriginalJdbc());
        newLoginBean.setSshAuth(loginBean.isSshAuth());
        newLoginBean.setConn(loginBean.getConn());
        this.loginBean = newLoginBean;
        this.dbTypeEnum = DriverUtil.getDbType(newLoginBean.getJdbc());
        init();
    }

    private void init() {
        ignoreCaseBox = new CheckBoxInput("ignoreCase", getLang("ignoreCase"));
        ignoreCaseBox.setValue("true");
        dialog = new HDialog(StartUtil.parentFrame, 800, 800) {
            @Override
            protected void closeEvent() {
                doStop();
            }
        };
        dialog.setWindowTitle(getLang("objQuery"));
        dialog.setRootPanel(getRootPanel());
        dialog.setIconImage(IconFileUtil.getLogo());
        ((JDialog) dialog.getWindow()).setResizable(true);

    }

    public void show() {
        dialog.show();
    }

    public HPanel getRootPanel() {
        LastPanel lastPanel = new LastPanel();
        HPanel panel = new HPanel();
        panel.add(initSearchPanel());
        panel.add(initFilterPanel());
        panel.add(initTypePanel());
        panel.setLastPanel(initResTable());

        lastPanel.set(panel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        return rootPanel;
    }

    /**
     * @return 查询结果面板
     */
    private LastPanel initResTable() {
        resTable = new HTable();
        resTable.setRowHeight(25);
        DataCol dataCol = new DataCol("object_name", "object_name");
        dataCol.setShow(false);
        resTable.addCols(dataCol, new DataCol("name", getLang("name")), new DataCol("type", getLang("type")));
        resTable.load(new ArrayList<>(), 0);
        ObjRightMenuActionHandler actionHandler = new ObjRightMenuActionHandler(dialog, resTable);
        actionHandler.setQueryComp(this);
        resTable.setRowPopMenu(new ObjTabPopMenu(loginBean.getJdbc()) {
            @Override
            public void onItemClick(EventType value, Map<String, String> oldRow) {
                try {
                    if (loginBean.getConn().isClosed()) {
                        loginBean.setConn(StartUtil.getLoginBean().getConn());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String schemaName = schemaSelectBox.getValue();
                //todo 完善右键点击逻辑
                HTreeNode schemaNode = new HTreeNode();
                schemaNode.setName(schemaName);
                schemaNode.setType(TreeMrType.SCHEMA.name());
                List<HTabRowBean> rowBeans = resTable.getSelectedRowBeans();
                HTreeNode[] treeNodes = new HTreeNode[rowBeans.size()];
                int i = 0;
                for (HTabRowBean rowBean : rowBeans) {
                    String nodeName = rowBean.getOldRow().get("name");
                    String type = rowBean.getOldRow().get("type");
                    HTreeNode treeNode = new HTreeNode();
                    treeNode.setType(type);
                    treeNode.setName(nodeName);
                    treeNode.setId(oldRow.get("object_name"));
                    treeNode.setParentHTreeNode(schemaNode);
                    treeNodes[i] = treeNode;
                    i++;
                }
                actionHandler.setSchemaName(schemaName);
                actionHandler.setTableName(oldRow.get("object_name"));
                actionHandler.resolve(value.name().toLowerCase(Locale.ROOT), loginBean, null, treeNodes);
            }
        });
        schemaSelectBox.addListener(e -> {
            String schemaName = schemaSelectBox.getValue();
            actionHandler.setSchemaName(schemaName);
            loginBean.getJdbc().setSchema(schemaName);
        });
        LastPanel tablePanel = new LastPanel();
        tablePanel.setTitle(getLang("queryResults"));
        tablePanel.setWithScroll(resTable.getComp());
        return tablePanel;
    }

    /**
     * 初始化查询面板
     *
     * @return 查询面包
     */
    protected HPanel initSearchPanel() {
        HPanel searchPanel = new HPanel(new HDivLayout(GridSplitEnum.C9, GridSplitEnum.C3));
        searchPanel.setTitle(getLang("search"));
        searchBtn = new HButton(getLang("search")) {
            @Override
            protected void onClick() {
                search();
            }
        };
        searchBtn.setIcon(getIcon("query"));
        stopBtn = new HButton(getLang("stop")) {
            @Override
            protected void onClick() {
                doStop();
            }
        };
        stopBtn.setIcon(getIcon("stop"));
        keyWordInput.setValue(preKeyWord);
        HBarPanel barPanel = new HBarPanel();
        barPanel.add(searchBtn, stopBtn);
        searchPanel.add(keyWordInput, barPanel);
        stopBtn.setEnabled(false);
        return searchPanel;
    }

    /**
     * 初始化筛选面板
     *
     * @return 筛选面板
     */
    private HPanel initFilterPanel() {
        HPanel filterPanel = new HPanel();
        filterPanel.setTitle(getLang("screen"));
        AbsTreeMr.genTreeMr(loginBean.getJdbc()).ifPresent(item -> {
            TreeMrType type;
            if (dbTypeEnum == DBTypeEnum.oracle) {
                type = TreeMrType.ROOT_USER_GROUP;
            } else if (dbTypeEnum == DBTypeEnum.mysql) {
                type = TreeMrType.ROOT_DATABASE_GROUP;
            } else if (dbTypeEnum == DBTypeEnum.hhdb || dbTypeEnum == DBTypeEnum.pgsql) {
                type = TreeMrType.DATA_ALL_SCHEMA;
            } else {
                type = TreeMrType.DATA_MODEL_SCHEMA_GROUP;
            }
            TreeMrNode treeMrNode = new TreeMrNode("schema", type, "schemaindex.png");
            Connection conn = null;
            try {
                conn = ConnUtil.getConn(loginBean.getJdbc());
                item.getChildNodeList(treeMrNode, conn, null).forEach(node -> schemaSelectBox.addOption(node.getName(), node.getName()));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    ConnUtil.close(conn);
                }
            }
            schemaSelectBox.setValue(loginBean.getJdbc().getSchema());
        });

        filterPanel.add(getWithLabelInput(getLang("schemaName"), schemaSelectBox,
                dbTypeEnum != DBTypeEnum.sqlserver ? ignoreCaseBox : new LabelInput()));
        return filterPanel;
    }

    /**
     * 初始化对象类型选择面板
     *
     * @return 类型选择面板
     */
    private HPanel initTypePanel() {
        HPanel rangePanel = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C3, GridSplitEnum.C3));
        typeGroup = new CheckGroupInput("searchType", rangePanel);
        for (TreeMrType treeMrType : ObjQueryUtil.getFullTypeList(dbTypeEnum)) {
            CheckBoxInput checkBoxInput = new CheckBoxInput(treeMrType.name(), treeMrType.name());
            if (treeMrType == TreeMrType.TABLE) {
                checkBoxInput.getComp().setSelected(true);
            }
            typeGroup.add(checkBoxInput);
        }
        HPanel rangeOptionPanel = new HPanel(new HDivLayout(GridSplitEnum.C10, GridSplitEnum.C1));
        HButton selectAllBtn = new HButton(getLang("selectAll")) {
            @Override
            protected void onClick() {
                rangePanel.getSubCompList().forEach(item -> ((CheckBoxInput) item).setValue("true"));
            }
        };
        HButton unSelectAllBtn = new HButton(getLang("cancel")) {
            @Override
            protected void onClick() {
                rangePanel.getSubCompList().forEach(item -> ((CheckBoxInput) item).setValue("false"));
            }
        };
        rangeOptionPanel.add(null, selectAllBtn, unSelectAllBtn);
        HPanel typePanel = new HPanel();
        typePanel.setTitle(getLang("objType"));
        typePanel.add(rangeOptionPanel, rangePanel);
        return typePanel;
    }

    /**
     * 查询
     */
    public void search() {
        String keyWord = keyWordInput.getValue();
        if (StringUtils.isBlank(keyWord)) {
            PopPaneUtil.error(dialog.getWindow(), getLang("enterKeyword"));
            return;
        }
        String schemaName = schemaSelectBox.getValue();
        if (StringUtils.isBlank(schemaName)) {
            PopPaneUtil.error(dialog.getWindow(), getLang("selectSchema"));
            return;
        }
        if (typeGroup.getValues().isEmpty()) {
            PopPaneUtil.error(dialog.getWindow(), getLang("selectType"));
            return;
        }
        List<TreeMrType> treeMrTypeList = new ArrayList<>(typeGroup.getValues()).stream().map(TreeMrType::valueOf).collect(Collectors.toList());
        try {
            if (worker != null) {
                doStop();
            }
            searchBtn.setText(getLang("querying"));
            searchBtn.setEnabled(false);
            resTable.load(new ArrayList<>(), 0);
            query = AbsObjQuery.getInstance(dbTypeEnum, loginBean.getJdbc(), schemaName, treeMrTypeList, keyWord);
            query.setIgnoreCase(ignoreCaseBox.isChecked());
            query.doQuery();
            stopBtn.setEnabled(true);
            worker = new BackgroundTask(query) {
                @Override
                protected void process(List<List<Map<String, String>>> listList) {
                    boolean flag = listList.get(0).size() > 0 && listList.get(0).size() > resTable.getRowCount();
                    if (!flag) {
                        return;
                    }
                    for (List<Map<String, String>> maps : listList) {
                        List<Map<String, String>> data = new ArrayList<>();
                        maps.forEach(item -> {
                            if (checkIsPackageChild(item.get("type"))) {
                                item.put("name", item.get("packageName") + "." + item.get("name"));
                            }
                            data.add(item);
                        });
                        resTable.load(data, 0);
                    }
                }

                @Override
                protected void done() {
                    resTable.load(query.getResList(), 0);
                    searchBtn.setEnabled(true);
                    stopBtn.setEnabled(false);
                    searchBtn.setText(getLang("search"));
                    PopPaneUtil.info(dialog.getWindow(), getLang("queryComplete"));
                }
            };
            worker.execute();
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(dialog.getWindow(), e.getMessage());
        }
    }

    private static class BackgroundTask extends SwingWorker<String, List<Map<String, String>>> {
        private final AbsObjQuery query;


        public BackgroundTask(AbsObjQuery query) {
            this.query = query;
        }

        @Override
        protected String doInBackground() throws InterruptedException {
            while (!query.getDone() && !isCancelled()) {
                publish(query.getResList());
                doSomething();
            }
            return "Cancelled";
        }

        protected void doSomething() throws InterruptedException {
            Thread.sleep(200);
        }
    }

    /**
     * 停止查询
     */
    private void doStop() {
        if (query != null) {
            query.stop();
        }
        if (worker != null) {
            worker.cancel(true);
        }
        worker = null;
        searchBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    public static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    public static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.OBJ_QUERY.name(), name, IconSizeEnum.SIZE_16));
    }

    private HGridPanel getWithLabelInput(String label, AbsInput input, AbsInput input2) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C4);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        gridPanel.setComp(3, input2);
        return gridPanel;
    }

    private boolean checkIsPackageChild(String type) {
        return type.equals(TreeMrType.PACKAGE_FUNCTION.name()) || type.equals(TreeMrType.PACKAGE_PROCEDURE.name());
    }
}
