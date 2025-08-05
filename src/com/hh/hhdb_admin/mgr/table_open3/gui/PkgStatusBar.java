package com.hh.hhdb_admin.mgr.table_open3.gui;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;

class PkgStatusBar extends HBarPanel{
	public PkgStatusBar(boolean readonly,PkgPageBar pbar,PkgModifyBar mbar) {
		super(HBarLayout.newLayout(AlignEnum.RIGHT));
		if (!readonly) {
			setLastHComp(mbar);
		}
		add(pbar);
	}
}
