package com.hh.hhdb_admin.mgr.tool.comp;

import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.text.WebTextField;
import com.alee.managers.style.StyleId;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.mgr.login.comp.CommonComp;
import com.hh.hhdb_admin.mgr.menubar.MenubarComp;
import com.hh.hhdb_admin.mgr.tool.ToolUtil;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;
import java.util.List;

/**
 * @author ouyangxu
 * @date 2021-12-22 0024 10:44:50
 */
public class CommonButtons {
	private HButton formatBtn;
	private HButton formatBreakWidthBtn;
	private HButton copyBtn;
	private HButton clearBtn;

	public String formatBreakWidth;

	public CommonButtons() {
		initToolBarBtn();
	}

	protected void initToolBarBtn() {
		formatBtn = new HButton(ToolUtil.getLang(ToolUtil.SQL_FORMAT));
		formatBtn.setIcon(ToolUtil.getIcon(ToolUtil.SQL_FORMAT));

		formatBreakWidthBtn = new HButton(ToolUtil.getLang(ToolUtil.SQL_FORMAT_BREAK_WIDTH));
		formatBreakWidthBtn.setIcon(ToolUtil.getIcon(ToolUtil.SQL_FORMAT_BREAK_WIDTH));
		formatBreakWidthBtn.addActionListener(new BreakWidthBthListener());

		copyBtn = new HButton(ToolUtil.getLang(ToolUtil.COPY_VALUE));
		copyBtn.setIcon(MenubarComp.getIcon("copy"));

		clearBtn = new HButton(ToolUtil.getLang(ToolUtil.CLEAR));
		clearBtn.setIcon(CommonComp.getIcon("reset"));
	}

	public List<AbsHComp> getButtons() {
		return Arrays.asList(formatBtn, formatBreakWidthBtn, copyBtn, clearBtn);
	}

	public HButton getFormatBtn() {
		return formatBtn;
	}

	public HButton getCopyBtn() {
		return copyBtn;
	}

	public HButton getClearBtn() {
		return clearBtn;
	}

	class BreakWidthBthListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			final WebPopOver popOver = new WebPopOver();
			popOver.setCloseOnFocusLoss(true);
			popOver.setPadding(10);

			final WebPanel container = new WebPanel(StyleId.panelTransparent, new BorderLayout(5, 5));
			final WebLabel label = new WebLabel(ToolUtil.getLang(ToolUtil.SQL_FORMAT_BREAK_WIDTH), WebLabel.CENTER);
			container.add(label, BorderLayout.NORTH);

			final WebTextField field = new WebTextField(formatBreakWidth != null ? formatBreakWidth : ToolUtil.getSqlFormatBreakWidth(), 20);
			field.setHorizontalAlignment(WebTextField.CENTER);
			field.addActionListener(e1 -> {
				if (ToolUtil.verifyBreakWidth(field.getText())) {
					formatBreakWidth = field.getText();
				}
			});
			field.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					if (ToolUtil.verifyBreakWidth(field.getText())) {
						formatBreakWidth = field.getText();
					}
				}
			});
			container.add(field, BorderLayout.CENTER);
			popOver.add(container);
			popOver.show((Component) e.getSource(), PopOverDirection.down);
		}
	}

	public String getFormatBreakWidth() {
		if (formatBreakWidth == null) {
			formatBreakWidth = ToolUtil.getSqlFormatBreakWidth();
		}
		return formatBreakWidth;
	}
}
