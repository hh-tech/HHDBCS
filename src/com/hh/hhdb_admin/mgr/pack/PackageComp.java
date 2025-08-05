package com.hh.hhdb_admin.mgr.pack;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.dbobj3.AllDbObjType;
import com.hh.frame.dbobj3.ora.PackParserTool;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.bean.SqlBookBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;


public class PackageComp extends CommonComp {
    private final Connection conn;
    private HTabPanel tabPane;
    private String selectedItem;
    private DesignComp headComp;
    private DesignComp bodyComp;
    private JdbcBean jdbc;
    private PackageBean csGuiBean;

    public PackageComp(JdbcBean jdbc,Connection conn,PackageBean csGuiBean) {
        this.conn = conn;
        this.jdbc = jdbc;
        this.csGuiBean = csGuiBean;
    }

    public HPanel getAddPanel(String packName) throws Exception {
        tabPane = new HTabPanel() {
            @Override
            protected void onSelected(String id) {
                selectedItem = id;
            }
        };
        headComp = new DesignComp(jdbc,conn, packName, AllDbObjType.PACK, false,csGuiBean);
        bodyComp = new DesignComp(jdbc,conn, packName, AllDbObjType.PACKBODY, false,csGuiBean);
        tabPane.setPanel("body", bodyComp.getPanel(), getHeaderConfig(LangMgr.lang(Word2.PACKAGE_BODY)));
        tabPane.setPanel("head", headComp.getPanel(), getHeaderConfig(LangMgr.lang(Word2.PACKAGE_HEAD)));
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(getBarPanel().getComp());
        lastPanel.set(tabPane.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        return rootPanel;
    }

    protected HPanel getDesignPanel(String packName, AllDbObjType sessionEnum) throws Exception {
        return new DesignComp(jdbc,conn, packName, sessionEnum, true,csGuiBean).getPanel();
    }

    protected void openFile(String text, String fileName) throws Exception {
        HPanel panel;
        String[] names = fileName.split("\\.");
        String type = names[names.length - 1];
        String packName = new PackParserTool(text).getName();
        switch (type) {
            case "pck":
                panel = getAddPanel(packName);
                String[] texts = text.split("\n/\n");
                if (texts.length < 2) {
                    DesignComp designComp = new DesignComp(jdbc,conn, packName, AllDbObjType.PACK, true,csGuiBean);
                    designComp.setText(text);
                    panel = designComp.getPanel();
                } else {
                    headComp.setText(texts[0]);
                    bodyComp.setText(texts[1]);
                }
                break;
            case "spc":
                DesignComp designComp = new DesignComp(jdbc,conn, packName, AllDbObjType.PACK, true,csGuiBean);
                designComp.setText(text);
                panel = designComp.getPanel();
                break;
            case "bdy":
                designComp = new DesignComp(jdbc,conn, packName, AllDbObjType.PACKBODY, true,csGuiBean);
                designComp.setText(text);
                panel =  designComp.getPanel();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        SharePaneObj obj = new SharePaneObj();
        obj.setId(RandomUtil.getUUID());
        obj.setTitle(packName);
        obj.setCsMgrEnum(CsMgrEnum.PACKAGE);
        obj.setFixTab(false);
        obj.setObj(panel);
        CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
    }

    private HBarPanel getBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton execBtn = new HButton(LangMgr.lang(Word2.EXECUTE)) {
            @Override
            protected void onClick() {
                if (headComp.execute()) {
                    bodyComp.execute();
                    tabPane.selectPanel("body");
                 } else {
                    tabPane.selectPanel("head");
                }
            }
        };
        execBtn.setIcon(FaSolidEnum.PLAY,16);
        HButton formatBtn = new HButton(LangMgr.lang(Word3.FORMATTING)) {
            @Override
            protected void onClick() {
                if ("head".equals(selectedItem)) {
                    headComp.format();
                } else {
                    bodyComp.format();
                }
            }
        };
        formatBtn.setIcon(FaSolidEnum.WAND_MAGIC_SPARKLES,16);
        HButton saveBtn = new HButton(LangMgr.lang(Word2.SAVE)) {
            @Override
            protected void onClick() {
                saveSqlBook(headComp.getText() + "\n/\n" + bodyComp.getText() + "\n/\n", true);
            }
        };
        saveBtn.setIcon(FaSolidEnum.FLOPPY_DISK,16);
        barPanel.add(execBtn, formatBtn, saveBtn);
        return barPanel;
    }

    private HeaderConfig getHeaderConfig(String name) {
        HeaderConfig headerConfig = new HeaderConfig(name);
        headerConfig.setTitleEditable(false);
        headerConfig.setFixTab(true);
        headerConfig.setDetachEnabled(false);
        return headerConfig;
    }
    
    /**
     * 保存sql到宝典
     * @param txt	保存内容
     * @param bool	是否提示
     */
    public static void saveSqlBook(String txt,boolean bool){
        try {
            if (!StringUtils.isNotBlank(txt)) return;
            
            HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
            SqlBookBean sqlBookBean = (SqlBookBean) CsVarUtil.eng2.doCall(CsMgrEnum.SQL_BOOK,new BaseOpBean(BaseOpEnum.GET_PATH));
            hfc.setPath(new File(sqlBookBean.getPath()));
            hfc.addFilter("SQL文件(*.sql)","sql");
            hfc.setDialogTitle("保存");
            hfc.setApproveButtonText("保存");
            if ( hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION){
                String fileName = hfc.getSelectedFile().getName();
                if (!FilenameUtils.getExtension(fileName).equalsIgnoreCase("sql")) {
                    fileName= String.format("%s%s%s", fileName, FilenameUtils.EXTENSION_SEPARATOR_STR, "sql");
                }
                FileUtils.writeStringToFile(new File(hfc.getSelectedFile().getParent(), fileName), txt, "utf-8");
                if (bool) PopDlgUtil.info(CsVarUtil.parentFrame, "成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }
    }
}
