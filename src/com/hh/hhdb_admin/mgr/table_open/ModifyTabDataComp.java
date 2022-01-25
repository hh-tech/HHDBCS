package com.hh.hhdb_admin.mgr.table_open;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.painter.PainterSupport;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.dbobj2.version.VersionBean;
import com.hh.frame.dbobj2.version.VersionUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.HTipTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.SeqCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.frame.swingui.view.tab.col.json.JsonColEditor;
import com.hh.frame.swingui.view.tab.menu.body.ExpTabBodyPopMenu;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import com.hh.hhdb_admin.mgr.table_open.common.InsertSqlBean;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyConstant;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;
import com.hh.hhdb_admin.mgr.table_open.comp.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * @author ouyangxu
 * @date 2020-12-22
 * @description 表格查看/编辑
 */
public class ModifyTabDataComp extends LastPanel {
	public static final String LOG_NAME = ModifyTabDataComp.class.getSimpleName();
	/**
	 * 连接信息
	 */
	private Connection connection;
	private JdbcBean jdbc;
	private String schemaName;
	private String tabName;
	private String selectSql;
	private String readOnlySelectSql;

	private DBTypeEnum dbType;
	private File tmpDir;

	private HTable tab = null;
	protected List<AbsCol> columns;
	protected List<TableColumn> jTableColumns;
	protected TableSearchToolBar searchToolBar;

	private HHPagePanel pagePanel;
	private HHOperateTablePanel opToolBarPanel;
	private LabelInput status;
	private QueryTool queryTool;
	private ModifyTabTool tabColTool;
	private SqlViewDialog sqlViewDialog;
	boolean readOnly = false;
	private Long start;
	//	private Map<String, String> columnTypeMap;
	private List<Map<String, String>> data;
	private boolean isChange = false;

	/**
	 * 显示行号
	 */
	protected boolean showNumber = true;
	/**
	 * 自动列宽
	 */
	protected boolean autoResizeCol = false;

	/**
	 * 横向显示
	 */
	protected boolean isRowStyle = true;

	/**
	 * 显示内边框
	 */
	protected boolean showLine = true;

	protected int rowHeight = 30;

	/**
	 * 显示where条件sql文本框
	 */
	protected boolean showWhereSqlInput = true;
	protected WhereTextInput whereTextInput;
	private String whereSql;

	/**
	 * 分页
	 */
	protected long limit = 30;
	protected long offset = 0;
	protected long page = 1;
	protected boolean hasNext = false;
	protected long tableCount = -1;
	protected long totalPages = 0;
	protected String tableCountTips = "";
	private long tmpTableCount = -1;
	private long tmpMaxPage = 1;
	//private boolean selectTableCount = false;

	/**
	 * SwingWork 进度条等
	 */
	protected static ExecutorService executor;
	protected Timer timer;
	protected InitConnTask connTask;
	protected SelectDataTask selectDataWorker;
	protected SelectCountTask selectCountTask;
	protected ProgressBar progressBar;
	protected WebLabel progressTimeLabel;
	protected JPanel progressPanel;
	private String timeTips;
	private static final int MIN_PROGRESS = 0;
	private static final int MAX_PROGRESS = 100;
	private static int currentProgress = MIN_PROGRESS;

	private ColListSelectionListener colListSelectionListener;

	private final static String ERROR_TIPS = "获取数据错误!";

	private String nullSymbol = "";

	public ModifyTabDataComp() {
	}

	public ModifyTabDataComp(JdbcBean jdbc, String tabName, File tmpDir) {
		this(jdbc, SelectTableSqlUtil.getSchema(DriverUtil.getDbType(jdbc), jdbc), tabName, tmpDir);
	}

	public ModifyTabDataComp(JdbcBean jdbc, String schemaName, String tabName, File tmpDir) {
		init(jdbc, schemaName, tabName, tmpDir);
	}


	public void init(JdbcBean jdbc, String schemaName, String tabName, File tmpDir) {
		try {
			this.jdbc = jdbc;
			this.schemaName = schemaName;
			this.tabName = tabName;
			this.dbType = DriverUtil.getDbType(jdbc);
			this.tmpDir = tmpDir;

			JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
			JsonValue aNull = fileJsonArr.get("null");
			nullSymbol = aNull != null ? aNull.asString() : nullSymbol;

			initProgressBar();
			set(progressPanel);
		} catch (Exception exception) {
			exception.printStackTrace();
			setWithScroll(new LabelInput(ERROR_TIPS, AlignEnum.CENTER).getComp());
			PopPaneUtil.error(exception.getMessage());
		}
	}

