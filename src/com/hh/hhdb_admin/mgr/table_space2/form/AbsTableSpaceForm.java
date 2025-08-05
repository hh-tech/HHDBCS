package com.hh.hhdb_admin.mgr.table_space2.form;

import java.sql.Connection;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;


public abstract class AbsTableSpaceForm {
    protected Connection conn;
    protected JdbcInfo jdbc;
    protected DBTypeEnum dbType;
    protected DBTypeEnum.Version vers;
    
    public TextInput nameInput;
    public HTextArea area;
    public HButton addBtn;
    public HButton delBtn;
    
    public AbsTableSpaceForm(Connection conn,JdbcInfo jdbc) {
        this.conn = conn;
        this.dbType = DriverUtil.getDbType(jdbc);
        this.vers = jdbc.getCurrentVersion();
        
        nameInput = new TextInput();
        nameInput.getComp().setColumns(20);
    
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        conf.setLineWrap(true);
        area = new HTextArea(conf);
    }
    
    public abstract LastPanel getLastPanel();
    
    /**
     * 检查form
     */
    public abstract String checkForm();
    
    /**
     * 获取sql
     */
    public abstract String getSql();

    
    protected WithLabelInput getLabelInput(String label, AbsInput input) {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C4, AlignEnum.RIGHT,AlignEnum.LEFT);
        return new WithLabelInput(labelConf, new LabelInput(label), input);
    }
    
    protected HPanel getLabelHPanel(String label, AbsHComp...hcomps) {
        HDivLayout layout;
        if (hcomps.length == 2) {
            layout = new HDivLayout(GridSplitEnum.C4,GridSplitEnum.C6);
        } else {
            layout = new HDivLayout(GridSplitEnum.C4);
        }
        HPanel panel = new HPanel(layout);
        panel.add(new LabelInput(label));
        panel.add(hcomps);
        return panel;
    }
    
    protected HeaderConfig newHeaderConfig(String name){
        HeaderConfig headerConfig = new HeaderConfig(name);
        headerConfig.setTitleEditable(false);
        headerConfig.setFixTab(true);
        headerConfig.setDetachEnabled(false);
        return headerConfig;
    }
}
