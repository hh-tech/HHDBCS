package com.hh.hhdb_admin.mgr.table_open3.gui;

import java.awt.Dimension;

import com.hh.hhdb_admin.comp.SqlViewDialog;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.TextInputExt;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.CsL;




public class PkgHeadBar extends HBarPanel{
	private TextInputExt whereTextInput;
	private OpenTableComp opc;
	private SqlViewDialog dialog;
	public PkgHeadBar(OpenTableComp opc) {
		super(HBarLayout.newLayout(AlignEnum.RIGHT));
		this.opc = opc;
		initWhereTextInput();
		dialog = new SqlViewDialog(this.opc.getComp());
	}
	
	
	public void setSearchBar(PkgSearchBar s){
		removeAll();
		add(s);
		setLastHComp(whereTextInput);
	}
	
	
	
	
	/**
	 * 初始化where sql筛选框
	 */
	private void initWhereTextInput() {
		HImgButton sqlBut = new HImgButton(){
			@Override
			protected void onClick() {
				if(StringUtils.isBlank(opc.pageSql)) {
					return;
				}
				
				dialog.setSql(opc.pageSql);
				dialog.show();
			}
		};
		sqlBut.setMouseExitedIcon(FaSolidEnum.OUTDENT,16);
		sqlBut.setMouseEnteredIcon(FaSolidEnum.OUTDENT,16);
		sqlBut.setToolTipText(LangMgr.lang(Word2.EXAMINE)+"SQL");
		whereTextInput = new TextInputExt("", sqlBut, null, LangMgr.sLang(CsL.FILTER_RESULT));
		whereTextInput.getExtBtn().setToolTipText(LangMgr.lang(Word2.EXPAND));
		whereTextInput.getTextInput().getComp().setToolTipText(LangMgr.sLang(CsL.FILTER_RESULT));
		
		sqlBut.getComp().setPreferredSize(new Dimension(30, 20));
		//回车监听
		whereTextInput.getTextInput().getComp().addActionListener(e -> {
			this.opc.loadData();
			this.opc.loadCount();
		});

	}
	
	
	public String getWhereTxt() {
		return whereTextInput.getValue();
	}
	

}
