package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.attributeMr.AttrHeardType;
import com.hh.frame.db_sql.UserSqlUtil;
import com.hh.frame.db_sql.info.UsrItem;
import com.hh.frame.db_sql.mysql.MysqlUserUtil;
import com.hh.frame.db_sql.mysql.info.MysqlPerm;
import com.hh.frame.db_sql.mysql.info.MysqlUserBean;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.dbobj3.mariadb.MariadbInstanceObj;
import com.hh.frame.dbobj3.mysql.MysqlInstanceObj;
import com.hh.frame.dbobj3.ob4mysql.ObInstanceObj;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlUsrForm extends UsrBaseForm {
    private TextInput hostLimitInput;
    private TextInput maxQueInput;
    private TextInput maxConInput;
    private TextInput maxUpdateInput;
    private TextInput conConInput;

    
    public MysqlUsrForm(UsrBean usrBean, Connection conn) throws Exception {
        super(usrBean,conn);
        if (usrBean.isUser()) {
            hostLimitInput = new TextInput(UsrItem.host_limit.name());
            maxQueInput = new TextInput(UsrItem.max_query.name());
            maxConInput = new TextInput(UsrItem.max_cons.name());
            maxUpdateInput = new TextInput(UsrItem.max_update.name());
            conConInput = new TextInput(UsrItem.con_cons.name());
        } else if (usrBean.getOprType() == UsrBean.OprType.PERM) {
            nameInput.setValue(usrBean.getName());
            roleGranteComp.refreshTable(getUsrPrivs());
        }
        
    }
    
    @Override
    public LastPanel getLastPanel() {
        LastPanel lastPanel = new LastPanel();
        LastPanel areaLast = new LastPanel();
        if (usrBean.isUser()) {
            HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
            layout.setRightWidth(100);
            HPanel panel = new HPanel(layout);
            panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
            panel.add(new HeightComp(5));
            panel.add(getLabelInput(LangMgr.lang(Word2.HOST)+" :", hostLimitInput));
            panel.add(getLabelInput(LangMgr.sLang(CsL.MAX_CONN)+" :", maxConInput));
            panel.add(getLabelInput(LangMgr.sLang(CsL.MAX_QUERY)+" :", maxQueInput));
            panel.add(getLabelInput(LangMgr.sLang(CsL.CON_CONS)+" :", conConInput));
            panel.add(getLabelInput(LangMgr.sLang(CsL.MAX_UPD)+" :", maxUpdateInput));
            panel.add(new HeightComp(5));
    
            areaLast.setTitle(LangMgr.lang(Word2.CODE));
            areaLast.set(area.getComp());
           
            lastPanel.setHead(panel.getComp());
            lastPanel.set(areaLast.getComp());
        } else if (usrBean.getOprType() == UsrBean.OprType.PERM) {
            areaLast.set(area.getComp());
            hTabPanel = new HTabPanel();
            hTabPanel.setPanel(LangMgr.lang(Word2.CODE),areaLast, newHeaderConfig(LangMgr.lang(Word2.CODE)));
            hTabPanel.setPanel(LangMgr.lang(Word2.AUTHORIZATION),roleGranteComp, newHeaderConfig(LangMgr.lang(Word2.PRIVILEGE)));
            hTabPanel.selectPanel(LangMgr.lang(Word2.AUTHORIZATION));
            lastPanel.set(hTabPanel.getComp());
        }
        return lastPanel;
    }
    
    @Override
    public void upInitForm(String usrName) throws SQLException {
        nameInput.setValue(subMysqlUsr(usrName));
        nameInput.setEnabled(false);
        
        hostLimitInput.setValue(MysqlUserUtil.subMysqlHost(usrName));

        MysqlInstanceObj mysqlInstanceObj = getInstanceObj();
        Map<String, String> userMap = mysqlInstanceObj.getObjProp(AllDbObjType.USER, usrName);
        maxQueInput.setValue( userMap.get(AttrHeardType.MAX_QUESTIONS.name()));
        maxUpdateInput.setValue(userMap.get(AttrHeardType.MAX_UPDATES.name()));
        maxConInput.setValue(userMap.get(AttrHeardType.MAX_CONNECTIONS.name()));
        conConInput.setValue(userMap.get(AttrHeardType.MAX_USER_CONNECTIONS.name()));
    
        oldUser = getUserBean();
    }

    @Override
    public String getSql() {
        StringBuffer buffer = new StringBuffer();
        String sb = "",grant = "",revok = "";
        if (usrBean.isUser()) {
            if (nameInput.isEnabled()) {
                sb = UserSqlUtil.getCreateSql(usrBean.getDbType(), usrBean.getVersion(), getUserBean());
            } else {
                sb = UserSqlUtil.getUpdateSql(usrBean.getDbType(), usrBean.getVersion(), oldUser, getUserBean());
            }
            if (StringUtils.isNotBlank(sb)) buffer.append(sb);
        } else if (usrBean.getOprType() == UsrBean.OprType.PERM) {
            Map<Boolean, List<String>> map = roleGranteComp.getNameMap();
            grant = UserSqlUtil.getGrantRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(true), MysqlUserUtil.toMysqlUsrHost(nameInput.getValue()));
            revok = UserSqlUtil.getRevokRolesSql(usrBean.getDbType(), usrBean.getVersion(), map.get(false), MysqlUserUtil.toMysqlUsrHost(nameInput.getValue()));
            if (StringUtils.isNotBlank(grant)) buffer.append(grant);
            if (StringUtils.isNotBlank(revok)) buffer.append(revok);
        }
        return StringUtils.isNotBlank(buffer) ? buffer.toString() : "";
    }
    
    @Override
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        if (nameInput.isEnabled() && StringUtils.isBlank(pwInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_ENTER_PASSWORD);
        }
        return null;
    }
    
    @Override
    public void inform() {
        if (usrBean.getOprType() == UsrBean.OprType.PERM) hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));
    }
    
    /**
     * 获取用户权限集合
     * @return
     * @throws SQLException
     */
    private List<Map<String, String>> getUsrPrivs() throws SQLException {
        MysqlInstanceObj mysqlInstanceObj = getInstanceObj();
        Map<String, String> map = mysqlInstanceObj.getUserPriv(nameInput.getValue());
        List<Map<String,String>> mapList=  new ArrayList<>();
        for(MysqlPerm s:MysqlPerm.values()){
        	String priv=map.get(s.name().toLowerCase());
        	if(priv!=null) {
        		 HashMap<String, String> hashMap = new HashMap<>();
                 hashMap.put("name", s.getPermKey());
                 hashMap.put("grant", priv.equals("Y") ? "true" : "false");
                 mapList.add(hashMap);
        	}
         
        }
        return mapList;
    }

    private MysqlInstanceObj getInstanceObj() {
        MysqlInstanceObj instanceObj = null;
        DBTypeEnum.Version ver = usrBean.getVersion();
        switch (usrBean.getDbType()) {
            case mysql:
                instanceObj = new MysqlInstanceObj(ver, conn);
                break;
            case mariadb:
                instanceObj = new MariadbInstanceObj(ver, conn);
                break;
            case oceanbase:
                instanceObj = new ObInstanceObj(ver, conn);
                break;
            default:
        }
        return instanceObj;
    }

    private MysqlUserBean getUserBean() {
        MysqlUserBean uBean=new MysqlUserBean(nameInput.getValue(),pwInput.getValue());
        uBean.setHost(hostLimitInput.getValue());
        uBean.setMax_connections(maxConInput.getValue());
        uBean.setMax_questions(maxQueInput.getValue());
        uBean.setMax_updates(maxUpdateInput.getValue());
        uBean.setMax_user_connections(conConInput.getValue());
        return uBean;
    }
}
