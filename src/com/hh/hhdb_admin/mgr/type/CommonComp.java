package com.hh.hhdb_admin.mgr.type;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import com.hh.hhdb_admin.mgr.sql_book.SqlBookMgr;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * @author YuSai
 */
public class CommonComp {

    private static final String DOMAIN_NAME = CommonComp.class.getName();

    static {
        try {
            LangMgr2.loadMerge(CommonComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    protected String getLang(String key) {
        LangMgr2.setDefaultLang(StartUtil.default_language);
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

    protected ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.TYPE.name(), name, IconSizeEnum.SIZE_16));
    }

    protected void saveToSqlBook(String text, String type) {
        try {
            String filterName = "tps".equals(type) ? getLang("type") : getLang("type_body");
            JsonObject o = StartUtil.eng.doCall(CsMgrEnum.SQL_BOOK, GuiJsonUtil.genGetShareIdMsg(SqlBookMgr.ObjType.SHARE_PATH));
            JFileChooser chooser = new JFileChooser();
            if (null != o) chooser.setCurrentDirectory(new File(GuiJsonUtil.toStrSharedId(o)));
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            FileFilter fileFilter = new FileNameExtensionFilter(filterName + "(*." + type + ")", type);
            chooser.setFileFilter(fileFilter);
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String url = chooser.getSelectedFile().getCanonicalPath();
                url = url.endsWith("." + type) ? url : url + "." + type;
                File file = new File(url);
                FileUtils.writeStringToFile(file, text, "utf-8");
                PopPaneUtil.info(StartUtil.parentFrame.getWindow(), QueryMgr.getLang("success"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e);
        }
    }

}
