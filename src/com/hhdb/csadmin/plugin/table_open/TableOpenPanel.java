package com.hhdb.csadmin.plugin.table_open;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumn;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.log.LM;
import com.hh.frame.dbobj.hhdb.HHdbColumn;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hhdb.csadmin.common.util.StartUtil;
import com.hhdb.csadmin.plugin.table_open.service.SqlOperationService;
import com.hhdb.csadmin.plugin.table_open.ui.ButtonPanelEditorRenderer;
import com.hhdb.csadmin.plugin.table_open.ui.HHOperateTablePanel;
import com.hhdb.csadmin.plugin.table_open.ui.HHPagePanel;
import com.hhdb.csadmin.plugin.table_open.ui.HHTableColumnCellRenderer;

/**
 * 打开表操作面板
 */
public class TableOpenPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public String databaseName; // 数据库名
	public String schemaName; // 模式名
	public String table; // 表名
	public SqlOperationService sqls;
	public int j = 0; // 面板id

	private TableOpen tabo;

	private TablePanelUtil tablePanel;
	private HHOperateTablePanel hhOpTbPanel;
	private HHPagePanel hhPgPanel;
	// 每页显示的数据条数
	public final int inNum = 30;
	// 模式+表名称
	private String tableName;
	// 查询表第N页数据SQL
	private String nextPageData = "select *,true upd,ctid from %s limit "
			+ inNum + " offset %d*" + inNum;
	// 当前页码数
	private int pageNum;
	// 表数据修改前的集合
	private List<TemporaryDate> oldValueLists = new ArrayList<>();
	// 表数据修改后的集合
	private List<TemporaryDate> newValueLists = new ArrayList<>();
	// 选中表格数据的当前所在行数
	private int row = 0;
	// 选中表格数据的当前所在列数
	private int column = 0;

	public TableOpenPanel(String databaseName, String schemaName, String table,String tableName, TableOpen tableOpen) {
		try {
			this.databaseName = databaseName;
			this.schemaName = schemaName;
			this.table = table;
			this.tabo = tableOpen;
			this.tableName = tableName;
			sqls = new SqlOperationService(tabo);
			initPanel();
			List<List<Object>> firstPageTable;
			firstPageTable = pageTable(nextPageData, 0);
			excutepage(firstPageTable);
			showPageCount();
			buttonAction();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, "错误信息：" + e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	// 初始化面板
	public void initPanel() {
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createLineBorder(new Color(178, 178, 178),1));
		tablePanel = new TablePanelUtil(new int[] { 0 });
		tablePanel.drawAllowed(false);
		tablePanel.setRowSorter(true);
		tablePanel.nterlacedDiscoloration(true,null,null); 
		tablePanel.highlight(true,false,null);
		tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		tablePanel.getBaseTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  //表不拉伸
		
		hhOpTbPanel = new HHOperateTablePanel();
		//禁用部分按钮
		hhOpTbPanel.getSaveData().setEnabled(false);
		hhOpTbPanel.getDelRow().setEnabled(false);
		hhPgPanel = new HHPagePanel();
		add(tablePanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(hhOpTbPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,10, 0, 0), 0, 0));
		add(hhPgPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0, 0, 30), 0, 0));
	}

	// 表格填充数据
	public void excutepage(List<List<Object>> pageTable) {
		//表头数据
		Vector<Object> colname = new Vector<Object>();
		colname.add("");
		for (Object field : pageTable.get(0)) {
			colname.add(field);
		}
		tablePanel.addLineNumber(pageTable);
		
		//判断哪些列需要加入流操作按钮
		ButtonPanelEditorRenderer er = new ButtonPanelEditorRenderer(this);
		Connection connection = null;
		HHdbColumn  hhdb = null;
		for (Object name : colname) {
			if(!"".equals(name)){
				if(name.toString().equals("upd")){
					break;
				}
				try {
					connection = sqls.getConn();
					hhdb  = new HHdbColumn(connection, schemaName, table, name+"", true,StartUtil.prefix);
					String type = hhdb.getColType();
					if(null != type && type.equals("bytea")){
						tablePanel.getBaseTable().getColumn(name).setCellRenderer(er);
						tablePanel.getBaseTable().getColumn(name).setCellEditor(er);
					}
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
				}
			}
		}
		
		//设置行号列样式
		TableColumn index = tablePanel.getBaseTable().getColumnModel().getColumn(0);
		index.setMaxWidth(25);
		index.setMinWidth(25);
		index.setCellRenderer(new HHTableColumnCellRenderer());
		//隐藏后面两列 upd ctid
		TableColumn coln = tablePanel.getBaseTable().getColumnModel().getColumn(colname.size() - 1);
		coln.setMinWidth(0);
		coln.setMaxWidth(0);
		coln.setWidth(0);
		coln.setPreferredWidth(0);
		TableColumn coln1 = tablePanel.getBaseTable().getColumnModel().getColumn(colname.size() - 2);
		coln1.setMinWidth(0);
		coln1.setMaxWidth(0);
		coln1.setWidth(0);
		coln1.setPreferredWidth(0);

		//点击行时
		tablePanel.getBaseTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				judge();
			}
		});
		//头部点击事件
		tablePanel.getBaseTable().getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//禁用部分按钮
				hhOpTbPanel.getSaveData().setEnabled(false);
				hhOpTbPanel.getDelRow().setEnabled(false);
			}
		});
	}

	/**
	 * 发送sql请求数据
	 */
	private List<List<Object>> pageTable(String pageData, int num) throws Exception {
		List<List<Object>> pageTb = new ArrayList<>();
		String pageDatas;
		if (num == 0) {
			pageDatas = String.format(pageData, tableName, 0);
		} else {
			pageDatas = String.format(pageData, tableName, num);
		}
		pageTb = sqls.getListList(pageDatas);
		return pageTb;
	}

	private void showPageCount() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 先获取表总行数
					String totalRowsSql = "select count(1) from	" + tableName;
					List<Object> rowStrList = sqls.getListList(
							totalRowsSql).get(1);
					int rowCount = Integer.parseInt(rowStrList.get(0) + "");
					int intCount = rowCount / inNum;
					int modCount = rowCount % inNum;
					// 总页码数
					int pageCount;
					if (modCount != 0) {
						pageCount = intCount + 1;
					} else {
						pageCount = intCount;
					}
					hhPgPanel.setPageCount(pageCount);
					hhPgPanel.getTotalPageNum().setText(pageCount + "");
					if (pageCount <= 1) {
						hhPgPanel.getNextPage().setEnabled(false);
						hhPgPanel.getLastPage().setEnabled(false);
					}
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
					JOptionPane.showMessageDialog(null, "错误信息：" + e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
				}
			}
		}).start();
	}

	/**
	 * 刷新
	 */
	public void refresh() {
		try {
			String curPageNum = hhPgPanel.getCurPageNum().getText();
			int cPNum = pageNum;
			if (!"".equals(curPageNum)) {
				cPNum = Integer.parseInt(curPageNum);
			}
			hhPgPanel.getCurPageNum().setText(cPNum + "");
			List<List<Object>> nextPageTable = pageTable(nextPageData, cPNum - 1);
			excutepage(nextPageTable);
			oldValueLists.clear();
			newValueLists.clear();
			row = 0;
			column = 0;
			//禁用部分按钮
			hhOpTbPanel.getSaveData().setEnabled(false);
			hhOpTbPanel.getDelRow().setEnabled(false);
			tablePanel.initializationColor();
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, "错误信息：" + e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
		}
		
	}

	/**
	 * 面板上的按钮点击事件
	 */
	private void buttonAction() {
		// 点击第一页按钮回到第一页
		hhPgPanel.getFirstPage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<List<Object>> firstPageTable = new ArrayList<>();
				try {
					firstPageTable = pageTable(nextPageData, 0);
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, "错误信息：" + e1.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
				}
				excutepage(firstPageTable);
				hhPgPanel.getCurPageNum().setText("1");
				hhPgPanel.getFirstPage().setEnabled(false);
				hhPgPanel.getPrePage().setEnabled(false);
				if (hhPgPanel.getPageCount() > 1) {
					hhPgPanel.getNextPage().setEnabled(true);
					hhPgPanel.getLastPage().setEnabled(true);
				}
			}
		});
		// 点击上一页按钮回到前一页
		hhPgPanel.getPrePage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String curPageNum = hhPgPanel.getCurPageNum().getText();
				int cPNum = pageNum;
				if (!"".equals(curPageNum)) {
					cPNum = Integer.parseInt(curPageNum);
				}
				List<List<Object>> prePageTable = new ArrayList<>();
				try {
					prePageTable = pageTable(nextPageData,cPNum - 2);
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, "错误信息：" + e1.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
				}
				excutepage(prePageTable);
				cPNum--;
				hhPgPanel.getCurPageNum().setText(cPNum + "");
				if (cPNum <= 1) {
					hhPgPanel.getFirstPage().setEnabled(false);
					hhPgPanel.getPrePage().setEnabled(false);
				}
				if (hhPgPanel.getPageCount() > 1) {
					hhPgPanel.getNextPage().setEnabled(true);
					hhPgPanel.getLastPage().setEnabled(true);
				}
			}
		});
		// 保存当前页码
		hhPgPanel.getCurPageNum().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String text = hhPgPanel.getCurPageNum().getText();
				if (text != null && !"".equals(text) && !"0".equals("")) {
					pageNum = Integer.parseInt(hhPgPanel.getCurPageNum().getText());
				}
			}

		});
		// 输入页码跳到当前输入页
		hhPgPanel.getCurPageNum().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String curPageNum = hhPgPanel.getCurPageNum().getText();
						if ("".equals(curPageNum)) {
							return;
						}
						// 判断每个字符是不是数字
						char[] ch = curPageNum.toCharArray();
						for (char c : ch) {
							if (!Character.isDigit(c)) {
								JOptionPane.showMessageDialog(null, "请输入正确的页码");
								hhPgPanel.getCurPageNum().setText(pageNum + "");
								return;
							}
						}

						// 判断输入的页数是不是大于总页数
						int cPNum = Integer.parseInt(curPageNum);
						if (cPNum > hhPgPanel.getPageCount() || cPNum <= 0) {
							JOptionPane.showMessageDialog(null, "请输入正确的页码");
							hhPgPanel.getCurPageNum().setText(pageNum + "");
							return;
						}
						List<List<Object>> nextPageTable = new ArrayList<>();
						try {
							nextPageTable = pageTable(nextPageData, cPNum - 1);
						} catch (Exception e1) {
							LM.error(LM.Model.CS.name(), e1);
							JOptionPane.showMessageDialog(null, "错误信息：" + e1.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
						}
						excutepage(nextPageTable);
						if (cPNum <= 1) {
							hhPgPanel.getFirstPage().setEnabled(false);
							hhPgPanel.getPrePage().setEnabled(false);
						} else {
							hhPgPanel.getFirstPage().setEnabled(true);
							hhPgPanel.getPrePage().setEnabled(true);
						}
						if (cPNum >= hhPgPanel.getPageCount()) {
							hhPgPanel.getNextPage().setEnabled(false);
							hhPgPanel.getLastPage().setEnabled(false);
						} else {
							hhPgPanel.getNextPage().setEnabled(true);
							hhPgPanel.getLastPage().setEnabled(true);
						}
					}
				});
			}
		});
		// 点击下一页按钮回到下一页
		hhPgPanel.getNextPage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String curPageNum = hhPgPanel.getCurPageNum().getText();
				int cPNum = pageNum;
				if (!"".equals(curPageNum)) {
					cPNum = Integer.parseInt(curPageNum);
				}
				List<List<Object>> nextPageTable = new ArrayList<>();
				try {
					nextPageTable = pageTable(nextPageData,cPNum);
				} catch (Exception e1) {
					LM.error(LM.Model.CS.name(), e1);
					JOptionPane.showMessageDialog(null, "错误信息：" + e1.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
				}
				excutepage(nextPageTable);
				cPNum++;
				hhPgPanel.getCurPageNum().setText(cPNum + "");
				if (cPNum > 1) {
					hhPgPanel.getFirstPage().setEnabled(true);
					hhPgPanel.getPrePage().setEnabled(true);
				}
				if (cPNum >= hhPgPanel.getPageCount()) {
					hhPgPanel.getNextPage().setEnabled(false);
					hhPgPanel.getLastPage().setEnabled(false);
				}
			}
		});
		// 点击最后一页按钮回到最后一页
		hhPgPanel.getLastPage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int tPNum = hhPgPanel.getPageCount();
						if (tPNum <= 1) {
							return;
						}
						List<List<Object>> lastPageTable = new ArrayList<>();
						try {
							lastPageTable = pageTable(nextPageData, tPNum - 1);
						} catch (Exception e1) {
							LM.error(LM.Model.CS.name(), e1);
							JOptionPane.showMessageDialog(null, "错误信息：" + e1.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
						}
						excutepage(lastPageTable);
						hhPgPanel.getCurPageNum().setText(tPNum + "");
						if (tPNum > 1) {
							hhPgPanel.getFirstPage().setEnabled(true);
							hhPgPanel.getPrePage().setEnabled(true);
						}
						hhPgPanel.getNextPage().setEnabled(false);
						hhPgPanel.getLastPage().setEnabled(false);
					}
				});
			}
		});
		// 点击添加按钮添加一个空行
		hhOpTbPanel.getAddRow().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] object = new Object[tablePanel.getBaseTable().getColumnCount()];
				tablePanel.getTableDataModel().addRow(object);
			}
		});
		// 点击删除按钮删除所选行07 12
		hhOpTbPanel.getDelRow().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final int[] selectedRows = tablePanel.getBaseTable().getSelectedRows();
				if(null != selectedRows && selectedRows.length != 0){    //判断是否选择了行
					int option = JOptionPane.showConfirmDialog(tablePanel,"是否删除已选择行", "提示信息", JOptionPane.YES_NO_OPTION);
					if (option == JOptionPane.YES_OPTION) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								String delSql = "delete from " + tableName+ " where ctid='%s'";
								int totalColumnCount = tablePanel.getBaseTable().getColumnCount();
								for (int i = 0; i < selectedRows.length; ++i) {
									String ctid = null == tablePanel.getBaseTable().getValueAt(selectedRows[i],totalColumnCount - 1) ? null : tablePanel.getBaseTable().getValueAt(selectedRows[i],totalColumnCount - 1).toString();
									if(null != ctid && !ctid.equals("")){   //删除数据库的数据
										String upd = null == tablePanel.getBaseTable().getValueAt(selectedRows[i],totalColumnCount - 2) ? null : tablePanel.getBaseTable().getValueAt(selectedRows[i],totalColumnCount - 2).toString();
										if (StringUtils.isNoneBlank(upd)&& ("t".equalsIgnoreCase(upd) || "true".equalsIgnoreCase(upd))) {
											String message = sqls.sendConn4Update(String.format(delSql, ctid));
											if (StringUtils.isBlank(message)) {
												return;
											}
										}
									}else{   					//删除临时数据
										tablePanel.getTableDataModel().removeRow(selectedRows[i]);
										//删除临时数据
										Iterator<TemporaryDate> newtem = newValueLists.iterator();
										while (newtem.hasNext()) {
											TemporaryDate temporaryDate =  newtem.next();
											if(temporaryDate.getRow() == selectedRows[i]){
												newtem.remove();
											}
										}
										Iterator<TemporaryDate> oidtem = oldValueLists.iterator();
										while (oidtem.hasNext()) {
											TemporaryDate temporaryDate = oidtem.next();
											if(temporaryDate.getRow() == selectedRows[i]){
												oidtem.remove();
											}
										}
										//从新排序
										for (TemporaryDate temporaryDate : newValueLists) {
											if( temporaryDate.getRow() > selectedRows[i] ){
												temporaryDate.setRow(temporaryDate.getRow()-1);
											}
										}
										for (TemporaryDate temporaryDate : oldValueLists) {
											if( temporaryDate.getRow() > selectedRows[i] ){
												temporaryDate.setRow(temporaryDate.getRow()-1);
											}
										}
										row = 0;
										column = 0;
										tablePanel.initializationColor();   //重置隔行变色
										judge();
										return;
									}
								}
								refresh();
							}
						});
					}
				}
			}
		});
		// 点击刷新按钮刷新当前页面
		hhOpTbPanel.getRefreshData().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		// 点击保存按钮保存修改
		hhOpTbPanel.getSaveData().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						try {
							//当在编辑某单元格时，点击保存则直接终止编辑获取当前编辑格已输入的值进行保存
							if (tablePanel.getBaseTable().isEditing()) {
								tablePanel.getBaseTable().getCellEditor().stopCellEditing();
								getEditedValues();
							}
							if(oldValueLists.size() == 0 ){ 
								return;
							}
							int totalColumnCount = tablePanel.getBaseTable().getColumnCount();
							String updateSql = "update " + tableName+ " set %s where ctid='%s'";
							String insertSql = "insert into " + tableName+ " (%s) values (%s)";
							List<TemporaryDate> newRowList = new ArrayList<>();
							List<Integer> rowNumList = new ArrayList<>();
							for (TemporaryDate temporaryDate : newValueLists) {
								String column = tablePanel.getBaseTable().getColumnName(temporaryDate.getColumn());
								Object ob = tablePanel.getBaseTable().getValueAt(temporaryDate.getRow(),totalColumnCount - 2);
								String upd = null == ob ? null : ob.toString();
								if (StringUtils.isNoneBlank(upd)&& ("t".equalsIgnoreCase(upd) || "true".equalsIgnoreCase(upd))) {
									// 修改sql    
									String ctid = tablePanel.getTableDataModel().getValueAt(temporaryDate.getRow(),totalColumnCount - 1).toString();
									String message = sqls.sendConn4Update(String.format(updateSql,column+ "="+ "'"+ StringUtils.replace(temporaryDate.getValue()+"","'","''") + "'", ctid));
									if (StringUtils.isBlank(message)) {
										return;
									}
								} else {
									// 记录新增的行
									newRowList.add(temporaryDate);
									boolean flag = false;
									for (int rowNum : rowNumList) {
										if (rowNum == temporaryDate.getRow()) {
											flag = true;
											break;
										}
									}
									if (!flag) {
										rowNumList.add(temporaryDate.getRow());
									}
								}
							}
							//拼接添加sql
							for (int rowNum : rowNumList) {
								StringBuffer columnBuffer = new StringBuffer();
								StringBuffer valueBuffer = new StringBuffer();
								for (TemporaryDate temporaryDate : newRowList) {
									if (rowNum == temporaryDate.getRow()) {
										String column = tablePanel.getBaseTable().getColumnName(temporaryDate.getColumn());
										columnBuffer.append(column);
										String value = temporaryDate.getValue()+"";
										if (value.contains("'")) {
											value = value.replaceAll("'", "''");
										}
										valueBuffer.append("'" + value + "'");
										columnBuffer.append(",");
										valueBuffer.append(",");
									}
								}
								String columnStr = columnBuffer.toString();
								String valueStr = valueBuffer.toString();
								columnStr = columnStr.substring(0,columnStr.length() - 1);
								valueStr = valueStr.substring(0,valueStr.length() - 1);
								String message = sqls.sendConn4Update(String.format(insertSql, columnStr, valueStr));
								if (StringUtils.isBlank(message)) {
									return;
								}
							}
							oldValueLists.clear();
							newValueLists.clear();
							JOptionPane.showMessageDialog(null, "保存成功！", "提示",JOptionPane.INFORMATION_MESSAGE);
							refresh();
						} catch (Exception e2) {
							LM.error(LM.Model.CS.name(), e2);
							JOptionPane.showMessageDialog(null, "错误信息：" + e2.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
						}
					}
				});
			}
		});

		// 监听表格数据变更
		tablePanel.getBaseTable().addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("tableCellEditor".equalsIgnoreCase(evt.getPropertyName().trim())) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (tablePanel.getBaseTable().isEditing()) {   //保存修改前的数据到数组
								row = tablePanel.getBaseTable().convertRowIndexToModel(tablePanel.getBaseTable().getEditingRow());
								column = tablePanel.getBaseTable().convertColumnIndexToModel(tablePanel.getBaseTable().getEditingColumn());
								if (row != -1 && column != -1) {
									Object oldValue = tablePanel.getBaseTable().getModel().getValueAt(row, column);
									if (oldValue == null) {
										oldValue = "";
									}
									boolean flag = false;
									for (TemporaryDate temporaryDate : oldValueLists) { 
										if ( row == temporaryDate.getRow() && column == temporaryDate.getColumn() ) {
											flag = true;
											break;
										}
									}
									TemporaryDate temporaryDate = new TemporaryDate();
									if (!flag) {
										temporaryDate.setRow(row);
										temporaryDate.setColumn(column);
										temporaryDate.setValue(oldValue);
										oldValueLists.add(temporaryDate);
									}
								}
							} else {      	//保存修改后的数据到数组
								getEditedValues();
							}
							judge();
						}
					});
				}
			}
		});
	}
	
	/**
	 * 获取单元格编辑后的值
	 */
	private void getEditedValues(){
		Object newValue = tablePanel.getBaseTable().getModel().getValueAt(row, column);
		if (newValue == null) {
			newValue = "";
		}
		Iterator<TemporaryDate> oldValueIter = oldValueLists.iterator();
		while (oldValueIter.hasNext()) {
			TemporaryDate oldValues = oldValueIter.next();
			if ( row == oldValues.getRow() && column == oldValues.getColumn() ) {
				Iterator<TemporaryDate>  list  = newValueLists.iterator();
				while (list.hasNext()) {
					TemporaryDate temporaryDate = list.next();
					if( row == temporaryDate.getRow() && column == temporaryDate.getColumn() ){
						list.remove();
					}
				}
				if (!newValue.toString().equals(oldValues.getValue().toString())) {
					TemporaryDate tem = new TemporaryDate();
					tem.setRow(row);
					tem.setColumn(column);
					tem.setValue(newValue);
					newValueLists.add(tem);
				} else {
					oldValueIter.remove();
				}
				break;
			}
		}
	}
	
	/**
	 * 判断数据是否有变化，禁用按钮
	 * @return
	 */
	private void judge() {
		for (TemporaryDate temporaryDate : newValueLists) {
			for (TemporaryDate temporaryDate2 : oldValueLists) {
				if ( temporaryDate.getRow() == temporaryDate2.getRow() && temporaryDate.getColumn() == temporaryDate2.getColumn() ) {
					if(!temporaryDate.getValue().toString().equals(temporaryDate2.getValue().toString())){
						hhOpTbPanel.getSaveData().setEnabled(true);
						hhOpTbPanel.getDelRow().setEnabled(true);
						return;
					}
				}
			}
		}
		hhOpTbPanel.getSaveData().setEnabled(false);
		hhOpTbPanel.getDelRow().setEnabled(true);
	}
	
	/**
	 * 记录表格数据变化的内部类 
	 * @author hhxd
	 */
	public class TemporaryDate {
		Integer row;   		//行
		Integer column;		//列
		Object  value;		//值
		
		public Integer getRow() {
			return row;
		}
		public void setRow(Integer row) {
			this.row = row;
		}
		public Integer getColumn() {
			return column;
		}
		public void setColumn(Integer column) {
			this.column = column;
		}
		public Object getValue() {
			return value;
		}
		public void setValue(Object value) {
			this.value = value;
		}
	}
	
}
