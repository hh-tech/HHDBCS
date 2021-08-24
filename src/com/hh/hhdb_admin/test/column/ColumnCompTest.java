package com.hh.hhdb_admin.test.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.column.ColumnComp;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;

/**
 * @author YuSai
 */
public class ColumnCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));
        String tableName = (String) JOptionPane.showInputDialog(null, "", "输入表名",
                JOptionPane.PLAIN_MESSAGE, null, null, "");
        HFrame frame = new HFrame(HFrame.SMALL_WIDTH);
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(new HButton("新增列") {
            @Override
            protected void onClick() {
                addOrEditColumn(tableName, false);
            }
        });
        barPanel.add( new HButton("设计列") {
            @Override
            protected void onClick() {
                addOrEditColumn(tableName, true);
            }
        });
        frame.setRootPanel(barPanel);
        frame.show();
    }

    private static void addOrEditColumn(String tableName, boolean isUpdate) {
        String colName = "";
        if (isUpdate) {
            colName = (String) JOptionPane.showInputDialog(null, "", "输入字段名",
                    JOptionPane.PLAIN_MESSAGE, null, null, "");
        }
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {
            try {
                Connection conn = ConnUtil.getConn(jdbcBean);
                DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
                String name = StringUtils.isEmpty(jdbcBean.getSchema()) ? jdbcBean.getUser() : jdbcBean.getSchema();
                if (dbTypeEnum != null) {
                    String schema = LoginUtil.getRealName(name, dbTypeEnum.name());
                    jdbcBean.setUser(LoginUtil.getRealName(jdbcBean.getUser(), dbTypeEnum.name()));
                    jdbcBean.setSchema(schema);
                    tableName = LoginUtil.getRealName(tableName, dbTypeEnum.name());
                    colName = LoginUtil.getRealName(colName, dbTypeEnum.name());
                    HDialog dialog = new HDialog(800, 600);
                    ColumnComp columnForm = new ColumnComp(conn, dbTypeEnum, schema, tableName) {
                        @Override
                        public void refreshTree() {
                            System.out.println("保存成功");
                        }
                    };
                    columnForm.show(isUpdate, colName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
