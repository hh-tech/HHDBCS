package com.hh.hhdb_admin.mgr.schema2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.db_sql.SchemaSqlUtil;
import com.hh.frame.db_sql.info.SqlSchemaUpdateBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SchemaPanel extends LastPanel {

	private TextInput nameInput;
	private TextAreaInput commentArea;
	private HTextArea area;
	private SqlSchemaUpdateBean old;
	private SqlSchemaUpdateBean b;

	public SchemaPanel() {

		commentArea = new TextAreaInput();
		if (CsVarUtil.getDbtype() == DBTypeEnum.hana) commentArea.setEnabled(false);

		HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
		nameInput = new TextInput();
		nameInput.getComp().setColumns(20);
		barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"));
		barPanel.add(nameInput);
		barPanel.add(new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
			@Override
			protected void onClick() {
				try {
					if (check()) {
						genSql2Area();
					}

				} catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		});

		barPanel.add(new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
			@Override
			protected void onClick() {
				try {
					if (StringUtils.isBlank(area.getText())) {
						if (check()) {
							genSql2Area();
						}
					}

					if (!StringUtils.isBlank(area.getText())) {
						if (GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, area.getText())) {
							PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
						}

						inform();
					}
				} catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		});

		setHead(barPanel.getComp());

		HSplitPanel split = new HSplitPanel(false);

		LastPanel lastOne = new LastPanel();
		lastOne.set(commentArea.getComp());
		lastOne.setTitle(LangMgr.lang(Word2.COMMENT));
		split.setLastComp4One(lastOne);

		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		conf.setLineWrap(true);
		area = new HTextArea(conf);

		LastPanel areaLast = new LastPanel();
		areaLast.set(area.getComp());
		areaLast.setTitle(LangMgr.lang(Word2.CODE));

		split.setLastComp4Two(areaLast);

		set(split.getComp());

	}

	/**
	 * 添加
	 * 
	 * @param ownner
	 */
	public void init(String ownner) {
		old = null;
		this.b = new SqlSchemaUpdateBean("");
		nameInput.setEnabled(true);
		this.b.setOwner(ownner);
		load(b);
	}

	/**
	 * 修改
	 * 
	 * @param b
	 */
	public void init(SqlSchemaUpdateBean b) {
		
		old = b.clone();
		this.b = b;
		nameInput.setEnabled(
				SchemaSqlUtil.isSchemaNameSupportEdit(CsVarUtil.getDbtype(), CsVarUtil.jdbc.getCurrentVersion()));
		load(b);
	}

	protected void inform() {

	}

	private void genSql2Area() {
		List<String> sql;
		if (old == null) {
			sql = SchemaSqlUtil.getCreateSchemaSqls(CsVarUtil.getDbtype(),CsVarUtil.jdbc.getCurrentVersion(), getFormData());
		} else {
			if (getFormData().equals(old)) {
				area.setText("");
				PopDlgUtil.info(getComp(), "没有更改数据");
				return;
			}
			sql = SchemaSqlUtil.getUpdateSchemaSqls(DriverUtil.getDbType(CsVarUtil.jdbc),
					CsVarUtil.jdbc.getCurrentVersion(), old, getFormData());
		}
		if (sql.isEmpty() || sql == null) {
			PopDlgUtil.info(getComp(), "暂不支持");
			return;
		}

		String s = StringUtils.join(sql, "\n");
		area.setText(s);
	}

	private void load(SqlSchemaUpdateBean b) {
		nameInput.setValue(b.getSchemaName());
		commentArea.setValue(b.getComment());
	}

	private SqlSchemaUpdateBean getFormData() {
		b.setSchemaName(nameInput.getValue());
		b.setComment(commentArea.getValue());
		return b;
	}

	private boolean check() {
		if (StringUtils.isBlank(nameInput.getValue())) {
			PopDlgUtil.info(getComp(), "请填写名称");
			return false;
		}
		return true;
	}

	private HBarLayout getLayout(AlignEnum align) {
		HBarLayout barLayout = HBarLayout.newLayout(align);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);
		return barLayout;
	}

}
