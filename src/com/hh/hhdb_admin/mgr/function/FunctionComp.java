package com.hh.hhdb_admin.mgr.function;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.dbobj2.ora.pack.OracleCompileTool;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.ui.from.FunBaseForm;
import com.hh.hhdb_admin.mgr.function.util.FunUtil;
import com.hh.hhdb_admin.mgr.query.util.QuerUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.Connection;

public class FunctionComp {
    private final Connection conn;
    private final JdbcBean jdbcBean;
    private FunBaseForm funForm;

    private HDialog dlog,dialog;
    HButton compileBut,upBut,saveBut,formatBut;

    private boolean isEdit;				//true 修改
    private final String schemaName;		//模式名
    private String oldText = "";        //面板初始内容

    public FunctionComp(JdbcBean jdbcBean,String schemaName)throws Exception {
        this.conn = ConnUtil.getConn(jdbcBean);
        this.jdbcBean = jdbcBean;
        this.schemaName = schemaName;
    }

    /**
     * 显示新建面板
     */
    public void show(String type){
        try {
            this.isEdit = false;
            TreeMrNode treeNode = new TreeMrNode("","", TreeMrType.valueOf(type), "");
            treeNode.setSchemaName(schemaName);
            AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(conn),treeNode);
            funForm = FunUtil.getFunBaseForm(funMr,conn,jdbcBean,isEdit);

            //下一步
            HBarLayout l = new HBarLayout();
            l.setAlign(AlignEnum.LEFT);
            HBarPanel toolBarPane = new HBarPanel(l);
            HButton sqlBut = new HButton(FunctionMgr.getLang("next")) {
                @Override
                public void onClick() {
                    dlog.hide();
                    getSqlPanel();
                }
            };

            sqlBut.setIcon(FunctionMgr.getIcon("next"));
            toolBarPane.add(sqlBut);
            LastPanel lastPanel = funForm.getParaPanel();
            lastPanel.setHead(toolBarPane.getComp());

            dlog = new HDialog(StartUtil.parentFrame,700){
                @Override
                protected void closeEvent() {
                    ConnUtil.close(conn);
                }
            };
            dlog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
            dlog.setWindowTitle(FunctionMgr.getLang("add"));
            dlog.setRootPanel(lastPanel);
            dlog.setSize(700,lastPanel.getHeight()+60);
            dlog.show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("error")+":"+e.getMessage());
        }
    }

    /**
     * 显示修改面板
     */
    public void show(String functionName, String id,String type){
        try {
            this.isEdit = true;

            TreeMrNode treeNode = new TreeMrNode(functionName,id, TreeMrType.valueOf(type), "");
            treeNode.setSchemaName(schemaName);
            AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(conn),treeNode);
            funForm = FunUtil.getFunBaseForm(funMr,conn,jdbcBean,isEdit);
            getSqlPanel();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("error")+":"+e.getMessage());
        }
    }

    /**
     * 删除
     */
    public void delete(String functionName, String id,String type){
        try {
            TreeMrNode treeNode = new TreeMrNode(functionName,id, TreeMrType.valueOf(type), "");
            treeNode.setSchemaName(schemaName);
            AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(conn),treeNode);
            FunBaseForm funForm = FunUtil.getFunBaseForm(funMr,conn,jdbcBean,isEdit);
            funForm.delete();
            refresh();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("error")+":"+e.getMessage());
        }finally {
            ConnUtil.close(conn);
        }
    }

    /**
     * oracle添加调试信息
     */
    public void addDebugInfo(String functionName,String type){
        try {
            SqlExeUtil.executeUpdate(conn,"ALTER "+ (TreeMrType.valueOf(type) == TreeMrType.FUNCTION ? "FUNCTION " : "PROCEDURE ") +
                    schemaName +"."+ functionName +" COMPILE DEBUG");
            PopPaneUtil.info(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("inerrancy2"));
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("error")+":"+e.getMessage());
        }finally {
            ConnUtil.close(conn);
        }
    }

    /**
     * 检查函数
     * @param functionName 函数名
     * @param id id
     * @param type type
     */
    public void examine(String functionName, String id,String type){
        try {
            TreeMrNode treeNode = new TreeMrNode(functionName,id, TreeMrType.valueOf(type), "");
            treeNode.setSchemaName(schemaName);
            AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(conn),treeNode);
            FunBaseForm funBaseForm = FunUtil.getFunBaseForm(funMr,conn,jdbcBean,isEdit);
            if (funBaseForm != null) {
                funBaseForm.examineFun();
            }
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("error")+":"+e.getMessage());
        }finally {
            ConnUtil.close(conn);
        }
    }

    /**
     * 打开sql编辑面板
     */
    private void getSqlPanel(){
        try {
            dialog = new HDialog(StartUtil.parentFrame,1000, 800){
                @Override
                protected void closeEvent() {
                    ConnUtil.close(conn);
                    if (null != dlog) {
                        dlog.dispose();
                    }
                }
            };
            dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
            dialog.setWindowTitle(FunctionMgr.getLang( isEdit ? "design" : "add"));
            LastPanel lastPanel = funForm.getSqlPanel();
            lastPanel.setHead(getHBarPanel().getComp());
            HPanel hPanel = new HPanel();
            hPanel.setLastPanel(lastPanel);
            dialog.setRootPanel(hPanel);
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),FunctionMgr.getLang("error")+":"+e.getMessage());
        }
    }

    private HBarPanel getHBarPanel() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hToolBar = new HBarPanel(l);

        if (isEdit) {
            DBTypeEnum db = DriverUtil.getDbType(jdbcBean);
            if (db == DBTypeEnum.oracle || db == DBTypeEnum.dm) {
                //编译
                compileBut = new HButton(FunctionMgr.getLang("compile")) {
                    @Override
                    public void onClick() {
                        funForm.messageText.setValue(compile().toString());
                    }
                };
                compileBut.setIcon(FunctionMgr.getIcon("formatsql"));
                hToolBar.add(compileBut);
            }
        } else {
            //上一步
            upBut = new HButton(FunctionMgr.getLang("up")) {
                @Override
                public void onClick() {
                    dialog.dispose();
                    dlog.show();
                }
            };
            upBut.setIcon(FunctionMgr.getIcon("pre"));
            hToolBar.add(upBut);
        }
        //保存
        saveBut = new HButton(FunctionMgr.getLang("save")) {
            @Override
            public void onClick() {
                StringBuilder sb = new StringBuilder();
                try {
                    funForm.save();
                    setEnabled(false);
                    refresh();

                    if (isEdit) {
                        sb.append(FunctionMgr.getLang("savesuccess")).append("!\n");
                        oldText = funForm.queryUi.getText();
                        if (null != compileBut) {
                            sb.append(compile());
                            compileBut.setEnabled(true);
                        }
                    } else {
                        JOptionPane.showMessageDialog(StartUtil.parentFrame.getWindow(), FunctionMgr.getLang("savesuccess"),FunctionMgr.getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isEdit) sb.append(e.getMessage()).append("\n");
                    if (!isEdit) PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
                }finally {
                    if (isEdit) funForm.messageText.setValue(sb.toString());
                }
            }
        };
        saveBut.setIcon(FunctionMgr.getIcon("save"));
        //格式化
        formatBut = new HButton(FunctionMgr.getLang("format")) {
            @Override
            public void onClick() {
                QuerUtil.formatSql(DriverUtil.getDbType(jdbcBean),funForm.queryUi);
            }
        };
        formatBut.setIcon(FunctionMgr.getIcon("format"));
        hToolBar.add(saveBut,formatBut);

        if (isEdit) {
            saveBut.setEnabled(false);
            oldText = funForm.queryUi.getText();
            funForm.queryUi.getTextArea().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {}
                @Override
                public void removeUpdate(DocumentEvent e) {}
                @Override
                public void changedUpdate(DocumentEvent e) {
                    saveBut.setEnabled(!oldText.equals(funForm.queryUi.getText()));
                    if (null != compileBut) compileBut.setEnabled(oldText.equals(funForm.queryUi.getText()));
                }
            });
        }
        return hToolBar;
    }

    /**
     * 编译
     */
    private StringBuffer compile(){
        StringBuffer sb = new StringBuffer();
        TreeMrType type = funForm.funMr.treeNode.getType();
        OracleCompileTool tool = new OracleCompileTool(conn, schemaName, type.name().equals(TreeMrType.FUNCTION.name()) ? OraSessionEnum.function : OraSessionEnum.procedure,funForm.funMr.treeNode.getName());
        tool.compile();
        String errorMsg = tool.getErrorMsg(false);
        sb.append(StringUtils.isEmpty(errorMsg) ? FunctionMgr.getLang("compileSuccess")+"\n" : FunctionMgr.getLang("compileResult")+"\n"+errorMsg+"\n");
        return sb;
    }

    /**
     * 刷新
     */
    protected void refresh() {
    }
}
