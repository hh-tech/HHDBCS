package com.hh.hhdb_admin.mgr.query;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.explain.ExplainBean;
import com.hh.frame.dbobj2.explain.ExplainUtil;
import com.hh.frame.dbobj2.kword.KeyWordUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.PreferSqlWinBean;
import com.hh.frame.sqlwin.SqlWin;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.sqlwin.rs.WinRs;
import com.hh.frame.sqlwin.rs.WinRsBean;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.DbCmdStrUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditorTextArea;
import com.hh.hhdb_admin.common.util.textEditor.tooltip.Tooltip;
import com.hh.hhdb_admin.mgr.query.ui.ObjRefreshPanel;
import com.hh.hhdb_admin.mgr.query.ui.OutputTabPanel;
import com.hh.hhdb_admin.mgr.query.ui.SettingsPanel;
import com.hh.hhdb_admin.mgr.query.util.QuerUtil;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询器插件
 *
 * @author hexu
 */
public class QueryComp extends AbsHComp{
    private JdbcBean jdbc;
    private static String logName = QueryComp.class.getSimpleName();
    private LastPanel lastPanel = new LastPanel(false);
    //输出结果选项卡面板
    private OutputTabPanel outputTabPanel;
    //分割面板，上面是查询面板，下面是输出选项卡面板
    private HSplitPanel splitPane = new HSplitPanel(false);
    //编辑器
    private QueryEditorTextArea textArea;
    //提示工具
    private Tooltip tip;
    //对象刷新面板
    private ObjRefreshPanel objRefres;

    private SelectBox autocommitbox,schemabox,executeTypeBox;
    private HButton executeBut,rollBut,stopBut,submitBut,objRefresh;
    //查询对象
    private SqlWin sqlwin;
    private String absSqlWinId;

    private boolean bool = true;
    private List<String> schemaList;
    private String currSchame;
    private int rowsum = 30;     //每页显示条数
    private String nullSign;     //空值显示标记

    private  Map<PosBean, WinRsBean> map = new HashMap<>();

    private JsonArray jsonValues;

    private Connection conns; //查询提示词连接

    /**
     *
     * @param jdbcBean
     * @param text 显示的内容
     * @throws Exception
     */
    public QueryComp(JdbcBean jdbcBean,String text) throws Exception {
        this.jdbc = jdbcBean;
        //设置每页显示数量
        JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
        rowsum = fileJsonArr.get("varPageSize").asInt();
        nullSign = fileJsonArr.get("null").asString();
        textArea = QueryEditUtil.getQueryEditor(true);
        tip = QueryEditUtil.getQueryTooltip(textArea.getTextArea(),textArea.type);
        tip.setJdbc(jdbc);
        if (StringUtils.isNotBlank(text)) textArea.setText(text);
        absSqlWinId = "OQ_" + QueryMgr.sign++;
        sqlwin = WinMgr.newWin(jdbc, absSqlWinId);

        schemaList = SqlWinUtil.getSchemaNameList(sqlwin.getConn());
        currSchame = jdbc.getSchema();
        if(StringUtils.isBlank(currSchame)) {
            currSchame = SqlWinUtil.getCurrSchema(sqlwin.getConn());
            if(StringUtils.isBlank(currSchame)) {
                currSchame = schemaList.size()>0?schemaList.get(0):"";
            }
        }

        setKeyWord();    //置常关键字
        init();
        comp = lastPanel.getComp();
    }

    public LastPanel getLastPanel() throws Exception {
        return lastPanel;
    }

