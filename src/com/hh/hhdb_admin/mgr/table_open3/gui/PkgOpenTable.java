package com.hh.hhdb_admin.mgr.table_open3.gui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.db_tabmg.util.HiddenColUtil;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonValue;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTipTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.SeqCol;
import com.hh.frame.swingui.view.tab.col.abs.AbsCol;
import com.hh.hhdb_admin.comp.lob.LobJsonCol;

class PkgOpenTable extends HTipTable implements ListSelectionListener {
	private OpenTableComp otc;
	private List<Map<String, String>> oldData;
	private PkgTabMenu menu;
	private TableModelListener mlistener;

	public PkgOpenTable(OpenTableComp openTableComp) {
		otc = openTableComp;

		setColumnSelectionAllowed(true);
		getComp().putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
		mlistener = new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent e) {
				otc.mBar.refresh();
			}
		};
		getComp().getColumnModel().getSelectionModel().addListSelectionListener(this);
		
		
		getComp().addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().trim().equalsIgnoreCase("tableCellEditor") && evt.getNewValue() == null) {
					otc.mBar.refresh();
				}
				
				if(evt.getPropertyName().trim().equalsIgnoreCase("model") ) {
					AbstractTableModel m = (AbstractTableModel)evt.getNewValue();
					m.removeTableModelListener(mlistener);
					m.addTableModelListener(mlistener);
				}
			}
		});

		if (otc.queryTask != null) {
			menu = new PkgTabMenu(otc) {
				@Override
				public void showPopup(MouseEvent e) {
					if (PkgOpenTable.this.isRowStyle()) {
						super.showPopup(e);
					}
				}
			};
			setRowPopMenu(menu);
		}

		addSelectAllAction();
		addPasteAction();
	}

	@Override
	protected void addDelKeyListener() {
		String deleteAction = "delete";
		InputMap inputMap = jTab.getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
		ActionMap actionMap = jTab.getActionMap();
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), deleteAction);
		actionMap.put(deleteAction, new AbstractAction() {
			private static final long serialVersionUID = -679276663210720663L;

			@Override
			public void actionPerformed(ActionEvent e) {
				otc.mBar.delRow();
			}
		});
	}

	public File getDataFile() {
		return otc.tmpDir;
	}

	@Override
	public void load(List<Map<String, String>> data, int start) {
		super.load(data, start);
		oldData = data;

	}

	@Override
	public void setNullSymbol(String nullSymbol) {
		super.setNullSymbol(nullSymbol);
		otc.nullSymbol = nullSymbol;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		int selectedColumn = getComp().getSelectedColumn();
		int selectedRow = getComp().getSelectedRow();
		getComp().changeSelection(selectedRow, selectedColumn, false, false);
		Component component = getComp().getEditorComponent();
		if (component instanceof JTextField) {
			component.requestFocus();
			((JTextField) component).selectAll();
		}
		otc.mBar.refresh();
	}

	/**
	 * 数据是否改动
	 * 
	 * @return
	 */
	public boolean isDataChanged() {
		if (getRowBeans(null) == null) {
			return false;
		}
		List<HTabRowBean> changedValue = getRowBeans(RowStatus.UPDATE);
		List<HTabRowBean> addValue = getRowBeans(RowStatus.ADD);
		List<HTabRowBean> delValue = getRowBeans(RowStatus.DEL);
		boolean change = changedValue != null && changedValue.size() > 0;
		change = isLobChange(changedValue, change);
		boolean add = addValue != null && addValue.size() > 0;
		boolean del = delValue != null && delValue.size() > 0;
		return isRowStyle() && (change || add || del);
	}

	public void reloadOldData() {
		if (oldData != null) {
			load(oldData, 1);
		}
	}

	private void addSelectAllAction() {
		// 添加表格ctrl+a全选事件
		ActionMap actionMap = getComp().getActionMap();
		actionMap.put("selectAll", new AbstractAction() {

			private static final long serialVersionUID = 6647591494008147634L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getComp().getSelectedRowCount() == getComp().getRowCount()) {
					getComp().clearSelection();
				} else {
					getComp().selectAll();
				}
				otc.mBar.refresh();
			}
		});
	}

	private void addPasteAction() {
		ActionMap actionMap =getComp().getActionMap();
		//添加表格Ctrl+v把剪切板中的数据粘贴到表格中
		InputMap inputMap =  getComp().getInputMap(javax.swing.JComponent.WHEN_FOCUSED);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "paste");
		actionMap.put("paste", new AbstractAction() {
			private static final long serialVersionUID = -1785153229578986169L;

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					String text = getStringFromClipboard();
					String[] textRows = text.split("\\r?\\n");
					if ((getComp().getSelectedRowCount() > 0 || getComp().getSelectedColumns().length > 0) && StringUtils.isNotBlank(text)) {
						List<HTabRowBean> beanList;
						if ( getComp().getSelectedRowCount() == 0) {	//点击的列标题名称,选择的此列所有行
							beanList = getRowBeans(null);
						} else {
							beanList = getSelectedRowBeans();
						}
						int i = 0;
						for (HTabRowBean rowBean : beanList) {
							if (rowBean.getStatus() != RowStatus.ADD) rowBean.setStatus(RowStatus.UPDATE);
							Map<String, String> map = null == rowBean.getCurrRow() ? new LinkedHashMap<>() : rowBean.getCurrRow();
							if(i>= textRows.length)
								break;
							String[] textCols = textRows[i].split("\\t", -1); //-1 means do not drop the last empty string
							int j = 0;
							for (int column : getComp().getSelectedColumns()) {
								for (AbsCol col : getColumns()) {
									//排除序列隐藏列和大字段列
									if (col.getValue().equals(getComp().getColumnName(column)) && 
											!SeqCol.name.equals(col.getName()) 
											&& !HiddenColUtil.isHiddenCol(otc.dbType,otc.ver, col.getName())
											&& !(col instanceof LobJsonCol)) {
										if(j>=textCols.length)
											break;
										map.put(col.getName(),textCols[j]);
										j++;
										break;
									}
								}
							}
							if (map.size() > 0) rowBean.setCurrRow(map);
							i++;
						}
						getComp().clearSelection();
						otc.mBar.refresh();
					}
				}catch (Exception e1){
					Lg.error(PkgOpenTable.class, e1);
					PopDlgUtil.error(e1);
				}
			
				
			}
			
		});
	}

	static boolean isLobChange(List<HTabRowBean> changedValue, boolean change) {
		int lobCount = 0;
		int changeCount = 0;
		if (change) {
			for (HTabRowBean hTabRowBean : changedValue) {
				Map<String, String> currRow = hTabRowBean.getCurrRow();
				Set<String> keySet = currRow.keySet();
				changeCount += keySet.size();
				for (String key : keySet) {
					String text = currRow.get(key);
					String oldText = hTabRowBean.getOldRow().get(key);
					boolean isLob = text != null && text.contains("\"__TEXT\":") && text.contains("\"file_path\":")
							&& oldText != null && oldText.contains("\"__TEXT\":");
					if (!isLob) {
						continue;
					}
					JsonValue newJsonValue = Json.parse(text);
					JsonValue oldJsonValue = Json.parse(oldText);
					boolean equals = newJsonValue.toPrettyString().equals(oldJsonValue.toPrettyString());
					if (equals) {
						lobCount++;
					}
				}
			}
			if (lobCount > 0 && changeCount == lobCount) {
				change = false;
			}
		}
		return change;
	}

	/**
	 * 获取剪切板内容
	 * 
	 * @return
	 */
	static String getStringFromClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		try {
			return (String) clipboard.getData(DataFlavor.stringFlavor);
		} catch (Exception ex) {
			return null;
		}
	}

}
