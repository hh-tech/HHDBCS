package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;

public class HHFunForm extends FunBaseForm {
    private SelectBox fh;
    private SelectBox yy;
    private TextInput name;
    
    private String prefix;
    
    public HHFunForm(AbsFunMr funMr, Connection conn, JdbcBean jdbcBean, boolean isEdit) throws Exception {
        super(funMr, conn, jdbcBean);
        this.isEdit = isEdit;
        prefix = dbType.equals(DBTypeEnum.hhdb) ? "hh" : "pg";
    }
    
    @Override
    public LastPanel getParaPanel() throws Exception {
        HPanel hPanel = new HPanel();
        hPanel.add(getPanel("", null, null));
        
        //名称
        name = new TextInput("name");
        hPanel.add(getPanel(FunctionMgr.getLang("name") + "：", name, new HDivLayout(10, 15, GridSplitEnum.C2, GridSplitEnum.C5)));
        //返回值
        fh = new SelectBox();
        for (String s : funMr.getDataType()) {
            fh.addOption(s, s);
        }
        hPanel.add(getPanel(FunctionMgr.getLang("returned") + "：", fh, new HDivLayout(10, 15, GridSplitEnum.C2, GridSplitEnum.C5)));
        
        //语言或者类型
        yy = new SelectBox(){
            @Override
            public void onItemChange(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) fh.setEnabled(getValue().equals("FUNCTION"));
            }
        };
        yy.addOption(FunctionMgr.getLang("process"),"PROCEDURE");
        yy.addOption(FunctionMgr.getLang("function"),"FUNCTION");
        hPanel.add(getPanel(FunctionMgr.getLang("dbType"),yy,new HDivLayout(10, 15, GridSplitEnum.C2,GridSplitEnum.C5)));
        
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
            sqlText.append("CREATE OR REPLACE ").append(yy.getValue()).append("\"" + funMr.treeNode.getSchemaName() + "\"").append(".")
                    .append((StringUtils.isNotBlank(name.getValue()) ? name.getValue() : "new")).append("(");
            for (int i = 0; i < rows; i++) {
                if (table.getValueAt(i, 0) != null && table.getValueAt(i, 0) != "" && table.getValueAt(i, 1) != null && table.getValueAt(i, 1) != ""
                        && table.getValueAt(i, 2) != null && table.getValueAt(i, 2) != "") {
                    if (i != 0) sqlText.append(",");
                    sqlText.append(table.getValueAt(i, 1)).append(" ");
                    sqlText.append(table.getValueAt(i, 0)).append(" ");
                    sqlText.append(table.getValueAt(i, 2));
                    if (table.getValueAt(i, 1).equals("OUT") || table.getValueAt(i, 1).equals("INOUT")) count++;
                }
            }
            sqlText.append(")");
            
            if(yy.getValue().equals("FUNCTION")){
                sqlText.append(" RETURNS ");
                if (count >= 2) {
                    sqlText.append(" RECORD ");
                } else {
                    sqlText.append(fh.getValue());
                }
            }
            sqlText.append(" AS ").append("$BODY$").append("\n").append("BEGIN\n")
                    .append("\t-- Routine body goes here...\n\n");
            if(yy.getValue().equals("FUNCTION")) sqlText.append("\tRETURN '';\n");
            sqlText.append("END").append("\n").append("$BODY$").append("\n");
            sqlText.append("LANGUAGE "+ "pl" + prefix + "sql" ).append(";");
        }
        return sqlText.toString();
    }
    
    @Override
    public void save() throws Exception {
        SqlExeUtil.executeUpdate(conn, queryUi.getText());
    }
    
    @Override
    public void delete() throws Exception {
        Map<String, Object> map = funMr.getFunParameter(conn).get(0);
        SqlExeUtil.executeUpdate(conn, "DROP " + (funMr.treeNode.getType() == TreeMrType.FUNCTION ? "FUNCTION " : "PROCEDURE ")
                + funMr.treeNode.getSchemaName() + "." + funMr.treeNode.getName() + "(" + map.get("arguments").toString() + ")");
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
    
}
