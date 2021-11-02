package com.hh.hhdb_admin.mgr.function.ui.deBug_from;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
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
public class DebugTab extends HTabPane {
    private DebugBaseForm debugBaseForm;
    
    private LastPanel lastPanel = new LastPanel(true);
    private HTable parameter, localVariable, stack ,result;
    private TextAreaInput messageText;
    
    
    public DebugTab(DebugBaseForm debugBaseForm) {
        try {
            this.debugBaseForm = debugBaseForm;
            setCloseBtn(false);
            //添加参数
            if (null != debugBaseForm.funMr) addPanel("parameter", FunctionMgr.getLang("parameter"), getParamPanel().getComp(), true);
            //添加变量
            addPanel("localVariable", FunctionMgr.getLang("localVariable"), getVarPanel().getComp(), true);
            
            if (debugBaseForm.dbType == DBTypeEnum.hhdb || debugBaseForm.dbType == DBTypeEnum.pgsql) {
                //添加消息
                messageText = new TextAreaInput("messageText");
                messageText.setLineWrap(true);
                messageText.setEnabled(false);
                LastPanel lasp = new LastPanel(false);
                lasp.set(messageText.getComp());
                addPanel("information", FunctionMgr.getLang("information"), lasp.getComp(), true);
                //添加结果
                addPanel("result", FunctionMgr.getLang("result"), getResultPanel().getComp(), true);
            }else {
                //添加结果
                if (null != debugBaseForm.funMr) addPanel("result", FunctionMgr.getLang("result"), getResultPanel().getComp(), true);
            }
            //添加堆栈
            addPanel("stack", FunctionMgr.getLang("stack"), getStackPanel().getComp(), true);
            
            lastPanel.set(getComp());
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
        }
    }
    
    public LastPanel getLastPanel(){
        return lastPanel;
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
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
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
                if (result.getColumns().size() <1) Arrays.asList("name","value").forEach(a -> result.addCols(new DataCol(a, FunctionMgr.getLang(a))));
                
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
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(),e.getMessage());
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
        localVariable.addCols(new DataCol("name", FunctionMgr.getLang("name")));
        localVariable.addCols(new DataCol("value", FunctionMgr.getLang("value")));
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
        HButton parameterBut = new HButton(FunctionMgr.getLang("addparameter")) {
            @Override
            public void onClick() {
                Map<String, String> line = new HashMap<>();
                line.put("name", "");
                line.put("value", "");
                localVariable.add(line);
            }
        };
        parameterBut.setIcon(FunctionMgr.getIcon("addparkey"));
        //删除变量
        HButton deleteBut = new HButton(FunctionMgr.getLang("deleteparameter")) {
            @Override
            public void onClick() {
                localVariable.deleteSelectRow();
            }
        };
        deleteBut.setIcon(FunctionMgr.getIcon("delparkey"));
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
        stack.addCols(new DataCol("row", FunctionMgr.getLang("row")));
        if (debugBaseForm.dbType == DBTypeEnum.hhdb || debugBaseForm.dbType == DBTypeEnum.pgsql) {
            stack.addCols(new DataCol("name", FunctionMgr.getLang("name")));
        }
        stack.addCols(new DataCol("value", FunctionMgr.getLang("value")));
    
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(stack.getComp());
        localVariable.load(new ArrayList<>(), 1);
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
                    e.printStackTrace();
                    PopPaneUtil.error(StartUtil.parentFrame .getWindow(), FunctionMgr.getLang("error")+":"+e.getMessage());
                }
            }
        });
        parameter.setRowHeight(25);
        parameter.hideSeqCol();
        DataCol nameDcl = new DataCol("name", FunctionMgr.getLang("name"));
        nameDcl.setCellEditable(false);
        DataCol typeDcl = new DataCol("type", FunctionMgr.getLang("type"));
        typeDcl.setCellEditable(false);
        parameter.addCols(nameDcl,typeDcl,new DataCol("value", FunctionMgr.getLang("value")));
        
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
        localVariable.load(new ArrayList<>(), 1);
        return lastPanel;
    }
}
