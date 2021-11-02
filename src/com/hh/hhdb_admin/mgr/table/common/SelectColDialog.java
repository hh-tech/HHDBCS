package com.hh.hhdb_admin.mgr.table.common;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.HTipTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author oyx
 * @date 2020-10-14  0014 10:39:09
 */
public class SelectColDialog {
	private final Set<String> colNames;
	private final HDialog dialog;
	private Set<String> selectCol;
	private HTable table;
	private JsonObject resJson;
	private String tips;
	public final AtomicInteger atomicId = new AtomicInteger();
	protected final static String COL_ID = "id";
	protected final static String COL_NAME = "name";
	protected final static String COL_SELECT = "select";


	public SelectColDialog(Set<String> colNames, String title, String selCol) {
		this.colNames = colNames;
		initTable();

		JScrollPane scrollPane = new JScrollPane(table.getComp());
		LastPanel lastPanel = new LastPanel(false);
		lastPanel.set(scrollPane);
		selectCol = new LinkedHashSet<>();
		dialog = new HDialog(TableComp.dialog == null ? StartUtil.getMainDialog() : TableComp.dialog,500, 400,true) {
			@Override
			protected void onConfirm() {
				TableModel model =  table.getComp().getModel();
				for (int i = 0; i < model.getRowCount(); i++) {
					Object valueAt = model.getValueAt(i, 1);
					if (Boolean.parseBoolean(valueAt.toString())) {
						selectCol.add((String) model.getValueAt(i, 0));
					}
				}
			}

			@Override
			protected void onCancel() {
				if (selCol != null) {
					selectCol.addAll(Arrays.stream(selCol.split(",")).collect(Collectors.toCollection(LinkedHashSet::new)));
				}
			}
		};

		dialog.setWindowTitle(title);
		dialog.setOption();
		dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
		HPanel panel =new HPanel();
		panel.setLastPanel(lastPanel);
		dialog.setRootPanel(panel);
	}

	private void initTable() {
		table = new HTipTable();
		DataCol colCol = new DataCol(COL_NAME, TableComp.getLang("columnName"));
		BoolCol selCol = new BoolCol(COL_SELECT, TableComp.getLang("select"));
		selCol.setWidth(80);
		table.addCols(colCol,selCol);
		table.setRowHeight(30);
		table.hideSeqCol();
		table.setEvenBgColor(table.getOddBgColor());
	}

	/**
	 * 加载数据
	 */
	public void loadTable(String selectNames) {
		if (colNames == null || colNames.size() <= 0) {
			PopPaneUtil.info(tips);
			return;
		}
		List<Map<String, String>> data = new ArrayList<>();
		Set<String> selectNameSet = new HashSet<>();
		if (StringUtils.isNoneBlank(selectNames)) {
			selectNameSet = Arrays.stream(selectNames.split(",")).collect(Collectors.toSet());
		}
		for (String colName : colNames) {
			Map<String, String> map = new HashMap<>();
			map.put(COL_ID, String.valueOf(atomicId.incrementAndGet()));
			map.put(COL_NAME, colName);
			map.put(COL_SELECT, String.valueOf(selectNameSet.contains(colName)));
			data.add(map);
		}
		table.load(data, 0);
		//setAlwaysOnTop至于窗口最顶部
		dialog.getWindow().setAlwaysOnTop(dialog.getWindow().isAlwaysOnTopSupported());
		dialog.show();
	}


	public HTable getTable() {
		return table;
	}

	public void setTable(HTable table) {
		this.table = table;
	}

	public Set<String> getSelectCol() {
		return selectCol;
	}

	public void setSelectCol(Set<String> selectCol) {
		this.selectCol = selectCol;
	}

	public JsonObject getResJson() {
		return resJson;
	}

	public void setResJson(JsonObject resJson) {
		this.resJson = resJson;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}
}
