package com.hh.hhdb_admin.mgr.function.run;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.HHdbPgsqlPrefixEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.sun.DefaultTableCellRenderer;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.*;

public abstract class Run {

    protected Connection conn;
    protected String schema;
    protected String name;
    protected Map<String, List<Map<String, String>>> argueMap;
    protected Map<String, String> retMap;
    protected List<Map<String, String>> paramLists;
    protected String retType = "";
    protected String retValue = "";
    protected HDialog dialog;
    protected HTable table;

    protected Run(Connection conn, String schema, String name) {
        this.conn = conn;
        this.schema = schema;
        this.name = name;
        this.argueMap = new HashMap<>();
        this.retMap = new HashMap<>();
        this.paramLists = new ArrayList<>();
        this.dialog = new HDialog(StartUtil.parentFrame, 800, 600);
        HPanel panel = new HPanel();
        table = new HTable() {
            @Override
            protected void onDClick(HTabRowBean rowBean) {
                dialog.dispose();
                String name = rowBean.getOldRow().get("name");
                paramLists = argueMap.get(name);
                retType = retMap.get(name);
                next(table.getSelectedRowBeans());
            }
        };
        table.hideSeqCol();
        table.setDClick(true);
        table.setCellEditable(false);
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(BorderFactory.createLineBorder(HHSwingUi.tabEvenColor));
        table.getComp().getTableHeader().setDefaultRenderer(renderer);
        DataCol nameCol = new DataCol("name", "name");
        nameCol.setShow(false);
        table.addCols(nameCol, new DataCol("value", ""));
        LastPanel lastPanel = new LastPanel();
        lastPanel.setWithScroll(table.getComp());
        panel.setLastPanel(lastPanel);
        ((JDialog) dialog.getWindow()).setResizable(true);
        dialog.setWindowTitle("Select Overloading");
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setRootPanel(panel);
        dialog.getWindow().setAlwaysOnTop(dialog.getWindow().isAlwaysOnTopSupported());
    }

    public List<Map<String, String>> getParamLists() {
        return paramLists;
    }

    public String getRetType() {
        return retType;
    }

    public String getRetValue() {
        return retValue;
    }

    public abstract boolean isOverWrite() throws Exception;

    public abstract void handParams() throws Exception;

    public void show() {
        dialog.show();
    }
    
    /**
     * 选择
     * @param lisi  当前选择行
     */
    public void next(List<HTabRowBean> lisi) {

    }

