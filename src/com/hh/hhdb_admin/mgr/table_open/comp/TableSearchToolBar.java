package com.hh.hhdb_admin.mgr.table_open.comp;

import com.alee.managers.style.StyleId;
import com.alee.managers.tooltip.TooltipManager;
import com.hh.frame.swingui.view.hmenu.HCheckPopup;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * @author ouyangxu
 * @date 2021-12-01 0001 15:05:52
 * @description 表格工具栏
 */
public class TableSearchToolBar extends SearchToolBar {
	public static final String SEARCH = "search";
	public static final String COLUMN = "column";
	protected TableViewSplitButton tableTypeBtn;

	public TableSearchToolBar(HTable htab) {
		super(htab);
	}

	public TableSearchToolBar(HTable htab, HBarLayout barLayout) {
		super(htab, barLayout);
	}

	@Override
	protected void init() {
		searchInput = new TableSearchTextInput() {
			@Override
			protected void doChange() {
				searchTextChange();
			}
		};
		add(searchInput, 200);
		htab.setSearchToolBar(this);
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
					jbtn = (JButton) this.comp;
					jbtn.setIcon(ModifyTabTool.getIcon(COLUMN));
					jbtn.putClientProperty(StyleId.STYLE_PROPERTY, StyleId.buttonIconHover);
					jbtn.setText(btnLabel);
					jbtn.setFocusPainted(false);
					jbtn.setBorderPainted(false);
					jbtn.setContentAreaFilled(false);
					TooltipManager.setTooltip(jbtn, "列");
				}
			};
			checkPop.setId(SEARCH_CHECK_POP_ID);
			add(checkPop);
		}
		initOther();
	}

	protected void initOther() {
		if (tableTypeBtn != null) {
			remove(tableTypeBtn);
		}
		tableTypeBtn = new TableViewSplitButton(htab);
		tableTypeBtn.getAutoSizeItem().addActionListener(e -> {
			JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) e.getSource();
			boolean selected = menuItem.isSelected();
			htab.setAutoResizeCol(!selected);
			htab.reload();
		});
		tableTypeBtn.getHorViewItem().addActionListener(this::typeActionPerformed);
		tableTypeBtn.getVerViewItem().addActionListener(this::typeActionPerformed);
		tableTypeBtn.getShowLineItem().addActionListener(e -> {
			boolean isSelect = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			htab.setShowHorLine(isSelect);
			htab.setShowVerLine(isSelect);
		});
		add(tableTypeBtn);

	}

	protected void typeActionPerformed(ActionEvent e) {
		Object source = e.getSource();
		htab.setRowStyle(source == tableTypeBtn.getHorViewItem());
	}

	public TextInput getSearchInput() {
		return searchInput;
	}
}
