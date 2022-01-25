package com.hh.hhdb_admin.test.constraint;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.constraint.ConstraintComp;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class ConstraintCompTest {

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
            HFrame frame = new HFrame(HFrame.MIDDLE_WIDTH);
            Connection conn = ConnUtil.getConn(jdbcBean);
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(conn);
            String name = StringUtils.isEmpty(jdbcBean.getSchema()) ? jdbcBean.getUser() : jdbcBean.getSchema();
            String schema = LoginUtil.getRealName(name, dbTypeEnum.name());
            jdbcBean.setUser(LoginUtil.getRealName(jdbcBean.getUser(), dbTypeEnum.name()));
            jdbcBean.setSchema(schema);
            tableName = LoginUtil.getRealName(tableName, dbTypeEnum.name());
            TableComp.jdbcBean = jdbcBean;
            TableComp.schemaName = jdbcBean.getSchema();
            HTabPanel tabPane = new HTabPanel();
            // 检查约束
            ConstraintComp ckComp = new ConstraintComp(TreeMrType.CHECK_KEY_GROUP, conn, dbTypeEnum, schema, tableName) {
                @Override
                public void refreshTree() {
                    System.out.println("新增检查约束，保存成功");
                }
            };
            tabPane.addPanel("ck", ckComp.getOtherPanel(),new HeaderConfig("检查约束").setFixTab(true));
            // 主键约束
            ConstraintComp pkComp = new ConstraintComp(TreeMrType.PRIMARY_KEY_GROUP, conn, dbTypeEnum, schema, tableName) {
                @Override
                public void refreshTree() {
                    System.out.println("新增主键约束，保存成功");
                }
            };
            tabPane.addPanel("pk", pkComp.getOtherPanel(),new HeaderConfig( "主键约束").setFixTab(true));
            // 唯一键约束
            ConstraintComp ukComp = new ConstraintComp(TreeMrType.UNIQUE_KEY_GROUP, conn, dbTypeEnum, schema, tableName) {
                @Override
                public void refreshTree() {
                    System.out.println("新增唯一键约束，保存成功");
                }
            };
            tabPane.addPanel("uk", ukComp.getOtherPanel(),new HeaderConfig("唯一键约束").setFixTab(true));
            // 外键约束
            ConstraintComp fkComp = new ConstraintComp(TreeMrType.FOREIGN_KEY_GROUP, conn, dbTypeEnum, schema, tableName) {
                @Override
                public void refreshTree() {
                    System.out.println("新增外键约束，保存成功");
                }
            };
            tabPane.addPanel("fk", fkComp.getForePanel(),new HeaderConfig("外键约束").setFixTab(true));
            LastPanel lastPanel = new LastPanel();
            lastPanel.set(tabPane.getComp());
            HPanel rootPanel = new HPanel();
            rootPanel.setLastPanel(lastPanel);
            frame.setRootPanel(rootPanel);
            frame.setWindowTitle("数据库:" + dbTypeEnum.name() + "||表:" + tableName + "--新增约束测试");
            frame.show();
        }
    }

}
