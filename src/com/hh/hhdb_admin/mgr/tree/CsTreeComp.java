package com.hh.hhdb_admin.mgr.tree;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.create_dbobj.treeMr.TreeMrUtil;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.create_dbobj.treeMr.base.ViewType;
import com.hh.frame.create_dbobj.treeMr.mr.AbsTreeMr;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBasePanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tree2.HTreeSearchBar;
import com.hh.frame.swingui.view.util.ImgUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.ExtDbTypeUtil;
import com.hh.hhdb_admin.mgr.login.comp.DBTypeBean;
import com.hh.hhdb_admin.mgr.tree.handler.LeftDoubleClickHandler;
import com.hh.hhdb_admin.mgr.tree.handler.RightClickHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;


public class CsTreeComp extends HPanel {
	private DBTypeEnum dbTypeEnum = CsVarUtil.getDbtype();
	private JdbcInfo jdbcBean=CsVarUtil.jdbcCloneWithSsh();
	private CsTree tree;
	private CsTree schemaTree;
//	private boolean isSchemaSearch = false;
	

	/**
	 * 获取树实例
	 *
	 * @param loginBean 登录信息
	 * @return 树实例
	 */
	public static CsTreeComp newTreeInstance()throws Exception {
		CsTreeComp treeComp = new CsTreeComp();
		JdbcInfo jdbcBean = treeComp.jdbcBean;
		AbsTreeMr treeMr=AbsTreeMr.genTreeMgr(jdbcBean, LangMgr.langType);
		TreeMrNode rootNodeMr = treeMr.getRootNode(jdbcBean.getName(), ViewType.valueOf(jdbcBean.getView().name()));
        CsFolderBean rootNode = new CsFolderBean(rootNodeMr.getName());
        rootNode.setMrNode(rootNodeMr);
		if (StringUtils.isNotBlank(jdbcBean.getOtherDbType())) {
			DBTypeBean a = ExtDbTypeUtil.selfDefinedDbTypeMaps.get(jdbcBean.getOtherDbType());
			if (!StringUtils.isBlank(a.getLogo())) {
				File f = new File(ExtDbTypeUtil.ICON_FILE, a.getLogo());
				if (f.exists()) {
					rootNode.setIcon(f.getName().toLowerCase().endsWith("svg") ? ImgUtil.readSvgIcon(f) : ImgUtil.readImgIcon(f));
				}else {
					rootNode.setFontType(FaSolidEnum.DATABASE);
				}
			}else {
				rootNode.setFontType(FaSolidEnum.DATABASE);
			}
		} else {
			rootNode.setIcon(IconFileUtil.getDbIcon(treeComp.dbTypeEnum, IconSizeEnum.SIZE_16));
		}
		
		treeComp.tree = new CsTree(rootNode);
		treeComp.tree.setRightHandler(new RightClickHandler(treeComp.tree, jdbcBean))
				.setLeftDoubleHandler(new LeftDoubleClickHandler(treeComp.tree));
		boolean isDatabaseModel=TreeMrUtil.isDatabaseModel(treeComp.dbTypeEnum);
        //根据数据库自动展开节点
        try {
			treeComp.tree.getLeftDoubleHandler().onClick(rootNode);
			if (rootNode.getType() == TreeMrType.ROOT) {
				CsFolderBean subNode=clickNode(treeComp,rootNode);
				if(isDatabaseModel) 
					clickNode(treeComp,subNode);
			}
        } catch (Exception e) {
			Lg.error(CsTreeComp.class,e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
		treeComp.initMainTreePanel(treeComp.tree,isDatabaseModel);
		return treeComp;
	}
	
	private static CsFolderBean clickNode(CsTreeComp treeComp,CsFolderBean node) throws Exception {
		node=(CsFolderBean) node.getNodeBeans().get(0);
		treeComp.tree.getLeftDoubleHandler().onClick(node);
		return node;
	}
	

	private void initMainTreePanel(CsTree tree,boolean isDatabaseModel) {
	
		LastPanel lastPanel = new LastPanel();
		lastPanel.setHead(new HTreeSearchBar(tree, true).getComp());
		lastPanel.setWithScroll(tree.getComp());
		//达梦不支持模式切换但是需要三层树状结构
		if (TreeMrUtil.hasSwitchTab(jdbcBean)) {
			HPanel databasePanel = new HPanel();
			databasePanel.setLastPanel(lastPanel);
			HTabPanel tabPane = new HTabPanel() {
				@Override
				public void onSelected(String id) {
//					isSchemaSearch = "schema".equals(id);
				}
			};
			HBasePanel schemaPanel=getSchemaPanel(isDatabaseModel);
			if(schemaPanel!=null) {
				HeaderConfig schemaHeader = newHeaderConfig( isDatabaseModel ? LangMgr.lang(Word3.LIBRARY_COLLECTION) : LangMgr.lang(Word2.SCHEMA));
				tabPane.setPanel("schema", schemaPanel, schemaHeader);
			}
			HeaderConfig databaseHeader = newHeaderConfig(LangMgr.lang(Word3.DATABASE));
			tabPane.setPanel("database", databasePanel, databaseHeader);
			tabPane.selectPanel("database");
			LastPanel last = new LastPanel();
			last.set(tabPane.getComp());
			setLastPanel(last);
		}else {
			setLastPanel(lastPanel);
		}
	}

	/**
	 * 生成模式切换面板
	 *
	 * @param loginBean 登录信息
	 * @return 模式选择面板
	 */
	private HPanel getSchemaPanel(boolean isDatabaseModel) {
        CsFolderBean treeNode = new CsFolderBean(isDatabaseModel ? LangMgr.lang(Word3.DATABASE):LangMgr.lang(Word2.SCHEMA));
//		treeNode.setType(TreeMrType.DATA_MODEL_SCHEMA_GROUP);
		treeNode.setFontType(isDatabaseModel ?FaSolidEnum.DATABASE:FaSolidEnum.FOLDER);
		schemaTree = CsTree.newSchemaChangeTree(treeNode);
		try {
			List<String> names=TreeMrUtil.getDbOrSchemaNames(dbTypeEnum, jdbcBean.getCurrentVersion(),CsVarUtil.conn);
			if(names==null) return null;
			String dbName = ((CsFolderBean)tree.getRoot().getNodeBeans().get(0)).getMrNode().getName();
			for(String name:names) {
				name = name.trim();
				CsFolderBean node = new CsFolderBean(name);
				if (isDatabaseModel) {
					node.setFontType(dbName.toLowerCase().equals(name.toLowerCase()) ? FaSolidEnum.HOCKEY_PUCK : FaSolidEnum.DATABASE);
				} else {
				    if (dbTypeEnum == DBTypeEnum.redis) {
                        node.setFontType(dbName.toLowerCase().equals(name.toLowerCase()) ? FaSolidEnum.FOLDER_OPEN : FaSolidEnum.FOLDER);
                    } else {
                        node.setFontType(dbName.equals(name) ? FaSolidEnum.FOLDER_OPEN : FaSolidEnum.FOLDER);
                    }
				}
				treeNode.setMrNode(new TreeMrNode(name,TreeMrType.SCHEMA_GROUP));
				schemaTree.add(treeNode.getId(), node);
			}
		} catch (Exception e) {
			PopDlgUtil.error(CsVarUtil.parentFrame,e);
			Lg.error(getClass(), e);
			return null;
		}
		
		schemaTree.expandAll();
		schemaTree.setRightHandler(new RightClickHandler(schemaTree, jdbcBean));
		HPanel panel = new HPanel();
		LastPanel lastPanel = new LastPanel();
		lastPanel.setHead(new HTreeSearchBar(schemaTree, false).getComp());
		lastPanel.setWithScroll(schemaTree.getComp());
		panel.setLastPanel(lastPanel);
		return panel;
	}

	/**
	 * 根据条件查找到指定节点刷新
	 *
	 * @param schemaName 模式名
	 * @param tableName  表名
	 * @param nodeType   节点类型
	 * @param refreshParent   是否刷新指定节点的父节点
	 * @throws Exception 
	 */
	public void refreshNode(String nodeId) throws Exception {
		if(nodeId!=null)
			tree.getLeftDoubleHandler().refreshNode(nodeId);
	}

	public CsTree getTree() {
		return tree;
	}
	
	private HeaderConfig newHeaderConfig(String name){
		HeaderConfig headerConfig = new HeaderConfig(name);
		headerConfig.setTitleEditable(false);
		headerConfig.setFixTab(true);
		headerConfig.setDetachEnabled(false);
		return headerConfig;
	}
}