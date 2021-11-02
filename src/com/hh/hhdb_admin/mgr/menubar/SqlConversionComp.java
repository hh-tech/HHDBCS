package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.dbtrans.ora2mysql.Ora2MysqlTool;
import com.hh.frame.ora2pg1.Ora2PgTool;
import com.hh.frame.parser.AbsStmt;
import com.hh.frame.parser.ParserUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * SQL转换页面
 *
 * @author HeXu
 */
public class SqlConversionComp {
    private HTextArea uArea = new HTextArea(false, true);
    private HTextArea bArea = new HTextArea(false, true);
    private SelectBox uBox = new SelectBox();
    private SelectBox bBox = new SelectBox();
    private LastPanel lastPanel = new LastPanel(false);
    
    public SqlConversionComp() {
        uArea.showBookMask(false);
        uArea.setConstants(ConstantsEnum.SYNTAX_STYLE_NONE);
        bArea.showBookMask(false);
        bArea.setConstants(ConstantsEnum.SYNTAX_STYLE_NONE);
        Arrays.asList("Oracle","PG","HHDB","mysql").forEach(a -> {
            uBox.addOption(a,a);
            bBox.addOption(a,a);
        });
        
        lastPanel.setHead(getHBarPanel().getComp());
        lastPanel.set(getSplitPanel().getComp());
    }
    
    public LastPanel getPanel() {
        return lastPanel;
    }
    
    private HSplitPanel getSplitPanel() {
        HSplitPanel splitPane = new HSplitPanel(false);
        splitPane.setSplitWeight(0.5);
    
        LastPanel hPanel = new LastPanel(false);
        HPanel hp = new HPanel(new HDivLayout(15, 20, GridSplitEnum.C2));
        hp.add(uBox);
        hPanel.setHead(hp.getComp());
        hPanel.set(uArea.getComp());
        splitPane.setLastComp4One(hPanel);
    
        LastPanel hPanel2 = new LastPanel(false);
        HPanel hp2 = new HPanel(new HDivLayout(15, 20, GridSplitEnum.C2));
        hp2.add(bBox);
        hPanel2.setHead(hp2.getComp());
        hPanel2.set(bArea.getComp());
        splitPane.setLastComp4Two(hPanel2);
        
        return splitPane;
    }
    
    private HBarPanel getHBarPanel() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton nextBtn = new HButton(MenubarComp.getLang("sql_Conversion")) {
            @Override
            protected void onClick() {
                try {
                    if (uBox.getValue().equals(bBox.getValue())) return;
                    
                    String str = uArea.getArea().getTextArea().getText();
                    if (StringUtils.isNotBlank(str)) {
                        if (!StringUtils.endsWith(str, ";")) str = str + ";";
                        StringBuilder convertSql = new StringBuilder();
                        
                        if (uBox.getValue().equals("Oracle") && bBox.getValue().equals("PG")) {
                            Ora2PgTool tool = new Ora2PgTool(null, new ArrayList<>());
                            for (AbsStmt abs : ParserUtil.getOraStmts(str)) {
                                for (AbsStmt absStmt : tool.toPgStmt(abs)) {
                                    convertSql.append(absStmt.toString()).append(";\n");
                                }
                            }
                        } else if (uBox.getValue().equals("Oracle") && bBox.getValue().equals("mysql")) {
                            Ora2MysqlTool tool = new Ora2MysqlTool(new ArrayList<>());
                            for (AbsStmt abs : ParserUtil.getOraStmts(str)) {
                                for (AbsStmt absStmt : tool.toMysqlStmt(abs)) {
                                    convertSql.append(absStmt.toString()).append(";\n");
                                }
                            }
//                        } else if (uBox.getValue().equals("mysql") && bBox.getValue().equals("HHDB")) {
//                            Mysql2hhdbTool tool = new Mysql2hhdbTool(new ArrayList<>());
//                            for (AbsStmt abs : ParserUtil.getMysqlStmts(str)) {
//                                String sql=ParserUtil.getSql(str, abs.getPos());
//                                for (AbsStmt absStmt : tool.toHHdbStmt(abs)) {
//                                    convertSql.append(absStmt.toString()).append(";\n");
//                                }
//                            }
                        } else {
                            PopPaneUtil.info(StartUtil.parentFrame.getWindow(), MenubarComp.getLang("nonsupport"));
                        }
                      
                        bArea.setText(convertSql.toString());
                        PopPaneUtil.info(StartUtil.parentFrame.getWindow(), MenubarComp.getLang("convSuccess"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
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
