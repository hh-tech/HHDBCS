package com.hh.hhdb_admin.test.index;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.index.IndexComp;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class IndexCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        String tableName = (String) JOptionPane.showInputDialog(null, "输入表名", "",
                JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (StringUtils.isBlank(tableName)) {
            System.exit(0);
        }
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            Connection conn = ConnUtil.getConn(jdbcBean);
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
            String name = StringUtils.isEmpty(jdbcBean.getSchema()) ? jdbcBean.getUser() : jdbcBean.getSchema();
            String schema = LoginUtil.getRealName(name, dbTypeEnum.name());
            jdbcBean.setUser(LoginUtil.getRealName(jdbcBean.getUser(), dbTypeEnum.name()));
            jdbcBean.setSchema(schema);
            tableName = LoginUtil.getRealName(tableName, dbTypeEnum.name());
            IndexComp common = new IndexComp(ConnUtil.getConn(jdbcBean), dbTypeEnum, schema, tableName) {
                @Override
                public void refreshTree() {
                    System.out.println("保存成功");
                }
            };
            HFrame frame = new HFrame(HFrame.MIDDLE_WIDTH);
            frame.setWindowTitle("数据库:" + dbTypeEnum.name() + "||表:" + tableName + "--新增约束测试");
            frame.setRootPanel(common.getPanel());
            frame.show();
        }
    }
}
