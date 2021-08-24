package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.ArrayList;

public class OrFunForm extends FunBaseForm {
    private SelectBox fh;
    private SelectBox yy;
    private TextInput name;

    public OrFunForm(AbsFunMr funMr, Connection conn, JdbcBean jdbcBean, boolean isEdit)throws Exception {
        super(funMr,conn,jdbcBean);
        this.isEdit = isEdit;
    }

    @Override
    public LastPanel getParaPanel() throws Exception {
        HPanel hPanel = new HPanel();
        hPanel.add(getPanel("",null,null));

        //名称
        name = new TextInput("name");
        hPanel.add(getPanel(FunctionMgr.getLang("name")+"：",name,new HDivLayout(10, 15, GridSplitEnum.C2,GridSplitEnum.C5)));
        //返回值
        fh = new SelectBox();
        for (String s : funMr.getDataType()) {
            fh.addOption(s,s);
        }
        hPanel.add(getPanel(FunctionMgr.getLang("returned")+"：",fh,new HDivLayout(10, 15, GridSplitEnum.C2,GridSplitEnum.C5)));

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
            sqlText.append("CREATE OR REPLACE ").append(yy.getValue()).append(" "+(StringUtils.isNotBlank(name.getValue()) ? name.getValue() : "new"));
            if (rows>0) sqlText.append("(");
            for (int i = 0; i < rows; i++) {
                if(table.getValueAt(i, 0) != null && table.getValueAt(i, 0) != "" && table.getValueAt(i, 1) != null && table.getValueAt(i, 1) !="" && table.getValueAt(i, 2) != null
                        && table.getValueAt(i, 2) != "") {
                    if (i != 0) sqlText.append(",");
                    sqlText.append(table.getValueAt(i, 0)).append(" ");
                    sqlText.append(table.getValueAt(i, 1)).append(" ");
                    sqlText.append(table.getValueAt(i, 2));
                }
            }
            if (rows>0) sqlText.append(")");
            //函数才有返回值
            if(yy.getValue().equals("FUNCTION")){
                sqlText.append(" RETURN ").append(fh.getValue());
            }
            sqlText.append(" AS ").append("\n").append("BEGIN").append("\n\n").append("\n").append("END").append(";");
        }
        return sqlText.toString();
    }

    @Override
    public void save() throws Exception {
        SqlExeUtil.executeUpdate(conn, queryUi.getText());
    }

    @Override
    public void delete() throws Exception {
        SqlExeUtil.executeUpdate(conn, "DROP "+ (funMr.treeNode.getType() == TreeMrType.FUNCTION ? "FUNCTION " : "PROCEDURE ") +
                funMr.treeNode.getSchemaName()+"."+ DbCmdStrUtil.toDbCmdStr(funMr.treeNode.getName(), DriverUtil.getDbType(conn)));
    }
    
    @Override
    public void examineFun() {
    }
}
