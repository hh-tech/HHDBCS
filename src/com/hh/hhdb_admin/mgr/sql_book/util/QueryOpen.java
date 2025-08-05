package com.hh.hhdb_admin.mgr.sql_book.util;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.file_client.openWay.WayAbsTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word3;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.QueryBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;


public class QueryOpen extends WayAbsTool {

    @Override
    public void openFile(File file) throws Exception {
        if (file != null) {
            if (FileUtils.sizeOf(file) > SqlBookMgr.maxFileSize) throw new Exception(String.format(LangMgr.sLang(CsL.SIZE_TIP),SqlBookMgr.maxM));
            String text = FileUtils.readFileToString(file, ChardetUtil.detectCharset(file));
            CsVarUtil.eng2.doCall(CsMgrEnum.QUERY, new QueryBean(text));
        }
    }

    @Override
    protected void setTypeList() {
        typeList.add(".sql");
        typeList.add(".vm");
        typeList.add(".pck");
        typeList.add(".spc");
        typeList.add(".bdy");
        typeList.add(".tps");
        typeList.add(".tpb");
    }

    @Override
    protected void setAppName() {
        appName = LangMgr.lang(Word3.QUERY_WINDOW);
    }
}
