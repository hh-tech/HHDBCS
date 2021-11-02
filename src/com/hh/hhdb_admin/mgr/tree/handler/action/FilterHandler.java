package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;

/**
 * @Author: Jiang
 * @Date: 2021/8/19 18:25
 */
public class FilterHandler extends AbsHandler {

    @Override
    public void resolve(HTreeNode treeNode) throws Exception {
        LoginBean loginBean = StartUtil.getLoginBean();
        TextInput textInput = new TextInput();
        String value = loginBean.getFilterData().get(treeNode.getType());
        if (value != null) {
            textInput.setValue(value);
        }
        HDialog dialog = new HDialog() {
            @Override
            protected void onConfirm() {
                try {
                    loginBean.getFilterData().put(treeNode.getType(), textInput.getValue());
                    tree.getLeftDoubleHandler().refreshNode(treeNode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        dialog.setSize(300, 130);
        dialog.setIconImage(IconFileUtil.getLogo());
        HPanel panel = new HPanel();
        LastPanel lastPanel = new LastPanel();

        dialog.setOption();
        lastPanel.set(textInput.getComp());

        panel.setLastPanel(lastPanel);
        dialog.setRootPanel(panel);
        dialog.setTitle("筛选关键字");
        dialog.show();
    }
}
