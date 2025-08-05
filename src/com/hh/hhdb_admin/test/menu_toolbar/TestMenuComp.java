package com.hh.hhdb_admin.test.menu_toolbar;
import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.base.JdbcInfo.View;
import com.hh.frame.json.JsonArray;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.engine2.GuiMgrArrBean;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.conf.CsConf;
import com.hh.hhdb_admin.mgr.menubar.MenubarComp;
import com.hh.hhdb_admin.mgr.toolbar.ToolbarComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestMenuComp {
	public static void main(String[] args) throws Exception {
		JsonObject jsonConf=new JsonObject();
		JsonArray arr=new JsonArray();
		jsonConf.set("mgrs", arr);
		GuiMgrArrBean bean = new GuiMgrArrBean(jsonConf);
		CsVarUtil.eng2=new PkgMenuGuiEngine(PkgEnum.class,bean);
		CsVarUtil.CONF=new CsConf();
		CsVarUtil.jdbc = new JdbcInfo(MgrTestUtil.getJdbcBean().getJobj());
		//这里切换是dba菜单还是普通用户菜单
		CsVarUtil.jdbc.setView(View.DBA);
		
		LangMgr.addLang(CsL.class);
		LafMgr.setLaf(LafIdEnum.FAST);
		CsVarUtil.parentFrame = new HFrame(HFrame.MIDDLE_WIDTH);
		CsVarUtil.parentFrame.setMenubar(new MenubarComp());
		HBarLayout layout=new HBarLayout();
		layout.setAlign(AlignEnum.LEFT);
		CsVarUtil.parentFrame.setToolBar(new ToolbarComp(layout));
		CsVarUtil.parentFrame.maximize();
		
		CsVarUtil.parentFrame.show();
	}
}
