package com.hh.hhdb_admin.mgr.obj_query.comp;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DateUtil;
import com.hh.frame.common.util.ErrorUtil;
import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.redis.base.RedisUtil;
import com.hh.frame.redis.db.FindKeyTool;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ctrl.SquareImgBtn;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
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

public class RedisObjQueryComp extends AbsQueryComp {
    public boolean isCluster;
    private TextInput maxInput;
   
    private FindKeyTool findKeyTool;
    private SquareImgBtn logBtn;

    
    public RedisObjQueryComp(JdbcBean jdbc, HWindow window) {
        super(jdbc, window);
        isCluster = RedisUtil.isCluster(CsVarUtil.conn);
    }

    public HPanel getRootPanel() {
        LastPanel lastPanel = new LastPanel();
        HPanel panel = new HPanel();
        panel.add(initSearchPanel());
        panel.add(initFilterPanel());
        panel.setLastPanel(initResTable());

        lastPanel.set(panel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        return rootPanel;
    }
    
    protected HPanel initSearchPanel() {
        keyWordInput.setValue(preKeyWord);
        searchBtn = new HButton(LangMgr.lang(Word2.QUERY)) {
            @Override
            protected void onClick() {
                search();
            }
        };
        searchBtn.setIcon(FaSolidEnum.MAGNIFYING_GLASS,16);
        stopBtn = new HButton(LangMgr.lang(Word2.STOP)) {
            @Override
            protected void onClick() {
                doStop();
            }
        };
        stopBtn.setIcon(FaSolidEnum.STOP,16);
        stopBtn.setEnabled(false);
        logBtn = new SquareImgBtn(16, FaSolidEnum.CIRCLE_INFO);
        HBarPanel barPanel = new HBarPanel();
        barPanel.add(searchBtn, stopBtn,logBtn);
    
        HPanel searchPanel = new HPanel(new HDivLayout(GridSplitEnum.C9, GridSplitEnum.C3));
        searchPanel.setTitle(LangMgr.lang(Word2.QUERY));
        searchPanel.add(keyWordInput, barPanel);
        return searchPanel;
    }
    
    /**
     * 初始化加载数量面板
     */
    private HPanel initFilterPanel() {
        HPanel filterPanel = new HPanel();
        filterPanel.setTitle(LangMgr.lang(Word4.LOAD_QUANTITY));
        filterPanel.add(getWithLabelInput(LangMgr.sLang(CsL.MAXIMUM_LOAD_QUANTITY),maxInput = new TextInput("max","1000"),new LabelInput()));
        return filterPanel;
    }
    
    /**
     * 初始化查询结果面板
     */
    private LastPanel initResTable() {
        resTable = new HTable();
        resTable.setRowHeight(25);
        resTable.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)), new DataCol("type", LangMgr.lang(Word2.TYPE)),new DataCol("schema", LangMgr.sLang(CsL.DATABASE_INFORMATION)));
        resTable.load(new ArrayList<>(), 0);
        ObjRightMenuActionHandler actionHandler = new ObjRightMenuActionHandler(dialog, resTable);
        actionHandler.setQueryComp(this);
        resTable.setRowPopMenu(new ObjTabPopMenu(jdbc) {
            @Override
            public void onItemClick(EventType value, Map<String, String> oldRow) {
                List<HTabRowBean> rowBeans = resTable.getSelectedRowBeans();
                if (rowBeans.size()>1) return;
                String schemaName = rowBeans.get(0).getOldRow().get("schema");
                String nodeName = rowBeans.get(0).getOldRow().get("name");
                String type = rowBeans.get(0).getOldRow().get("type");
                CsFolderBean treeNode = new CsFolderBean(nodeName);
                treeNode.setMrNode(new TreeMrNode(nodeName,TreeMrType.valueOf(type.toUpperCase())));
                actionHandler.setSchemaName(isCluster ? "DB0": schemaName);
                actionHandler.resolve(value, jdbc, null, treeNode);
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
            PopDlgUtil.info(dialog, LangMgr.sLang(CsL.ENTER_KEY_WORD));
            return;
        }
        String max = maxInput.getValue();
        if (StringUtils.isNotBlank(max) && SelectTableSqlUtil.isNumeric(max)) {
            if (Integer.parseInt(max) <= 0) {
                PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.sLang(CsL.MAXIMUM_LOAD_QUANTITY)+"不能为0!");
                return;
            }
        } else {
            PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.sLang(CsL.MAXIMUM_LOAD_QUANTITY)+"请输入正整数!");
            return;
        }
        
        try {
            if (worker != null) {
                doStop();
            }
            searchBtn.setText(LangMgr.sLang(CsL.IN_QUERY));
            searchBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            resTable.load(new ArrayList<>(), 0);
            logBtn.setToolTipText(null);
    
            findKeyTool = new FindKeyTool(jdbc,keyWord,Integer.parseInt(max));
            new Thread(findKeyTool).start();
            worker = new AbsQueryComp.BackgroundTask(findKeyTool) {
                @Override
                protected void process(List<List<Map<String, String>>> listList) {
                    boolean flag = listList.get(0).size() > 0 && listList.get(0).size() > resTable.getRowCount();
                    if (!flag) return;
                    listList.forEach(e -> resTable.load(e, 0));
                }

                @Override
                protected void done() {
                    resTable.load(toListMap(findKeyTool.getKeyList()), 0);
                    searchBtn.setEnabled(true);
                    stopBtn.setEnabled(false);
                    searchBtn.setText(LangMgr.lang(Word2.QUERY));
    
                    FindKeyTool.Info info=findKeyTool.getInfo();
                    StringBuilder sb=new StringBuilder("<html>");
                    sb.append(LangMgr.lang(Word4.START_TIME)+"："+ DateUtil.dateToStr(info.getBegin(), false)).append("<br>");
                    sb.append(LangMgr.lang(Word4.RUNNING_STATUS)+"："+info.getStatus().name()).append("<br>");
                    sb.append(LangMgr.lang(Word4.RUNNING_TIME)+"："+info.runningSec()+"s").append("<br>");
                    sb.append(LangMgr.lang(Word4.QUERY_CONDITION)+"："+info.getPattern()).append("<br>");
                    sb.append(LangMgr.sLang(CsL.MAXIMUM_NUMBER)+"："+info.getMax()).append("<br>");
                    sb.append(LangMgr.sLang(CsL.NUMBER_MATCHING_BARS)+"："+info.getMatchedNum()).append("<br>");
                    if(info.getEnd()!=null)
                        sb.append(LangMgr.lang(Word4.END_TIME)+"："+DateUtil.dateToStr(info.getEnd(), false)).append("<br>");
                    sb.append("错误信息：");
                    if(info.getException()==null) {
                        sb.append("无错误");
                    } else {
                        sb.append(ErrorUtil.getMessge(info.getException()));
                    }
                    sb.append("</html>");
                    logBtn.setToolTipText(sb.toString());
                    PopDlgUtil.info(dialog, LangMgr.lang(Word4.QUERY_COMPLETE));
                }
            };
            worker.execute();
        } catch (Exception e) {
            Lg.error(RedisObjQueryComp.class, e);
            PopDlgUtil.error(dialog,e);
        }
    }

    /**
     * 停止查询
     */
    protected void doStop() {
        if (findKeyTool != null) {
            findKeyTool.stop();
        }
        if (worker != null) {
            worker.cancel(true);
        }
        worker = null;
        searchBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }
    
}
