package com.hh.hhdb_admin.comp.sql;

import java.util.List;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.parser.sql_fmt2.StmtFmtTool;
import com.hh.frame.parser.sql_fmt2.base.AbsSqlCode;
import com.hh.frame.parser.sql_fmt2.gen.SqlFmtParser;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;

public class SqlVmEditorDlg extends HDialog {
	private String originVm=null;
	public SqlVmEditorDlg(String vm, HWindow parent) throws Exception {
		super(parent,800,400);
		
		StringBuffer sb = new StringBuffer();
        SqlFmtParser parser=new SqlFmtParser(vm);
        List<AbsSqlCode> list=parser.toCodeList();
        StmtFmtTool fmtTool=new StmtFmtTool(list);
        fmtTool.fmt2Lines().forEach(a -> sb.append(a+"\n"));
        originVm = sb.toString().trim();
        HTextArea textArea = new HTextArea();
        textArea.getRSyntaxTextArea().setLineWrap(true);
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        textArea.setConf(conf);
        textArea.setText(originVm);
    
        HBarLayout layout = new HBarLayout();
        layout.setAlign(AlignEnum.CENTER);
        layout.setTopHeight(20);
        layout.setBottomHeight(10);
        layout.setxGap(20);
        HBarPanel hTool = new HBarPanel(layout);
        hTool.add(new HButton(LangMgr.lang(Word2.OK)) {  //确定
            @Override
            public void onClick() {
                save(textArea.getText());
                dispose();
            }
        }, new HButton(LangMgr.lang(Word2.RESET)) {  //重置
            @Override
            public void onClick() {
                textArea.setText(originVm);
            }
        });
    
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(textArea.getComp());
        setRootPanel(lastPanel);
        setStatusBar(hTool);
	}
	
    protected void save(String sql){
        
    }
}
