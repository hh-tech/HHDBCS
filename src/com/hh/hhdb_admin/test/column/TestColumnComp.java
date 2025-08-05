package com.hh.hhdb_admin.test.column;

import org.apache.commons.lang3.StringUtils;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.column.ColumnComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestColumnComp {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();

		MgrTestUtil.initCsVarJdbc();
		HBarPanel bar=new HBarPanel();
		LabelInput tabName=new LabelInput("表格名称");
		TextInput text=new TextInput();
		bar.add(tabName);
		bar.add(text, 100);
		HButton addBtn=new HButton("新增列") {
			public void onClick() {
				addOrEditColumn(text.getValue(),false);
			}
		};
		HButton mdfBtn=new HButton("设计列") {
			public void onClick() {
				addOrEditColumn(text.getValue(),true);
			}
		};
		
		bar.add(addBtn,mdfBtn);
		CsVarUtil.parentFrame.setToolBar( bar);
		CsVarUtil.parentFrame.show();
	}
	
	private static void addOrEditColumn(String tableName, boolean isUpdate) {
		if(StringUtils.isBlank(tableName)) {
			PopDlgUtil.error("表格名称不能空，必须是已经在数据库存在");
			return;
		}
		
        String colName = "";
        if (isUpdate) {
        	colName=PopDlgUtil.nameInput("输入字段名", "");
        	if(StringUtils.isBlank(colName)) {
        		PopDlgUtil.error("列名称不能空，必须是已经在表格中存在");
        		return;
        	}
        }
        DBTypeEnum dbtype=CsVarUtil.getDbtype();
        tableName=SqlStrUtil.rmQuote4Sel(tableName, dbtype,CsVarUtil.jdbc.getCurrentVersion());
        String schemaName=CsVarUtil.jdbc.getSchema();
        if(StringUtils.isBlank(schemaName)) {
        	schemaName=SqlStrUtil.rmQuote4Sel(CsVarUtil.jdbc.getUser(), dbtype,CsVarUtil.jdbc.getCurrentVersion());
        }
        
        ColumnComp columnForm = new ColumnComp(CsVarUtil.conn, dbtype,schemaName , tableName) {
            @Override
            public void refreshTree() {
            	System.out.println("刷新树状结构");
            }
        };
        columnForm.show(isUpdate, colName);
    }

}
