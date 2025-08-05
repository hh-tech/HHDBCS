package com.hh.hhdb_admin.test.attribute;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.attribute.AttributeBean;
import com.hh.hhdb_admin.mgr.attribute.AttributeComp;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

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
    private AttributeComp attributeComp;
    private TreeMrNode curNode;
    private String schemaName = "";
    private String tableName = "";
    private SelectBox selectBox;
    private final Map<String, TreeMrNode> curNodeMap = new HashMap<>();

    private void init() throws Exception {
        LafMgr.setLaf(LafIdEnum.SPRING);
        MgrTestUtil.initCsVarJdbc();
        attributeComp = new AttributeComp();

        LastPanel rootPanel = new LastPanel();
        selectBox = new SelectBox();
        rootNode = new TreeMrNode(TreeMrType.ROOT.name(), TreeMrType.ROOT, "localhost.png");
        adminRootNode = new TreeMrNode(TreeMrType.DBA_ROOT.name(), TreeMrType.DBA_ROOT, "localhost.png");
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
                try {
                    AttributeBean bean = new AttributeBean();
                    bean.setNodeType(curNode.getType());
                    bean.setName(StringUtils.isBlank(selectBox.getValue()) ? "" : selectBox.getValue());
                    bean.setOid(StringUtils.isBlank(curNode.getId()) ? "" : curNode.getId());
                    bean.setSchema(schemaName);
                    bean.setTableName(tableName);
                    attributeComp.showAttr(bean, CsVarUtil.jdbc, CsVarUtil.conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

  

        HPanel panel = new HPanel(new HDivLayout(GridSplitEnum.C6, GridSplitEnum.C2, GridSplitEnum.C2));
        panel.add(selectBox);
        panel.add(preBtn);
        panel.add(nextBtn);
        panel.add(okBtn);

        rootPanel.set(panel.getComp());
        HDialog dialog = CsVarUtil.getMainDialog();
        dialog.setSize(800, 100);
        dialog.setWindowTitle("属性插件测试");
        HPanel tempPanel = new HPanel();
        tempPanel.setLastPanel(rootPanel);
        dialog.setRootPanel(tempPanel);
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }

    private void loadChild() throws Exception {
    	
    	AbsTreeMr treeMr=AbsTreeMr.genTreeMgr(CsVarUtil.jdbc, LangMgr.langType);
    	curNodeMap.clear();
        selectBox.removeAllItems();
        curNode.setTableName(tableName);
        curNode.setSchemaName(schemaName);
        treeMr.getChildNodeList(curNode,  CsVarUtil.conn).forEach(item -> {
            selectBox.addOption(item.getName(), item.getName());
            curNodeMap.put(item.getName(), item);
        });
        curNode = curNodeMap.get(selectBox.getValue());
    }

    private void updateInfo() {
        DBTypeEnum dbTypeEnum=CsVarUtil.getDbtype();
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
                if (type == TreeMrType.ROOT_SCHEMA_OR_DB) {
                    schemaName = curNode.getName();
                }
                break;
        }
        if (curNode.getType() == TreeMrType.ROOT_SCHEMA_OR_DB && dbTypeEnum == DBTypeEnum.oracle) {
            schemaName = curNode.getName();
        }
        if (curNode.getType() == TreeMrType.TABLE) {
            tableName = curNode.getName();
        }
    }
}
