package com.hh.hhdb_admin.common.csTabPanel;

import com.alee.laf.grouping.GroupPane;
import com.alee.managers.style.StyleId;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.LastPanel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CSTablePanel extends AbsHComp {
	private JToggleButton localBtn;
	private Map<String, Component> compMap = new HashMap<>();
	private Map<String, JToggleButton> btnMap = new HashMap<>();
	private LastPanel lp = new LastPanel();
	private GroupPane groupPane;
	//private HBarPanel bar;

	public CSTablePanel() {
		comp = lp.getComp();
		groupPane = new GroupPane();
		groupPane.setPadding(5,2,20,2);
		groupPane.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Color.lightGray));
//		HBarLayout hlay = new HBarLayout();
//		hlay.setAlign(AlignEnum.LEFT);
//		bar = new HBarPanel(hlay);
		lp.setHead(groupPane);
	}

	public void addPanel(String id, String title, Component comp) {
		JToggleButton toggleButton = new JToggleButton(title);
		toggleButton.putClientProperty(StyleId.STYLE_PROPERTY, StyleId.togglebuttonHover);
		toggleButton.addActionListener(e -> {
			click(id,toggleButton);
		});
//		btn.setSize(100, 30);
//		setSelectSty(btn, false);
		if (localBtn == null) {
			localBtn = toggleButton;
			lp.set(comp);
			toggleButton.setSelected(true);
			//setSelectSty(localBtn, true);
		}
		compMap.put(id, comp);
		btnMap.put(id, toggleButton);
		groupPane.add(toggleButton);
		lp.updateUI();
	}

	public void selectPanel(String id) {
		JToggleButton btn = btnMap.get(id);
		if(btn==null) {
			return;
		}
		click(id, btn);
	}
	
	/**
	 * 关闭
	 * @param id
	 */
	public void closeTabPanel(String id) {
		JPanel jp = (JPanel)lp.getComp();
		jp.remove(compMap.get(id));
		compMap.remove(id);
		
		groupPane.remove(btnMap.get(id));
		btnMap.remove(id);
	}
	
	public int getTabCount(){
		return compMap.size();
	}
	
	protected void stateChanged(String id, String title) {

	}

	private void click(String id, JToggleButton btn) {
		if (btn != localBtn) {
			btn.setSelected(true);
			localBtn = btn;
			lp.set(compMap.get(id));
			lp.updateUI();
			stateChanged(id, btn.getText());
		}
	}

//	private void setSelectSty(HButton btn, boolean sel) {
//		JButton jbtn = (JButton) btn.getComp();
//		if (sel) {
//			jbtn.setBorder(BorderFactory.createLineBorder(Color.green, 2, false));
//		} else {
//			jbtn.setBorder(BorderFactory.createLineBorder(Color.cyan, 0, false));
//		}
//	}
}
