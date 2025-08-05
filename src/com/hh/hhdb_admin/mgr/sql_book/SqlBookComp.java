package com.hh.hhdb_admin.mgr.sql_book;

import com.hh.frame.file_client.HHFileUtil;
import com.hh.frame.file_client.openWay.OpenMgrTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.LangType;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.file_table.FmTabProxy;
import com.hh.frame.swingui.view.file_table.menu.builder.AbsTabMenuItemBuilder;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab_files.TabFileComp;
import com.hh.frame.swingui.view.tab_files.TabFileMenuPop;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.sql_book.util.PackOpen;
import com.hh.hhdb_admin.mgr.sql_book.util.QueryOpen;
import com.hh.hhdb_admin.mgr.sql_book.util.VMOpen;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

public abstract class SqlBookComp {
	private HPanel panel;
	private HDialog dialog;
	private TabFileComp tfComp;
	private LangType curLanguage;

	private OpenMgrTool openMgrTool;
	private final HashMap<String, Enum<?>> map = new LinkedHashMap<>();


	public SqlBookComp(String dPath) {
		try {
			QueryOpen queryOpen = new QueryOpen();
			VMOpen vmOpen = new VMOpen();
			PackOpen packOpen = new PackOpen();
			openMgrTool = new OpenMgrTool(new File(CsVarUtil.getEtcFile(), "file_open_config.json"), queryOpen, vmOpen,
					packOpen);
			map.put(queryOpen.getAppName(),
					FaSolidEnum.MAGNIFYING_GLASS);
			map.put(vmOpen.getAppName(),
					FaSolidEnum.RULER_COMBINED);
			map.put(packOpen.getAppName(),
					FaSolidEnum.PEN_TO_SQUARE);


			tfComp = new TabFileComp(HHFileUtil.newLocalFile(new File(dPath)),new File(CsVarUtil.getEtcFile(), "sqlbook.path.json"),new File(CsVarUtil.getEtcFile(), "sqlbook.collect.json"), CsVarUtil.parentFrame);
			
			TabFileMenuPop pop = new TabFileMenuPop(tfComp);
			pop.setOpenTool(openMgrTool, map);
			pop.addMenuItem(null, getTaskPopItem());
			tfComp.setMenuPop(pop);
			
			panel = new HPanel();
			panel.setLastPanel(tfComp.getHComp());

			curLanguage = CsVarUtil.CONF.getLang();
		} catch (Exception e) {
			Lg.error(SqlBookComp.class, e);
			PopDlgUtil.error(CsVarUtil.parentFrame, e);
		}
	}

	public void show() throws Exception {
		dialog = new HDialog(CsVarUtil.parentFrame, 900, 720);
		dialog.setIconImage(IconFileUtil.getLogo());
		dialog.setWindowTitle(LangMgr.lang(Word4.FILE_MANAGER));
		dialog.setRootPanel(panel);
		dialog.getWindow().setLocationRelativeTo(null);
		tfComp.refresh();
		dialog.setStatusBar(new HBarPanel());
		dialog.show();
	}

	/**
	 * 打开任务
	 */
	private AbsTabMenuItemBuilder getTaskPopItem() {
		return new AbsTabMenuItemBuilder(LangMgr.sLang(CsL.OPEN_TASK),
				FaSolidEnum.HOURGLASS_HALF) {
			@Override
			public void refreshWhenPop(FmTabProxy proxy, HMenuItem item) {
				if (proxy.isLocal() && proxy.isOneRowSelected() && proxy.isOnlyFilesSelected()) {
					item.setVisible(true);
					item.setEnabled(true);
				} else {
					item.setVisible(false);
				}

			}

			@Override
			protected void onAction(FmTabProxy proxy) {

				try {
					File f = new File(proxy.getSelectFilePath());
					if (f != null) {
						openTask(f.getAbsolutePath());
						dialog.dispose();
					}
				} catch (Exception e) {
					Lg.error(SqlBookComp.class, e);
					PopDlgUtil.error(dialog, e);
				}

			}
		};
	}

	public LangType getCurLanguage() {
		return curLanguage;
	}

	protected abstract void openTask(String filePath) throws Exception;

	public String getCurrentDir() throws Exception {
		return tfComp.getIFile().getPath();
	}
}
