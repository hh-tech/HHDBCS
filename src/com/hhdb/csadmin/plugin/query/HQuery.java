package com.hhdb.csadmin.plugin.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultCaret;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.log.LM;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.dbobj.hhdb.HHdbSession;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.dao.ConnService;
import com.hhdb.csadmin.common.ui.textEdit.QueryEditorUi2;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.query.dataselect.SqlOperationService;
import com.hhdb.csadmin.plugin.query.service.ExportFileService;
import com.hhdb.csadmin.plugin.query.service.QueryService;
import com.hhdb.csadmin.plugin.query.syntax.Constants;
import com.hhdb.csadmin.plugin.query.syntax.ExecuteType;
import com.hhdb.csadmin.plugin.query.syntax.ObjType;
import com.hhdb.csadmin.plugin.query.syntax.Query;
import com.hhdb.csadmin.plugin.query.syntax.QueryTokenizer;
import com.hhdb.csadmin.plugin.query.syntax.SQLFormatter;
import com.hhdb.csadmin.plugin.query.util.ButtonPanelEditorRenderer;
import com.hhdb.csadmin.plugin.query.util.HHTableColumnCellRenderer;
import com.hhdb.csadmin.plugin.query.util.Pgsqlcmd;
import com.hhdb.csadmin.plugin.query.util.QueryUIUtils;
import com.hhdb.csadmin.plugin.query.util.SqlUtil;

/**
 * @ClassName: HQuery
 * @author: qinsz
 * @Description: 查询器插件
 * @date: 2017年10月27日 下午2:34:31
 */
public class HQuery {
	public String databaseName; // 数据库名
	public String schemaName; // 模式名
	public String tablename; // 表名
	public SqlOperationService sqls ;
	// 主面板
	private JPanel jPanel = new JPanel(new GridBagLayout());
	// 菜单栏
	private JToolBar mainToolBar = new JToolBar();
	// 编辑器分离面板，上面是查询面板，下面是输出面板
	private JSplitPane jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	private int selectPosiotion = 0;
	private int endPosition = 0;
	// 输出表格数据条数
	private int pageposition =30;
	//默认第一页
	private int pagenum=0;

	private Query singleQuery = null;
	// 模式名字集合
	List<String> schemeNameList;
	// 查询面板
	private QueryEditorUi2 queryUi;
	// 执行按钮
	private JButton excuteBtn;
	// 停止按钮
	private JButton stopBtn;
	// 提交按钮
	private JButton commitBtn;
	// 回滚按钮
	private JButton rollbackBtn;

	private JComboBox<String> autocommitbox;
	// 打开文件按钮
	private JButton openBtn;
	private JButton opensqlBtn;
	// 保存文件按钮
	private JButton saveBtn;
	// 另存文件按钮
	private JButton saveasBtn;
	//热键设置
	private JButton keySetBtn;
	// private JButton booksBtn;
	// private JButton historyBtn;
	// 格式化按钮
	private JButton formatBtn;
	// 模式选择框
	private JComboBox<String> schemabox;
	// 输出框
	private JPanel outPanel = new JPanel(new BorderLayout());
	// 输出框的菜单
	private JToolBar outToolBar = new JToolBar();
	private TablePanelUtil tablePanel;
	// 输出框按钮
	private JButton lastpageBtn = new JButton("上一页", new ImageIcon(
			ClassLoader.getSystemResource("icon/nextpage.png")));
	private JButton nextpageBtn = new JButton("下一页", new ImageIcon(
			ClassLoader.getSystemResource("icon/nextpage.png")));
	private JButton exportallBtn = new JButton("导出", new ImageIcon(
			ClassLoader.getSystemResource("icon/exportall.png")));
	private JLabel msglabel = new JLabel();
	// 输出面板的表单
	private JTable table;
	private JRadioButton radio1 = new JRadioButton("导出csv");
	private JRadioButton radio2 = new JRadioButton("导出excel");
	// 输出面板的提示区域
	private JTextArea infoTextArea;
	// 针对查询执行的链接
	private Connection conn = null;
	// 查询器插件主类
	private HQueryPlugin queryPlugin;
	// 服务器信息的类
	private ServerBean serverbean;
	// 查询链接的pid
	private String sessionPid = "";
	
//	private HHTablePanel hhTbPanel;

	// 是否继续执行
	private boolean excuflag = true;
	
	// 是否是drop操作刷新结构树
	private boolean refDropflag = false;
	
	public QueryService qs;
	/**
	 * 返回查询面板
	 * 
	 * @return
	 */
	public QueryEditorUi2 getQueryUi() {
		return queryUi;
	}