    public void close() {
        try {
            WinMgr.closeWin(absSqlWinId);
            ConnUtil.close(conns);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
    	lastPanel.setHead(initToolBar().getComp());
        splitPane.setPanelOne(textArea);
        splitPane.setSplitWeight(0.5);
        lastPanel.set(splitPane.getComp());
        LabelInput position = new LabelInput();
        position.setAlign(AlignEnum.RIGHT);
        lastPanel.setFoot(position.getComp());

        //设置光标位置显示
        textArea.getTextArea().addCaretListener(e -> {
            try {
                int pos = textArea.getTextArea().getCaretPosition(); // 获取行数
                int lineOfC = textArea.getTextArea().getLineOfOffset(pos) + 1; // 获取列数
                int col = pos - textArea.getTextArea().getLineStartOffset(lineOfC - 1) + 1;
                position.setValue(lineOfC + ":" + col + "       ");
            } catch (Exception ex) {
                logUtil.error(logName, ex);
            }
        });

        textArea.getTextArea().addFocusListener(new FocusListener() {
            @Override
            public void focusLost(FocusEvent e) {
                //光点移除不显示坐标
                position.setValue(" ");
            }
            @Override
            public void focusGained(FocusEvent e) {}
        });

        //添加快捷键
        textArea.getTextArea().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyReleased(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getModifiers() == InputEvent.SHIFT_MASK) {
                    if (e.getKeyCode() == (KeyEvent.VK_F10)) {
                        if (rollBut.getComp().isEnabled()) rollback();
                    }else if (e.getKeyCode() == (KeyEvent.VK_ESCAPE)) {
                        if (stopBut.getComp().isEnabled()) cancel();
                    }
                }else if (e.getKeyCode() == (KeyEvent.VK_F8)) {
                    execute();
                }else if (e.getKeyCode() == (KeyEvent.VK_F10)) {
                    if (submitBut.getComp().isEnabled()) commit();
                }
            }
        });
    }
    private HBarPanel initToolBar()throws Exception {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel hTool = new HBarPanel(l);
        //执行按钮
        executeBut = new HButton(QueryMgr.getLang("execute")) {
            @Override
            public void onClick() {
                execute();
            }
        };
        executeBut.setIcon(QueryMgr.getIcon("start"));
        executeBut.setToolTipText("F8");
        hTool.add(executeBut);
        //执行方式
        executeTypeBox = new SelectBox("executeTypeBox");
        executeTypeBox.addOption(QueryMgr.getLang("automaticSplit"),QueryMgr.getLang("automaticSplit"));
        executeTypeBox.addOption(QueryMgr.getLang("singleQuery"),QueryMgr.getLang("singleQuery"));
        executeTypeBox.addOption(QueryMgr.getLang("singleExecution"),QueryMgr.getLang("singleExecution"));
        executeTypeBox.setValue(QueryMgr.getLang("automaticSplit"));
        hTool.add(executeTypeBox);
        //停止按钮
        stopBut = new HButton(QueryMgr.getLang("stop")) {
            @Override
            public void onClick() {
                cancel();
            }
        };
        stopBut.setIcon(QueryMgr.getIcon("stop"));
        stopBut.setEnabled(false);
        stopBut.setToolTipText("SHIFT+ESC");
        hTool.add(stopBut);
        //提交按钮
        submitBut = new HButton(QueryMgr.getLang("submit")) {
            @Override
            public void onClick() {
                commit();
            }
        };
        submitBut.setIcon(QueryMgr.getIcon("commit"));
        submitBut.setEnabled(false);
        submitBut.setToolTipText("F10");
        hTool.add(submitBut);
        //回滚按钮
        rollBut = new HButton(QueryMgr.getLang("roll-back")) {
            @Override
            public void onClick() {
                rollback();
            }
        };
        rollBut.setIcon(QueryMgr.getIcon("rollback"));
        rollBut.setEnabled(false);
        rollBut.setToolTipText("SHIFT+F10");
        hTool.add(rollBut);
        //提交方式
        autocommitbox = new SelectBox("autocommitbox");
        autocommitbox.addOption(QueryMgr.getLang("auto-guestPosting"),QueryMgr.getLang("auto-guestPosting"));
        autocommitbox.addOption(QueryMgr.getLang("auto-commit"),QueryMgr.getLang("auto-commit"));
        autocommitbox.setValue(QuerUtil.initCommitType(DriverUtil.getDbType(jdbc)));
        hTool.add(autocommitbox);

        //模式选择框
        if (SqlWinUtil.showSchemaBox(DriverUtil.getDbType(jdbc))) {
            schemabox = new SelectBox("schemabox");
            for (String string : schemaList) {
                schemabox.addOption(string,string);
            }
            schemabox.setValue(currSchame);
            jdbc.setSchema(schemabox.getValue());
            hTool.add(schemabox);

            schemabox.getComp().addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        try {
                            jdbc.setSchema(schemabox.getValue());
                            jdbc.setCurSessionSchema(DbCmdStrUtil.toDbCmdStr(schemabox.getValue(),DriverUtil.getDbType(jdbc)));
                            sqlwin.getJdbc().setSchema(schemabox.getValue());

                            ConnUtil.setCurrentSchema(sqlwin.getConn(),jdbc.getCurSessionSchema() );
                            sqlwin.commit();

                            if(!currSchame.equals(schemabox.getValue())){ //设置关键词
                                setKeyWord();
                            }
                            currSchame = schemabox.getValue();
                            tip.setJdbc(jdbc);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            logUtil.error(logName, e1);
                        }
                    }
                }
            });
        }else {
            schemabox = new SelectBox("schemabox");
        }
        // 格式化SQL
        HButton formatBut = new HButton(QueryMgr.getLang("format")) {
            @Override
            public void onClick() {
                QuerUtil.formatSql(textArea);
            }
        };
        formatBut.setIcon(QueryMgr.getIcon("format"));
        //分析
        HButton fenxiBtn = new HButton(QueryMgr.getLang("analyse")) {
            @Override
            public void onClick() {
            	String selectedSql = textArea.getSelectedText();
                if(StringUtils.isEmpty(selectedSql)) {
                	PopPaneUtil.info(StartUtil.parentFrame.getWindow(), QueryMgr.getLang("selectoneSQL"));
                	return;
                }
                outputTabPanel = createOTabPanel();
                JSplitPane jsp = splitPane.getComp();
                jsp.setRightComponent(outputTabPanel.getHTabPane().getComp());
                if (bool) {
                    splitPane.setSplitWeight(0.5);
                    bool = false;
                }else {
                    jsp.setDividerLocation(jsp.getDividerLocation());
                }
                try {
                	ExplainBean bean;
                	if (sqlwin.hasCommit()) {
                		bean = ExplainUtil.getExplain(sqlwin.getConn(), selectedSql);
                	}else {
                		bean = ExplainUtil.getExplain(sqlwin.getConn(), selectedSql);
                		sqlwin.getConn().rollback();
                	}
					outputTabPanel.setMessage(selectedSql+"\n"+bean.getText());
                } catch (Exception e) {
                	outputTabPanel.setMessage(selectedSql+"\n"+ e);
				}
            }
        };
        fenxiBtn.setIcon(QueryMgr.getIcon("detail"));
        fenxiBtn.setToolTipText(QueryMgr.getLang("selectoneSQL"));
        //热键设置
        HButton hotkeysBut = new HButton(QueryMgr.getLang("hotkeys")) {
            @Override
            public void onClick() {
                new SettingsPanel(rowsum,nullSign) {
					@Override
					public void save(int row,String nullSigns) {
						rowsum = row;
                        nullSign = nullSigns;
					}
                };
            }
        };
        hotkeysBut.setIcon(QueryMgr.getIcon("key"));
        //保存到sql宝典
        HButton saveSqlBook = new HButton(QueryMgr.getLang("saveSqlBook")) {
            @Override
            public void onClick() {
                try {
                    JsonObject o = StartUtil.eng.doCall(CsMgrEnum.SQL_BOOK, GuiJsonUtil.genGetShareIdMsg(SqlBookMgr.ObjType.SHARE_PATH));
                    JFileChooser chooser = new JFileChooser();
                    if (null != o) chooser.setCurrentDirectory(new File(GuiJsonUtil.toStrSharedId(o)));
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    FileFilter fileFilter = new FileNameExtensionFilter("SQL文件(*.sql)","sql");
                    chooser.setFileFilter(fileFilter);
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        String url = chooser.getSelectedFile().getCanonicalPath();
                        url = url.endsWith(".sql") ? url : url+".sql";
                        File file = new File(url);
                        FileUtils.writeStringToFile(file, textArea.getText(), "utf-8");
                        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), QueryMgr.getLang("success"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                }
            }
        };
        saveSqlBook.setIcon(QueryMgr.getIcon("book"));
        //对象刷新
        objRefresh = new HButton(QueryMgr.getLang("objectRefresh")) {
            @Override
            public void onClick() {
                if (null == objRefres) {
                    objRefres = new ObjRefreshPanel(objRefresh,jdbc) {
                        @Override
                        public void update(JsonArray jsonValues) {
                            tip.setkeyword(jsonValues);
                        }
                    };
                }
                objRefres.show();
            }
        };
        objRefresh.setIcon(QueryMgr.getIcon("refresh"));
    
        hTool.add(formatBut,fenxiBtn,hotkeysBut,saveSqlBook,objRefresh);
        return hTool;
    }

    private void execute() {
        map = new HashMap<>();
        if (!StringUtils.isNotBlank(textArea.getText())) {
            return;
        }
        new Thread(new SwingWorker<String, String>() {
            @Override
            protected String doInBackground() throws Exception {
                executeBut.setEnabled(false);
                stopBut.setEnabled(true);
                executeTypeBox.setEnabled(false);

                String sql = "";
                String selectedSql = textArea.getSelectedText();
                sql =StringUtils.isNotEmpty(selectedSql) ? selectedSql : textArea.getText();
                outputTabPanel = createOTabPanel();
                JSplitPane jsp = splitPane.getComp();
                //获取执行sql的位置信息
                PosBean pos = new PosBean();
                pos.setBeginColumn(StringUtils.isNotEmpty(selectedSql) ? textArea.getTextArea().getSelectionStart() : 0);
                pos.setBeginLine(StringUtils.isNotEmpty(selectedSql) ? textArea.getLineByPosition(textArea.getTextArea().getSelectionStart()) : 0);
                pos.setEndColumn(StringUtils.isNotEmpty(selectedSql) ? textArea.getTextArea().getSelectionEnd() : 0);
                pos.setEndLine(StringUtils.isNotEmpty(selectedSql) ? textArea.getLineByPosition(textArea.getTextArea().getSelectionEnd()) : 0);
                //mod命令运行
                if (sql.trim().startsWith("\\")) {
                    // \copy是元子命令，需要用流
                    if (sql.trim().startsWith("\\copy")) {
                        QuerUtil.copyStream(jdbc,sql);
                        outputTabPanel.setMessage("Do success!\n");
                        return "";
                    }
                }

                if (!ConnUtil.isConnected(sqlwin.getConn()))  {   //防止连接断掉从新设置
                    sqlwin.setConn(ConnUtil.getConn(sqlwin.getJdbc()));
                }
                if (autocommitbox.getValue().equals(QueryMgr.getLang("auto-commit"))) {
                    sqlwin.setAutoCommit(true);
                } else {
                    sqlwin.setAutoCommit(false);
                }

                PreferSqlWinBean pwb = new PreferSqlWinBean();
                pwb.setNumPerPage(rowsum);
                sqlwin.setPrefer(pwb);
                WinRs rs = null;
                if (executeTypeBox.getValue().equals(QueryMgr.getLang("automaticSplit"))) {
                    rs = sqlwin.runSql(sql, pos);
                } else if (executeTypeBox.getValue().equals(QueryMgr.getLang("singleQuery"))) {
                    rs = sqlwin.runSqlNoParse(sql, pos, true);
                } else if (executeTypeBox.getValue().equals(QueryMgr.getLang("singleExecution"))) {
                    rs = sqlwin.runSqlNoParse(sql, pos, false);
                }

                getPrecisePositon(map = rs.getRsMap());  //处理结果集中的错误信息
                outputTabPanel.showRs(map,rowsum,nullSign);
                jsp.setRightComponent(outputTabPanel.getHTabPane().getComp());
                if (bool||jsp.getHeight()-jsp.getDividerLocation()<=15) {
                    splitPane.setSplitWeight(0.5);
                    bool = false;
                }else {
                    jsp.setDividerLocation(jsp.getDividerLocation());
                }
                return "";
            }
            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception e) {
                    logUtil.error(logName, e);
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                } finally {
                    executeBut.setEnabled(true);
                    stopBut.setEnabled(false);
                    executeTypeBox.setEnabled(true);
                    if(autocommitbox.getValue().equals(QueryMgr.getLang("auto-guestPosting"))) {
                        try {
                            //判断是否有执行错误的sql进行回滚
                            for (PosBean posBean : map.keySet()) {
                                if (null != map.get(posBean).getErr()) {
                                    QuerUtil.rollbackErr(sqlwin.getConn());
                                    break;
                                }
                            }
                            //根据情况显示提交回滚等按钮
                            if(QuerUtil.hasCommit(sqlwin)) {
                                autocommitbox.setEnabled(false);
                                submitBut.setEnabled(true);
                                rollBut.setEnabled(true);
                                schemabox.setEnabled(false);
                                executeTypeBox.setEnabled(false);
//                            }else {
                                //手动提交模式下处理当执行select等不用提交的语句时，被操作表连接挂起问题
//                                commit();
                            }
                        } catch (Exception e) {
                        	e.printStackTrace();
                            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
                            try {
                                sqlwin.rollback();
                            }catch (Exception e1){
                               e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    private void commit(){
        try {
            sqlwin.commit();
        } catch (Exception e1) {
            logUtil.error(logName, e1);
            outputTabPanel.setMessage(QueryMgr.getLang("submitFailed") + "！" + e1.getMessage() + "\n");
        }finally {
            submitBut.setEnabled(false);
            rollBut.setEnabled(false);
            autocommitbox.setEnabled(true);
            schemabox.setEnabled(true);
            executeTypeBox.setEnabled(true);
        }
    }

    private void cancel(){
        try {
            sqlwin.cancel();
            if (!ConnUtil.isConnected(sqlwin.getConn()))  {
                sqlwin.setConn(ConnUtil.getConn(sqlwin.getJdbc()));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            logUtil.error(logName, e1);
        } finally {
            stopBut.setEnabled(false);
            executeBut.setEnabled(true);
            executeTypeBox.setEnabled(true);
        }
    }

    private void rollback() {
        try {
            sqlwin.rollback();
            submitBut.setEnabled(false);
            rollBut.setEnabled(false);
            autocommitbox.setEnabled(true);
            schemabox.setEnabled(true);
            executeTypeBox.setEnabled(true);
        } catch (Exception e1) {
            logUtil.error(logName, e1);
        }
    }

    private OutputTabPanel createOTabPanel() {
    	return new OutputTabPanel(jdbc){
            @Override
            protected void highlighted(ChangeEvent e, Map<String, List<Integer>> resultMap) {
                try {
                    textArea.getTextArea().removeAllLineHighlights();
                    JTabbedPane jtab = (JTabbedPane) e.getSource();
                    List<Integer> li = resultMap.get(jtab.getTitleAt(jtab.getSelectedIndex()));
                    if (null != li) {
                        for (Integer integer : li) {
                            textArea.getTextArea().addLineHighlight(integer, new Color(255, 255, 170));
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e1);
                }
            }
        };
    }

    private void setKeyWord() throws Exception{
        //强制关闭之前查询的连接，防止表过多查询过久的情况
        ConnUtil.close(conns);
        jsonValues = new JsonArray();
        //先设置常用的关键字
        jsonValues = KeyWordUtil.getKeyWordJson(sqlwin.getConn());
        tip.setkeyword(jsonValues);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    conns = ConnUtil.getConn(jdbc);
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, currSchame,"table"); //先设置表提示
                    tip.setkeyword(jsonValues);
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, currSchame,"view");
                    tip.setkeyword(jsonValues);
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, currSchame,"mview");
                    tip.setkeyword(jsonValues);
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, currSchame,"function","procedure");
                    tip.setkeyword(jsonValues);
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, currSchame,"synonym");
                    tip.setkeyword(jsonValues);
                    KeyWordUtil.getDbObjectJson(jsonValues,conns, currSchame,"pack");
                    tip.setkeyword(jsonValues);
                } catch (Exception e) {
                    e.printStackTrace();
                    logUtil.error(logName, e);
                }finally {
                    ConnUtil.close(conns);
                    jsonValues = new JsonArray();
                }
            }
        }).start();
    }

    /**
     * 根据错误信息获得精准错误位置信息
     */
    private void getPrecisePositon(Map<PosBean, WinRsBean> map) {
        try {
            for (PosBean posBean : map.keySet()) {
                WinRsBean rsBean=map.get(posBean);
                if (null != rsBean.getErr()) {
                    String m2 = "";
                    String message = rsBean.getErr().getMessage();
                    int i = message.indexOf("Position:");
                    if (i < 0) {
                        i = message.indexOf(QueryMgr.getLang("location"));
                        if (i > 0) {
                            m2 = message.substring(i + 3);
                        }
                    } else {
                        m2 = message.substring(i + 9);
                    }
                    if (i > 0) {
                        String m1 = message.substring(0, i) + "Line:";
                        int o = textArea.getTextArea().getLineStartOffset(posBean.getBeginLine()-1);
                        int p=0;
                        int col=0;
                        p = textArea.getLineByPosition(Integer.parseInt(m2.trim())+o);
                        col = Integer.parseInt(m2.trim())+ o - textArea.getTextArea().getLineStartOffset(p - 1);
                        message = m1 + p + "  Position:" + col;
                    }
                    rsBean.setErr(new Exception(message));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