	public void close() {
		try {
			if (connTask != null && !connTask.isDone()) {
				connTask.cancel(true);
			}
			if (selectDataWorker != null && !selectDataWorker.isDone()) {
				selectDataWorker.cancel(true);
			}
			ConnUtil.close(connection);
			FileUtils.deleteQuietly(tmpDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void refreshTab() {
		refreshTab(this.tabName);
	}

	/**
	 * 刷新表格
	 *
	 * @param tabName 表名
	 * @throws Exception
	 */
	public synchronized void refreshTab(String tabName) {
		try {
			startProgress();
			if (connection == null) {
				connTask = new InitConnTask() {
					@Override
					protected void done() {
						connection = super.conn;
						selectData(tabName);
					}
				};
				executor.execute(connTask);
			} else {
				selectData(tabName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//setWithScroll(tab.getComp());
			PopPaneUtil.error(e);
		}
	}

	/**
	 * 初始化进度条
	 */
	private void initProgressBar() {
		if (TableOpenMgr.threadPool == null) {
			TableOpenMgr.initPool();
		}

		executor = TableOpenMgr.threadPool;
		progressTimeLabel = new WebLabel();
		progressTimeLabel.setPreferredWidth(200);
		progressTimeLabel.setIcon(ModifyTabTool.getIcon(ModifyConstant.TIME_ICON));
		progressTimeLabel.setMargin(0, 10, 0, 0);
		progressBar = new ProgressBar();
		progressBar.getComp().setPreferredSize(new Dimension(300, 15));
		progressBar.setIndeterminate(true);
		progressPanel = new JPanel(new GridBagLayout());
		progressPanel.add(progressBar.getComp());
		progressPanel.add(progressTimeLabel);
	}

	/**
	 * 启动进度条
	 */
	private void startProgress() {
		currentProgress = 0;
		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
		progressTimeLabel.setText("");
		TooltipManager.setTooltip(progressPanel, "正在加载,请稍等..");
		start = System.currentTimeMillis();
		if (timer == null) {
			timer = new Timer(50, new ProgressTimerListener());
		} else if (timer.isRunning()) {
			timer.stop();
		}
		timer.start();
		set(progressPanel);
	}

	/**
	 * 查询数据
	 *
	 * @param tabName
	 */
	protected void selectData(String tabName) {
		if (StringUtils.isNoneBlank(readOnlySelectSql)) {
			loadReadOnlyTable(readOnlySelectSql);
		} else {
			VersionBean dbVersion = VersionUtil.getDbVersion(connection);
			this.tabName = tabName;
			this.selectSql = SelectTableSqlUtil.getSelectSql(dbType, dbVersion, null, schemaName, tabName, page, limit, offset, getWhereSql());
			this.selectSql = SqlStrUtil.removeSemi(selectSql);
			tabColTool = new ModifyTabTool(dbType, tab) {
				@Override
				protected void SaveLobCallBack() {
					super.SaveLobCallBack();
					refreshTab();
				}
			};
			tabColTool.setReadOnly(false);
			tabColTool.setDbVersion(dbVersion);
			selectDataWorker = new SelectDataTask(selectSql) {
				@Override
				protected void done() {
					data = super.mapList;
					load(data);
					if (tableCount == -1 && data.size() > 0) {
						doSelectCount();
					}
				}
			};
			executor.execute(selectDataWorker);
		}
	}

	/**
	 * 错误处理
	 *
	 * @param e
	 * @param errorTips
	 */
	protected void setErrorMessage(Exception e, String errorTips) {
//		progressBar.getComp().set
		e.printStackTrace();
		if (tab != null) {
			setWithScroll(tab.getComp());
		} else {
			JPanel contentPanel = new JPanel(new GridBagLayout());
			WebPanel tipsPanel = new WebPanel(new FlowLayout());
			tipsPanel.setMaximumWidth(800);
			WebLabel errorLabel = new WebLabel(errorTips);
			WebButton reloadBtn = new WebButton("重新加载", e1 -> {
				refreshTab();
			});
			reloadBtn.setMaximumWidth(300);
			tipsPanel.add(errorLabel, reloadBtn);
			contentPanel.add(tipsPanel);
			set(contentPanel);
		}
		PopPaneUtil.error(e);
		setPageStatus();
		setSaveBtnStatus();
		opToolBarPanel.getRefreshData().setEnabled(true);
	}


	/**
	 * 获取只读表格
	 *
	 * @param sql sql语句
	 */
	public void loadReadOnlyTable(String sql) {
		this.readOnlySelectSql = sql;
		try {
			startProgress();
			if (connection == null) {
				connTask = new InitConnTask() {
					@Override
					protected void done() {
						connection = super.conn;
						selectReadOnlyData(readOnlySelectSql);
					}
				};
				executor.execute(connTask);
			} else {
				selectReadOnlyData(readOnlySelectSql);
			}
		} catch (Exception e) {
			e.printStackTrace();
			setWithScroll(new LabelInput(ERROR_TIPS, AlignEnum.CENTER).getComp());
			PopPaneUtil.error(e);
		}
	}

	/**
	 * 查询只读数据
	 *
	 * @param sql sql
	 */
	protected void selectReadOnlyData(String sql) {
		start = System.currentTimeMillis();
		readOnly = true;
		VersionBean dbVersion = VersionUtil.getDbVersion(connection);
		this.selectSql = SelectTableSqlUtil.getSelectSql(dbType, dbVersion, sql, schemaName, tabName, page, limit, offset, getWhereSql());
		this.selectSql = SqlStrUtil.removeSemi(selectSql);
		tabColTool = new ModifyTabTool(dbType, tab);
		tabColTool.setReadOnly(true);
		selectDataWorker = new SelectDataTask(selectSql) {
			@Override
			protected void done() {
				data = super.mapList;
				load(data);
				tab.setCellEditable(!readOnly);
			}
		};
		executor.execute(selectDataWorker);
	}

	/**
	 * 将指定的数据加载到表格中
	 *
	 * @param data 数据集合
	 */
	private void load(List<Map<String, String>> data) {
		if (queryTool.getColNames() == null || queryTool.getSelTypes() == null) {
			return;
		}
		if (tab != null) {
			//是否显示序号列
			AbsCol column = tab.getColumn(SeqCol.name);
			if (column == null) {
				showNumber = false;
			} else {
				showNumber = column.isShow();
			}
			autoResizeCol = tab.isAutoResizeCol();
			isRowStyle = tab.isRowStyle();
			rowHeight = tab.getComp().getRowHeight();
			showLine = tab.getComp().getShowHorizontalLines() && tab.getComp().getShowVerticalLines();

			jTableColumns = new ArrayList<>();
			int columnCount = tab.getComp().getColumnModel().getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				jTableColumns.add(tab.getComp().getColumnModel().getColumn(i));
			}
			nullSymbol = tab.getNullSymbol();
		}
		tab = new HTipTable() {
			/**
			 * 添加delete键删除选中行事件
			 */
			@Override
			protected void addDelKeyListener() {
				final String deleteAction = "delete";
				InputMap inputMap = jTab.getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
				ActionMap actionMap = jTab.getActionMap();
				inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteAction);
				actionMap.put(deleteAction, new AbstractAction() {
					private static final long serialVersionUID = -679276663210720663L;

					@Override
					public void actionPerformed(ActionEvent e) {
						delRow();
					}
				});
			}
		};
		tab.setAutoResizeCol(autoResizeCol);
		tab.setRowHeight(rowHeight);
		tab.setNullSymbol(nullSymbol);
		tab.setShowHorLine(showLine);
		tab.setShowVerLine(showLine);
		HMenuItem refreshMenuItem = new HMenuItem("刷新");
		refreshMenuItem.getComp().addActionListener(e -> {
			refreshTab();
		});
		ExpTabBodyPopMenu expTabBodyPopMenu = new ExpTabBodyPopMenu();
		expTabBodyPopMenu.addItem(refreshMenuItem);
		tab.setRowPopMenu(expTabBodyPopMenu);
		//初始化表格头和列
		addTableCol();
		//表格加载数据
		tab.load(data, 1);
		//删除已有表格和工具栏
		removeAll();
		//设置顶部
		initHead();
		//设置底部按钮栏
		setFoot(initToolBar().getComp());
		//设置底部栏信息
		setStatusAndListener();
		//设置中间表格
		setWithScroll(tab.getComp());
		//设置ToolTip显示时间
		ToolTipManager.sharedInstance().setDismissDelay(30000);
		if (!isRowStyle) {
			tab.setRowStyle(isRowStyle);
		}
		setColumnsWidth();
		tab.getComp().updateUI();
	}

	/**
	 * 设置头部组件
	 */
	protected void initHead() {
		if (showWhereSqlInput) {
			initWhereTextInput();
			HPanel headPanel = new HPanel(new HDivLayout(GridSplitEnum.C9));
			headPanel.add(whereTextInput, searchToolBar);
			PainterSupport.setMargin(headPanel.getComp(), 5, 0, 5, 0);
			setHead(headPanel.getComp());
		} else {
			PainterSupport.setMargin(searchToolBar.getComp(), 5, 0, 5, 0);
			setHead(searchToolBar.getComp());
		}
	}

	/**
	 * 初始化where sql筛选框
	 */
	private void initWhereTextInput() {
		whereTextInput = new WhereTextInput();
		WebTextField textField = (WebTextField) whereTextInput.getComp();
		if (StringUtils.isNoneBlank(whereSql)) {
			textField.setText(whereSql);
			textField.requestFocus();
		}
		//回车监听
		textField.addActionListener(e -> {
			if (isCancelSaveData()) {
				return;
			}
			String fieldText = textField.getText();
			String oldWhereSql = getWhereSql();
			if (getWhereSql() == null && fieldText != null) {
				page = 1;
				offset = 0;
			}
			setWhereSql(fieldText);
			refreshTab();
			boolean isSelectCount = StringUtils.isBlank(fieldText) || StringUtils.isNoneBlank(getWhereSql()) && StringUtils.isNoneBlank(fieldText) && !getWhereSql().equals(oldWhereSql);
			if (isSelectCount) {
				doSelectCount();
			}
		});
		//输入监听
		textField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			String fieldText = textField.getText();
			if (getWhereSql() != null && StringUtils.isBlank(fieldText)) {
				setWhereSql(fieldText);
			}
		});
		//sql按钮点击
		whereTextInput.getSqlButton().addActionListener(e -> {
			sqlViewDialog.setSql(selectSql);
			sqlViewDialog.show();
		});

	}

	/**
	 * 设置表头和列
	 */
	private void addTableCol() {
		tab.setRowStyle(true);
		searchToolBar = new TableSearchToolBar(tab) {
			@Override
			protected void typeActionPerformed(ActionEvent e) {
				super.typeActionPerformed(e);
				isRowStyle = htab.isRowStyle();
				setSaveBtnStatus();
			}
		};

		List<AbsCol> oldCols = columns == null ? null : new ArrayList<>(columns);

		this.columns = tabColTool.createCol(queryTool, jdbc, schemaName, tabName);
		AbsCol seqCol = tab.getColumn(SeqCol.name);
		if (seqCol != null) {
			seqCol.setShow(showNumber);
		}
		for (AbsCol absCol : this.columns) {
			if (absCol == null) {
				continue;
			}
			if (absCol instanceof LobJsonCol) {
				absCol.setWidth(160);
			} else if (SelectTableSqlUtil.getHideColNames().contains(absCol.getValue())) {
				absCol.setShow(false);
			}
			if (oldCols != null && oldCols.size() == this.columns.size()) {
				for (AbsCol column : oldCols) {
					if (column.getName().equals(absCol.getName())) {
						absCol.setShow(column.isShow());
						absCol.setWidth(column.getWidth());
						absCol.setMaxWidth(column.getMaxWidth());
					}
				}
			}
			tab.addCols(absCol);
		}
	}

	/**
	 * 刷新后设置上次的列宽
	 */
	protected void setColumnsWidth() {
		if (jTableColumns != null) {
			ArrayList<TableColumn> oldColumns = new ArrayList<>(jTableColumns);
			TableColumnModel columnModel = tab.getComp().getColumnModel();
			if (oldColumns.size() == columnModel.getColumnCount()) {
				for (int i = 0; i < oldColumns.size(); i++) {
					TableColumn tableColumn = columnModel.getColumn(i);
					TableColumn oldColumn = oldColumns.get(i);
					if (tableColumn.getHeaderValue().equals(oldColumn.getHeaderValue())) {
						tableColumn.setPreferredWidth(oldColumn.getPreferredWidth());
						tableColumn.setWidth(oldColumn.getWidth());
						tableColumn.setMaxWidth(oldColumn.getMaxWidth());
					}
				}
			}
		}
	}

	/**
	 * 从数据库查询数据
	 *
	 * @param sql 查询语句
	 * @return 数据集合
	 * @throws Exception
	 */
	private synchronized List<Map<String, String>> getDbData(String sql) throws Exception {
		try {
			if (!tmpDir.exists()) {
				tmpDir.mkdirs();
			}
			tmpDir = new File(tmpDir, String.valueOf(System.currentTimeMillis()));
			FileUtils.deleteQuietly(tmpDir);
			queryTool = new QueryTool(connection, sql, tmpDir, (int) (limit + 1));
			queryTool.next();
			//Thread.sleep(2000);
			List<Enum<?>> selTypes = queryTool.getSelTypes();
			List<String> colNames = queryTool.getColNames();
			if (!readOnly) {
				tabColTool.initColumnTypeMap(connection, schemaName, tabName);
			}
			List<Map<String, String>> dataMap = tabColTool.toDataMap(selTypes, colNames, queryTool.getCurrentFile());
			hasNext = dataMap.size() > 0 && dataMap.size() > limit;
			if (hasNext) {
				dataMap.remove(dataMap.size() - 1);
			}
			return dataMap;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 初始化底部工具类
	 *
	 * @return HPanel
	 */
	private HPanel initToolBar() {
		HPanel hPanel = null;
		if (!readOnly) {
			opToolBarPanel = new HHOperateTablePanel();
			hPanel = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C9));
		} else {
			hPanel = new HPanel(new HDivLayout());
		}
		Border emptyBorder = BorderFactory.createEmptyBorder(3, 2, 0, 3);

		pagePanel = new HHPagePanel(limit);
		pagePanel.getComp().setBorder(emptyBorder);

		status = pagePanel.getStatusLabel();
		if (opToolBarPanel != null) {
			hPanel.add(opToolBarPanel);
		}
		sqlViewDialog = new SqlViewDialog(tab.getComp());

		hPanel.add(pagePanel);
		return hPanel;
	}


	/**
	 * 设置按钮状态和点击事件监听
	 */
	private void setStatusAndListener() {
		//添加按钮监听
		//第一页
		pagePanel.getFirstPage().addActionListener(this::firstPageActionPerformed);
		//上一页
		pagePanel.getPrePage().addActionListener(this::prePageActionPerformed);
		//下一页
		pagePanel.getNextPage().addActionListener(this::nextPageActionPerformed);
		//改变分页大小
		pagePanel.getPageSizeComp().getComboBox().addActionListener(this::changeLimitActionPerformed);
		//最后一页
		pagePanel.getLastPage().addActionListener(this::lastPageActionPerformed);
		//获取总行数
		pagePanel.getTotalPageBtn().addActionListener(e -> doSelectCount());

		if (opToolBarPanel != null) {
			opToolBarPanel.getRefreshData().setEnabled(true);
			opToolBarPanel.getSaveData().setEnabled(false);
			opToolBarPanel.getCancel().setEnabled(false);
			opToolBarPanel.getDelRow().setEnabled(false);
			opToolBarPanel.getSqlView().setEnabled(false);
			opToolBarPanel.getAddRow().setEnabled(isRowStyle);

			opToolBarPanel.getRefreshData().addActionListener(this::refreshActionPerformed);
			opToolBarPanel.getAddRow().addActionListener(this::aAddRowAction);
			opToolBarPanel.getDelRow().addActionListener(e -> delRow());
			opToolBarPanel.getSaveData().addActionListener(this::saveActionPerformed);
			opToolBarPanel.getSqlView().addActionListener(this::sqlViewActionPerformed);
			opToolBarPanel.getCancel().addActionListener(this::cancelAction);
		}
		if (isRowStyle) {
			//表格点击事件监听
			tab.getComp().addMouseListener(new RowClickListener());
			//表格修改数据事件监听
			tab.getComp().addPropertyChangeListener(new TableChangeListener());
		}
		//设置按钮状态和提示栏信息
		setPageStatus();
		//添加表格ctrl+a全选事件
		ActionMap actionMap = tab.getComp().getActionMap();
		actionMap.put("selectAll", new SelectAllAction());
	}

	/**
	 * 设置保存,删除按钮状态
	 */
	private void setSaveBtnStatus() {
		if (opToolBarPanel != null) {
			opToolBarPanel.getSaveData().setEnabled(false);
			List<HTabRowBean> changedValue = tab.getRowBeans(RowStatus.UPDATE);
			List<HTabRowBean> addValue = tab.getRowBeans(RowStatus.ADD);
			List<HTabRowBean> delValue = tab.getRowBeans(RowStatus.DEL);
			boolean change = changedValue != null && changedValue.size() > 0;
			int lobCount = 0;
			for (HTabRowBean hTabRowBean : changedValue) {
				Map<String, String> currRow = hTabRowBean.getCurrRow();
				Set<String> keySet = currRow.keySet();
				for (String key : keySet) {
					String text = currRow.get(key);
					if (text != null && text.contains("\"__TEXT\": ") && text.contains("\"file_path\": ")) {
						lobCount++;
					}
				}
			}
			if (lobCount > 0 && changedValue.size() == lobCount) {
				change = false;
			}

			boolean add = addValue != null && addValue.size() > 0;
			boolean del = delValue != null && delValue.size() > 0;
			isChange = isRowStyle && (change || add || del);
			opToolBarPanel.getAddRow().setEnabled(isRowStyle);
			//保存按钮
			opToolBarPanel.getSaveData().setEnabled(isChange);
			//取消按钮
			opToolBarPanel.getCancel().setEnabled(isChange);
			//sql预览按钮
			opToolBarPanel.getSqlView().setEnabled(isChange);
			//删除按钮
			int size = tab.getSelectedRowBeans().size();
			opToolBarPanel.getDelRow().setEnabled(size > 0);
		}
	}

	/**
	 * 放弃更改
	 */
	private void cancelAction(ActionEvent actionEvent) {
		try {
			if (data != null && queryTool.getColNames() != null) {
				load(data);
			} else {
				tab.load(data, 1);
				setSaveBtnStatus();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			PopPaneUtil.error(exception);
		}
	}

	/**
	 * 弹出提示框:是否保存修改的内容
	 *
	 * @return true不修改  false为修改
	 */
	protected boolean isCancelSaveData() {
		if (isChange) {
			Object[] options = {"保存", "不保存", "取消"};
			final JDialog dialog = new JDialog();
			dialog.setAlwaysOnTop(dialog.isAlwaysOnTopSupported());
			int result = JOptionPane.showOptionDialog(dialog, "是否保存修改的内容?", "确认",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			if (result == JOptionPane.YES_OPTION) {
				saveData();
			} else if (result == JOptionPane.CLOSED_OPTION) {
				return true;
			} else {
				return result == JOptionPane.CANCEL_OPTION;
			}
		}
		return false;
	}

	/**
	 * 跳转到第一页
	 *
	 * @param e
	 */
	public void firstPageActionPerformed(ActionEvent e) {
		JButton comp = pagePanel.getFirstPage().getComp();
		boolean isEnabled = comp.isEnabled();
		try {
			if (isCancelSaveData()) {
				return;
			}
			if (isEnabled) {
				comp.setEnabled(false);
			}
			if (page > 1) {
				tabColTool.setTable(tab);
				page = 1;
				offset = 0;
				refreshTab(tabName);
			}

		} catch (Exception exception) {
			PopPaneUtil.error(exception);
			exception.printStackTrace();
		} finally {
			comp.setEnabled(isEnabled);
		}
	}

	/**
	 * 上一页点击事件
	 */
	protected void prePageActionPerformed(ActionEvent e) {
		JButton comp = pagePanel.getPrePage().getComp();
		boolean isEnabled = comp.isEnabled();
		try {
			if (isCancelSaveData()) {
				return;
			}
			if (isEnabled) {
				comp.setEnabled(false);
			}
			if (page > 1) {
				tabColTool.setTable(tab);
				page--;
				offset = Math.max(page - 1, 0) * limit;
				page = Math.max(page, 1);
				refreshTab(tabName);
			}

		} catch (Exception exception) {
			PopPaneUtil.error(exception);
			exception.printStackTrace();
		} finally {
			comp.setEnabled(isEnabled);
		}
	}


	/**
	 * 下一页点击事件
	 */
	protected void nextPageActionPerformed(ActionEvent e) {
		JButton comp = pagePanel.getNextPage().getComp();
		boolean isEnabled = comp.isEnabled();
		try {
			if (isCancelSaveData()) {
				return;
			}
			if (isEnabled) {
				comp.setEnabled(false);
			}
			if (hasNext) {
				offset = page * limit;
				tabColTool.setTable(tab);
				page++;
				refreshTab(tabName);
			}
		} catch (Exception exception) {
			PopPaneUtil.error(exception);
			exception.printStackTrace();
		} finally {
			comp.setEnabled(isEnabled);
		}
	}

	/**
	 * 最后一页
	 *
	 * @param e
	 */
	protected void lastPageActionPerformed(ActionEvent e) {
		JButton comp = pagePanel.getLastPage().getComp();
		boolean isEnabled = comp.isEnabled();
		try {
			if (isCancelSaveData()) {
				return;
			}
			if (isEnabled) {
				comp.setEnabled(false);
			}
			if (hasNext) {
				if (tableCount == -1) {
					setTableCountLabel();
				}
				page = (int) totalPages;
				offset = (page - 1) * limit;
				tabColTool.setTable(tab);
				refreshTab(tabName);
			}
		} catch (Exception exception) {
			PopPaneUtil.error(exception);
			exception.printStackTrace();
		} finally {
			comp.setEnabled(isEnabled);
		}
	}

	/**
	 * 设置加载时间和下一页等按钮状态
	 */
	private void setPageStatus() {
		pagePanel.getCurPageNum().setValue(String.valueOf(page));
		//pagePanel.getCurPageNum().setEnabled(false);
		pagePanel.getNextPage().setEnabled(hasNext);
		pagePanel.getLastPage().setEnabled(hasNext);
		pagePanel.getPrePage().setEnabled(page > 1);
		pagePanel.getFirstPage().setEnabled(page > 1);
		if (start == null) {
			start = System.currentTimeMillis();
		}
		status.setValue(" " + timeTips);
		status.getComp().setIcon(ModifyTabTool.getIcon(ModifyConstant.TIME_ICON));
		TooltipManager.setTooltip(status.getComp(), ModifyTabTool.getIcon(ModifyConstant.TIME_ICON), "加载时间:" + timeTips);

		setSaveBtnStatus();
		computeTmpTotalLines();
		String value = tableCount == -1 ? (tmpTableCount + "+") : tableCountTips;
		String tmpLimitTips = String.format("%s-%s", (offset + 1), (offset + data.size()));
		if (data.size() == 1) {
			tmpLimitTips = String.valueOf((offset + 1));
		}
		pagePanel.getPageTotalLabel().setValue(value);
		TooltipManager.setTooltip(pagePanel.getCurPageNum().getComp(), String.format("当前页数 : %s (%s 行)", page, tmpLimitTips));
		TooltipManager.setTooltip(pagePanel.getPageTotalLabel().getComp(), tableCount == 0 ? String.format("总行数: %s+ (点击获取)", tmpTableCount) : value);
		TooltipManager.setTooltip(pagePanel.getPageSizeComp().getComp(), String.format("分页设置 (%s)", limit));
	}

	/**
	 * 查询总行数(进度条显示)
	 */
	protected void doSelectCount() {
		//setTableCountLabel();
		pagePanel.replace(pagePanel.getPageTotalLabel(), pagePanel.getProgressBar());
		pagePanel.getProgressBar().setVisible(true);
		pagePanel.getProgressBar().setIndeterminate(true);
		pagePanel.getPageTotalLabel().setValue(tmpTableCount + "+");
		selectCountTask = new SelectCountTask() {
			@Override
			protected void done() {
				pagePanel.getProgressBar().setVisible(false);
				pagePanel.replace(pagePanel.getProgressBar(), pagePanel.getPageTotalLabel());
			}
		};
		executor.execute(selectCountTask);
	}

	/**
	 * 设置总行数Label
	 */
	protected void setTableCountLabel() {
		try {
			long start = System.currentTimeMillis();
			tableCount = getTableCount();
			computeTotalPages();
			long end = System.currentTimeMillis();
			pagePanel.getPageTotalLabel().setValue(tableCountTips);
			TooltipManager.setTooltip(pagePanel.getPageTotalLabel().getComp(), tableCountTips);
			System.out.printf("%s (耗时: %s ms%n)", tableCountTips, (end - start));
		} catch (Exception exception) {
			exception.printStackTrace();
			PopPaneUtil.error(exception);
		}
	}

	/**
	 * 计算总页数
	 */
	protected void computeTotalPages() {
		totalPages = tableCount / limit;
		if (tableCount % limit != 0) {
			totalPages++;
		}
		if (tableCount >= 0) {
			tableCountTips = String.format("共%s行(%s页)", tableCount, totalPages);
		} else {
			tableCountTips = "点击获取总行数";
		}
	}

	/**
	 * 计算临时表格行数
	 */
	protected void computeTmpTotalLines() {
		tmpMaxPage = Math.max(tmpMaxPage, page);
		if (tmpMaxPage == 1) {
			tmpTableCount = data.size();
		} else {
			if (data.size() == limit) {
				tmpTableCount = Math.max((limit * page), tmpTableCount);
			} else {
				long tmp = (limit * (page - 1)) + data.size();
				tmpTableCount = Math.max(tmp, tmpTableCount);
			}
		}
	}

	/**
	 * 改变分页大小监听
	 *
	 * @param e
	 */
	protected void changeLimitActionPerformed(ActionEvent e) {
		pagePanel.getPageSizeComp().getPopOver().dispose();
		Object selectedItem = pagePanel.getPageSizeComp().getComboBox().getSelectedItem();
		if (selectedItem == null) {
			return;
		}
		boolean isProper = true;
		if (SelectTableSqlUtil.isNumeric(selectedItem.toString())) {
			limit = Integer.parseInt(selectedItem.toString());
		} else {
			Object inputObj = JOptionPane.showInputDialog(this.getComp(), "请输入分页大小", "分页大小设置", JOptionPane.QUESTION_MESSAGE, null, null, limit);
			String input = inputObj == null ? "" : inputObj.toString();
			if (StringUtils.isNoneBlank(input)) {
				input = input.trim();
				if (SelectTableSqlUtil.isNumeric(input)) {
					if (Integer.parseInt(input) <= 0) {
						isProper = false;
						PopPaneUtil.error("分页大小不能为0!");
					} else {
						limit = Integer.parseInt(input);
					}
				} else {
					isProper = false;
					PopPaneUtil.error("请输入正整数!");
				}
			} else {
				pagePanel.getPageSizeComp().setValue(String.valueOf(limit));
				isProper = false;
			}
		}
		if (isProper) {
			offset = 0;
			page = 1;
			computeTotalPages();
			refreshTab(tabName);
		} else {
			pagePanel.getPageSizeComp().getComboBox().setSelectedItem(limit);
		}
	}

	/**
	 * 获取总行数
	 *
	 * @return 行数
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	protected long getTableCount() throws SQLException, ClassNotFoundException {
		try (Connection countConn = ConnUtil.getConn(jdbc)) {
			String countSql = SelectTableSqlUtil.getCountSql(dbType, schemaName, tabName, whereSql);
			List<String> selectOneColumn = SqlQueryUtil.selectOneColumn(countConn, countSql);
			if (selectOneColumn.size() > 0) {
				String count = selectOneColumn.get(0);
				return Long.parseLong(count);
			}
		}
		return 0;
	}

	/**
	 * 查询总行数
	 */
	class SelectCountTask extends SwingWorker<Boolean, Void> {

		@Override
		protected Boolean doInBackground() {
			setTableCountLabel();
			return true;
		}
	}

	/**
	 * 获取连接
	 */
	class InitConnTask extends SwingWorker<Boolean, Void> {
		protected Connection conn;

		@Override
		protected Boolean doInBackground() {
			Boolean flag = null;
			try {
				conn = ConnUtil.getConn(jdbc);
				flag = true;
			} catch (Exception e) {
				flag = false;
				setErrorMessage(e, "获取连接失败!");
			}
			return flag;
		}
	}

	/**
	 * 查询数据
	 */
	class SelectDataTask extends SwingWorker<Boolean, Void> {

		protected List<Map<String, String>> mapList;
		protected String selectSql;

		public SelectDataTask(String selectSql) {
			this.selectSql = selectSql;
		}

		@Override
		protected Boolean doInBackground() {
			Boolean flag = null;
			try {
				if (opToolBarPanel != null) {
					opToolBarPanel.getCompList().forEach(comp -> comp.getComp().setEnabled(false));
				}
				if (pagePanel != null) {
					pagePanel.getCompList().forEach(comp -> comp.getComp().setEnabled(false));
				}
				mapList = getDbData(selectSql);
			} catch (Exception e) {
				flag = false;
				setErrorMessage(e, ERROR_TIPS);
			}
			return flag;
		}
	}

	/**
	 * 改变进度条(需要改成出现错误停止)
	 */
	class ProgressTimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			currentProgress += 5;
			progressBar.setValue(currentProgress);
			boolean isStop = currentProgress >= MAX_PROGRESS;
			boolean isDone = (connTask != null && connTask.isDone()) && (selectDataWorker != null && selectDataWorker.isDone());
			if (isStop) {
				currentProgress = MIN_PROGRESS;
			}
			if (start == null) {
				start = System.currentTimeMillis();
			}
			double t = (System.currentTimeMillis() - start) / 1000.0;
			timeTips = (new DecimalFormat(t > 1 ? "#.##" : "#.###").format(t)) + "s";
			if (isDone) {
				((Timer) e.getSource()).stop();
			} else if (status != null) {
				status.setValue(" " + timeTips);
			}
			progressTimeLabel.setText(timeTips);
		}
	}


	/**
	 * 表格行点击事件
	 */
	private class RowClickListener extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent e) {
			setSaveBtnStatus();
		}
	}

	/**
	 * 表格修改数据事件监听
	 */
	private class TableChangeListener implements PropertyChangeListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (TableCellEditor.class.getSimpleName().equalsIgnoreCase(evt.getPropertyName().trim())) {
				boolean isOldLobJson = evt.getOldValue() instanceof JsonColEditor && ((JsonColEditor) evt.getOldValue()).getJsonCol() instanceof LobJsonCol;
				boolean isNewLobJson = evt.getNewValue() instanceof JsonColEditor && ((JsonColEditor) evt.getNewValue()).getJsonCol() instanceof LobJsonCol;
				if (!(isOldLobJson || isNewLobJson)) {
					setSaveBtnStatus();
				}
			}
		}
	}


	/**
	 * 刷新按钮点击事件
	 */
	public void refreshActionPerformed(ActionEvent e) {
		try {
			if (isCancelSaveData()) {
				return;
			}
			opToolBarPanel.getRefreshData().setEnabled(false);
			refreshTab();
		} catch (Exception exception) {
			PopPaneUtil.error(exception);
			exception.printStackTrace();
		}
	}

	public static class ColListSelectionListener implements ListSelectionListener {
		private final JTable jTable;

		public ColListSelectionListener(JTable jTable) {
			this.jTable = jTable;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			int selectedColumn = jTable.getSelectedColumn();
			int selectedRow = jTable.getSelectedRow();
			jTable.changeSelection(selectedRow, selectedColumn, false, false);
//			jTable.editCellAt(selectedRow, selectedColumn);
			Component component = jTable.getEditorComponent();
			if (component instanceof JTextField) {
				component.requestFocus();
				((JTextField) component).selectAll();
			}
		}
	}

	/**
	 * 添加按钮点击事件
	 */
	private void aAddRowAction(ActionEvent e) {
		//添加一行 并聚焦第一个单元格
		if (tab.getComp().isEditing()) {
			tab.getComp().getCellEditor().stopCellEditing();
		}
		tab.add(tab.getRowCount(), new HashMap<>());
		ModifyTabTool.requestFocus(tab, 1, colListSelectionListener);
		setSaveBtnStatus();
	}

	/**
	 * 保存按钮点击事件
	 */
	public void saveActionPerformed(ActionEvent e) {
		final JDialog dialog = new JDialog();
		dialog.setAlwaysOnTop(dialog.isAlwaysOnTopSupported());
		int result = JOptionPane.showConfirmDialog(dialog, "确定要提交修改吗?", "确认提交", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			saveData();
		}
	}

	/**
	 * Sql预览点击事件
	 */
	public void sqlViewActionPerformed(ActionEvent e) {
		try {
			if (tab.getComp().isEditing()) {
				tab.getComp().getCellEditor().stopCellEditing();
			}
			tabColTool.setTable(tab);
			List<String> listSql = tabColTool.getUpdateOrDelSql(queryTool, connection, schemaName, tabName);
			listSql.addAll(tabColTool.getAddSql(schemaName, tabName));
			StringBuilder sql = new StringBuilder();
			listSql.forEach(s -> {
				sql.append(s);
				if (StringUtils.isNoneBlank(s)) {
					sql.append(";\n");
				}
			});
			sqlViewDialog.setSql(sql.toString());
			sqlViewDialog.show();
		} catch (Exception exception) {
			setSaveBtnStatus();
			PopPaneUtil.error(exception);
			exception.printStackTrace();
		}
	}


	/**
	 * Ctrl+A全选事件
	 */
	private class SelectAllAction extends AbstractAction {

		private static final long serialVersionUID = 6647591494008147634L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (tab.getComp().getSelectedRowCount() == tab.getComp().getRowCount()) {
				tab.getComp().clearSelection();
			} else {
				tab.getComp().selectAll();
			}
			setSaveBtnStatus();
		}
	}

	/**
	 * 保存数据
	 */
	private void saveData() {
		Connection conn = null;
		opToolBarPanel.getSaveData().setEnabled(false);
		try {
			if (tab.getComp().isEditing()) {
				tab.getComp().getCellEditor().stopCellEditing();
			}
			tabColTool.setTable(tab);
			conn = ConnUtil.getConn(jdbc);
			tab.getComp().getColumnModel().getSelectionModel().removeListSelectionListener(colListSelectionListener);
			List<String> listSql = tabColTool.getUpdateOrDelSql(queryTool, conn, schemaName, tabName);
			List<InsertSqlBean> insertSqlBeans = tabColTool.buildInsertSqlBeans(schemaName, tabName);
			if (listSql.size() > 0 || insertSqlBeans.size() > 0) {
				tabColTool.doUpdate(conn, listSql, insertSqlBeans);
				refreshTab();
			} else {
				opToolBarPanel.getSaveData().setEnabled(false);
			}
			setSaveBtnStatus();
		} catch (Exception exception) {
			logUtil.error(LOG_NAME, exception);
			exception.printStackTrace();
			PopPaneUtil.error(exception);
			setSaveBtnStatus();
		} finally {
			ConnUtil.close(conn);
		}
	}

	/**
	 * 删除行
	 */
	private void delRow() {
		opToolBarPanel.getDelRow().setEnabled(false);
		try {
			if (tab.getComp().isEditing()) {
				tab.getComp().getCellEditor().stopCellEditing();
			}
			tabColTool.setTable(tab);
			int select = tab.getComp().getSelectedRows().length;
			if (select > 0) {
				final JDialog dialog = new JDialog();
				dialog.setAlwaysOnTop(dialog.isAlwaysOnTopSupported());
				int result = JOptionPane.showConfirmDialog(dialog, String.format("确定要删除 %s 条记录吗?", select), "确认删除", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					tab.deleteSelectRow();
					setSaveBtnStatus();
				}
			} else {
				PopPaneUtil.info("请选择要删除的记录");
			}
		} catch (Exception exception) {
			logUtil.error(LOG_NAME, exception);
			exception.printStackTrace();
			PopPaneUtil.error(exception);
			setSaveBtnStatus();
		}
	}

	public String getSchemaName() {
		return schemaName;
	}

	public String getTabName() {
		return tabName;
	}

	public long getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public HTable getTab() {
		return tab;
	}

	public boolean isShowWhereSqlInput() {
		return showWhereSqlInput;
	}

	public void setShowWhereSqlInput(boolean showWhereSqlInput) {
		this.showWhereSqlInput = showWhereSqlInput;
	}

	public String getWhereSql() {
		return whereSql;
	}

	public void setWhereSql(String whereSql) {
		this.whereSql = whereSql;
	}

}
