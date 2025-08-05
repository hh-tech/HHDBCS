package com.hh.hhdb_admin.mgr.pack;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.dbobj3.dm.DmSchemaSubObj;
import com.hh.frame.dbobj3.ora.OraSchemaSubObj;
import com.hh.frame.dbobj3.ora.PackParserTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.tree2.HTree2;
import com.hh.frame.swingui.view.tree2.LeafBean;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.attribute.AttributeBean;
import com.hh.hhdb_admin.mgr.attribute.AttributeComp;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.CsTree;
import com.hh.hhdb_admin.mgr.tree.CsTreeUtil;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DesignComp extends CommonComp {
    private final HPanel panel;
    private final Connection conn;
    private final String schema;
    private final String packName;
    private final AllDbObjType sessionEnum;
    private final DBTypeEnum.Version ver = CsVarUtil.jdbc.getCurrentVersion();
    private HTree2 tree;
    private CsFolderBean rootNode;
    private HButton execBtn;
    private HTextArea textArea;
    private PackParserTool parserTool;
    private String oldText;
    private final TextAreaInput consoleArea;
    private final HSplitPanel portraitPanel;
    private JdbcBean jdbc;
    private PackageBean csGuiBean;
//    private int breakWidth;     //格式化宽度

    protected DesignComp(JdbcBean jdbc, Connection conn, String packName, AllDbObjType sessionEnum, boolean showBtn, PackageBean csGuiBean) throws Exception {
        this.conn = conn;
        this.schema = csGuiBean.getSchema();
        this.packName = packName;
        this.sessionEnum = sessionEnum;
        this.jdbc=jdbc;
        this.csGuiBean = csGuiBean;
//        breakWidth=StartUtil.CONF.getSqlFormatWith();
        HSplitPanel horizontalPanel = new HSplitPanel();
        horizontalPanel.setSplitWeight(0.2);
        horizontalPanel.setLastComp4One(getTreePanel());
        horizontalPanel.setLastComp4Two(getTextPanel());

        consoleArea = new TextAreaInput();
        consoleArea.setEnabled(false);
        LastPanel consoleLast = new LastPanel();
        consoleLast.set(consoleArea.getComp());
        portraitPanel = new HSplitPanel(false);
        portraitPanel.getComp().setResizeWeight(1);
        portraitPanel.setPanelOne(horizontalPanel);
        portraitPanel.setLastComp4Two(consoleLast);
        LastPanel lastPanel = new LastPanel();
        if (showBtn) {
            lastPanel.setHead(getBarPanel().getComp());
        }
        lastPanel.set(portraitPanel.getComp());
        panel = new HPanel();
        panel.setLastPanel(lastPanel);
        refreshTree();
    }

    protected HPanel getPanel() throws Exception {
        return panel;
    }

    protected String getText() {
        return textArea.getText();
    }

    protected void setText(String text) {
        textArea.setText(text);
        refreshTree();
    }

    protected boolean execute() {
        boolean result = true;
        DBTypeEnum dbTypeEnum = null;
        try {
            portraitPanel.setDividerLocation(600);
            String sql = textArea.getText();
            oldText = sql;
            SqlExeUtil.executeUpdate(conn, sql);
            dbTypeEnum = DriverUtil.getDbType(jdbc);
            String errorMsg;
            if (DBTypeEnum.oracle == dbTypeEnum) {
                errorMsg = getOracleErrorMsg(sessionEnum);
            } else {
                errorMsg = getDmErrorMsg();
            }
            if (StringUtils.isEmpty(errorMsg)) {
                consoleArea.setValue(LangMgr.sLang(CsL.SAVE_COMPILED_SUCCESS));
                refreshTree();
                if (execBtn != null) {
                    execBtn.setEnabled(false);
                }
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
            } else {
                consoleArea.setValue(LangMgr.sLang(CsL.COMPILE_FAILED) + "\n" + errorMsg);
                result = false;
            }
        } catch (Exception e) {
            Lg.error(DesignComp.class, e);
            if (DBTypeEnum.oracle == dbTypeEnum) {
                PopDlgUtil.error(CsVarUtil.parentFrame, e);
            } else {
                consoleArea.setValue(LangMgr.sLang(CsL.SAVE_COMPILED_FAILED) + "\n" + e.getMessage());
            }
            result = false;
        }
        return result;
    }

    protected void format() {
        GlobalWorkUtil.formatSql(textArea);
        refreshTree();
    }

    private HBarPanel getBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        execBtn = new HButton(LangMgr.lang(Word2.EXECUTE)) {
            @Override
            protected void onClick() {
                execute();
            }
        };
        execBtn.setEnabled(false);
        execBtn.setIcon(FaSolidEnum.PLAY,16);
        HButton formatBtn = new HButton(LangMgr.lang(Word3.FORMATTING)) {
            @Override
            protected void onClick() {
                format();
            }
        };
        formatBtn.setIcon(FaSolidEnum.WAND_MAGIC_SPARKLES,16);
        HButton saveBtn = new HButton(LangMgr.lang(Word2.SAVE)) {
            @Override
            protected void onClick() {
                GlobalWorkUtil.saveText(textArea.getText());
            }
        };
        saveBtn.setIcon(FaSolidEnum.FLOPPY_DISK,16);
        barPanel.add(execBtn, formatBtn, saveBtn);
        return barPanel;
    }

    private LastPanel getTreePanel() {
    	String name="Declaration";
        rootNode = new CsFolderBean(name);
        rootNode.setMrNode(new TreeMrNode(name,TreeMrType.ROOT));

//        rootNode.setType(TreeMrType.ROOT);
        rootNode.setFontType(FaSolidEnum.CUBE);
        tree = new HTree2(rootNode,false) {
            @Override
            protected void oneClickNode(LeafBean bean) {
                CsFolderBean treeNode = (CsFolderBean) bean;
                if (TreeMrType.ROOT != treeNode.getType()) {
                    int startLine;
                    int endLine;
                    int index = Integer.parseInt(treeNode.getId());
                    if (TreeMrType.TYPE == treeNode.getType()) {
                        startLine = parserTool.getTypePosList().get(index).getBeginLine();
                        endLine = parserTool.getTypePosList().get(index).getEndLine();
                    } else if (TreeMrType.FUNCTION == treeNode.getType()) {
                        startLine = parserTool.getFunPosList().get(index).getBeginLine();
                        endLine = parserTool.getFunPosList().get(index).getEndLine();
                    } else {
                        startLine = parserTool.getProcPosList().get(index).getBeginLine();
                        endLine = parserTool.getProcPosList().get(index).getEndLine();
                    }
                    searchContext(textArea, treeNode);
                    heightLight(textArea, startLine, endLine);
                }
            }
    
    
            @Override
            protected void rightClickNode(List<LeafBean> treeNodes, MouseEvent e) {
                if (treeNodes.size() == 0) return;
                try {
                    if (treeNodes.size() == 1) {
                        CsFolderBean treeNode = (CsFolderBean) treeNodes.get(0);
                        if (TreeMrType.ROOT == treeNode.getType()) {
                            HPopMenu hp = new HPopMenu();
                            HMenuItem refreshMenu = new HMenuItem(LangMgr.lang(Word2.REFRESH)) {
                                @Override
                                protected void onAction() {
                                    refreshTree();
                                }
                            };
                            refreshMenu.setFontIcon(FaSolidEnum.ROTATE);
                            hp.addItem(refreshMenu);
                            hp.showPopup(e);
                        }
                    }
                } catch (Exception exception) {
                    Lg.error(CsTree.class,exception);
                    PopDlgUtil.error(CsVarUtil.parentFrame,exception);
                }
            }
        };
        LastPanel lastPanel = new LastPanel();
        lastPanel.setWithScroll(tree.getComp());
        return lastPanel;
    }

    private LastPanel getTextPanel() throws Exception {
        textArea = new HTextArea();
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        textArea.setConf(conf);
        textArea.getRSyntaxTextArea().addCaretListener(e -> {
            if (execBtn != null && oldText != null) {
                execBtn.setEnabled(!oldText.equals(textArea.getText()));
            }
        });
        HMenuItem attrMenuItem = new HMenuItem(LangMgr.lang(Word2.ATTRIBUTE)) {
            @Override
            protected void onAction() {
                try {
                    List<Map<String, String>> maps = queryObject();
                    if (maps.size() > 0) {
                        Map<String, String> map = maps.get(0);
                        AttributeBean bean = new AttributeBean();
                        bean.setNodeType(TreeMrType.valueOf(map.get("object_type")));
                        bean.setName(map.get("object_name"));
                        bean.setSchema(map.get("owner"));
                        bean.setTableName(map.get("object_name"));
                        JdbcBean jdbcBean = CsVarUtil.jdbcCloneWithSsh();
                        jdbcBean.setSchema(map.get("owner"));
                        new AttributeComp().showAttr(bean, jdbcBean, conn);
                    }
                } catch (Exception e) {
                    Lg.error(DesignComp.class, e);
                }
            }
        };
        textArea.getRSyntaxTextArea().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
//                if (e.getModifiers() == InputEvent.META_MASK) {
            	 if (e.getModifiersEx() == InputEvent.META_DOWN_MASK) {
                    String selectText = textArea.getSelectedText();
                    if (selectText != null) {
                        if (queryObject().size() > 0) {
                            textArea.getRSyntaxTextArea().getPopupMenu().add(attrMenuItem.getComp());
                        } else {
                            textArea.getRSyntaxTextArea().getPopupMenu().remove(attrMenuItem.getComp());
                        }
                    }
                }
            }
        });
        textArea.getRSyntaxTextArea().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textArea.getRSyntaxTextArea().getHighlighter().removeAllHighlights();
            }
        });
        textArea.setText(queryPackageText());
        oldText = textArea.getText();
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(textArea.getComp());
        return lastPanel;
    }

    private void refreshTree() {
        tree.reload(rootNode.getId());
        getTreeNodes();
        tree.expandFolder(rootNode.getId());
    }

    private void getTreeNodes() {
        List<CsFolderBean> treeNodes = new ArrayList<>();
        if (StringUtils.isNotBlank(textArea.getText())) {
            parserTool = new PackParserTool(textArea.getText());
            treeNodes.addAll(queryTreeNode(parserTool.getTypeNames(), TreeMrType.TYPE));
            treeNodes.addAll(queryTreeNode(parserTool.getFunNames(), TreeMrType.FUNCTION));
            treeNodes.addAll(queryTreeNode(parserTool.getProcNames(), TreeMrType.PROCEDURE));
        }
        for (CsFolderBean treeNode : treeNodes) {
            tree.add(rootNode.getId(),treeNode);
        }
    }

    private List<CsFolderBean> queryTreeNode(List<String> names, TreeMrType type) {
        int i = 0;
        List<CsFolderBean> treeNodes = new ArrayList<>();
        for (String name : names) {
            CsFolderBean node = new CsFolderBean(name);
            node.setMrNode(new TreeMrNode(name,type));
//            node.setType(type);
            node.setId(i + "");
            node.setFontType(CsTreeUtil.getFontType(new TreeMrNode(name,type,"")));
            treeNodes.add(node);
            i++;
        }
        return treeNodes;
    }

    private void searchContext(HTextArea textArea, CsFolderBean treeNode) {
        SearchContext context = new SearchContext();
        context.setSearchFor(treeNode.getType() + " " + treeNode.getName());
        context.setMatchCase(false);
        context.setWholeWord(true);
        context.setSearchForward(true);
        context.setMarkAll(false);
        boolean find = SearchEngine.find(textArea.getRSyntaxTextArea(), context).wasFound();
        if (!find) {
            context.setSearchFor(treeNode.getType() + "  " + treeNode.getName());
            SearchEngine.find(textArea.getRSyntaxTextArea(), context).wasFound();
        }
    }

    private void heightLight(HTextArea textArea, int startLine, int endLine) {
        try {
            textArea.getRSyntaxTextArea().getHighlighter().removeAllHighlights();
            int startIndex = textArea.getRSyntaxTextArea().getLineStartOffset(startLine - 1);
            int endIndex = textArea.getRSyntaxTextArea().getLineEndOffset(endLine - 1);
            textArea.getRSyntaxTextArea().setCaretPosition(startIndex);
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
            textArea.getRSyntaxTextArea().getHighlighter().addHighlight(startIndex, endIndex, painter);
        } catch (BadLocationException e) {
            Lg.error(DesignComp.class, e);
        }
    }

    private List<Map<String, String>> queryObject() {
        String objName = textArea.getSelectedText().trim();
        String currSchema = schema;
        if (objName.contains(".")) {
            currSchema = objName.split("\\.")[0];
            objName = objName.split("\\.")[1];
        }
        if (!StringUtils.startsWith(currSchema, "\"") || !StringUtils.endsWith(currSchema, "\"")) {
            currSchema = currSchema.toUpperCase();
        } else {
            currSchema = currSchema.replace("\"", "");
        }
        if (!StringUtils.startsWith(objName, "\"") || !StringUtils.endsWith(objName, "\"")) {
            objName = objName.toUpperCase();
        } else {
            objName = objName.replace("\"", "");
        }
        String sql = "select owner, object_type, object_name\n" +
                "from all_objects\n" +
                "where object_type in ('TABLE', 'VIEW', 'TYPE')\n" +
                "AND status = 'VALID'\n" +
                "AND owner = '%s'\n" +
                "AND object_name = '%s'\n";
        try {
            return SqlQueryUtil.selectStrMapList(conn, String.format(sql, currSchema, objName));
        } catch (SQLException e) {
            Lg.error(DesignComp.class, e);
        }
        return new ArrayList<>();
    }

    private String getOracleErrorMsg(AllDbObjType oraSessionEnum) throws Exception {
        String pType = oraSessionEnum.name().replace("_", " ");
        String packageName = parserTool.getName();
        if (packageName.contains(".")) {
            packageName = packageName.split("\\.")[1];
        }
        String sql = "select line, type, text from SYS.USER_ERRORS where type = '%s' and name = '%s' order by type, line";
//        List<Map<String, Object>> maps = SqlQueryUtil.select(conn, String.format(sql, pType, LoginUtil.getRealName(packageName, DBTypeEnum.oracle.name())));
        List<Map<String, Object>> maps = SqlQueryUtil.select(conn, String.format(sql, pType, SqlStrUtil.rmQuote4Sel(packageName, DBTypeEnum.oracle, ver)));

        StringBuilder errorText = new StringBuilder();
        for (Map<String, Object> map : maps) {
            String line = "0".equals((map.get("line") + "")) ? "1" : (map.get("line") + "");
            errorText.append(map.get("type")).append("  ").append(line).append("  ")
                    .append((map.get("text") + "").replace("\n", "")).append("\n");
        }
        return errorText.toString();
    }

    private String getDmErrorMsg() {
        String sql = "ALTER PACKAGE %s.%s COMPILE";
        String packageName = parserTool.getName();
        if (packageName.contains(".")) {
            packageName = packageName.split("\\.")[1];
            if (!packageName.startsWith("\"") && !packageName.endsWith("\"")) {
                packageName = packageName.toUpperCase();
            }
        }
        try {
            SqlExeUtil.executeUpdate(conn, String.format(sql, schema, packageName));
            return "";
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    private String queryPackageText() {
        String text = "";
        try {
            DBTypeEnum dbTypeEnum = DriverUtil.getDbType(jdbc);
            if (DBTypeEnum.oracle == dbTypeEnum) {
                text = new OraSchemaSubObj(ver, conn, schema, packName).getCreateSql(sessionEnum);
            } else {
                text = new DmSchemaSubObj(ver, conn, schema, packName).getCreateSql(AllDbObjType.PACK);
            }
            if ("create or replace ".equals(text) || StringUtils.isEmpty(text)) {
                if (AllDbObjType.PACK == sessionEnum) {
                    text = String.format("create or replace package %s.%s is \n\nend;", schema, packName);
                } else {
                    text = String.format("create or replace package body %s.%s is \n\nend;", schema, packName);
                }
            }
        } catch (Exception e) {
            Lg.error(DesignComp.class, e);
        }
        return text;
    }

}
