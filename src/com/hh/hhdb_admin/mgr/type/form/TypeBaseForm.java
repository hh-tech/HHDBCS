package com.hh.hhdb_admin.mgr.type.form;


import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.type.TypeBean;

import java.sql.Connection;

public abstract class TypeBaseForm {
    protected Connection conn;
    protected TypeBean typeBean;
    
    public HTextArea area;
    
    public TypeBaseForm(Connection conn, TypeBean typeBean) {
        try {
            this.conn = conn;
            this.typeBean = typeBean;
            
            HTAreaConf conf = new HTAreaConf();
            conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
            conf.setLineWrap(true);
            area = new HTextArea(conf);
            
        } catch (Exception e) {
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }
        
    }
    
    public abstract LastPanel getLastPanel();
    
    public abstract void upInitForm() throws Exception;
    
    public abstract void save();
}
