package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.attributeMr.AttrHeardType;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.dbobj3.kingbase.KingbaseInstanceObj;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KingbaseFunForm extends FunBaseForm {
    private SelectBox fh,owner;
    private TextInput name;
    private TextAreaInput commentInput;
    
    private String ownerSql,commentSql;
    private DBTypeEnum database_mode;   //当前金仓数据库所属模式
    
    public KingbaseFunForm(AbsFunMr funMr, JdbcBean jdbcBean,Connection conn,String user,  boolean isEdit) throws Exception {
        super(funMr, jdbcBean,conn, user);
        this.isEdit = isEdit;
        database_mode = SqlQueryUtil.selectStrList(conn,"SHOW DATABASE_mode").get(1).get(0).equals("pg") ? DBTypeEnum.pgsql : DBTypeEnum.oracle;
    }
    
    @Override
    public LastPanel getParaPanel() throws Exception {
        HPanel hPanel = new HPanel();
        hPanel.add(getPanel("", null));
        
        //名称
        name = new TextInput("name");
        hPanel.add(getPanel(LangMgr.lang(Word2.NAME) + "：", name));
        
        if(funMr.treeNode.getType() == TreeMrType.FUNCTION) {
            //返回值
            fh = new SelectBox();
            funMr.getDataType().forEach(a -> fh.addOption(a, a));
            hPanel.add(getPanel(LangMgr.lang(Word2.RETURN) + "：", fh));
            //拥有者
            owner = new SelectBox();
            DBTypeEnum.Version ver = CsVarUtil.jdbc.getCurrentVersion();
            for (Map<String, String> user : new KingbaseInstanceObj(ver, conn).getAllObjsProp(AllDbObjType.USER)) {
                String name = user.get(AttrHeardType.NAME.name());
                owner.addOption(name, name);
            }
            owner.setValue(user);
            hPanel.add(getPanel(LangMgr.lang(Word3.OWNER) + "：", owner));
        }
        //注释
        commentInput = new TextAreaInput("comment", "",4);
        hPanel.add(getPanel(LangMgr.lang(Word2.TABLE_COMMENT)+ "：",commentInput));
        
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
            sqlText.append("LANGUAGE ").append(database_mode == DBTypeEnum.pgsql ? "plpgsql":"plsql").append(";");
            
            //所有者
            if(funMr.treeNode.getType() == TreeMrType.FUNCTION)  ownerSql = "\n\nALTER "+funMr.treeNode.getType().name()+" "+nameStr+"\n    OWNER TO "+owner.getValue()+";";
            //注释
            commentSql = "\n\nCOMMENT ON "+funMr.treeNode.getType().name()+" "+nameStr+"\n    IS '"+commentInput.getValue()+"';";
        }
        return sqlText.toString();
    }
    
    @Override
    public void save() throws Exception {
        if (!StringUtils.isNotBlank(queryUi.getText())) return;
        List<String> sqlList = new LinkedList<>();
        sqlList.add(queryUi.getText());
        if(funMr.treeNode.getType() == TreeMrType.FUNCTION && StringUtils.isNotBlank(ownerSql)) sqlList.add(ownerSql);
        if (StringUtils.isNotBlank(commentSql)) sqlList.add(commentSql);
        SqlExeUtil.batchExecute(conn, sqlList);
    }
    
    @Override
    public void delete() throws Exception {
        SqlExeUtil.executeUpdate(conn, funMr.getDelSql(conn));
    }
    
    @Override
    public void examineFun() {
    }

}
