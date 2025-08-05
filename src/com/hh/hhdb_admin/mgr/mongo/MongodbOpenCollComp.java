package com.hh.hhdb_admin.mgr.mongo;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.PageBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.mongo.obj.MongoDocUtil;
import com.hh.frame.swingui.cs_desk.jsonEditor.PkgAbsTextArea;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.IntSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import org.bson.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.*;

import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class MongodbOpenCollComp extends LastPanel {
	protected final Map<String, BsonDocument> docMap = new HashMap<>();
	protected final ArrayList<Map<String, String>> data = new ArrayList<>();
	protected final LinkedHashSet<String> headers = new LinkedHashSet<>();
	protected final MongoColl mongoColl;
	protected final String collName;
	protected List<String> idList = new ArrayList<>();

	protected DocTable docTable;
	protected final HashMap<String, String> headerTypeMap = new HashMap<>();

	protected PkgAbsTextArea area;
	protected PageBean page = new PageBean(0, 30);
	private HButton queryBtn;
	private IntSpinner limitSpinner;
	protected MongoColl queryColl = null;
	private LabelInput statusInput = new LabelInput();
	private HButton modifyBtn = null;
	private HButton removeBtn = null;
	private HButton refreshBtn = null;

	public MongodbOpenCollComp(MongoColl mongoColl, String collName) {
		// mongoColl该连接是公用的，用来修改和删除操作，查询操作需要启动新线程并建立新连接
		this.mongoColl = mongoColl;
		this.collName = collName;

		docTable = new DocTable(docMap);
		docTable.getComp().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				docTable.selectCell(e);
				if (e.getButton() == MouseEvent.BUTTON2)
					enter();
			}
		});
		docTable.setRowPopMenu(new DocPopMenu(this));

		// 设置查询和搜索工具条
		SearchToolBar searchToolBar = new SearchToolBar(docTable);
		queryBtn = new HButton(LangMgr.lang(Word2.EXECUTE)) {
			@Override
			public void onClick() {
				if (queryBtn.getComp().getText().equals(LangMgr.lang(Word2.EXECUTE))) {
					refresh();
				} else {
					statusInput.setValue("停止查询...");
					closeQueryDb();
					System.out.println("connect closed:" + LocalDateTime.now());
				}
			}
		};
		queryBtn.setIcon(FaSolidEnum.PLAY, 16);

		HBarPanel toolbar = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		toolbar.add(queryBtn);
		toolbar.add(new LabelInput(" 最多加载"));
		limitSpinner = new IntSpinner(50, 10, 1000, 10);
		toolbar.add(limitSpinner);
		toolbar.setLastHComp(searchToolBar);
		setHead(toolbar.getComp());

		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_JSON);
		area = new PkgAbsTextArea(conf) {
			@Override
			protected void onChange(boolean hasUpdate) {
			}//
		};

		String example = "#查询示例\n" + "#{\r\n" + "#    $or: [\r\n"
				+ "#        { \"_id\" : { \"$oid\" : \"667d1bfda314f1901a072f96\" } },\r\n"
				+ "#        { \"item\" : { \"$regex\" : \".*品3.*\" } }\r\n" + "#    ]\r\n" + "#}\n\n";
		area.setText(example + "{}");

		// 设置中间的查询面板和表格面板
		HPanel queryPanel = new HPanel();
		LastPanel areaLast = new LastPanel();
		areaLast.set(area.getComp());
		queryPanel.setLastPanel(areaLast);

		LastPanel tabLast = new LastPanel();
		tabLast.setWithScroll(docTable.getComp());
		HPanel tablePanel = new HPanel();
		tablePanel.setLastPanel(tabLast);

		HSplitPanel splitPanel = new HSplitPanel(false);
		splitPanel.setPanelOne(queryPanel);
		splitPanel.setPanelTwo(tablePanel);
		splitPanel.setSplitWeight(0.2);
		this.set(splitPanel.getComp());

		// 设置底部状态栏
		HBarPanel statusBar = new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
		modifyBtn = new HButton(LangMgr.lang(Word2.EXAMINE)+LangMgr.lang(Word2.MODIFY)) {
			@Override
			public void onClick() {
				List<HTabRowBean> rows = docTable.getSelectedRowBeans();
				String id = rows.get(0).getOldRow().get(MongoDocUtil.RAW_ID);
				try {
					MongodbDocAddUpdateComp comp = new MongodbDocAddUpdateComp(CsVarUtil.conn, mongoColl.getDbName(), collName, id, null) {
						@Override
						protected void refresh(boolean isAdded) {
							MongodbOpenCollComp.this.refresh();
						}
					};
					comp.showDialog();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		modifyBtn.setEnabled(false);
		removeBtn = new HButton(LangMgr.lang(Word2.DELETE)) {
			@Override
			public void onClick() {
				List<HTabRowBean> rows = docTable.getSelectedRowBeans();
				rows.forEach(tabRowBean -> {
					String id = tabRowBean.getOldRow().get(MongoDocUtil.RAW_ID);
					mongoColl.delete(collName, id);
				});
				refresh();
			}
		};
		removeBtn.setEnabled(false);
		refreshBtn = new HButton(LangMgr.lang(Word2.REFRESH)) {
			@Override
			public void onClick() {
				refresh();
			}
		};
		statusBar.add(modifyBtn, removeBtn, refreshBtn);
		HBarPanel infoBar = new HBarPanel();
		infoBar.add(statusInput);
		statusBar.setLastHComp(infoBar);
		this.setFoot(statusBar.getComp());

		docTable.getComp().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (docTable.getSelectedRowCount() == 1) {
					modifyBtn.setEnabled(true);
					removeBtn.setEnabled(true);
				} else if (docTable.getSelectedRowCount() > 1) {
					modifyBtn.setEnabled(false);
					removeBtn.setEnabled(true);
				} else {
					modifyBtn.setEnabled(false);
					removeBtn.setEnabled(false);
				}
			}
		});

		refresh();
	}

	protected void enter() {
		DocBean docBean = docTable.getSelectedDocBean();
		if (docBean == null || (docBean.type != BsonType.DOCUMENT && docBean.type != BsonType.ARRAY)
				|| MongoDocUtil.DOC_ID.equals(docBean.key))
			return;
	}

	private void closeQueryDb() {
		if (queryColl != null) {
			queryColl.close();
			queryColl = null;
		}
	}

	public void refresh() {
		new Thread(new SwingWorker<String, String>() {
			@Override
			public String doInBackground() {
				try {
					statusInput.setValue("执行查询中...");
					queryBtn.setIcon(FaSolidEnum.STOP, 16);
					queryBtn.setText(LangMgr.lang(Word2.STOP));
					clear();
					queryColl = new MongoColl(CsVarUtil.newConn(CsVarUtil.jdbc), mongoColl.getDbName());
					refreshTable();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "";
			}

			@Override
			protected void done() {
				queryBtn.setIcon(FaSolidEnum.PLAY, 16);
				queryBtn.setText(LangMgr.lang(Word2.EXECUTE));
				closeQueryDb();
				statusInput.setValue("记录总数：" + data.size());
			}
		}).start();
	}

	private void clear() {
		docMap.clear();
		headers.clear();
		headerTypeMap.clear();
		data.clear();
		docTable.clearCols();
		docTable.getComp().setModel(new DefaultTableModel(new Object[][] {}, new String[] {}));
		;
	}

	protected void refreshTable() {
		// 获取所有文档
		String txt = area.getText();
		// 去除以#开头或者空格和#开头的注释
		txt = txt.replaceAll("(?m)^[ \t]*#.*$", "").replaceAll("(?m)^[ \t]*\r?\n", "");
		Map<String, BsonDocument> result = queryColl.find(collName, txt, limitSpinner.getInt());
		docMap.putAll(result);
		// 填到表格
		// 总是把id显示在第一列
		System.out.println("fill tab:" + LocalDateTime.now());

		if (docMap.size() > 0) {
			headers.add(MongoDocUtil.RAW_ID);
			BsonDocument doc = docMap.values().iterator().next();
			doc.forEach((key, value) -> {
				if (!key.equals(MongoDocUtil.RAW_ID))
					headers.add(key);
				headerTypeMap.put(key, value.getBsonType().toString());
			});
		}

		docMap.values().forEach(doc -> {
			Map<String, String> map = new HashMap<>();
			headers.forEach(header -> {
				BsonValue obj = doc.get(header);
				if (obj != null) {
					String text = MongoDocUtil.renderText(obj);
					map.put(header, text);
				}
			});
			data.add(map);
		});

		headers.forEach(header -> {
			String type = headerTypeMap.get(header);
			String name = type == null ? header : header + "(" + type + ")";
			docTable.addCols(new DocCol(header, name));
		});

		docTable.refreshToolBar();
		docTable.load(data, 1);
		System.out.println("fill tab done:" + LocalDateTime.now());
	}

	public boolean isCancelSaveData() {
		closeQueryDb();
		return true;
	}

	public DocTable getDocTable() {
		return docTable;
	}
}
