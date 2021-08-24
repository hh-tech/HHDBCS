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
    
    public LastPanel lastPanel = new LastPanel(true);
    private HTable parameter, localVariable, stack ,result;
    private TextAreaInput messageText;
    
    
    public DebugTab(DebugBaseForm debugBaseForm) {
        try {
            this.debugBaseForm = debugBaseForm;
            setCloseBtn(false);
            //添加参数
            if (null != debugBaseForm.treeNode) addPanel("parameter", FunctionMgr.getLang("parameter"), getParamPanel().getComp(), true);
            //添加变量
            addPanel("localVariable", FunctionMgr.getLang("localVariable"), getVarPanel().getComp(), true);
            //添加消息
            if (debugBaseForm.dbType == DBTypeEnum.hhdb || debugBaseForm.dbType == DBTypeEnum.pgsql) {
                messageText = new TextAreaInput("messageText");
                messageText.setLineWrap(true);
                messageText.setEnabled(false);
                LastPanel lasp = new LastPanel(false);
                lasp.set(messageText.getComp());
                addPanel("information", FunctionMgr.getLang("information"), lasp.getComp(), true);
            }else {
                //添加结果
                if (null != debugBaseForm.treeNode) addPanel("result", FunctionMgr.getLang("result"), getResultPanel().getComp(), true);
            }
            //添加堆栈
            addPanel("stack", FunctionMgr.getLang("stack"), getStackPanel().getComp(), true);
            
            lastPanel.set(getComp());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * 设置参数
     */
    public void setTabParas(List<Map<String, String>> pars) {
        parameter.load(pars, 1);
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
            JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
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
    public void setTabResult(List<String> parsList) {
        if (null == debugBaseForm.treeNode) return;
        try {
            List<Map<String, String>> list = new ArrayList<>();

            for (String name : parsList) {
                if (StringUtils.isNotBlank(name)) {
                    Map<String, String> map = new HashMap<>();
                    map.put("name", name);
                    String val = debugBaseForm.debug.getVarValue(name);
                    val = StringUtils.isNotBlank(val) ? val : "NULL";
                    map.put("value", val.contains("print_var:") ? "NULL" : val);
                    list.add(map);
                }
            }
            result.load(list, 1);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), FunctionMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
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
        //添加参数
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
        //删除参数
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
        parameter.setRowHeight(25);
        parameter.hideSeqCol();
        parameter.addCols(new DataCol("name", FunctionMgr.getLang("name")));
        parameter.addCols(new DataCol("type", FunctionMgr.getLang("type")));
        parameter.addCols(new DataCol("value", FunctionMgr.getLang("value")));
        
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
        result.addCols(new DataCol("name", FunctionMgr.getLang("name")));
        result.addCols(new DataCol("value", FunctionMgr.getLang("value")));
        
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setWithScroll(result.getComp());
        localVariable.load(new ArrayList<>(), 1);
        return lastPanel;
    }
}
