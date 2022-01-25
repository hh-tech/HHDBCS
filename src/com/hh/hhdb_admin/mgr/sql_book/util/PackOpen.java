package com.hh.hhdb_admin.mgr.sql_book.util;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.file_client.openWay.WayAbsTool;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginComp;
import com.hh.hhdb_admin.mgr.pack.PackageMgr;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookComp;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;

/**
 * @author YuSai
 */
public class PackOpen extends WayAbsTool {

    @Override
    public void openFile(File file) throws Exception {
        if (file != null) {
            LoginBean loginBean = LoginComp.loginBean;
            if (loginBean != null) {
                JdbcBean jdbcBean = loginBean.getJdbc();
                DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
                if (dbTypeEnum != null) {
                    if (Arrays.asList(DBTypeEnum.oracle.name(), DBTypeEnum.dm.name()).contains(dbTypeEnum.name())) {
                        if (FileUtils.sizeOf(file) > SqlBookMgr.maxFileSize) {
                            throw new Exception(String.format(LangMgr2.getValue(SqlBookComp.class.getName(), "SIZE_TIP"),SqlBookMgr.maxM));
                        }
                        String text = FileUtils.readFileToString(file, ChardetUtil.detectCharset(file));
                        StartUtil.eng.doPush(CsMgrEnum.PACKAGE, GuiJsonUtil.toJsonCmd(PackageMgr.CMD_OPEN_AS_DESIGN)
                                .add(StartUtil.PARAM_SCHEMA, LoginComp.loginBean.getJdbc().getSchema())
                                .add("text", text)
                                .add("fileName", file.getName()));
                    } else {
                        PopPaneUtil.info(SqlBookComp.getLang("onlyOracleDm"));
                    }
                }
            }
        }
    }

    @Override
    protected void setTypeList() {
        typeList.add(".pck");
        typeList.add(".spc");
        typeList.add(".bdy");
    }

    @Override
    protected void setAppName() {
        super.appName = SqlBookComp.getLang("packageDesign");
    }
}
