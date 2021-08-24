package com.hh.hhdb_admin.test.attribute;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.attribute.AttributeComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Jiang
 * @date: 2020/12/23
 */

public class AttributeCompTest {

    public static void main(String[] args) throws Exception {
        new AttributeCompTest().init();
    }

    private TreeMrNode rootNode;
    private TreeMrNode adminRootNode;
    private Connection conn;
    private JdbcBean jdbcBean;
    private AttributeComp attributeComp;
    private TreeMrNode curNode;
    private String schemaName = "";
    private String tableName = "";
    private SelectBox selectBox;
    private final Map<String, TreeMrNode> curNodeMap = new HashMap<>();

    private void init() throws Exception {
        HHSwingUi.init();
        attributeComp = new AttributeComp();

        LastPanel rootPanel = new LastPanel();
        selectBox = new SelectBox();
        rootNode = new TreeMrNode(TreeMrType.ROOT.name(), TreeMrType.ROOT, "localhost.png");
        adminRootNode = new TreeMrNode(TreeMrType.ADMIN_ROOT.name(), TreeMrType.ADMIN_ROOT, "localhost.png");
        selectBox.addOption(rootNode.getName(), rootNode.getName());
        selectBox.addOption(adminRootNode.getName(), adminRootNode.getName());
        curNodeMap.put(rootNode.getName(), rootNode);
        curNodeMap.put(adminRootNode.getName(), adminRootNode);
        curNode = rootNode;

        selectBox.addListener(e -> {
            String name = selectBox.getValue();
            if (name == null) {
                return;
            }
            curNode = curNodeMap.get(name);
            if (curNode == null) {
                return;
            }
            updateInfo();
        });
        HButton nextBtn = new HButton("重置") {
            @Override
            protected void onClick() {
                curNodeMap.clear();
                selectBox.removeAllItems();
                selectBox.addOption(rootNode.getName(), rootNode.getName());
                selectBox.addOption(adminRootNode.getName(), adminRootNode.getName());
                curNodeMap.put(rootNode.getName(), rootNode);
                curNodeMap.put(adminRootNode.getName(), adminRootNode);
                curNode = rootNode;
                schemaName = "";
                tableName = "";
            }
        };
        HButton preBtn = new HButton("子节点") {
            @Override
            protected void onClick() {
                try {
                    updateInfo();
                    loadChild();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        HButton okBtn = new HButton("查看属性") {
            @Override
            protected void onClick() {
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("name", StringUtils.isBlank(selectBox.getValue()) ? "" : selectBox.getValue());
                jsonObject.add("schemaName", schemaName);
                jsonObject.add("tableName", tableName);
                jsonObject.add("oid", StringUtils.isBlank(curNode.getId()) ? "" : curNode.getId());
                jsonObject.add("databaseName", "");
                jsonObject.add("nodeType", curNode.getType().name());
                try {
                    attributeComp.showAttr(jsonObject, jdbcBean, conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        jdbcBean = MgrTestUtil.getJdbcBean();
        conn = ConnUtil.getConn(jdbcBean);

        HPanel panel = new HPanel(new HDivLayout(GridSplitEnum.C6, GridSplitEnum.C2, GridSplitEnum.C2));
        panel.add(selectBox);
        panel.add(preBtn);
        panel.add(nextBtn);
        panel.add(okBtn);

        rootPanel.set(panel.getComp());
        HDialog dialog = StartUtil.getMainDialog();
        dialog.setSize(800, 100);
        dialog.setWindowTitle("属性插件测试");
        HPanel tempPanel = new HPanel();
        tempPanel.setLastPanel(rootPanel);
        dialog.setRootPanel(tempPanel);
        dialog.show();
    }

    private void loadChild() throws Exception {
        AbsTreeMr.genTreeMr(jdbcBean).ifPresent(treeMr -> {
            curNodeMap.clear();
            selectBox.removeAllItems();
            curNode.setTableName(tableName);
            curNode.setSchemaName(schemaName);
            treeMr.getChildNode(curNode, conn).forEach(item -> {
                selectBox.addOption(item.getName(), item.getName());
                curNodeMap.put(item.getName(), item);
            });
            curNode = curNodeMap.get(selectBox.getValue());
        });
    }

    private void updateInfo() {
        DBTypeEnum dbTypeEnum;
        try {
            dbTypeEnum = DriverUtil.getDbType(conn);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        TreeMrType type = curNode.getType();
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
            case sqlserver:
                if (type == TreeMrType.SCHEMA) {
                    schemaName = curNode.getName();
                }
                break;
            default:
                if (type == TreeMrType.ROOT_DATA_MODEL) {
                    schemaName = curNode.getName();
                }
                break;
        }
        if (curNode.getType().equals(TreeMrType.ROOT_DATA_MODEL) && dbTypeEnum == DBTypeEnum.oracle) {
            schemaName = curNode.getName();
        }
        if (curNode.getType().equals(TreeMrType.TABLE)) {
            tableName = curNode.getName();
        }
    }
}
