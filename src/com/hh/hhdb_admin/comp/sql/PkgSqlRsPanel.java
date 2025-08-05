package com.hh.hhdb_admin.comp.sql;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.SqlWin;
import com.hh.frame.sqlwin.rs.*;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.CsL;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.*;

abstract class PkgSqlRsPanel extends HTabPanel {
	public static final String ERRORPANE = "errorPane";
	private SqlWin sqlWin;
	private JdbcBean jdbc;
	//错误信息显示面板
	private TextAreaInput errorText;
	//保存查询结果集信息
	private Map<String, List<Integer>> resultMap = new LinkedHashMap<String, List<Integer>>();

	
	public PkgSqlRsPanel(JdbcBean jdbc) {
		this.jdbc = jdbc;
		errorText = new TextAreaInput();
		errorText.setLineWrap(true);
		errorText.setEnabled(false);
		LastPanel lastp = new LastPanel();
		lastp.set(errorText.getComp());
		setPanel(ERRORPANE,lastp, newHeaderConfig(LangMgr.lang(Word2.INFORMATION)));
		
		// table页切换事件
		((JTabbedPane)getComp()).addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane jtab = (JTabbedPane) e.getSource();
				highlighted(jtab.getTitleAt(jtab.getSelectedIndex()),resultMap);
			}
		});
	}

	abstract void doExport(String schemaName, String sql) ;
	
	public void setRs(Map<PosBean, WinRsBean> posMap)throws Exception {
		int number = 1;
		for (int i = 1; i <= resultMap.size(); i++) {
			close(i+"");
		}
		resultMap.clear();
		
		StringBuffer sbf = new StringBuffer();
		for(PosBean key : posMap.keySet()) {
			WinRsBean rsBean = posMap.get(key);
			if(rsBean instanceof UpdateRsBean) {
				sbf.append(LangMgr.lang(Word1.LINE) + key.getBeginLine() + ":"+LangMgr.lang(Word3.RETURNED_VALUE)+"(" + ((UpdateRsBean)rsBean).getUpdateNum() + ")\n");
			}
			if(rsBean.getErr()!=null || rsBean instanceof OutputRsBean) {
				String message="";
				if (rsBean.getErr() != null){
					message = rsBean.getErr().getMessage();
				} else {
					for(String str : ((OutputRsBean)rsBean).getOutput()) {
						message += "\n"+str;
					}
				}
				message = message.contains("canceling statement due to user request") ? LangMgr.sLang(CsL.INTERRUPT_SQL_EXECUTION) : message;
				message = message.indexOf("org.") != -1 ? message.substring(0, message.indexOf("org.")) : message;
				sbf.append(LangMgr.lang(Word1.LINE) + key.getBeginLine() + ":" + message + "\n");
			}
			if(rsBean instanceof QueryRsBean) {
	
				PkgSqlDataTab dataTab = new PkgSqlDataTab(sqlWin,jdbc,((QueryRsBean) rsBean).getqTool(),rsBean.getRunMills(),key) {

					@Override
					protected void clickExport(String schemaName, String sql) {
						doExport(schemaName,sql);
					}
					
				};
				setPanel(number+"",dataTab,newHeaderConfig(LangMgr.lang(Word2.RESULT)+number));
				
				//保存结果集
				List<Integer> list  = new ArrayList<>();
				for (int i = key.getBeginLine()-1; i < key.getEndLine(); i++) {
					list.add(i);
				}
				resultMap.put(LangMgr.lang(Word2.RESULT)+number,list);
				number++;
			}
			if(rsBean instanceof MultiRsBean) {
				MultiRsBean mb = (MultiRsBean) rsBean;
				if(mb.getOutput()!=null&&mb.getOutput().size()>0) {
					String message="";
					for(String str:mb.getOutput()) {
						message+="\n"+str;
					}
					message = message.contains("canceling statement due to user request") ? LangMgr.sLang(CsL.INTERRUPT_SQL_EXECUTION) : message;
					message = message.indexOf("org.") != -1 ? message.substring(0, message.indexOf("org.")) : message;
					sbf.append(LangMgr.lang(Word1.LINE) + key.getBeginLine() + LangMgr.lang(Word3.EXPORT_VALUE)+":" + message + "\n");
				}
				setPanel(number+"",multiRsDataTab(mb,rsBean.getRunMills()),newHeaderConfig(LangMgr.lang(Word2.RESULT)+number));
				//保存结果集
				List<Integer> list  = new ArrayList<>();
				for (int i = key.getBeginLine()-1; i < key.getEndLine(); i++) {
					list.add(i);
				}
				resultMap.put(LangMgr.lang(Word2.RESULT)+number,list);
				number++;
			}
			
		}
		setMessage(sbf.toString());
		selectPanel(resultMap.size() + "");
	}
	
	public void setMessage(String message){
		errorText.setValue(message);
	}
 
	private LastPanel multiRsDataTab (MultiRsBean mb,long runMills){
        HTabPanel hTabPane = new HTabPanel();
        LabelInput msglabel = new LabelInput();
        int idnum = 1;
        for(String key:mb.getRsMap().keySet()) {
            List<List<String>> list = mb.getRsMap().get(key);
            HTable table = new HTable();
            List<String> head = list.get(0);
            for(String colName:head) {
                table.addCols(new DataCol(colName, colName));
            }
            SearchToolBar sToolbar = new SearchToolBar(table);
            table.setRowHeight(25);
            table.setRowStyle(true);
            LastPanel last = new LastPanel(false);
            last.setHead(sToolbar.getComp());
            last.setWithScroll(table.getComp());
            hTabPane.setPanel(idnum+"", last,newHeaderConfig(key));
            table.load(gendata(list), 1);
            idnum++;
        }
        msglabel.setValue(LangMgr.lang(Word2.TIME_CONSUMING) + "：" + runMills + "ms");
        msglabel.setAlign(AlignEnum.LEFT);
        
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(hTabPane.getComp());
        lastPanel.setFoot(msglabel.getComp());
        return lastPanel;
    }
    
    private List<Map<String, String>> gendata(List<List<String>> list){
        List<Map<String, String>> datas= new ArrayList<Map<String,String>>();
        List<String> head = list.get(0);
        for(int i=1;i<list.size();i++) {
            List<String> datalist = list.get(i);
            Map<String,String> data = new HashMap<String, String>();
            for(int j=0;j<head.size();j++) {
                data.put(head.get(j), datalist.get(j));
            }
            datas.add(data);
        }
        return datas;
    }
	
	/**
	 * 高亮显示对应行sql
	 */
	protected void highlighted(String title,Map<String, List<Integer>> resultMap){
	}

	public void setJdbc(JdbcBean jdbc) {
		this.jdbc = jdbc;
	}
	
	public void setSqlWin(SqlWin sqlWin) {
		this.sqlWin = sqlWin;
	}
	
	private  HeaderConfig newHeaderConfig(String name){
		HeaderConfig headerConfig = new HeaderConfig(name);
		headerConfig.setTitleEditable(false);
		headerConfig.setFixTab(true);
		headerConfig.setDetachEnabled(false);
		return headerConfig;
	}
}
