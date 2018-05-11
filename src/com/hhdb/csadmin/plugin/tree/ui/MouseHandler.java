package com.hhdb.csadmin.plugin.tree.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.plugin.tree.HTree;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.BasePopupMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.ConsMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.ConstraintsMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.DBMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.DBsMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.ErrorMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.ExtendMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.ExtendsMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.FunctionMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.FunctionsMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.IndexMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.IndexsMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.MoreTreeNodeMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.QueryMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.QuerysMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.SchemaMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.SchemasMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.SequenceMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.SequencesMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.TableMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.TableSpMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.TableSpsMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.TablesMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.TypeMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.TypesMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.UserMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.UsersMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.ViewMenu;
import com.hhdb.csadmin.plugin.tree.ui.rightMenu.ViewsMenu;
import com.hhdb.csadmin.plugin.tree.util.ThreadUtils;
import com.hhdb.csadmin.plugin.tree.util.TreeNodeUtil;
/**
 * 鼠标事件
 * @author 胡圆锥
 *
 */
public class MouseHandler extends MouseAdapter {
	private BaseTree tree = null;
	private TreeDBClick treedb;
	private TreeClick treeclick;
	private HTree htree = null;	
	private FunctionMenu functionMenu;
	private FunctionsMenu functionsMenu;
	private QueryMenu queryMenu;
	private QuerysMenu querysMenu;
	private SchemasMenu schemasMenu;
	private SchemaMenu schemaMenu;
	private SequenceMenu sequenceMenu;
	private SequencesMenu sequencesMenu;
	private TableMenu tableMenu;
	private TablesMenu tablesMenu;
	private TypeMenu typeMenu;
	private TypesMenu typesMenu;
	private ViewMenu viewMenu;
	private ViewsMenu viewsMenu;
	private ExtendsMenu extendsMenu;
	private ExtendMenu extendMenu;
	private DBsMenu dbsMenu;
	private DBMenu dbMenu;
	private TableSpsMenu tablespsMenu;
	private TableSpMenu tablespMenu;
	private UsersMenu usersMenu;
	private UserMenu userMenu;
	private ConsMenu consMenu;
	private IndexMenu indexMenu;
	private ConstraintsMenu constraintsMenu;
	private IndexsMenu indexsMenu;

