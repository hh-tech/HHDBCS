package com.hh.hhdb_admin.mgr.usr.comp;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.userMr.mr.AbsUsrMr;
import com.hh.frame.dbobj2.version.VersionUtil;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.usr.util.UsrUtil;

/**
 * 重命名管理面板
 */
public abstract class ReNameUsrComp {
    private final Connection conn;
    private DBTypeEnum dbType;
    private static final String domainName = ReNameUsrComp.class.getName();
    private final static String LK_RENAME_TITLE = "RENAME_TITLE";
    private final static String LK_RENAME_INPUT = "RENAME_INPUT";
    private final static String LK_NAME_EQUAL_ERROR = "NAME_EQUAL_ERROR";
    private final static String LK_INPUT_NEW_NAME = "INPUT_NEW_NAME";
    private final static String LK_UPD_SUCCESS = "UPD_SUCCESS";
    private final static String LK_UPD_FAILD = "UPD_FAILD";

    static {
        try {
			LangMgr2.loadMerge(ReNameUsrComp.class);
		} catch (IOException e) {
			PopPaneUtil.error(e);
		}
    }


    public ReNameUsrComp(Connection conn) {
        this.conn = conn;
        try {
            this.dbType = DriverUtil.getDbType(conn);
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    /**
     * 显示面板
     */
    public void show(HDialog parent, String usrName) {
        if (StartUtil.default_language == LangEnum.EN) {
            JOptionPane.setDefaultLocale(Locale.ENGLISH);
        } else {
            JOptionPane.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        }
        String newName;
        JOptionPane pane = new JOptionPane(
                LangMgr2.getValue(domainName, LK_RENAME_INPUT),
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null, null
        );

        pane.setWantsInput(true);
        pane.setSelectionValues(null);
        pane.selectInitialValue();
        pane.setInitialSelectionValue(UsrUtil.formatName(dbType, usrName));
        JDialog dialog = pane.createDialog(parent.getWindow().getParent(), LangMgr2.getValue(domainName, LK_RENAME_TITLE) + "(" + usrName + ")");
        dialog.setIconImage(IconFileUtil.getLogo().getImage());
        dialog.setVisible(true);
        dialog.dispose();
        newName = (String) pane.getInputValue();
        if (pane.getInputValue() == JOptionPane.UNINITIALIZED_VALUE) {
            return;
        }
        if (newName == null) {
            return;
        }
        if (StringUtils.isBlank(newName)) {
            PopPaneUtil.error(LangMgr2.getValue(domainName, LK_INPUT_NEW_NAME));
        } else if (newName.trim().equals(UsrUtil.formatName(dbType, usrName))) {
            PopPaneUtil.error(LangMgr2.getValue(domainName, LK_NAME_EQUAL_ERROR));
        } else {
            try {
                if (conn != null) {
                    AbsUsrMr usrMr = AbsUsrMr.genUsrSqlMr(this.dbType, VersionUtil.getDbVersion(conn));
                    usrMr.reNameUsr(newName, UsrUtil.formatName(dbType, usrName), conn);
                    infoRefreshUsr();
                    PopPaneUtil.info(LangMgr2.getValue(domainName, LK_UPD_SUCCESS));
                }
            } catch (Exception e) {
                PopPaneUtil.error(LangMgr2.getValue(domainName, LK_UPD_FAILD) + e.getMessage());
            }
        }
    }


    protected abstract void infoRefreshUsr();

}
