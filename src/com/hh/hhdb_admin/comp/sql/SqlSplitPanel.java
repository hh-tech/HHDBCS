package com.hh.hhdb_admin.comp.sql;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaFRDlg;
import com.hh.frame.swingui.view.textarea2.dlg.HTAreaGotoDlg;
import com.hh.frame.swingui.view.textarea2.sql.HSqlArea;
import com.hh.frame.swingui.view.textarea2.sql.SqlAreaConf;
import com.hh.frame.swingui.view.textarea2.sql.SqlConfDlg;

import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SqlSplitPanel extends HSplitPanel implements Closeable{
	
	PkgSqlRsPanel rsPanel=null;
	PkgSqlTopPanel topPanel=null;
	
	protected void callDbTask(String schemaName, String sql) {
		StringBuilder sb=new StringBuilder();
		sb.append(LangMgr.lang(Word2.SCHEMA)+"："+schemaName).append("\n");
		sb.append("SQL："+sql);
		PopDlgUtil.info(comp, sb.toString());
	}
	
	public SqlSplitPanel(SqlAreaConf conf, JdbcBean jdbc) throws Exception{
		super(false);
		rsPanel=new PkgSqlRsPanel(jdbc){
            @Override
            protected void highlighted(String title, Map<String, List<Integer>> resultMap) {
                try {
                    topPanel.getHarea().getRSyntaxTextArea().removeAllLineHighlights();
                    List<Integer> li = resultMap.get(title);
                    if (null != li) {
                        for (Integer integer : li) {
                            if (topPanel.getHarea().getRSyntaxTextArea().getLineCount() - 1 >= integer) {
                                if (LafMgr.getCurrent() == LafIdEnum.DARK) {
                                    topPanel.getHarea().getRSyntaxTextArea().addLineHighlight(integer, new Color(10, 10, 100));
                                } else {
                                    topPanel.getHarea().getRSyntaxTextArea().addLineHighlight(integer, new Color(255, 255, 170));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Lg.error(getClass(), e);
                }
            }

			@Override
			void doExport(String schemaName, String sql) {
				callDbTask(schemaName,sql);
			}
            
        };
		topPanel=new PkgSqlTopPanel(conf,jdbc,rsPanel) ;
		super.setLastComp4One(topPanel);
		super.setPanelTwo(rsPanel);
		super.setSplitWeight(0.7);
	}

	public void setCfgDlg(SqlConfDlg dlg) {
		topPanel.setCfgDlg(dlg);
	}
	public void setFRDlg(HTAreaFRDlg dlg) {
		topPanel.setFRDlg(dlg);
	}
	public void setGotoDlg(HTAreaGotoDlg dlg) {
		topPanel.setGotoDlg(dlg);
	}
	
	@Override
	public void close() throws IOException {
		topPanel.close();
	}
	
	public String getText(){
		return topPanel.harea.getText();
	}
	
	public void setText(String text) {
		topPanel.harea.setText(text);
	}
	
	public HSqlArea getHarea() {
		return topPanel.getHarea();
	}
	
	protected void openOtherDialog() {
		
	};
}
