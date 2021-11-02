package com.hh.hhdb_admin.mgr.sql_book;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.ImageIcon;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.file_client.HHFileUtil;
import com.hh.frame.file_client.openWay.OpenMgrTool;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab_files.TabFileComp;
import com.hh.frame.swingui.view.tab_files.TabFileRequires;
import com.hh.frame.swingui.view.tab_files.common.FileTableUtil;
import com.hh.frame.swingui.view.tab_files.menu.DirPopMenu;
import com.hh.frame.swingui.view.tab_files.menu.FilePopMenu;
import com.hh.frame.swingui.view.tab_files.menu.item.FmPopMenuItem;
import com.hh.frame.swingui.view.tab_files.menu.item.MenuItemState;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.sql_book.util.PackOpen;
import com.hh.hhdb_admin.mgr.sql_book.util.QueryOpen;
import com.hh.hhdb_admin.mgr.sql_book.util.TypeOpen;
import com.hh.hhdb_admin.mgr.sql_book.util.VMOpen;

public abstract class SqlBookComp {
    private HPanel panel;
    private HDialog dialog;
    private TabFileComp tfComp;
    private LangEnum curLanguage;
    private static final String domainName = SqlBookComp.class.getName();
    private final static String LK_SQL_BOOK = "SQL_BOOK";
    private final static String LK_OPEN_TASK = "OPEN_TASK";

    private OpenMgrTool openMgrTool;
    private final HashMap<String, ImageIcon> map = new LinkedHashMap<>();

    static {
        try {
			LangMgr2.loadMerge(SqlBookComp.class);
		} catch (IOException e) {
			PopPaneUtil.error(e);
		}
    }

    public SqlBookComp(String dPath, HDialog dialog) {
        try {
            this.dialog = dialog;
            QueryOpen queryOpen = new QueryOpen();
            VMOpen vmOpen = new VMOpen();
            PackOpen packOpen = new PackOpen();
            TypeOpen typeOpen = new TypeOpen();
            openMgrTool = new OpenMgrTool(new File(StartUtil.getEtcFile(),"file_open_config.json"), queryOpen, vmOpen, packOpen, typeOpen);
            map.put(queryOpen.getAppName(), IconFileUtil.getIcon(new IconBean(CsMgrEnum.SQL_BOOK.name(), "query", IconSizeEnum.SIZE_16)));
            map.put(vmOpen.getAppName(), IconFileUtil.getIcon(new IconBean(CsMgrEnum.SQL_BOOK.name(), "template", IconSizeEnum.SIZE_16)));
            map.put(packOpen.getAppName(), IconFileUtil.getIcon(new IconBean(CsMgrEnum.PACKAGE.name(), "pack", IconSizeEnum.SIZE_16)));
            map.put(typeOpen.getAppName(), IconFileUtil.getIcon(new IconBean(CsMgrEnum.TYPE.name(), "edit", IconSizeEnum.SIZE_16)));

            FilePopMenu fileMenu = new FilePopMenu(openMgrTool,map);
            
            tfComp = new TabFileComp(HHFileUtil.newLocalFile(new File(dPath)), new DirPopMenu(), fileMenu) {
                @Override
                protected void openFile(String fileName) {
                    try {
                    	File f = new File(getCurrentDir() + File.separator + fileName);
                        openMgrTool.getDefToos(fileName).openFile(f,ChardetUtil.detectCharset(f));
                    } catch (Exception e) {
                        e.printStackTrace();
                        PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                    }
                }
            };
            tfComp.setFileOpenOnDbClick(true);
            TabFileRequires requires = new TabFileRequires(IconFileUtil.getLogo(), StartUtil.parentFrame);
            panel = tfComp.getPanel(requires);
            fileMenu.addPopMenuItem(getTaskPopItem(fileMenu.getTable(),dPath));
            curLanguage = LangMgr2.getDefaultLang();
        }catch (Exception e){
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

    public void show() throws Exception {
        dialog.setWindowTitle(getLang(LK_SQL_BOOK));
        dialog.setSize(900, 720);
        dialog.setRootPanel(panel);
        tfComp.refresh();
        dialog.show();
    }
    /**
     * 打开任务
     */
    private FmPopMenuItem getTaskPopItem(HTable table,String path) {
    	return new FmPopMenuItem(getLang(LK_OPEN_TASK),IconFileUtil.getIcon(new IconBean(CsMgrEnum.SQL_BOOK.name(), "task", IconSizeEnum.SIZE_16))) {
			
			@Override
			public MenuItemState getStateWhenPop() {
				if(table.getSelectedRowBeans().size()==1) {
					return MenuItemState.enable;
				}else {
					return MenuItemState.invisible;
				}
			}
			
			
			@Override
			protected void onAction() {
				 try {
		                File f = new File(path,FileTableUtil.getFileName(table.getSelectedRowBeans().get(0)));
		                if (f != null) {
	                        openTask(f.getAbsolutePath());
	                        dialog.dispose();
		                }
	            	 } catch (Exception e) {
	                     e.printStackTrace();
	                     PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
	                 }
			}
		};
    }


    public LangEnum getCurLanguage() {
        return curLanguage;
    }

    public static String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(domainName, key);
    }

    protected abstract void openTask(String filePath) throws Exception;

	public String getCurrentDir() throws Exception {
		return tfComp.getCurrentDir();
	}
}
