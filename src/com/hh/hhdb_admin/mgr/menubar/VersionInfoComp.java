package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.common.base.BaseProduct;
import com.hh.frame.sun.DefaultTableCellRenderer;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CSProduct;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ouyangxu
 * @date 2021-04-25 16:02:52
 * @description 产品版本信息
 */
public class VersionInfoComp {
	private static final String ATTRIBUTE = "ATTRIBUTE";
	private static final String VALUE = "value";
	private static final String PRODUCT_NAME = "productName";
	private static final String VERSION = "version";
	private static final String COMMIT_VERSION = "commit_version";
	private static final String COMMIT_DATE = "commit_date";
	private static final String SP = "-->>";
	private final Color selectCol = new Color(211, 234, 255, 235);
	private final HDialog dialog;

	public VersionInfoComp() {
		dialog = new HDialog(StartUtil.parentFrame, HFrame.LARGE_WIDTH);
		File verFile = new File(StartUtil.getEtcFile(), CSProduct.VERSION_FILE_NAME);
		HDivLayout divLayout = new HDivLayout();
		divLayout.setyGap(20);
		HPanel basePanel = new HPanel(divLayout);
		if (verFile.exists()) {
			try {
				BaseProduct baseProduct = CSProduct.readProductJson(verFile);
				HTable table = buildTable();
				HPanel productPanel = buildProductPanel(null, baseProduct, table);
				LastPanel lastPanel = new LastPanel();
				lastPanel.setWithScroll(productPanel.getComp());
				basePanel.setLastPanel(lastPanel);
				List<Map<String, String>> verMaps = getValuesMaps(baseProduct);
				table.load(verMaps, 1);
			} catch (IOException e) {
				PopPaneUtil.error(StartUtil.parentFrame.getWindow(), MenubarComp.getLang("versionError"));
				return;
			}
		} else {
			PopPaneUtil.error(StartUtil.parentFrame.getWindow(), MenubarComp.getLang("versionNotExit"));
			return;
		}
		dialog.setRootPanel(basePanel);
		dialog.setIconImage(IconFileUtil.getLogo().getImage());
		dialog.getWindow().setLocationRelativeTo(null);
		dialog.setWindowTitle(MenubarComp.getLang(VERSION));
		dialog.show();
	}


	/**
	 * 递归创建打包详细信息的panel
	 *
	 * @param baseName    名称
	 * @param baseProduct Product
	 * @param table       表格
	 * @return panel
	 */
	private HPanel buildProductPanel(String baseName, BaseProduct baseProduct, HTable table) {
		HDivLayout rootDivLayout = new HDivLayout();
		HDivLayout divLayout = new HDivLayout();
		rootDivLayout.setyGap(5);
		divLayout.setLeftWidth(20);
		HPanel rootPanel = new HPanel(rootDivLayout);
		HPanel tablePanel = new HPanel(divLayout);
		HBarPanel btnPanel = new HBarPanel();
		HImgButton copBtn = getCopyButton(baseProduct);

		btnPanel.add(copBtn);
		tablePanel.add(table);
		rootPanel.add(btnPanel, tablePanel);
		baseName = baseName == null ? baseProduct.getName(StartUtil.default_language) : baseName + SP + baseProduct.getName(StartUtil.default_language);
		rootPanel.setTitle(baseName);
		List<BaseProduct> depProducts = baseProduct.getDepProducts();
		if (depProducts != null && depProducts.size() > 0) {
			for (BaseProduct depProduct : depProducts) {
				HTable hTable = buildTable();
				List<Map<String, String>> valuesMaps = getValuesMaps(depProduct);
				HPanel buildProductPanel = buildProductPanel(baseName, depProduct, hTable);
				rootPanel.add(buildProductPanel);
				hTable.load(valuesMaps, 1);
			}
		}
		return rootPanel;
	}

	/**
	 * 复制按钮
	 * @param baseProduct 基础信息
	 * @return 按钮
	 */
	private HImgButton getCopyButton(BaseProduct baseProduct) {
		HImgButton copBtn = new HImgButton();
		ImageIcon copy = MenubarComp.getIcon("copy");
		copBtn.setMouseExitedIconIcon(copy);
		copBtn.setMouseEnteredIcon(copy);
		copBtn.getComp().setToolTipText(MenubarComp.getLang("copyInfo"));
		copBtn.addActionListener(e -> {
			String s = buildCopyStr(baseProduct);
			ClipboardUtil.putText(s);
			PopPaneUtil.info(dialog.getWindow(), MenubarComp.getLang("copySuccess"));
		});

		return copBtn;
	}

	/**
	 * 递归复制信息
	 * @param baseProduct 基础信息
	 * @return 按钮
	 */
	private String buildCopyStr(BaseProduct baseProduct) {
		List<Map<String, String>> valuesMaps = getValuesMaps(baseProduct);
		StringBuilder str = new StringBuilder();
		if (valuesMaps.size() > 0) {
			for (Map<String, String> valuesMap : valuesMaps) {
				str.append(valuesMap.get(ATTRIBUTE)).append(":").append(valuesMap.get("value")).append("\n");
			}
		}
		List<BaseProduct> depProducts = baseProduct.getDepProducts();
		if (depProducts != null && depProducts.size() > 0) {
			str.append("\n");
			for (BaseProduct depProduct : depProducts) {
				str.append(buildCopyStr(depProduct));
			}
		}
		if (str.length() > 0) {
			str.deleteCharAt(str.length() - 1);
		}
		return str.toString();
	}

	private HTable buildTable() {
		HTable table = new HTable();
		DataCol name = new DataCol(ATTRIBUTE, MenubarComp.getLang(ATTRIBUTE));
		name.setWidth(220);
		table.addCols(name, new DataCol(VALUE, MenubarComp.getLang(VALUE)));
		table.hideSeqCol();
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setBorder(BorderFactory.createLineBorder(HHSwingUi.tabEvenColor));
		table.getComp().getTableHeader().setDefaultRenderer(renderer);
		table.setEvenBgColor(table.getOddBgColor());
		table.getComp().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getComp().setSelectionBackground(selectCol);
		table.getComp().setSelectionForeground(Color.black);
		table.getComp().setShowGrid(false);
		return table;
	}

	private List<Map<String, String>> getValuesMaps(BaseProduct baseProduct) {
		List<Map<String, String>> verMaps = new ArrayList<>();
		Map<String, String> nameValueMap = new LinkedHashMap<>();
		nameValueMap.put(PRODUCT_NAME, baseProduct.getName(StartUtil.default_language));
		nameValueMap.put(VERSION, baseProduct.getVersion());
		nameValueMap.put(COMMIT_VERSION, baseProduct.getCommitGitTag());
		nameValueMap.put(COMMIT_DATE, baseProduct.getCommitDate());
		nameValueMap.forEach((k, v) -> {
			Map<String, String> verMap = new LinkedHashMap<>();
			verMap.put(ATTRIBUTE, MenubarComp.getLang(k));
			verMap.put(VALUE, v);
			verMaps.add(verMap);
		});
		return verMaps;
	}

}
