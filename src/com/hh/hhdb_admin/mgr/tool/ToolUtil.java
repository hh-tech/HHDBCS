package com.hh.hhdb_admin.mgr.tool;

import com.hh.frame.common.util.db.SelectTableSqlUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ouyangxu
 * @date 2021-12-16 0016 16:43:11
 */
public class ToolUtil {
	public static String sqlFormatBreakWidth = "50";

	public static List<HFrame> hFrames;

	public static HFrame initFrame(Enum<?> k) {
		HFrame frame = new HFrame(HFrame.LARGE_WIDTH);
		frame.setWindowTitle(LangMgr.sLang(k));
		frame.setIconImage(IconFileUtil.getLogo());
		frame.getWindow().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		if (hFrames == null) {
			hFrames = new ArrayList<>();
		}
		hFrames.add(frame);
		return frame;
	}

	public static void closeFrame(HFrame frame) {
		if (PopDlgUtil.confirm(frame,LangMgr.sLang(CsL.CLOSE_WINDOW))) {
			frame.dispose();
			if (hFrames != null) {
				hFrames.remove(frame);
			}
		}
	}

	public static boolean verifyBreakWidth(String value) {
		PopDlgUtil.window = CsVarUtil.parentFrame;
		if (SelectTableSqlUtil.isNumeric(value)) {
			if (Integer.parseInt(value) <= 0) {
				PopDlgUtil.info(LangMgr.sLang(CsL.POSITIVE_NTEGER));
			} else {
				return true;
			}
		} else {
			PopDlgUtil.info(LangMgr.sLang(CsL.POSITIVE_NTEGER));
		}
		return false;
	}

//	public static Cursor setWaitingCursor() {
//		JFrame frame = CsVarUtil.parentFrame.getWindow();
//		Cursor originCursor = frame.getCursor();//Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
//		frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//		return originCursor;
//	}
//
//	public static void restoreCursor(Cursor cursor) {
//		JFrame frame = CsVarUtil.parentFrame.getWindow();
//		frame.setCursor(cursor);
//	}
}
