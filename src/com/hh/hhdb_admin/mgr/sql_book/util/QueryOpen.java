package com.hh.hhdb_admin.mgr.sql_book.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.hh.frame.file_client.openWay.WayAbsTool;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookComp;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;


public class QueryOpen extends WayAbsTool {

    @Override
    public void openFile(File flie, String charset) throws Exception {
        if (flie != null) {
            if (FileUtils.sizeOf(flie) > SqlBookMgr.maxFileSize) throw new Exception(String.format(LangMgr2.getValue(SqlBookComp.class.getName(), "SIZE_TIP"),SqlBookMgr.maxM));
            String text = FileUtils.readFileToString(flie, charset);
            StartUtil.eng.doPush(CsMgrEnum.QUERY, GuiJsonUtil.toJsonCmd(QueryMgr.CMD_SHOW_QUERY).add("text", text));
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
        super.appName = SqlBookComp.getLang("query");
    }
}
