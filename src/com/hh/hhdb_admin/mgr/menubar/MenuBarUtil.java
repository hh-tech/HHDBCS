package com.hh.hhdb_admin.mgr.menubar;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.fa.FaRegularEnum;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.hmenu.HMenu;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.util.CmdUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CommToolMar;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.Map;

/**
 * @author ouyangxu
 * @date 2022-01-19 0019 14:38:20
 */
public class MenuBarUtil {
	public static final String EXIT = "exit";

	public static final String ABOUT = "about";
	public static final String LICENSE = "license";


	protected static void onMenuItemClick(String action) {
		switch (action) {
			case EXIT:
				if (PopDlgUtil.confirm(LangMgr.sLang(CsL.IS_EXIT))) {
					System.exit(0);
				}
				break;
			case ABOUT:
				CsVarUtil.eng2.doCall(CsMgrEnum.MENUBAR, new BaseOpBean(BaseOpEnum.SHOW_ABOUT));
				break;
			case LICENSE:
				CsVarUtil.eng2.doCall(CsMgrEnum.MENUBAR, new BaseOpBean(BaseOpEnum.SHOW_LICENSE));
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + action);
		}
	}
	
	/**
	 * 获取帮助菜单子项
	 * @param loginFrame 登入页面调用时,登入页面的父面板
	 * @return
	 */
	public static HMenuItem[] getHelpMenuItems(HFrame loginFrame) {
		HMenuItem aboutItem = new HMenuItem(LangMgr.lang(Word2.ABOUT), FaRegularEnum.RECTANGLE_LIST) {
			@Override
			protected void onAction() {
				onMenuItemClick(MenuBarUtil.ABOUT);
			}
		};
		HMenuItem licenseItem = new HMenuItem(LangMgr.lang(Word2.LICENCE),FaSolidEnum.LOCK) {
			@Override
			protected void onAction() {
				onMenuItemClick(MenuBarUtil.LICENSE);
			}
		};
		HMenuItem upgradeItem = new HMenuItem(LangMgr.lang(Word2.UPGRADE), FaSolidEnum.ARROW_ROTATE_LEFT) {
			protected void onAction() {
				if (null != CsVarUtil.parentFrame) {
					if (PopDlgUtil.confirm(LangMgr.sLang(CsL.CLOSE_THE_CLIENT))) {
						CsVarUtil.parentFrame.dispose();
						if (null != loginFrame) loginFrame.dispose();
					} else {
						return;
					}
				}
				try {
					//调用升级程序
					if (SystemUtils.IS_OS_WINDOWS) {
						CmdUtil.deskOpenFile("upgrade.bat");
					} else {
						CmdUtil.runSH(System.getProperty("user.dir")+ File.separator+"upgrade.sh");
					}
				} catch (Exception e) {
					Lg.error(MenuBarUtil.class,e);
					PopDlgUtil.error(CsVarUtil.parentFrame, e);
				} finally {
					System.exit(0);
				}
			}
		};
		
		return new HMenuItem[]{aboutItem, licenseItem,upgradeItem};
	}

	protected static void setItemAction(HMenu windowMenu, CommToolMar commToolMar, Map<String, Map<String, Enum<?>>> windowBtnList) {
		for (String key : windowBtnList.keySet()) {
			Map<String, Enum<?>> map = windowBtnList.get(key);
			for (String s : map.keySet()) {
				HMenuItem menuItem = new HMenuItem(s) {
					@Override
					protected void onAction() {
						commToolMar.onBtnClick(key);
					}
				};
				menuItem.setFontIcon(map.get(s));
				windowMenu.addItem(menuItem);
			}
		}
	}
}
