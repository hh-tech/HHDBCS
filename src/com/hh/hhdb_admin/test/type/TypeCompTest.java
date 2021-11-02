package com.hh.hhdb_admin.test.type;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.mgr.type.TypeComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class TypeCompTest {

    public static void main(String[] args) throws Exception {
        //初始化自定义UI
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            DBTypeEnum dbType = DriverUtil.getDbType(jdbcBean);
            if (dbType != null) {
                if (dbType.equals(DBTypeEnum.oracle)) {
                    String name = StringUtils.isEmpty(jdbcBean.getSchema()) ? jdbcBean.getUser() : jdbcBean.getSchema();
                    String schema = LoginUtil.getRealName(name, dbType.name());
                    jdbcBean.setUser(LoginUtil.getRealName(jdbcBean.getUser(), dbType.name()));
                    jdbcBean.setSchema(schema);
                    Connection conn = ConnUtil.getConn(jdbcBean);
                    TypeComp typeComp = new TypeComp(conn, schema, OraSessionEnum.type);
                    HFrame frame = new HFrame();
                    frame.setRootPanel(typeComp.getPanel("test_type"));
                    frame.show();
                    frame.maximize();
                } else {
                    PopPaneUtil.info(dbType.name() + "暂不支持");
                }
            }
        }
    }
}
