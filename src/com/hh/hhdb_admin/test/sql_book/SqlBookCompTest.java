package com.hh.hhdb_admin.test.sql_book;

import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookComp;

import java.io.File;

public class SqlBookCompTest {
    public static void main(String[] args) {
        try {
            HHSwingUi.init();

            IconFileUtil.setIconBaseDir(new File("etc/icon/"));
            SqlBookComp bookComp = new SqlBookComp(StartUtil.getEtcFile().getAbsolutePath(), StartUtil.getMainDialog()) {
                @Override
                protected void openTask(String filePath) {
                    PopPaneUtil.info("打开任务框");
                }

                @Override
                protected void openQuery(String text) {
                    PopPaneUtil.info("在查询框中打开");

                }

                @Override
                protected void openVM(String text) {
                    PopPaneUtil.info("在模版框中打开");
                }
            };
            bookComp.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