	/**
	 * 构造方法
	 * 
	 * @param queryPlugin
	 */
	public HQuery(HQueryPlugin queryPlugin) {
		this.queryPlugin = queryPlugin;
		CmdEvent getsbEvent = new CmdEvent(queryPlugin.PLUGIN_ID,
				"com.hhdb.csadmin.plugin.conn", "GetServerBean");
		HHEvent revent = queryPlugin.sendEvent(getsbEvent);
		serverbean = (ServerBean) revent.getObj();

		init();
		outPanel.setVisible(false);
		if (schemabox != null && schemabox.getSelectedItem() != null) {
			String schema = schemabox.getSelectedItem().toString();
			try {
				chagepopupList(schema);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
	}

	/**
	 * 获取主面板
	 * 
	 * @return
	 */
	public JPanel getjPanel() {
		return jPanel;
	}

	/**
	 * 关闭连接
	 */
	public void closeConn() {
		try {
			conn.close();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
	}

	/***
	 * 改变提示词汇
	 * 
	 * @param schemaName
	 * @throws Exception
	 */
	private void chagepopupList(String schemaName) throws Exception {
		if (queryUi == null) {
			return;
		}
		setStatement();
		if (schemabox != null && schemabox.getSelectedItem() != null) {
			String schema = schemabox.getSelectedItem().toString();
			try {
				// 获取表名
				List<Map<String, String>> list = QueryService.getTableIdName(
						conn, schema);
				List<String> tablelist = new ArrayList<String>();
				for (Map<String, String> maps : list) {
					tablelist.add(maps.get("name").toString());
				}
				queryUi.setTableCompletionProvider(tablelist);
				// 获取视图
				list = QueryService.getViewIdName(conn, schema);
				List<String> viewlist = new ArrayList<String>();
				for (Map<String, String> maps : list) {
					viewlist.add(maps.get("name").toString());
				}
				queryUi.setViewCompletionProvider(viewlist);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
	}

	/**
	 * 查询器页面初始化
	 */
	private void init() {
		try {
			setStatement();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		jPanel = new JPanel(new GridBagLayout());
		excuteBtn = new JButton("执行", new ImageIcon(
				ClassLoader.getSystemResource("icon/start.png")));
		stopBtn = new JButton("停止", new ImageIcon(
				ClassLoader.getSystemResource("icon/stop.png")));
		commitBtn = new JButton("提交", new ImageIcon(
				ClassLoader.getSystemResource("icon/commit.png")));
		rollbackBtn = new JButton("回滚", new ImageIcon(
				ClassLoader.getSystemResource("icon/rollback.png")));
		autocommitbox = new JComboBox<String>();
		autocommitbox.addItem("自动提交");
		autocommitbox.addItem("手动提交");
		autocommitbox.setMaximumSize(new Dimension(100, 22));

		openBtn = new JButton("本地加载", new ImageIcon(
				ClassLoader.getSystemResource("icon/open.png")));
		opensqlBtn = new JButton("宝典加载", new ImageIcon(
				ClassLoader.getSystemResource("icon/open.png")));
		saveBtn = new JButton("保存到宝典", new ImageIcon(
				ClassLoader.getSystemResource("icon/save.png")));
		saveasBtn = new JButton("下载到本地", new ImageIcon(
				ClassLoader.getSystemResource("icon/saveas.png")));
		// booksBtn = new JButton("SQL宝典", new ImageIcon(
		// ClassLoader.getSystemResource("icon/book.png")));
		// historyBtn = new JButton("历史记录", new ImageIcon(
		// ClassLoader.getSystemResource("icon/saveas.png")));
		formatBtn = new JButton("格式化SQL", new ImageIcon(
				ClassLoader.getSystemResource("icon/formatsql.png")));
		keySetBtn = new JButton("热键设置", new ImageIcon(
				ClassLoader.getSystemResource("icon/addforeign_key.png")));

		stopBtn.setEnabled(false);
		commitBtn.setEnabled(false);
		rollbackBtn.setEnabled(false);
		try {
			schemeNameList = QueryService.getSchemaNameList(conn);
		} catch (SQLException e2) {
			LM.error(LM.Model.CS.name(), e2);
		}
		schemabox = new JComboBox<String>();
		for (int i = 0; i < schemeNameList.size(); i++) {
			String schema = schemeNameList.get(i);
			if (i == 0) {
				try {
					QueryService.setEnvSql(conn, schema);
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
			}
			schemabox.addItem(schema);
		}
		schemabox.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					schemeNameList = QueryService.getSchemaNameList(conn);
				} catch (SQLException e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
				schemabox.removeAllItems();
				for (String schema : schemeNameList) {
					schemabox.addItem(schema);
				}
			}
		});
		// 监听选中
		schemabox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				// 如果选中了一个
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// 这里写你的任务 ，比如取到现在的值
					String schema = (String) schemabox.getSelectedItem();
					// 设置环境属性
					try {
						QueryService.setEnvSql(conn, schema);
						chagepopupList(schema);
					} catch (Exception e1) {
						LM.error(LM.Model.CS.name(), e1);
					}
				}
			}
		});
		HHEvent toevent = new HHEvent(queryPlugin.PLUGIN_ID,
				"com.hhdb.csadmin.plugin.tree", EventTypeEnum.COMMON.name());
		HHEvent relevent = queryPlugin.sendEvent(toevent);
		String schemaName = relevent.getValue("schemaname_str");
		if (StringUtils.isNoneBlank(schemaName)) {
			schemabox.setSelectedItem(schemaName);
		}
		schemabox.setPreferredSize(new Dimension(150, 22));
		schemabox.setMaximumSize(new Dimension(150, 22));

		mainToolBar.add(excuteBtn);
		mainToolBar.add(stopBtn);
		mainToolBar.add(commitBtn);
		mainToolBar.add(rollbackBtn);
		mainToolBar.add(autocommitbox);
		mainToolBar.add(openBtn);
		mainToolBar.add(opensqlBtn);
		mainToolBar.add(saveBtn);
		mainToolBar.add(saveasBtn);
		mainToolBar.add(formatBtn);
		mainToolBar.add(schemabox);
		mainToolBar.add(keySetBtn);
		mainToolBar.add(Box.createHorizontalGlue());

		queryUi = new QueryEditorUi2();
		queryUi.getContentPane().setBorder(null);

		outToolBar.add(lastpageBtn, new GridBagConstraints(1, 0, 1, 1, 0.0,
				1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 0), 0, 0));
		outToolBar.add(nextpageBtn, new GridBagConstraints(0, 0, 1, 1, 0.0,
				1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 0), 0, 0));
		outToolBar.add(exportallBtn, new GridBagConstraints(2, 0, 1, 1, 0.0,
				1.0, GridBagConstraints.NORTH, GridBagConstraints.NONE,
				new Insets(0, 5, 0, 0), 0, 0));
		outToolBar.add(new JPanel(), new GridBagConstraints(3, 0, 1, 1, 1.0,
				1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		JPanel status = new JPanel();
		status.setPreferredSize(new Dimension(350, 22));
		status.setLayout(new GridBagLayout());
		status.add(msglabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(
						0, 20, 0, 0), 0, 0));
		status.add(new JPanel(), new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		jSplitPane.setTopComponent(queryUi.getContentPane());
		jPanel.add(mainToolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						2, 0, 2, 0), 0, 0));
		jPanel.add(jSplitPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));

		commitBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (commitBtn.isEnabled()) {
					try {
						conn.commit();
						commitBtn.setEnabled(false);
						rollbackBtn.setEnabled(false);
						autocommitbox.setEnabled(true);
						if(refDropflag){
							HHEvent rfschemaEv = new CmdEvent(queryPlugin.PLUGIN_ID, "com.hhdb.csadmin.plugin.tree", "RefreshTreeNodeByQueryDropEvent");
							queryPlugin.sendEvent(rfschemaEv);
						}
					} catch (Exception e1) {
						LM.error(LM.Model.CS.name(), e1);
						infoTextArea.append("提交失败！"+e1.getMessage()+"\n");
						commitBtn.setEnabled(false);
						rollbackBtn.setEnabled(false);
						autocommitbox.setEnabled(true);
					}
				}
			}
		});
		rollbackBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (rollbackBtn.isEnabled()) {
					try {
						conn.rollback();
						commitBtn.setEnabled(false);
						rollbackBtn.setEnabled(false);
						autocommitbox.setEnabled(true);
					} catch (Exception e1) {
						LM.error(LM.Model.CS.name(), e1);
					}
				}
			}
		});
		stopBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (stopBtn.isEnabled()) {
					try {
						excuflag = false;
						if (!QueryService.cancelQuery(sessionPid, queryPlugin)) {
							conn.close();
						}
					} catch (Exception e1) {
						LM.error(LM.Model.CS.name(), e1);
					} finally {
						stopBtn.setEnabled(false);
						excuteBtn.setEnabled(true);
					}
				}
			}
		});
		class ExecuteThread implements Runnable {
			public void run() {
				try {
					setStatement();
				} catch (SQLException e1) {
					LM.error(LM.Model.CS.name(), e1);
				}
				excuteBtn.setEnabled(false);
				stopBtn.setEnabled(true);
				try {
					conn.setAutoCommit(false);
				} catch (SQLException e) {
					LM.error(LM.Model.CS.name(), e);
				}
				String sql = "";
				String selectedSql = queryUi.getSelectedText();
				if (StringUtils.isNotEmpty(selectedSql)) {
					selectPosiotion = queryUi.getTextArea().getSelectionStart();
					endPosition = queryUi.getTextArea().getSelectionEnd();
					sql = selectedSql;
				} else {
					selectPosiotion = 0;
					endPosition = 0;
					sql = queryUi.getTextArea().getText();
				}
				QueryTokenizer qt = new QueryTokenizer();
				qt.extractQuotedStringTokens(sql);
				qt.extractSingleLineCommentTokens(sql);
				qt.extractMultiLineCommentTokens(sql);
				deriveQueries(sql, selectPosiotion, qt);
				if (endPosition > selectPosiotion) {
					queryUi.getTextArea().requestFocusInWindow();
					queryUi.getTextArea().select(selectPosiotion, endPosition);
				}
				stopBtn.setEnabled(false);
				excuteBtn.setEnabled(true);
			}
		}
		excuteBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (excuteBtn.isEnabled()) {
					if(queryUi.getText()==null||queryUi.getText().trim().isEmpty()){
						return;
					}
					Runnable tr = new ExecuteThread();
					Thread th1 = new Thread(tr);
					excuflag = true;
					th1.start(); // 开始执行线程
				}
			}
		});

		openBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式，既可以选择文件又可以选择文件夹
				int result = chooser.showOpenDialog(null); // 打开"打开文件"对话框
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					try {
						queryUi.getTextArea().setText(
								FileUtils.readFileToString(file, "utf-8"));
					} catch (IOException e1) {
						LM.error(LM.Model.CS.name(), e1);
					}
				}
			}
		});
		opensqlBtn.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				CmdEvent querySQLSaveEv = new CmdEvent(queryPlugin.PLUGIN_ID, "com.hhdb.csadmin.plugin.sql_book", "querySQLBook");
				querySQLSaveEv.setObj(queryUi.getTextArea());
				HHEvent rfEvent = queryPlugin.sendEvent(querySQLSaveEv);
				Component cp = (Component)rfEvent.getObj();
				JFrame myrame = new JFrame();
				myrame.add(cp);
				ImageIcon icon=new ImageIcon(System.getProperty("user.dir")+"/etc/icon/manage.png");
				myrame.setIconImage(icon.getImage());
				myrame.setAutoRequestFocus(false);
				myrame.setTitle("选择需要加载的SQL");
				Dimension size = new Dimension(1080, 600);
				myrame.setSize(size);
				myrame.setLocationRelativeTo(null);
				myrame.setVisible(true);
			}
		});

		// 保存
		saveBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				CmdEvent querySQLSaveEv = new CmdEvent(queryPlugin.PLUGIN_ID, "com.hhdb.csadmin.plugin.sql_book", "querySQLSave");
				querySQLSaveEv.addProp("sql",queryUi.getTextArea().getText());
				HHEvent rfEvent = queryPlugin.sendEvent(querySQLSaveEv);
				rfEvent.getObj();
			}
		});

		// 另存为
		saveasBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式，既可以选择文件又可以选择文件夹
				int result = chooser.showSaveDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					try {
						FileUtils.writeStringToFile(file, queryUi.getTextArea()
								.getText(), "utf-8");
					} catch (Exception e1) {
						LM.error(LM.Model.CS.name(), e1);
					}
				}
			}
		});

		// 格式化SQL
		formatBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				String sql = queryUi.getTextArea().getText();
				if(sql.trim().isEmpty()){
					return;
				}
				if (sql.length() > 100000) {
					JOptionPane.showMessageDialog(null, "内容超过上限不能格式化操作", "提示",
							JOptionPane.WARNING_MESSAGE, null);
					return;
				}
				// 获取每段sql
				List<String> sqlList = new ArrayList<String>();
				int index = 0;
				int lastIndex = 0;
				while ((index = sql.indexOf(";", index + 1)) != -1) {
					String substring = sql.substring(lastIndex, index);
					sqlList.add(substring);
					lastIndex = index + 1;
				}
				// 格式化
				StringBuilder sb = new StringBuilder();
				if (sqlList.size() == 0) {
					sqlList.add(sql);
				}
				for (String query : sqlList) {
					String formatted = new SQLFormatter(query).format();

					sb.append(formatted.trim());
					if (!formatted.endsWith(";")) {
						sb.append(";");
					}
					sb.append("\n\n");
				}
				queryUi.getTextArea().setText(sb.toString());
			}
		});
		
		keySetBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				SetKeyFrame skframe = new SetKeyFrame(queryUi);
				skframe.setVisible(true);
			}
		});

		/**
		 * 下一页
		 */
		nextpageBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (nextpageBtn.isEnabled()) {
					pagenum++;
					showQueryData(pageposition, singleQuery);
//					if (endPosition > selectPosiotion) {
//						queryUi.getTextArea().requestFocusInWindow();
//						queryUi.getTextArea().select(selectPosiotion,
//								endPosition);
//					}
				}
			}
		});
		/**
		 * 上一页
		 */
		lastpageBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(lastpageBtn.isEnabled()){
					pagenum--;
					showQueryData(pageposition, singleQuery);
//					if (endPosition > selectPosiotion) {
//						queryUi.getTextArea().requestFocusInWindow();
//						queryUi.getTextArea().select(selectPosiotion, endPosition);
//					}
				}
				
			}
		});
		/**
		 * 导出
		 */
		exportallBtn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Object[] options = { "当前数据", "全部数据", "取消" };
				JOptionPane pane = new JOptionPane(initExport(),
						JOptionPane.PLAIN_MESSAGE,
						JOptionPane.OK_CANCEL_OPTION, null, options, options[0]);
				JDialog dlg = pane.createDialog(jPanel, "导出向导");
				dlg.setModal(true);
				dlg.setSize(300, 160);
				dlg.setLocationRelativeTo(null);
				dlg.setVisible(true);
				String value = (String) pane.getValue();
				try {
					setStatement();
					if ("当前数据".equals(value)) {
						String ssql = singleQuery.getDerivedQuery();
						if (pageposition > 0) {
							ssql = "select * from ("
									+ singleQuery.getDerivedQuery()
									+ ") a limit " + pageposition;
						}
						if (radio1.isSelected()) {
							ExportFileService.CSVWriter(conn, ssql);
						} else if (radio2.isSelected()) {
							ExportFileService.createExcel_all(conn,ssql);
						}
					} else if ("全部数据".equals(value)) {
						if (radio1.isSelected()) {
							ExportFileService.CSVWriter(conn,
									singleQuery.getDerivedQuery());
						} else if (radio2.isSelected()) {
							ExportFileService.createExcel_all(conn,
									singleQuery.getDerivedQuery());
						}
					} else if ("取消".equals(value)) {
						dlg.dispose();
					}
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					QueryUIUtils.showErrorBox(jPanel, e1.getMessage());
				}
			}
		});
	}

	/**
	 * 初始化导出csv和excel菜单组
	 * 
	 * @return
	 */
	private JPanel initExport() {
		JPanel jp = new JPanel();
		radio1.setSelected(true);
		jp.add(radio1);
		jp.add(radio2);
		ButtonGroup group = new ButtonGroup();// 创建单选按钮组
		group.add(radio1);
		group.add(radio2);
		return jp;
	}

	/**
	 * 初始化输出面板
	 */
	private void initInfoTextArea() {
		outPanel.removeAll();
		infoTextArea = new JTextArea();
		DefaultCaret caret = (DefaultCaret) infoTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(infoTextArea);
		scroll.getViewport().setBackground(Color.WHITE);
		scroll.setBorder(null);
		infoTextArea.setEditable(false);
		outPanel.add(scroll, BorderLayout.CENTER);

		boolean dl = false;
		int location=0;
		if (outPanel.isVisible()) {
			dl = true;
			location=jSplitPane.getDividerLocation();
		}
		outPanel.setVisible(true);
		jSplitPane.setBottomComponent(outPanel);
		if (dl) {
			jSplitPane.setDividerLocation(location);
		} else {
			jSplitPane.setDividerLocation(0.6);
		}
	}

	/**
	 * 显示查询语句的结果集
	 * 
	 * @param position
	 */
	private void showQueryData(int position, Query query) {
		int line = queryUi.getLineByPosition(query.getPosition());
		String lineStr = "行" + line + ": ";
		try {
			long btime = System.currentTimeMillis();
			outPanel.removeAll();
			outPanel.add(outToolBar, BorderLayout.NORTH);
			table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setRowHeight(22);
			setStatement();
			String sql = QueryService.executeQuery(
					query.getDerivedQuery(), position,pagenum);
			//查询出来的数据包括字段名、字段类、字段数据
			List<List<Object>> list = SqlQueryUtil.selectColumnAndDataList(
					 conn,sql);
			
			Vector<Object> colname = new Vector<Object>();
			//将查询出来的字段名分出来
			colname.add("");
			for (Object field : list.get(0)) {
				colname.add(field);
				
			}
			Vector<Object> vect = new Vector<Object>();
			//将查询出来的数据分出来
			for(int j=2;j<list.size();j++){
				List<Object> l = list.get(j);
				Vector<Object> rowsLine = new Vector<Object>();
				rowsLine.add(j-1+"");   //行号
				for(Object value:l){
					rowsLine.add(value);
				}
				vect.add(rowsLine);
			}
			tablePanel = new TablePanelUtil(true, colname, vect, false, true);
			tablePanel.nterlacedDiscoloration(true,null,null); 
			tablePanel.highlight(true,false,null);
			//表不拉伸
			tablePanel.getBaseTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
			tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			//获取字段类型
			List<Object> suv =list.get(1);
			List<Object> typelist = new ArrayList<Object>();
			typelist.add("");
			for(int i=0;i<suv.size();i++){
				typelist.add(suv.get(i));
			}
			
			//判断哪些列需要加入流操作按钮
			ButtonPanelEditorRenderer er =  new ButtonPanelEditorRenderer(queryPlugin);
			for(int i=0;i<colname.size();i++){
				Object name = colname.get(i);
				if(!"".equals(name)){
				Object type=typelist.get(i);
					if(type.equals("bytea")){
						tablePanel.getBaseTable().getColumn(name).setCellRenderer(er);
						tablePanel.getBaseTable().getColumn(name).setCellEditor(er);
					}
				}
			}
			
			TableColumn index = tablePanel.getBaseTable().getColumnModel()
					.getColumn(0);
			index.setWidth(20);
			index.setPreferredWidth(20);
			index.setCellRenderer(new HHTableColumnCellRenderer());
			long etime = System.currentTimeMillis();
			msglabel.setText("查询总耗时：" + (etime - btime) +"ms");
			
			if (position > 0) {
				if (list.size()>=32) {
					nextpageBtn.setEnabled(true);
				} 
				else if (list.size()<32){
					nextpageBtn.setEnabled(false);
				}
				if(pagenum<=0){
					lastpageBtn.setEnabled(false);
				}
				else if(pagenum>0){
					lastpageBtn.setEnabled(true);
				}
			} else {
				nextpageBtn.setEnabled(false);
				lastpageBtn.setEnabled(false);
			}
			outPanel.add(tablePanel, BorderLayout.CENTER);
			outPanel.add(msglabel, BorderLayout.SOUTH);
			boolean dl = false;
			int derivedLocation =0;
			if (outPanel.isVisible()) {
				dl = true;
				derivedLocation=jSplitPane.getDividerLocation();
			}
			outPanel.setVisible(true);
			jSplitPane.setBottomComponent(outPanel);

			if (dl) {
				jSplitPane.setDividerLocation(derivedLocation);
			} else {
				jSplitPane.setDividerLocation(0.6);
			}
		} catch (Exception e) {
			try {
				initInfoTextArea();
				infoTextArea.append(lineStr + e.getMessage() + "\n");
				conn.rollback();
			} catch (Exception e2) {
				LM.error(LM.Model.CS.name(), e2);
			}
		}
	}
	

	/**
	 * 查询规划
	 */
	private void showExplainData(Query query) {
		int line = queryUi.getLineByPosition(query.getPosition());
		String lineStr = "行" + line + ": ";
		try {
			long btime = System.currentTimeMillis();
			outPanel.removeAll();
			table = new JTable();
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setRowHeight(22);

			List<List<Object>> list = SqlQueryUtil.selectList(conn,
					query.getDerivedQuery());

			Vector<Object> data = new Vector<Object>();
			Vector<String> colname = new Vector<String>();
			for (Object field : list.get(0)) {
				colname.add(field.toString());
			}
			for (int i = 0; i < list.size(); i++) {
				List<Object> ll = list.get(i);
				Vector<Object> data1 = new Vector<Object>();
				for (Object value : ll) {
					data1.add(value != null ? value.toString() : "");
				}
				data.add(data1);
			}
			table.setModel(new DefaultTableModel(data, colname));
			long etime = System.currentTimeMillis();
			msglabel.setText("查询总耗时：" + (etime - btime) );
//					+ " ms, 检索到: "
//					+ (list.size() - 1) + " 行");
			JScrollPane scroll = new JScrollPane(table);
			scroll.getViewport().setBackground(Color.WHITE);
			scroll.setBorder(null);
			outPanel.add(scroll, BorderLayout.CENTER);
			outPanel.add(msglabel, BorderLayout.SOUTH);
			int divider=0;
			boolean dl = false;
			if (outPanel.isVisible()) {
				dl = true;
				divider =jSplitPane.getDividerLocation();
			}
			outPanel.setVisible(true);
			jSplitPane.setBottomComponent(outPanel);
			if (dl) {
				jSplitPane.setDividerLocation(divider);
			} else {
				jSplitPane.setDividerLocation(0.6);
			}
		} catch (Exception e) {
			try {
				initInfoTextArea();
				infoTextArea.append(lineStr + e.getMessage() + "\n");
				conn.rollback();
			} catch (Exception e2) {
				LM.error(LM.Model.CS.name(), e2);
			}
		}
	}

	/**
	 * 设置选中的模式下的statement
	 * 
	 * @throws SQLException
	 */
	private void setStatement() throws SQLException {
		if (conn == null || conn.isClosed() || !ConnUtil.isConnected(conn)) {
			try {
				conn = ConnService.createConnection(serverbean);
				HHdbSession hhdbsession = new HHdbSession(conn,StartUtil.prefix);
				sessionPid = hhdbsession.getPid();
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
		if (schemabox != null && schemabox.getSelectedItem() != null) {
			String schema = schemabox.getSelectedItem().toString();
			try {
				QueryService.setEnvSql(conn, schema);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
	}

	/**
	 * 执行查询器里面的SQL
	 * 
	 * @param querySql
	 * @param selectPosiotion
	 * @param qt
	 */
	private void deriveQueries(String querySql, int selectPosiotion,
			QueryTokenizer qt) {
		refDropflag = false;
		int index = 0;
		int lastIndex = 0;
		Query tempQuery = null;
		boolean isFun = false;
		boolean isSingleSql = false;
		Query firstQuery = null;
		int queryCount = 0;
		StringBuffer lineStr = new StringBuffer();
		if (!querySql.contains(Constants.QUERY_DELIMITER)) {
			querySql = querySql + Constants.QUERY_DELIMITER;
		}
		while ((index = querySql.indexOf(Constants.QUERY_DELIMITER, index + 1)) != -1) {
			if (!excuflag) {
				break;
			}
			// 设置sql语句起始索引位置
			int position = index;
			while (position > lastIndex) {
				if (!qt.notInAnyToken(position)) {
					break;
				} else {
					position--;
				}
			}
			// 去掉SQL语句前面的空白
			while (position < index + 1) {
				if (querySql.charAt(position) == '\n'
						|| querySql.charAt(position) == ' ') {
					position++;
				} else {
					break;
				}
			}
			if (selectPosiotion >= 0) {
				position += selectPosiotion;
			}
			if (qt.notInAnyToken(index)) {
				QueryTokenizer qt1 = new QueryTokenizer();
				String substring = querySql.substring(lastIndex, index + 1)
						.trim();

				try {
					if (substring.startsWith("\\")) {
						substring = Pgsqlcmd.pgCommandHandler(substring);
					}
				} catch (Exception e) {
					String message = e.getMessage();
					try {
						if (!conn.isClosed()) {
							int line = queryUi.getLineByPosition(position);
							initInfoTextArea();
							infoTextArea.append("行" + line + ": " + message
									+ "\n");
							conn.rollback();
						}
					} catch (Exception e2) {
						LM.error(LM.Model.CS.name(), e2);
					}
					return;
				}

				Query query = new Query(substring);
				String noCommentsSql;
				if (!isFun) {
					tempQuery = query;
					tempQuery.setPosition(position);
					noCommentsSql = qt1.removeAllCommentsFromQuery(tempQuery
							.getOriginalQuery());
					tempQuery.setDerivedQuery(noCommentsSql);
				} else {
					noCommentsSql = qt1.removeAllCommentsFromQuery(query
							.getOriginalQuery());
				}
				String upperNoCommentsSql = noCommentsSql.toUpperCase().trim();
				if (upperNoCommentsSql.indexOf("CREATE ") == 0
						&& upperNoCommentsSql.indexOf(" FUNCTION ") > 0
						|| isFun) {
					String originalQuery = tempQuery.getOriginalQuery();
					String derivedQuery = tempQuery.getDerivedQuery();
					if (SqlUtil.isFuncEnd(derivedQuery)) {
						tempQuery.setExcuteType(ExecuteType.CREATE);
						tempQuery.setObjType(ObjType.FUNCTION);
						isFun = false;
					} else {
						Query tempQuery1 = query;
						if (isFun) {
							String noCommentsSql1 = qt1
									.removeAllCommentsFromQuery(tempQuery1
											.getOriginalQuery());
							derivedQuery += Constants.QUERY_DELIMITER
									+ noCommentsSql1;
							originalQuery += tempQuery1.getOriginalQuery();
						}
						if (SqlUtil.isFuncEnd(derivedQuery)) {
							tempQuery.setDerivedQuery(derivedQuery);
							tempQuery.setOriginalQuery(originalQuery);
							tempQuery.setExcuteType(ExecuteType.CREATE);
							tempQuery.setObjType(ObjType.FUNCTION);
							isFun = false;
						} else {
							isFun = true;
							tempQuery.setDerivedQuery(derivedQuery);
							tempQuery.setOriginalQuery(originalQuery);
							lastIndex = index + 1;
							continue;
						}
					}
				} else {
					if ((upperNoCommentsSql.indexOf("SELECT ") == 0 || upperNoCommentsSql
							.indexOf("SELECT\n") == 0)
							&& (upperNoCommentsSql.indexOf("INTO") < 0 || (upperNoCommentsSql
									.indexOf("INTO") > 0 && upperNoCommentsSql
									.indexOf("INTO") > upperNoCommentsSql
									.indexOf("FROM")))) {
						tempQuery.setExcuteType(ExecuteType.SELECT);
					} else if (upperNoCommentsSql.indexOf("INSERT ") == 0
							|| upperNoCommentsSql.indexOf("INSERT\n") == 0) {// 插入
						tempQuery.setExcuteType(ExecuteType.INSERT);
					} else if (upperNoCommentsSql.indexOf("UPDATE ") == 0
							|| upperNoCommentsSql.indexOf("UPDATE\n") == 0) {// 更新
						tempQuery.setExcuteType(ExecuteType.UPDATE);
					} else if ((upperNoCommentsSql.indexOf("DROP ") == 0 && upperNoCommentsSql
							.indexOf("TABLE") > 0)
							|| (upperNoCommentsSql.indexOf("DROP\n") == 0 && upperNoCommentsSql
									.indexOf("TABLE") > 0)) {// 删除表
						tempQuery.setExcuteType(ExecuteType.DROP);
						tempQuery.setObjType(ObjType.TABLE);
					} else if ((upperNoCommentsSql.indexOf("DROP ") == 0)
							|| (upperNoCommentsSql.indexOf("DROP\n") == 0)) {// 删除操作
						tempQuery.setExcuteType(ExecuteType.DROP);
					}else if (upperNoCommentsSql.indexOf("DELETE ") == 0
							|| upperNoCommentsSql.indexOf("DELETE\n") == 0) {// 删除
						tempQuery.setExcuteType(ExecuteType.DELETE);
					} else if ((upperNoCommentsSql.indexOf("CREATE ") == 0 && upperNoCommentsSql
							.indexOf(" TABLE ") > 0)
							|| (upperNoCommentsSql.indexOf("CREATE\n") == 0 && upperNoCommentsSql
									.indexOf("TABLE ") > 0)) {// 创建表
						tempQuery.setExcuteType(ExecuteType.CREATE);
						tempQuery.setObjType(ObjType.TABLE);
					} else if ((upperNoCommentsSql.indexOf("CREATE ") == 0 && upperNoCommentsSql
							.indexOf(" DATABASE ") > 0)
							|| (upperNoCommentsSql.indexOf("CREATE\n") == 0 && upperNoCommentsSql
									.indexOf("DATABASE ") > 0)) {// 创建数据库
						tempQuery.setExcuteType(ExecuteType.CREATE);
						tempQuery.setObjType(ObjType.DATABASE);
					} else if ((upperNoCommentsSql.indexOf("CREATE ") == 0 && upperNoCommentsSql
							.indexOf(" TABLESPACE ")>0)
							|| (upperNoCommentsSql.indexOf("CREATE\n") == 0 && upperNoCommentsSql
							.indexOf("TABLESPACE ")>0)) {// 创建表空间
						tempQuery.setExcuteType(ExecuteType.CREATE);
						tempQuery.setObjType(ObjType.TABLESPACE);
					} else if ((upperNoCommentsSql.indexOf("CREATE ") == 0 && upperNoCommentsSql
							.indexOf(" SEQUENCE ") > 0)
							|| (upperNoCommentsSql.indexOf("CREATE\n") == 0 && upperNoCommentsSql
									.indexOf("SEQUENCE ") > 0)) {// 创建序列
						tempQuery.setExcuteType(ExecuteType.CREATE);
						tempQuery.setObjType(ObjType.SEQUENCE);
					} else if ((upperNoCommentsSql.indexOf("CREATE ") == 0 && upperNoCommentsSql
							.indexOf("TRIGGER") > 0)
							|| (upperNoCommentsSql.indexOf("CREATE\n") == 0 && upperNoCommentsSql
									.indexOf("TRIGGER") > 0)) {// 创建触发器
						tempQuery.setExcuteType(ExecuteType.CREATE);
						tempQuery.setObjType(ObjType.TRIGGER);
					} else if (upperNoCommentsSql.indexOf("EXECUTE ") == 0
							|| upperNoCommentsSql.indexOf("EXECUTE\n") == 0) {// 执行
						tempQuery.setExcuteType(ExecuteType.EXECUTE);
					} else if (upperNoCommentsSql.indexOf("BEGIN") == 0) {// 打开事物
						tempQuery.setExcuteType(ExecuteType.BEGIN);
					} else if (upperNoCommentsSql.indexOf("COMMIT") == 0) {// 提交
						tempQuery.setExcuteType(ExecuteType.COMMIT);
					} else if (upperNoCommentsSql.indexOf("ROLLBACK") == 0) {// 回滚
						tempQuery.setExcuteType(ExecuteType.ROLLBACK);
					} else if (upperNoCommentsSql.indexOf("EXPLAIN") == 0) {// 查询规划
						tempQuery.setExcuteType(ExecuteType.EXPLAIN);
					} else if (upperNoCommentsSql.indexOf("WITH") == 0
							&& upperNoCommentsSql.indexOf(" RECURSIVE") > 0) {// 递归查询
						tempQuery.setExcuteType(ExecuteType.RECURSIVE);
					}
					tempQuery.setDerivedQuery(noCommentsSql.trim());
				}
				if (queryCount == 0) {
					firstQuery = tempQuery;
					isSingleSql = true;
				} else {
					isSingleSql = false;
					try {
						if (queryCount == 1) {
							initInfoTextArea();
							dealMultiSql(lineStr, firstQuery);
						}
						lineStr.delete(0, lineStr.length());
						dealMultiSql(lineStr, tempQuery);
					} catch (Exception e) {
						String message = e.getMessage();
						if (message
								.contains("canceling statement due to user request")) {
							message = "强制中断sql执行";
						}
						try {
							if (!conn.isClosed()) {
								infoTextArea.append(lineStr + message + "\n");
								conn.rollback();
							}
						} catch (Exception e2) {
							LM.error(LM.Model.CS.name(), e2);
						}
						break;
					}
				}
				queryCount += 1;
				lastIndex = index + 1;
			}
		}
		if (!excuflag) {
			try {
				conn.rollback();
				return;
			} catch (SQLException e) {
				LM.error(LM.Model.CS.name(), e);
			}
		}
		if (isSingleSql) {
			try {
				dealSingleSql(lineStr, firstQuery);
			} catch (Exception e) {
				String message = e.getMessage();
				if (message.contains("canceling statement due to user request")) {
					message = "强制中断sql执行";
				}
				try {
					if (!conn.isClosed()) {
						infoTextArea.append(lineStr + message + "\n");
						conn.rollback();
					}
				} catch (Exception e2) {
					LM.error(LM.Model.CS.name(), e2);
				}
			}
		} else {
			if (tempQuery.getExcuteType() == ExecuteType.SELECT
					|| tempQuery.getExcuteType() == ExecuteType.RECURSIVE) {
				pageposition = 30;
				singleQuery = tempQuery;
				showQueryData(pageposition, tempQuery);
			}
		}
		if (autocommitbox.getSelectedItem().toString().equals("自动提交")) {
			try {
				if (!conn.isClosed()) {
					if (QueryService.isCommit(sessionPid, queryPlugin)) {
						conn.commit();
						if(refDropflag){
							HHEvent rfschemaEv = new CmdEvent(queryPlugin.PLUGIN_ID, "com.hhdb.csadmin.plugin.tree", "RefreshTreeNodeByQueryDropEvent");
							queryPlugin.sendEvent(rfschemaEv);
						}
					} else {
						if (QueryService.isTransactionError(sessionPid,
								queryPlugin)) {
							conn.rollback();
						}
					}
				}
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				infoTextArea.append("提交失败！"+e.getMessage()+"\n");
			}
		} else {
			if (QueryService.isCommit(sessionPid, queryPlugin)) {
				commitBtn.setEnabled(true);
				rollbackBtn.setEnabled(true);
				autocommitbox.setEnabled(false);
			} else if (QueryService.isTransactionError(sessionPid, queryPlugin)) {
				try {
					conn.rollback();
				} catch (SQLException e) {
					LM.error(LM.Model.CS.name(), e);
				}
			}
		}
	}

	/**
	 * 只有一行sql操作
	 * 
	 * @param lineStr
	 * @param firstQuery
	 * @throws IOException
	 * @throws JDBCException
	 */
	private void dealSingleSql(StringBuffer lineStr, Query firstQuery)
			throws Exception {
		int line = queryUi.getLineByPosition(firstQuery.getPosition());
		lineStr.append("行" + line + ": ");
		singleQuery = firstQuery;
		if (firstQuery.getExcuteType() == ExecuteType.SELECT
				|| firstQuery.getExcuteType() == ExecuteType.RECURSIVE) {
			pageposition = 30;
			pagenum=0;
			showQueryData(pageposition, firstQuery);
		} else {
			initInfoTextArea();
			if (firstQuery.getExcuteType() == ExecuteType.EXPLAIN) {
				showExplainData(firstQuery);
			} else if (firstQuery.getExcuteType() == ExecuteType.UPDATE) {
				int count = SqlExeUtil.executeUpdate(conn,
						firstQuery.getDerivedQuery());
				infoTextArea.append(lineStr + "更新成功" + count + "条记录！\n");
			} else if (firstQuery.getExcuteType() == ExecuteType.INSERT) {
				int count = SqlExeUtil.executeUpdate(conn,
						firstQuery.getDerivedQuery());
				infoTextArea.append(lineStr + "插入成功" + count + "条记录！\n");
			}else if (firstQuery.getExcuteType() == ExecuteType.DROP) {
				int count = SqlExeUtil.executeUpdate(conn,
						firstQuery.getDerivedQuery());
				infoTextArea.append(lineStr + "删除成功" + count + "条记录！\n");
				refDropflag = true;
			} 
			else if (firstQuery.getExcuteType() == ExecuteType.BEGIN) {
				try {
					SqlExeUtil
							.executeUpdate(conn, firstQuery.getDerivedQuery());
					infoTextArea.append(lineStr + "打开事物成功！\n");
					autocommitbox.setSelectedItem("手动提交");
				} catch (Exception e) {
					infoTextArea.append(lineStr + "打开事物失败！" + e.getMessage()
							+ "\n");
				}
			} else if (firstQuery.getExcuteType() == ExecuteType.ROLLBACK) {
				try {
					SqlExeUtil
							.executeUpdate(conn, firstQuery.getDerivedQuery());
					commitBtn.setEnabled(false);
					rollbackBtn.setEnabled(false);
					infoTextArea.append(lineStr + "回滚成功！\n");
					autocommitbox.setEnabled(true);
				} catch (Exception e) {
					infoTextArea.append(lineStr + "回滚失败！" + e.getMessage()
							+ "\n");
				}
			} else if (firstQuery.getExcuteType() == ExecuteType.COMMIT) {
				try {
					SqlExeUtil
							.executeUpdate(conn, firstQuery.getDerivedQuery());
					commitBtn.setEnabled(false);
					rollbackBtn.setEnabled(false);
					infoTextArea.append(lineStr + "提交成功！\n");
					autocommitbox.setEnabled(true);
				} catch (Exception e) {
					infoTextArea.append(lineStr + "提交失败！" + e.getMessage()
							+ "\n");
				}
			} else if (firstQuery.getExcuteType() == ExecuteType.CREATE
					&& firstQuery.getObjType() == ObjType.FUNCTION) {
				
				SqlExeUtil.executeUpdate(conn, firstQuery.getDerivedQuery());
				infoTextArea.append(lineStr + "创建函数成功！\n");
				
			} else if (firstQuery.getExcuteType() == ExecuteType.CREATE
					&& (firstQuery.getObjType() == ObjType.DATABASE
					||firstQuery.getObjType() == ObjType.TABLESPACE)) {
				Connection co = null;
				try {
					co = ConnService.createConnection(serverbean);
					SqlExeUtil.executeUpdate(co, firstQuery.getDerivedQuery());
					if(firstQuery.getObjType() == ObjType.DATABASE){
						infoTextArea.append(lineStr + "创建数据库成功！\n");
					}else{
						infoTextArea.append(lineStr + "创建表空间成功！\n");
					}
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					throw e;
				} finally {
					if (co != null) {
						co.close();
					}
				}
			} else {
				int count = SqlExeUtil.executeUpdate(conn,
						firstQuery.getDerivedQuery());
				infoTextArea.append(lineStr + "执行成功" + count + "条记录！\n");
			}
		}
	}

	/**
	 * 执行多行sql操作
	 * 
	 * @param lineStr
	 * @param query
	 * @throws IOException
	 * @throws JDBCException
	 */
	private void dealMultiSql(StringBuffer lineStr, Query query)
			throws Exception {
		int line = queryUi.getLineByPosition(query.getPosition());
		lineStr.append("行" + line + ": ");

		if (query.getExcuteType() == ExecuteType.UPDATE) {
			int count = SqlExeUtil.executeUpdate(conn, query.getDerivedQuery());
			infoTextArea.append(lineStr + "更新成功" + count + "条记录！\n");
		} else if (query.getExcuteType() == ExecuteType.INSERT) {
			int count = SqlExeUtil.executeUpdate(conn, query.getDerivedQuery());
			infoTextArea.append(lineStr + "插入成功" + count + "条记录！\n");
		} else if (query.getExcuteType() == ExecuteType.SELECT
				|| query.getExcuteType() == ExecuteType.RECURSIVE) {
			int count = SqlQueryUtil.getCountSql(conn, query.getDerivedQuery());
			infoTextArea.append(lineStr + "查询成功返回" + count + "条记录！\n");
		} else if (query.getExcuteType() == ExecuteType.EXPLAIN) {
			int count = SqlQueryUtil.getCountSql(conn, query.getDerivedQuery());
			infoTextArea.append(lineStr + "查询成功返回" + count + "条记录！\n");
		} else if (query.getExcuteType() == ExecuteType.CREATE
				&& query.getObjType() == ObjType.FUNCTION) {
			SqlExeUtil.executeUpdate(conn, query.getDerivedQuery());
			infoTextArea.append(lineStr + "创建函数成功！\n");
		} else if (query.getExcuteType() == ExecuteType.BEGIN) {
			try {
				infoTextArea.append(lineStr + "打开事物成功！\n");
				autocommitbox.setSelectedItem("手动提交");
			} catch (Exception e) {
				infoTextArea
						.append(lineStr + "打开事物失败！" + e.getMessage() + "\n");
			}
		} else if (query.getExcuteType() == ExecuteType.ROLLBACK) {
			try {
				SqlExeUtil.executeUpdate(conn, query.getDerivedQuery());
				commitBtn.setEnabled(false);
				rollbackBtn.setEnabled(false);
				infoTextArea.append(lineStr + "回滚成功！\n");
				autocommitbox.setEnabled(true);
			} catch (Exception e) {
				infoTextArea.append(lineStr + "回滚失败！" + e.getMessage() + "\n");
			}
		} else if (query.getExcuteType() == ExecuteType.COMMIT) {
			try {
				SqlExeUtil.executeUpdate(conn, query.getDerivedQuery());
				commitBtn.setEnabled(false);
				rollbackBtn.setEnabled(false);
				infoTextArea.append(lineStr + "提交成功！\n");
				autocommitbox.setEnabled(true);
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				infoTextArea.append(lineStr + "提交失败！" + e.getMessage() + "\n");
			}
		} 
		else if (query.getExcuteType() == ExecuteType.CREATE
				&& (query.getObjType() == ObjType.DATABASE
				||query.getObjType() == ObjType.TABLESPACE)) {
			Connection co = null;
			try {
				co = ConnService.createConnection(serverbean);
				SqlExeUtil.executeUpdate(co, query.getDerivedQuery());
				if(query.getObjType() == ObjType.DATABASE){
					infoTextArea.append(lineStr + "创建数据库成功！\n");
				}else{
					infoTextArea.append(lineStr + "创建表空间成功！\n");
				}
			}catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				throw e;
			} finally {
				if (co != null) {
					co.close();
				}
			}
		}
		else if (query.getExcuteType() == ExecuteType.DROP) {
			int count = SqlExeUtil.executeUpdate(conn,
					query.getDerivedQuery());
			infoTextArea.append(lineStr + "删除成功" + count + "条记录！\n");
			refDropflag = true;
		} 
		else {
			int count = SqlExeUtil.executeUpdate(conn, query.getDerivedQuery());
			infoTextArea.append(lineStr + "执行成功" + count + "条记录！\n");
		}
	}
}
