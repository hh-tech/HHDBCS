package com.hh.hhdb_admin.mgr.table_open;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.dbobj3.edit.bean.AbstractSqlBean;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInputExt;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.HTipTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.SeqCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.SqlViewDialog;
import com.hh.hhdb_admin.mgr.table_open.common.ExpTabPopMenu;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTableTool;
import com.hh.hhdb_admin.mgr.table_open.comp.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Deprecated
public class ModifyTabDataComp extends LastPanel {
	/**
	 * 连接信息
	 */
	private Connection connection;
	private JdbcBean jdbc;
	private String schemaName;
	private String parentTable;	//打开分区子表时主表的名称
	private String tabName;
	private String selectSql;
	private String readOnlySelectSql;

	private DBTypeEnum dbType;
	private DBTypeEnum.Version ver;
	private File tmpDir;

	private HTable tab = null;
	protected List<AbsCol> columns;
	protected List<TableColumn> jTableColumns;
	protected TableSearchToolBar searchToolBar;
	
	private HHPagePanel pagePanel;
	private HHOperateTablePanel opToolBarPanel;
	private LabelInput status;
	private QueryTool queryTool;
	private ModifyTableTool tableSqlTool;
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
	protected TextInputExt whereTextInput;
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

	/**
	 * SwingWork 进度条等
	 */
	protected static ExecutorService executor;
	protected Timer timer;
	protected InitConnTask connTask;
	protected SelectDataTask selectDataWorker;
	protected SelectCountTask selectCountTask;
	protected ProgressBar progressBar;
	protected LabelInput progressTimeLabel;
	protected JPanel progressPanel;
	private String timeTips;
	private static final int MIN_PROGRESS = 0;
	private static final int MAX_PROGRESS = 100;
	private static int currentProgress = MIN_PROGRESS;

	private ColListSelectionListener colListSelectionListener;

	private static final String ERROR_TIPS = LangMgr.sLang(CsL.FETCH_DATA_ERROR);

	private String nullSymbol = "";

