package com.hh.hhdb_admin.mgr.table_open;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.version.VersionBean;
import com.hh.frame.dbobj2.version.VersionUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.*;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.frame.swingui.view.tab.col.json.JsonColEditor;
import com.hh.frame.swingui.view.tab.menu.body.ExpTabBodyPopMenu;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import com.hh.hhdb_admin.mgr.table_open.common.InsertSqlBean;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabDataUtil;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;
import com.hh.hhdb_admin.mgr.table_open.ui.HHOperateTablePanel;
import com.hh.hhdb_admin.mgr.table_open.ui.HHPagePanel;
import com.hh.hhdb_admin.mgr.table_open.ui.LobJsonCol;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModifyTabDataComp extends LastPanel {
	public static final String LOG_NAME = ModifyTabDataComp.class.getSimpleName();
	private Connection conn;
	private JdbcBean jdbc;
	private String schemaName;
	private String tabName;
	private String selectSql;
	private DBTypeEnum dbType;
	private File tmpDir;
	private HTable tab = null;
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
	 * 每页数据文件条数
	 */
	private int rowPerPage = 30;

	private ColListSelectionListener colListSelectionListener;

	private final String load_tips = "正在加载中...";
	private final String error_tips = "获取数据错误!";

	private String nullSymbol = "";

	public ModifyTabDataComp() {
	}

	public ModifyTabDataComp(JdbcBean jdbc, String tabName, File tmpDir) {
		this(jdbc, ModifyTabDataUtil.getSchema(DriverUtil.getDbType(jdbc), jdbc), tabName, tmpDir);
	}

	public ModifyTabDataComp(JdbcBean jdbc, String schemaName, String tabName, File tmpDir) {
		init(jdbc, schemaName, tabName, tmpDir);
	}

	public void init(JdbcBean jdbc, String schemaName, String tabName, File tmpDir) {
		setWithScroll(new LabelInput(load_tips, AlignEnum.CENTER).getComp());

		this.jdbc = jdbc;
		this.schemaName = schemaName;
		this.tabName = tabName;
		this.dbType = DriverUtil.getDbType(jdbc);
		this.tmpDir = tmpDir;
		SwingUtilities.invokeLater(() -> {
			try {
				conn = ConnUtil.getConn(jdbc);
				JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
				JsonValue aNull = fileJsonArr.get("null");
				nullSymbol = aNull != null ? aNull.asString() : nullSymbol;
			} catch (Exception throwables) {
				throwables.printStackTrace();
				setWithScroll(new LabelInput(error_tips, AlignEnum.CENTER).getComp());
				PopPaneUtil.error(throwables.getMessage());
			}
		});

	}

	public void close() {
		ConnUtil.close(conn);
		FileUtils.deleteQuietly(tmpDir);
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
		SwingUtilities.invokeLater(() -> {
			try {
				start = System.currentTimeMillis();
				VersionBean dbVersion = VersionUtil.getDbVersion(conn);
				this.tabName = tabName;
				this.selectSql = ModifyTabDataUtil.getSelAllSql(dbType, dbVersion, schemaName, tabName);
				tabColTool = new ModifyTabTool(dbType, tab, this);
				tabColTool.setReadOnly(false);
				tabColTool.setDbVersion(dbVersion);
				data = getDbData(selectSql);
				load(data);
			} catch (Exception e) {
				e.printStackTrace();
				setWithScroll(tab.getComp());
				PopPaneUtil.error(e);
			} finally {
				opToolBarPanel.getRefreshData().setEnabled(true);
			}
		});

	}

	/**
	 * 将指定的数据加载到表格中
	 *
	 * @param data 数据集合
	 */
	private void load(List<Map<String, String>> data) throws Exception {
		if (queryTool.getColNames() == null || queryTool.getSelTypes() == null) {
			return;
		}
		//删除已有表格和工具栏
		removeAll();
		tab = new HTipTable();
		tab.setNullSymbol(nullSymbol);
//		tab.setHeadPopMenu(new DefHeaderPopMenu());
		tab.setRowPopMenu(new ExpTabBodyPopMenu());
		//JTableHeader tableHeader = tab.getComp().getTableHeader();
		//tableHeader.setFont(tableHeader.getFont().deriveFont(Font.BOLD));
		//初始化表格头和列
		addTableCol();

		//将表格添加到panel中间
		//设置底部按钮栏
		setFoot(initToolBar().getComp());
		//设置底部栏信息
		setStatusAndListener();

		//设置表头提示
//		try {
//			if (columnTypeMap != null && !readOnly) {
//				JTable jTable = ((JTable) tab.getComp());
//				OpenTableHeader openTableHeader = new OpenTableHeader(jTable.getColumnModel(), columnTypeMap);
//				openTableHeader.setTable(jTable);
//				jTable.setTableHeader(openTableHeader);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		//表格加载数据
		tab.load(data, 1);
		setWithScroll(tab.getComp());
		//设置ToolTip显示时间
		ToolTipManager.sharedInstance().setDismissDelay(10000);
//			((JPanel) comp).updateUI();
	}

	/**
	 * 获取只读表格
	 *
	 * @param sql sql语句
	 * @return
	 * @throws Exception
	 */
	public void loadReadOnlyTable(String sql) {
		SwingUtilities.invokeLater(() -> {
			try {
				start = System.currentTimeMillis();
				readOnly = true;
				tabColTool = new ModifyTabTool(dbType, tab, this);
				tabColTool.setReadOnly(true);
				List<Map<String, String>> data = getDbData(sql);
				load(data);
				tab.setCellEditable(!readOnly);
			} catch (Exception e) {
				e.printStackTrace();
				setWithScroll(new LabelInput(error_tips, AlignEnum.CENTER).getComp());
				PopPaneUtil.error(e);
			}
		});
	}

	/**
	 * 设置表头和列
	 */
	private void addTableCol() {
		tab.setRowHeight(30);
		tab.setRowStyle(true);
		SearchToolBar sToolbar = new SearchToolBar(tab);
		setHead(sToolbar.getComp());
		List<AbsCol> cols = tabColTool.createCol(queryTool, jdbc, schemaName, tabName);
		cols.stream().filter(Objects::nonNull).forEach(absCol -> {
			if (absCol instanceof LobJsonCol) {
				absCol.setWidth(160);
			} else if (ModifyTabDataUtil.getHideColNames().contains(absCol.getValue())) {
				absCol.setShow(false);
			}
			tab.addCols(absCol);
		});
	}

	/**
	 * 从数据库查询数据
	 *
	 * @param sql 查询语句
	 * @return 数据集合
	 * @throws Exception
	 */
	private List<Map<String, String>> getDbData(String sql) throws Exception {
		try {
			if (!tmpDir.exists()) {
				tmpDir.mkdirs();
			}
			tmpDir = new File(tmpDir, String.valueOf(System.currentTimeMillis()));
			FileUtils.deleteQuietly(tmpDir);
			queryTool = new QueryTool(conn, sql, tmpDir, rowPerPage);
			queryTool.next();

			List<Enum<?>> selTypes = queryTool.getSelTypes();
			List<String> colNames = queryTool.getColNames();
			if (!readOnly) {
				tabColTool.initColumnTypeMap(conn, schemaName, tabName);
			}
			return tabColTool.toDataMap(selTypes, colNames, queryTool.getCurrentFile());
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
			hPanel = new HPanel(new HDivLayout(GridSplitEnum.C5, GridSplitEnum.C3, GridSplitEnum.C4));
		} else {
			hPanel = new HPanel(new HDivLayout(GridSplitEnum.C8, GridSplitEnum.C4));
		}

		HPanel statusPanel = new HPanel(new HDivLayout(10, 10, GridSplitEnum.C12));
		status = new LabelInput();
		status.getComp().setBorder(BorderFactory.createEmptyBorder(10, 10, 3, 20));
		statusPanel.add(status);

		pagePanel = new HHPagePanel();
//		pagePanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 3, 2));

		if (opToolBarPanel != null) {
//			opToolBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));
			hPanel.add(opToolBarPanel);
		}
		sqlViewDialog = new SqlViewDialog(tab.getComp());

		hPanel.add(statusPanel);
		hPanel.add(pagePanel);
		return hPanel;
	}


	/**
	 * 设置按钮状态和点击事件监听
	 */
	private void setStatusAndListener() throws Exception {
		//添加按钮监听
		pagePanel.getPrePage().addActionListener(new PrePageListener());
		pagePanel.getNextPage().addActionListener(new NextPageListener());

		if (opToolBarPanel != null) {
			opToolBarPanel.getRefreshData().setEnabled(true);
			opToolBarPanel.getSaveData().setEnabled(false);
			opToolBarPanel.getCancel().setEnabled(false);
			opToolBarPanel.getDelRow().setEnabled(false);
			opToolBarPanel.getSqlView().setEnabled(false);


			opToolBarPanel.getRefreshData().addActionListener(new RefreshListener());
			opToolBarPanel.getAddRow().addActionListener(new AddRowListener());
			opToolBarPanel.getDelRow().addActionListener(new DelRowListener());
			opToolBarPanel.getSaveData().addActionListener(new SaveListener());
			opToolBarPanel.getSqlView().addActionListener(new SqlViewListener());
			opToolBarPanel.getCancel().addActionListener(e -> {
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
			});

		}

		//表格点击事件监听
		tab.getComp().addMouseListener(new RowClickListener());
		//表格修改数据事件监听
		tab.getComp().addPropertyChangeListener(new TableChangeListener());
		//查询分页总数
//		ThreadUtil.start(new TotalThread());
		setPageStatus();
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
			boolean add = addValue != null && addValue.size() > 0;
			boolean del = delValue != null && delValue.size() > 0;
			isChange = change || add || del;
			//保存按钮
			opToolBarPanel.getSaveData().setEnabled(isChange);
			//取消按钮
			opToolBarPanel.getCancel().setEnabled(isChange);
			//sql预览按钮
			opToolBarPanel.getSqlView().setEnabled(isChange);
			//删除按钮
			opToolBarPanel.getDelRow().setEnabled(tab.getSelectedRowBeans().size() > 0);
		}
	}

	/**
	 * 表格行点击事件
	 */
	private class RowClickListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
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
	private class RefreshListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (isCancelSaveData()) {
					return;
				}
				start = System.currentTimeMillis();
				opToolBarPanel.getRefreshData().setEnabled(false);
				setWithScroll(new LabelInput(load_tips, AlignEnum.CENTER).getComp());
				refreshTab();
			} catch (Exception exception) {
				PopPaneUtil.error(exception);
				exception.printStackTrace();
			}
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
			int result = JOptionPane.showOptionDialog(tab.getComp().getParent(), "是否保存修改的内容?", "确认",
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
	 * 上一页点击事件
	 */
	private class PrePageListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (isCancelSaveData()) {
					return;
				}
				start = System.currentTimeMillis();
				tabColTool.setTable(tab);
				if (queryTool.hasPrevious()) {
					queryTool.previous();
					//表格加载数据
					setPageStatus();
				}
			} catch (Exception exception) {
				PopPaneUtil.error(exception);
				exception.printStackTrace();
			}
		}
	}


	/**
	 * 下一页点击事件
	 */
	private class NextPageListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (isCancelSaveData()) {
					return;
				}
				start = System.currentTimeMillis();
				tabColTool.setTable(tab);
				if (queryTool.hasNext()) {
					queryTool.next();
					setPageStatus();
				}
			} catch (Exception exception) {
				PopPaneUtil.error(exception);
				exception.printStackTrace();
			}
		}
	}

	private void setPageStatus() throws Exception {
		tabColTool.setTable(tab);
		data = tabColTool.toDataMap(queryTool.getSelTypes(), queryTool.getColNames(), queryTool.getCurrentFile());
		tab.load(data, 1);
		pagePanel.getCurPageNum().setValue(String.valueOf(queryTool.getCurrentPage()));
		pagePanel.getCurPageNum().setEnabled(false);
		pagePanel.getNextPage().setEnabled(queryTool.hasNext());
		pagePanel.getPrePage().setEnabled(queryTool.hasPrevious());
		if (start == null) {
			start = System.currentTimeMillis();
		}
		status.setValue("加载时间：" + (System.currentTimeMillis() - start) + "ms");
		start = null;
		setSaveBtnStatus();
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
			jTable.editCellAt(selectedRow, selectedColumn);
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
	private class AddRowListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
//			HDialog dialog = new HDialog(700, 500);
//			dialog.setTitle("添加数据");
//			AddDataPanel dataComp = new AddDataPanel(dbType, columnTypeMap);
//			HPanel panel=new HPanel();
//			panel.setLastPanel(dataComp);
//			dialog.setRootPanel(panel);
//			//((JDialog) dialog.getWindow()).setResizable(true);
//			dialog.show();
			setStatus();
		}

		/**
		 * 添加一行 并聚焦第一个单元格
		 */
		private void setStatus() {
			if (tab.getComp().isEditing()) {
				tab.getComp().getCellEditor().stopCellEditing();
			}
			tab.add(tab.getRowCount(), new HashMap<>());
			ModifyTabDataUtil.requestFocus(tab, 1, colListSelectionListener);
//			int columnCount = jTable.getColumnCount();
//			Map<String, Enum<?>> lobMap = tabColTool.getLobMap();
		}
	}


	/**
	 * 删除按钮点击事件
	 */
	private class DelRowListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			opToolBarPanel.getDelRow().setEnabled(false);
			try {
				if (tab.getComp().isEditing()) {
					tab.getComp().getCellEditor().stopCellEditing();
				}
				tabColTool.setTable(tab);
				int select = tab.getComp().getSelectedRows().length;
				if (select > 0) {
					int result = JOptionPane.showConfirmDialog(tab.getComp(), String.format("确定要删除 %s 条记录吗?", select), "确认删除", JOptionPane.YES_NO_OPTION);
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
	}

	/**
	 * 保存按钮点击事件
	 */
	private class SaveListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int result = JOptionPane.showConfirmDialog(tab.getComp(), "确定要提交修改吗?", "确认提交", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				saveData();
			}
		}
	}

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
	 * Sql预览点击事件
	 */
	private class SqlViewListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (tab.getComp().isEditing()) {
					tab.getComp().getCellEditor().stopCellEditing();
				}
				tabColTool.setTable(tab);
				List<String> listSql = tabColTool.getUpdateOrDelSql(queryTool, conn, schemaName, tabName);
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
	 * 表头悬浮提示 (后面需要添加类型,长度等信息) 有bug暂时取消
	 */
