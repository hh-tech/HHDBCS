package com.hh.hhdb_admin.test.constraint;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.constraint.ConstraintMgr;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;

/**
 * @author YuSai
 */
public class ConstraintTestComp extends AbsMainTestComp {

    @Override
    public void init() {
        String tableName = (String) JOptionPane.showInputDialog(null, "输入表名", "",
                JOptionPane.PLAIN_MESSAGE, null, null, "");
        if (StringUtils.isBlank(tableName)) {
            System.exit(0);
        }
        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        if (jdbcBean != null) {

            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbcBean);
            if (dbTypeEnum != null) {
                String name = StringUtils.isEmpty(jdbcBean.getSchema()) ? jdbcBean.getUser() : jdbcBean.getSchema();
                String schema = LoginUtil.getRealName(name, dbTypeEnum.name());
                jdbcBean.setUser(LoginUtil.getRealName(jdbcBean.getUser(), dbTypeEnum.name()));
                jdbcBean.setSchema(schema);

                JsonObject json = new JsonObject();
                json.add("schema", schema);
                tableName = LoginUtil.getRealName(tableName, dbTypeEnum.name());
                json.add("table", tableName);
                HBarLayout barLayout = new HBarLayout();
                barLayout.setAlign(AlignEnum.LEFT);
                HBarPanel barPanel = new HBarPanel(barLayout);
                barPanel.add(new HButton("新增检查约束") {
                    @Override
                    public void onClick() {
                        json.add("constType", TreeMrType.CHECK_KEY_GROUP.name());
                        json.add("__CMD", ConstraintMgr.CMD_SHOW_CONSTRAINT_CK_DIALOG);
                        StartUtil.eng.doPush(CsMgrEnum.CONSTRAINT, json);
                    }
                });
                barPanel.add(new HButton("新增主键约束") {
                    @Override
                    public void onClick() {
                        json.add("constType", TreeMrType.PRIMARY_KEY_GROUP.name());
                        json.add("__CMD", ConstraintMgr.CMD_SHOW_CONSTRAINT_PK_DIALOG);
                        StartUtil.eng.doPush(CsMgrEnum.CONSTRAINT, json);
                    }
                });
                barPanel.add(new HButton("新增唯一键约束") {
                    @Override
                    public void onClick() {
                        json.add("constType", TreeMrType.UNIQUE_KEY_GROUP.name());
                        json.add("__CMD", ConstraintMgr.CMD_SHOW_CONSTRAINT_UK_DIALOG);
                        StartUtil.eng.doPush(CsMgrEnum.CONSTRAINT, json);
                    }
                });
                barPanel.add(new HButton("新增外键约束") {
                    @Override
                    public void onClick() {
                        json.add("constType", TreeMrType.FOREIGN_KEY_GROUP.name());
                        json.add("__CMD", ConstraintMgr.CMD_SHOW_CONSTRAINT_FK_DIALOG);
                        StartUtil.eng.doPush(CsMgrEnum.CONSTRAINT, json);
                    }
                });
                tFrame.setWindowTitle("数据库:" + dbTypeEnum.name() + "||表:" + tableName + "--新增约束测试");
                tFrame.setToolBar(barPanel);
            }
        }
    }

}
