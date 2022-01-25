package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.util.VerifyUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;

public class MysqlFunForm extends FunBaseForm {
    private SelectBox fh;
    private TextInput name,lengthField,scaleField;
    private TextAreaInput commentInput;
    private String originalSql; //修改函数时，函数本身的sql

    public MysqlFunForm(AbsFunMr funMr, Connection conn, JdbcBean jdbcBean, boolean isEdit)throws Exception {
        super(funMr,conn,jdbcBean);
        this.isEdit = isEdit;
    }

    @Override
    public LastPanel getParaPanel() throws Exception {
        HPanel hPanel = new HPanel();
        hPanel.add(getPanel("",null));

        //名称
        name = new TextInput("name");
        hPanel.add(getPanel(FunctionMgr.getLang("name")+"：",name));

        //返回值
        if(funMr.treeNode.getType() == TreeMrType.FUNCTION){
            lengthField =  new TextInput("length");
            lengthField.setInputVerifier(VerifyUtil.getTextIntVerifier("请填写正确的数字", 1, 2147483647));
            scaleField =  new TextInput("cale");
            scaleField.setInputVerifier(VerifyUtil.getTextIntVerifier("请填写正确的数字", 1, 2147483647));
            fh = new SelectBox(){
                @Override
                public void onItemChange(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        scaleField.setEnabled(FunBaseForm.createTabTool.getTableObjFun().getTwoLengthType().contains(getValue().toUpperCase()));
                    }
                }
            };
            for (String s : FunBaseForm.createTabTool.getTableObjFun().getAllTypeStr()) {
                fh.addOption(s,s);
            }
            fh.getComp().setSelectedItem("varchar");
            hPanel.add(getPanel(FunctionMgr.getLang("returned")+"：",fh));
    
            hPanel.add(getPanel("长度：",lengthField));
            hPanel.add(getPanel("精度：",scaleField));
        }
        //注释
        commentInput = new TextAreaInput("comment", "",4);
        hPanel.add(getPanel(FunctionMgr.getLang("comment")+ "：",commentInput));
        //参数
        hPanel.add(getTablePanel());

        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(hPanel.getComp());
        return lastPanel;
    }

    @Override
    public String getSql() throws Exception {
        StringBuffer sqlText = new StringBuffer();
        if (isEdit) {  //修改时获取的函数创建sql
            sqlText.append(funMr.getCreateSql(conn));
            originalSql = sqlText.toString();
        }else {
            JTable table = hTable.getComp();
            boolean bool = funMr.treeNode.getType() == TreeMrType.FUNCTION;
            sqlText.append("CREATE "+ (bool ? "FUNCTION" : "PROCEDURE") ).append(" `"+(StringUtils.isNotBlank(name.getValue()) ? name.getValue() : "new")).append("`(");
            for (int i = 0; i < table.getRowCount(); i++) {
                if(table.getValueAt(i, 0) != null && table.getValueAt(i, 0) != "" && table.getValueAt(i, 1) != null && table.getValueAt(i, 1) != "") {
                    if (i != 0) sqlText.append(",");
                    //根据情况拼接精度
                    JsonObject typeJson = Json.parse(bool ? table.getValueAt(i, 1).toString() : table.getValueAt(i, 2).toString() ).asObject();
                    String type = getType(typeJson.getString(TypeColumn.__TEXT),typeJson.getString(TypeColumn.JSON_LENGTH),typeJson.getString(TypeColumn.JSON_SCALE));
    
                    if (!bool) sqlText.append(table.getValueAt(i, 1)).append(" ");
                    sqlText.append("`"+table.getValueAt(i, 0)+"`").append(" ");
                    sqlText.append(type);
                }
            }
            sqlText.append(")");
            if (bool) {
                String type = getType(fh.getValue(),lengthField.getValue(),scaleField.getComp().isEnabled() ? scaleField.getValue() : null);
                sqlText.append(" RETURNS ").append(type);
            }
            //注释
            sqlText.append("\nCOMMENT '"+commentInput.getValue()+"'");
            sqlText.append("\n").append("BEGIN\n")
                    .append("\t#Routine body goes here...\n")
                    .append("\n");
            if (bool) sqlText.append("\tRETURN '';\n");
            sqlText.append("END").append(";");
        }
        return sqlText.toString();
    }

    @Override
    public void save() throws Exception {
        if (isEdit) {
            SqlExeUtil.executeUpdate(conn,"DROP "+ (funMr.treeNode.getType() == TreeMrType.FUNCTION ? "FUNCTION" : "PROCEDURE") +
                    " IF EXISTS `"+funMr.treeNode.getSchemaName()+"`.`"+funMr.treeNode.getName()+"`");
            try {
                SqlExeUtil.executeUpdate(conn, queryUi.getText());
            }catch (Exception e){
                //如果报错，则抛出异常还原之前删除的函数
                SqlExeUtil.executeUpdate(conn, originalSql);
                throw e;
            }
        } else {
            SqlExeUtil.executeUpdate(conn, queryUi.getText());
        }
    }

    @Override
    public void delete() throws Exception {
        SqlExeUtil.executeUpdate(conn, getDelSql());
    }
    
    @Override
    public void examineFun() {
    }
    
    private String getType(String type,String length,String scale){
        Integer len = null;
        Integer sca = null;
        if (StringUtils.isNoneBlank(length) && !FunBaseForm.createTabTool.getTableObjFun().getNoLengthType().contains(type.toUpperCase())) {
            len = Integer.valueOf(length);
        }
        if (StringUtils.isNoneBlank(scale) && FunBaseForm.createTabTool.getTableObjFun().getTwoLengthType().contains(type.toUpperCase())) {
            sca = Integer.valueOf(scale);
        }
        
        return type + (null != len ? "(" + len + (null != sca ? "," + sca : "") + ")" : "");
    }

	@Override
	public String getDelSql() throws Exception {
		return "DROP "+ (funMr.treeNode.getType() == TreeMrType.FUNCTION ? "FUNCTION " : "PROCEDURE ") +
                " IF EXISTS `"+funMr.treeNode.getSchemaName()+"`.`"+funMr.treeNode.getName()+"`";
	}
    
}
