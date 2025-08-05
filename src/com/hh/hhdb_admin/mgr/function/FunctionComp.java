package com.hh.hhdb_admin.mgr.function;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.ora.OraSqlUtil;
import com.hh.frame.dbobj3.ora.OracleCompileTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.function.run.*;
import com.hh.hhdb_admin.mgr.function.ui.from.FunBaseForm;
import com.hh.hhdb_admin.mgr.function.util.FunUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.List;


public class FunctionComp {
    private  Connection conn;
    private  JdbcBean jdbcBean;
    private FunBaseForm funForm;

    private HDialog createDlg,sqlDlg;
    private HButton compileBut,upBut,saveBut,formatBut;

    private String objName;     //对象名称
    private String objId;       //对象id
    private boolean isEdit;				//true 修改
    private String schemaName;		//模式名
    private String oldText = "";        //面板初始内容
    
    private AbsFunMr funMr;
    private Run run;
    private TreeMrType objType=TreeMrType.PROCEDURE;
    private DBTypeEnum dbtype;

    public FunctionComp(JdbcBean jdbcBean, Connection conn, String schemaName,String objName, String objId,boolean isFunNotProc)throws Exception {
//    	this.conn=CsVarUtil.conn;
//        this.jdbcBean = CsVarUtil.jdbc;
    	this.jdbcBean=jdbcBean;
    	this.conn=conn;
        this.dbtype=CsVarUtil.getDbtype();
        this.schemaName = schemaName;
        this.objName = objName;
        this.objId = objId;
        if(isFunNotProc)
        	objType=TreeMrType.FUNCTION;
    }

