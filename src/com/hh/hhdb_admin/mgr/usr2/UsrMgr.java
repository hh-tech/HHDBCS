package com.hh.hhdb_admin.mgr.usr2;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.db_sql.UserSqlUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

public class UsrMgr extends AbsGuiMgr {
    private Connection conn;

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.USR;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        conn = CsVarUtil.conn;
        JdbcBean jdbc=CsVarUtil.jdbc;
        UsrBean csGuiBean = new UsrBean(bj.getJobj());
        csGuiBean.setDbType(CsVarUtil.getDbtype());
        csGuiBean.setVersion(CsVarUtil.jdbc.getCurrentVersion());
        switch (csGuiBean.getOprType()) {
            case ADD_USER:
            case UPDATE_USER:
            case ADD_ROLE:
            case UPDATE_ROLE:
            case PERM:
                UsrPanel addUpComp=new UsrPanel(csGuiBean,jdbc,conn) {
                    @Override
                    protected void inform() {
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                    }
                };
                if(csGuiBean.getOprType() == UsrBean.OprType.ADD_USER || csGuiBean.getOprType() == UsrBean.OprType.ADD_ROLE)
                	addUpComp.show();
                else
                	addUpComp.show(csGuiBean.getName());
                break;           
//            case PERM:
//                new PermPanel(csGuiBean,jdbc,conn);
//                break;
            case RENAME:
                String newName = PopDlgUtil.nameInput(LangMgr.sLang(CsL.PLEASE_ENTER_NAME), csGuiBean.getName());
                if (!StringUtils.isBlank(newName)) {
                    if (newName.trim().equals(csGuiBean.getName())) {
                        PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(CsL.NEW_NAME_THE_SAME));
                        break;
                    }
                    try {
                        String sql = UserSqlUtil.getRenameUserSql(DriverUtil.getDbType(jdbc),jdbc.getCurrentVersion(),csGuiBean.getName(),newName);
                        if (GlobalWorkUtil.exeSql(jdbc,CsVarUtil.conn, sql)) {
                            CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                            PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.lang(Word4.UPDATE_SUCCESS));
                        }
                    } catch (Exception e) {
                        PopDlgUtil.error(CsVarUtil.parentFrame,new Exception(LangMgr.lang(Word4.MODIFICATION_FAILED) + e.getMessage()));
                    }
                }
                break;
            default:
                break;
        }
        return null;
    }
}
