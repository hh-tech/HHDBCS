package com.hh.hhdb_admin.mgr.sql_book;

import com.hh.frame.file_client.HHFileUtil;
import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab_files.TabFileComp;
import com.hh.frame.swingui.view.tab_files.menu.DirPopMenu;
import com.hh.frame.swingui.view.tab_files.menu.FilePopMenu;
import com.hh.frame.swingui.view.tab_files.utils.TabFileUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.io.FileUtils;

import java.awt.event.MouseEvent;
import java.io.File;

public abstract class SqlBookComp {
    private final HPanel panel;
    private HDialog dialog;
    private final TabFileComp tfComp;
    private final LangEnum curLanguage;
    private static final int maxM = 10;
    private static final int maxFileSize = maxM * 1024 * 1024;
    private static final String domainName = SqlBookComp.class.getName();
    private final static String LK_SQL_BOOK = "SQL_BOOK";
    private final static String LK_OPEN_TASK = "OPEN_TASK";
    private final static String LK_SIZE_TIP = "SIZE_TIP";

    static {
        LangMgr.merge(domainName, com.hh.frame.lang.LangUtil.loadLangRes(SqlBookComp.class));
    }

    public SqlBookComp(String dPath, HDialog dialog) {
        this.dialog = dialog;
        tfComp = new TabFileComp(HHFileUtil.newLocalFile(new File(dPath)), new DirPopMenu(), new SFilePopMenu()){
            @Override
            protected void openFile(String fileName) {
                try {
                    File f = new File(getCurrentDir()+File.separator+fileName);
                    if (f != null) {
                        if (FileUtils.sizeOf(f) > maxFileSize) throw new Exception(String.format(LangMgr.getDefaultValue(domainName, LK_SIZE_TIP),maxM));
                        String text = FileUtils.readFileToString(f, "utf-8");
                        if (f.getName().contains(".vm")) {
                            openVM(text);
                        } else {
                            openQuery(text);
                        }
                        dialog.dispose();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
                }
            }
        };
        tfComp.setFileOpenOnDbClick(true);
        tfComp.setChildDialogIcon(IconFileUtil.getLogo());
        panel = tfComp.getPanel();
        TabFileUtil.setParentWindow(StartUtil.parentFrame);
        curLanguage = LangMgr.getDefaultLang();
    }

    public void show() throws Exception {
        dialog.setWindowTitle(LangMgr.getDefaultValue(domainName, LK_SQL_BOOK));
        dialog.setSize(900, 720);
        dialog.setRootPanel(panel);
        tfComp.refresh();
        dialog.show();
    }

    private class SFilePopMenu extends FilePopMenu {
        HMenuItem taskItemImport = new HMenuItem(LangMgr.getDefaultValue(domainName, LK_OPEN_TASK)) {
            @Override
            protected void onAction() {
            	 try {
	                File f = getSFile();
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

        private File getSFile() throws Exception {
            if (this.tab.getComp().getSelectedRowCount() > 0) {
                return new File(tfComp.getCurrentDir(),
                		TabFileUtil.getFileName(this.tab.getSelectedRowBeans().get(0)));
            }
            return null;
        }

        @Override
        public void init(HTable tab) {
            super.init(tab);
            this.addSeparator();
            this.addItem(taskItemImport);
        }

        @Override
        public void showPopup(MouseEvent e) {
            boolean enabled;
			try {
				enabled = getSFile() != null;
			} catch (Exception e1) {
				enabled = false;
			}
	        taskItemImport.setIcon(IconFileUtil.getIcon(new IconBean(CsMgrEnum.SQL_BOOK.name(), "task", IconSizeEnum.SIZE_16)));
            taskItemImport.setEnabled(enabled);
            super.showPopup(e);
        }
    }

    public LangEnum getCurLanguage() {
        return curLanguage;
    }

    protected abstract void openTask(String filePath) throws Exception;

    protected abstract void openQuery(String text);

    protected abstract void openVM(String text);

	public String getCurrentDir() throws Exception {
		return tfComp.getCurrentDir();
	}
}
