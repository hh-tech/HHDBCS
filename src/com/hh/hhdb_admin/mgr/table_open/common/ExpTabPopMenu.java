package com.hh.hhdb_admin.mgr.table_open.common;

import com.hh.frame.dbmg.exp.data.wr.Data2InsertWriter;
import com.hh.frame.dbobj3.util.DataUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.hmenu.HMenu;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.SeqCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.frame.swingui.view.tab.exp.AbsTabExp2File;
import com.hh.frame.swingui.view.tab.exp.TabExp2Excel;
import com.hh.frame.swingui.view.tab.menu.body.ExpTabBodyPopMenu;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.comp.sql.SqlVmEditorDlg;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 表格右键导出菜单
 */
public class ExpTabPopMenu extends ExpTabBodyPopMenu {
    private HDialog dialog;
    private final TextAreaInput templateInput = new TextAreaInput();
    
    private String schema;
    private List<Enum<?>> fieldTypeList = new LinkedList<>();       //列类型集合
    private Map<String,String> colMap = new LinkedHashMap<>();      //列名称与值集合
    private Map<String,Enum<?>> colTypeMap = new LinkedHashMap<>(); //列名称与类型集合
    
    /**
     * 初始化表格右键导出insert语句菜单
     * @param fieldTypeList 当前查询语句字段类型集合
     * @param schema        当前查询语句的模式
     */
    public ExpTabPopMenu(List<Enum<?>> fieldTypeList, String schema) {
        this.fieldTypeList = fieldTypeList;
        this.schema = schema;
    }
    
    @Override
    protected HMenu getExpMenu(ActionListener actionListener) {
        HMenu expMenu = super.getExpMenu(actionListener);
        HMenuItem expSqlItem = new HMenuItem(INSERT);
        expMenu.addItem(expSqlItem);
        expSqlItem.getComp().addActionListener(actionListener);
        return expMenu;
    }
    
    @Override
    protected ActionListener initListener() {
        return new TestActionListener(tab);
    }
    
    protected class TestActionListener extends ExpActionListener {
    
        public TestActionListener(HTable table) {
            super(table);
            templateInput.setRows(4);
            templateInput.setLineWrap(true);
            templateInput.setEnabled(false);
        }
    
        @Override
        public void actionPerformed(ActionEvent e) {
            super.actionPerformed(e);
            try {
                if (type.toLowerCase().contains(INSERT.toLowerCase())) {
                    type = SQL;
                    Map<String,String> cMap = new LinkedHashMap<>();
                    colMap = new LinkedHashMap<>();
                    colTypeMap = new LinkedHashMap<>();
                    List<AbsCol> list = table.getColumns();
                    for (int i = 0; i < list.size(); i++) {
                        if (!list.get(i).getName().equals(SeqCol.name)) {
                            if (list.get(i).isShow()) cMap.put(list.get(i).getValue(),list.get(i).getValue());
                            colMap.put(list.get(i).getName(),list.get(i).getValue());
                            colTypeMap.put(list.get(i).getName(),fieldTypeList.get(i-1));
                        }
                    }
                    templateInput.setValue(DataUtil.getInsertVm(cMap, schema, "new_table"));
                    
                    HPanel rootPanel = new HPanel(new HDivLayout(15, 10, GridSplitEnum.C12));
                    rootPanel.getComp().setBorder(BorderFactory.createEmptyBorder());
                    HPanel templateLine = new HPanel(new HDivLayout(GridSplitEnum.C3, GridSplitEnum.C7));
                    templateLine.add(new LabelInput("数据模版:"), templateInput, new HButton("模版"){
                        @Override
                        protected void onClick() {
//                            new TempDlog(templateInput.getValue(), DataUtil.getInsertVm(cMap, schema, "new_table"), "", dialog) {
//                                public void save(String sql) {
//                                    templateInput.setValue(sql);
//                                }
//                            };
                        	try {
                        		SqlVmEditorDlg dlg=	new SqlVmEditorDlg(DataUtil.getInsertVm(cMap, schema, "new_table"),dialog) {
                        			@Override
                        			protected void save(String sql){
                        				 templateInput.setValue(sql);
                        		    } 
                        		};
                        		dlg.show();
                        	}catch(Exception e) {
                        		PopDlgUtil.error(e);
                        	}
                        }
                    });
                    rootPanel.add(templateLine);
                    
                    LastPanel lastPanel = new LastPanel(false);
                    lastPanel.set(rootPanel.getComp());
                    dialog = new HDialog(CsVarUtil.parentFrame, HWindow.SMALL_WIDTH,lastPanel.getHeight()+100, true){
                        protected void onConfirm() {
                            try {
                                if (StringUtils.isBlank(templateInput.getValue())) throw new Exception("模版不能为空");
                                dialog.dispose();
                                HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
                                hfc.addFilter("SQL文件(*.sql)", "sql");
                                hfc.setDialogTitle("导出文件");
                                hfc.setApproveButtonText("保存");
                                if ( hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION){
                                    String fileName = hfc.getSelectedFile().getName();
                                    if (!FilenameUtils.getExtension(fileName).equalsIgnoreCase(type)) {
                                        fileName= String.format("%s%s%s", fileName, FilenameUtils.EXTENSION_SEPARATOR_STR, type.toLowerCase());
                                    }
                                    expData(initTabExp2File(new File(hfc.getSelectedFile().getParent(), fileName)));
                                }
                            }catch (Exception e1){
                                PopDlgUtil.error(e1);
                            }
                        }
                    };
                    dialog.setWindowTitle("导出INSERT");
                    dialog.setRootPanel(lastPanel);
                    dialog.show();
                }
            } catch (Exception exception) {
                PopDlgUtil.error(exception);
            }
        }
        
        @Override
        protected AbsTabExp2File initTabExp2File(File selectedFile) {
            AbsTabExp2File tabExp2File = super.initTabExp2File(selectedFile);
            if ((type.equalsIgnoreCase(SQL))) tabExp2File = new TabExp2Sql(table, selectedFile);
            return tabExp2File;
        }
    }
    
    protected class TabExp2Sql extends TabExp2Excel {
        
        public TabExp2Sql(HTable hTab, File sqlFile) {
            super(hTab, sqlFile);
        }
        
        @Override
        protected void expData(List<List<String>> tabListData) throws Exception {
            start = System.currentTimeMillis();
            
            if (file.exists()) FileUtils.deleteQuietly(file);
            List<String> colLi = tabListData.get(0);
            tabListData.remove(0);
            Data2InsertWriter dataWriter = new Data2InsertWriter(templateInput.getValue(),nullSymbol);
            dataWriter.initWriterFile(file);
            for (List<String> li : tabListData) {
                for (int i = 0; i < li.size(); i++) {
                    dataWriter.appendField(colMap.get(colLi.get(i)), colTypeMap.get(colLi.get(i)), li.get(i));
                }
                dataWriter.endLine();
            }
            dataWriter.close();
            
            expLine = tabListData.size();
            totalLine = hTab.getRowCount();
            end = System.currentTimeMillis();
        }
    }
}
