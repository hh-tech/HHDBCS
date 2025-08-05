package com.hh.hhdb_admin.mgr.obj_query;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.treeMr.base.EventType;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.menu.body.AbsTabBodyPopMenu;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Jiang
 * @Date: 2021/7/20 10:09
 */
public class ObjTabPopMenu extends AbsTabBodyPopMenu {

    private final JdbcBean jdbcBean;

    public ObjTabPopMenu(JdbcBean jdbcBean) {
        this.jdbcBean = jdbcBean;
    }

    @Override
    public void init(HTable tab) {
        this.tab = tab;
    }

    @Override
    public MouseAdapter getMouseAdapter() {
        return new PopMouseAdapter();
    }

    public void onItemClick(EventType value, Map<String, String> oldRow) {

    }

    protected class PopMouseAdapter extends MouseAdapter {
        protected boolean isColRightClick = false;

        @Override
        public void mouseReleased(MouseEvent e) {
            JTable jTab = tab.getComp();
            if (e.isMetaDown()) {
                List<HTabRowBean> rowBeans = tab.getSelectedRowBeans();
    
                if (rowBeans.size() > 1 && DriverUtil.getDbType(jdbcBean) == DBTypeEnum.redis) return;
                
                Set<String> typeSet = new HashSet<>();
                for (HTabRowBean rowBean : rowBeans) {
                    typeSet.add(rowBean.getOldRow().get("type"));
                }
                if (typeSet.size() > 1) {
                    return;
                }
                List<Integer> ctrlList = tab.getHeaderRenderer().getCtrlList();
                int column = jTab.columnAtPoint(e.getPoint());
                if (ctrlList.size() > 0 && ctrlList.contains(column)) {
                    isColRightClick = true;
                    colRightClick();
                } else {
                    isColRightClick = false;
                    int row = jTab.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < jTab.getRowCount()) {
                        List<Integer> list = Arrays.stream(jTab.getSelectedRows()).boxed().collect(Collectors.toList());
                        if (!list.contains(row)) {
                            jTab.setRowSelectionInterval(row, row);
                        }
                        Map<String, String> rowData = tab.getRowBean(row).getOldRow();
                        String type = rowData.get("type");
                        clear();
                        AbsTreeMr absTreeMr=AbsTreeMr.genTreeMgr(jdbcBean, LangMgr.langType);
                        TreeMrNode treeMrNode = new TreeMrNode("name", TreeMrType.valueOf(type.toUpperCase()), "");
                        Map<String, EventType> menuMap = absTreeMr.getRightMenu(treeMrNode);
                        if (menuMap.isEmpty()) {
                            addItem(new HMenuItem(LangMgr.lang(Word2.ATTRIBUTE)));
                        } else {
                            if (rowBeans.size() > 1) {
                                addItem(new HMenuItem(LangMgr.lang(Word2.DELETE)) {
                                    @Override
                                    protected void onAction() {
                                        onItemClick(EventType.DELETE, rowData);
                                    }
                                });
                            } else {
                                for (Map.Entry<String, EventType> menuEntry : menuMap.entrySet()) {
                                    if (menuEntry.getValue() == EventType.SEP) {
                                        addSeparator();
                                    } else {
                                        addItem(new HMenuItem(menuEntry.getKey()) {
                                            @Override
                                            protected void onAction() {
                                                onItemClick(menuEntry.getValue(), rowData);
                                            }
                                        });
                                    }
                                }
                            }
                        }
//                        AbsTreeMr.genTreeMr(jdbcBean).ifPresent(absTreeMr -> {
//                            TreeMrNode treeMrNode = new TreeMrNode("name", TreeMrType.valueOf(type.toUpperCase()), "");
//                            Map<String, EventType> menuMap = absTreeMr.getRightMenu(treeMrNode);
//                            if (menuMap.isEmpty()) {
//                                addItem(new HMenuItem(LangMgr.lang(Word2.ATTRIBUTE)));
//                            } else {
//                                if (rowBeans.size() > 1) {
//                                    addItem(new HMenuItem(LangMgr.lang(Word2.DELETE)) {
//                                        @Override
//                                        protected void onAction() {
//                                            onItemClick(EventType.DELETE, rowData);
//                                        }
//                                    });
//                                } else {
//                                    for (Map.Entry<String, EventType> menuEntry : menuMap.entrySet()) {
//                                        if (menuEntry.getValue() == EventType.SEP) {
//                                            addSeparator();
//                                        } else {
//                                            addItem(new HMenuItem(menuEntry.getKey()) {
//                                                @Override
//                                                protected void onAction() {
//                                                    onItemClick(menuEntry.getValue(), rowData);
//                                                }
//                                            });
//                                        }
//                                    }
//                                }
//                            }
//                        });
                    } else {
                        jTab.clearSelection();
                    }
                }
                if (jTab.getCellEditor() != null) {
                    jTab.getCellEditor().stopCellEditing();
                }
                addItem();
                showPopup(e);
            }
        }

        protected void colRightClick() {

        }
    }
}
