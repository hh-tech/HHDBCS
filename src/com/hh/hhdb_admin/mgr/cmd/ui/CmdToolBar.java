package com.hh.hhdb_admin.mgr.cmd.ui;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.sqlwin.util.SqlWinUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.cmd.CmdComp;
import com.hh.hhdb_admin.mgr.cmd.CmdMgr;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;

public class CmdToolBar {
    private HBarPanel hTool;
    private CmdComp cmdcomp;
    private HButton stopBut, imp, exp, setup;
    private SelectBox schemabox;
    
    public int rowsum = 30;     //每页显示条数
    public String nullSign;     //空值显示标记
    
    public CmdToolBar(CmdComp cmd)throws Exception {
        this.cmdcomp = cmd;
        //设置每页显示数量
        JsonObject fileJsonArr = Json.parse(FileUtils.readFileToString(StartUtil.defaultJsonFile, StandardCharsets.UTF_8)).asObject();
        rowsum = fileJsonArr.get("varPageSize").asInt();
        nullSign = fileJsonArr.get("null").asString();
        
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        hTool = new HBarPanel(l);
        
        //撤销
        stopBut = new HButton(CmdMgr.getLang("backout")) {
            @Override
            public void onClick() {
                cmdcomp.cancel();
            }
        };
        stopBut.setEnabled(false);
        //导入
        imp = new HButton(CmdMgr.getLang("import")) {
            @Override
            public void onClick() {
            	try {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						cmdcomp.openFile(chooser.getSelectedFile().getPath());
					}
				} catch (Exception e) {
					e.printStackTrace();
					PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
				}
            }
        };
        imp.setIcon(CmdMgr.getIcon("import"));
        //导出
        exp = new HButton(CmdMgr.getLang("export")) {
            @Override
            public void onClick() {
                try {
                    String sql = cmdcomp.getText();
                    if (!StringUtils.isNotBlank(sql)) return;
                    
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    FileFilter fileFilter = new FileNameExtensionFilter("TXT文件(*.txt)", "txt");
                    chooser.setFileFilter(fileFilter);
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        String url = chooser.getSelectedFile().getCanonicalPath();
                        url = url.endsWith(".txt") ? url : url + ".txt";
                        FileUtils.writeStringToFile(new File(url), sql, "utf-8");
                        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), CmdMgr.getLang("success"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
                }
            }
        };
        exp.setIcon(CmdMgr.getIcon("export"));
        //设置
        setup = new HButton(CmdMgr.getLang("setup")) {
            @Override
            public void onClick() {
                new SettingsPanel(rowsum,nullSign) {
                    @Override
                    public void save(int row,String nullSigns) {
                        rowsum = row;
                        nullSign = nullSigns;
                        cmd.setSqlRunTool(row,nullSigns);
                    }
                };
            }
        };
        setup.setIcon(CmdMgr.getIcon("key"));
        hTool.add(stopBut,imp,exp,setup);
        
        //模式选择框
        if (SqlWinUtil.showSchemaBox(DriverUtil.getDbType(cmd.getJdbc()))) {
            schemabox = new SelectBox("schemabox");
            List<String> schemaList = new LinkedList<>();
            Connection conn = null;
            try {
                conn = ConnUtil.getConn(cmd.getJdbc());
                schemaList = SqlWinUtil.getSchemaNameList(conn);
                schemaList.forEach(a -> schemabox.addOption(a,a));
                schemabox.setValue(cmd.getJdbc().getSchema());
                hTool.add(schemabox);
    
                schemabox.getComp().addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            cmd.updaSchema(schemabox.getValue());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
            } finally {
                ConnUtil.close(conn);
            }
        }else {
            schemabox = new SelectBox("schemabox");
        }
    }
    
    public void setCancelBtnEnab(boolean enable) {
        stopBut.setEnabled(enable);
        schemabox.setEnabled(!enable);
    }
    
    public HBarPanel gethTool() {
        return hTool;
    }
}
