package com.hh.hhdb_admin.mgr.login.comp;

import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonValue;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.FileOpenButton;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加数据库类型的表单
 * @author yangxianhui
 *
 */
class PkgAddDBTypeComp extends AbsHComp{
	private HPanel panel;
	private TextInput nameTextInput;
	private FileChooserInput logoTextInput;
	private TextInput classTextInput;
	private TextInput urlTextInput;
	private List<String> drivers;
	private HTable table;
	public PkgAddDBTypeComp(File iconDir,File libDir) {
		HDivLayout lay=new HDivLayout(GridSplitEnum.C2,GridSplitEnum.C10);
		lay.setTopHeight(20);
		lay.setBottomHeight(20);
		lay.setRightWidth(15);
		lay.setyGap(5);
		drivers = new ArrayList<String>();
		panel = new HPanel(lay);
		LabelInput nameInput = new LabelInput(LangMgr.lang(Word2.NAME)+"*:");
		nameTextInput = new TextInput();
		
		LabelInput logoInput = new LabelInput(LangMgr.lang(Word2.ICON)+":");
		logoTextInput = new FileChooserInput() {
			@Override
			protected void afterShowBox() {
				try {
				File f = getBtn().getSelectedFile();
				if(f!=null) {
					ImageIcon iicon = ImgUtil.readImgIcon(f);
					String extension = FilenameUtils.getExtension(f.getName());
					if(!extension.toLowerCase().equals("svg")&&(iicon.getIconWidth()!=16||iicon.getIconWidth()!=16)) {
						PopDlgUtil.error(getComp(), LangMgr.sLang(CsL.SIZETIP));
						return;
					}
					FileUtils.copyFileToDirectory(f, iconDir);
					textInput.setValue(f.getName());
				}
				}catch(Exception e) {
					Lg.error(getClass(), e);
				}
			}
		};
		logoTextInput.addFilter(LangMgr.lang(Word2.ICON), "jpg","png","svg");
		
		LabelInput classInput = new LabelInput("Class*:");
		classTextInput = new TextInput();
		
		LabelInput urlInput = new LabelInput("Url*:");
		urlTextInput = new TextInput();
		
		LabelInput driverInput = new LabelInput(LangMgr.lang(Word2.DRIVER)+"*:");
		
		panel.add(nameInput,nameTextInput);
		panel.add(classInput,classTextInput);
		panel.add(urlInput,urlTextInput);
		
		panel.add(logoInput,logoTextInput);
		
		table = new HTable();
		table.addCols(new DataCol("jar","JAR"));
		
		LastPanel last = new LastPanel();
		HBarPanel barPanel = new HBarPanel();
		FileOpenButton fob = new FileOpenButton(LangMgr.sLang(CsL.ADD_JAR)) {
			@Override
			protected void onClick() {
				super.onClick();
				List<File> fs = getSelectedFiles();
				if(fs!=null&&!fs.isEmpty()) {
					for(File f:fs) {
						try {
							FileUtils.copyFileToDirectory(f, libDir);
							if(!drivers.contains(f.getName())) {
								drivers.add(f.getName());
							}
						} catch (IOException e) {
							Lg.error(getClass(), e);
						}
					}
					
				}
				loadData();
			}
		};
		fob.setMultiSelectionEnabled(true);
		fob.addFilter("jar", "jar");
		barPanel.add(fob);
		
		barPanel.add(new HButton(LangMgr.sLang(CsL.REMOVE_JAR)) {
			@Override
			protected void onClick() {
				super.onClick();
				List<HTabRowBean> rows = table.getSelectedRowBeans();
				if(rows.isEmpty()) {
					PopDlgUtil.error(panel.getComp(), LangMgr.sLang(CsL.REMOVE_JAR_TIP));
					return;
				}
				if(rows!=null&&!rows.isEmpty()) {
					
					
					for(HTabRowBean f:rows) {
							String name = f.getOldRow().get("jar");
							File file = new File(libDir,name);
							if(file.exists()) {
								FileUtils.deleteQuietly(file);
							}
							drivers.remove(name);
					}
					
				}
				loadData();
			}
		});
		
		last.setHead(barPanel.getComp());
		last.setWithScroll(table.getComp());
		
		panel.add(driverInput,last);
		table.load(null, 1);
		comp = panel.getComp();
	}
	
	
	public void reset() {
		drivers.clear();
		urlTextInput.setValue("");
		nameTextInput.setValue("");
		classTextInput.setValue("");
		logoTextInput.setValue("");
		loadData();
	}
	
	public void loadDbTypeBean(DBTypeBean bean) {
		drivers.clear();
		for(JsonValue v:bean.getDriver()) {
			drivers.add(v.asString());
		}
		urlTextInput.setValue(bean.getUrl());
		nameTextInput.setValue(bean.getName());
		classTextInput.setValue(bean.getClazz());
		logoTextInput.setValue(bean.getLogo());
		loadData();
	}
	
	
	public DBTypeBean getDBtypeBean() {
		DBTypeBean bean = new DBTypeBean();
		bean.setUrl(urlTextInput.getValue());
		bean.setName(nameTextInput.getValue());
		bean.setClass(classTextInput.getValue());
		bean.setLogo(logoTextInput.getValue());
		JsonArray arr = new JsonArray();
		for(String str:drivers) {
			arr.add(str);
		}
		bean.setDriver(arr);
		return bean;
	}
	
	
	
	public HPanel getHComp(){
		return panel;
	}
	
	private void loadData() {
		List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
		for(String str:drivers) {
			Map<String,String> maps = new HashMap<String,String>();
			maps.put("jar", str);
			mapList.add(maps);
		}
		table.load(mapList, 1);
	}
}
