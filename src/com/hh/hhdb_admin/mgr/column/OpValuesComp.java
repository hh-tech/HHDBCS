package com.hh.hhdb_admin.mgr.column;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.icon.IconFileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 可选值添加修改删除的组件 如mysql的 set和enum类型，可以设置值
 */
public class OpValuesComp {
    private final HDialog dialog;
    private final HTable table;

    public OpValuesComp(HDialog d) {
        this.dialog = new HDialog(d, 400, 500);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(ColumnComp.getLang("setValue"));
        LastPanel lastPanel = new LastPanel(false);
        table = new HTable();
        table.setRowHeight(27);
        DataCol col = new DataCol("id", "id");
        col.setShow(false);
        table.addCols(col, new DataCol("value", "value"));
        lastPanel.setWithScroll(table.getComp());
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HImgButton addBtn = new HImgButton() {
            @Override
            protected void onClick() {
                genLine("");
            }
        };
        addBtn.setMouseExitedIconIcon(ColumnComp.getIcon("add"));
        addBtn.setMouseEnteredIcon(ColumnComp.getIcon("add"));
        HImgButton delBtn = new HImgButton() {
            @Override
            protected void onClick() {
                List<HTabRowBean> selections = table.getSelectedRowBeans();
                for (HTabRowBean bean : selections) {
                    int rowIndex = 0;
                    if (bean.getCurrRowNum() >= 1) {
                        rowIndex = bean.getCurrRowNum() - 1;
                    }
                    table.deleteSelectRow();
                    if (table.getComp().getRowCount() > 0) {
                        table.getComp().setRowSelectionInterval(rowIndex, rowIndex);
                    }
                }
            }
        };
        delBtn.setMouseExitedIconIcon(ColumnComp.getIcon("delete"));
        delBtn.setMouseEnteredIcon(ColumnComp.getIcon("delete"));
        HButton button = new HButton(ColumnComp.getLang("BTN_QD")) {
            @Override
            protected void onClick() {
                clickQrBtn();

            }
        };
        HButton qxBtn = new HButton(ColumnComp.getLang("BTN_QX")) {
            @Override
            protected void onClick() {
                clickQxBtn();
            }
        };
        barPanel.add(addBtn);
        barPanel.add(delBtn);
        barPanel.add(button);
        barPanel.add(qxBtn);
        lastPanel.setFoot(barPanel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        dialog.setRootPanel(rootPanel);
    }

    /**
     * 加载表格数据
     *
     * @param data 表格数据List
     */
    public void loadData(List<String> data) {
        table.load(new ArrayList<>(), 1);
        for (String dataStr : data) {
            genLine(dataStr);
        }
    }

    /**
     * 获取表格数据
     *
     * @return 表格数据
     */
    public List<String> getValues() {
        List<String> list = new ArrayList<>();
        int rows = table.getComp().getModel().getRowCount();
        for (int i = 0; i < rows; i++) {
            list.add(table.getComp().getValueAt(i, 1).toString());
        }
        return list;
    }

    /**
     * 隐藏
     */
    public void hide() {
        dialog.hide();
    }

    /**
     * 显示
     */
    public void show() {
        dialog.show();
    }

    /**
     * dispose
     */
    public void dispose() {
        dialog.dispose();
    }

    /**
     * 点击确认按钮
     */
    protected void clickQrBtn() {
        updateEditStatus();
        dialog.hide();
    }

    /**
     * 点击取消按钮
     */
    protected void clickQxBtn() {
        updateEditStatus();
        dialog.hide();
    }

    /**
     * 修改编辑状态
     */
    private void updateEditStatus() {
        //当在编辑某单元格时，点击保存则直接终止编辑获取当前编辑格已输入的值进行保存
        if (table.getComp().isEditing()) {
            table.getComp().getCellEditor().stopCellEditing();
        }
    }

    /**
     * 生成一列
     */
    private void genLine(String value) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id", UUID.randomUUID().toString());
        map.put("value", value);
        table.add(map);
        int rowCount = table.getComp().getRowCount();
        if (table.getComp().getRowCount() > 0) {
            table.getComp().setRowSelectionInterval(rowCount - 1, rowCount - 1);
        }
    }

}
