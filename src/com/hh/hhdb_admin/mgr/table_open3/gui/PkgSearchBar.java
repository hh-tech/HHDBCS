package com.hh.hhdb_admin.mgr.table_open3.gui;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JRadioButtonMenuItem;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.CheckMenuItem;
import com.hh.frame.swingui.view.hmenu.HCheckPopup;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.hmenu.HPopMenu;
import com.hh.frame.swingui.view.hmenu.RadioMenuItem;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;

class PkgSearchBar extends SearchToolBar {
	private HButton tableTypeBtn;
	public PkgSearchBar(OpenTableComp opc) {
		super(opc.oTable);
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
					setIcon(FaSolidEnum.LIST,16);
					setText(btnLabel);
					getComp().setFocusPainted(false);
					getComp().setContentAreaFilled(false);
					setToolTipText(LangMgr.lang(Word1.COLUMN));
				}
			};
			checkPop.setId(SEARCH_CHECK_POP_ID);
			add(checkPop);
		}
		initOther();
	}

	private void initOther() {
		if (tableTypeBtn != null) remove(tableTypeBtn);
		tableTypeBtn = new HButton("");
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

	private void typeActionPerformed(ActionEvent e) {
		JRadioButtonMenuItem jrb = (JRadioButtonMenuItem) e.getSource();
		boolean bool = jrb.isSelected() && jrb.getText().equals("横向显示");
		htab.setRowStyle(bool);
//		htab.setRowPopMenu(bool ? new ExpTabPopMenu(fieldTypeList,schema) : null);
	}
}
