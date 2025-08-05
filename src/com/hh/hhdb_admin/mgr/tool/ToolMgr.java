package com.hh.hhdb_admin.mgr.tool;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.data2sql.DataToSqlComp;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.mgr.tool.comp.SqlConversionComp;
import com.hh.hhdb_admin.mgr.tool.comp.SqlFormatComp;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author ouyangxu
 * @date 2021-12-17 0017 17:21:53
 */
public class ToolMgr extends AbsGuiMgr {
	@Override
	public void init(JsonObject jObj) {

	}

	@Override
	public Enum<?> getType() {
		return CsMgrEnum.TOOL;
	}

	@Override
	public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
		LastPanel comp = null;
		HFrame frame = null;
		BaseOpBean csGuiBean = new BaseOpBean(bj.getJobj());
		switch (csGuiBean.getOpType()) {
			case SQL_CONVERSION:
				frame = ToolUtil.initFrame(CsL.SQL_CONVERSION);
				comp = new SqlConversionComp(frame);
				break;
			case SQL_FORMAT:
				frame = ToolUtil.initFrame(CsL.SQL_FORMAT);
				comp = new SqlFormatComp(frame);
				break;
			case DATA_TO_SQL:
				frame = ToolUtil.initFrame(CsL.TO_SQL);
				comp = new LastPanel();
				comp.set(new DataToSqlComp().getComp());
				break;
			default:
		}
		showComp(frame, comp);
		return null;
	}

	protected void showComp(HFrame frame, LastPanel lastPanel) {
		if (lastPanel == null) {
			if (null != frame) ToolUtil.closeFrame(frame);
			return;
		}
		frame.getWindow().addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				ToolUtil.closeFrame(frame);
			}
		});
		frame.setRootPanel(lastPanel);
		frame.show();
		frame.getWindow().setLocation(100 + (ToolUtil.hFrames.size() * 50), 100 + (ToolUtil.hFrames.size() * 50));
	}
}
