package com.hh.hhdb_admin.mgr.trigger2.form;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.create_dbobj.obj_query.AbsObjQuery;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;


public abstract class TriggerForm {
    protected Connection conn;
    protected JdbcBean jdbc;
    protected String schemaName;
    protected DBTypeEnum dbType;
    protected DBTypeEnum.Version version;
    
    public TextInput nameInput;
    protected TextInput objectNameInput;
    protected SelectBox triggerBox;
    public CheckBoxInput enableInput;
    public HTextArea area;
    

    public TriggerForm(Connection conn, JdbcBean jdbc, String schemaName) throws Exception {
        this.conn = conn;
        this.jdbc = jdbc;
        this.schemaName = schemaName;
        this.dbType = DriverUtil.getDbType(jdbc);
        this.version = jdbc.getCurrentVersion();

        nameInput = new TextInput();
        nameInput.getComp().setColumns(20);
        objectNameInput = new TextInput();
    
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        conf.setLineWrap(true);
        area = new HTextArea(conf);
    }

    public abstract LastPanel getLastPanel();
    
    public abstract String getSql();
    
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        return null;
    }

    protected void showTable(String schema,String type) {
        HDialog tDialog = new HDialog(500, 500);
        tDialog.setWindowTitle(LangMgr.lang(Word2.CHOOSE));
        tDialog.setIconImage(IconFileUtil.getLogo());
        try {
            HPanel panel = new HPanel();
            HTable table = new HTable();
            table.setCellEditable(false);
            table.setSingleSelection(true);
            table.getComp().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() >= 2) {
                        Map<String, String> map = table.getSelectedRowBeans().get(0).getOldRow();
                        if (null != map) {
                            objectNameInput.setValue(map.get("name"));
                            tDialog.dispose();
                        }
                    }
                }
            });
            table.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)),new DataCol("type", LangMgr.lang(Word2.TYPE)));
            SearchToolBar toolBar = new SearchToolBar(table);
            loadData(table,schema,type);
            LastPanel lastPanel = new LastPanel();
            lastPanel.setHead(toolBar.getComp());
            lastPanel.setWithScroll(table.getComp());
            panel.setLastPanel(lastPanel);
            tDialog.setRootPanel(panel);
            tDialog.setStatusBar(new HBarPanel());
            tDialog.show();
        }catch (Exception e){
            PopDlgUtil.logErr(getClass(),tDialog, e);
        }
    }
    
    private void loadData(HTable table,String schema,String type) throws Exception {
        List<TreeMrType> treeMrTypes = Collections.singletonList(TreeMrType.valueOf(type));
        AbsObjQuery query = AbsObjQuery.getInstance( jdbc,schema,treeMrTypes, objectNameInput.getValue(),version);
        query.setIgnoreCase(true);
        query.doQuery();
        while (!query.getDone()) {
            SleepUtil.sleep100();
        }
        table.load(query.getResList(), 1);
    }
    
    protected HDivLayout getLayout() {
        HDivLayout layout = new HDivLayout(GridSplitEnum.C4);
        layout.setMaxWidth(600);
        return layout;
    }
    
    protected WithLabelInput getLabelInput(String label, AbsInput input) {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C4, AlignEnum.RIGHT,AlignEnum.LEFT);
        return new WithLabelInput(labelConf, new LabelInput(label), input);
    }
}
