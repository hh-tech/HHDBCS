package com.hh.hhdb_admin.mgr.vm_editor;

import com.hh.frame.code_pop.RedisVmPopTool;
import com.hh.frame.code_pop.SqlVmPopTool;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.TemplateUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.base.HTAreaUtil;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.textarea2.code_pop.HCodeArea;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlConfDlg;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.bean.QueryBean;
import com.hh.hhdb_admin.common.bean.SqlBookBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 模板编辑器
 *
 * @author hexu
 */
public class VmComp extends AbsHComp {
    private LastPanel lastPanel = new LastPanel(false);
    private HSplitPanel splitPane = new HSplitPanel(false);
    private HCodeArea textArea;
    private HButton executeBut, saveSqlBook,loading;
    private SelectBox executeTypeBox;
    private DBTypeEnum dbtype;
    
    private boolean bool = true;
    private Map<String, Object> varMap =new HashMap<>();

    public VmComp(DBTypeEnum dbtype,String text) throws Exception {
        varMap.put("StrUtil",new StringUtils());
        SqlAreaConf sqlconf = new SqlAreaConf();
        this.dbtype=dbtype;
        if (dbtype == DBTypeEnum.redis) {
            sqlconf.setDocType(TAreaDocType.SYNTAX_STYLE_PYTHON);
            textArea = new HCodeArea(sqlconf,new RedisVmPopTool().getCodes());
        } else {
            sqlconf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
            textArea = new HCodeArea(sqlconf,new SqlVmPopTool().getCodes());
        }
        textArea.setCfgDlg(new SqlConfDlg(CsVarUtil.parentFrame.getWindow()));
        if (StringUtils.isNotBlank(text)) {
            textArea.setText(text);
        } else {
            textArea.setText(LangMgr.sLang(CsL.VM_USE_HELP));
        }
        lastPanel.setHead(getBarPanel().getComp());
        LastPanel lPanel=new LastPanel(false);
        lPanel.set(textArea.getComp());
        splitPane.setLastComp4One(lPanel);
        splitPane.setSplitWeight(0.5);
        lastPanel.set(splitPane.getComp());
        comp = lastPanel.getComp();
        saveSqlBook.setEnabled(!StringUtils.isEmpty(textArea.getText()));
        textArea.getRSyntaxTextArea().addCaretListener(e -> saveSqlBook.setEnabled(!StringUtils.isEmpty(textArea.getText())));
        textArea.setFRDlg(GlobalWorkUtil.getFrDlg());
        textArea.setGotoDlg(GlobalWorkUtil.getAreaGotoDlg());
    
        //添加快捷键
        textArea.getRSyntaxTextArea().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == (KeyEvent.VK_F8)) {
                    execute();
                }
            }
        });
    }

    public LastPanel getLastPanel() throws Exception {
        return lastPanel;
    }

    private HBarPanel getBarPanel() throws Exception {
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBar = new HBarPanel(l);
        //执行按钮
        executeBut = new HButton(LangMgr.lang(Word2.EXECUTE)) {
            @Override
            public void onClick() {
                execute();
            }
        };
        executeBut.setIcon(FaSolidEnum.PLAY,16);
        executeBut.setToolTipText("F8");

        executeTypeBox = new SelectBox("executeTypeBox");
        executeTypeBox.addOption(LangMgr.sLang(CsL.EXECUTE_TO_WINDOW), LangMgr.sLang(CsL.EXECUTE_TO_WINDOW));
        if (dbtype != DBTypeEnum.redis) executeTypeBox.addOption(LangMgr.sLang(CsL.EXECUTE_TO_QUERY), LangMgr.sLang(CsL.EXECUTE_TO_QUERY));
        executeTypeBox.addOption(LangMgr.sLang(CsL.EXECUTE_TO_FILE), LangMgr.sLang(CsL.EXECUTE_TO_FILE));
        executeTypeBox.setValue(LangMgr.sLang(CsL.EXECUTE_TO_WINDOW));

        //保存到sql宝典
        saveSqlBook = new HButton(LangMgr.lang(Word2.SAVE)) {
            @Override
            public void onClick() {
                toFile("vm");
            }
        };
        saveSqlBook.setIcon(FaSolidEnum.FLOPPY_DISK,16);
        //加载
        loading = new HButton(LangMgr.lang(Word2.LOAD)) {
            @Override
            public void onClick() {
                try {
                    HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
                    SqlBookBean sqlBookBean = (SqlBookBean) CsVarUtil.eng2.doCall(CsMgrEnum.SQL_BOOK,new BaseOpBean(BaseOpEnum.GET_PATH));
                    hfc.setPath(new File(sqlBookBean.getPath()));
                    hfc.addFilter("VM文件(*.vm)", "vm");
                    hfc.setDialogTitle(LangMgr.lang(Word2.LOAD));
                    hfc.setApproveButtonText(LangMgr.lang(Word2.LOAD));
                    if ( hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION){
                        File file = hfc.getSelectedFile();
                        textArea.setText(FileUtils.readFileToString(file, "utf-8"));
                        textArea.getRSyntaxTextArea().setCaretPosition(0);
                    }
                } catch (Exception e) {
                    Lg.error(VmComp.class, e);
                    setRightCom(e.getMessage());
                }
            }
        };
        loading.setIcon(FaSolidEnum.FOLDER_OPEN,16);
        toolBar.add(executeBut, executeTypeBox, saveSqlBook,loading);
        return toolBar;
    }

    private void toFile(String type) {
        try {
            String sql = StringUtils.isNotEmpty(textArea.getSelectedText()) ? textArea.getSelectedText() : textArea.getText();
            if (!StringUtils.isNotBlank(sql)) return;
            HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
            SqlBookBean sqlBookBean = (SqlBookBean) CsVarUtil.eng2.doCall(CsMgrEnum.SQL_BOOK,new BaseOpBean(BaseOpEnum.GET_PATH));
            hfc.setPath(new File(sqlBookBean.getPath()));
            hfc.addFilter(type.toUpperCase() + "文件(*." + type + ")", type);
            hfc.setDialogTitle(LangMgr.lang(Word2.SAVE));
            hfc.setApproveButtonText(LangMgr.lang(Word2.SAVE));
            if ( hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION){
                new Thread(new SwingWorker<String, String>() {
                    File file = null;

                    @Override
                    protected String doInBackground() throws Exception {
                        setEnab(false);
                        String url = hfc.getSelectedFile().getCanonicalPath();
                        file = new File(url.endsWith("." + type) ? url : url + "." + type);
                        if (type.equals("vm")) {
                            FileUtils.writeStringToFile(file, textArea.getText(), "utf-8");
                        } else {
                            TemplateUtil.varMap2File(varMap, sql, file);
                        }
                        PopDlgUtil.info(CsVarUtil.parentFrame,LangMgr.lang(Word2.SUCCESSFULLY));
                        return "";
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                        } catch (Exception e) {
                            e.printStackTrace();
                            setRightCom(LangMgr.lang(Word2.ERROR) + ":" + e);
                            FileUtils.deleteQuietly(file);
                        } finally {
                            setEnab(true);
                        }
                    }
                }).start();
            }
        } catch (Exception e) {
            Lg.error(VmComp.class, e);
            setRightCom(e.getMessage());
        }
    }

    private void execute() {
        String sql = StringUtils.isNotEmpty(textArea.getSelectedText()) ? textArea.getSelectedText() : textArea.getText();
        if (!StringUtils.isNotBlank(sql) || sql.equals(LangMgr.sLang(CsL.VM_USE_HELP))) return;
        
        if (executeTypeBox.getValue().equals(LangMgr.sLang(CsL.EXECUTE_TO_FILE))) {
            toFile("sql");
        } else {
            new Thread(new SwingWorker<String, String>() {
                @Override
                protected String doInBackground() throws Exception {
                    setEnab(false);
                    if (executeTypeBox.getValue().equals(LangMgr.sLang(CsL.EXECUTE_TO_WINDOW))) {
                        setRightCom(TemplateUtil.strVm2str(varMap, sql));
                    } else if (executeTypeBox.getValue().equals(LangMgr.sLang(CsL.EXECUTE_TO_QUERY))) {
                        String s = TemplateUtil.strVm2str(varMap, sql);
                        if (StringUtils.isNotBlank(s)) {
                            CsVarUtil.eng2.doCall(CsMgrEnum.QUERY, new QueryBean(s));
                        }
                    }
                    return "";
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (Exception e) {
                        Lg.error(VmComp.class, e);
                        StringBuffer sb = new StringBuffer();
                        sb.append(LangMgr.lang(Word2.ERROR) + ":");
                        sb.append(e.getMessage().indexOf("Java heap space") != -1 ? LangMgr.sLang(CsL.TOO_MUCH_CONTENT) : e.toString());
                        setRightCom(sb.toString());
                    } finally {
                        setEnab(true);
                    }
                }
            }).start();
        }
    }

    private void setEnab(boolean bool) {
        executeBut.setEnabled(bool);
        executeTypeBox.setEnabled(bool);
        saveSqlBook.setEnabled(bool);
        loading.setEnabled(bool);
    }

    private void setRightCom(String text) {
        //当为选择行运行时，报错时计算相对行位置
        if (text.startsWith("错误:java.util.concurrent.ExecutionException: com.hh.frame.common.velocity_src.exception.ParseErrorException:")) {
            String selectText= textArea.getSelectedText();
            if(!StringUtils.isBlank(selectText)) {
                try {
                    RSyntaxTextArea ra=textArea.getRSyntaxTextArea();
                    int beginLine = HTAreaUtil.getLineNum(ra, ra.getSelectionStart());
                    int line = Integer.parseInt(text.split("\\[line")[1].split(",")[0].trim());
                    beginLine = beginLine + line -1;
    
                    // Encountered "#end" at <unknown template>[line 2, column 9]
                    StringBuffer sb = new StringBuffer();
                    sb.append(text.split("\\[line")[0]+"[line "+beginLine+",");
                    sb.append(text.split("\\[line")[1].split(",")[1]);
                    text = sb.toString();
                }catch (Exception e){
                    //计算错误不做处理
                }
            }
        }
        
        TextAreaInput textAr = new TextAreaInput();
        textAr.setEnabled(false);
        textAr.setValue(text);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(textAr.getComp());
        HTabPanel hTabPane = new HTabPanel();
        hTabPane.setPanel("1", lastPanel, new HeaderConfig(LangMgr.lang(Word2.RESULT)).setFixTab(true));

        JSplitPane jsp = splitPane.getComp();
        jsp.setRightComponent(hTabPane.getComp());
        if (bool || jsp.getHeight() - jsp.getDividerLocation() <= 15) {
            splitPane.setSplitWeight(0.5);
            bool = false;
        } else {
            jsp.setDividerLocation(jsp.getDividerLocation());
        }
    }
    
    public HCodeArea getHarea() {
        return textArea;
    }
}
