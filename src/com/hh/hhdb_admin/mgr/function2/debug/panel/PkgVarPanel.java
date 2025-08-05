package com.hh.hhdb_admin.mgr.function2.debug.panel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;

public class PkgVarPanel extends LastPanel{
	public static final String NAME = "name";
	public static final String VALUE = "value";
	
	private HTable varTable;
	public PkgVarPanel() {
		varTable = new HTable();
    	varTable.setRowHeight(25);
    	varTable.hideSeqCol();
    	varTable.addCols(new DataCol(NAME, LangMgr.lang(Word2.NAME)));
    	varTable.addCols(new DataCol(VALUE, LangMgr.lang(Word1.VALUE)));
        //用户输入结束时查询一次
    	varTable.getComp().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
                    if (!varTable.getComp().isEditing()) setTabVariables();
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
                if (varTable.getComp().isEditing()) varTable.getComp().getCellEditor().stopCellEditing();
                Map<String, String> line = new HashMap<>();
                line.put(NAME, "");
                line.put(VALUE, "");
                varTable.add(line);
            }
        };
        parameterBut.setIcon(FaSolidEnum.PLUS,16);
        //删除变量
        HButton deleteBut = new HButton(LangMgr.lang(Word2.DELETE)) {
            @Override
            public void onClick() {
                if (varTable.getComp().isEditing()) varTable.getComp().getCellEditor().stopCellEditing();
                varTable.deleteSelectRow();
            }
        };
        deleteBut.setIcon(FaSolidEnum.MINUS,16);
        toolBarPane.add(parameterBut, deleteBut);
        setHead(toolBarPane.getComp());
        setWithScroll(varTable.getComp());
        varTable.load(new ArrayList<>(), 1);
	}
	protected void setTabVariables() {
		
	}
	public HTable getVarTable() {
		return varTable;
	}
	
	
	
	
	

}
