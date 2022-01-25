package com.hh.hhdb_admin.mgr.table_open.comp;

import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.managers.style.StyleId;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyConstant;
import com.hh.hhdb_admin.mgr.table_open.common.ModifyTabTool;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Objects;

/**
 * @author ouyangxu
 * @date 2021-11-29 0029 16:11:28
 * @description 分页大小设置下拉框
 */
public class PageSelect extends AbsInput {

	protected WebComboBox comboBox;
	protected Collection<?> items;
	protected String selected;
	protected WebButton button;
	protected WebPopOver popOver;

	public PageSelect() {
		this(null);
	}

	public PageSelect(String id) {
		super(id);
		init();
	}

	public PageSelect(Collection<String> items, String selected) {
		super(null);
		this.items = items;
		this.selected = selected;
		init();
	}

	@Override
	public String getValue() {
		return Objects.requireNonNull(comboBox.getSelectedItem()).toString();
	}

	@Override
	public void setValue(String value) {
		this.selected = value;
		comboBox.setSelectedItem(value);
	}


	protected void init() {
		popOver = new WebPopOver(StyleId.popover, button);
		if (items != null) {
			comboBox = new WebComboBox(items);
			if (selected != null) {
				comboBox.setSelectedItem(selected);
				popOver.fireClosed();
			}
		} else {
			comboBox = new WebComboBox();
		}

		comboBox.putClientProperty(StyleId.STYLE_PROPERTY, StyleId.comboboxUndecorated);
		button = new WebButton(ModifyTabTool.getIcon(ModifyConstant.PAGE_SIZE_SETTING_ICON));
		button.setStyleId(StyleId.buttonIconHover);

		popOver.setPadding(5);
		popOver.setCloseOnFocusLoss(true);
		final WebPanel container = new WebPanel(StyleId.panelTransparent, new BorderLayout(5, 5));

		final WebLabel label = new WebLabel("选择分页大小", WebLabel.CENTER);
		container.add(label, BorderLayout.NORTH);
		container.add(comboBox, BorderLayout.CENTER);
		popOver.add(container);
		button.addActionListener(e -> {
			popOver.show(button, PopOverDirection.up);
		});

		comp = button;
	}

	public WebComboBox getComboBox() {
		return comboBox;
	}

	@Override
	public JButton getComp() {
		return button;
	}

	public WebPopOver getPopOver() {
		return popOver;
	}
}
