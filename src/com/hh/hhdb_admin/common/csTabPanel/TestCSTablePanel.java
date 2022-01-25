package com.hh.hhdb_admin.common.csTabPanel;

import java.awt.Color;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HScrollPane;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.tab.col.bar.BarCol;
import com.hh.frame.swingui.view.tab.col.bigtext.BigTextCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.tab.col.icon.IconCol;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.frame.swingui.view.tab.menu.sort.AllTypeSortPopMenu;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.PopPaneUtil;
public class TestCSTablePanel {
	private static HSplitPanel sp = new HSplitPanel(false);
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//初始化自定义UI
					HHSwingUi.newSkin();
//					HHSwingUi.init();
				} catch (Exception e) {
					e.printStackTrace();
				}
				HFrame hFrame = new HFrame();
				setDefaultSplit();
				hFrame.setRootPanel(sp);
				hFrame.setWindowTitle("分割面板");
				HBarPanel statusPanel=new HBarPanel();
				statusPanel.add(new LabelInput("状态栏"));
				statusPanel.setBorderColor(Color.green);
				hFrame.setStatusBar(statusPanel);
				hFrame.show();
				hFrame.maximize();
			}
		});
	}

	public static void setDefaultSplit() {		
		HDivLayout rightL1=new HDivLayout(GridSplitEnum.C6);
		sp.setPanelOne(genHPanel(rightL1));
		getRight2();
	}

	private static void getRight2() {
		CSTablePanel tp = new CSTablePanel();
		LastPanel lp1 = new LastPanel();
		lp1.setHead(new HButton("woshitou").getComp());
		LastPanel lp2 = new LastPanel();
		HTable tab = new HTable();
		
		/************************************************/
		DataCol nameCol1 = new DataCol("name1", "地址1");
		nameCol1.setShow(false);
		JsonCol jsonCol = new JsonCol("json", "文件") {
			@Override
			public JsonObject onClick(JsonObject json, int row, int column) {
				System.out.println(String.format("根据业务需求单元格[%d,%d]", row, column));
				return super.onClick(json, row, column);
			}
		};
		jsonCol.setSortPopMenu(null);
		HButton lineDataBtn = new HButton("行数据");
		final String toolbarName = "toolbar";
		BarCol toolBarCol = new BarCol(toolbarName, "按钮", lineDataBtn, new HButton("按钮2") {
			@Override
			protected void onClick() {
				PopPaneUtil.info("点击测试");
			}

		});
		toolBarCol.setSortPopMenu(null);
		toolBarCol.setWidth(160);
		List<String> values = new ArrayList<>();
		values.add("张三");
		values.add("李四");
		values.add("王五");
		ListCol listCol = new ListCol("clients", "客户", values);
		listCol.setSelectable(false);
		listCol.setWidth(80);
		DataCol idCol = new DataCol("id", "id");
		idCol.setHAlign(AlignEnum.RIGHT);
		idCol.setSortPopMenu(new AllTypeSortPopMenu());
		tab.addCols(idCol, new DataCol("empty", "空值"));
		IconCol iconCol = new IconCol("image", "图片");
		iconCol.putIcon("cpu", null);
		BigTextCol addCol = new BigTextCol("name", "地址");
		addCol.setHAlign(AlignEnum.CENTER);
		tab.addCols(iconCol, addCol, nameCol1, jsonCol, toolBarCol, new DataCol("age", "年纪"), new DataCol("sex", "性别"), new BoolCol("pk", "PKPKPK"), listCol);
		/********************************************************/
		
		tab.load(getData(1, 20), 1);
		lp2.setHead(new SearchToolBar(tab).getComp());
		lp2.setWithScroll(tab.getComp());
		lp1.set(lp2.getComp());
		
		JSplitPane jsp = sp.getComp();		
		tp.addPanel("111","test",lp1.getComp());
		HTextArea hta = new HTextArea(false, true);
		hta.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
		tp.addPanel("222","test", hta.getComp());
		tp.addPanel("333","test", new JTextArea("sdfffdddddddddddddddddd"));
        jsp.setRightComponent(tp.getComp());
		sp.setSplitWeight(0.3);
	}
	private static HScrollPane genHPanel(HDivLayout layout) {
		HButton btn1 = new HButton("你好1");
		TextInput input = new TextInput();
		input.setValue("你好2");
		HButton btn3 = new HButton("你好3");
		HScrollPane scroll = new HScrollPane();
		HPanel panel=new HPanel(layout);
		panel.add(btn1,input,btn3);
		for(int i=1;i<3;i++) {
			panel.add(new HButton("xtingbu"));
		}
		scroll.setPanel(panel);
		scroll.setBorderColor(Color.RED);
		return scroll;
	}
	
	static List<Map<String, String>> getData(int start, int end) {
		List<Map<String, String>> data = new ArrayList<>();
		for (int i = start; i <= end; i++) {
			Map<String, String> map = new HashMap<>();
			map.put("id", "id" + i);
			map.put("name", RandomUtil.getStr(getDict(), 10, 20));
			map.put("name1", RandomUtil.getStr(getDict(), 100, 200));
			map.put("json", getJsonStr(i));
			map.put("age", "年龄" + i);
			map.put("clients", "选择");
			if (i % 2 == 0) {
				map.put("pk", "true");
				map.put("sex", "true");
				map.put("image", "cpu");
			} else if (i % 3 == 0) {
				map.put("pk", "false");
				map.put("sex", "false");
			} else {
				map.put("sex", null);
			}
			data.add(map);
		}
		return data;
	}
	private static char[] getDict() {
		return "qwertyuiopasdfghjklzxcvbnm1234567890方法开始就调用编程十万个怎么办".toCharArray();
	}

	private static String getJsonStr(int count) {
		if (count % 2 == 0) {
			return null;
		}
		JsonObject jobj = new JsonObject();
		jobj.add("name", "name" + count);
		jobj.add("value", "值" + count);
		jobj.add(JsonCol.__TEXT, "BLOB");
		return jobj.toPrettyString();
	}

}
