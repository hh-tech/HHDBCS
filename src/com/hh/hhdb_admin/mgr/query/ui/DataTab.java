package com.hh.hhdb_admin.mgr.query.ui;

import com.alee.painter.PainterSupport;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.dbtask.TaskType;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.frame.swingui.view.tab.menu.body.ExpTabBodyPopMenu;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.db_task.TaskMgr;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.query.util.QuerUtil;
import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;
import com.hh.hhdb_admin.mgr.table_open.comp.LobJsonCol;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 数据tab页
 * @author hexu
 *
 */
public class DataTab extends LastPanel {
	private static String logName = DataTab.class.getSimpleName();
	//消耗时间
	private LabelInput pagelabel = new LabelInput();
	private LabelInput countlabel = new LabelInput();
	private HButton upperBut,lowerBut;
	private HTable tab;
	private SearchToolBar stb;
	//当前页
	private int page = 1;
	//已加载数据的最大页
	private int dataMaxPage = 1;
	//如果到点击下一页没有数据时，才会给sqlMaxPage赋值
	private int sqlMaxPage = -1;
	private int rowsum = 30;     //每页显示条数
	private String nullSign;     //空值显示标记
	private long beginMills;
	private ModifyTabTool tabColTool;
	private QueryTool que;
	private JdbcBean jdbc;

	private SelectBox typeBox;

