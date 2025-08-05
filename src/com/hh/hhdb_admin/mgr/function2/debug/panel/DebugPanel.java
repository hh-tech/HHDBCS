package com.hh.hhdb_admin.mgr.function2.debug.panel;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.text.DefaultHighlighter;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.DebugSqlUtil.DebugType;
import com.hh.frame.db_sql.info.SqlParamBean;
import com.hh.frame.db_sql.info.SqlParamBean.ParamType;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.DebugTab;
import com.hh.hhdb_admin.mgr.function2.debug.base.ColNameCons;
import com.hh.hhdb_admin.mgr.function2.debug.base.DebugBean;
import com.hh.hhdb_admin.mgr.function2.debug.base.DebugTool;


public abstract class DebugPanel extends LastPanel{
	private Map<String, HTextArea> editorMap = new HashMap<>();               //编辑器集合
	private Map<String, Map<Integer, int[]>> linMap= new HashMap<>();                  //行号sql对应关系集合 key：编辑面板对象名称  map：函数内容与行号位置对应关系,大小代表函数行数
	private List<String> resultNameList = new ArrayList<>(); //调试返回值和输出参数名称集合
	private Map<String,String> name2IdMap = new HashMap<>();
	public final static String RESULT_NAME = "RESULT";
	private HBarPanel barPanel;
	private HButton debugButton;
	private HButton nextLineButton;
	private HButton nextPointButton;
	private HButton enterButton;
	private HButton stopButton;
	static String SCRIPT = "Script";
	private DebugTool debug;
	private PkgAbsTabPanel tabPanel;
	protected String debugTitle = SCRIPT;
	private HTabPanel scriptTabPanel;
	private Integer currentLine;
//	private String objId;
//	private List<SqlParamBean> params;
	private List<Map<String, String>> stackData;
	private static final String STEP = "step";
	private static final String CONTINUE = "continue";
	private static final String STEPINTO = "stepInto";
	
	
	private DebugBean debugBean;
	
