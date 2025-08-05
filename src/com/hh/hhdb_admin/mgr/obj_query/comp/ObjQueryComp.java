package com.hh.hhdb_admin.mgr.obj_query.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.obj_query.AbsObjQuery;
import com.hh.frame.create_dbobj.obj_query.ObjQueryUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.CheckGroupInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SearchSelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.obj_query.ObjTabPopMenu;
import com.hh.hhdb_admin.mgr.obj_query.handler.ObjRightMenuActionHandler;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ObjQueryComp extends AbsQueryComp{
    private SearchSelectBox schemaSelectBox;
    private CheckBoxInput ignoreCaseBox;
    private CheckGroupInput typeGroup;
    private AbsObjQuery query;
    private ObjRightMenuActionHandler actionHandler;
    
    public ObjQueryComp(JdbcBean jdbc, HWindow window) {
        super(jdbc, window);
    }

    public HPanel getRootPanel() {
        LastPanel lastPanel = new LastPanel();
        HPanel panel = new HPanel();
        panel.add(initSearchPanel());
        LastPanel lp = initResTable();
        panel.add(initFilterPanel());
        panel.add(initTypePanel());
        panel.setLastPanel(lp);

        lastPanel.set(panel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        return rootPanel;
    }
    
    /**
     * 初始化筛选面板
     *
     * @return 筛选面板
     */
    private HPanel initFilterPanel() {
        HPanel filterPanel = new HPanel();
        filterPanel.setTitle(LangMgr.lang(Word2.SCREEN));
        schemaSelectBox = new SearchSelectBox() {
            @Override
            protected void onValueChanged(String value) {
                String schemaName = schemaSelectBox.getValue();
                actionHandler.setSchemaName(schemaName);
                jdbc.setSchema(schemaName);
            }
        };
        try {
            for(String name:SqlWinUtil.getSchemaNameList(dbTypeEnum,jdbc.getCurrentVersion(), CsVarUtil.conn)) {
                schemaSelectBox.addOption(name, name);
            }
            String currSchema;
            if(StringUtils.isNoneBlank(jdbc.getSchema())) {
                currSchema= SqlStrUtil.rmQuote4Sel(jdbc.getSchema(), dbTypeEnum, jdbc.getCurrentVersion());
            }else {
                currSchema=SqlWinUtil.getCurrSchema(dbTypeEnum, jdbc.getCurrentVersion(), CsVarUtil.conn);
            }
            schemaSelectBox.setValue(currSchema);
        }catch (Exception e){
            Lg.error(getClass(), e);
        }

        ignoreCaseBox = new CheckBoxInput("ignoreCase", LangMgr.sLang(CsL.IGNORE_CASE));
        ignoreCaseBox.setValue("true");
        filterPanel.add(getWithLabelInput(LangMgr.lang(Word2.SCHEMA)+" : ", schemaSelectBox,
                (dbTypeEnum != DBTypeEnum.sqlserver && dbTypeEnum != DBTypeEnum.hive) ? ignoreCaseBox : new LabelInput()));
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
        HButton selectAllBtn = new HButton(LangMgr.lang(Word2.CHECK_ALL)) {
            @Override
            protected void onClick() {
                rangePanel.getSubCompList().forEach(item -> ((CheckBoxInput) item).setValue("true"));
            }
        };
        HButton unSelectAllBtn = new HButton(LangMgr.lang(Word2.CANCEL)) {
            @Override
            protected void onClick() {
                rangePanel.getSubCompList().forEach(item -> ((CheckBoxInput) item).setValue("false"));
            }
        };
        rangeOptionPanel.add(null, selectAllBtn, unSelectAllBtn);
        HPanel typePanel = new HPanel();
        typePanel.setTitle(LangMgr.lang(Word2.TYPE));
        typePanel.add(rangeOptionPanel, rangePanel);
        return typePanel;
    }
    
    /**
     * @return 查询结果面板
     */
    private LastPanel initResTable() {
        resTable = new HTable();
        resTable.setRowHeight(25);
        DataCol dataCol = new DataCol("object_name", "object_name");
        dataCol.setShow(false);
        resTable.addCols(dataCol, new DataCol("name", LangMgr.lang(Word2.NAME)), new DataCol("type", LangMgr.lang(Word2.TYPE)));
        resTable.load(new ArrayList<>(), 0);
        actionHandler = new ObjRightMenuActionHandler(dialog, resTable);
        actionHandler.setQueryComp(this);
        resTable.setRowPopMenu(new ObjTabPopMenu(jdbc) {
            @Override
            public void onItemClick(EventType value, Map<String, String> oldRow) {
                String schemaName = schemaSelectBox.getValue();
                CsFolderBean schemaNode = new CsFolderBean(schemaName);
//                schemaNode.setType(TreeMrType.SCHEMA);
                schemaNode.setMrNode(new TreeMrNode(schemaName,TreeMrType.SCHEMA));
                List<HTabRowBean> rowBeans = resTable.getSelectedRowBeans();
                CsFolderBean[] treeNodes = new CsFolderBean[rowBeans.size()];
                int i = 0;
                for (HTabRowBean rowBean : rowBeans) {
                    String nodeName = rowBean.getOldRow().get("name");
                    String type = rowBean.getOldRow().get("type");
                    CsFolderBean treeNode = new CsFolderBean(nodeName);
                    TreeMrType treeType=TreeMrType.valueOf(type.toUpperCase());
//                    treeNode.setType(TreeMrType.valueOf(type.toUpperCase()));
                    TreeMrNode node=new TreeMrNode(nodeName,treeType);
                    node.setId(oldRow.get("object_name"));
//                    treeNode.setObjectId(oldRow.get("object_name"));
                    treeNode.setFolder(schemaNode);
                    treeNode.setMrNode(node);
                    treeNodes[i] = treeNode;
                    i++;
                }
                actionHandler.setSchemaName(schemaName);
                actionHandler.setTableName(oldRow.get("object_name"));
//                actionHandler.resolve(value.name().toLowerCase(Locale.ROOT), jdbc, null, treeNodes);
                actionHandler.resolve(value, jdbc, null, treeNodes);

            }
        });
        LastPanel tablePanel = new LastPanel();
        tablePanel.setTitle(LangMgr.lang(Word2.RESULT));
        tablePanel.setWithScroll(resTable.getComp());
        return tablePanel;
    }
    
    /**
     * 查询
     */
    public void search() {
        String keyWord = keyWordInput.getValue();
        if (StringUtils.isBlank(keyWord)) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.ENTER_KEYWORD));
            return;
        }
        String schemaName = schemaSelectBox.getValue();
        if (StringUtils.isBlank(schemaName)) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.SELECT_SCHEMA));
            return;
        }
        if (typeGroup.getValues().isEmpty()) {
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.SELECT_TYPE));
            return;
        }
        List<TreeMrType> treeMrTypeList = new ArrayList<>(typeGroup.getValues()).stream().map(TreeMrType::valueOf).collect(Collectors.toList());
        try {
            if (worker != null) {
                doStop();
            }
            searchBtn.setText(LangMgr.sLang(CsL.IN_QUERY));
            searchBtn.setEnabled(false);
            resTable.load(new ArrayList<>(), 0);
//            jdbc.setCurSessionSchema(schemaName);
            query = AbsObjQuery.getInstance(jdbc, schemaName, treeMrTypeList, keyWord,jdbc.getCurrentVersion());
            query.setIgnoreCase(ignoreCaseBox.isChecked());
            query.doQuery();
            stopBtn.setEnabled(true);
            worker = new AbsQueryComp.BackgroundTask(query) {
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
                    searchBtn.setText(LangMgr.lang(Word2.QUERY));
                    PopDlgUtil.info(dialog, LangMgr.lang(Word4.QUERY_COMPLETE));
                }
            };
            worker.execute();
        } catch (Exception e) {
            Lg.error(ObjQueryComp.class, e);
            PopDlgUtil.error(dialog,e);
        }
    }

    /**
     * 停止查询
     */
    protected void doStop() {
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
}
