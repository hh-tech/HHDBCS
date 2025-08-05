package com.hh.hhdb_admin.mgr.synonym2.form;

import com.hh.frame.common.base.AlignEnum;
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
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.*;

public abstract class SynBaseForm {
    protected String schema;
    protected Connection conn;
    protected JdbcBean jdbcBean;
    protected DBTypeEnum.Version version;
    protected DBTypeEnum dbTypeEnum;
    
    protected SelectBox typeBox;
    protected SearchSelectBox objectOwnerBox;
    protected TextInput objectNameInput;
    
    public CheckBoxInput publicInput;
    public TextInput nameInput;
    public HTextArea area;
    
    
    public SynBaseForm(JdbcBean jdbc, Connection conn,String schema) {
        this.jdbcBean = jdbc;
        this.conn = conn;
        this.schema = schema;
        this.version = jdbc.getCurrentVersion();
        this.dbTypeEnum = DriverUtil.getDbType(jdbcBean);
    
        nameInput = new TextInput();
        nameInput.getComp().setColumns(20);
        
        objectNameInput = new TextInput("objectName");
        
        List<TreeMrType> treeMrTypes;
        if (dbTypeEnum == DBTypeEnum.gaussdb || dbTypeEnum == DBTypeEnum.gbase || dbTypeEnum == DBTypeEnum.informix) {
            treeMrTypes = Arrays.asList(TreeMrType.TABLE, TreeMrType.VIEW, TreeMrType.SEQUENCE, TreeMrType.SYNONYM, TreeMrType.PROCEDURE, TreeMrType.FUNCTION);
        } else if (dbTypeEnum == DBTypeEnum.derby) {
            treeMrTypes = Arrays.asList(TreeMrType.TABLE, TreeMrType.VIEW);
        } else {
            treeMrTypes = Arrays.asList(TreeMrType.TABLE, TreeMrType.VIEW, TreeMrType.M_VIEW, TreeMrType.SEQUENCE,
                    TreeMrType.TYPE, TreeMrType.SYNONYM, TreeMrType.PROCEDURE, TreeMrType.FUNCTION);
        }
        typeBox = new SelectBox("typeBox");
        treeMrTypes.forEach(a -> typeBox.addOption(a.name(),a.name()));
        
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        conf.setLineWrap(true);
        area = new HTextArea(conf);
    }
    
    public abstract LastPanel getLastPanel();
    
    public abstract String getSql();
    
    public abstract String checkForm();
    
    protected void showTable() {
        try {
            if (StringUtils.isBlank(objectNameInput.getValue())) {
                PopDlgUtil.info(LangMgr.sLang(CsL.ENTER_KEYWORD));
                return;
            }
    
            HDialog tDialog = new HDialog(500, 500);
            tDialog.setWindowTitle(LangMgr.lang(Word2.CHOOSE));
            tDialog.setIconImage(IconFileUtil.getLogo());
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
    
            AbsObjQuery query = AbsObjQuery.getInstance( jdbcBean,(dbTypeEnum == DBTypeEnum.gbase || dbTypeEnum == DBTypeEnum.informix) ? schema : objectOwnerBox.getValue(),
                    Collections.singletonList(TreeMrType.valueOf(typeBox.getValue())), objectNameInput.getValue(),this.jdbcBean.getCurrentVersion());
            query.setIgnoreCase(true);
            query.doQuery();
            while (!query.getDone()) {
                SleepUtil.sleep100();
            }
            table.load(query.getResList(), 1);
    
            LastPanel lastPanel = new LastPanel();
            lastPanel.setHead(toolBar.getComp());
            lastPanel.setWithScroll(table.getComp());
            panel.setLastPanel(lastPanel);
            tDialog.setRootPanel(panel);
            tDialog.setStatusBar(new HBarPanel());
            tDialog.show();
        }catch (Exception e){
            PopDlgUtil.error(e);
        }
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
