package com.hh.hhdb_admin.mgr.sql_book.util;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.file_client.openWay.WayAbsTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.pack.PackageBean;
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
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(CsVarUtil.jdbcCloneWithSsh());
            if (dbTypeEnum != null) {
                if (Arrays.asList(DBTypeEnum.oracle.name(), DBTypeEnum.dm.name()).contains(dbTypeEnum.name())) {
                    if (FileUtils.sizeOf(file) > SqlBookMgr.maxFileSize) {
                        throw new Exception(String.format(LangMgr.sLang(CsL.SIZE_TIP),SqlBookMgr.maxM));
                    }
                    String text = FileUtils.readFileToString(file, ChardetUtil.detectCharset(file));
                    PackageBean packageBean = new PackageBean(PackageBean.OprType.SHOW,null);
                    packageBean.setText(text);
                    packageBean.setFileName(file.getName());
                    packageBean.setSchema(CsVarUtil.jdbc.getSchema());
                    CsVarUtil.eng2.doCall(CsMgrEnum.PACKAGE, packageBean);
                } else {
                    PopDlgUtil.info(LangMgr.sLang(CsL.ONLY_ORACLE_DM));
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
        appName = LangMgr.sLang(CsL.PACKAGE_DESIGN);
    }
}
