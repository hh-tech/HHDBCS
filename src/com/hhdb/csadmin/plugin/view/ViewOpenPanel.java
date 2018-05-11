package com.hhdb.csadmin.plugin.view;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.swingcontrol.displayTable.TablePanelUtil;
import com.hhdb.csadmin.common.ui.textEdit.QueryEditorUi2;
import com.hhdb.csadmin.common.ui.textEdit.QueryTextPane;
import com.hhdb.csadmin.common.util.HHSqlUtil;
import com.hhdb.csadmin.common.util.IconUtilities;
import com.hhdb.csadmin.plugin.table_open.ui.HHTableColumnCellRenderer;
import com.hhdb.csadmin.plugin.view.service.SqlOperationService;
import com.hhdb.csadmin.plugin.view.ui.BaseButton;
import com.hhdb.csadmin.plugin.view.ui.HHPagePanel;
import com.hhdb.csadmin.plugin.view.util.ButtonPanelEditorRenderer;
import com.hhdb.csadmin.plugin.view.util.ViewsPanelHandle;

/**  
 * 打开表操作面板
 */
public class ViewOpenPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	public SqlOperationService sqsv;
	public ViewsPanelHandle vstp;
	//按钮栏
	private JToolBar toolBar = new JToolBar();
	private TablePanelUtil hhTbPanel;
	//翻页按钮面板
	private HHPagePanel hhPgPanel;
	//选项卡面板
	private JTabbedPane tab;
	
	/*** 控制快捷键保存只能保存一次*/
	private boolean key = true;
	/*** 新建还是修改 */
	private boolean isHave = false;     //默认新建
	
	private QueryEditorUi2 edit;    //编辑面板
	private QueryTextPane preview;			//预览面板
	public HView hv;
	JSplitPane vSplitPane;
	
	/*** 每页显示的数据条数*/
	public final int inNum = 30;
	/***查询视图第N页数据sql*/
	public String nextPageData = "select *,true upd from %s limit " + inNum + " offset %d*" + inNum;
	/***前一次的页码数*/
	public int pageNum;
	/***模式名*/
	public String smName = "";
	/***数据库名*/
	public String dbName = "";
	/*** 视图名*/
	public String viewName; 
	
	
	
	public ViewOpenPanel(HView hv){
		this.hv=hv;
		sqsv = new SqlOperationService(hv,this);
		vstp = new ViewsPanelHandle(this);
		vSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	}
	
	/**
	 * 打开视图初始化
	 * @param firstPageTable
	 */
	public void openview() {
		List<List<Object>> list = pageTable(nextPageData, 0);  // 发送sql,获取数据,展示第一页数据
		this.setLayout(new GridBagLayout());
		this.setBorder(BorderFactory.createLineBorder(new Color(178, 178, 178),1));
		hhTbPanel = new TablePanelUtil(sqsv.getUneditable(list,true,"bytea"));
		hhTbPanel.nterlacedDiscoloration(true,null,null); 
		hhTbPanel.highlight(true,false,null);
		hhTbPanel.setRowSorter(true);
		hhTbPanel.setBorder(BorderFactory.createEmptyBorder());
		hhTbPanel.getBaseTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		hhPgPanel = new HHPagePanel();
		excutepage(list);
		add(hhTbPanel, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(hhPgPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0, 0, 30), 10, 0));
		
		buttonAction();
		showPageCount();
	}

	
	/**
	 * 处理数据显示
	 * @param pageTable
	 */
	public void excutepage(List<List<Object>> lis) {
		//表头数据
		Vector<Object> colname = new Vector<Object>();
		colname.add("");
		for (Object field : lis.get(0)) {
			colname.add(field);
		}
		Vector<Object> vect = new Vector<Object>();
		//将查询出来的数据分出来
		for(int j=2;j<lis.size();j++){
			List<Object> l = lis.get(j);
			Vector<Object> rowsLine = new Vector<Object>();
			rowsLine.add(j-1+"");   //行号
			for(Object value:l){
				rowsLine.add(value);
			}
			vect.add(rowsLine);
		}
		//获取字段类型
		List<Object> typelist = new ArrayList<Object>();
		typelist.add("");
		for (Object field : lis.get(1)) {
			typelist.add(field);
		}
		//添加数据
		hhTbPanel.getTableDataModel().setDataVector(vect,colname);
		//判断哪些列需要加入流操作按钮
		ButtonPanelEditorRenderer er =  new ButtonPanelEditorRenderer(this);
		for(int i=0;i<colname.size();i++){
			Object name = colname.get(i);
			if(!"".equals(name)){
			Object type=typelist.get(i);
				if(type.equals("bytea")){
					hhTbPanel.getBaseTable().getColumn(name).setCellRenderer(er);
					hhTbPanel.getBaseTable().getColumn(name).setCellEditor(er);
				}
			}
		}
		//设置行号列样式
		TableColumn index = hhTbPanel.getBaseTable().getColumnModel().getColumn(0);
		index.setMaxWidth(25);
		index.setMinWidth(25);
		index.setCellRenderer(new HHTableColumnCellRenderer());
		//隐藏后面列 upd 
		TableColumn coln = hhTbPanel.getBaseTable().getColumnModel().getColumn(colname.size() - 1);
		coln.setMinWidth(0);
		coln.setMaxWidth(0);
		coln.setWidth(0);
		coln.setPreferredWidth(0);
	}

	/**
	 * 编辑页面
	 * 
	 */
	public void viewsTabPanelHandle(boolean isHaves) {
		//获取sql编辑面板
		edit = new QueryEditorUi2();
		getKeyName();
		//获取sql预览面板
		preview = new QueryTextPane();
		if(isHaves){   //为修改时
			this.isHave = isHaves;
			vstp.setViewName(viewName);
			vstp.design(edit);
		}
		tab = new JTabbedPane(JTabbedPane.TOP);
		tab.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		setLayout(new GridBagLayout());
		createBtn("保存", IconUtilities.loadIcon("save.png"), "save");
		createBtn("另存为", IconUtilities.loadIcon("save.png"), "saveAs");
		toolBar.addSeparator();  //分隔符
		createBtn("预览", IconUtilities.loadIcon("runview.png"), "preview");
		toolBar.addSeparator();
		toolBar.setFloatable(false);
		tab.addTab("定义", edit.getContentPane());
		tab.addTab("SQL查看", new JScrollPane(preview));
		add(toolBar, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		add(tab, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		//sql预览页
		tab.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
			    int selectedIndex = tabbedPane.getSelectedIndex();
			    if(selectedIndex==1){ 
			    	String temp = "";
		    		temp = "CREATE VIEW \""+smName+"\".\""+viewName+"\" AS"+"\n";
		    		String sqlText=edit.getText();
		    		if(sqlText.trim().lastIndexOf(";")<0){
		    			sqlText+=";";
		    		}
		    		preview.setText(temp+sqlText);
			    }
			}
		});
		
		//监听键盘Ctrl+S保存
		edit.getContentPane().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_S ){
					saveViewKey();
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCmd = e.getActionCommand();
		if (actionCmd.equals("save")) {
			if (isHave) {  //修改
				boolean flag = vstp.updatView(edit, viewName,smName);
				toolBar.getComponentAtIndex(0).setEnabled(flag);
			} else {   									 //保存
				boolean flag = vstp.saveView(edit,smName);
				toolBar.getComponentAtIndex(0).setEnabled(flag);
			}
		} else if ("saveAs".equals(actionCmd)) {
			vstp.saveView(edit, smName);
		} else if (actionCmd.equals("preview")) {   //视图预览
			String strSelected = edit.getText();
			if (strSelected != null && !"".equals(strSelected.trim())) {
				vSplitPane.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {  //窗口大小改变时调整大小位置
						vSplitPane.setDividerLocation(0.5);
					}
				});
				vSplitPane.setTopComponent(tab);
				vSplitPane.setDividerLocation(0.5);
				vstp.previewPanel(edit,vSplitPane);
				add(vSplitPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH,new Insets(0, 0, 0, 0), 0, 0), 0);
				vSplitPane.getParent().doLayout();  //刷新布局
			}
		}
	}
	
	/**
	 * 快捷键保存
	 */
	public void saveViewKey(){
		if(key){
			boolean flag;
			if(isHave){
				flag = vstp.updatView(edit, viewName,smName);
			}else{
				flag = vstp.saveView(edit, smName);
			}
			toolBar.getComponentAtIndex(0).setEnabled(flag);
			key = flag;
		}
	}
	
	/**
	 * 展示页码
	 */
	public void showPageCount() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// 先获取表总行数
					String totalRowsSql = "select count(1) from	" + "\""+ smName + "\".\"" + viewName + "\"";
					List<List<Object>> rowStr = sqsv.getListList(totalRowsSql);
					List<Object> rowStrList = rowStr.get(1);
					int rowCount = Integer.parseInt(rowStrList.get(0)+"");
					int intCount = rowCount / inNum;
					int modCount = rowCount % inNum;
					// 总页码数
					int pageCount;
					if (modCount != 0) {
						pageCount = intCount + 1;
					} else {
						pageCount = intCount;
					}
					getHhPgPanel().setPageCount(pageCount);
					getHhPgPanel().getTotalPageNum().setText(pageCount + "");
					if (pageCount <= 1) {
						getHhPgPanel().getNextPage().setEnabled(false);
						getHhPgPanel().getLastPage().setEnabled(false);
					}
				} catch (IOException e) {
					LM.error(LM.Model.CS.name(), e);
				} catch (Exception e) {
					LM.error(LM.Model.CS.name(), e);
				}
			}
		}).start();
	}

	/**
	 * 发送sql请求数据
	 * @param pageData SQL语句
	 * @param num  页码
	 * @return
	 */
	public List<List<Object>> pageTable(String pageData, int num) {
		List<List<Object>> list = new ArrayList<>();
		try {
			String pageDatas;
			if (num == 0) {
				pageDatas = String.format(pageData, "\""+ smName + "\".\"" + viewName + "\"", 0);
			} else {
				pageDatas = String.format(pageData, "\""+ smName + "\".\"" + viewName + "\"", num);
			}
			list = sqsv.getListType(pageDatas);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
		return list;
	}


	/**
	 * 面板上的按钮点击事件
	 */
	public void buttonAction() {
		// 点击第一页按钮回到第一页
		getHhPgPanel().getFirstPage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (Integer.parseInt(getHhPgPanel().getCurPageNum().getText()) <= 1) {   //当在第一页点击回首页无效
					return;
				}
				List<List<Object>> list = pageTable(nextPageData, 0);
				excutepage(list);
				getHhPgPanel().getCurPageNum().setText("1");
				getHhPgPanel().getFirstPage().setEnabled(false);
				getHhPgPanel().getPrePage().setEnabled(false);
				if (getHhPgPanel().getPageCount() > 1) {
					getHhPgPanel().getNextPage().setEnabled(true);
					getHhPgPanel().getLastPage().setEnabled(true);
				}
			}
		});
		// 点击上一页按钮回到前一页
		getHhPgPanel().getPrePage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cPNum = Integer.parseInt(getHhPgPanel().getCurPageNum().getText());
				if (cPNum <= 1) {   //当在第一页点击回首页无效
					return;
				}
				List<List<Object>> list = pageTable(nextPageData,cPNum - 2);
				excutepage(list);
				cPNum--;
				getHhPgPanel().getCurPageNum().setText(cPNum + "");
				if (cPNum <= 1) {
					getHhPgPanel().getFirstPage().setEnabled(false);
					getHhPgPanel().getPrePage().setEnabled(false);
				}
				if (getHhPgPanel().getPageCount() > 1) {
					getHhPgPanel().getNextPage().setEnabled(true);
					getHhPgPanel().getLastPage().setEnabled(true);
				}
			}
		});
		// 点击页码输入框时记录之前的页码
		getHhPgPanel().getCurPageNum().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String text = getHhPgPanel().getCurPageNum().getText();
				if (text != null && !"".equals(text) && !"0".equals("")) {
					pageNum = Integer.parseInt(getHhPgPanel().getCurPageNum().getText());
				}
			}

		});
		// 输入页码跳到当前输入页
		getHhPgPanel().getCurPageNum().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						String curPageNum = getHhPgPanel().getCurPageNum().getText();
						if ("".equals(curPageNum)) {
							return;
						}
						// 判断每个字符是不是数字
						for (char c : curPageNum.toCharArray()) {
							if (!Character.isDigit(c)) {
								JOptionPane.showMessageDialog(null, "请输入正确的页码");
								getHhPgPanel().getCurPageNum().setText(pageNum + "");
								return;
							}
						}
						// 判断输入的页数是不是大于总页数
						int cPNum = Integer.parseInt(curPageNum);
						if (cPNum > getHhPgPanel().getPageCount()) {
							JOptionPane.showMessageDialog(null,"请输入正确的页码");
							getHhPgPanel().getCurPageNum().setText(pageNum + "");
							return;
						}
						List<List<Object>> nextPageTable = pageTable(nextPageData,cPNum - 1);
						excutepage(nextPageTable);
						if (cPNum <= 1) {
							getHhPgPanel().getFirstPage().setEnabled(false);
							getHhPgPanel().getPrePage().setEnabled(false);
						} else {
							getHhPgPanel().getFirstPage().setEnabled(true);
							getHhPgPanel().getPrePage().setEnabled(true);
						}
						if (cPNum >= getHhPgPanel().getPageCount()) {
							getHhPgPanel().getNextPage().setEnabled(false);
							getHhPgPanel().getLastPage().setEnabled(false);
						} else {
							getHhPgPanel().getNextPage().setEnabled(true);
							getHhPgPanel().getLastPage().setEnabled(true);
						}
					}
				});
			}
		});
		// 点击下一页按钮回到下一页
		getHhPgPanel().getNextPage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int cPNum = Integer.parseInt(getHhPgPanel().getCurPageNum().getText());
				if (cPNum >= getHhPgPanel().getPageCount()) {   //处于最后一页点击无效
					return;
				}
				List<List<Object>> nextPageTable = pageTable(nextPageData, cPNum);
				excutepage(nextPageTable);
				cPNum++;
				getHhPgPanel().getCurPageNum().setText(cPNum + "");
				if (cPNum > 1) {
					getHhPgPanel().getFirstPage().setEnabled(true);
					getHhPgPanel().getPrePage().setEnabled(true);
				}
				if (cPNum >= getHhPgPanel().getPageCount()) {
					getHhPgPanel().getNextPage().setEnabled(false);
					getHhPgPanel().getLastPage().setEnabled(false);
				}
			}
		});
		// 点击最后一页按钮回到最后一页
		getHhPgPanel().getLastPage().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int tPNum = getHhPgPanel().getPageCount();
						if (tPNum <= 1) {  //只有一页点击无效
							return;
						}
						List<List<Object>> lastPageTable = pageTable(nextPageData,tPNum - 1);
						excutepage(lastPageTable);
						getHhPgPanel().getCurPageNum().setText(tPNum + "");
						if (tPNum > 1) {
							getHhPgPanel().getFirstPage().setEnabled(true);
							getHhPgPanel().getPrePage().setEnabled(true);
						}
						getHhPgPanel().getNextPage().setEnabled(false);
						getHhPgPanel().getLastPage().setEnabled(false);
					}
				});
			}
		});
	}
	
	
	/**
	 * 设置表名视图名提示关键词
	 */
	public void getKeyName() {
		List<Map<String, Object>> list = new ArrayList<>();
		List<String> lis = new ArrayList<>();
		List<String> liss = new ArrayList<>();
		try {
			// 表名
			list = sqsv.getNameByType(HHSqlUtil.ITEM_TYPE.TABLE, "prop_coll"); 
			for (Map<String, Object> maps : list) {
				lis.add(maps.get("name").toString());
			}
			edit.setTableCompletionProvider(lis);
			// 视图
			list = sqsv.getNameByType(HHSqlUtil.ITEM_TYPE.VIEW, "prop_coll");
			for (Map<String, Object> maps : list) {
				liss.add(maps.get("name").toString());
			}
			edit.setViewCompletionProvider(liss);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
			JOptionPane.showMessageDialog(null, e.getMessage(), "提示",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 创建按钮
	 * @param text
	 * @param icon
	 * @param comand
	 */
	private void createBtn(String text, Icon icon, String comand) {
		BaseButton basebtn = new BaseButton(text, icon);
		basebtn.setActionCommand(comand);
		basebtn.addActionListener(this);
		toolBar.add(basebtn);
	}
	
	public TablePanelUtil getHhTbPanel() {
		return hhTbPanel;
	}

	public void setHhTbPanel(TablePanelUtil hhTbPanel) {
		this.hhTbPanel = hhTbPanel;
	}

	public HHPagePanel getHhPgPanel() {
		return hhPgPanel;
	}

	public void setHhPgPanel(HHPagePanel hhPgPanel) {
		this.hhPgPanel = hhPgPanel;
	}

	

}
