package com.hh.hhdb_admin.mgr.pack;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.dbobj2.kword.KeyWordUtil;
import com.hh.frame.dbobj2.ora.OraSessionEnum;
import com.hh.frame.dbobj2.ora.OraSessionObj;
import com.hh.frame.dbobj2.ora.pack.PackParserTool;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rtextarea.SearchContext;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rtextarea.SearchEngine;
import com.hh.frame.swingui.view.tree.HTree;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.mgr.attribute.AttributeComp;
import com.hh.hhdb_admin.mgr.login.LoginComp;
import com.hh.hhdb_admin.mgr.login.LoginUtil;
import com.hh.hhdb_admin.mgr.query.util.QuerUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

/**
 * @author YuSai
 */
public class PackageComp {

    private static final String DOMAIN_NAME = PackageComp.class.getName();

    static {
        LangMgr.merge(DOMAIN_NAME, LangUtil.loadLangRes(PackageComp.class));
    }

    private final Connection conn;
    private final String schema;
    private HSplitPanel rootPanel;
    private TextAreaInput consoleArea;
    private PackParserTool parserTool;
    private HButton saveBtn;
    private QueryEditorTextArea textArea;
    private HTree tree;
    private HTreeNode treeNode;
    private String oldText;

    public PackageComp(Connection conn, String schema) {
        this.conn = conn;
        this.schema = schema;
    }

