package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.hhdb.HHdbUser;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

public class HHFunForm extends FunBaseForm {
    private SelectBox fh,owner;
    private TextInput name;
    private TextAreaInput commentInput;
    
    private String prefix;
    
    public HHFunForm(AbsFunMr funMr, Connection conn, JdbcBean jdbcBean, boolean isEdit) throws Exception {
        super(funMr, conn, jdbcBean);
        this.isEdit = isEdit;
        prefix = dbType == DBTypeEnum.hhdb ? "hh" : "pg";
    }
    
    @Override
    public LastPanel getParaPanel() throws Exception {
        HPanel hPanel = new HPanel();
        hPanel.add(getPanel("", null));
        
        //名称
        name = new TextInput("name");
        hPanel.add(getPanel(FunctionMgr.getLang("name") + "：", name));
        
        if(funMr.treeNode.getType() == TreeMrType.FUNCTION) {
            //返回值
            fh = new SelectBox();
            funMr.getDataType().forEach(a -> fh.addOption(a, a));
            hPanel.add(getPanel(FunctionMgr.getLang("returned") + "：", fh));
            //拥有者
            owner = new SelectBox();
            new HHdbUser(conn, HHdbPgsqlPrefixEnum.valueOf(prefix)).getAllUser().forEach(a->owner.addOption(a.get("username"),a.get("username")));
            owner.setValue(jdbcBean.getUser());
            hPanel.add(getPanel(FunctionMgr.getLang("owner") + "：", owner));
        }
        //注释
        commentInput = new TextAreaInput("comment", "",4);
        hPanel.add(getPanel(FunctionMgr.getLang("comment")+ "：",commentInput));
        
        //参数
        hPanel.add(getTablePanel());
        
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(hPanel.getComp());
        hTable.load(new ArrayList<>(), 1);
        return lastPanel;
    }
    
    @Override
    public String getSql() throws Exception {
        StringBuffer sqlText = new StringBuffer();
        if (isEdit) {  //修改时获取的函数的创建sql
            sqlText.append(funMr.getCreateSql(conn));
        } else {
            JTable table = hTable.getComp();
            int rows = table.getRowCount();
            int count = 0;   //记录输出参数数量
            
            StringBuffer nameStr = new StringBuffer();
            nameStr.append("\"" + funMr.treeNode.getSchemaName() + "\"").append(".")
                    .append((StringUtils.isNotBlank(name.getValue()) ? name.getValue() : "new")).append("(");
            for (int i = 0; i < rows; i++) {
                if (table.getValueAt(i, 0) != null && table.getValueAt(i, 0) != "" && table.getValueAt(i, 1) != null && table.getValueAt(i, 1) != ""
                        && table.getValueAt(i, 2) != null && table.getValueAt(i, 2) != "") {
                    if (i != 0) nameStr.append(",");
                    nameStr.append(table.getValueAt(i, 1)).append(" ");
                    nameStr.append(table.getValueAt(i, 0)).append(" ");
                    nameStr.append(table.getValueAt(i, 2));
                    if (table.getValueAt(i, 1).equals("OUT") || table.getValueAt(i, 1).equals("INOUT")) count++;
                }
            }
            nameStr.append(")");
            
            sqlText.append("CREATE OR REPLACE ").append(funMr.treeNode.getType().name()).append(nameStr);
            if(funMr.treeNode.getType() == TreeMrType.FUNCTION) {
                sqlText.append(" RETURNS ");
                if (count >= 2) {
                    sqlText.append(" RECORD ");
                } else {
                    sqlText.append(fh.getValue());
                }
            }
            sqlText.append(" AS ").append("$BODY$").append("\n").append("BEGIN\n")
                    .append("\t-- Routine body goes here...\n\n");
            if(funMr.treeNode.getType() == TreeMrType.FUNCTION) sqlText.append("\tRETURN '';\n");
            sqlText.append("END").append("\n").append("$BODY$").append("\n");
            sqlText.append("LANGUAGE "+ "pl" + prefix + "sql" ).append(";");
            
            //所有者
            if(funMr.treeNode.getType() == TreeMrType.FUNCTION) sqlText.append("\n\nALTER "+funMr.treeNode.getType().name()+" "+nameStr+"\n    OWNER TO "+owner.getValue()+";");
            //注释
            sqlText.append("\n\nCOMMENT ON "+funMr.treeNode.getType().name()+" "+nameStr+"\n    IS '"+commentInput.getValue()+"';");
        }
        return sqlText.toString();
    }
    
    @Override
    public void save() throws Exception {
        SqlExeUtil.executeUpdate(conn, queryUi.getText());
    }
    
    @Override
    public void delete() throws Exception {
        SqlExeUtil.executeUpdate(conn, getDelSql());
    }
    
    @Override
    public void examineFun() {
        StringBuffer mesg = new StringBuffer();
        try {
            mesg.append(funMr.checkFunction(conn));
        } catch (Exception e) {
            mesg.append(e.getMessage());
        } finally {
            if (mesg.length() > 0) {
                TextAreaInput mes = new TextAreaInput();
                mes.setLineWrap(true);
                mes.setEnabled(false);
                mes.setValue(mesg.toString());
                LastPanel lasp = new LastPanel(false);
                lasp.set(mes.getComp());
    
                HDialog dialog = new HDialog(StartUtil.parentFrame,600, 300);
                dialog.setWindowTitle(FunctionMgr.getLang("result"));
                dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
                HPanel hPanel = new HPanel();
                hPanel.setLastPanel(lasp);
                dialog.setRootPanel(hPanel);
                dialog.show();
            }
        }
    }

	@Override
	public String getDelSql() throws Exception {
		Map<String, Object> map = funMr.getFunParameter(conn).get(0);
        return "DROP " + (funMr.treeNode.getType() == TreeMrType.FUNCTION ? "FUNCTION " : "PROCEDURE ")
                + funMr.treeNode.getSchemaName() + "." + funMr.treeNode.getName() + "(" + map.get("arguments") + ")";
	}
    
}
