package com.hh.hhdb_admin.mgr.main_frame;

import java.io.File;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.swingui.cs_desk.lockScreen.LockTool;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.engine2.GuiShareObjInf;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.tab_panel.HMgrTabPanel;
import com.hh.frame.swingui.view.hmenu.HMenuBar;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BarVisibelBean;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.db_task2.TaskMgr;
import com.hh.hhdb_admin.mgr.query2.QueryMgr;
import com.hh.hhdb_admin.mgr.redisPubSub.RedisPubSubMgr;
import com.hh.hhdb_admin.mgr.tree.CsTreeComp;

/**
 * @author Jiang
 * @date 2020/10/10
 */

public class MainFrameMgr extends AbsGuiMgr implements GuiShareObjInf {
	private MainFrameComp frameComp;
	private MainFrameConf conf=null;

	@Override
	public void init(JsonObject jObj) {
		if(jObj!=null) {
			conf=new MainFrameConf(jObj);
		}
		frameComp = (MainFrameComp) CsVarUtil.parentFrame;
	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.MAIN_FRAME;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		frameComp = (MainFrameComp) CsVarUtil.parentFrame;
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case INIT:
				init(frameComp);
				frameComp.maximize();
				//frameComp.show();
				frameComp.getWindow().setVisible(true);
				break;
			case CLOSE_ALL_TAB:
				frameComp.closeAllTab();
				//重新登录数据库时初始化对象
				TaskMgr.sharePaneObj = null;
				break;
			case SET_TOOLBAR_VISIBLE:
				frameComp.setToolbarVisible(new BarVisibelBean(bj.getJobj()).getVisible());
				break;
			case SET_STATUSBAR_VISIBLE:
				frameComp.setStatusVisible(new BarVisibelBean(bj.getJobj()).getVisible());
				break;
			case DISPOSE:
				frameComp.dispose();
				break;
			case LOCK:
				LockTool lock = new LockTool(frameComp);
				lock.lock();
				break;
			default:
				break;
		}
		return null;
	}

	@Override
	public BaseJsonBean shareObj(Object obj) throws Exception {
		SharePaneObj share = (SharePaneObj)obj;
		if (share.getCsMgrEnum() == CsMgrEnum.TOOLBAR) {
			frameComp.setToolBar((HBarPanel) share.getObj());
		} else if (share.getCsMgrEnum() == CsMgrEnum.MENUBAR) {
			frameComp.setMenubar((HMenuBar) share.getObj());
		} else if (share.getCsMgrEnum() == CsMgrEnum.TREE) {
			frameComp.setTree((CsTreeComp) share.getObj());
		} else {
			frameComp.addTabPaneItem(share);
		}
		return null;
	}
	
	/**
	 * 初始化主面板
	 *
	 * @param frameComp 主面板
	 * @throws Exception e
	 */
	private void init(MainFrameComp frameComp) throws Exception {
		if(conf==null || conf.getShowMenu()) {
			//初始化menubar
			CsVarUtil.eng2.doCall(CsMgrEnum.MENUBAR, new BaseOpBean(BaseOpEnum.INIT));
		}
		
		if(conf==null || conf.getShowToolbar()) {
//			//初始化toolbar
			CsVarUtil.eng2.doCall(CsMgrEnum.TOOLBAR, new BaseOpBean(BaseOpEnum.INIT));
		}
	
		if(conf==null || conf.getShowStatus()) {
			//初始化状态栏
			BottomStatusBar statusBar = new BottomStatusBar();
			frameComp.setStatusPanel(statusBar);
		}
		
		if(conf==null || conf.getShowTabbedPane()) {
			//初始化中间内容
			HMgrTabPanel tabPane = new HMgrTabPanel(){
				@Override
				public boolean onConfirmClose(String id) {
					return frameComp.quTabPaneClose(id);
				}
				@Override
				public void onClose(String id) {
					super.onClose(id);
					frameComp.onTabPaneClose(id);
				}
				@Override
				protected void onSelected(String id) {
					frameComp.setTextAreaFocus(id);
				}
			};
			frameComp.setRootPanel(tabPane);
		}
		
		if(conf==null || conf.getShowTree()) {
			//初始化树组件
			CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new BaseOpBean(BaseOpEnum.INIT));
		}
		
		frameComp.setStatusVisible(CsVarUtil.CONF.isShowStatusBar());
		frameComp.setToolbarVisible(CsVarUtil.CONF.IsShowToolBar());
		//查询器临时文件路径
		WinMgr.workDir = new File(CsVarUtil.workspace, QueryMgr.WSQLTMP);
		//打开表临时文件路径
//		TableOpenMgr.tmpFile = new File(CsVarUtil.workspace, TableOpenMgr.OPEN_TMP);
		//发布订阅临时文件路径
		RedisPubSubMgr.tmpFile = new File(CsVarUtil.workspace, RedisPubSubMgr.PUBSUB_TMP);
		//初始化弹出框父面板
		PopDlgUtil.window = CsVarUtil.parentFrame;
	}
}
