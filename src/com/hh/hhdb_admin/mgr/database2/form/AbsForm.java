package com.hh.hhdb_admin.mgr.database2.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.hhdb_admin.common.CsL;

import java.sql.Connection;


public abstract class AbsForm {
    protected Connection conn;
    protected JdbcBean jdbc;
    protected DBTypeEnum.Version version;
    protected DBTypeEnum dbTypeEnum;
    
    protected HPanel hPanel;
    public TextInput nameInput;
    
    public AbsForm(JdbcBean jdbc, Connection conn) {
        this.jdbc = jdbc;
        this.conn = conn;
        this.version = jdbc.getCurrentVersion();
        this.dbTypeEnum = DriverUtil.getDbType(jdbc);
    
        hPanel = new HPanel(getLayout());
        hPanel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        
        nameInput = new TextInput();
        nameInput.getComp().setColumns(20);
    }
    
    protected HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }
    
    protected HDivLayout getLayout() {
        HDivLayout layout = new HDivLayout(GridSplitEnum.C3);
        layout.setTopHeight(10);
        layout.setyGap(8);
        layout.setBottomHeight(10);
        layout.setRightWidth(100);
        return layout;
    }
    
    public abstract HPanel getPanel();

    public abstract String getSql();
    
    public void inform() {
    }
}
