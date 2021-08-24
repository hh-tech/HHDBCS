package com.hh.hhdb_admin.test.main_frame;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.TopImageBtn;
import com.hh.frame.swingui.view.hmenu.HMenu;
import com.hh.frame.swingui.view.hmenu.HMenuBar;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tree.HTree;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameComp;

import java.io.File;

/**
 * @author Jiang
 * @date 2020/12/22
 */

public class MainFrameCompTest {

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        MainFrameComp mainFrameComp = new MainFrameComp();
        mainFrameComp.setWindowTitle("主面板测试");
        IconFileUtil.setIconBaseDir(new File("etc/icon/"));

        HMenuBar menuBar = new HMenuBar();

        HMenu menu=new HMenu("工具栏操作");
        HMenuItem showItem = new HMenuItem("显示工具栏") {
            @Override
            protected void onAction() {
                mainFrameComp.setToolbarVisible(true);
            }
        };
        HMenuItem hideItem = new HMenuItem("隐藏工具栏") {
            @Override
            protected void onAction() {
                mainFrameComp.setToolbarVisible(false);
            }
        };
        menu.addItem(showItem, hideItem);
        menuBar.add(menu);
        mainFrameComp.setMenubar(menuBar);
        HBarLayout layout = new HBarLayout();
        layout.setAlign(AlignEnum.LEFT);
        HBarPanel toolBar = new HBarPanel(layout);
        toolBar.add(new TopImageBtn("隐藏菜单", IconFileUtil.getIcon(new IconBean("menubar", "hide.png"))) {
            @Override
            protected void onClick() {
                mainFrameComp.setMenubarVisible(false);
            }
        });
        toolBar.add(new TopImageBtn("显示菜单", IconFileUtil.getIcon(new IconBean("menubar", "view.png"))) {
            @Override
            protected void onClick() {
                mainFrameComp.setMenubarVisible(true);
            }
        });
        toolBar.add(new TopImageBtn("隐藏树", IconFileUtil.getIcon(new IconBean("menubar", "hide.png"))) {
            @Override
            protected void onClick() {
                mainFrameComp.setTreeVisible(false);
            }
        });
        toolBar.add(new TopImageBtn("显示树", IconFileUtil.getIcon(new IconBean("menubar", "view.png"))) {
            @Override
            protected void onClick() {
                mainFrameComp.setTreeVisible(true);
            }
        });
        mainFrameComp.setToolBar(toolBar);

        HTreeNode treeNode = new HTreeNode();
        treeNode.setName("root");
        HTree tree = new HTree(treeNode) {
            @Override
            protected void leftClickTreeNode(HTreeNode treeNode) {
                if ("root".equals(treeNode.getName())) {
                    HTreeNode childNode1 = new HTreeNode();
                    childNode1.setName("childNode1");
                    HTreeNode childNode2 = new HTreeNode();
                    childNode2.setName("childNode2");
                    treeNode.add(childNode1);
                    treeNode.add(childNode2);
                }
            }
        };
        HPanel panel = new HPanel();
        LastPanel leftPanel = new LastPanel(false);
        leftPanel.set(tree.getComp());
        panel.setLastPanel(leftPanel);
        HSplitTabPanel tabPane = new HSplitTabPanel(mainFrameComp);
        tabPane.addPanel("1", "测试tab", new HPanel());

        mainFrameComp.setRootPanel(panel, tabPane);
        mainFrameComp.show();
        mainFrameComp.maximize();
    }

}
