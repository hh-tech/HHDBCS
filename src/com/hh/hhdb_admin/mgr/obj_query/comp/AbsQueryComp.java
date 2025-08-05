package com.hh.hhdb_admin.mgr.obj_query.comp;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JobStatus;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.obj_query.AbsObjQuery;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.redis.db.FindKeyTool;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;

import javax.swing.*;
import java.util.*;


public abstract class AbsQueryComp {
    public final DBTypeEnum dbTypeEnum;
    protected final String preKeyWord;
    protected final HWindow window;
    protected final JdbcBean jdbc;
   
    
    protected HTable resTable;
    protected TextInput keyWordInput = new TextInput();
    protected HButton searchBtn,stopBtn;
    protected HDialog dialog;
    
    protected SwingWorker<String, List<Map<String, String>>> worker;
    
    public AbsQueryComp(JdbcBean jdbc, HWindow window) {
        this(jdbc, "", window);
    }
    
    public AbsQueryComp(JdbcBean jdbc, String preKeyWord, HWindow window) {
        this.preKeyWord = preKeyWord;
        this.window = window;
        this.jdbc = jdbc;
        this.dbTypeEnum = DriverUtil.getDbType(jdbc);
    
        dialog = new HDialog(window, 800, 800) {
            @Override
            protected void closeEvent() {
                doStop();
            }
        };
        dialog.setWindowTitle(LangMgr.sLang(CsL.OBJ_QUERY));
        dialog.setRootPanel(getRootPanel());
        dialog.setIconImage(IconFileUtil.getLogo());
        ((JDialog) dialog.getWindow()).setResizable(true);
        dialog.setStatusBar(new HBarPanel());
    }
    
    public void show() {
        dialog.show();
    }
    
    /**
     * 查询
     */
    public abstract void search();
    
    public abstract HPanel getRootPanel();
    
    /**
     * 停止查询
     */
    protected abstract void doStop();
    
    /**
     * 初始化查询面板
     */
    protected HPanel initSearchPanel() {
        HPanel searchPanel = new HPanel(new HDivLayout(GridSplitEnum.C9, GridSplitEnum.C3));
        searchPanel.setTitle(LangMgr.lang(Word2.QUERY));
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
        keyWordInput.setValue(preKeyWord);
        HBarPanel barPanel = new HBarPanel();
        barPanel.add(searchBtn, stopBtn);
        searchPanel.add(keyWordInput, barPanel);
        stopBtn.setEnabled(false);
        return searchPanel;
    }
    
    protected HGridPanel getWithLabelInput(String label, AbsInput input, AbsInput input2) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C4);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        gridPanel.setComp(3, input2);
    
        return gridPanel;
    }
    
    protected boolean checkIsPackageChild(String type) {
        return type.equals(TreeMrType.PACKAGE_FUNCTION.name()) || type.equals(TreeMrType.PACKAGE_PROCEDURE.name());
    }
    
    public static class BackgroundTask extends SwingWorker<String, List<Map<String, String>>> {
        private AbsObjQuery query;
        private FindKeyTool find;
        
        public BackgroundTask(AbsObjQuery query) {
            this.query = query;
        }
    
        /**
         * redis对象查询
         * @param find
         */
        public BackgroundTask(FindKeyTool find) {
            this.find = find;
        }
        
        @Override
        protected String doInBackground() throws InterruptedException {
            if (null != query) {
                while (!query.getDone() && !isCancelled()) {
                    publish(query.getResList());
                    doSomething();
                }
            } else {
                while (find.getInfo().getStatus() != JobStatus.DONE && !isCancelled()) {
                    publish(toListMap(find.getKeyList()));
                    doSomething();
                }
            }
            return "Cancelled";
        }
        
        protected void doSomething() throws InterruptedException {
            Thread.sleep(200);
        }
        
        protected List<Map<String, String>> toListMap(List<FindKeyTool.KeyBean> beanList){
            List<Map<String, String>> resList = new ArrayList<>();
            for (FindKeyTool.KeyBean keyBean : new LinkedList<>(beanList)) {
                Map<String, String> itemMap = new HashMap<>();
                itemMap.put("name", keyBean.getKey());
                itemMap.put("type", keyBean.getType().name());
                itemMap.put("schema", keyBean.getDbInfo());
                resList.add(itemMap);
            }
            return resList;
        }
    }
}
