package com.hh.hhdb_admin.mgr.table_open3.gui;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.PageBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.OpenTabSqlUtil;
import com.hh.frame.db_sql.conn.TabConnUtil;
import com.hh.frame.db_tabmg.beans.AbstractSqlBean;
import com.hh.frame.db_tabmg.task.ConnTask;
import com.hh.frame.db_tabmg.task.CountTask;
import com.hh.frame.db_tabmg.task.GetKeyTask;
import com.hh.frame.db_tabmg.task.QueryTask;
import com.hh.frame.db_tabmg.tool.TabTool;
import com.hh.frame.db_tabmg.util.OpenTableConnUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.col.SeqCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.TabHelpTool;
import com.hh.hhdb_admin.common.util.CsVarUtil;

public class OpenTableComp extends LastPanel {
	protected PkgModifyBar mBar;// 左下角添加删除行功能的组件--修改数据
	protected PkgOpenTable oTable;
	protected PkgPageBar pBar; // 右下角翻页组件
	protected PkgSearchBar sBar; // 右上角搜索组件
	protected File tmpDir;
	protected String pageSql;
	
	
	
	private PkgStatusBar statusBar;
	private PkgHeadBar headBar;
	private PkgLoadingPanel loading;// 加载画面

	protected String schemaName;
	private String tableName;
	private String sql;
	

	private JdbcBean jdbc;
	private Connection conn;

	private List<File> dataDirs = new ArrayList<>();

	protected Version ver;
	protected DBTypeEnum dbType;

	private ConnTask conntask; // 连接管理
	protected QueryTask queryTask;// 查询数据
	private CountTask countTask;// 计算总数
	private GetKeyTask pkTask;

	private boolean readonly;// 是否可读
	private long start;
	protected String nullSymbol;
	private List<String> keys;
	protected PageBean page = new PageBean(0, 30);
	
	

	
	public OpenTableComp(JdbcBean jdbcBean, String tableName, File tmpDir,boolean readonly) {
		this.nullSymbol = (CsVarUtil.CONF == null || null == CsVarUtil.CONF.getNullValue()) ? "<NULL>"
				: CsVarUtil.CONF.getNullValue();
		this.oTable = new PkgOpenTable(OpenTableComp.this);
		this.oTable.setNullSymbol(nullSymbol);
		this.mBar = new PkgModifyBar(this);
		this.jdbc = jdbcBean;
		this.pBar = new PkgPageBar(this);
		this.headBar = new PkgHeadBar(this);
		this.loading = new PkgLoadingPanel();
		this.schemaName = jdbcBean.getSchema();
		this.tmpDir = tmpDir;
		this.tableName = tableName;
		this.dbType = DriverUtil.getDbType(jdbcBean);
		this.ver = jdbcBean.getCurrentVersion();
		this.statusBar = new PkgStatusBar(readonly, pBar, mBar);
		this.queryTask = genQueryTask();
		this.conntask = new ConnTask(jdbcBean) {
			@Override
			protected void onExecuteSuccess(Connection con) {
				conn = con;
				try {
					refreshTask();
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(getComp(), e);
				}
			}

			@Override
			protected Connection initConn(JdbcBean b) throws Exception {
				return CsVarUtil.newConn(b);
			}
		};

		this.countTask = new CountTask() {

			@Override
			protected void onExecuteSuccess(int total) {
				page.setTotalNum(total);
			}
		};
		
		this.pkTask = new GetKeyTask(dbType,ver,schemaName,tableName) {
			protected void onExecuteSuccess(java.util.List<String> pks) {
				keys = pks;
			};
		};

	}

	/**
	 * 加载sql
	 * 
	 * @param sql
	 */
	public void loadSql(String sql) {
		this.readonly = true;
		this.statusBar = new PkgStatusBar(readonly, pBar, mBar);
		this.sql = sql;
		this.queryTask.setTool(null);
		loadData();
	}

	/**
	 * 加载数据
	 */
	public void loadData() {
		removeAll();
		try {
			set(loading);
			start = System.currentTimeMillis();
			PkgLoadingWorker loadingWorker = new PkgLoadingWorker(conntask, pkTask,countTask, queryTask) {

				@Override
				protected void loadingSuccess() {
					toDataMode();
					mBar.refresh();
				}

				@Override
				protected void loadingFaild(Throwable e) {
					Lg.error(getClass(), e);
					if (oTable == null || (oTable.getColumns().size() == 1
							&& oTable.getColumns().get(0).getName() == SeqCol.name)) {
						toErrorMode(e);
					} else {
						toDataMode();
					}
					PopDlgUtil.error(getComp(), e);
				}

			};
			loadingWorker.execute();
			loading.startProgress(loadingWorker);
		} catch (Exception e) {
			Lg.error(getClass(), e);
			PopDlgUtil.error(getComp(), e);
		}
	}

	public void close() {
		ConnUtil.close(conn);
		for (File f : dataDirs) {
			FileUtils.deleteQuietly(f);
		}
		dataDirs.clear();
	}

	/**
	 * 单独查询行数
	 */
	protected void loadCount() {
		this.countTask.setCount(0);
		start = System.currentTimeMillis();
		PkgLoadingWorker loadingWorker = new PkgLoadingWorker(conntask, countTask) {

			@Override
			protected void loadingSuccess() {
				pBar.refreshData(System.currentTimeMillis() - start);
			}

			@Override
			protected void loadingFaild(Throwable e) {
				Lg.error(getClass(), e);
				PopDlgUtil.error(getComp(), e);
			}

		};
		loadingWorker.execute();
	}

