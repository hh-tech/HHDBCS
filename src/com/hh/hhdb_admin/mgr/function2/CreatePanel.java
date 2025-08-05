package com.hh.hhdb_admin.mgr.function2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.ProcFunSqlUtil;
import com.hh.frame.db_sql.info.SqlFunBean;
import com.hh.frame.db_sql.info.SqlParamBean;
import com.hh.frame.db_sql.info.SqlProcBean;
import com.hh.frame.db_sql.info.SqlParamBean.ParamType;
import com.hh.frame.db_sql.ora.OraSqlUtil.CompileType;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CompileTool;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.comp.column.DataTypePanel;

//import com.hh.hhdb_admin.comp.DataTypePanel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class CreatePanel extends LastPanel{
	private TextInput nameInput;
	private TextInput returnInput;
	private boolean isFun;
	private String schema;
	private static final String TYPE = "type";
	private static final String NAME = "name";
	private static final String PTYPE = "ptype";
	private int count=1;
	private HTable table;
	private DBTypeEnum dbtype= CsVarUtil.getDbtype();
	private Version ver=CsVarUtil.jdbc.getCurrentVersion();
	
	/**
	 * 
	 */
	public CreatePanel(boolean isFun,String schema) {
        HPanel hPanel = new HPanel();
        this.isFun = isFun;
        this.schema = schema;
        HBarPanel barPanel = createHBarPanel();
        barPanel.add(new HButton("下一步") {
        	@Override
        	protected void onClick() {
        		doNext();
        	}
        });
        
        hPanel.add(barPanel);
        //名称
        HBarPanel nameBar = createHBarPanel();
        nameInput = new TextInput("name");
        nameInput.getComp().setColumns(20);
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String number = simpleFormat.format(new Date());
        String defaultName = isFun?"F_"+number:"P_"+number;
        nameInput.setValue(SqlStrUtil.rmQuote4Sel(defaultName, dbtype, ver));
        nameBar.add(new LabelInput("名称"));
        nameBar.add(nameInput);
        //返回值 
    	HDivLayout layout = new HDivLayout();
		layout.setTopHeight(10);
		layout.setBottomHeight(10);
    	DataTypePanel panel = new DataTypePanel(dbtype, ver);
        
        if(isFun) {
        	
        	returnInput = new TextInput();
            returnInput.getComp().setColumns(20);
            nameBar.add(new LabelInput(LangMgr.lang(Word3.RETURNED_VALUE) + ":"));
            nameBar.add(returnInput);
            returnInput.setValue(panel.getFullTypeDefine());
            nameBar.add(new HButton("选择") {
            	@Override
            	protected void onClick() {
            		
            		if(!StringUtils.isBlank(returnInput.getValue())) {
//                        panel.setByFullType(returnInput.getValue());
                        try {
							panel.setFullTypeDefine(returnInput.getValue());
						} catch (Exception e) {
							GlobalWorkUtil.err(CreatePanel.class, e);
						}
            		}
            		
            		HDialog dialog = new HDialog(this.getComp(), 500,300,false) {
            			@Override
            			protected void onConfirm() {
                            returnInput.setValue(panel.getFullTypeDefine());
            			}
            		};
            		dialog.setRootPanel(panel);
            		dialog.show();
            		
            		
            		
            	}
            });
        }
        
        hPanel.add(nameBar);
        
        
        
        
        HBarPanel buttonBar = createHBarPanel();
        buttonBar.add(new HButton("添加") {
        	
        	@Override
        	protected void onClick() {
        		Map<String, String> line = new HashMap<>();
                line.put(NAME,"p" + genCount());
                
                JsonObject obj = new JsonObject();
//                obj.set(JsonCol.__TEXT, panel.getFullType());
                obj.set(JsonCol.__TEXT, panel.getFullTypeDefine());
                line.put(TYPE, obj.toString());
                line.put(PTYPE, SqlParamBean.ParamType.IN.name());
                table.add(line);
        	}
        	
        	
        });
        buttonBar.add(new HButton("删除") {
        	@Override
        	protected void onClick() {
        		table.deleteSelectRow();
        	}
        });
        hPanel.add(buttonBar);
        setHead(hPanel.getComp());
        initTable();
        table.load(null, 1);
       
    }
	protected void doNext() {
		String sql = genSql();
		if(StringUtils.isBlank(sql)) {
			PopDlgUtil.info(getComp(), "暂不支持");
			return;
		}
		Window w = HDialog.getWindowForComponent(this.comp);
		Window parent = w;
		if(w.getParent()!=null) {
			parent = HDialog.getWindowForComponent(w.getParent());
		}
		
		if(w instanceof JDialog) {
			w.setVisible(false);
		}
		
		
		HDialog dialog = new HDialog(parent,800,600,false);
		PkgEditPanel panel = new PkgEditPanel("") {
		
			@Override
			protected void doPrev() {
				w.setVisible(true);
				dialog.dispose();
			}
			
			@Override
			protected void inform() {
				
				CreatePanel.this.inform();
				
				dialog.dispose();
				w.dispose();
				
			}
		};
		CompileType type = isFun?CompileType.function:CompileType.procedure;
		String objName = SqlStrUtil.rmQuote4Sel(nameInput.getValue(), dbtype,ver);
		CompileTool tool = new CompileTool(dbtype,ver, CsVarUtil.conn, schema,objName, type);
		panel.setTool(tool);
		
		
		panel.init(sql, true);
		dialog.setStatusBar(new HBarPanel());
		dialog.setWindowTitle(LangMgr.lang(Word2.DESIGN) + (isFun?LangMgr.lang(Word2.FUNCTION):LangMgr.lang(Word4.PROCEDURE)));
		dialog.setRootPanel(panel);
		dialog.show();
	}
	
	
	protected void inform() {
		System.out.println("通知");
	}
	
	
	
	private int genCount() {
		return count++;
	}
	
	
	
	private void initTable() {
		table = new HTable();
	    setWithScroll(table.getComp());
		table.addCols(new DataCol(NAME, "参数名"));
		
		if(!isFun) {
			ParamType[] arr = SqlParamBean.ParamType.values();
			List<String> list = new ArrayList<>();
			for(ParamType t:arr) {
				list.add(t.name());
			}
			table.addCols(new ListCol(PTYPE, "参数类型",list));
		}
		table.addCols(new PkgTypeCol(TYPE, "数据类型"));
	}
	

	
	private SqlProcBean getData() {
		SqlProcBean b;
		String inputName = SqlStrUtil.rmQuote4Sel(nameInput.getValue(), dbtype,ver);
		if(isFun) {
			SqlFunBean funB = new SqlFunBean(inputName);
			funB.setReturnType(returnInput.getValue());
			b = funB;
		}else {
			b = new SqlProcBean(inputName);
		}
		b.setSchemaName(schema);
		
		List<SqlParamBean> list = new ArrayList<>();
		
		List<HTabRowBean> row =  table.getRowBeans(null);
		for(HTabRowBean be:row) {
			String name =  getTableData(be,NAME);
			String type = Json.parse(getTableData(be,TYPE)).asObject().getString(JsonCol.__TEXT);
			SqlParamBean spb = new SqlParamBean(name, type);
			if(!isFun) {
				String ptype =  getTableData(be,PTYPE);
				spb.setParamType(ParamType.valueOf(ptype));
			}
			
			list.add(spb);
		}
		
		b.setParams(list);
		
		
		return b;
	}
	
	private String getTableData(HTabRowBean be,String name) {
		if(be.getCurrRow()!=null&&be.getCurrRow().get(name)!=null) {
			return be.getCurrRow().get(name);
		}else {
			return be.getOldRow().get(name);
		}
	}
	

	
	protected String genSql() {
		if(isFun) {
			return ProcFunSqlUtil.getCreateFunTemplateSql(dbtype,ver, (SqlFunBean) getData());
		}else {
			return ProcFunSqlUtil.getCreateProcTemplateSql(dbtype,ver, getData());
		}
	}
	
	
	private HBarPanel createHBarPanel() {
		HBarLayout layout = new HBarLayout();
		layout.setAlign(AlignEnum.LEFT);
		layout.setTopHeight(10);
		layout.setBottomHeight(5);
		HBarPanel barPanel = new HBarPanel(layout);
		
		return barPanel;
	}
	


}
