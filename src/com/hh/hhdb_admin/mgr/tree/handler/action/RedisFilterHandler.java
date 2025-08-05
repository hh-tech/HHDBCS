package com.hh.hhdb_admin.mgr.tree.handler.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.util.SbUtil;
import com.hh.frame.create_dbobj.treeMr.base.RedisObjGroupNode;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.IntSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

/*
* Author:Zhang Dagang
* Date:2024年4月27日
*/
public class RedisFilterHandler extends AbsHandler {
	private HDialog dlg=new HDialog(CsVarUtil.getMainDialog(),HWindow.SMALL_WIDTH) {
		protected void onConfirm() {
			apply();
		}
	};
	private TextInput filterInput=new TextInput();
	private TextInput splitInput=new TextInput();
	private IntSpinner maxNum=new IntSpinner();
	private TextAreaInput area=new TextAreaInput();
	private RedisObjGroupNode redisObjNode;
	private RefreshNodeBean refreshBean;
	public RedisFilterHandler() {
		HBarPanel bar=new HBarPanel();
		bar.add(new LabelInput("键值过滤"));
		bar.add(filterInput,100);
		bar.add(new LabelInput("键值分割"));
		bar.add(splitInput,100);
		bar.add(new LabelInput("最多加载"));
		bar.add(maxNum,100);
		HButton btn=new HButton("应用") {
			@Override
			public void onClick() {
				apply();
			}
		};
		
		bar.add(btn);
		dlg.setToolBar(bar);
		LastPanel panel=new LastPanel();
		panel.setTitle("替他键值(逗号隔开)");
		panel.setWithScroll(area.getComp());
		dlg.setRootPanel(panel);
	}
	@Override
	public void resolve(CsFolderBean folderBean) throws Exception {
		redisObjNode=(RedisObjGroupNode)folderBean.getMrNode();
		filterInput.setValue(redisObjNode.getKeyPattern());
		splitInput.setValue(redisObjNode.getKeySpliter());
		maxNum.setValue(redisObjNode.getMaxKeyLoad());
		StringBuilder sb=new StringBuilder();
		SbUtil.append(sb, redisObjNode.getPreLoadKeys());
		area.setValue(sb.toString());
		refreshBean=new RefreshNodeBean(folderBean.getId());
		dlg.show();
	}
	
	private void apply() {
		try {
			redisObjNode.setKeyPattern(filterInput.getValue().trim());
			redisObjNode.setKeySpliter(splitInput.getValue().trim());
			redisObjNode.setMaxKeyLoad(maxNum.getInt());
			List<String> keys=new ArrayList<>();
			String v=area.getValue().trim();
			for(String k:StringUtils.split(v, ',')) {
				keys.remove(k);
				keys.add(k);
			}
			redisObjNode.setPreLoadKeys(keys);
			CsVarUtil.eng2.doCall(CsMgrEnum.TREE, refreshBean);
		} catch (Exception e) {
			GlobalWorkUtil.err(getClass(), e);
		}
	}

}
