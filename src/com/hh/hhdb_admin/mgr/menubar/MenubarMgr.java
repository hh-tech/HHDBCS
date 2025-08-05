package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.cs_desk.about.AboutComp;
import com.hh.frame.swingui.cs_desk.license2.LicComp;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

/**
 * @author: Jiang
 * @date: 2020/10/12
 */
public class MenubarMgr extends AbsGuiMgr {

	@Override
	public void init(JsonObject jObj) {
	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.MENUBAR;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case INIT:
				SharePaneObj obj = new SharePaneObj();
				obj.setCsMgrEnum(CsMgrEnum.MENUBAR);
				obj.setObj(new MenubarComp());
				CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, obj);
				break;
			case SHOW_LICENSE:
				LicComp comp = new LicComp(CsVarUtil.parentFrame.getWindow(),CsVarUtil.licMgr,false);
				comp.show();
				break;
			case SHOW_ABOUT:
				AboutComp aboutComp = new AboutComp(IconFileUtil.getTheme(),CsVarUtil.parentFrame.getWindow());
				HDialog dialog = new HDialog(CsVarUtil.parentFrame.getWindow(),800,500,false);
				dialog.setWindowTitle(LangMgr.lang(Word2.ABOUT));
				dialog.setStatusBar(new HBarPanel());
				dialog.setRootPanel(aboutComp.getPanel());
				dialog.show();
				break;
			default:
				break;
		}
		return null;
	}
}