//	private static class OpenTableHeader extends JTableHeader {
//		private static final long serialVersionUID = 3897336005194146430L;
//		private final Map<String, String> columnTypeMap;
//
//		public OpenTableHeader(TableColumnModel cm, Map<String, String> columnTypeMap) {
//			super(cm);
//			this.columnTypeMap = columnTypeMap;
////			URL url = ModifyTabTool.class.getResource("pk.png");
////			ImageIcon imageIcon = new ImageIcon(url);
////			TableColumn column = cm.getColumn(1);
////
////			Border headerBorder =new BorderUIResource(new TableUI.TableHeaderBorder());
////			JLabel blueLabel = new JLabel(column.getHeaderValue().toString(), imageIcon, JLabel.LEFT);
////			blueLabel.setBorder(headerBorder);
////			TableCellRenderer renderer = new JComponentTableCellRenderer();
////
////			//将用于绘制 TableColumn 的头的 TableCellRenderer 设置为 renderer。
////			column.setHeaderRenderer(renderer);
////			//设置 Object，将使用其字符串表示形式作为 headerRenderer 的值。
////			column.setHeaderValue(blueLabel);
//
//		}
//
//		@Override
//		public String getToolTipText(MouseEvent e) {
//			String tip;
//			java.awt.Point p = e.getPoint();
//			int index = columnModel.getColumnIndexAtX(p.x);
//			if (index > columnModel.getColumnCount()) {
//				return null;
//			}
//			String columnName = table.getColumnName(index);
//			String type = columnTypeMap.get(columnName);
//			if (type != null) {
//				tip = String.format("%s : %s", columnName, type);
//			} else {
//				tip = columnName;
//			}
//			return tip;
//		}
//	}
	public String getSchemaName() {
		return schemaName;
	}

	public String getTabName() {
		return tabName;
	}

	public int getRowPerPage() {
		return rowPerPage;
	}

	public void setRowPerPage(int rowPerPage) {
		this.rowPerPage = rowPerPage;
	}

	public HTable getTab() {
		return tab;
	}

}
