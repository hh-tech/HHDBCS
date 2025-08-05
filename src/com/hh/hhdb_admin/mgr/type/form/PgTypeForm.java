package com.hh.hhdb_admin.mgr.type.form;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.hhdb_admin.mgr.type.TypeBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;


public class PgTypeForm extends TypeBaseForm {
    private TextAreaInput messageArea;
    
    public PgTypeForm(Connection conn, TypeBean typeBean) {
        super(conn, typeBean);
        
        messageArea = new TextAreaInput();
        messageArea.setEnabled(false);
    }
    
    @Override
    public LastPanel getLastPanel() {
        String text = "";
        if (StringUtils.isEmpty(text)) {
            text = String.format("create type %s.%s (\n\tINPUT = \"\",\n\tOUTPUT = \"\"\n);", typeBean.getSchema(), typeBean.getName());
        }
        area.setText(text);
        area.getRSyntaxTextArea().setRows(20);
        
        LastPanel lastPanel2 = new LastPanel();
        lastPanel2.setHead(area.getComp());
        lastPanel2.set(messageArea.getComp());
        return lastPanel2;
    }
    
    @Override
    public void upInitForm() {
    }
    
    @Override
    public void save() {
        try {
            SqlExeUtil.executeUpdate(conn, area.getText());
            messageArea.setValue(LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
        } catch (Exception e) {
            Lg.error(PgTypeForm.class, e);
            messageArea.setValue(e.getMessage());
        }
    }
    
}
