package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;

public class Db2FunForm extends FunBaseForm {
    private SelectBox fh;
    private TextInput name;

    public Db2FunForm(AbsFunMr funMr, JdbcBean jdbcBean,Connection conn, boolean isEdit)throws Exception {
        super(funMr,jdbcBean,conn,null);
        this.isEdit = isEdit;
    }

    @Override
    public LastPanel getParaPanel() throws Exception {
        HPanel hPanel = new HPanel();
        hPanel.add(getPanel("",null));

        //名称
        name = new TextInput("name");
        hPanel.add(getPanel(LangMgr.lang(Word2.NAME)+"：",name));

        //返回值
        if(funMr.treeNode.getType() == TreeMrType.FUNCTION){
            fh = new SelectBox();
            for (String s : funMr.getDataType()) {
                fh.addOption(s,s);
            }
            hPanel.add(getPanel(LangMgr.lang(Word2.RETURN)+"：",fh));
        }

        //参数
        hPanel.add(getTablePanel());

        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(hPanel.getComp());

        hTable.load(new ArrayList<>(),1);
        return lastPanel;
    }

    @Override
    public String getSql() throws Exception {
        StringBuffer sqlText = new StringBuffer();
        if (isEdit) {  //修改时获取的函数的创建sql
            sqlText.append(funMr.getCreateSql(conn));
        }else {
            JTable table = hTable.getComp();
            int rows = table.getRowCount();
            boolean bool = funMr.treeNode.getType() == TreeMrType.FUNCTION;
            sqlText.append("CREATE OR REPLACE ").append(bool ? "FUNCTION" : "PROCEDURE").append(" \""+funMr.treeNode.getSchemaName()).append("\".\"")
                .append((StringUtils.isNotBlank(name.getValue()) ? name.getValue() : "new")+"\"");
            sqlText.append("(");
            for (int i = 0; i < rows; i++) {
                if(table.getValueAt(i, 0) != null && table.getValueAt(i, 0) != "" && table.getValueAt(i, 1) != null && table.getValueAt(i, 1) !="") {
                    if (i != 0) sqlText.append(",");
                    if (bool) {
                        sqlText.append(table.getValueAt(i, 0));
                        sqlText.append(" "+getTypeLen(table.getValueAt(i, 1)+""));
                    }else {
                        sqlText.append(table.getValueAt(i, 1));
                        sqlText.append(" "+getTypeLen(table.getValueAt(i, 0)+""));
                        sqlText.append(" "+table.getValueAt(i, 2));
                    }
                }
            }
            sqlText.append(")");
            if(bool) {
                sqlText.append("\nRETURNS ");
                if (fh.getValue().equals("TABLE")) {
                    sqlText.append(fh.getValue()+"( )\n\t").append("LANGUAGE SQL\n").append("RETURN\n\t\t----SQL\n");
                }else{
                    sqlText.append(getTypeLen(fh.getValue())+"\n\t").append("LANGUAGE SQL\n");
                    sqlText.append("BEGIN ATOMIC").append("\n").append("\t\t----SQL\n").append("END").append(";");
                }
            }else {
                sqlText.append("\nLANGUAGE SQL\n").append("BEGIN\n").append("\t\t----SQL\n").append("END").append(";");
            }
        }
        return sqlText.toString();
    }

    @Override
    public void save() throws Exception {
        SqlExeUtil.executeUpdate(conn, queryUi.getText());
    }

    @Override
    public void delete() throws Exception {        
        SqlExeUtil.executeUpdate(conn, funMr.getDelSql(conn));
    }
    
    @Override
    public void examineFun() {
    }
    
    private String getTypeLen(String type){
        if (type.equals("VARCHAR") || type.equals("VARBINARY") || type.equals("VARGRAPHIC")) {
            return type+"(200)";
        }else {
            return type;
        }
    }

}
