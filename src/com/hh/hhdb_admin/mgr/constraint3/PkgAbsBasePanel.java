package com.hh.hhdb_admin.mgr.constraint3;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.lang3.StringUtils;

abstract class PkgAbsBasePanel extends LastPanel {
	protected String tabName;
	protected String schema;
	protected HTextArea sqlTextArea = getSqlTextArea();

	public PkgAbsBasePanel(String schema,String tabName) {
		this.tabName = tabName;
		this.schema = schema;
	}

	protected LastPanel getSqlPanel(HTextArea area) {
		LastPanel sqlPanel = new LastPanel();
		sqlPanel.setTitle(LangMgr.lang(Word2.CODE));
		sqlPanel.set(area.getComp());
		return sqlPanel;
	}

	protected HTextArea getSqlTextArea() {
		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		return new HTextArea(conf);
	}

	/**
	 * 保存完成后通知其它组件
	 */
	protected abstract void inform();

	/**
	 * 生成代码
	 * 
	 * @param colList 选中的列
	 * @return
	 */
	protected abstract String genSql();

	protected void check() throws Exception {

	}

	protected HButton createSaveButton() {
		HButton button = new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
			@Override
			protected void onClick() {
				try {
					if (StringUtils.isBlank(sqlTextArea.getText())) {
						genSql2Area();
					}
					if (GlobalWorkUtil.exeSql(CsVarUtil.jdbc,CsVarUtil.conn, sqlTextArea.getText())) {
						PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
						inform();
					}
				} catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		};
		return button;
	}

	protected HButton createViewSqlButton() {
		HButton button = new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
			@Override
			protected void onClick() {
				try {
					check();
					genSql2Area();
				} catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		};
		return button;
	}

	private void genSql2Area() throws Exception {
		String sql = genSql();
		if (StringUtils.isBlank(sql)) {
			throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
		}
		sqlTextArea.setText(sql);
	}

	protected HBarLayout getLayout(AlignEnum align) {
		HBarLayout barLayout = HBarLayout.newLayout(align);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);
		return barLayout;
	}

	protected HBarPanel getBarPanel() {
		HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
		barPanel.add(createViewSqlButton());
		barPanel.add(createSaveButton());
		return barPanel;
	}
}
