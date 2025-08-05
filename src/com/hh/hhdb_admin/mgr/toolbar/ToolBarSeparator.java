package com.hh.hhdb_admin.mgr.toolbar;

import com.hh.frame.swingui.view.abs.AbsHComp;

import javax.swing.*;
import java.awt.*;

/**
 * @author ouyangxu
 * @date 2022-01-13 0013 14:30:44
 */
public class ToolBarSeparator extends AbsHComp {
	protected JToolBar.Separator separator;

	public ToolBarSeparator() {
		super();
		comp = separator = new JToolBar.Separator(new Dimension(5,50));
		separator.setOrientation(JSeparator.VERTICAL);
	}

	@Override
	public Component getComp() {
		return separator;
	}
}
