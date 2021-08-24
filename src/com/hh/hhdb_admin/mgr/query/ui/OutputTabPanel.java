package com.hh.hhdb_admin.mgr.query.ui;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.parser.PosBean;
import com.hh.frame.sqlwin.rs.*;
import com.hh.frame.swingui.view.container.HTabPane;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.hhdb_admin.mgr.query.QueryMgr;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 输出结果选项卡面板组件
 * @author hexu
 */
public class OutputTabPanel {
	private JdbcBean jdbc;
	private HTabPane hTabPane;

	//错误信息显示面板
	private TextAreaInput errorText;
	//保存查询结果集信息
	private Map<String, QueryTool> resultMap;
	private Map<String, List<Integer>> resultMaps;


	public OutputTabPanel(JdbcBean jdbc) {
		this.jdbc = jdbc;
		resultMap = new LinkedHashMap<String, QueryTool>();
		resultMaps = new LinkedHashMap<String, List<Integer>>();
		hTabPane = new HTabPane();
		errorText = new TextAreaInput("errorText");
		errorText.setLineWrap(true);
		errorText.setEnabled(false);
		hTabPane.addPanel("errorPane", QueryMgr.getLang("info"),errorText.getComp(),false);

		// table页切换事件
		((JTabbedPane)hTabPane.getComp()).addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				highlighted(e,resultMaps);
			}
		});
	}

	public HTabPane getHTabPane(){		
		return hTabPane;
	}
	
	/**
	 * 处理显示结果
	 * @param rsMap
	 * @param rowsum
	 * @param nullSign
	 * @throws Exception
	 */
	public void showRs(Map<PosBean, WinRsBean> rsMap,int rowsum,String nullSign) throws Exception {
		int number = 1;
		StringBuffer sbf = new StringBuffer();
		for(PosBean key:rsMap.keySet()) {
			WinRsBean rsBean=rsMap.get(key);
			if(rsBean instanceof UpdateRsBean) {
				UpdateRsBean upb = (UpdateRsBean)rsBean;
				sbf.append(QueryMgr.getLang("line") + key.getBeginLine() + ":"+QueryMgr.getLang("returned")+"(" + upb.getUpdateNum() + ")\n");
			}
			if(rsBean.getErr()!=null) {
				String message=rsBean.getErr().getMessage();
				message = message.contains("canceling statement due to user request") ? QueryMgr.getLang("interrupt") : message;
				message = message.indexOf("org.") != -1 ? message.substring(0, message.indexOf("org.")) : message;
				sbf.append(QueryMgr.getLang("line") + key.getBeginLine() + ":" + message + "\n");
			}
			if(rsBean instanceof OutputRsBean) {
//			if(rsBean.getOutput()!=null&&rsBean.getOutput().size()>0) {
				OutputRsBean orb = (OutputRsBean)rsBean;
				String message="";
				for(String str:orb.getOutput()) {
					message+="\n"+str;
				}
				message = message.contains("canceling statement due to user request") ? QueryMgr.getLang("interrupt") : message;
				message = message.indexOf("org.") != -1 ? message.substring(0, message.indexOf("org.")) : message;
				sbf.append(QueryMgr.getLang("line") + key.getBeginLine() + ""+QueryMgr.getLang("output")+":" + message + "\n");
			}
			if(rsBean instanceof QueryRsBean) {
				QueryRsBean qb = (QueryRsBean) rsBean;
				DataTab dataTab = new DataTab(jdbc,qb.getqTool(),rowsum,nullSign,rsBean.getRunMills());
				hTabPane.addPanel(number+"", QueryMgr.getLang("result")+number,dataTab.getComp(),false);

				//保存结果集
				List<Integer> list  = new ArrayList<>();
				for (int i = key.getBeginLine()-1; i < key.getEndLine(); i++) {
					list.add(i);
				}
				resultMap.put(QueryMgr.getLang("result")+number,qb.getqTool());
				resultMaps.put(QueryMgr.getLang("result")+number,list);
				number++;
			}
			if(rsBean instanceof MultiRsBean) {
				MultiRsBean mb = (MultiRsBean) rsBean;
				if(mb.getOutput()!=null&&mb.getOutput().size()>0) {
					String message="";
					for(String str:mb.getOutput()) {
						message+="\n"+str;
					}
					message = message.contains("canceling statement due to user request") ? QueryMgr.getLang("interrupt") : message;
					message = message.indexOf("org.") != -1 ? message.substring(0, message.indexOf("org.")) : message;
					sbf.append(QueryMgr.getLang("line") + key.getBeginLine() + ""+QueryMgr.getLang("output")+":" + message + "\n");
				}
				MultiRsDataTab mrTab = new MultiRsDataTab(mb,rsBean.getRunMills());
				hTabPane.addPanel(number+"", QueryMgr.getLang("result")+number,mrTab.getComp(),false);

				//保存结果集
				List<Integer> list  = new ArrayList<>();
				for (int i = key.getBeginLine()-1; i < key.getEndLine(); i++) {
					list.add(i);
				}
//				resultMap.put(QueryMgr.getLang("result")+number,qb.getqTool());
				resultMaps.put(QueryMgr.getLang("result")+number,list);
				number++;
			}
			
		}
		setMessage(sbf.toString());
		hTabPane.selectPanel(resultMaps.size() + "");
	}

	public void setMessage(String message){
		errorText.setValue(message);
	}

	/**
	 * 高亮显示对应行sql
	 */
	protected void highlighted(ChangeEvent e,Map<String, List<Integer>> resultMap){
	}
}