	public MouseHandler(BaseTree tree,HTree htree){
		this.htree = htree;
		this.tree = tree;
		treedb = new TreeDBClick(htree);
		treeclick = new TreeClick(htree);
	}
	public void mouseClicked(final MouseEvent e) {
		int selRow = tree.getRowForLocation(e.getX(), e.getY());
		boolean condition = true;
		condition = condition && (selRow != -1); // 如果选中
		// 右键菜单
		if (condition && e.getButton() == 3) {
			int count = tree.getSelectionCount();
			TreePath selPath = tree.getPathForLocation(e.getX(), e.getY()); // 返回指定节点的树路径
				
			TreePath[] ts = tree.getSelectionPaths();
			boolean flag = false;
			if(null != ts){
				for(TreePath flagtp:ts){
					if(selPath.equals(flagtp)){
						flag = true;
						break;
					}
				}
			}else{
				tree.setSelectionPath(selPath);
			}
			if(count>1&&flag){
				rightClikTreeNode(ts, e);
			}else{
				final BaseTreeNode treeNode = (BaseTreeNode)selPath.getLastPathComponent();
				tree.setSelectionPath(selPath);
				rightClickTreeNode(treeNode, e);
			}
			return;
		}
		if (condition && e.getClickCount() == 2) {
			TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
			final BaseTreeNode treeNode = (BaseTreeNode)selPath.getLastPathComponent();
			if(treeNode != null){
				treedb.execute(treeNode);
			}
			return;
		}
		if (condition && e.getClickCount() == 1) {
			ThreadUtils.invokeLater(new Runnable() {
				@Override
				public void run() {
					TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
					final BaseTreeNode treeNode = (BaseTreeNode)selPath.getLastPathComponent();
					treeclick.execute(treeNode);
				}
			});
			
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(final MouseEvent e) {

	}
	
	private void rightClikTreeNode(TreePath[] ts,MouseEvent e){
		String flagType = ((BaseTreeNode)ts[0].getLastPathComponent()).getType();
		List<BaseTreeNode> BaseTreeNodelist = new ArrayList<BaseTreeNode>();
		for(TreePath tp:ts){
			BaseTreeNode tn = (BaseTreeNode)tp.getLastPathComponent();
			if(!flagType.equals(tn.getType())){
				ErrorMenu errormenu = new ErrorMenu();
				errormenu.showPopup(e.getComponent(), e.getX(), e.getY());
				return;
			}else{
				BaseTreeNodelist.add(tn);
			}
		}
		MoreTreeNodeMenu mtnm = new MoreTreeNodeMenu(htree);
		BasePopupMenu popupMenu = mtnm.getInstance(BaseTreeNodelist, flagType);
		popupMenu.showPopup(e.getComponent(), e.getX(), e.getY());
	}
	
	private void rightClickTreeNode(BaseTreeNode treeNode, MouseEvent e) {
		String type = treeNode.getType();
		BasePopupMenu popupMenu = null;
		CmdEvent getspflag = new CmdEvent(htree.PLUGIN_ID,
				"com.hhdb.csadmin.plugin.conn","getSuperuser");
		HHEvent relEvent = htree.sendEvent(getspflag);
		String spflag = relEvent.getValue("superuser_value");
				
		if(type.equals(TreeNodeUtil.DB_TYPE)) {
			if(spflag.equals("true")){
				if(dbsMenu==null){
					dbsMenu = new DBsMenu(htree);
				}
				popupMenu = dbsMenu.getInstance(treeNode);
			}
		}else if(type.equals(TreeNodeUtil.DB_ITEM_TYPE)) {
			if(spflag.equals("true")){
				if(dbMenu==null){
					dbMenu = new DBMenu(htree);
				}
				popupMenu = dbMenu.getInstance(treeNode);
			}
		}else if(type.equals(TreeNodeUtil.SCHEMA_TYPE)) {
			if(schemasMenu==null){
				schemasMenu = new SchemasMenu(htree);
			}
			popupMenu =schemasMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.SCHEMA_ITEM_TYPE)) {
			if(schemaMenu==null){
				schemaMenu = new SchemaMenu(htree);
			}
			popupMenu =schemaMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.TABLE_TYPE)) {
			if(tablesMenu==null){
				tablesMenu = new TablesMenu(htree);
			}
			popupMenu =tablesMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.TABLE_ITEM_TYPE)) {
			if(tableMenu==null){
				tableMenu = new TableMenu(htree);
			}
			popupMenu =tableMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.COL_TYPE)) {

		}else if(type.equals(TreeNodeUtil.CONSTRAINT_TYPE)) {
			if(constraintsMenu==null){
				constraintsMenu=new ConstraintsMenu(htree);
			}
			popupMenu=constraintsMenu.getInstance(treeNode);
		
		}else if(type.equals(TreeNodeUtil.INDEX_TYPE)) {
			if(indexsMenu==null){
				indexsMenu = new IndexsMenu(htree);
			}
			popupMenu = indexsMenu.getInstance(treeNode);

		}else if(type.equals(TreeNodeUtil.RULE_TYPE)) {

		}else if(type.equals(TreeNodeUtil.TABLE_TRIGGER_TYPE)) {

		}else if(type.equals(TreeNodeUtil.VIEW_TYPE)) {
			if(viewsMenu==null){
				viewsMenu = new ViewsMenu(htree);
			}
			popupMenu = viewsMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.VIEW_ITEM_TYPE)) {
			if(viewMenu==null){
				viewMenu = new ViewMenu(htree);
			}
			popupMenu = viewMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.SEQ_TYPE)) {
			if(sequencesMenu==null){
				sequencesMenu = new SequencesMenu(htree);
			}
			popupMenu = sequencesMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.SEQ_ITEM_TYPE)) {
			if(sequenceMenu==null){
				sequenceMenu = new SequenceMenu(htree);
			}
			popupMenu = sequenceMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.FUN_TYPE)) {
			if(functionsMenu==null){
				functionsMenu = new FunctionsMenu(htree);
			}
			popupMenu = functionsMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.FUN_ITEM_TYPE)) {
			if(functionMenu==null){
				functionMenu = new FunctionMenu(htree);
			}
			popupMenu = functionMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.TYPE_TYPE)) {
			if(typesMenu==null){
				typesMenu = new TypesMenu(htree);
			}
			popupMenu = typesMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.TYPE_ITEM_TYPE)) {
			if(typeMenu==null){
				typeMenu = new TypeMenu(htree);
			}
			popupMenu = typeMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.SELECT_TYPE)) {
			if(querysMenu==null){
				querysMenu = new QuerysMenu(htree);
			}
			popupMenu = querysMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.SELECT_ITEM_TYPE)) {
			if(queryMenu==null){
				queryMenu = new QueryMenu(htree);
			}
			popupMenu = queryMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.EXTENSION_TYPE)) {
			if(extendsMenu==null){
				extendsMenu = new ExtendsMenu(htree);
			}
			popupMenu = extendsMenu.getInstance(treeNode);
		}else if(type.equals(TreeNodeUtil.EXTENSION_PLUGIN_TYPE)) {
			if(extendMenu==null){
				extendMenu = new ExtendMenu(htree);
			}
			popupMenu = extendMenu.getInstance(treeNode);
		}else if(type.equals(TreeNodeUtil.PERFORMANCE_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.TAB_SPACE_TYPE)) {
			
			if(tablespsMenu==null){
				tablespsMenu = new TableSpsMenu(htree);
			}
			popupMenu = tablespsMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.TAB_SPACE_ITEM_TYPE)) {
			
			if(tablespMenu==null){
				tablespMenu = new TableSpMenu(htree);
			}
			popupMenu = tablespMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.LOGIN_ROLE_TYPE)) {
			if(usersMenu==null){
				usersMenu = new UsersMenu(htree);
			}
			popupMenu = usersMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.LOGIN_ROLE_ITEM_TYPE)) {
			
			if(userMenu==null){
				userMenu = new UserMenu(htree);
			}
			popupMenu = userMenu.getInstance(treeNode);
			
		}else if(type.equals(TreeNodeUtil.GROUP_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.CPU_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.DISK_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.MEMORY_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.COURSE_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.NETWORK_MONITORING_TYPE)) {
			
		}else if(type.equals(TreeNodeUtil.INDEX_ITEM_TYPE)){
			if(indexMenu==null){
				indexMenu = new IndexMenu(htree);
			}
			popupMenu = indexMenu.getInstance(treeNode);
		}
		else if(type.equals(TreeNodeUtil.CONSTRAINT_PK_ITEM_TYPE)||
				type.equals(TreeNodeUtil.CONSTRAINT_FK_ITEM_TYPE)||
				type.equals(TreeNodeUtil.CONSTRAINT_UK_ITEM_TYPE)||
				type.equals(TreeNodeUtil.CONSTRAINT_CK_ITEM_TYPE)||
				type.equals(TreeNodeUtil.CONSTRAINT_XK_ITEM_TYPE)){
			if(consMenu==null){
				consMenu = new ConsMenu(htree);
			}
			popupMenu = consMenu.getInstance(treeNode);
		}
		if (popupMenu != null) {
			popupMenu.showPopup(e.getComponent(), e.getX(), e.getY());
		}
	}

}
