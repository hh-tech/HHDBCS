package com.hh.hhdb_admin.mgr.query2;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlConfDlg;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.TaskAddBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.comp.sql.SqlSplitPanel;
import com.hh.hhdb_admin.conf.SqlPanelConf;
import org.apache.commons.lang3.StringUtils;

/**
 * 查询器插件
 */
public class QueryComp {
    private SqlSplitPanel sqlArea;
    private static SqlConfDlg confDlg=null;

    
    public QueryComp(JdbcBean jdbcBean, String text, HWindow window) throws Exception {
        try {
        	SqlAreaConf sqlconf = new SqlAreaConf();
            sqlconf.setNullValue(CsVarUtil.CONF.getNullValue());
            sqlconf.setPageSize(CsVarUtil.CONF.getSqlPanelConf().getRowPerPage());
            sqlconf.setAutoPop(CsVarUtil.CONF.getSqlPanelConf().isAutoPop());
            sqlconf.setFontSize(CsVarUtil.CONF.getSqlPanelConf().getFontSize());
            sqlconf.setTheme(CsVarUtil.CONF.getSqlPanelConf().getTareaTheme());
            if(confDlg==null) {
            	confDlg=new SqlConfDlg(window.getWindow()) {
	                @Override
	                protected void onSaveTo(HTAreaConf conf) {
	                    if(null != conf) {
	                    	onConfirm();
	                        SqlAreaConf sqlconf = (SqlAreaConf) conf;
	                        SqlPanelConf sqlPanelConf = CsVarUtil.CONF.getSqlPanelConf();
	                        sqlPanelConf.setRowPerPage(sqlconf.getPageSize());
	                        sqlPanelConf.setAutoPop(sqlconf.isAutoPop());
	                        sqlPanelConf.setFontSize(sqlconf.getFontSize());
	                        sqlPanelConf.setTAreaThemeType(sqlconf.getTheme());
	                        CsVarUtil.CONF.setNullValue(sqlconf.getNullValue());
	                        CsVarUtil.saveConfig();
	                        confDlg.hide();	                  
	                    }
	                }
	            };
	            confDlg.setWindowTitle(LangMgr.sLang(CsL.CONFIGURATION_STATEMENT_WINDOW));
            }
	            

            sqlArea=new SqlSplitPanel(sqlconf,jdbcBean) {
            	@Override
            	protected void callDbTask(String schema, String sql) {
            		String sc = schema;
            		try {
            			if (DBTypeEnum.isInformixSeries(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion())) {
							sc = SqlWinUtil.getCurrSchema(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion(),CsVarUtil.conn);
						}
            		}catch (Exception e){
            		    e.printStackTrace();
            		}
					TaskAddBean bean = new TaskAddBean();
					bean.setTaskType(TaskType.QUERY_EXP);
					bean.setSchema(sc);
					bean.setQuerySql(sql);
					CsVarUtil.eng2.doCall(CsMgrEnum.DB_TASK, bean);
            	}
            };
            sqlArea.setCfgDlg(confDlg);
            sqlArea.setFRDlg(GlobalWorkUtil.getFrDlg());
            sqlArea.setGotoDlg(GlobalWorkUtil.getAreaGotoDlg());
            if (StringUtils.isNotBlank(text)) sqlArea.setText(text);
        }catch (Exception e){
            PopDlgUtil.logErr(QueryComp.class, window, e);
        }
    }
    
    public SqlSplitPanel getPanel(){
        return sqlArea;
    }
}
