package com.hh.hhdb_admin.mgr.partition.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

public abstract class AbsParPanel {
    protected Connection conn;
    protected JdbcBean jdbc;
    protected String schemaName;
    protected String tabName;
    protected DBTypeEnum dbType;
    protected DBTypeEnum.Version version;
    
    protected TextInput firstTextInput;
    protected TextInput secondTextInput;
    
    public TextInput nameInput;
    public HTextArea area;
    
    public String parType = "RANGE";
    
    
    public AbsParPanel(Connection conn, JdbcBean jdbc, String schemaName, String tabName) throws Exception {
        this.conn = conn;
        this.jdbc = jdbc;
        this.schemaName = schemaName;
        this.tabName = tabName;
        this.dbType = DriverUtil.getDbType(jdbc);
        this.version = jdbc.getCurrentVersion();
        
        nameInput = new TextInput();
        nameInput.getComp().setColumns(20);
        
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        conf.setLineWrap(true);
        area = new HTextArea(conf);
    }
    
    public abstract LastPanel getLastPanel();
    
    public abstract String getSql();
    
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.FILL_TABLE_NAME);
        }
        return null;
    }
    
    protected WithLabelInput getLabelInput(String label, AbsInput input) {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C4, AlignEnum.RIGHT, AlignEnum.LEFT);
        return new WithLabelInput(labelConf, new LabelInput(label), input);
    }
}
