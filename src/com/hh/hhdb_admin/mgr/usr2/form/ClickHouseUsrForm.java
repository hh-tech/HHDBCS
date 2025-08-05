package com.hh.hhdb_admin.mgr.usr2.form;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;


public class ClickHouseUsrForm extends UsrBaseForm {

    public ClickHouseUsrForm(UsrBean usrBean, Connection conn) {
        super(usrBean, conn);
    }

    @Override
    public LastPanel getLastPanel() {
        LastPanel lastPanel = new LastPanel();
        LastPanel areaLast = new LastPanel();
        areaLast.set(area.getComp());
        hTabPanel = new HTabPanel();
        hTabPanel.setPanel(LangMgr.lang(Word2.CODE), areaLast, newHeaderConfig(LangMgr.lang(Word2.CODE)));
        lastPanel.set(hTabPanel.getComp());
        return lastPanel;
    }

    @Override
    public void upInitForm(String usrName) throws SQLException {
    }

    @Override
    public String getSql() {
        if (usrBean.isUser()) {
            return String.format("CREATE USER %s IDENTIFIED BY '%s'", nameInput.getValue(), pwInput.getValue());
        }
        if (usrBean.isRole()) {
            return String.format("CREATE ROLE %s", nameInput.getValue());
        }
        return null;
    }

    @Override
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        if (usrBean.isUser() && nameInput.isEnabled() && StringUtils.isBlank(pwInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_ENTER_PASSWORD);
        }
        return null;
    }

    @Override
    public void inform() {
        hTabPanel.selectPanel(LangMgr.lang(Word2.CODE));
    }
}
