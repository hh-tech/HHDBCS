package com.hh.hhdb_admin.mgr.function2.debug.panel;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;

abstract class PkgAbsTabPanel  extends HTabPanel{
	private static String HEAP = "heap";
	private static String VAR = "var";
	private static String PARAM = "param";
	private static String RESULT = "result";
	private static String INFO = "information";
	
	private PkgParamPanel parampanel;
	private PkgVarPanel varPanel;
	private PkgResultPanel resultPanel;
	private PkgStackPanel stackPanel;
	
	private TextAreaInput messageText;
	
	public PkgAbsTabPanel(boolean withParamsSetting) {
		if(withParamsSetting) {
			setPanel(PARAM, getParamPanel(), getConfig(LangMgr.lang(Word2.PARAMETER)));
		}
		
		stackPanel = new PkgStackPanel();
		setPanel(HEAP, stackPanel, getConfig(LangMgr.lang(Word2.STACK)));
		varPanel = new PkgVarPanel() {
			@Override
			protected void setTabVariables() {
				try {
					variableUpdateInform();
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(getComp(), e);
				}
			}
		};
		setPanel(VAR, varPanel, getConfig(LangMgr.lang(Word4.LOCAL_VARIABLE)));
		resultPanel = new PkgResultPanel();
		setPanel(RESULT, resultPanel, getConfig(LangMgr.lang(Word2.RESULT)));
		
		//添加消息
		messageText = new TextAreaInput("messageText");
        messageText.setLineWrap(true);
        messageText.setEnabled(false);
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(messageText.getComp());
        setPanel(INFO, lastPanel, PkgUtil.newHeaderConfig(LangMgr.lang(Word2.INFORMATION)));
	}
	
	
	public void setInfo(String text) {
		messageText.setValue(text);
	}
	
	public HTable getParamTable() {
		return parampanel.getTable();
	}




	public HTable getVarTable() {
		return varPanel.getVarTable();
	}




	public HTable getResultTable() {
		return resultPanel.getVarTable();
	}




	public HTable getStackTable() {
		return stackPanel.getTable();
	}

	
	
	public void setParamsEditable(boolean cellEditable) {
		if(parampanel!=null) {
			parampanel.setValueEditable(cellEditable);
		}
		
	}

	public abstract void paramsUpdateInform() throws Exception ;
	public abstract void variableUpdateInform() throws Exception ;

	/**
     * 获取参数面板
     */
    private LastPanel getParamPanel() {
    	parampanel = new PkgParamPanel() {
        	@Override
        	public void inform() {
        		try {
        			
        			paramsUpdateInform();
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(getComp(), e);
				}
        	}
        };
        
        return parampanel;
        
    }
    
	private HeaderConfig getConfig(String name) {
		HeaderConfig config = new HeaderConfig(name);
		config.setDetachEnabled(false);
		config.setFixTab(true);
		config.setTitleEditable(false);
		return config;
	}
}
