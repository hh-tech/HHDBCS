package com.hh.hhdb_admin.mgr.table_open.comp;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.*;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.table_open.common.ExpTabPopMenu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

/**
 * @author ouyangxu
 * @date 2021-12-01 0001 15:05:52
 * @description 表格工具栏
 */
public class TableSearchToolBar extends SearchToolBar {
	private HButton tableTypeBtn;
	private List<Enum<?>> fieldTypeList;
	private String schema;

	
	public TableSearchToolBar(HTable htab,List<Enum<?>> fieldTypeList, String schema) {
		super(htab);
		this.fieldTypeList = fieldTypeList;
		this.schema = schema;
	}

	@Override
	protected void initCheckPop() {
		if (checkPop == null) {
			checkPop = new HCheckPopup("", mapLabel, mapValue) {
				@Override
				protected void onAction(Map<String, Boolean> map) {
					popupOnAction(map);
				}

				@Override
				protected void initBtn() {
//					jbtn = (JButton) this.comp;
					setIcon(FaSolidEnum.LIST,16);
					setText(btnLabel);
					getComp().setFocusPainted(false);
//					jbtn.setBorderPainted(false);
					getComp().setContentAreaFilled(false);
					setToolTipText(LangMgr.lang(Word1.COLUMN));
				}
			};
			checkPop.setId(SEARCH_CHECK_POP_ID);
			add(checkPop);
		}
		initOther();
	}

	protected void initOther() {
		if (tableTypeBtn != null) remove(tableTypeBtn);
		tableTypeBtn = new HButton();
		tableTypeBtn.setIcon(FaSolidEnum.EYE,16);
		tableTypeBtn.getComp().setFocusPainted(false);
		tableTypeBtn.getComp().setContentAreaFilled(false);
		tableTypeBtn.setToolTipText(LangMgr.lang(Word4.DISPLAY_SWITCHING));
		tableTypeBtn.addActionListener(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				HPopMenu pm = new HPopMenu();
				boolean tableModel = htab.isRowStyle();
				boolean autoResizeCol = htab.isAutoResizeCol();
				boolean showLine = htab.getComp().getShowHorizontalLines() && htab.getComp().getShowVerticalLines();
				CheckMenuItem autoSizeItem = new CheckMenuItem(LangMgr.lang(Word4.AUTOMATIC_WIDTH)){
					@Override
					protected void onAction() {
						htab.setAutoResizeCol(!isSelected());
						htab.reload();
					}
				};
				autoSizeItem.setSelected(!autoResizeCol);
				pm.addCheckedItem(autoSizeItem);
				CheckMenuItem showLineItem = new CheckMenuItem(LangMgr.sLang(CsL.DISPLAY_INNER_BORDER)){
					@Override
					protected void onAction() {
						htab.setShowHorLine(isSelected());
						htab.setShowVerLine(isSelected());
					}
				};
				showLineItem.setSelected(showLine);
				pm.addCheckedItem(showLineItem);
				pm.addSeparator();
				RadioMenuItem hViewItem = new RadioMenuItem(LangMgr.lang(Word4.LATERAL_DISPLAY));
				hViewItem.getComp().addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						typeActionPerformed(e);
					}
				});
				hViewItem.setSelected(tableModel);
				RadioMenuItem vViewItem = new RadioMenuItem(LangMgr.lang(Word4.PORTRAIT_DISPLAY));
				vViewItem.getComp().addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						typeActionPerformed(e);
					}
				});
				vViewItem.setSelected(!tableModel);
				pm.addRadioItem(hViewItem,vViewItem);
				HMenuItem nullSiteItem = new HMenuItem(LangMgr.sLang(CsL.NULL_SETTING)){
					@Override
					protected void onAction() {
						String inputObj = PopDlgUtil.nameInput(LangMgr.lang(Word2.SETTINGS),htab.getNullSymbol());
						htab.setNullSymbol(inputObj);
						htab.getComp().updateUI();
						CsVarUtil.CONF.setNullValue(inputObj);
						CsVarUtil.saveConfig();
					}
				};
				pm.addSeparator();
				pm.addItem(nullSiteItem);
				pm.getComp().show(tableTypeBtn.getComp(), tableTypeBtn.getWidth() / 3, tableTypeBtn.getHeight());
			}
		});
		add(tableTypeBtn);
	}

	protected void typeActionPerformed(ActionEvent e) {
		JRadioButtonMenuItem jrb = (JRadioButtonMenuItem) e.getSource();
		boolean bool = jrb.isSelected() && jrb.getText().equals("横向显示");
		
//		boolean bool = e.getSource() == tableTypeBtn.getHorViewItem();
		htab.setRowStyle(bool);
		htab.setRowPopMenu(bool ? new ExpTabPopMenu(fieldTypeList,schema) : null);
	}
}
