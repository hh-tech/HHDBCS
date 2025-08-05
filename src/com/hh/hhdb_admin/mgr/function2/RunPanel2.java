package com.hh.hhdb_admin.mgr.function2;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.db_sql.ProcFunSqlUtil;
import com.hh.frame.db_sql.conn.ProFunConnUtil;
import com.hh.frame.db_sql.info.SqlFunBean;
import com.hh.frame.db_sql.info.SqlParamBean;
import com.hh.frame.db_sql.info.SqlProcBean;
import com.hh.frame.db_sql.info.SqlParamBean.ParamType;
import com.hh.frame.dbtask2.TaskType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.TaskAddBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.sql.ProcFunRunPanel;
import com.hh.hhdb_admin.mgr.function.ui.run.RunBaseForm;

public class RunPanel2 extends LastPanel {
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String VALUE = "value";

	private String schema;
	private String name;
	private boolean isFun;

	private HTable table;
	private List<SqlParamBean> beans;

	protected void callDbTask(String schemaName, String sql) {
		TaskAddBean bean = new TaskAddBean();
		bean.setTaskType(TaskType.QUERY_EXP);
		bean.setSchema(schemaName);
		bean.setQuerySql(sql);
		CsVarUtil.eng2.doCall(CsMgrEnum.DB_TASK, bean);
	}

	private ProcFunRunPanel panel;

	public RunPanel2() throws Exception {

		panel = new ProcFunRunPanel(new SqlAreaConf(), CsVarUtil.jdbcCloneWithSsh()) {
			@Override
			protected void callDbTask(String schemaName, String sql) {
				RunPanel2.this.callDbTask(schemaName, sql);
			}

		};

		LastPanel paramLast = new LastPanel();
		table = new HTable();

		DataCol dc = new DataCol(NAME, LangMgr.lang(Word2.PARAM));
		dc.setCellEditable(false);
		DataCol dcType = new DataCol(TYPE, LangMgr.lang(Word2.TYPE));
		dcType.setCellEditable(false);
		table.addCols(dc, dcType, new DataCol(VALUE, LangMgr.lang(Word1.VALUE)));
		// 根据用户输入参数值更新sql
		table.getComp().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				try {
					if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
						refreshArea();
					}
				} catch (Exception e) {
					Lg.error(RunBaseForm.class, e);
					PopDlgUtil.error(CsVarUtil.parentFrame, e);
				}
			}
		});
		table.load(null, 1);

		table.getComp().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		paramLast.setWithScroll(table.getComp());
		HeaderConfig paramconfig = new HeaderConfig(LangMgr.lang(Word2.PARAMETER));
		paramconfig.setFixTab(true);

		panel.getRsPanel().setPanel("param", paramLast, paramconfig);
		panel.getRsPanel().selectPanel("param");
		set(panel.getComp());
	}

	public void setText(String text) {
		panel.setText(text);
	}

	public void init(String schema, String name, String oid, boolean isFun) throws Exception {
		this.schema = schema;
		this.name = name;
		this.isFun = isFun;
		beans = ProFunConnUtil.selectParamBean(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion(),
				CsVarUtil.conn, schema, name, oid, isFun);
		refreshArea();
		table.load(toTableData(beans), 1);
	}

	private void refreshArea() throws Exception {
		List<HTabRowBean> data = table.getRowBeans(RowStatus.UPDATE);
		for (SqlParamBean b : beans) {
			for (HTabRowBean hb : data) {
				if (hb.getOldRow().get(NAME).equals(b.getParamName())) {
					b.setParamValue(hb.getCurrRow().get(VALUE));
				}
			}
		}

		SqlProcBean b;
		if (isFun) {
			b = new SqlFunBean(name);

		} else {
			b = new SqlProcBean(name);
		}
		b.setSchemaName(schema);
		b.setParams(beans);
		String sql = getRunSql(b);
		if (StringUtils.isBlank(sql)) {
			throw new Exception("暂不支持");
		}
		panel.setText(sql);

	}

	private List<Map<String, String>> toTableData(List<SqlParamBean> beans) {
		List<Map<String, String>> maps = new ArrayList<>();
		for (SqlParamBean b : beans) {
			if (b.getParamType() == ParamType.IN || b.getParamType() == ParamType.INOUT) {
				Map<String, String> m = new HashMap<>();
				m.put(NAME, b.getParamName());
				m.put(TYPE, b.getDataType());
				m.put(VALUE, "");
				maps.add(m);
			}

		}
		return maps;
	}

	private String getRunSql(SqlProcBean b) {
		return ProcFunSqlUtil.getDebugRunSql(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion(), b,false);
	}

	public void close() throws IOException {
		panel.close();
	}
}
