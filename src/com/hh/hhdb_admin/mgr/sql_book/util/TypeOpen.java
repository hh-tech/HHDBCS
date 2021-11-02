package com.hh.hhdb_admin.mgr.sql_book.util;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.file_client.openWay.WayAbsTool;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginComp;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookComp;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import com.hh.hhdb_admin.mgr.type.TypeMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Objects;

/**
 * @author YuSai
 */
public class TypeOpen extends WayAbsTool {

    @Override
    public void openFile(File file, String charset) throws Exception {
        if (file != null) {
            LoginBean loginBean = LoginComp.loginBean;
            if (loginBean != null) {
                JdbcBean jdbcBean = loginBean.getJdbc();
                DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
                if (dbTypeEnum != null) {
                    if (Objects.equals(DBTypeEnum.oracle.name(), dbTypeEnum.name())) {
                        if (FileUtils.sizeOf(file) > SqlBookMgr.maxFileSize) {
                            throw new Exception(String.format(LangMgr2.getValue(SqlBookComp.class.getName(), "SIZE_TIP"),SqlBookMgr.maxM));
                        }
                        String[] names = file.getName().split("\\.");
                        String type = names[names.length - 1];
                        OraSessionEnum sessionEnum;
                        if ("tps".equals(type)) {
                            sessionEnum = OraSessionEnum.type;
                        } else {
                            sessionEnum = OraSessionEnum.typebody;
                        }
                        String text = FileUtils.readFileToString(file, charset);
                        StartUtil.eng.doPush(CsMgrEnum.TYPE, GuiJsonUtil.toJsonCmd(TypeMgr.CMD_OPEN_AS_DESIGN)
                                .add(StartUtil.PARAM_SCHEMA, LoginComp.loginBean.getJdbc().getSchema())
                                .add(TypeMgr.PARAM_TYPE, sessionEnum.name())
                                .add("text", text)
                                .add("fileName", names[0]));
                    } else {
                        PopPaneUtil.info(SqlBookComp.getLang("onlyOracle"));
                    }
                }
            }
        }
    }

    @Override
    protected void setTypeList() {
        typeList.add(".tps");
        typeList.add(".tpb");
    }

    @Override
    protected void setAppName() {
        super.appName = SqlBookComp.getLang("typeDesign");
    }
}
