package com.hh.hhdb_admin.mgr.toolbar;

import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.TopImageBtn;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.CommToolMar;
import java.util.*;

/**
 * @author Jiang
 * @date 2020/10/12
 */

public class ToolbarComp extends HBarPanel {

	public ToolbarComp(HBarLayout layout) {
		super(layout);
		CommToolMar tm = new CommToolMar();
		List<String> btnList = tm.genToolInfo();
		for (String key : btnList) {
			add(new TopImageBtn(tm.genTitle(key), tm.getIcon(key)) {
				@Override
				protected void onClick() {
					tm.onBtnClick(key);
				}
			});
		}

	}
}
