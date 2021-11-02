package com.hh.hhdb_admin.mgr.usr.comp.form;


import java.awt.Dimension;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.userMr.mr.AbsUsrMr;
import com.hh.frame.dbobj2.version.VersionBean;
import com.hh.frame.dbobj2.version.VersionUtil;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;

public abstract class UsrBaseForm extends AbsHComp {
    private static final String domainName = UsrBaseForm.class.getName();
    protected Connection conn;
    protected DBTypeEnum dbType;
    protected AbsUsrMr usrMr;

    static {
        try {
			LangMgr2.loadMerge(UsrBaseForm.class);
		} catch (IOException e) {
			PopPaneUtil.error(e);
		}
    }

    public UsrBaseForm(Connection conn) {
        try {
            this.conn = conn;
            this.dbType = DriverUtil.getDbType(conn);
            VersionBean version = VersionUtil.getDbVersion(conn);
            this.usrMr = AbsUsrMr.genUsrSqlMr(this.dbType, version);
        } catch (Exception e) {
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }

    }


    /**
     * 检查form
     */
    public abstract ResultBean checkForm();

    /**
     * 清空form
     */
    public abstract void clearForm() throws Exception;

    /**
     * 获取sql
     */
    public abstract String getSql() throws Exception;

    /**
     * 是否修改
     */
    public abstract void setUpdate(boolean isUpdate);

    public Dimension getSize() {
        return new Dimension(750, 610);
    }

    /**
     * 初始化form
     */
    public abstract void initForm(String usrName) throws Exception;

    protected HDivLayout getLayout() {
        HDivLayout layout = new HDivLayout(GridSplitEnum.C4);
        layout.setMaxWidth(600);
        return layout;
    }

    /**
     * 获取文本
     */
    protected WithLabelInput getLabelInput(String label, AbsInput input) {
        HPanel hPanel = new HPanel(getLayout());
        return new WithLabelInput(hPanel, label, input);
    }

    /**
     * 是否符合日期格式
     */
    protected boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.parse(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }

    /**
     * 结果集
     */
    public static class ResultBean {
        private final int code;
        private final String msg;

        public ResultBean(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

    }

    public String getLang(String key) {
        return LangMgr2.getValue(domainName, key);
    }
}
