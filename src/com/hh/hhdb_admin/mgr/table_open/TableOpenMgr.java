package com.hh.hhdb_admin.mgr.table_open;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameComp;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;
import com.hh.hhdb_admin.mgr.table.TableMgr;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class TableOpenMgr extends AbsGuiMgr {
	public static final String CMD_OPEN_TABLE = "OPEN_TABLE";        //打开表

	private Map<String, ModifyTabDataComp> map = new HashMap<>();
	private String loginId;
	public static final String OPEN_TMP = "open_tmp";
	public static File tmpFile;
	public static int maxPoolSize;

	public static ExecutorService threadPool;


	@Override
	public void init(JsonObject jObj) {
		try {
			initPool();
			LangMgr2.loadMerge(TableOpenMgr.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//启动时在MainFrameMgr中初始化
//		tmpFile = new File(StartUtil.workspace, OPEN_TMP);
	}

	@Override
	public CsMgrEnum getType() {
		return CsMgrEnum.FUNCTION;
	}

	@Override
	public String getHelp() {
		return null;
	}

	@Override
	public void doPush(JsonObject msg) throws Exception {
		String openId = "";
		//StartUtil.workspase
		if (initLoginBean(msg)) {
			return;
		}
		if (!tmpFile.exists()) {
			tmpFile.mkdirs();
		}
		LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(loginId);
		switch (GuiJsonUtil.toStrCmd(msg)) {
			case StartUtil.CMD_CLOSE:
				openId = GuiJsonUtil.toPropValue(msg, StartUtil.CMD_ID);
				ModifyTabDataComp tabDataComp = map.get(openId);
				tabDataComp.close();
				map.remove(openId);
				StartUtil.eng.rmFromSharedMap(openId);
				break;
			case CMD_OPEN_TABLE:
				String schemaName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
				String table = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_TABLE);
				ModifyTabDataComp tablePanel = null;
				//判断是否已打
				boolean bool = true;
				for (String string : map.keySet()) {
					tablePanel = map.get(string);
					if (table.equals(tablePanel.getTabName()) && schemaName.equals(tablePanel.getSchemaName())) {
						openId = string;
						bool = false;
						break;
					}
				}
				if (bool) {
					//打开新的
					try {
						tablePanel = new ModifyTabDataComp();
						openId = StartUtil.eng.push2SharedMap(tablePanel);
						tablePanel.init(loginBean.getJdbc(), schemaName, table, tmpFile);
						tablePanel.refreshTab();
					} catch (Exception e) {
						e.printStackTrace();
					}
					map.put(openId + "", tablePanel);
				}
				if (((MainFrameComp) StartUtil.parentFrame).getTabPane() == null) {
					HDialog dialog = new HDialog(StartUtil.parentFrame, HDialog.LARGE_WIDTH);
					HPanel panel = new HPanel();
					panel.setLastPanel(tablePanel);
					((JDialog) dialog.getWindow()).setTitle(tablePanel.getSchemaName() + "." + tablePanel.getTabName());
					((JDialog) dialog.getWindow()).setResizable(false);
					dialog.setRootPanel(panel);
					dialog.show();
				} else {
					StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
							.add(StartUtil.CMD_ID, openId).add("title", schemaName + "." + table).add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.TABLE_OPEN.name()));
				}
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
	}

	private boolean initLoginBean(JsonObject msg) throws Exception {
		loginId = msg.getString("loginId");
		if (StringUtils.isBlank(loginId)) {
			loginId = TableMgr.getLoginBeanId(CsMgrEnum.TABLE);
			if (StringUtils.isBlank(loginId)) {
				PopPaneUtil.info("获取连接失败！");
				return true;
			}
		}
		return false;
	}

	@Override
	public JsonObject doCall(JsonObject msg) throws Exception {
		JsonObject res = new JsonObject();
		String openId = "";
		if (initLoginBean(msg)) {
			return res;
		}
		LoginBean loginBean = (LoginBean) StartUtil.eng.getSharedObj(loginId);
		switch (GuiJsonUtil.toStrCmd(msg)) {
			case CMD_OPEN_TABLE:
				//测试打开表
				String schemaName = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA);
				String table = GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_TABLE);

				//判断是否已打开
				boolean bool = true;
				for (String string : map.keySet()) {
					if (table.equals(map.get(string).getTabName()) && schemaName.equals(map.get(string).getSchemaName())) {
						res.add(StartUtil.CMD_ID, string);
						bool = false;
						break;
					}
				}

				if (bool) { //打开新的
					ModifyTabDataComp tablePanel = new ModifyTabDataComp(loginBean.getJdbc(), schemaName, table, tmpFile);
					tablePanel.refreshTab();
					openId = StartUtil.eng.push2SharedMap(tablePanel);
					res.add(StartUtil.CMD_ID, openId + "");

					map.put(openId, tablePanel);
				}
				break;
			default:
				unknowMsg(msg.toPrettyString());
		}
		return res;
	}

	public static void initPool() {
		int n = Runtime.getRuntime().availableProcessors();
		maxPoolSize = (int) Math.ceil(n * 2 * ((1 - 0.9) * 100));
		ThreadFactory namedThreadFactory = new BasicThreadFactory.Builder().namingPattern("table-open-pool-%d").daemon(true).build();
		threadPool = new ThreadPoolExecutor(n, maxPoolSize,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(1024), namedThreadFactory,
				new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	/**
	 * 中英文
	 *
	 * @param key
	 * @return
	 */
	public static String getLang(String key) {
		return LangMgr2.getValue(TableOpenMgr.class.getName(), key);
	}
}