	public ModifyTabDataComp(JdbcBean jdbc,  String tabName, File tmpDir) {
		this.jdbc = jdbc;
		this.dbType = DriverUtil.getDbType(jdbc);
		this.ver=jdbc.getCurrentVersion();
		if(StringUtils.isNoneBlank(jdbc.getSchema()))
			schemaName=SqlStrUtil.addQuote4DDL(jdbc.getSchema(), dbType, ver);
		
		this.tabName = tabName;
		this.ver=jdbc.getCurrentVersion();
		this.tmpDir = tmpDir;			
		nullSymbol= (CsVarUtil.CONF == null || null == CsVarUtil.CONF.getNullValue()) ? "<NULL>" : CsVarUtil.CONF.getNullValue();
		initProgressBar();
		set(progressPanel);
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
			Lg.error(ModifyTabDataComp.class, e);
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
						connection = conn;
						selectData(tabName);
					}
				};
				executor.execute(connTask);
			} else {
				selectData(tabName);
			}
		} catch (Exception e) {
			//setWithScroll(tab.getComp());
			Lg.error(ModifyTabDataComp.class, e);
			PopDlgUtil.error(e);
		}
	}

	/**
	 * 初始化进度条
	 */
	private void initProgressBar() {
		if (TableOpenMgr.threadPool == null) TableOpenMgr.initPool();
		executor = TableOpenMgr.threadPool;
		progressTimeLabel = new LabelInput();
		progressTimeLabel.getComp().setPreferredSize(new Dimension(200, 20));
		progressTimeLabel.setIcon(FaSolidEnum.CLOCK_ROTATE_LEFT,16);
		progressBar = new ProgressBar();
		progressBar.getComp().setPreferredSize(new Dimension(300, 15));
		progressBar.setIndeterminate(true);
		progressPanel = new JPanel(new GridBagLayout());
		progressPanel.add(progressBar.getComp());
		progressPanel.add(new JLabel(" "));
		progressPanel.add(progressTimeLabel.getComp());
	}

	/**
	 * 启动进度条
	 */
	private void startProgress() {
		currentProgress = 0;
		progressBar.setValue(0);
		progressBar.setIndeterminate(false);
		progressTimeLabel.setValue("");
//		TooltipManager.setTooltip(progressPanel, "正在加载,请稍等..");
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
//			VersionBean dbVersion = VersionUtil.getDbVersion(connection);
			this.tabName = tabName;
			this.selectSql = SelectTableSqlUtil.getSelectSql(dbType, ver, null, schemaName, tabName, page, limit, offset, getWhereSql());
//			this.selectSql = SqlStrUtil.removeSemi(selectSql);
			//tabColTool.setDbVersion(dbVersion);
			selectDataWorker = new SelectDataTask(selectSql, false) {
				@Override
				protected void done() {
					data = mapList;
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
	 * @param e
	 * @param errorTips
	 */
	protected void setErrorMessage(Exception e, String errorTips) {
		e.printStackTrace();
		if (tab != null) {
			setWithScroll(tab.getComp());
		} else {
			JPanel contentPanel = new JPanel(new GridBagLayout());
			JPanel tipsPanel = new JPanel(new FlowLayout());
			JLabel errorLabel = new JLabel(errorTips);
			JButton reloadBtn = new JButton(LangMgr.lang(Word4.RELOAD)) ;
			reloadBtn.addActionListener(e1 -> refreshTab());
			tipsPanel.add(errorLabel);
			tipsPanel.add(reloadBtn);
			
			contentPanel.add(tipsPanel);
			set(contentPanel);
		}
		PopDlgUtil.error(e);
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
						connection = conn;
						selectReadOnlyData(readOnlySelectSql);
					}
				};
				executor.execute(connTask);
			} else {
				selectReadOnlyData(readOnlySelectSql);
			}
		} catch (Exception e) {
			setWithScroll(new LabelInput(ERROR_TIPS, AlignEnum.CENTER).getComp());
			PopDlgUtil.error(e);
			Lg.error(ModifyTabDataComp.class, e);
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
//		VersionBean dbVersion = VersionUtil.getDbVersion(connection);
		this.selectSql = SelectTableSqlUtil.getSelectSql(dbType, ver, sql, schemaName, tabName, page, limit, offset, getWhereSql());
		this.selectSql = SqlStrUtil.removeSemi(selectSql);
		selectDataWorker = new SelectDataTask(selectSql, true) {
			@Override
			protected void done() {
				data = mapList;
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
				String deleteAction = "delete";
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
		tab.setColumnSelectionAllowed(true);
		HMenuItem refreshMenuItem = new HMenuItem(LangMgr.lang(Word2.REFRESH));
		refreshMenuItem.getComp().addActionListener(e -> {
			refreshTab();
		});
		ExpTabPopMenu expTabBodyPopMenu = new ExpTabPopMenu(queryTool.getSelTypes(),schemaName);
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
//		tab.getComp().updateUI();
	}

	/**
	 * 设置头部组件
	 */
	protected void initHead() {
		if (showWhereSqlInput) {
			initWhereTextInput();
			HDivLayout lay=new HDivLayout(GridSplitEnum.C9);
			lay.setTopHeight(5);
			lay.setBottomHeight(10);
			HPanel headPanel = new HPanel(lay);
			headPanel.add(whereTextInput, searchToolBar);
			setHead(headPanel.getComp());
		} else {
			setHead(searchToolBar.getComp());
		}
	}

	/**
	 * 初始化where sql筛选框
	 */
	private void initWhereTextInput() {
		HImgButton sqlBut = new HImgButton(){
			@Override
			protected void onClick() {
				sqlViewDialog.setSql(selectSql);
				sqlViewDialog.show();
			}
		};
		sqlBut.setMouseExitedIcon(FaSolidEnum.OUTDENT,16);
		sqlBut.setMouseEnteredIcon(FaSolidEnum.OUTDENT,16);
		sqlBut.setToolTipText(LangMgr.lang(Word2.EXAMINE)+"SQL");
		whereTextInput = new TextInputExt("", sqlBut, null, LangMgr.sLang(CsL.FILTER_RESULT));
		whereTextInput.getExtBtn().setToolTipText(LangMgr.lang(Word2.EXPAND));
		whereTextInput.getTextInput().getComp().setToolTipText(LangMgr.sLang(CsL.FILTER_RESULT));
		if (StringUtils.isNoneBlank(whereSql)) {
			whereTextInput.setValue(whereSql);
			whereTextInput.getTextInput().getComp().requestFocus();
		}
		sqlBut.getComp().setPreferredSize(new Dimension(30, 20));
		//回车监听
		whereTextInput.getTextInput().getComp().addActionListener(e -> {
			if (!isCancelSaveData()) return;
			String fieldText = whereTextInput.getValue();
			String oldWhereSql = getWhereSql();
			if (getWhereSql() == null && fieldText != null) {
				page = 1;
				offset = 0;
			}
			setWhereSql(fieldText);
			refreshTab();
			boolean isSelectCount = StringUtils.isBlank(fieldText) || StringUtils.isNoneBlank(getWhereSql()) && StringUtils.isNoneBlank(fieldText) && !getWhereSql().equals(oldWhereSql);
			if (isSelectCount) doSelectCount();
		});
		//输入监听
		whereTextInput.getTextInput().getComp().getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			String fieldText = whereTextInput.getValue();
			if (getWhereSql() != null && StringUtils.isBlank(fieldText)) setWhereSql(fieldText);
		});
	}

	/**
	 * 设置表头和列
	 */
	private void addTableCol() {
		tab.setRowStyle(true);
		searchToolBar = new TableSearchToolBar(tab,queryTool.getSelTypes(),schemaName) {
			@Override
			protected void typeActionPerformed(ActionEvent e) {
				super.typeActionPerformed(e);
				isRowStyle = htab.isRowStyle();
				setSaveBtnStatus();
			}
		};

		List<AbsCol> oldCols = columns == null ? null : new ArrayList<>(columns);

		this.columns = tableSqlTool.createCol(tab);
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
			} else {
				absCol.setWidth(50);//设置一个默认宽度，防止内容过长表格不会平铺显示
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
			queryTool = new QueryTool(dbType,ver,connection, sql, tmpDir, (int) (limit + 1), nullSymbol);
			queryTool.next();
			//Thread.sleep(2000);
//			List<Enum<?>> selTypes = queryTool.getSelTypes();
//			List<String> colNames = queryTool.getColNames();
//			if (!readOnly) {
//				tabColTool.initColumnTypeMap(connection, schemaName, tabName);
//			}
			tableSqlTool = new ModifyTableTool(schemaName, tabName, nullSymbol, queryTool);
			tableSqlTool.setReadOnly(readOnly);
			List<Map<String, String>> dataMap = tableSqlTool.toDataMap();
			hasNext = dataMap.size() > 0 && dataMap.size() > limit;
			if (hasNext) {
				dataMap.remove(dataMap.size() - 1);
			}
			return dataMap;
		} catch (Exception e) {
			e.printStackTrace();
			Lg.error(ModifyTabDataComp.class, e);
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
        pagePanel.getPageSizeBut().getComp().addActionListener(this::changeLimitActionPerformed);
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
		//添加表格Ctrl+v把剪切板中的数据粘贴到表格中
		InputMap inputMap =  tab.getComp().getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "paste");
		actionMap.put("paste", new PasteAction());
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

			change = ModifyTableTool.isLobChange(changedValue, change);


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
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
		}
	}

	/**
	 * 弹出提示框:是否保存修改的内容
	 *
	 * @return true不修改  false为修改
	 */
	public boolean isCancelSaveData() {
		if (isChange) {
			if (PopDlgUtil.confirm(CsVarUtil.parentFrame,LangMgr.sLang(CsL.SAVE_OR_NOT))) {
				saveData();
				return false;
			}
		}
		return true;
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
			if (!isCancelSaveData()) {
				return;
			}
			if (isEnabled) {
				comp.setEnabled(false);
			}
			if (page > 1) {
//				tabColTool.setTable(tab);
				page = 1;
				offset = 0;
				refreshTab(tabName);
			}

		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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
			if (!isCancelSaveData()) {
				return;
			}
			if (isEnabled) {
				comp.setEnabled(false);
			}
			if (page > 1) {
//				tabColTool.setTable(tab);
				page--;
				offset = Math.max(page - 1, 0) * limit;
				page = Math.max(page, 1);
				refreshTab(tabName);
			}

		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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
			if (!isCancelSaveData()) {
				return;
			}
			if (isEnabled) {
				comp.setEnabled(false);
			}
			if (hasNext) {
				offset = page * limit;
//				tabColTool.setTable(tab);
				page++;
				refreshTab(tabName);
			}
		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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
			if (!isCancelSaveData()) {
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
//				tabColTool.setTable(tab);
				refreshTab(tabName);
			}
		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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
		status.setIcon(FaSolidEnum.CLOCK_ROTATE_LEFT,16);
		status.getComp().setToolTipText(LangMgr.lang(Word4.LOAD_TIME)+":" + timeTips);

		setSaveBtnStatus();
		computeTmpTotalLines();
		String value = tableCount == -1 ? (tmpTableCount + "+") : tableCountTips;
		String tmpLimitTips = String.format("%s-%s", (offset + 1), (offset + data.size()));
		if (data.size() == 1) {
			tmpLimitTips = String.valueOf((offset + 1));
		}
		pagePanel.getPageTotalLabel().setValue(value);
		pagePanel.getCurPageNum().getComp().setToolTipText(String.format(LangMgr.lang(Word3.CURRENT_PAGE)+" : %s (%s "+LangMgr.lang(Word1.ROW)+")", page, tmpLimitTips));
		pagePanel.getPageTotalLabel().getComp().setToolTipText(tableCount == 0 ? String.format(LangMgr.lang(Word3.TOTAL_ROWS)+": %s+ ("+LangMgr.sLang(CsL.GET_HEAD_OFFICE)+")"
				, tmpTableCount) : value);
		pagePanel.getPageSizeBut().setToolTipText(String.format(LangMgr.lang(Word4.PAGE_SIZE)+" (%s)", limit));
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
			pagePanel.getPageTotalLabel().getComp().setToolTipText(tableCountTips);
			System.out.printf("%s ("+LangMgr.lang(Word2.TIME_CONSUMING)+": %s ms%n)", tableCountTips, (end - start));
		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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
			tableCountTips = String.format(LangMgr.lang(Word1.COMMON)+"%s"+LangMgr.lang(Word1.ROW)+"(%s"+LangMgr.lang(Word1.PAGE)+")", tableCount, totalPages);
		} else {
			tableCountTips = LangMgr.sLang(CsL.GET_HEAD_OFFICE);
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
		boolean isProper = true;
		
		String input = PopDlgUtil.nameInput(LangMgr.sLang(CsL.ENTER_PAGE_SIZE),limit+"");
		if (StringUtils.isNoneBlank(input)) {
			input = input.trim();
			if (SelectTableSqlUtil.isNumeric(input)) {
				if (Integer.parseInt(input) <= 0) {
					isProper = false;
					PopDlgUtil.error(new Exception(LangMgr.sLang(CsL.POSITIVE_NTEGER)));
				} else {
					limit = Integer.parseInt(input);
				}
			} else {
				isProper = false;
				PopDlgUtil.error(new Exception(LangMgr.sLang(CsL.POSITIVE_NTEGER)));
			}
		} else {
//			pagePanel.getPageSizebutton().setValue(String.valueOf(limit));
			isProper = false;
		}
		if (isProper) {
			offset = 0;
			page = 1;
			computeTotalPages();
			refreshTab(tabName);
		} else {
//			pagePanel.getPageSizeComp().getComboBox().setSelectedItem(limit);
		}
	}

	/**
	 * 获取总行数
	 *
	 * @return 行数
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	protected long getTableCount() throws Exception {
		try (Connection countConn = CsVarUtil.newConn(jdbc)) {
			String countSql = SelectTableSqlUtil.getCountSql(dbType,ver, schemaName, tabName, whereSql);
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
	@Deprecated
	//结构不好，不清除conn是否被关闭了
	class InitConnTask extends SwingWorker<Boolean, Void> {
		protected Connection conn;

		@Override
		protected Boolean doInBackground() {
			Boolean flag = null;
			try {
				conn = CsVarUtil.newConn(jdbc);
				flag = true;
			} catch (Exception e) {
				flag = false;
				setErrorMessage(e, LangMgr.sLang(CsL.FAILED_GET_CONNECTION));
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
		protected boolean readOnly = false;

		SelectDataTask(String selectSql, boolean readOnly) {
			this.selectSql = selectSql;
			this.readOnly = readOnly;
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
			progressTimeLabel.setValue(timeTips);
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
			if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
				if (!tab.getComp().isEditing()) {
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
			if (!isCancelSaveData()) {
				return;
			}
			opToolBarPanel.getRefreshData().setEnabled(false);
			refreshTab();
		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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
//		ModifyTableTool.requestFocus(tab, 1, colListSelectionListener);
		setSaveBtnStatus();
	}

	/**
	 * 保存按钮点击事件
	 */
	public void saveActionPerformed(ActionEvent e) {
		if (PopDlgUtil.confirm(LangMgr.sLang(CsL.CONFIRM_SUBMISSION))) {
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
			//tabColTool.setTable(tab);
			List<AbstractSqlBean> sqlBeans = getListSqlBean(connection);

			List<String> listSql = tableSqlTool.getSqlBeanStr(sqlBeans);
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
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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

	private List<AbstractSqlBean> getListSqlBean(Connection conn) throws IOException, SQLException {
		List<HTabRowBean> updateRowBean = tab.getRowBeans(RowStatus.UPDATE);
		List<HTabRowBean> delRowBeans = tab.getRowBeans(RowStatus.DEL);
		List<HTabRowBean> addRowBeans = tab.getRowBeans(RowStatus.ADD);

		List<Map<String, String>> updateNewMapList = new ArrayList<>();
		List<Map<String, String>> oldMapList = updateRowBean.stream().map(HTabRowBean::getOldRow).collect(Collectors.toList());
		for (HTabRowBean rowBean : updateRowBean) {
//			for (String key : newRowMap.keySet()) {
//				rowBean.getOldRow().put(key, newRowMap.get(key));
//			}
//			Map<String, String> newMap = rowBean.getOldRow().entrySet()
//					.stream()
//					.collect(
//							Collectors.toMap(
//									Map.Entry::getKey,
//									Map.Entry::getValue,
//									(v1, v2) -> v2,
//									() -> new HashMap<>(rowBean.getCurrRow())));
//			Map<String, String> newMap = Stream.of(rowBean.getOldRow(), rowBean.getCurrRow())
//					.map(Map::entrySet).
//							flatMap(Collection::stream)
//					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2)
//					);
			Map<String, String> newMap = new LinkedHashMap<>();
			newMap.putAll(rowBean.getOldRow());
			newMap.putAll(rowBean.getCurrRow());
			updateNewMapList.add(newMap);
		}

		List<Map<String, String>> delMapList = delRowBeans.stream().map(HTabRowBean::getOldRow).collect(Collectors.toList());
		List<Map<String, String>> addNewMapList = addRowBeans.stream().map(HTabRowBean::getCurrRow).collect(Collectors.toList());

		List<AbstractSqlBean> updateSqlBeans = tableSqlTool.getUpdateSqlBean(conn, oldMapList, updateNewMapList);
		List<AbstractSqlBean> deleteSqlBeans = tableSqlTool.getDeleteSqlBean(conn, delMapList);
		List<AbstractSqlBean> addSqlBeans = tableSqlTool.getAddSqlBean(addNewMapList);
		updateSqlBeans.addAll(deleteSqlBeans);
		updateSqlBeans.addAll(addSqlBeans);

		return updateSqlBeans;
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
//			tabColTool.setTable(tab);
			conn = CsVarUtil.newConn(jdbc);
			tab.getComp().getColumnModel().getSelectionModel().removeListSelectionListener(colListSelectionListener);

			List<AbstractSqlBean> sqlBeans = getListSqlBean(conn);
			//List<InsertSqlBean> insertSqlBeans = tabColTool.buildInsertSqlBeans(schemaName, tabName);
			if (sqlBeans.size() > 0) {
				//tabColTool.doUpdate(conn, listSql, insertSqlBeans);
				tableSqlTool.saveCommit(this.dbType,conn, sqlBeans);
				refreshTab();
			} else {
				opToolBarPanel.getSaveData().setEnabled(false);
			}
			setSaveBtnStatus();
		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
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
			int select = tab.getComp().getSelectedRows().length;
			if (select > 0) {
				if (PopDlgUtil.confirm(String.format(LangMgr.sLang(CsL.CONFIRM_DELETION), select))) {
					tab.deleteSelectRow();
					setSaveBtnStatus();
				}
			} else {
				PopDlgUtil.info(LangMgr.sLang(CsL.SELECT_DELETION_RECORD));
			}
		} catch (Exception exception) {
			Lg.error(ModifyTabDataComp.class, exception);
			PopDlgUtil.error(exception);
			setSaveBtnStatus();
		}
	}
	
	/**
	 * Ctrl+v把剪切板中的数据粘贴到表格中
	 */
	private class PasteAction extends AbstractAction {
		private static final long serialVersionUID = 6647591494008147634L;
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String text = ModifyTableTool.getStringFromClipboard();
				String[] textRows = text.split("\\r?\\n");
				if ((tab.getComp().getSelectedRowCount() > 0 || tab.getComp().getSelectedColumns().length > 0) && StringUtils.isNotBlank(text)) {
					List<HTabRowBean> beanList;
					if ( tab.getComp().getSelectedRowCount() == 0) {	//点击的列标题名称,选择的此列所有行
						beanList = tab.getRowBeans(null);
					} else {
						beanList = tab.getSelectedRowBeans();
					}
					int i = 0;
					for (HTabRowBean rowBean : beanList) {
						if (rowBean.getStatus() != RowStatus.ADD) rowBean.setStatus(RowStatus.UPDATE);
						Map<String, String> map = null == rowBean.getCurrRow() ? new LinkedHashMap<>() : rowBean.getCurrRow();
						if(i>= textRows.length)
							break;
						String[] textCols = textRows[i].split("\\t", -1); //-1 means do not drop the last empty string
						int j = 0;
						for (int column : tab.getComp().getSelectedColumns()) {
							for (AbsCol col : tab.getColumns()) {
								//排除序列隐藏列和大字段列
								if (col.getValue().equals(tab.getComp().getColumnName(column)) && !SeqCol.name.equals(col.getName()) && !SelectTableSqlUtil.getHideColNames().contains(col.getName())
										&& !(col instanceof LobJsonCol)) {
									if(j>=textCols.length)
										break;
									map.put(col.getName(),textCols[j]);
									j++;
									break;
								}
							}
						}
						if (map.size() > 0) rowBean.setCurrRow(map);
						i++;
					}
					tab.getComp().clearSelection();
					setSaveBtnStatus();
				}
			}catch (Exception e1){
				Lg.error(ModifyTabDataComp.class, e1);
				PopDlgUtil.error(e1);
			}
		}
	}
	
	public String getSchemaName() {
		return schemaName;
	}
	
	public String getParentTable() {
		return parentTable;
	}
	
	public void setParentTable(String parentTable) {
		this.parentTable = parentTable;
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