	public DataTab(JdbcBean jdbc,QueryTool que,int rowsum,String nullSign,long runMills)throws Exception{
		super(false);
		this.jdbc = jdbc;
		this.que = que;
		this.rowsum = rowsum;
		this.nullSign = nullSign;
		DBTypeEnum dbtype = DriverUtil.getDbType(jdbc);

		HBarLayout l = new HBarLayout();
		l.setAlign(AlignEnum.LEFT);
		HBarPanel toolBar = new HBarPanel(l);
		//上一页
		upperBut = new HButton(QueryMgr.getLang("upper")) {
			@Override
			public void onClick() {
				try {
					page--;
					page = page == 0 ? 1 : page;
					beginMills = System.currentTimeMillis();
					loadData(que.getSelTypes(), que.getColNames(), que.previous());
				} catch (Exception e1) {
					e1.printStackTrace();
					logUtil.error(logName, e1);
				}
			}
		};
		upperBut.setIcon(QueryMgr.getIcon("previouspage"));
		toolBar.add(upperBut);
		//下一页
		lowerBut = new HButton(QueryMgr.getLang("lower")) {
			@Override
			public void onClick() {
				try {
					beginMills = System.currentTimeMillis();
					File file = que.next();
					if (null == file) {
						JOptionPane.showMessageDialog(null, QueryMgr.getLang("noNext"),QueryMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
						sqlMaxPage = page;
						lowerBut.setEnabled(false);
					}else{
						page++;
						if(page>dataMaxPage) {
							dataMaxPage = page;
						}
						loadData(que.getSelTypes(), que.getColNames(), file);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					logUtil.error(logName, e1);
					try {
						QuerUtil.nextPageRollback(que.getConn(), e1);
					}catch (Exception e) {
						e1.printStackTrace();
					}
				}
			}
		};
		lowerBut.setIcon(QueryMgr.getIcon("nextpage"));
		toolBar.add(lowerBut);

		//显示形式
		typeBox = new SelectBox("schemabox"){
			@Override
			public void onItemChange(ItemEvent e) {  //值改变事件
				if ( e.getStateChange() == ItemEvent.SELECTED && null != tab) tab.setRowStyle(getValue().equals("transverse"));
			}
		};
		typeBox.addOption(QueryMgr.getLang("transverse"),"transverse");
		typeBox.addOption(QueryMgr.getLang("vertical"),"vertical");
		toolBar.add(typeBox);

		//导出
		HButton expBut = new HButton(QueryMgr.getLang("exp")) {
			@Override
			public void onClick() {
				StartUtil.eng.doPush(CsMgrEnum.DB_TASK, GuiJsonUtil.toJsonCmd(TaskMgr.CMD_ADD_TASK)
						.add("schema", jdbc.getSchema())
						.add(TaskMgr.PARAM_TASK_TYPE, TaskType.EXP_QUERY_AS_XLS.name())
						.add("sql", que.getSelect())
						.add(TaskMgr.PARAM_AUTO_START, true));
			}
		};
		expBut.setIcon(QueryMgr.getIcon("exportall"));
		toolBar.add(expBut);
		//查询结果集数量
		HButton counlistBut = new HButton(QueryMgr.getLang("counlist")) {
			@Override
			public void onClick() {
				new Thread(new Runnable() {
					@Override
					public void run() {
						countlabel.setValue(QuerUtil.getCount(jdbc,que.getSelect()));
					}
				}).start();
			}
		};
		counlistBut.setIcon(QueryMgr.getIcon("zoom"));
		toolBar.add(counlistBut);
		
		showTable(dbtype, que.getSelTypes(), que.getColNames(), que.first(),runMills);
		
		HPanel headPanel = new HPanel(new HDivLayout(GridSplitEnum.C9));
		headPanel.add(toolBar, stb);
		PainterSupport.setMargin(headPanel.getComp(), 5, 0, 5, 0);
		setHead(headPanel.getComp());
	}
	private void loadData(List<Enum<?>> selTypes,List<String> colNames,File csv) throws Exception {
		List<Map<String, String>> data = tabColTool.toDataMap(selTypes, colNames, csv);
		tab.setRowStyle(true); //正常显示才能刷显示数据
		tab.load(data, 1);
		tab.setRowStyle(typeBox.getValue().equals("transverse"));
		pagelabel.setValue(QueryMgr.getLang("page")+page+"  "+QueryMgr.getLang("timeSpent") + "：" + (System.currentTimeMillis() - beginMills) + "ms");
		forbidden(data.size());
	}
	/**
	 * 组装数据显示
	 */
	private void showTable(DBTypeEnum dbtype,List<Enum<?>> selTypes,List<String> colNames,File csv,long runMills) throws Exception {
		tab = new HTable();
		tab.setNullSymbol(nullSign);
//		tab.setHeadPopMenu(new DefHeaderPopMenu());
		tab.setRowPopMenu(new ExpTabBodyPopMenu());
		tabColTool = new ModifyTabTool(dbtype, tab);
		tabColTool.setReadOnly(true);
		List<Map<String, String>> data = tabColTool.toDataMap(selTypes, colNames, csv);
		tab.setRowHeight(25);
		tab.setRowStyle(true);
		List<AbsCol> cols = tabColTool.createCol(que,jdbc,null,null);
		cols.stream().filter(Objects::nonNull).forEach(absCol -> {
			if (absCol instanceof LobJsonCol) {
				absCol.setWidth(160);
			} else if (SelectTableSqlUtil.getHideColNames().contains(absCol.getValue())) {
				absCol.setShow(false);
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

		pagelabel.setValue(QueryMgr.getLang("page")+page+"  "+QueryMgr.getLang("timeSpent") + "：" + runMills + "ms");
		HBarLayout leftLayout = new HBarLayout();
		leftLayout.setAlign(AlignEnum.LEFT);
		HBarPanel barPanel = new HBarPanel(leftLayout);
		barPanel.add(pagelabel);
		barPanel.add(countlabel,null,20,null);
		setFoot(barPanel.getComp());

		forbidden(data.size());
	}
	/**
	 * 设置上下页按钮是否可用
	 */
	private void forbidden(int curRows)throws Exception{
		if(curRows<rowsum||page==sqlMaxPage) {
			lowerBut.setEnabled(false);
		}else {
			lowerBut.setEnabled(true);
		}
		if(page==1) {
			upperBut.setEnabled(false);
		}else{
			upperBut.setEnabled(true);
		}
	}
}