    /**
     * 显示新建面板
     */
    public void addFun(){
        try {
            this.isEdit = false;
            TreeMrNode treeNode = new TreeMrNode("","", objType, "");
            treeNode.setSchemaName(schemaName);
            funMr = AbsFunMr.genFunMr(dbtype,jdbcBean.getCurrentVersion(),treeNode);
            funForm = FunUtil.getFunBaseForm(funMr,jdbcBean,conn,isEdit);

            //下一步
            HBarLayout l = new HBarLayout();
            l.setAlign(AlignEnum.LEFT);
            HBarPanel toolBarPane = new HBarPanel(l);
            createDlg = new HDialog(CsVarUtil.parentFrame,700){
                @Override
                protected void closeEvent() {
                	dispose();
                }
            };
            HButton sqlBut = new HButton(LangMgr.lang(Word3.NEXT_STEP)) {
                @Override
                public void onClick() {
                    createDlg.hide();
                    getSqlPanel();
                }
            };

            sqlBut.setIcon(FaSolidEnum.RIGHT_LONG,16);
            toolBarPane.add(sqlBut);
            LastPanel lastPanel = funForm.getParaPanel();
            lastPanel.setHead(toolBarPane.getComp());

            
            createDlg.setIconImage(IconFileUtil.getLogo());
            createDlg.setWindowTitle(LangMgr.lang(Word2.ADD));
            createDlg.setRootPanel(lastPanel);
            createDlg.setSize(700,lastPanel.getHeight()+60);
            createDlg.setStatusBar(new HBarPanel());
            createDlg.show();
        }catch (Exception e){
            Lg.error(FunctionComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }

    /**
     * 显示修改面板
     */
    public void updFun(){
        try {
            this.isEdit = true;
            TreeMrNode treeNode = new TreeMrNode(objName,objId, objType, "");
            treeNode.setSchemaName(schemaName);
            AbsFunMr funMr = AbsFunMr.genFunMr(dbtype,jdbcBean.getCurrentVersion(),treeNode);
            funForm = FunUtil.getFunBaseForm(funMr,jdbcBean,conn,isEdit);
            getSqlPanel();
        }catch (Exception e){
            Lg.error(FunctionComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }

    /**
     * 删除
     */
//    public void delete(){
//        try {
//            TreeMrNode treeNode = new TreeMrNode(objName,objId, objType, "");
//            treeNode.setSchemaName(schemaName);
//            funMr = AbsFunMr.genFunMr(dbtype,treeNode);
//            FunBaseForm funForm = FunUtil.getFunBaseForm(funMr,conn,jdbcBean.getUser(),isEdit);
//            funForm.delete();
//            refresh();
//        }catch (Exception e){
//            Lg.error(FunctionComp.class, e);
//            PopDlgUtil.error(CsVarUtil.parentFrame,e);
//        }
//    }

    /**
     * oracle添加调试信息
     */
    public void addDebugInfo(){
        try {
            SqlExeUtil.executeUpdate(conn,"ALTER "+ objType.name() +" "+
                    schemaName +"."+ objName +" COMPILE DEBUG");
            PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
        }catch (Exception e){
            Lg.error(FunctionComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }

    /**
     * 检查函数
     * @param functionName 函数名
     * @param id id
     */
    public void examine(){
        try {
            TreeMrNode treeNode = new TreeMrNode(objName,objId, objType, "");
            treeNode.setSchemaName(schemaName);
            funMr = AbsFunMr.genFunMr(dbtype,jdbcBean.getCurrentVersion(),treeNode);
            FunBaseForm funBaseForm = FunUtil.getFunBaseForm(funMr,jdbcBean,conn,isEdit);
            if (funBaseForm != null) {
                funBaseForm.examineFun();
            }
        }catch (Exception e){
            Lg.error(FunctionComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }
    
    /**
     * 函数运行
     * @param packageName   所属包名称，不存在则为空
     */
    public void runFunction(String packageName) {
        try {
            TreeMrNode treeNode = new TreeMrNode(objName,objId,objType, "");
            treeNode.setSchemaName(schemaName);
            treeNode.setPackageName(packageName);
            funMr = AbsFunMr.genFunMr(dbtype,jdbcBean.getCurrentVersion(),treeNode);
            
            //判断是否有同名函数
            switch (DriverUtil.getDbType(jdbcBean)) {
                case hhdb:
                case pgsql:
                case tdsql4pg:
                case gaussdb:
                    run = new RunHhPg(jdbcBean,conn, schemaName, objName) {
                        @Override
                        public void next(List<HTabRowBean> lisi) {
                            FunUtil.getRunFun(funMr,conn,jdbcBean,run.getParamLists());
                        }
                    };
                    break;
                case oracle:
                    run = new RunOracle(conn, schemaName, packageName, objName) {
                        @Override
                        public void next(List<HTabRowBean> lisi) {
                            FunUtil.getRunFun(funMr,conn,jdbcBean,run.getParamLists());
                        }
                    };
                    break;
                case mysql:
                case mariadb:
                case tdsql4mysql:
                    run = new RunMysql(jdbcBean,conn, schemaName, objName, objType==TreeMrType.FUNCTION);
                    break;
                case sqlserver:
                    run = new RunSqlServer(conn, schemaName, objName, objType==TreeMrType.FUNCTION);
                    break;
                case oceanbase:
                	if(this.jdbcBean.getCurrentVersion()==null)
                		run = new RunMysql(jdbcBean,conn, schemaName, objName, objType==TreeMrType.FUNCTION);
                	if(this.jdbcBean.getCurrentVersion()==DBTypeEnum.Version.ob4ora) {
                		run = new RunOceanBase4ora(conn, schemaName, packageName, objName) {
                            @Override
                            public void next(List<HTabRowBean> lisi) {
                                FunUtil.getRunFun(funMr,conn,jdbcBean,run.getParamLists());
                            }
                        };
                	}
                    break;
                default:
                    PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.sLang(CsL.DB_NOT_SUPPORTED));
            }
            if (null != run) {
                if (run.isOverWrite()) {
                    run.show();
                } else {
                    run.handParams();
                    FunUtil.getRunFun(funMr,conn,jdbcBean,run.getParamLists());
                }
            }
        }catch (Exception e){
            Lg.error(FunctionComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
//            ConnUtil.close(conn);
        }
    }
    
    /**
     * 打开sql编辑面板
     */
    private void getSqlPanel(){
        try {
            sqlDlg = new HDialog(CsVarUtil.parentFrame,1000, 800){
                @Override
                protected void closeEvent() {
                	dispose();
                }
            };
            sqlDlg.setIconImage(IconFileUtil.getLogo());
            sqlDlg.setWindowTitle(LangMgr.lang( isEdit ? Word2.DESIGN : Word2.ADD));
            LastPanel lastPanel = funForm.getSqlPanel();
            lastPanel.setHead(getHBarPanel().getComp());
            HPanel hPanel = new HPanel();
            hPanel.setLastPanel(lastPanel);
            sqlDlg.setRootPanel(hPanel);
            sqlDlg.setStatusBar(new HBarPanel());
            sqlDlg.show();
        }catch (Exception e){
            Lg.error(FunctionComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }

    private HBarPanel getHBarPanel() {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hToolBar = new HBarPanel(l);

        if (isEdit) {
            DBTypeEnum db = DriverUtil.getDbType(jdbcBean);
//            if (db == DBTypeEnum.oracle || db == DBTypeEnum.dm || db == DBTypeEnum.oceanbase4ora) {
            if(DBTypeEnum.isOraSeries(db, jdbcBean.getCurrentVersion())) {
                //编译
                compileBut = new HButton(LangMgr.lang(Word2.COMPILE)) {
                    @Override
                    public void onClick() {
                        PopDlgUtil.info(CsVarUtil.parentFrame,compile().toString());
                    }
                };
                compileBut.setIcon(FaSolidEnum.RECEIPT,16);
                hToolBar.add(compileBut);
            }
        } else {
            //上一步
            upBut = new HButton(LangMgr.lang(Word3.LAST_STEP)) {
                @Override
                public void onClick() {
                    sqlDlg.dispose();
                    createDlg.show();
                }
            };
            upBut.setIcon(FaSolidEnum.LEFT_LONG,16);
            hToolBar.add(upBut);
        }
        //保存
        saveBut = new HButton(LangMgr.lang(Word2.SAVE)) {
            @Override
            public void onClick() {
                try {
                    funForm.save();
                    setEnabled(false);
                    refresh();

                    if (isEdit) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(LangMgr.lang(Word4.SAVED_SUCCESSFULLY)).append("!\n");
                        oldText = funForm.queryUi.getText();
                        if (null != compileBut) {
                            sb.append(compile());
                            compileBut.setEnabled(true);
                        }
                        PopDlgUtil.info(CsVarUtil.parentFrame,sb.toString());
                    } else {
                        PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                        sqlDlg.dispose();
                    }
                } catch (Exception e) {
                    Lg.error(FunctionComp.class, e);
                    PopDlgUtil.error(CsVarUtil.parentFrame,e);
                }
            }
        };
        saveBut.setIcon(FaSolidEnum.FLOPPY_DISK,16);
        //格式化
        formatBut = new HButton(LangMgr.lang(Word3.FORMATTING)) {
            @Override
            public void onClick() {
            	GlobalWorkUtil.formatSql(funForm.queryUi);
            }
        };
        formatBut.setIcon(FaSolidEnum.WAND_MAGIC_SPARKLES,16);
        hToolBar.add(saveBut,formatBut);

        if (isEdit) {
            saveBut.setEnabled(false);
            oldText = funForm.queryUi.getText();
            funForm.queryUi.getRSyntaxTextArea().addCaretListener(e -> {
                saveBut.setEnabled(!oldText.equals(funForm.queryUi.getText()));
                if (null != compileBut) compileBut.setEnabled(oldText.equals(funForm.queryUi.getText()));
            });
        }
        return hToolBar;
    }

    /**
     * 编译
     */
    private StringBuffer compile(){
        StringBuffer sb = new StringBuffer();
        try {
            TreeMrType type = funForm.funMr.treeNode.getType();
            OracleCompileTool tool = new OracleCompileTool(CsVarUtil.jdbc.getCurrentVersion(),conn, schemaName, type.name().equals(TreeMrType.FUNCTION.name()) ? OraSqlUtil.CompileType.function : OraSqlUtil.CompileType.procedure,funForm.funMr.treeNode.getName());
            tool.compile();
            String errorMsg = null;
            if (DriverUtil.getDbType(jdbcBean) != DBTypeEnum.dm) {
                errorMsg = tool.getErrorMsg(false);
            }
            sb.append(StringUtils.isEmpty(errorMsg) ? LangMgr.lang(Word4.COMPILE_SUCCESS)+"\n" : LangMgr.lang(Word2.RESULT)+":\n"+errorMsg+"\n");
        }catch (Exception e){
            sb.append(e.getMessage());
            return sb;
        }
        return sb;
    }

    /**
     * 刷新
     */
    protected void refresh() {
    }
}
