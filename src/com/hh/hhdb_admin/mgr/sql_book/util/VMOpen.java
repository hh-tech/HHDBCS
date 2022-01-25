package com.hh.hhdb_admin.mgr.sql_book.util;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.file_client.openWay.WayAbsTool;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookComp;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import com.hh.hhdb_admin.mgr.vm_editor.VmMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;


public class VMOpen extends WayAbsTool {

    @Override
    public void openFile(File file) throws Exception {
        if (file != null) {
            if (FileUtils.sizeOf(file) > SqlBookMgr.maxFileSize) throw new Exception(String.format(LangMgr2.getValue(SqlBookComp.class.getName(), "SIZE_TIP"),SqlBookMgr.maxM));
            String text = FileUtils.readFileToString(file, ChardetUtil.detectCharset(file));
            StartUtil.eng.doPush(CsMgrEnum.VM, GuiJsonUtil.toJsonCmd(VmMgr.CMD_SHOW_VM).add("text", text));
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
        super.appName = SqlBookComp.getLang("templateEditor");
    }
}
