package com.hh.hhdb_admin.mgr.trigger2;

import java.io.IOException;
import java.sql.Connection;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.db_sql.conn.TriggerConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;

public class TriggerEditPanel extends LastPanel {
	private Connection conn;
	private JdbcBean jdbcBean;
	private String schema;
	private String oldText;
	
	private HTextArea area;
	private HDialog dialog;
	
	
    public TriggerEditPanel(Connection conn, JdbcBean jdbcBean, String schema) {
    	this.conn = conn;
    	this.jdbcBean = jdbcBean;
    	this.schema = schema;
		dialog = new HDialog(CsVarUtil.parentFrame, 800, 600);
        HBarLayout layout = new HBarLayout();
        layout.setTopHeight(10);
        layout.setBottomHeight(10);
        layout.setAlign(AlignEnum.LEFT);
	
		HButton saveButton = new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK) {
			@Override
			protected void onClick() {
				if (GlobalWorkUtil.exeSql(jdbcBean, conn, area.getText())) {
					setEnabled(false);
					PopDlgUtil.info(dialog, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
					dialog.dispose();
				}
			}
		};
		saveButton.setEnabled(false);
		HButton formatButton = new HButton(LangMgr.lang(Word3.FORMAT), FaSolidEnum.WAND_MAGIC_SPARKLES) {
			@Override
			protected void onClick() {
				GlobalWorkUtil.formatSql(area);
			}
		};
		HBarPanel  barPanel = new HBarPanel(layout);
		barPanel.add(saveButton,formatButton);
  
		HTAreaConf config = new HTAreaConf();
		config.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        area = new HTextArea(config);
		area.getRSyntaxTextArea().addCaretListener(e -> saveButton.setEnabled(!oldText.equals(area.getText())));
		
		setHead(barPanel.getComp());
		set(area.getComp());
    }
	
	/**
	 * 修改用户 显示
	 */
	public void show(String name) {
		try {
			oldText = TriggerConnUtil.selectUpdateTriggerSql(CsVarUtil.getDbtype(), jdbcBean.getCurrentVersion(),conn, schema,name);
			area.setText(oldText);
			
			dialog.setWindowTitle(LangMgr.lang(Word2.MODIFY) + "(" + name + ")");
			dialog.setRootPanel(this);
			dialog.setStatusBar(new HBarPanel());
			dialog.show();
		} catch (Exception e) {
			PopDlgUtil.logErr(TriggerEditPanel.class, CsVarUtil.parentFrame, e);
		}
	}
    
	public static void main(String[] args) throws Exception {
		try {
			LangMgr.addLang(CsL.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JdbcInfo bean = new JdbcInfo();
		bean.setClazz(DriverUtil.ORA_DRIVER);
		bean.setUser("TSP");
		bean.setPassword("123456");
		bean.setDbUrl("jdbc:oracle:thin:@192.168.2.253:1521/orcl");
		
		CsVarUtil.conn = ConnUtil.getConn(bean);
		CsVarUtil.jdbc = bean;
		TriggerEditPanel p = new TriggerEditPanel(CsVarUtil.conn,CsVarUtil.jdbc,"TSP");
		p.show("ZZZZ");
	}
}
