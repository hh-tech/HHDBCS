package com.hh.hhdb_admin.mgr.index2;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.db_sql.IndexSqlUtil;
import com.hh.frame.db_sql.info.SqlIndexBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.comp.colsel.ColSelPanel;

public class IndexPanel extends LastPanel {
	private CheckBoxInput input1;
	private CheckBoxInput input2;
	private TextInput nameInput;
	private HTextArea area;
	private ColSelPanel selPanel;
	private SqlIndexBean b = null;
	private DBTypeEnum type = DriverUtil.getDbType(CsVarUtil.jdbc);
	private DBTypeEnum.Version ver = CsVarUtil.jdbc.getCurrentVersion();
	public IndexPanel() {
		nameInput = new TextInput();
		nameInput.getComp().setColumns(25);
		HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));

		barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"));
		barPanel.add(nameInput);
		if(IndexSqlUtil.supportUniqueIdx(type, ver)) {
			input1 = new CheckBoxInput("box1", "唯一索引");
			barPanel.add(input1);
		}
		if(IndexSqlUtil.supportReverseIdx(type, ver)) {
			input2 = new CheckBoxInput("box2", "反向索引");
			barPanel.add(input2);
		}
	
		
		barPanel.add(createViewSqlButton());
		barPanel.add(createSaveButton());

		setHead(barPanel.getComp());
		selPanel = new ColSelPanel();
		HSplitPanel split = new HSplitPanel(false);
		split.setLastComp4One(selPanel);
		LastPanel last = new LastPanel();

		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		area = new HTextArea(conf);
		last.set(area.getComp());

		HBarPanel bar = new HBarPanel(getLayout(AlignEnum.LEFT));
		bar.add(new LabelInput(LangMgr.lang(Word2.CODE) + ":", AlignEnum.LEFT));
		last.setHead(bar.getComp());
		split.setLastComp4Two(last);

		split.setDividerLocation(400);
		set(split.getComp());
	}

	private HButton createSaveButton() {
		HButton button = new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
			@Override
			protected void onClick() {
				try {

					if (StringUtils.isBlank(area.getText())) {
						if (check()) {
							genSql2Area();
						}else {
							return;
						}
					}
					if (GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, area.getText())) {
						PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
					}

					inform();

				} catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		};
		return button;
	}

	private HButton createViewSqlButton() {
		HButton button = new HButton(LangMgr.lang(Word2.CODE), FaSolidEnum.MAGNIFYING_GLASS) {
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
		};
		return button;
	}

	private void genSql2Area() throws Exception {
		String sql = IndexSqlUtil.getCreateSampleIdxSql(type,ver, getFormData());
		if (StringUtils.isBlank(sql)) {
			throw new Exception("暂不支持");
		}
		area.setText(sql);
	}

	private void reset() {
		nameInput.setValue("");
		if(input1!=null)input1.setValue(false);
		if(input2!=null)input2.setValue(false);
	}

	private SqlIndexBean getFormData() {
		b.setIndexName(nameInput.getValue());
		if(input1!=null)b.setUnique(Boolean.valueOf(input1.getValue()));
		if(input2!=null)b.setReverse(Boolean.valueOf(input2.getValue()));
		b.setColNames(selPanel.getSelected());
		return b;
	}

	private HBarLayout getLayout(AlignEnum align) {
		HBarLayout barLayout = HBarLayout.newLayout(align);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);
		return barLayout;
	}

	private boolean check() {
		if (StringUtils.isBlank(nameInput.getValue())) {
			PopDlgUtil.info(getComp(), "请填写索引名称");
			return false;
		}

		if (selPanel.getSelected().isEmpty()) {
			PopDlgUtil.info(getComp(), "请选择索引列");
			return false;
		}

		return true;

	}

	protected void inform() {
		System.out.println("通知更新");
	}

	public void init(String tabName, String schema) throws Exception {
		reset();
		String idxName = SqlIndexBean.getDefaultIndexName(schema, tabName);
		nameInput.setValue(idxName);
		b = new SqlIndexBean(tabName, idxName);
		b.setSchemaName(schema);

//		input1.setEnabled(IndexSqlUtil.supportUniqueIdx(type, v));
//		input2.setEnabled(IndexSqlUtil.supportReverseIdx(type, v));
		selPanel.loadData(CsVarUtil.jdbc, CsVarUtil.conn, schema, tabName, null);
	}

	public static void main(String[] args) throws Exception {

		try {
			LangMgr.addLang(CsL.class);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JdbcInfo bean = new JdbcInfo();
		bean.setClazz(DriverUtil.ORA_DRIVER);
		bean.setUser("hu");
		bean.setPassword("123456");
		bean.setDbUrl("jdbc:oracle:thin:@192.168.2.253:1521/orcl");

		CsVarUtil.conn = ConnUtil.getConn(bean);
		CsVarUtil.jdbc = bean;
		HFrame frame = new HFrame() {
			@Override
			public void dispose() {

				try {
					CsVarUtil.conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.dispose();
			}
		};
		IndexPanel p = new IndexPanel();
		p.init("MYTABLE", "HU");
		frame.setRootPanel(p);
		frame.show();
	}

}
