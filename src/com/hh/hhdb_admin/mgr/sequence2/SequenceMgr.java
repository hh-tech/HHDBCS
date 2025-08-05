package com.hh.hhdb_admin.mgr.sequence2;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.SeqSqlUtil;
import com.hh.frame.db_sql.conn.SeqConnUtil;
import com.hh.frame.db_sql.info.SqlSelSeqBean;
import com.hh.frame.db_sql.info.SqlSeqBean;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 * 序列
 */
public class SequenceMgr extends AbsGuiMgr {
	
    @Override
    public void init(JsonObject jObj) {
    	
    }
    
    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.SEQUENCE;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        SeqBean csGuiBean = new SeqBean(bj.getJobj());
        String schemaName = csGuiBean.getSchema();
        String seqName = csGuiBean.getName();
        DBTypeEnum dbtype=CsVarUtil.getDbtype();
        DBTypeEnum.Version ver=CsVarUtil.jdbc.getCurrentVersion();
        Connection conn=CsVarUtil.conn;
        JdbcBean jdbc=CsVarUtil.jdbc;
        switch (csGuiBean.getOprType()) {
            case ADD:
            	SeqPanel seqPanel = getSeqPanel(csGuiBean.getRefreshNodeId());
            	seqPanel.init(schemaName);
            	showDialog(LangMgr.lang(Word2.CREATE) + LangMgr.lang(Word2.SEQUENCE),seqPanel);
                break;
            case UPDATE:
            	SeqPanel seqPanel2 = getSeqPanel(csGuiBean.getRefreshNodeId());
            	SqlSelSeqBean sqlSel = new SqlSelSeqBean(schemaName, seqName);
            	SqlSeqBean b = SeqConnUtil.selectSeq(dbtype,ver, conn,sqlSel);
            	seqPanel2.init(b);
            	showDialog(LangMgr.lang(Word2.MODIFY) + LangMgr.lang(Word2.SEQUENCE),seqPanel2);
                break;
            case RENAME:
            	String newSeqName = PopDlgUtil.nameInput(CsVarUtil.parentFrame,LangMgr.sLang(CsL.INPUT_SEQ),seqName);
                if (!StringUtils.isBlank(newSeqName)) {
                    if (newSeqName.trim().equals(seqName)) {
                        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(CsL.SAME_NAME));
                        break;
                    }
                    String sql = SeqSqlUtil.getRenameSeqSql(dbtype,ver,addQuote(schemaName),addQuote(seqName), newSeqName);
                    if(GlobalWorkUtil.exeSql(jdbc,conn, sql)) {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
						PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(FrameL.TIP_OPERATION_SUCCEEDED));
					}
                }
                break;

            default:
                break;
        }
        return null;
    }
    
    private String addQuote(String name) {
    	return SqlStrUtil.addQuote4DDL(name,CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion());
    }
    
    private void showDialog(String title,SeqPanel panel) {
    	HDialog dialog = new HDialog(CsVarUtil.parentFrame,800,700,false);
    	dialog.setRootPanel(panel);
    	dialog.setStatusBar(new HBarPanel());
    	dialog.setWindowTitle(title);
    	dialog.show();
    }
    
    private SeqPanel getSeqPanel(String id) {
    	return new SeqPanel() {
    		@Override
    		protected void inform() {
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(id));
    		}
    	};
    }
}
