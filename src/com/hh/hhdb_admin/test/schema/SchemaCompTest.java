package com.hh.hhdb_admin.test.schema;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.schema.SchemaComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

import java.sql.Connection;

/**
 * @author: Jiang
 * @date: 2020/12/24
 */

public class SchemaCompTest {

    public static void main(String[] args) throws Exception {
        new SchemaCompTest().init();
    }

    private SelectBox selectBox;

    public void init() throws Exception {
        HHSwingUi.init();
        SchemaComp schemaComp = new SchemaComp();
        HBarLayout layout = new HBarLayout();
        layout.setAlign(AlignEnum.LEFT);
        HBarPanel toolBar = new HBarPanel();

        JdbcBean jdbcBean = MgrTestUtil.getJdbcBean();
        Connection conn = ConnUtil.getConn(jdbcBean);

        selectBox = new SelectBox();
        loadSchema(conn, jdbcBean);

        toolBar.add(new HButton("新增模式") {
            @Override
            protected void onClick() {
                try {
                    schemaComp.add(conn, jdbcBean);
                } catch (Exception exception) {
                    PopPaneUtil.error(exception.getMessage());
                }
                loadSchema(conn, jdbcBean);
            }
        });
        toolBar.add(new HButton("修改模式") {
            @Override
            protected void onClick() {
                schemaComp.update(conn, selectBox.getValue());
                loadSchema(conn, jdbcBean);
            }
        });
        toolBar.add(new HButton("删除模式") {
            @Override
            protected void onClick() {
//                try {
//                    schemaComp.delete(conn, selectBox.getValue());
//                } catch (Exception exception) {
//                    exception.printStackTrace();
//                }
//                loadSchema(conn, jdbcBean);
            }
        });
        HDialog dialog = StartUtil.getMainDialog();
        dialog.setSize(800, 150);
        HPanel panel = new HPanel();
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(toolBar.getComp());
        lastPanel.set(selectBox.getComp());
        panel.setLastPanel(lastPanel);
        dialog.setRootPanel(panel);
        dialog.setWindowTitle("模式测试");
        dialog.show();
    }

    private void loadSchema(Connection conn, JdbcBean jdbcBean) {
        selectBox.removeAllItems();
        AbsTreeMr.genTreeMr(jdbcBean).ifPresent(treeMr -> {
            TreeMrNode treeMrNode = new TreeMrNode("模式", TreeMrType.DATA_MODEL_SCHEMA_GROUP, "localhost.png");
            treeMr.getChildNode(treeMrNode, conn).forEach(item -> selectBox.addOption(item.getName(), item.getName()));
        });
    }
}
