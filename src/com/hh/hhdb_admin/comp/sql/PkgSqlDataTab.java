package com.hh.hhdb_admin.comp.sql;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.csv.CsvUtil;
import com.hh.frame.db_tabmg.util.HiddenColUtil;
import com.hh.frame.dbcmd.Rs2TextTool;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.SqlWin;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.TabHelpTool;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.lob.LobJsonCol;

/**
 * 数据tab页
 *
 * @author hexu
 */
abstract class PkgSqlDataTab extends LastPanel {
	//消耗时间
	private LabelInput pagelabel = new LabelInput();
	private LabelInput countlabel = new LabelInput();
	private HButton preBtn, nextBtn;
	private HTable tab;
	private HTextArea hTextArea;
	private SearchToolBar stb;
	private CheckBoxInput textCb;
	private SelectBox typeBox;
	//当前页
	private int page = 1;
	//已加载数据的最大页
	private int dataMaxPage = 1;
	//如果到点击下一页没有数据时，才会给sqlMaxPage赋值
	private int sqlMaxPage = -1;
	private long beginMills;
	private TabHelpTool tabColTool;
	private QueryTool que;
	private Rs2TextTool tool;
	
	private String schema;	//当前结果集对应的模式
	private File file;	//当前页数据
	private JdbcBean jdbc;
	private DBTypeEnum dbtype;
	private DBTypeEnum.Version ver;

