package com.hh.hhdb_admin.mgr.main_frame;

import com.hh.frame.json.JsonObject;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.engine.GuiMsgType;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitTabPanel;
import com.hh.frame.swingui.view.hmenu.HMenuBar;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.menubar.MenubarMgr;
import com.hh.hhdb_admin.mgr.toolbar.ToolbarMgr;
import com.hh.hhdb_admin.mgr.tree.TreeComp;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Jiang
 * @date 2020/10/10
 */

public class MainFrameMgr extends AbsGuiMgr {
    public static final String PARAM_ID = "id";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_MGR_TYPE = "mgrType";
    public static final String CMD_SHOW = "showMainFrame";
    public static final String CMD_CLOSE_ALL_TAB = "closeAllTabPane";
    public static final String SET_TREE_VISIBLE = "setTreeVisible";
    public static final String SET_MENUBAR_VISIBLE = "setMenubarVisible";
    public static final String SET_TOOLBAR_VISIBLE = "setToolbarVisible";
    public static final String SET_STATUS_VISIBLE = "setStatusVisible";
    public static final String ADD_TAB_PANE_ITEM = "addTabPaneItem";
    public static final String SWITCH_SCHEMA = "switchSchema";
    private MainFrameComp frameComp;

    @Override
    public void init(JsonObject jObj) {
        frameComp = (MainFrameComp)StartUtil.parentFrame;
    }

    @Override
    public String getHelp() {
        return GuiJsonUtil.genCmdHelp(ADD_TAB_PANE_ITEM, "添加组件实例到tab pane，需要3个参数，PARAM_ID(对象id)、PARAM_TITLE(tab标题)、PARAM_MGR_TYPE(对象所属的组件类型)",
                GuiMsgType.RECE);
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.MAIN_FRAME;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_SHOW:
                init(frameComp);
                frameComp.show();
                frameComp.maximize();
                break;
            case CMD_CLOSE_ALL_TAB:
                frameComp.closeAllTab();
                frameComp.getTabPane().reset();
                break;
            case SET_TREE_VISIBLE:
                frameComp.setTreeVisible(msg.getBoolean("visible"));
                break;
            case SET_MENUBAR_VISIBLE:
                frameComp.setMenubarVisible(msg.getBoolean("visible"));
                break;
            case SET_TOOLBAR_VISIBLE:
                frameComp.setToolbarVisible(msg.getBoolean("visible"));
                break;
            case SET_STATUS_VISIBLE:
                frameComp.setStatusVisible(msg.getBoolean("visible"));
                break;
            case ADD_TAB_PANE_ITEM:
                frameComp.addTabPaneItem(msg);
                break;
            case SWITCH_SCHEMA:
                JsonObject treeRes = StartUtil.eng.doCall(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_INIT));
                Object treeCompObj = StartUtil.eng.getSharedObj(treeRes.getString("id"));
                frameComp.setTree((TreeComp) treeCompObj);
                break;
            default:
                unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
//        if (ObjType.DIALOG.name().equals(GuiJsonUtil.getShareIdType(msg))) {
//            Boolean isClose = msg.getBoolean(IS_CLOSE_PROGRAM_ONCLOSE_DIALOG);
//            isCloseProgram = isClose != null && isClose;
//            frameComp.initDialog();
//            return GuiJsonUtil.toJsonSharedId(frameComp.getDialogId());
//        }
        unknowMsg(msg.toPrettyString());
        return new JsonObject();
    }

    /**
     * 初始化主面板
     *
     * @param frameComp 主面板
     * @throws Exception e
     */
    private void init(MainFrameComp frameComp) throws Exception {
        //初始化menubar
        JsonObject menubarRes = StartUtil.eng.doCall(CsMgrEnum.MENUBAR, GuiJsonUtil.toJsonCmd(MenubarMgr.CMD_INIT));
        Object menubarObj = StartUtil.eng.getSharedObj(menubarRes.getString("id"));
        frameComp.setMenubar((HMenuBar) menubarObj);

        //初始化toolbar
        JsonObject toolbarRes = StartUtil.eng.doCall(CsMgrEnum.TOOLBAR, GuiJsonUtil.toJsonCmd(ToolbarMgr.CMD_INIT));
        Object toolbarObj = StartUtil.eng.getSharedObj(toolbarRes.getString("id"));
        frameComp.setToolBar((HBarPanel) toolbarObj);

        //初始化中间内容
        JsonObject treeRes = StartUtil.eng.doCall(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_INIT));
        Object treeCompObj = StartUtil.eng.getSharedObj(treeRes.getString("id"));
        HSplitTabPanel tabPane = new HSplitTabPanel(frameComp) {
            @Override
            public void onClose(String id) {
            	super.onClose(id);
                frameComp.onTabPaneClose(id);
            }
        };
        frameComp.setRootPanel((TreeComp) treeCompObj, tabPane);

        //初始化临时文件存储路径
        WinMgr.workDir = new File(StartUtil.workspace, "wsqltmp");
        //清除查询临时文件
        if (WinMgr.workDir.exists()) {
            FileUtils.cleanDirectory(WinMgr.workDir);
        }
    }
}
