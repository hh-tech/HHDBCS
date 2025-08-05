package com.hh.hhdb_admin.mgr.toolbar;

import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.TopImageBtn;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.CommToolMar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ToolbarComp extends HBarPanel {
	private final CommToolMar commToolMar;

	public ToolbarComp(HBarLayout layout) {
		super(layout);
		commToolMar = new CommToolMar();

		List<TopImageBtn> managesButtons = initButtons(commToolMar.genManageNames());
		List<TopImageBtn> otherButtons = initButtons(commToolMar.genOtherNames());
		List<TopImageBtn> windowButtons = initButtons(commToolMar.genWindowNames());
		for (TopImageBtn btn : managesButtons) {
			add(btn);
		}
		add(new ToolBarSeparator());
		for (TopImageBtn otherButton : otherButtons) {
			add(otherButton);
		}
		add(new ToolBarSeparator());
		for (TopImageBtn windowButton : windowButtons) {
			add(windowButton);
		}
	}

	/**
	 *
	 * @param btnNames 按钮id
	 * @return
	 */
	private List<TopImageBtn> initButtons(Map<String, Map<String, Enum<?>>> btnNames) {
		List<TopImageBtn> imageButtons = new ArrayList<>();
		for (String key : btnNames.keySet()) {
			Map<String, Enum<?>> map = btnNames.get(key);
			for (String s : map.keySet()) {
				TopImageBtn topImageBtn = new TopImageBtn(s, map.get(s)) {
					@Override
					protected void onClick() {
						commToolMar.onBtnClick(key);
					}
				};
				imageButtons.add(topImageBtn);
			}
		}
		return imageButtons;
	}


}
