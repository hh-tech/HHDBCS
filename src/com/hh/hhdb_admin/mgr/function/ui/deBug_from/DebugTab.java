package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.FunctionMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * 调试信息选项卡面板
 *
 * @author HeXu
 */
public class DebugTab extends HTabPanel {
    private DebugBaseForm debugBaseForm;
    private HTable parameter, localVariable, stack ,result;
    private TextAreaInput messageText;
    
    
    public DebugTab(DebugBaseForm debugBaseForm) {
        try {
            this.debugBaseForm = debugBaseForm;
            //添加参数
            if (null != debugBaseForm.funMr) setPanel("parameter", getParamPanel(), FunctionMgr.newHeaderConfig(LangMgr.lang(Word2.PARAM)));
            //添加变量
            setPanel("localVariable", getVarPanel(), FunctionMgr.newHeaderConfig(LangMgr.lang(Word4.LOCAL_VARIABLE)));
            
            if (debugBaseForm.dbType == DBTypeEnum.hhdb || debugBaseForm.dbType == DBTypeEnum.pgsql) {
                //添加消息
                messageText = new TextAreaInput("messageText");
                messageText.setLineWrap(true);
                messageText.setEnabled(false);
                LastPanel lastPanel = new LastPanel();
                lastPanel.set(messageText.getComp());
                setPanel("information", lastPanel, FunctionMgr.newHeaderConfig(LangMgr.lang(Word2.INFORMATION)));
                //添加结果
                setPanel("result", getResultPanel(), FunctionMgr.newHeaderConfig(LangMgr.lang(Word2.RESULT)));
            }else {
                //添加结果
                if (null != debugBaseForm.funMr) setPanel("result", getResultPanel(), FunctionMgr.newHeaderConfig(LangMgr.lang(Word2.RESULT)));
            }
            //添加堆栈
            setPanel("stack", getStackPanel(), FunctionMgr.newHeaderConfig(LangMgr.lang(Word2.STACK)));
            
        } catch (Exception e) {
            Lg.error(DebugTab.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }
    
    /**
     * 设置参数
     */
    public void setTabParas(List<Map<String, String>> pars) {
        parameter.load(pars, 1);
    }
    
    /**
     * 设置参数表格是否可编辑
     * @param bool
     */
    public void setParamEdit(boolean bool){
        if (parameter.getComp().isEditing()) parameter.getComp().getCellEditor().stopCellEditing();
        parameter.setCellEditable(bool);
        parameter.reload();
    }
    
    public HTable getParameter() {
        return parameter;
    }
    
    /**
     * 设置查询变量显示
     */
    public void setTabVariables() {
        try {
            if (null == debugBaseForm.debug) return;
            
            List<Map<String, String>> list = new ArrayList<>();
            JTable parmtable = localVariable.getComp();
            if (parmtable.isEditing()) parmtable.getCellEditor().stopCellEditing();
            
            for (int i = 0; i < parmtable.getRowCount(); i++) {
                String name = parmtable.getValueAt(i, 0).toString();
                if (StringUtils.isNotBlank(name)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", name);
                    String val = debugBaseForm.debug.getVarValue(name);
                    val = StringUtils.isNotBlank(val) ? val : "NULL";
                    map.put("value", val.contains("print_var:") ? "NULL" : val);
                    list.add(map);
                }
            }
            localVariable.load(list, 1);
        } catch (Exception e) {
            Lg.error(DebugTab.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }
    
    /**
     * 设置输出消息
     */
    public void setTabMsg(String text) {
        messageText.setValue(text);
    }
    
    /**
     * 设置调试结果显示
     */
    public void setTabResult(Map<String,String> parsMap) {
        if (null == debugBaseForm.funMr) return;
        try {
            List<Map<String, String>> list = new ArrayList<>();
            
            if (debugBaseForm.dbType == DBTypeEnum.hhdb || debugBaseForm.dbType == DBTypeEnum.pgsql) {
                if (result.getColumns().size() <1) parsMap.keySet().forEach(a -> result.addCols(new DataCol(a, a)));
    
                Map<String, String> dparma = new HashMap<String, String>();
                parsMap.keySet().forEach(a -> dparma.put(a, parsMap.get(a)+""));
                list.add(dparma);
            } else {
                if (result.getColumns().size() <1) {
                    result.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)));
                    result.addCols(new DataCol("value", LangMgr.lang(Word1.VALUE)));
                }
                
                for (String name : parsMap.keySet()) {
                    if (StringUtils.isNotBlank(name)) {
                        Map<String, String> map = new HashMap<>();
                        map.put("name", name);
                        String val = debugBaseForm.debug.getVarValue(name);
                        val = StringUtils.isNotBlank(val) ? val : "NULL";
                        map.put("value", val.contains("print_var:") ? "NULL" : val);
                        list.add(map);
                    }
                }
            }
            result.load(list, 1);
        } catch (Exception e) {
            Lg.error(DebugTab.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }
    
    /**
     * 设置堆栈
     */
    public void setTabStack(List<Map<String, String>> pars) {
        stack.load(pars, 1);
    }
    
    /**
     * 获取变量面板
     * @return
     */
    private LastPanel getVarPanel() {
        localVariable = new HTable();
        localVariable.setRowHeight(25);
        localVariable.hideSeqCol();
        localVariable.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)));
        localVariable.addCols(new DataCol("value", LangMgr.lang(Word1.VALUE)));
        //用户输入结束时查询一次
        localVariable.getComp().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
                    if (!localVariable.getComp().isEditing()) setTabVariables();
                }
            }
        });
        
        HBarLayout l = new HBarLayout();
        l.setAlign(AlignEnum.LEFT);
        HBarPanel toolBarPane = new HBarPanel(l);
        //添加变量
        HButton parameterBut = new HButton(LangMgr.lang(Word2.ADD)) {
            @Override
            public void onClick() {
                if (localVariable.getComp().isEditing()) localVariable.getComp().getCellEditor().stopCellEditing();
                Map<String, String> line = new HashMap<>();
                line.put("name", "");
                line.put("value", "");
                localVariable.add(line);
            }
        };
        parameterBut.setIcon(FaSolidEnum.PLUS,16);
        //删除变量
        HButton deleteBut = new HButton(LangMgr.lang(Word2.DELETE)) {
            @Override
            public void onClick() {
                if (localVariable.getComp().isEditing()) localVariable.getComp().getCellEditor().stopCellEditing();
                localVariable.deleteSelectRow();
            }
        };
        deleteBut.setIcon(FaSolidEnum.MINUS,16);
        toolBarPane.add(parameterBut, deleteBut);
        
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(toolBarPane.getComp());
        lastPanel.setWithScroll(localVariable.getComp());
        localVariable.load(new ArrayList<>(), 1);
        return lastPanel;
    }
    
    /**
     * 获取栈堆面板
     */
    private LastPanel getStackPanel() {
        stack = new HTable();
        stack.setRowHeight(25);
        stack.hideSeqCol();
        stack.addCols(new DataCol("row", LangMgr.lang(Word1.LINES)));
        stack.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)));
        stack.addCols(new DataCol("value", LangMgr.lang(Word1.VALUE)));
    
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(stack.getComp());
        return lastPanel;
    }
    
    /**
     * 获取参数面板
     */
    private LastPanel getParamPanel() {
        parameter = new HTable();
        //根据用户输入参数值更新sql
        parameter.getComp().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
                        if (!parameter.getComp().isEditing()) {
                            Map<String, List<String>> valMap = new LinkedHashMap<>();
                            for(int i=0;i<parameter.getComp().getRowCount();i++){
                                List<String> list = new LinkedList<>();
                                list.add(parameter.getComp().getValueAt(i, 1)+"");
                                list.add(parameter.getComp().getValueAt(i, 2)+"");
                                valMap.put(parameter.getComp().getValueAt(i, 0)+"",list);
                            }
                            debugBaseForm.qed.setText(debugBaseForm.getSql(valMap));
                        }
                    }
                }catch (Exception e){
                    Lg.error(DebugTab.class, e);
                    PopDlgUtil.error(CsVarUtil.parentFrame, e);
                }
            }
        });
        parameter.setRowHeight(25);
        parameter.hideSeqCol();
        DataCol nameDcl = new DataCol("name", LangMgr.lang(Word2.NAME));
        nameDcl.setCellEditable(false);
        DataCol typeDcl = new DataCol("type", LangMgr.lang(Word2.TYPE));
        typeDcl.setCellEditable(false);
        parameter.addCols(nameDcl,typeDcl,new DataCol("value", LangMgr.lang(Word1.VALUE)));
        
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(parameter.getComp());
        return lastPanel;
    }
    
    /**
     * 获取结果面板
     */
    private LastPanel getResultPanel() {
        result = new HTable();
        result.setRowHeight(25);
        result.hideSeqCol();
        
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(result.getComp());
        return lastPanel;
    }
}
