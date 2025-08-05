package com.hh.hhdb_admin.comp.column;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.DBTypeEnum.Version;
import com.hh.frame.create_dbobj.table2.CreateNewTabCol;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.with_label.WithLabelComp;

/*
* Author:Zhang Dagang
* Date:2024年2月21日
*/
public class ColumnPanel extends HPanel {
	private HBarPanel nameBarPanel=new HBarPanel();
	private CheckBoxInput notNullCheckBox=new CheckBoxInput(null,"非空");
	private TextInput nameInput=new TextInput();
	private TextAreaInput defautValueInput=new TextAreaInput(null,null,2);
	private TextAreaInput commentInput=new TextAreaInput(null,null,2);
	private DataTypePanel dateTypePanel=null;
	private CreateNewTabCol oldCreateNewTabCol=null;
	
	public ColumnPanel(DBTypeEnum dbtype,Version ver) {
		insertComp();
		nameBarPanel.add(notNullCheckBox);
		nameBarPanel.setLastHComp(nameInput);
		dateTypePanel=new DataTypePanel(dbtype,ver);
		add(new WithLabelComp("名称",nameBarPanel,GridSplitEnum.C3));
		add(new WithLabelComp("类型",dateTypePanel,GridSplitEnum.C3));
		add(new WithLabelComp("默认值",defautValueInput,GridSplitEnum.C3));
		add(new WithLabelComp("注解",commentInput,GridSplitEnum.C3));
	}
	

	public ColumnPanel(DBTypeEnum dbtype,Version ver,CreateNewTabCol tabCol) throws Exception {
		this(dbtype,ver);
		nameInput.setValue(tabCol.getColName());
		notNullCheckBox.setValue(!tabCol.getIsNull());
		defautValueInput.setValue(tabCol.getDefautValue());
		commentInput.setValue(tabCol.getComment());
		oldCreateNewTabCol=tabCol;
		dateTypePanel.setFullTypeDefine(tabCol.getFullTypeDefine());
	}
	
	public CreateNewTabCol getCreateNewTabCol() {
		CreateNewTabCol c=new CreateNewTabCol(nameInput.getValue(),dateTypePanel.getFullTypeDefine());
		c.setComment(commentInput.getValue());
		c.setIsNull(!notNullCheckBox.isChecked());
		c.setDefautValue(defautValueInput.getValue());
		return c;
	}
	
	public CreateNewTabCol getOldCreateNewTabCol() {
		return oldCreateNewTabCol;
	}
	
	protected void insertComp() {
	
	}
	

}
