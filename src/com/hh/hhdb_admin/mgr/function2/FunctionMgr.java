package com.hh.hhdb_admin.mgr.function2;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.db_sql.DebugSqlUtil;
import com.hh.frame.db_sql.ProcFunSqlUtil;
import com.hh.frame.db_sql.conn.ProFunConnUtil;
import com.hh.frame.db_sql.info.SqlFunBean;
import com.hh.frame.db_sql.info.SqlParamBean;
import com.hh.frame.db_sql.info.SqlProcBean;
import com.hh.frame.db_sql.ora.OraSqlUtil.CompileType;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CompileTool;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.function2.debug.base.DebugBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

public class FunctionMgr extends AbsGuiMgr {
	
	private SqlProcBean procBean;
    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.FUNCTION;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        FunctionBean csGuiBean = new FunctionBean(bj.getJobj());
        FunctionBean.OprType opEnum = csGuiBean.getOprType();
        DBTypeEnum dbtype = CsVarUtil.getDbtype();
        Version v = CsVarUtil.jdbc.getCurrentVersion();
        String schema = csGuiBean.getSchema();
        String name = csGuiBean.getName();
        TreeMrType type =csGuiBean.getNodeType();
        

       
        
        switch (opEnum) {
            case ADD:
            	String title = String.format("%s%s", LangMgr.lang(Word2.CREATE),getName(type));
                CreatePanel panel = new CreatePanel(type == TreeMrType.FUNCTION,csGuiBean.getSchema()) {
                	@Override
                	protected void inform() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                	}
                };
                showDialog(title,panel,800,panel.getHeight()+40);
                
                break;
            case UPDATE:
            	title = String.format("%s%s", LangMgr.lang(Word2.DESIGN),getName(type));
                PkgEditPanel editPanel = new PkgEditPanel(name) {
                	@Override
                	protected void inform() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                	}
                };
                CompileType ct = type == TreeMrType.FUNCTION?CompileType.function:CompileType.procedure;
                CompileTool tool = new CompileTool(dbtype, v, CsVarUtil.conn, schema,name, ct);
                editPanel.setTool(tool);
                String sql = ProFunConnUtil.getProcFunSql(dbtype, v, CsVarUtil.conn, schema,name,csGuiBean.getId(),type == TreeMrType.FUNCTION).trim();
                if(StringUtils.isBlank(sql)) {
                	throw new Exception("暂不支持");
                }
                
                editPanel.init(sql, false);
                showDialog(title,editPanel,800,680);
                break;
            case RUN:
            	title = String.format("%s%s", LangMgr.lang(Word2.RUN),getName(type));
            	RunPanel2 runPanel = new RunPanel2();
            	runPanel.init(csGuiBean.getSchema(), csGuiBean.getName(), csGuiBean.getId(),type == TreeMrType.FUNCTION);
            	
            	HDialog dialog = new HDialog(CsVarUtil.parentFrame,1000,680,false) {
            		protected void closeEvent() {
            			try {
							runPanel.close();
						} catch (IOException e) {
							Lg.error(getClass(), e);
							PopDlgUtil.error(CsVarUtil.parentFrame,e);
						}
            		};
            	};
            	dialog.setStatusBar(null);
            	dialog.setRootPanel(runPanel);
            	dialog.setWindowTitle(title);
            	dialog.show();
                break;
            case EXAMINE:
            	PkgUtil.checkFunction(dbtype, v, CsVarUtil.conn,schema, csGuiBean.getId());
                break;
            case ADD_DEBUG_INFO:
            	String debugSql = ProcFunSqlUtil.getAddDebugInfoSql(dbtype, v,  schema,name,type == TreeMrType.FUNCTION);
            	if(!StringUtils.isBlank(debugSql)) {
            		if(GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, debugSql)) {
                		PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.EXECUTED_SUCCESSFULLY));
                	};
            	}else {
            		throw new Exception("暂不支持");
            	}
            	
                break;
            case DEBUG_FUNCTION:
            case DEBUG:
            	PkgUtil.examine(dbtype, v, CsVarUtil.conn,schema);
            	if(PkgUtil.supportDebug(dbtype, v,  CsVarUtil.conn,schema, name, csGuiBean.getId(), type == TreeMrType.FUNCTION)) {
            		DebugBean db = new DebugBean();
            		db.setDebugObjId(csGuiBean.getId());
            		db.setDebugObjSchema(schema);
            		
            		if(StringUtils.isBlank(name)) {
            			db.setDebugSql(DebugSqlUtil.getDebugTemplate());
            			db.setParams(null);
            		}else {
            			procBean = getBean(name,schema,csGuiBean.getId(),type== TreeMrType.FUNCTION);
            			db.setDebugSql(ProcFunSqlUtil.getDebugRunSql(dbtype, v, procBean, true));
            			db.setParams(procBean.getParams());
            		}
            		
            		DebugComp comp = new DebugComp(CsVarUtil.parentFrame.getWindow(),db) {
            			
            			@Override
            			protected String getDebugSql() {
            				try {
            					procBean.setParams(db.getParams());
								return ProcFunSqlUtil.getDebugRunSql(dbtype, v, procBean, true);
							} catch (Exception e) {
								Lg.error(getClass(), e);
							}
            				return null;
            			}
            			
            		};
                	comp.show();
            	};
            	
                break;

            default:
                break;
        }
        return null;
    }
    
    
    
	private SqlProcBean getBean(String name,String schema,String objId,boolean isFun) throws Exception {
		SqlProcBean b;
		if(isFun) {
			SqlFunBean sqlFunBean = new SqlFunBean(name);
			String text = ProFunConnUtil.getProcFunSql(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.conn, schema, name, objId, isFun);
			sqlFunBean.setReturnType(PkgUtil.getReturnType(text));
			b = sqlFunBean;
			
		}else {
			b = new SqlProcBean(name);
		}
		
		b.setSchemaName(schema);
		List<SqlParamBean> params = ProFunConnUtil.selectParamBean(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.conn,schema, name, objId, isFun);
		b.setParams(params);
		return b;
	}
    
    
    
    private void showDialog(String title,LastPanel panel,int w,int h) {
    	HDialog dialog = new HDialog(CsVarUtil.parentFrame,w,h,false);
    	dialog.setRootPanel(panel);
    	dialog.setStatusBar(new HBarPanel());
    	dialog.setWindowTitle(title);
    	dialog.show();
    }
    
    private String getName(TreeMrType type) {
    	return type == TreeMrType.FUNCTION?LangMgr.lang(Word2.FUNCTION):LangMgr.lang(Word4.PROCEDURE);
    }
}
