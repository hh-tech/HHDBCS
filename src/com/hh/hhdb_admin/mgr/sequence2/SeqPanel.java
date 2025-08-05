package com.hh.hhdb_admin.mgr.sequence2;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.SeqSqlUtil;
import com.hh.frame.db_sql.info.SqlSeqBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HBorderPanel;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.BigDecimalSpinner;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HBorderLayout.PositionType;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
/**
 * 数据库序列面板
 */
public class SeqPanel extends LastPanel {
	private HTextArea area;
	private SqlSeqBean b =null;
	private SqlSeqBean old =null;
	private TextInput nameInput;
	
	private BigDecimalSpinner startInput ;
	private BigDecimalSpinner incrementInput;
	private BigDecimalSpinner maxInput;
	private BigDecimalSpinner minInput;
	
	private CheckBoxInput cycleBox ;
	private CheckBoxInput orderBox;
	private BigDecimalSpinner cacheInput;
	private TextInput hiddenInput;
	private DBTypeEnum dbtype=CsVarUtil.getDbtype();
	public SeqPanel() {
		HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.LEFT));
		nameInput = new TextInput();
		nameInput.getComp().setColumns(20);
		barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"));
		barPanel.add(nameInput);
		barPanel.add(new HButton(LangMgr.lang(Word2.CODE),FaSolidEnum.MAGNIFYING_GLASS) {
			@Override
			protected void onClick() {
				try {
					if(check()) {
						genSql2Area();
					}
				}catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		});
		
		barPanel.add(new HButton(LangMgr.lang(Word2.SAVE),FaSolidEnum.FLOPPY_DISK) {
			@Override
			protected void onClick() {
				try {
					if(StringUtils.isBlank(area.getText())) {
						if(check()) {
							genSql2Area();
						}else {
							return;
						}
					}
					
					
					if(!StringUtils.isBlank(area.getText())) {
						if(GlobalWorkUtil.exeSql(CsVarUtil.jdbc,CsVarUtil.conn, area.getText())) {
							PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
						}
						inform();
					}
					
					
					
				}catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		});

		
		
		
		HGridPanel grid = new HGridPanel(new HGridLayout(GridSplitEnum.C6));
		
		
		
		
		HPanel pLeft = new HPanel(getLayout());
		startInput = new BigDecimalSpinner();
		incrementInput = new BigDecimalSpinner();
		maxInput = new BigDecimalSpinner();
		minInput = new BigDecimalSpinner();
		pLeft.add(new LabelInput(LangMgr.lang(Word3.START_VALUE)),startInput);
		pLeft.add(new LabelInput(LangMgr.lang(Word2.INCREASE)),incrementInput);
		pLeft.add(new LabelInput(LangMgr.lang(Word3.MAX_VALUE)),maxInput);
		pLeft.add(new LabelInput(LangMgr.lang(Word3.MIN_VALUE)),minInput);
		pLeft.setTitle("基础");
		
		HPanel pRight = new HPanel(getLayout());

        cycleBox = new CheckBoxInput("cycle");
		orderBox = new CheckBoxInput("order");
		cacheInput = new BigDecimalSpinner();
		hiddenInput = new TextInput();
		hiddenInput.setVisible(false);
		pRight.add(new LabelInput(LangMgr.lang(Word2.CACHE)),cacheInput);
		pRight.add(new LabelInput(LangMgr.lang(Word2.CYCLE)),cycleBox);
		pRight.add(new LabelInput(LangMgr.lang(Word2.ORDER)),orderBox);
		
		
		
		pRight.add(new LabelInput(""),hiddenInput);
		pRight.setTitle("其它");
		grid.setComp(1, pLeft);
		grid.setComp(2, pRight);
		HBorderPanel borderPanel = new HBorderPanel();
		borderPanel.set(PositionType.NORTH, barPanel);
		borderPanel.set(PositionType.CENTER, grid);
		
		setHead(borderPanel.getComp());
		
		
		
		HTAreaConf conf = new HTAreaConf();
		conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		conf.setLineWrap(true);
		area = new HTextArea(conf);
		
		HPanel areaPanel = new HPanel();
		LastPanel areaLast = new LastPanel();
		areaLast.set(area.getComp());
		areaPanel.setLastPanel(areaLast);
		areaLast.setTitle(LangMgr.lang(Word2.CODE));
		set(areaPanel.getComp());
	}
	protected void inform() {
		System.out.println("通知更新");
	}
	
	private HBarLayout getLayout(AlignEnum align) {
		HBarLayout barLayout  = HBarLayout.newLayout(align);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);
		return barLayout;
	}
	private HDivLayout getLayout() {
		HDivLayout layout = new HDivLayout(GridSplitEnum.C3);
		layout.setTopHeight(10);
		layout.setyGap(8);
		layout.setBottomHeight(10);
		return layout;
		
	}
	
	private void genSql2Area() throws Exception {
		String sql = "";
		if(nameInput.isEnabled()) {
			sql = SeqSqlUtil.getCreateSeqSql( dbtype, CsVarUtil.jdbc.getCurrentVersion(), getFormData());
			
		}else {
			if(getFormData().equals(old)) {
				area.setText("");
				PopDlgUtil.info(getComp(),"没有更改数据");
				return;
			}
			sql = SeqSqlUtil.getUpdateSeqSql( dbtype, CsVarUtil.jdbc.getCurrentVersion(), old,getFormData());
		}
		if(StringUtils.isBlank(sql)) {
			area.setText("");
			PopDlgUtil.info(getComp(),"暂不支持");
			return;
		}
		
		
		area.setText(sql);
	}
	
	
	private void reset() {
		SqlSeqBean bean = new SqlSeqBean("");
		load(bean);
	}
	
	
	private void load(SqlSeqBean bean) {
		
		nameInput.setValue(bean.getSeqName());
		minInput.setValue(bean.getMinValue());
		maxInput.setValue(bean.getMaxValue());
		startInput.setValue(bean.getStartWith());
		incrementInput.setValue(bean.getIncrement());
		cacheInput.setValue(bean.getCache());
		cycleBox.setValue(bean.getCycle()==null?Boolean.FALSE.toString():String.valueOf(bean.getCycle()));
		
		boolean isOrderSupport = SeqSqlUtil.isSeqSupportOrder(dbtype, CsVarUtil.jdbc.getCurrentVersion());
		if(isOrderSupport) {
			orderBox.setValue(bean.getOrder()==null?Boolean.FALSE.toString():String.valueOf(bean.getOrder()));
		}
		
		
		orderBox.setEnabled(isOrderSupport);
	}
	
	/**
	 * 用于添加时schema参数
	 * @param schema
	 */
	public void init(String schema) {
		reset();
		old = null;
		String idxName=String.format("seq_%s_%s", schema.toLowerCase(),System.currentTimeMillis());
		nameInput.setValue(idxName);
		b=new SqlSeqBean(SqlStrUtil.rmQuote4Sel(idxName, dbtype, null));
		b.setSchemaName(schema);
		startInput.setEnabled(true);
		nameInput.setEnabled(true);
	}
	
	
	/**
	 * 用于修改时载入数据
	 * @param b
	 */
	public void init(SqlSeqBean b) {
		reset();
		old = b.clone();
		this.b = b;
		load(b);
		nameInput.setEnabled(false);
	}
	
	private SqlSeqBean getFormData() {
		
//		b.setSeqName(nameInput.getValue());
		b.setSeqName(SqlStrUtil.rmQuote4Sel(nameInput.getValue(), dbtype, null));
		b.setStartWith(startInput.getBigDecimal());
		if(cycleBox.isEnabled()) {
			b.setCycle(cycleBox.getComp().isSelected());
		}else {
			b.setCycle(null);
		}
		
		if(orderBox.isEnabled()) {
			b.setOrder(orderBox.getComp().isSelected());
		}else {
			b.setOrder(null);
		}
		
		
		if(cacheInput.isEnabled()) {
			b.setCache(cacheInput.getBigDecimal());
		}else {
			b.setCache(null);
		}
		
		b.setIncrement(incrementInput.getBigDecimal());
		b.setMaxValue(maxInput.getBigDecimal());
		b.setMinValue(minInput.getBigDecimal());
		
		return b;
	}
	
	private boolean check() throws Exception {
		if(StringUtils.isBlank(nameInput.getValue())) {
			PopDlgUtil.info(getComp(), "请填写名称");
			return false;
		}
		
		return true;
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
		SeqPanel p = new SeqPanel();
		p.init("HU");
		frame.setRootPanel(p);
		frame.show();
	}

}