    protected static HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    private static SelectBox dbTypeBox;

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        HFrame frame = new HFrame(HFrame.MIDDLE_WIDTH);
        HPanel rootPanel = new HPanel();
        TextInput packInput = new TextInput("package");
        SelectBox funcOrProcBox = new SelectBox("funcOrProcBox");
        funcOrProcBox.addOption(TreeMrType.FUNCTION.name(), TreeMrType.FUNCTION.name());
        funcOrProcBox.addOption(TreeMrType.PROCEDURE.name(), TreeMrType.PROCEDURE.name());
        TextInput nameInput = new TextInput("nameInput");
        HBarPanel barPanel = new HBarPanel();
        barPanel.add(new HButton("运行") {
            @Override
            protected void onClick() {
                runClick(packInput.getValue(), nameInput.getValue(), funcOrProcBox.getValue());
            }
        });
        LastPanel lastPanel = new LastPanel();
        dbTypeBox = new SelectBox("database") {
            @Override
            protected void onItemChange(ItemEvent e) {
                HPanel panel = new HPanel();
                switch (DBTypeEnum.valueOf(this.getValue())) {
                    case hhdb:
                    case pgsql:
                        panel.add(getWithLabelInput("函数/过程名：", nameInput));
                        panel.add(barPanel);
                        break;
                    case dm:
                    case oracle:
                        panel.add(getWithLabelInput("包名：", packInput));
                        panel.add(getWithLabelInput("函数/过程名：", nameInput));
                        panel.add(barPanel);
                        break;
                    case mysql:
                    case sqlserver:
                    case db2:
                        panel.add(getWithLabelInput("类型：", funcOrProcBox));
                        panel.add(getWithLabelInput("名称：", nameInput));
                        panel.add(barPanel);
                        break;
                    default:
                        panel.add(new LabelInput("暂不支持: " + DBTypeEnum.valueOf(this.getValue()), AlignEnum.CENTER));
                }
                lastPanel.set(panel.getComp());
                rootPanel.setLastPanel(lastPanel);
            }
        };
        Arrays.stream(DBTypeEnum.values()).forEach(dbTypeEnum -> dbTypeBox.addOption(dbTypeEnum.name(), dbTypeEnum.name()));
        rootPanel.add(getWithLabelInput("数据库：", dbTypeBox));
        HPanel panel = new HPanel();
        panel.add(getWithLabelInput("包名：", packInput));
        panel.add(getWithLabelInput("函数/过程名：", nameInput));
        panel.add(barPanel);
        lastPanel.set(panel.getComp());
        rootPanel.setLastPanel(lastPanel);
        frame.setRootPanel(rootPanel);
        frame.show();
    }

    private static void runClick(String packName, String name, String type) {
        try {
            Run run;
            Connection conn;
            switch (DBTypeEnum.valueOf(dbTypeBox.getValue())) {
                case hhdb:
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean(DBTypeEnum.hhdb));
                    run = new RunHhPg(conn, conn.getSchema(), name, HHdbPgsqlPrefixEnum.hh) {
                        @Override
                        public void next(List<HTabRowBean> lisi) {
                            PopPaneUtil.info("paramLists:" + this.getParamLists().toString() +
                                    "\nretType:" + this.getRetType() + "\nretValue:" + this.getRetValue());
                        }
                    };
                    break;
                case pgsql:
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean(DBTypeEnum.pgsql));
                    run = new RunHhPg(conn, conn.getSchema(), name, HHdbPgsqlPrefixEnum.pg) {
                        @Override
                        public void next(List<HTabRowBean> lisi) {
                            PopPaneUtil.info("paramLists:" + this.getParamLists().toString() +
                                    "\nretType:" + this.getRetType() + "\nretValue:" + this.getRetValue());
                        }
                    };
                    break;
                case oracle:
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean(DBTypeEnum.oracle));
                    run = new RunOracle(conn, conn.getSchema(), packName, name) {
                        @Override
                        public void next(List<HTabRowBean> lisi) {
                            PopPaneUtil.info("paramLists:" + this.getParamLists().toString() +
                                    "\nretType:" + this.getRetType() + "\nretValue:" + this.getRetValue());
                        }
                    };
                    break;
                case mysql:
                    JdbcBean jdbcBean = MgrTestUtil.getJdbcBean(DBTypeEnum.mysql);
                    conn = ConnUtil.getConn(jdbcBean);
                    assert jdbcBean != null;
                    run = new RunMysql(conn, jdbcBean.getSchema(), name, type);
                    break;
                case sqlserver:
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean(DBTypeEnum.sqlserver));
                    run = new RunSqlServer(conn, conn.getSchema(), name, type);
                    break;
                case db2:
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean());
                    run = new RunDb2(conn, "TEST", name, type) {
                        @Override
                        public void next(List<HTabRowBean> lisi) {
                            PopPaneUtil.info("paramLists:" + this.getParamLists().toString() +
                                    "\nretType:" + this.getRetType() + "\nretValue:" + this.getRetValue());
                        }
                    };
                    break;
                case dm:
                    conn = ConnUtil.getConn(MgrTestUtil.getJdbcBean(DBTypeEnum.dm));
                    run = new RunDm(conn, conn.getSchema(), packName, name) {
                        @Override
                        public void next(List<HTabRowBean> lisi) {
                            PopPaneUtil.info("paramLists:" + this.getParamLists().toString() +
                                    "\nretType:" + this.getRetType() + "\nretValue:" + this.getRetValue());
                        }
                    };
                    break;
                default:
                    PopPaneUtil.info("暂不支持: " + DBTypeEnum.valueOf(dbTypeBox.getValue()));
                    throw new IllegalStateException("Unexpected value: " + DBTypeEnum.valueOf(dbTypeBox.getValue()));
            }
            if (run.isOverWrite()) {
                run.show();
            } else {
                run.handParams();
                PopPaneUtil.info("paramLists: " + run.getParamLists() +
                        "\nretType: " + run.getRetType() + "\nretValue: " + run.getRetValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(e);
        }
    }

}
