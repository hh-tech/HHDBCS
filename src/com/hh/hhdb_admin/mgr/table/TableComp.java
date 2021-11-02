package com.hh.hhdb_admin.mgr.table;

import java.awt.Container;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.create_dbobj.table.CreateTableTool;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.common.CreateTableSqlSyntax;
import com.hh.hhdb_admin.mgr.table.common.TableCreatePanel;
import com.hh.hhdb_admin.mgr.table.common.TableUtil;
import com.hh.hhdb_admin.mgr.table.comp.ColumnPanel;
import com.hh.hhdb_admin.mgr.table.comp.ForeignKeyPanel;
import com.hh.hhdb_admin.mgr.table.comp.PartitionPanel;
import com.hh.hhdb_admin.mgr.table.comp.SqlViewDialog;
import com.hh.hhdb_admin.mgr.table.comp.TableNamePanel;
import com.hh.hhdb_admin.mgr.table.comp.UniquePanel;

/**
 * @author oyx
 * @date 2020-9-18  0018 15:25:41
 */
public class TableComp implements CreateTableSqlSyntax {

	public static final String LOG_NAME = TableComp.class.getSimpleName();
	public static final String DOMAIN_NAME = TableComp.class.getName();

	static {
		try {
            LangMgr2.loadMerge(TableComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	private static final String TAB_COL_PANEL = "col_mgr_panel";
	private static final String COL_UNIQUE_PANEL = "col_unique_panel";
	private static final String COL_FOREIGN_KEY_PANEL = "col_foreign_key_panel";
	public static final String COL_P_PANEL = "col_p_panel";

	private LastPanel lastPanel;
	private Connection conn;

	private ColumnPanel columnPanel;
	private UniquePanel uniquePanel;
	private ForeignKeyPanel foreignKeyPanel;
	private TableNamePanel tableNamePanel;
	private PartitionPanel partitionPanel;

	private TableCreatePanel[] createPanels;

	private SqlViewDialog sqlViewDialog;
	public static String schemaName = "";

	private static final String LK_SAVE_SUCCESS = "SAVE_SUCCESS";
	public static CreateTableTool createTabTool;
	public static JdbcBean jdbcBean;
	private static DBTypeEnum dbTypeEnum;
	public static HDialog dialog = new HDialog(StartUtil.parentFrame, HFrame.LARGE_WIDTH, HFrame.LARGE_WIDTH / 4 * 3);;
	public String title;

	public TableComp(Connection conn, DBTypeEnum dbTypeEnum) {
		this.conn = conn;
		TableComp.dbTypeEnum = dbTypeEnum;
		createTabTool = new CreateTableTool(dbTypeEnum);

		lastPanel = initComp();
		Container parent = dialog.getWindow().getParent();
		HPanel panel = new HPanel();
		panel.setLastPanel(lastPanel);
		dialog.setRootPanel(panel);
		dialog.setWindowTitle(getTitle());
		dialog.setIconImage(IconFileUtil.getLogo());
		dialog.getWindow().setLocationRelativeTo(parent);
	}

	/**
	 * 初始化各组件表格
	 */
	public void genTableData() {
		for (TableCreatePanel createPanel : createPanels) {
			createPanel.addCol();
			createPanel.getTable().getComp().selectAll();
			createPanel.getTable().deleteSelectRow();
		}
	}

	public static CreateTableTool getCreateTabTool() {
		return createTabTool;
	}

	/**
	 * 初始化组件
	 *
	 * @return LastPanel
	 */
	public LastPanel initComp() {
		HDivLayout divLayout = new HDivLayout(5, 10, GridSplitEnum.C12);
		divLayout.setTopHeight(5);

		lastPanel = new LastPanel(false);
		tableNamePanel = new TableNamePanel(divLayout) {
			@Override
			protected void viewSqlOnclick() {
				viewSql();
			}

			@Override
			protected void saveOnclick() {
				save();
			}
		};
		divLayout = new HDivLayout(5, 20, GridSplitEnum.C12);
		divLayout.setTopHeight(10);

		columnPanel = new ColumnPanel();
		uniquePanel = new UniquePanel(columnPanel);
		foreignKeyPanel = new ForeignKeyPanel(columnPanel, conn);
		partitionPanel = new PartitionPanel(divLayout);

		HTabPane tabPane = new HTabPane();
		tabPane.setCloseBtn(false);
		tabPane.addPanel(TAB_COL_PANEL, getLang("column"), this.columnPanel);
		tabPane.addPanel(COL_UNIQUE_PANEL, getLang("uniqueKey"), this.uniquePanel);
		tabPane.addPanel(COL_FOREIGN_KEY_PANEL, getLang("foreignKey"), this.foreignKeyPanel);
		if (TableUtil.showPartition(getDbType())) {
			tabPane.addPanel(COL_P_PANEL, getLang("partitionType"), partitionPanel);
		}
		createPanels = new TableCreatePanel[]{columnPanel, uniquePanel, foreignKeyPanel};

		lastPanel.setHead(tableNamePanel.getLastPanel().getComp());
		//设置Tab
		lastPanel.set(tabPane.getComp());
		sqlViewDialog = new SqlViewDialog(lastPanel.getComp());
		return lastPanel;

	}

	/**
	 * 弹出创建表窗口
	 */
	public void show() {
		dialog.show();
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	/**
	 * 保存表
	 */
	private void save() {
		try {
			String tableName = getTableName();
			if (tableName == null) {
				return;
			}
			stopEditing();
			String sql;
			try {
				sql = build(tableName);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				PopPaneUtil.error(dialog.getWindow(), e);
				return;
			}
			List<String> sqlList = Arrays.stream(sql.split(String.valueOf(SEMI_COLON))).filter(StringUtils::isNoneBlank)
					.map(s -> s.replaceAll(String.valueOf(SEMI_COLON), EMPTY)).collect(Collectors.toList());
			SqlExeUtil.batchExecute(conn, sqlList);
			refresh();
			dialog.dispose();
			PopPaneUtil.info(StartUtil.parentFrame.getWindow(), TableComp.getLang(LK_SAVE_SUCCESS));
		} catch (SQLException e) {
			StringBuilder error = new StringBuilder();
			if (e instanceof BatchUpdateException) {
				BatchUpdateException bue = (BatchUpdateException) e;
				if (!e.getMessage().contains("getNextException()")) {
					error.append(e.getMessage()).append("\r\n");
				}
				SQLException nextException = bue.getNextException();
				while (nextException != null) {
					error.append(nextException.getMessage()).append("\r\n");
					nextException = nextException.getNextException();
				}
			} else {
				error.append(e.getMessage());
			}
			e.printStackTrace();
			PopPaneUtil.error(dialog.getWindow(), error.toString());
		}
	}

	/**
	 * 预览sql
	 */
	private void viewSql() {
		String tableName = getTableName();
		if (tableName == null) {
			return;
		}
		stopEditing();
		String sql;
		try {
			sql = build(tableName);
		} catch (Exception e) {
			e.printStackTrace();
			PopPaneUtil.error(dialog.getWindow(), e);
			return;
		}
		sqlViewDialog.setSql(sql);
		sqlViewDialog.show();
	}

	private String build(String tableName) throws Exception {
		//构建sql语句
		createTabTool.setTableName(tableName);
		createTabTool.setSchemaName(schemaName);
		createTabTool = getCreateTabTool();
		columnPanel.build(createTabTool);
		uniquePanel.build(createTabTool);
		foreignKeyPanel.build(createTabTool);
		partitionPanel.build(createTabTool);
		createTabTool.addTableComment(tableNamePanel.getAnnotateInput().getValue());
		return createTabTool.toString();
	}

	private void stopEditing() {
		for (TableCreatePanel createPanel : createPanels) {
			TableUtil.stopEditing(createPanel.getTable());
		}
	}

	private String getTableName() {
		String tableName = tableNamePanel.getTableNameInput().getValue();
		if (tableName == null || StringUtils.isBlank(tableName.trim())) {
			PopPaneUtil.info(dialog.getWindow(), getLang("fillTableName"));
			return null;
		}
		return tableName;
	}


	public String getTitle() {
		return getLang("createTable");
	}

	public LastPanel getLastPanel() {
		return lastPanel;
	}

	public static String getLang(String key) {
		return LangMgr2.getValue(TableComp.class.getName(), key);
	}

	protected void refresh() {
	}

	public static DBTypeEnum getDbType() {
		return dbTypeEnum;
	}

}
