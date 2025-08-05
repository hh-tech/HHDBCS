package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.common.base.DBTypeEnum;
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

public class GBaseFunForm extends FunBaseForm {
    protected SelectBox fh;
    protected TextInput name;
    private String originalSql; //修改函数时，函数本身的sql

    public GBaseFunForm(AbsFunMr funMr, JdbcBean jdbcBean,Connection conn, boolean isEdit)throws Exception {
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
        
        if(funMr.treeNode.getType() == TreeMrType.FUNCTION) {
            //返回值
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
            originalSql = sqlText.toString();
        }else {
            JTable table = hTable.getComp();
            int rows = table.getRowCount();
            
            String funName = "";
            if (dbType == DBTypeEnum.dm) {
                funName = "\""+funMr.treeNode.getSchemaName()+"\".";
            }
            funName += StringUtils.isNotBlank(name.getValue()) ? name.getValue() : "new";
            sqlText.append("CREATE ").append(funMr.treeNode.getType().name()).append(" " + funName);
            
            sqlText.append("(");
            for (int i = 0; i < rows; i++) {
                if(table.getValueAt(i, 0) != null && table.getValueAt(i, 0) != "" && table.getValueAt(i, 1) != null && table.getValueAt(i, 1) !="" && table.getValueAt(i, 2) != null
                        && table.getValueAt(i, 2) != "") {
                    if (i != 0) sqlText.append(",");
                    sqlText.append(table.getValueAt(i, 1)).append(" ");
                    sqlText.append(table.getValueAt(i, 0)).append(" ");
                    sqlText.append(table.getValueAt(i, 2));
                }
            }
            sqlText.append(")");
            if(funMr.treeNode.getType() == TreeMrType.FUNCTION)  sqlText.append(" RETURNS ").append(fh.getValue());
            sqlText.append(funMr.treeNode.getType() == TreeMrType.FUNCTION ? " AS " : "").append("\n").append("BEGIN\n");
            sqlText.append("\t--enter procedure body here\n\n");
            if(funMr.treeNode.getType() == TreeMrType.PROCEDURE) sqlText.append("END\n");
            sqlText.append("END ").append(funMr.treeNode.getType().name()+";");
        }
        return sqlText.toString();
    }

    @Override
    public void save() throws Exception {
        if (isEdit) {
            delete();
            try {
                SqlExeUtil.executeUpdate(conn, queryUi.getText());
            }catch (Exception e){
                SqlExeUtil.executeUpdate(conn, originalSql);
                throw e;
            }
        } else {
            SqlExeUtil.executeUpdate(conn, queryUi.getText());
        }
    }

    @Override
    public void delete() throws Exception {
        SqlExeUtil.executeUpdate(conn, funMr.getDelSql(conn));
    }
    
    @Override
    public void examineFun() {
    }

}