	public DebugPanel(DebugBean debugBean) throws Exception{
		
		this.debugBean = debugBean;
		HBarLayout layout = new HBarLayout();
		layout.setTopHeight(10);
		layout.setBottomHeight(10);
		layout.setAlign(AlignEnum.LEFT);
		barPanel = new HBarPanel(layout);
		initButton();
		barPanel.add(debugButton,enterButton,nextLineButton,nextPointButton,stopButton);
		setHead(barPanel.getComp());
		HSplitPanel splitPanel = new HSplitPanel(false);
		splitPanel.setDividerLocation(400);
		scriptTabPanel = new HTabPanel();
		openNewTabArea(null,SCRIPT,"");
		splitPanel.setPanelOne(scriptTabPanel);
		tabPanel = new PkgAbsTabPanel(debugBean.getParams()!=null) {

			@Override
			public void paramsUpdateInform() throws Exception {
				List<HTabRowBean> arr = getParamTable().getRowBeans(RowStatus.UPDATE);
				debugBean.setParams(toParamBeans(arr));
    			editorMap.get(SCRIPT).setText(getDebugSql());
			}

			@Override
			public void variableUpdateInform() throws Exception {
				setTabVariables();
			}
			
		};
		splitPanel.setPanelTwo(tabPanel);
		set(splitPanel.getComp());
		
		editorMap.get(SCRIPT).setText(debugBean.getDebugSql());
		resetStatus(false);
		this.debug = new DebugTool(CsVarUtil.jdbcCloneWithSsh(),debugBean.getDebugObjId());
		this.debug.addProperyListenner(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals(DebugTool.P_END)) {
					try {
						Lg.debug(getClass(), "target已退出....");
						if(debug.getError()!=null) {
							throw debug.getError();
						}
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								try {
									resetForm();
									setResult();
									setInfo();
									
								} catch (Exception e) {
									Lg.error(getClass(), e);
								}
							}
						});
					} catch (Exception e) {
						//调用的abort,导致退出异常的不报错
						if(e.getMessage()!=null&&!e.getMessage().contains("由于用户请求而正在取消查询")) {
							Lg.error(getClass(), e);
							PopDlgUtil.error(getComp(), e);
						}
						
					}
				}
				
			}
		});
		
		
		List<SqlParamBean> params = debugBean.getParams();
		if(params!=null) {
			for(SqlParamBean s:params) {
				if(s.getParamType() == ParamType.INOUT||s.getParamType() == ParamType.OUT) {
					resultNameList.add(s.getParamName());
				}
			}
			tabPanel.getParamTable().load(PkgUtil.toParamsTableData(params),1);
		}

		if(debug.getDebugtype() == DebugType.adp_debug&&PkgUtil.containResult(debugBean.getDebugSql())) {
			resultNameList.add(RESULT_NAME);
		}
		
	}
	/**
	 * 将参数表转成对应的Paramsbean
	 * @param arr
	 * @return
	 */
	private List<SqlParamBean> toParamBeans(List<HTabRowBean> arr) {
		Map<String,String> map = new HashMap<>();
		for(HTabRowBean b:arr) {
			map.put(b.getOldRow().get(ColNameCons.NAME),b.getCurrRow().get(ColNameCons.VALUE));
		}
		List<SqlParamBean> params = debugBean.getParams();
		for(SqlParamBean c:params) {
			if(map.containsKey(c.getParamName())) {
				c.setParamValue(map.get(c.getParamName()));
			}
		}
		return params;
	};
	
	
    protected abstract String getDebugSql();
	
	public void close() {
		debug.close();
	}
	

	private void loadResult(List<Map<String,String>> list) {
		tabPanel.getResultTable().load(list, 1);
	}
	
	private void setInfo(String text) {
		tabPanel.setInfo(text);
	}
	

	
	private void resetForm() throws Exception {
		tabPanel.getStackTable().load(null, 1);
		debugTitle = null;
        resetAreaHighLights(debugTitle,-1);
		resetStatus(false);
	}
	
	
	
	
	
	
	
	private void doDebug() throws Exception{
		debug.initConn();
		debug.init();
		//关闭其它的tab页面
		closeOtherTab();
		//设置脚本对应的行信息
		linMap.put(SCRIPT, PkgUtil.getRowMap(editorMap.get(SCRIPT).getText()));
		//执行脚本
		debug.runProc(editorMap.get(SCRIPT).getText());
		debugTitle = SCRIPT;
		
		if(debug.getDebugtype() == DebugType.pldbgapi) {
			work();
			if(!debugTitle.equals(SCRIPT)) {
				String source = debug.getSource(debugBean.getDebugObjId(),null,null,null);
				openNewTabArea(debugBean.getDebugObjId(),debugTitle, source);
				resetAreaHighLights(debugTitle,currentLine );
				work();
			}
			resetStatus(true);
			setResult();
		} else if(debug.getDebugtype() == DebugType.adp_debug) {
			debug.refreshOutput();
			resetStatus(true);
			doOp(STEPINTO);
			doSetPoint(null,SCRIPT);
		}
		
	};

	private void doStop()throws Exception{
		resetForm();
		debug.abort();
		close();
	};
	
	private void doSetPoint(String id, String name)throws Exception{
		PkgDebugArea area = ((PkgDebugArea)editorMap.get(name));
		if(debug.getDebugtype() == DebugType.pldbgapi) {
			if(id==null) {
				area.rmBookMarks(area.getbookmaskLines());
				Lg.debug(getClass(), "Script脚本不支持设置断点");
				return;
			}
		}
		
		
		
		
		List<Integer> list = area.getbookmaskLines();
		LinkedHashMap<String, Map<String, String>> s = debug.getBreakPointsMap();
		List<Integer> brs = new ArrayList<>();
		Map<String,String> map = new HashMap<>();
		
		
		
		if(debug.getDebugtype() == DebugType.adp_debug) {
			if(name.equals(SCRIPT)) {
				map = s.get("");
			}else {
				map = s.get(name);
			}
				
		}else if(debug.getDebugtype() == DebugType.pldbgapi) {
			if(id!=null&&s.get(id)!=null){
				map = s.get(id);
			}
		}
		
		
		if(map != null) {
			for(Entry<String, String> e:map.entrySet()) {
				Integer breakLine = Integer.parseInt(e.getKey());
				if(breakLine!=-1) {
					brs.add(breakLine);
				}
			}
		}
		
		Lg.debug(getClass(), "breaks points:" + brs);
		
		//新增的断点
		List<Integer> add = PkgUtil.getAdd(brs, list);
		//减少的断点
		List<Integer> reduce = PkgUtil.getAdd(list,brs);
		
		//删除减少的断点
		if(!reduce.isEmpty()) {
			for(Integer line:reduce) {
				debug.rmBreakPoint(map.get(line+""), line);
			}
		}
		if(add.isEmpty()) {
			return;
		}
		
		
		//增加新增的断点
		if(debug.getDebugtype() == DebugType.adp_debug) {
				
				if(name.equals(SCRIPT)) {
					area.rmBookMarks( debug.setBreakPoints(add));
				}else {
					List<Map<String, String>> stackData = debug.getStackData();
					Map<String,String> stack = new LinkedHashMap<>();
	                for (Map<String, String> strMap : stackData) {
	                    if (strMap.get("name").equals(name)) {
	                    	stack = strMap;
	                        break;
	                    }
	                }
	                String type = stack.get("type");
	                String ns =  type.contains("PACKAGE") ?  "PACKAGE_BODY" : "TOP_LEVEL";
	                List<Integer> errorList = debug.setBreakPoints(add, name, CsVarUtil.jdbc.getUser(), type, ns);
	                area.rmBookMarks(errorList);
				}
			}else if(debug.getDebugtype() == DebugType.pldbgapi) {
				 debug.setBreakPoints(list,id);
			}
	};
	

	
	private void doOp(String name) throws Exception {
		
		SwingWorker<Object, Void>  worker = new SwingWorker<Object, Void>() {
        	@Override
        	protected Object doInBackground() throws Exception {
        		if(name.equals(STEP)) {
        			return debug.step();
        		}else if(name.equals(STEPINTO)) {
        			if(debug.getDebugtype() == DebugType.pldbgapi) {
        				return debug.stepIntoStr();
        			}else if(debug.getDebugtype() == DebugType.adp_debug) {
        				return debug.stepInto();
        			}
        		}else if(name.equals(CONTINUE)) {
        			return debug.continueGo();
        		}
        		return null;
        		
        	}
        	
        	@Override
        	protected void done() {
        		try {
        			
        			
        			if(debug.getDebugtype() == DebugType.pldbgapi) {
        				SleepUtil.sleep100();
            			if(debug.isEnd()) {
            				Lg.debug(getClass(), "debugee end");
            				return;
            			}
        				if(name.equals(STEPINTO)) {
            				Object o = get();
            				String s = o.toString();
            				s = s.substring(1, s.length()-1);
            				String[] s2 = s.split(",");
            				String oid = s2[0];
            				String name = SqlStrUtil.removeQuote(s2[2]);
            				if(!name.equals(debugTitle)) {
            					openNewTabArea(oid,name, debug.getSource(oid,null,null,null));
            				}
            			}
        				work();
    	                resetAreaHighLights(debugTitle,currentLine );
    	                setResult();
    	                setInfo();
        			}else if(debug.getDebugtype() == DebugType.adp_debug) {
        				Object obj = get();
            			Lg.debug(getClass(), "return ==>" + obj);
        				debug.refreshOutput();
        				if(obj!=null) {
        					
        					int ret = Integer.parseInt(obj.toString());
    						if(ret == -1){
    							debug.abort();
    							resetForm();
    							close();
    						}else if(ret ==0) {
    							resetForm();
    							close();
    						}else {
    							work();
    							if(debugTitle.endsWith(SCRIPT)) {
    								resetAreaHighLights(debugTitle,ret);
    								return;
    							}
    							if(editorMap.get(debugTitle) == null) {
    								
    								if(stackData!=null&&!stackData.isEmpty()) {
    									Map<String,String> map = stackData.get(stackData.size() - 1);
    									String type = map.get(ColNameCons.TYPE);
    									if(type.equals("PACKAGE_BODY")) {
    										type = "PACKAGE BODY";
    									}
    									String schema = debugBean.getDebugObjSchema()==null?CsVarUtil.jdbc.getUser():debugBean.getDebugObjSchema();
    									String sql =debug.getSource(null, schema, debugTitle, type);
            				            if(sql==null) {
            				            	return;
            				            }
            				            //进入新方法打开一个编辑页面
            				            openNewTabArea(null,debugTitle,sql);
            				            //调试方法设置不可修改
            				            editorMap.get(debugTitle).getRSyntaxTextArea().setEditable(false);
            				           
    								}
    								
    								
    							}
    							 resetAreaHighLights(debugTitle,ret);
    				           
    						}
        				}
        			}
	        		
        		}catch (Exception e) {
        			
        				PopDlgUtil.error(getComp(), e);
    					Lg.error(getClass(), e);
        			
				}
        	}
		};
		worker.execute();
		
		
		
	}
	





	/**
	 * 打开一个新的area
	 * @param name
	 * @param text
	 */
	private void openNewTabArea(String id,String name,String text) {
		name2IdMap.put(name, id);
		HTextArea area = new PkgDebugArea() {
			@Override
			protected void onSetBookMark(MouseEvent e) {
				try {
					if(!debugButton.getComp().isEnabled()) {
						doSetPoint(id,name);
					}
				} catch (Exception e1) {
					Lg.error(getClass(), e1);
				}
			}
		};
		if(id!=null) {
			area.setId(id);
		}
		
		editorMap.put(name, area);
		area.setText(text);
		linMap.put(name, PkgUtil.getRowMap(text));
		
		HPanel areaPanel = new HPanel();
		LastPanel last = new LastPanel();
		last.setWithScroll(area.getComp());
		areaPanel.setLastPanel(last);
		scriptTabPanel.setPanel(name, areaPanel, PkgUtil.getConfig(name));
	}
	


    
    /**
     * 根据运行状态，控制按钮和文本的是否可编辑
     * @param isRunning
     */
	private void resetStatus(boolean isRunning) {
		if(!isRunning) {
			try {
				resetAreaHighLights(debugTitle,-1);
			} catch (Exception e) {
				Lg.error(getClass(), e);
			}
		}
		debugButton.setEnabled(!isRunning);
		nextLineButton.setEnabled(isRunning);
		nextPointButton.setEnabled(isRunning);
		enterButton.setEnabled(isRunning);
		stopButton.setEnabled(isRunning);
		editorMap.get(SCRIPT).getRSyntaxTextArea().setEditable(!isRunning);
		tabPanel.setParamsEditable(!isRunning);
	}
	
	
	
	
	
	/**
	 * 设置高亮位置
	 * @param debugTitle bug对应的title
	 * @param currentline 当前行
	 * @throws Exception 
	 */
	private void resetAreaHighLights(String debugTitle,int currentline) throws Exception {
		 //清除所有页面编辑器高亮显示
        editorMap.keySet().forEach(a -> editorMap.get(a).getRSyntaxTextArea().getHighlighter().removeAllHighlights());
        if(currentline==-1) {
        	return;
        }
        if(debugTitle==null) {
			return;
		}
    	 if(!scriptTabPanel.getSelectedId().endsWith(debugTitle)) {
         	scriptTabPanel.selectPanel(debugTitle);
         }
       
        
        //设置当前调试行高亮显示
        int[] attr = linMap.get(debugTitle).get(currentline);
        if (attr != null && attr.length > 0) {
            editorMap.get(debugTitle).getRSyntaxTextArea().getHighlighter().addHighlight(attr[0], attr[1], new DefaultHighlighter.DefaultHighlightPainter(new Color(172, 192, 253)));
        }
      
	}
	
	
	
	
	
	private void work() throws Exception {
		//设置堆载信息
		stackData = debug.getStackData();
		if(!stackData.isEmpty()) {
			tabPanel.getStackTable().load(stackData, 1);
			//获取当前debug的tab页名称
			String name  = stackData.get(stackData.size() - 1).get("name");
			currentLine = Integer.parseInt(stackData.get(stackData.size() - 1).get("row"));
			debugTitle = StringUtils.isNotBlank(name) ? name:SCRIPT;
			//设置参数值
			setTabVariables();
			resetResultData();
		}
	}
	
	
	/**
	 * 关闭除脚本以为的tab页面
	 */
	private void closeOtherTab() {
		List<String> ids = new ArrayList<>();
		for( Entry<String, HTextArea> e:editorMap.entrySet()) {
			ids.add(e.getKey());
		}
		for(String id:ids) {
			if(!id.equals(SCRIPT)) {
				scriptTabPanel.close(id);
				editorMap.remove(id);
			}
		}
		
	}


	private void initButton() {
		debugButton = new HButton(LangMgr.lang(Word2.DEBUG),FaSolidEnum.PLAY) {
			@Override
			protected void onClick() {
				try {
					doDebug();
				} catch (Exception e) {
					Lg.error(getClass(), e);
					if(e.getMessage()!=null&&e.getMessage().contains("ORA-01031")) {
						PopDlgUtil.info(getComp(),"权限不足，请先给用户授予DEBUG权限!");
					}else {
						PopDlgUtil.error(getComp(), e);
					}
					close();
					resetStatus(false);
				}
			}
		};
		
		nextLineButton = new HButton(LangMgr.lang(Word3.NEXT_STEP)+ "(step)",FaSolidEnum.ARROWS_DOWN_TO_LINE) {
			@Override
			protected void onClick() {
				try {
					doOp(STEP);
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(getComp(), e);
				}
			}
		};
		
		nextPointButton = new HButton(LangMgr.sLang(CsL.NEXT_BREAK_POINT)+ "(continue)",FaSolidEnum.ARROWS_TURN_TO_DOTS) {
			@Override
			protected void onClick() {
				try {
					doOp(CONTINUE);
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(getComp(), e);
				}
			}
		};
		enterButton = new HButton(LangMgr.lang(Word2.ENTER) + "(step-into)",FaSolidEnum.ARROW_RIGHT_TO_BRACKET) {
			protected void onClick() {
				
				try {
					doOp(STEPINTO);
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(getComp(), e);
				}
				
			};
		};
		
		stopButton = new HButton(LangMgr.lang(Word2.STOP),FaSolidEnum.STOP) {
			protected void onClick() {
				try {
					doStop();
				} catch (Exception e) {
					Lg.error(getClass(), e);
					PopDlgUtil.error(getComp(), e);
				}
				
			};
		};
	}
	
	

	private void resetResultData() throws Exception {
		if(resultNameList.isEmpty()) {
			return;
		}
		List<Map<String,String>> maps = new ArrayList<>();
		for(String s:resultNameList) {
			Map<String,String> map = new HashMap<>();
			map.put(ColNameCons.NAME, s);
			String var = "";
			if(!debug.isEnd()) {
				var = debug.getVarValue(s);
			}
			
			map.put(ColNameCons.VALUE, StringUtils.isBlank(var)?"":var);
			maps.add(map);
		}
		
		tabPanel.getResultTable().load(maps, 1);
	}

	 /**
     * 设置查询变量显示
     */
    private void setTabVariables() {
        try {
            List<Map<String, String>> list = new ArrayList<>();
            JTable parmtable = tabPanel.getVarTable().getComp();
            if (parmtable.isEditing()) parmtable.getCellEditor().stopCellEditing();
            
            for (int i = 0; i < parmtable.getRowCount(); i++) {
                String name = parmtable.getValueAt(i, 0).toString();
                if (StringUtils.isNotBlank(name)) {
                    Map<String, String> map = new HashMap<>();
                    map.put(ColNameCons.NAME, name);
                    String val = debug.getVarValue(name);
                    val = StringUtils.isNotBlank(val) ? val : "NULL";
                    map.put(ColNameCons.VALUE, val.contains("print_var:") ? "NULL" : val);
                    list.add(map);
                }
            }
            tabPanel.getVarTable().load(list, 1);
        } catch (Exception e) {
            Lg.error(DebugTab.class, e);
            PopDlgUtil.error(getComp(),e);
        }
    }
	
    
    
    private void setInfo() {
    	setInfo(debug.getWarningStr());
    }
   
    private void setResult() throws Exception {
    	List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> info = debug.getResultMap();
        if(info!=null) {
        	
             if(!info.isEmpty()) {
             	for(Entry<String, String> e:info.entrySet()) {
             		Map<String,String> varMap = new HashMap<String, String>();
             		varMap.put(ColNameCons.NAME, e.getKey());
             		varMap.put(ColNameCons.VALUE, e.getValue());
             		list.add(varMap);
             	}
             	loadResult(list);
             }
            
        }
        
       
	}
	

   
	
	

	
}
