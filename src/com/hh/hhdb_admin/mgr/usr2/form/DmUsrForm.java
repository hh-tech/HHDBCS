package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.dm.DmRoleUserSqlUtil;
import com.hh.frame.db_sql.dm.info.DmSelUserInfoBean;
import com.hh.frame.db_sql.ora.OraTableSpaceSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


public class DmUsrForm extends OrUsrForm {
    
    public DmUsrForm(UsrBean usrBean, Connection conn) throws Exception {
        super(usrBean,conn);
    }
    
    protected void init() throws Exception{
        defaultTsBox = new SelectBox();
        defaultTsBox.addOption("","");
        tmpTsBox = new SelectBox();
        tmpTsBox.addOption("","");
        
        if (usrBean.isUser()) {
            //设置用户的表空间
            Map<String,String> hashMap = SqlQueryUtil.selectTowColMap(conn, OraTableSpaceSqlUtil.getNameAndContentsSql());
            for (String s : hashMap.keySet()) {
                if (hashMap.get(s).equals("PERMANENT")) {
                    defaultTsBox.addOption(s, s);
                } else if (hashMap.get(s).equals("TEMPORARY")) {
                    tmpTsBox.addOption(s, s);
                }
            }
        }
        
        //显示所有可选角色
        roleGranteComp.refreshTable(getUsrRoles());
    }
    
    @Override
    public LastPanel getLastPanel() {
        LastPanel lastPanel = new LastPanel();
        if (usrBean.isUser()) {
            HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
            layout.setRightWidth(100);
            HPanel panel = new HPanel(layout);
            panel.add(new HeightComp(5));
            panel.add(getLabelInput(LangMgr.sLang(CsL.DEFAULT_TS)+" ：", defaultTsBox));
            panel.add(getLabelInput(LangMgr.sLang(CsL.TMP_TS)+" ：", tmpTsBox));
            panel.add(new HeightComp(5));
            panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
            lastPanel.setHead(panel.getComp());
        }
        LastPanel areaLast = new LastPanel();
        areaLast.set(area.getComp());
        hTabPanel = new HTabPanel();
        hTabPanel.setPanel(LangMgr.lang(Word2.CODE),areaLast, newHeaderConfig(LangMgr.lang(Word2.CODE)));
        hTabPanel.setPanel(LangMgr.lang(Word2.AUTHORIZATION),roleGranteComp, newHeaderConfig(LangMgr.lang(usrBean.getOprType() == UsrBean.OprType.PERM ? Word2.PRIVILEGE : Word2.ROLE)));
        hTabPanel.selectPanel(LangMgr.lang(Word2.AUTHORIZATION));
    
        lastPanel.set(hTabPanel.getComp());
        return lastPanel;
    }
    
    @Override
    public void upInitForm(String usrName) throws SQLException {
        nameInput.setValue(usrName);
        nameInput.setEnabled(false);
        roleGranteComp.refreshTable(getUsrRoles());
    
        String sql = DmRoleUserSqlUtil.getSelUserInfoSql(usrBean.getVersion(), new DmSelUserInfoBean(usrName));
        Map<DmSelUserInfoBean.DmUserInfo, String> map=SqlQueryUtil.selectOneStrMap( conn, sql, DmSelUserInfoBean.DmUserInfo.class);
        defaultTsBox.setValue(map.get(DmSelUserInfoBean.DmUserInfo.DEFAULT_TABLESPACE));
        tmpTsBox.setValue(map.get(DmSelUserInfoBean.DmUserInfo.TEMPORARY_TABLESPACE));
        
        oldUser = getUserBean();
    }
    
    /**
     * 获取用户权限集合
     * @return
     * @throws SQLException
     */
    protected List<Map<String, String>> getUsrPrivs() throws SQLException {
        List<String> myPerms = SqlQueryUtil.selectOneColumn(conn, DmRoleUserSqlUtil.getUsePrivsNamesSql(nameInput.getValue()));
        List<String> allPerms = SqlQueryUtil.selectOneColumn(conn, DmRoleUserSqlUtil.getUsePrivsNamesSql("DBA"));
        List<Map<String, String>> mapList = new ArrayList<>();
        for(String s:allPerms){
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("name", s);
            hashMap.put("grant", myPerms.contains(s) ? "true" : "false");
            mapList.add(hashMap);
        }
        return mapList;
    }
}
