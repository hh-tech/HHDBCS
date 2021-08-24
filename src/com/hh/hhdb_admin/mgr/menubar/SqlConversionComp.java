package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.ora2pg1.Ora2PgTool;
import com.hh.frame.parser.AbsStmt;
import com.hh.frame.parser.ParserUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.ArrayList;

/**
 * SQL转换页面
 *
 * @author HeXu
 */
public class SqlConversionComp {
    private HTextArea uArea = new HTextArea(false, true);
    private HTextArea bArea = new HTextArea(false, true);
    private LastPanel lastPanel = new LastPanel(false);
    
    public SqlConversionComp() {
        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setLastComp4One(getLastPanel("Oracle:", uArea));
        splitPane.setLastComp4Two(getLastPanel("PG:", bArea));
        splitPane.setSplitWeight(0.5);
        
        lastPanel.setHead(getHBarPanel().getComp());
        lastPanel.set(splitPane.getComp());
    }
    
    public LastPanel getPanel() {
        return lastPanel;
    }
    
    private LastPanel getLastPanel(String name, HTextArea textArea) {
        textArea.showBookMask(false);
        textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_NONE);
        
        LastPanel hPanel = new LastPanel(false);
        LabelInput label = new LabelInput(name);
        label.setAlign(AlignEnum.LEFT);
        hPanel.setHead(label.getComp());
        hPanel.set(textArea.getComp());
        return hPanel;
    }
    
    private HBarPanel getHBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton nextBtn = new HButton(MenubarComp.getLang("sql_Conversion")) {
            @Override
            protected void onClick() {
                try {
                    String str = uArea.getArea().getTextArea().getText();
                    if (StringUtils.isNotBlank(str)) {
                        StringBuilder convertSql = new StringBuilder();
                        if (!StringUtils.endsWith(str, ";")) str = str + ";";
                        
                        Ora2PgTool tool = new Ora2PgTool(null, new ArrayList<>());
                        for (AbsStmt abs : ParserUtil.getOraStmts(str)) {
                            for (AbsStmt absStmt : tool.toPgStmt(abs)) {
                                convertSql.append(absStmt.toString()).append(";\n");
                            }
                        }
                        bArea.setText(convertSql.toString());
                        JOptionPane.showMessageDialog(null, MenubarComp.getLang("convSuccess"), MenubarComp.getLang("tip"), JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, e.getMessage(), MenubarComp.getLang("tip"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        nextBtn.setIcon(MenubarComp.getIcon("next"));
        
        HButton clearBtn = new HButton(MenubarComp.getLang("clear")) {
            @Override
            protected void onClick() {
                String str = uArea.getArea().getTextArea().getText();
                if (StringUtils.isNotBlank(str)) {
                    bArea.setText("");
                    uArea.setText("");
                }
            }
        };
        clearBtn.setIcon(MenubarComp.getIcon("delete"));
        
        barPanel.add(nextBtn,clearBtn);
        return barPanel;
    }
}