    protected void add(String name) {
        TextInput nameInput = new TextInput("packageName");
        HDialog dialog = new HDialog(StartUtil.parentFrame, 400, 120);
        dialog.setWindowTitle(getLang("addPackage"));
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setTopHeight(10);
        HPanel panel = new HPanel(divLayout);
        panel.add(getWithLabelInput(getLang("packName"), nameInput));
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton subitBtn = new HButton(getLang("submit")) {
            @Override
            protected void onClick() {
                if (StringUtils.isEmpty(nameInput.getValue())) {
                    PopPaneUtil.info(dialog.getWindow(), getLang("enterPackageName"));
                    return;
                }
                try {
                    String PackName = nameInput.getValue();
                    SqlExeUtil.execute(conn, String.format("create or replace package %s is \n\nend;", PackName));
                    SqlExeUtil.execute(conn, String.format("create or replace package body %s is \n\nend;", PackName));
                } catch (SQLException e) {
                    e.printStackTrace();
                    PopPaneUtil.error(dialog.getWindow(), e.getMessage());
                    return;
                }
                dialog.dispose();
                StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                        .add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.PACKAGE_GROUP.name())
                        .add(StartUtil.PARAM_TABLE, name)
                        .add(StartUtil.PARAM_SCHEMA, schema));
            }
        };
        subitBtn.setIcon(getIcon("submit"));
        HButton cancelBtn = new HButton(getLang("cancel")) {
            @Override
            protected void onClick() {
                dialog.dispose();
            }
        };
        cancelBtn.setIcon(getIcon("cancel"));
        barPanel.add(subitBtn, cancelBtn);
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(panel.getComp());
        lastPanel.setFoot(barPanel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        dialog.setRootPanel(rootPanel);
        dialog.show();
    }

    public void design(String packName, OraSessionEnum oraSessionEnum) throws Exception {
        textArea = QueryEditUtil.getQueryEditor(true);
        textArea.getTextArea().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {}

            @Override
            public void removeUpdate(DocumentEvent e) {}

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (saveBtn != null) {
                    saveBtn.setEnabled(!oldText.equals(textArea.getText()));
                }
            }
        });
        textArea.getTextArea().addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textArea.getTextArea().getHighlighter().removeAllHighlights();
            }
        });
        HMenuItem attrMenuItem = new HMenuItem(getLang("attribute")) {
            @Override
            protected void onAction() {
                try {
                    List<Map<String, String>> maps = queryObject(textArea.getSelectedText().trim());
                    if (maps.size() > 0) {
                        Map<String, String> map = maps.get(0);
                        JsonObject data =  new JsonObject();
                        data.add("tableName", map.get("object_name"));
                        data.add("schemaName", map.get("owner"));
                        data.add("name", map.get("object_name"));
                        data.add("nodeType", map.get("object_type"));
                        JdbcBean jdbcBean = LoginComp.loginBean.getJdbc();
                        jdbcBean.setSchema(map.get("owner"));
                        new AttributeComp().showAttr(data, jdbcBean, conn);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        };
        textArea.getTextArea().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getModifiers() == InputEvent.META_MASK) {
                    String selectText = textArea.getSelectedText();
                    if (selectText != null) {
                        if (queryObject(selectText.trim()).size() > 0) {
                            textArea.getTextArea().getPopupMenu().add(attrMenuItem.getComp());
                        } else {
                            textArea.getTextArea().getPopupMenu().remove(attrMenuItem.getComp());
                        }
                    }
                }
            }
        });
        textArea.setText(queryPackageText(oraSessionEnum, packName));
        oldText = textArea.getText();
        setKeyWord();
        initPanel(packName, oraSessionEnum);
    }

    protected HSplitPanel getPanel() {
        return rootPanel;
    }

    private void initPanel(String packName, OraSessionEnum oraSessionEnum) {
        rootPanel = new HSplitPanel(false);
        rootPanel.getComp().setResizeWeight(1);
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        saveBtn = new HButton(getLang("save")) {
            @Override
            protected void onClick() {
                try {
                    rootPanel.setDividerLocation(600);
                    String sql = textArea.getText();
                    SqlExeUtil.executeUpdate(conn, sql);
                    String errorMsg = getErrorMsg(packName, oraSessionEnum);
                    if (StringUtils.isEmpty(errorMsg)) {
                        consoleArea.setValue(getLang("saveSuccess"));
                        treeNode.removeAllChildren();
                        tree.addHTreeNode(treeNode, getTreeNode(sql), true);
                        oldText = textArea.getText();
                        saveBtn.setEnabled(false);
                    } else {
                        consoleArea.setValue(getLang("compileFailed") + "\n" + errorMsg);
                    }
                    StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH).add(TreeMgr.PARAM_NODE_TYPE, TreeMrType.PACKAGE_GROUP.name()));
                } catch (Exception e) {
                    e.printStackTrace();
                    consoleArea.setValue(getLang("saveFail") + "\n" + e.getMessage());
                }
            }
        };
        saveBtn.setIcon(getIcon("save"));
        saveBtn.setEnabled(false);
        HButton formatBtn = new HButton(getLang("format")) {
            @Override
            protected void onClick() {
                QuerUtil.formatSql(textArea);
                treeNode.removeAllChildren();
                tree.addHTreeNode(treeNode, getTreeNode(textArea.getText()), true);
            }
        };
        formatBtn.setIcon(getIcon("format"));
        barPanel.add(saveBtn, formatBtn);
        LastPanel textAreaLast = new LastPanel();
        textAreaLast.set(textArea.getComp());
        HSplitPanel splitPanel = new HSplitPanel();
        splitPanel.setSplitWeight(0.22);
        splitPanel.setLastComp4One(initPackTree(textArea, packName, oraSessionEnum));
        splitPanel.setLastComp4Two(textAreaLast);
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(barPanel.getComp());
        lastPanel.set(splitPanel.getComp());
        consoleArea = new TextAreaInput();
        consoleArea.setEnabled(false);
        LastPanel consoleLast = new LastPanel();
        consoleLast.set(consoleArea.getComp());
        rootPanel.setLastComp4One(lastPanel);
        rootPanel.setLastComp4Two(consoleLast);
    }

    private String queryPackageText(OraSessionEnum oraSessionEnum, String packName) {
        OraSessionObj oraSessionObj = new OraSessionObj(conn, schema, oraSessionEnum, packName);
        try {
            return oraSessionObj.getCreateSql();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    private LastPanel initPackTree(QueryEditorTextArea textArea, String packName, OraSessionEnum sessionEnum) {
        treeNode = new HTreeNode();
        treeNode.setName(getTitle(packName, sessionEnum));
        treeNode.setType("root");
        treeNode.setOpenIcon(getIcon("pack"));
        for (HTreeNode node : getTreeNode(textArea.getText())) {
            treeNode.add(node);
        }
        tree = new HTree(treeNode) {
            @Override
            protected void selectTreeNode(HTreeNode treeNode) {
                if (!"root".equals(treeNode.getType())) {
                    int startLine;
                    int endLine;
                    int index = Integer.parseInt(treeNode.getId());
                    if ("type".equals(treeNode.getType())) {
                        startLine = parserTool.getTypePosList().get(index).getBeginLine();
                        endLine = parserTool.getTypePosList().get(index).getEndLine();
                    } else if ("function".equals(treeNode.getType())) {
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
            protected void rightClickTreeNode(HTreeNode treeNode, MouseEvent e) {
                if ("root".equals(treeNode.getType())) {
                    HPopMenu hp = new HPopMenu();
                    HMenuItem refreshMenu = new HMenuItem(getLang("refresh")) {
                        @Override
                        protected void onAction() {
                            treeNode.removeAllChildren();
                            tree.addHTreeNode(treeNode, getTreeNode(textArea.getText()), true);
                        }
                    };
                    refreshMenu.setIcon(getIcon("refresh"));
                    hp.addItem(refreshMenu);
                    hp.showPopup(e);
                }
            }
        };
        LastPanel lastPanel = new LastPanel();
        lastPanel.setWithScroll(tree.getComp());
        return lastPanel;
    }

    private String getTitle(String name, OraSessionEnum sessionEnum) {
        return name + (OraSessionEnum.pack.equals(sessionEnum) ? getLang("head") : getLang("body"));
    }

    private List<HTreeNode> getTreeNode(String sql) {
        List<HTreeNode> treeNodes = new ArrayList<>();
        parserTool = new PackParserTool(sql);
        treeNodes.addAll(queryTreeNode(parserTool.getTypeNames(), "type"));
        treeNodes.addAll(queryTreeNode(parserTool.getFunNames(), "function"));
        treeNodes.addAll(queryTreeNode(parserTool.getProcNames(), "procedure"));
        return treeNodes;
    }

    private List<HTreeNode> queryTreeNode(List<String> names, String type) {
        int i = 0;
        List<HTreeNode> treeNodes = new ArrayList<>();
        for (String name : names) {
            HTreeNode node = new HTreeNode();
            node.setName(name);
            node.setType(type);
            node.setId(i + "");
            node.setOpenIcon(getIcon(type));
            treeNodes.add(node);
            i++;
        }
        return treeNodes;
    }

    private void searchContext(QueryEditorTextArea textArea, HTreeNode treeNode) {
        SearchContext context = new SearchContext();
        context.setSearchFor(treeNode.getType() + " " + treeNode.getName());
        context.setMatchCase(false);
        context.setWholeWord(true);
        context.setSearchForward(true);
        context.setMarkAll(false);
        boolean find = SearchEngine.find(textArea.getTextArea(), context).wasFound();
        if (!find) {
            context.setSearchFor(treeNode.getType() + "  " + treeNode.getName());
            SearchEngine.find(textArea.getTextArea(), context).wasFound();
        }
    }

    private void heightLight(QueryEditorTextArea textArea, int startLine, int endLine) {
        try {
            textArea.getTextArea().getHighlighter().removeAllHighlights();
            int startIndex = textArea.getTextArea().getLineStartOffset(startLine - 1);
            int endIndex = textArea.getTextArea().getLineEndOffset(endLine - 1);
            textArea.getTextArea().setCaretPosition(startIndex);
            Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
            textArea.getTextArea().getHighlighter().addHighlight(startIndex, endIndex, painter);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getErrorMsg(String packageName, OraSessionEnum oraSessionEnum) throws Exception {
        String pType = oraSessionEnum.getDbType();
        String sql = "select line, type, text from SYS.USER_ERRORS where type = '%s' and name = '%s' order by type, line";
        List<Map<String, Object>> maps = SqlQueryUtil.select(conn, String.format(sql, pType, LoginUtil.getRealName(packageName, DBTypeEnum.oracle.name())));
        StringBuilder errorText = new StringBuilder();
        for (Map<String, Object> map : maps) {
            String line = "0".equals((map.get("line") + "")) ? "1" : (map.get("line") + "");
            errorText.append(map.get("type")).append("  ").append(line).append("  ")
                    .append((map.get("text") + "").replace("\n", "")).append("\n");
        }
        return errorText.toString();
    }

    private List<Map<String, String>> queryObject(String objName) {
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
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return new ArrayList<>();
    }

    private JsonArray jsonValues;

    private void setKeyWord() throws Exception {
        //强制关闭之前查询的连接，防止表过多查询过久的情况
        jsonValues = new JsonArray();
        //先设置常用的关键字
        jsonValues = KeyWordUtil.getKeyWordJson(conn);
        textArea.setkeyword(jsonValues);
        new Thread(() -> {
            try {
                KeyWordUtil.getDbObjectJson(jsonValues, conn, schema, "table"); //先设置表提示
                textArea.setkeyword(jsonValues);
                KeyWordUtil.getDbObjectJson(jsonValues, conn, schema, "view");
                textArea.setkeyword(jsonValues);
                KeyWordUtil.getDbObjectJson(jsonValues, conn, schema, "function");
                textArea.setkeyword(jsonValues);
                DBTypeEnum dbtype = DriverUtil.getDbType(conn);
                if (dbtype == DBTypeEnum.oracle || dbtype == DBTypeEnum.dm) {
                    KeyWordUtil.getDbObjectJson(jsonValues, conn, schema, "synonym");
                    textArea.setkeyword(jsonValues);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                jsonValues = new JsonArray();
            }
        }).start();
    }

    private HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    private String getLang(String key) {
        LangMgr.setDefaultLang(StartUtil.default_language);
        return LangMgr.getValue(DOMAIN_NAME, key);
    }

    private ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.PACKAGE.name(), name, IconSizeEnum.SIZE_16));
    }

}