	/**
	 * 是否取消了保存数据
	 * 
	 * @return
	 */
	public boolean isCancelSaveData() {
		if (!readonly&&oTable!=null&&oTable.isDataChanged()) {
			if (PopDlgUtil.confirm(CsVarUtil.parentFrame, LangMgr.sLang(CsL.SAVE_OR_NOT))) {
				saveData();
				return false;
			}
		}
		return true;
	}

	/**
	 * 保存更改数据
	 * 
	 * @return
	 */
	protected void saveData() {
		Connection conn = null;
		try {
			mBar.setCompEnabled(false);
			oTable.stopEdit();
			conn = CsVarUtil.newConn(jdbc);
			List<AbstractSqlBean> sqlBeans = getListSqlBean();
			if (sqlBeans.size() > 0) {
				OpenTableConnUtil.saveCommit(this.dbType,ver, conn, sqlBeans);
			}
			loadData();
		} catch (Exception exception) {
			Lg.error(OpenTableComp.class, exception);
			PopDlgUtil.error(exception);
		} finally {
			mBar.refresh();
			ConnUtil.close(conn);
		}
	}

	/**
	 * 生成改动的sql预览
	 * 
	 * @return
	 */
	protected String genSql() {
		try {
			List<AbstractSqlBean> sqls = getListSqlBean();
			List<String> sqlArr = new ArrayList<>();
			if (sqls == null || tableName == null||readonly) {
				return "";
			}

			for (AbstractSqlBean sql : sqls) {
				sqlArr.add(sql.toString()+";");
			}
			return String.join("\n", sqlArr);
		} catch (Exception e) {
			Lg.error(getClass(), e);
			return "";
		}
	}

	private List<AbstractSqlBean> getListSqlBean() throws IOException {

		if (queryTask == null) {
			return null;
		}
		TabTool tool = new TabTool(dbType, ver, schemaName, tableName, oTable, queryTask.getTool().getNameType(),keys);
		return tool.getAllSqlBeans();

	}

	private void toDataMode() {
		removeAll();
		setHead(headBar.getComp());
		setWithScroll(oTable.getComp());
		setFoot(statusBar.getComp());
	}

	private void toErrorMode(Throwable e) {
		removeAll();
		JPanel contentPanel = new JPanel(new GridBagLayout());
		JPanel tipsPanel = new JPanel(new FlowLayout());
		String errorTips = "操作异常";
		JLabel errorLabel = new JLabel(errorTips);
		JButton reloadBtn = new JButton(LangMgr.lang(Word4.RELOAD)) ;
		reloadBtn.addActionListener(e1 -> loadData());
		tipsPanel.add(errorLabel);
		tipsPanel.add(reloadBtn);
		contentPanel.add(tipsPanel);
		set(contentPanel);
	}

	private void refreshTask() throws Exception {
		String sql2 = sql;
		countTask.setCon(conn);
		pkTask.setConn(conn);
		String tabFullName = SqlStrUtil.getQuotedFullName(dbType, ver, schemaName, tableName);
			if(!readonly) {
				List<String> cols = TabConnUtil.getSelColNames(dbType, ver, conn, schemaName, tableName);
				sql2 = OpenTabSqlUtil.getSelTabSql(dbType, ver, tabFullName, cols, headBar.getWhereTxt());
			}else {
				if(!StringUtils.isBlank(headBar.getWhereTxt())) {
					sql2 = sql2 + " where " + headBar.getWhereTxt();
				}
			}

			
		countTask.setDatasql(sql2);
		
		
		pageSql = OpenTabSqlUtil.getSelPagingSql(dbType, ver, sql2, page);

		File dataDir = new File(tmpDir, String.valueOf(System.currentTimeMillis()));
		dataDirs.add(dataDir);
		queryTask.setTool(new QueryTool(dbType, ver, conn, pageSql, dataDir, page.getNumPerPage() + 1, nullSymbol));
	}

	private QueryTask genQueryTask() {
		return new QueryTask() {
			@Override
			protected void onExecuteSuccess(File rfile) {
				try {
					mBar.setCompEnabled(false);
					boolean isRowStyle = oTable == null?true:oTable.isRowStyle();
					boolean autoResizeCol = oTable == null?true:oTable.isAutoResizeCol();
					int rowHeight = oTable == null?25:oTable.getComp().getRowHeight();
					boolean showLine = oTable == null?true:oTable.getComp().getShowHorizontalLines() && oTable.getComp().getShowVerticalLines();
					
					PkgOpenTable oldTable = oTable;
					oTable = new PkgOpenTable(OpenTableComp.this);
					oTable.setAutoResizeCol(autoResizeCol);
					oTable.setRowHeight(rowHeight);
					oTable.setNullSymbol(nullSymbol);
					oTable.setShowHorLine(showLine);
					oTable.setShowVerLine(showLine);
					
					
					QueryTool qtool = getTool();
					TabHelpTool tool = new TabHelpTool(qtool.getDbType(), qtool.getDbVer(), qtool.getNullFlag(), qtool.getSelTypes(), qtool.getColNames());
					List<Map<String, String>> datas = tool.toDataMap(rfile);
					
					List<AbsCol> cols = tool.createCol(oTable.getDataFile());
					for (AbsCol col : cols) {
						if (oldTable != null && oldTable.getColumn(col.getName()) != null) {
							col.setShow(oldTable.getColumn(col.getName()).isShow());
						}
						oTable.addCols(col);
					}
					
					
					sBar = new PkgSearchBar(OpenTableComp.this);
					oTable.setSearchToolBar(sBar);
					headBar.setSearchBar(sBar);
					oTable.load(datas, 1);
					oTable.setRowStyle(isRowStyle);
					pBar.refreshData(System.currentTimeMillis() - start);
				} catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		};
	}
	
	
	
}
