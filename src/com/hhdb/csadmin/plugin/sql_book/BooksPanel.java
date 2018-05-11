package com.hhdb.csadmin.plugin.sql_book;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import com.hh.frame.common.enums.DBTypeEnum;
import com.hh.frame.common.log.LM;
import com.hh.frame.dirobj.DirTool;
import com.hh.frame.dirobj.InitTool;
import com.hh.frame.dirobj.LinkTool;
import com.hh.frame.dirobj.ObjTool;
import com.hh.frame.dirobj.bean.DbObjBean;
import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.common.util.HSQL_Util;
import com.hhdb.csadmin.plugin.sql_book.attribute.mouse.MouseHandler;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeCellRenderer;
import com.hhdb.csadmin.plugin.sql_book.attribute.tree.BaseTreeNode;
import com.hhdb.csadmin.plugin.sql_book.bean.MetaTreeNodeBean;
import com.hhdb.csadmin.plugin.sql_book.service.SqlOperationService;
import com.hhdb.csadmin.plugin.sql_book.ui.BaseTree;
import com.hhdb.csadmin.plugin.sql_book.ui.ShortcutPanel;
import com.hhdb.csadmin.plugin.sql_book.util.TreeNodeUtil;

/**
 * sql宝典面板
 * @author hhxd
 * 
 */
public class BooksPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	public SqlOperationService sqls;
	public JScrollPane sqlDetail;
	public ServerBean svbean;

	//hsql操作类
	public String prefix = "hh_";   //hsql的前缀
	public InitTool initTool;
	public DirTool dirtool;
	public ObjTool objtool;
	public LinkTool linkTool;
	/*** 快捷键源文件id  */
	public Integer myDirId;  	
	/*** 弹出窗 */
	public ShortcutPanel shp;	   

	/*** 树形图 */
	private BaseTree tree;
	/*** 打开宝典的来源处 true：正常打开 false：从查询器打开。 */
	public Boolean source = true;   
	/*** 查询器编辑面板 */
	public JTextArea jtext;
	/*** 查询器传递的sql */
	public String sql;
	
	/**
	 * 
	 * @param hbook
	 * @param bool
	 * 结构树展示功能控制标识 true:为主界面结构树。 false:快捷键地址选择结构树。
	 * 
	 */
	public BooksPanel(HSqlBook hbook,Boolean bool) {
		setLayout(new GridBagLayout());
		sqls = new SqlOperationService(hbook, this);
		svbean = sqls.getServerbean();
		BaseTreeNode root = new BaseTreeNode();
		init(root);
		tree = new BaseTree(root);
		tree.setCellRenderer(new BaseTreeCellRenderer());  //设置数据渲染
		tree.addMouseListener(new MouseHandler(tree,this,bool));  //添加鼠标点击事件
//		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);   //设置为可以多选

		//分隔面板
		JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);   
		jsp.setDividerLocation(0.4);
		jsp.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		
		JScrollPane jp = new JScrollPane(tree);
		jp.setPreferredSize(new Dimension(300,200));
		
		jsp.setLeftComponent(jp);
		jsp.setRightComponent(sqlDetail = new JScrollPane());
		
		add(jsp, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
	}
	
	/**
	 * 初始化
	 */
	public void init(BaseTreeNode root) {
		try {
			//获取HSQL的连接
			initTool = new InitTool(DBTypeEnum.hsql, prefix);
			dirtool = new DirTool(DBTypeEnum.hsql, prefix);
			objtool = new ObjTool(DBTypeEnum.hsql, prefix);
			linkTool = new LinkTool(DBTypeEnum.hsql, prefix);
			//创建表
			initTool.createAll(HSQL_Util.getConnection());
			//初始化根节点
			MetaTreeNodeBean tablemtn = new MetaTreeNodeBean();
			//判断是否有根节点
			DbObjBean dirbean = dirtool.get(HSQL_Util.getConnection(),0);
			if(null != dirbean ){
				tablemtn.setName(dirbean.getName());
				tablemtn.setOpenIcon("book.png");
				tablemtn.setType(TreeNodeUtil.ROOT_TYPE);
				tablemtn.setId(dirbean.getId());
			}else{
				//新建根节点
				int dirId = dirtool.createRoot(HSQL_Util.getConnection(),"SQL宝典");
				tablemtn.setName("SQL宝典");
				tablemtn.setOpenIcon("book.png");
				tablemtn.setType(TreeNodeUtil.ROOT_TYPE);
				tablemtn.setId(dirId);
			}
			root.setMetaTreeNodeBean(tablemtn);
		} catch (Exception e) {
			LM.error(LM.Model.CS.name(), e);
		}
	}
	
	
	public BaseTree getTree() {
		return tree;
	}

	public void setTree(BaseTree tree) {
		this.tree = tree;
	}


	
}
