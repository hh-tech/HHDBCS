package com.hh.hhdb_admin.mgr.main_frame;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.ClassLoadUtil;
import com.hh.frame.json.Json;
import com.hh.frame.swingui.laf.FontSizeEnum;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.ctrl.HWebLinkButton;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.mem_bar.MemBarComp;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.hhdb_admin.CSProduct;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @author ouyangxu 底部栏 (hhlogo 皮肤切换 内存条显示)
 * @date 2021-11-02 0002 16:54:57
 */
public class BottomStatusBar extends HBarPanel {
	/**
	 * 获取底部栏 (hhlogo 皮肤切换 内存条显示)
	 *
	 * @param switchSkinListener 皮肤下拉框选择监听事件
	 */
	public BottomStatusBar() {
		super(null);
		init();
	}

	public void init() {
		HBarLayout barLayout=new HBarLayout();
		barLayout.setAlign(AlignEnum.LEFT);
		HBarPanel footLeft=new HBarPanel(barLayout);
		String val = "";
		try {
			val = Json.parse(ClassLoadUtil.loadTextRes(CSProduct.class,"CSProduct.json")).asObject().get("website").asObject().get("v").asString();
		}catch (Exception e){
			e.printStackTrace();
		}
		HWebLinkButton box = new HWebLinkButton(ImgUtil.resize(IconFileUtil.getLogo(),16,16),val,CsVarUtil.website);
		
		SelectBox skinBox = LafMgr.getSkinSelectBox();
		skinBox.addListener(e -> {
			CsVarUtil.CONF.setSkin(LafIdEnum.valueOf(skinBox.getValue()));
			CsVarUtil.saveConfig();
		});
		SelectBox sizeBox = LafMgr.getSizeSelectBox();
		sizeBox.addListener(e -> {
			CsVarUtil.CONF.setFontSize(FontSizeEnum.valueOf(sizeBox.getValue()));
			CsVarUtil.saveConfig();
		});
		
		if(LafMgr.getCurrentFontSize()!=null) {
			sizeBox.setValue(LafMgr.getCurrentFontSize().name());
		}
		
		footLeft.add(box,sizeBox,skinBox);
		
		HGridPanel grid =new HGridPanel(new HGridLayout(GridSplitEnum.C6));
		grid.setComp(1, footLeft);
		grid.setComp(2, new MemBarComp());
		grid.getComp().setBorder(BorderFactory.createMatteBorder(1,0,0,0, Color.lightGray));
		setLastHComp(grid);
	}
}