	public PkgSqlDataTab(SqlWin sqlWin,JdbcBean jdbc, QueryTool que, long runMills, PosBean posBean) throws Exception {
		super(false);
		this.que = que;
		this.jdbc=jdbc;
		this.dbtype=DriverUtil.getDbType(jdbc);
		this.ver=jdbc.getCurrentVersion();
		this.schema = jdbc.getSchema();
		tool = new Rs2TextTool(que.getColNames());
		
		HBarLayout l = new HBarLayout();
		l.setAlign(AlignEnum.LEFT);
		HBarPanel toolBar = new HBarPanel(l);
		//上一页
		preBtn = new HButton(LangMgr.lang(Word3.PREVIOUS)) {
			@Override
			public void onClick() {
				try {
					page--;
					page = page == 0 ? 1 : page;
					beginMills = System.currentTimeMillis();
					loadData(que.previous());
				} catch (Exception e) {
					Lg.error(PkgSqlDataTab.class, e);
					rollback(que.getConn(),e);
				}
			}
		};
		preBtn.setIcon(FaSolidEnum.BACKWARD,16);
		toolBar.add(preBtn);
		//下一页
		nextBtn = new HButton(LangMgr.lang(Word3.NEXT)) {
			@Override
			public void onClick() {
				try {
					beginMills = System.currentTimeMillis();
					File file = que.next();
					if (null == file) {
						PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.sLang(CsL.NO_NEXT_PAGE));
						sqlMaxPage = page;
						nextBtn.setEnabled(false);
					} else {
						page++;
						if (page > dataMaxPage) {
							dataMaxPage = page;
						}
						loadData(file);
					}
				} catch (Exception e) {
					Lg.error(PkgSqlDataTab.class, e);
					rollback(que.getConn(),e);
				}
			}
		};
		nextBtn.setIcon(FaSolidEnum.FORWARD,16);
		toolBar.add(nextBtn);
		//显示形式
		typeBox = new SelectBox("schemabox") {
			@Override
			public void onItemChange(ItemEvent e) {  //值改变事件
				if (e.getStateChange() == ItemEvent.SELECTED && null != tab) {
					tab.setRowStyle(getValue().equals("transverse"));
					tab.setRowPopMenu(getValue().equals("transverse") ? new PkgSqlExpPopMenu(que.getSelTypes(),schema) : null);
				}
			}
		};
		typeBox.addOption(LangMgr.lang(Word4.LATERAL_DISPLAY), "transverse");
		typeBox.addOption(LangMgr.lang(Word4.PORTRAIT_DISPLAY), "vertical");
		toolBar.add(typeBox);
		//导出
		HButton expBut = new HButton(LangMgr.lang(Word2.EXPORT)) {
			@Override
			public void onClick() {
				clickExport( schema,que.getSelect());
			}
		};
		expBut.setIcon(FaSolidEnum.RIGHT_FROM_BRACKET,16);
		if (!StringUtils.isNotBlank(this.jdbc.getOtherDbType())) toolBar.add(expBut);
		//查询结果集数量
		HButton counlistBut = new HButton(LangMgr.sLang(CsL.RESULT_TOTAL_NUMBER)) {
			@Override
			public void onClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							long xtime = System.currentTimeMillis();
							int count=sqlWin.getTotal(posBean);
							long xxtime = System.currentTimeMillis();
							countlabel.setValue(LangMgr.lang(Word2.RESULT)+"："+LangMgr.lang(Word2.TIME_CONSUMING) + "=" + (xxtime - xtime) + "ms,"
									+ LangMgr.lang(Word2.TOTAL)+"="+ count+LangMgr.lang(Word1.LINES));
						} catch (Throwable e) {
							PopDlgUtil.logErr(PkgSqlDataTab.class, comp, e);
						}
					}
				}).start();
			}
		};
		counlistBut.setIcon(FaSolidEnum.MAGNIFYING_GLASS,16);
		toolBar.add(counlistBut);
		//文本显示
		HPanel headPanel = new HPanel(new HDivLayout(GridSplitEnum.C9));
		textCb = new CheckBoxInput("text",LangMgr.lang(Word4.TEXT_DISPLAY)) {
			@Override
			protected void onClick(ActionEvent e) {
				try {
					showTable(file, runMills);
					headPanel.removeAll();
					if (this.isChecked()) {
						headPanel.add(toolBar);
					} else {
						headPanel.add(toolBar, stb);
					}
					headPanel.updateUI();
					typeBox.setEnabled(!this.isChecked());
				}catch (Exception e1){
					PopDlgUtil.logErr(PkgSqlDataTab.class, CsVarUtil.parentFrame, e1);
				}
			}
		};
		toolBar.add(textCb);
		
		showTable(que.first(), runMills);
		headPanel.add(toolBar, stb);
		setHead(headPanel.getComp());
	}
	protected abstract void clickExport(String schemaName, String sql);

	private void loadData(File csv) throws Exception {
		this.file = csv;
		List<Map<String, String>> data = tabColTool.toDataMap(csv);
		if (textCb.isChecked()) {
			tool.setQuickCmdLines(CsvUtil.csv2Data(csv, que.getNullFlag()));
			hTextArea.setText(tool.toString());
		} else {
			tab.setRowStyle(true); //正常显示才能刷显示数据
			tab.load(data, 1);
			tab.setRowStyle(typeBox.getValue().equals("transverse"));
		}
		pagelabel.setValue(LangMgr.lang(Word1.PAGE) + page + "  " + LangMgr.lang(Word2.TIME_CONSUMING) + "：" + (System.currentTimeMillis() - beginMills) + "ms");
		forbidden(data.size());
	}

	/**
	 * 组装数据显示
	 */
	private void showTable(File csv, long runMills) throws Exception {
		this.file = csv;
		tabColTool = new TabHelpTool(que.getDbType(),que.getDbVer(),que.getNullFlag(),que.getSelTypes(),que.getColNames());
		List<Map<String, String>> data = tabColTool.toDataMap(csv);
		
		if (textCb.isChecked()) {
			tool.setQuickCmdLines(CsvUtil.csv2Data(csv, que.getNullFlag()));
			hTextArea = new HTextArea();
			hTextArea.setText(tool.toString());
			set(hTextArea.getComp());
		} else {
			tab = new HTable();
			tab.setNullSymbol(que.getNullFlag());
//			tab.setHeadPopMenu(new DefHeaderPopMenu());
			tab.setRowPopMenu(new PkgSqlExpPopMenu(que.getSelTypes(),schema));
			tab.setRowHeight(25);
			List<AbsCol> cols = tabColTool.createCol(que.getBaseDir());
			cols.stream().filter(Objects::nonNull).forEach(absCol -> {
				if (absCol instanceof LobJsonCol) {
					absCol.setWidth(160);
				} else if (HiddenColUtil.isHiddenCol(dbtype, ver, absCol.getValue())) {
					absCol.setShow(false);
				} else {
					absCol.setWidth(50);//设置一个默认宽度，防止内容过长表格不会平铺显示
				}
				tab.addCols(absCol);
			});
			
			LastPanel lastPanel = new LastPanel(false);
			stb = new SearchToolBar(tab);
			lastPanel.setHead(stb.getComp());
			lastPanel.setWithScroll(tab.getComp());
			
			set(lastPanel.getComp());
			tab.setCellEditable(true);
			tab.load(data, 1);
			tab.setRowStyle(typeBox.getValue().equals("transverse"));
		}

		pagelabel.setValue(LangMgr.lang(Word1.PAGE) + page + "  " + LangMgr.lang(Word2.TIME_CONSUMING) + "：" + runMills + "ms");
		HBarLayout leftLayout = new HBarLayout();
		leftLayout.setAlign(AlignEnum.LEFT);
		HBarPanel barPanel = new HBarPanel(leftLayout);
		barPanel.add(pagelabel);
		barPanel.add(countlabel, null, 20, null);
		setFoot(barPanel.getComp());

		forbidden(data.size());
	}
	
	/**
	 * 翻页报错时回滚
	 * @throws Exception
	 */
	public  void rollback(Connection conn,Exception ex){
		try {
		
			if (DBTypeEnum.isPgSeries(dbtype,ver)) {
				if (ex.getMessage().contains("current transaction is aborted, commands ignored until end of transaction block")
						|| ex.getMessage().contains("当前事务被终止, 事务块结束之前的查询被忽略")) {
					boolean ok = PopDlgUtil.confirm(CsVarUtil.parentFrame, LangMgr.sLang(CsL.RUNNING_ERROR));
					if(ok) conn.rollback();
				}
			}
		}catch (Exception e){
			Lg.error(PkgSqlDataTab.class, e);
		}
	}

	/**
	 * 设置上下页按钮是否可用
	 */
	private void forbidden(int curRows) throws Exception {
		if (curRows < que.getNumPerPage() || page == sqlMaxPage) {
			nextBtn.setEnabled(false);
		} else {
			nextBtn.setEnabled(true);
		}
		if (page == 1) {
			preBtn.setEnabled(false);
		} else {
			preBtn.setEnabled(true);
		}
	}
}
