package com.hh.hhdb_admin.mgr.type.form;

import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.dbobj3.ora.OraSchemaSubObj;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.type.TypeBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;


public class OrTypeForm extends TypeBaseForm {
    private TextAreaInput messageArea;
    
    public OrTypeForm(Connection conn, TypeBean typeBean) {
        super(conn, typeBean);
        
        messageArea = new TextAreaInput();
        messageArea.setEnabled(false);
    }
    
    @Override
    public LastPanel getLastPanel() {
        String text = "";
        if (StringUtils.isEmpty(text)) {
            if (AllDbObjType.TYPE.name().equals(typeBean.getType())) {
                text = String.format("create or replace type %s.%s as object (\n\n);", typeBean.getSchema(), typeBean.getName());
            } else {
                text = String.format("create or replace type body %s.%s is \n\nend;", typeBean.getSchema(), typeBean.getName());
            }
        }
        area.setText(text);
        area.getRSyntaxTextArea().setRows(20);
        
        LastPanel lastPanel2 = new LastPanel();
        lastPanel2.setHead(area.getComp());
        lastPanel2.set(messageArea.getComp());
        return lastPanel2;
    }
    
    @Override
    public void upInitForm() throws Exception {
        OraSchemaSubObj oraSchemaSubObj = new OraSchemaSubObj(CsVarUtil.jdbc.getCurrentVersion(), conn, typeBean.getSchema(), typeBean.getName());
        String text = oraSchemaSubObj.getCreateSql(AllDbObjType.valueOf(typeBean.getType()));
        area.setText(text);
    }
    
    @Override
    public void save() {
        try {
            SqlExeUtil.executeUpdate(conn, area.getText());
            String errorMsg = getErrorMsg();
            if (StringUtils.isEmpty(errorMsg)) {
                messageArea.setValue(LangMgr.sLang(CsL.SAVE_COMPILED_SUCCESS));
            } else {
                messageArea.setValue(LangMgr.sLang(CsL.COMPILE_FAILED) + "\n" + errorMsg);
            }
        } catch (Exception e) {
            Lg.error(OrTypeForm.class, e);
            messageArea.setValue(e.getMessage());
        }
    }
    
    private String getErrorMsg() throws Exception {
        String sql = "select line, type, text from SYS.USER_ERRORS where type = '%s' and name = '%s' order by type, line";
        String type = typeBean.getType().replaceAll("_", "").toLowerCase();
        List<Map<String, Object>> maps = SqlQueryUtil.select(conn, String.format(sql, type, SqlStrUtil.rmQuote4Sel(typeBean.getName(), CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion())));
        
        StringBuilder errorText = new StringBuilder();
        for (Map<String, Object> map : maps) {
            String line = "0".equals((map.get("line") + "")) ? "1" : (map.get("line") + "");
            errorText.append(map.get("type")).append("  ").append(line).append("  ")
                    .append((map.get("text") + "").replace("\n", "")).append("\n");
        }
        return errorText.toString();
    }
    
}
