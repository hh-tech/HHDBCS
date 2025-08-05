package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.parser.sql_fmt2.StmtFmtTool;
import com.hh.frame.parser.sql_fmt2.base.AbsSqlCode;
import com.hh.frame.parser.sql_fmt2.gen.SqlFmtParser;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 模版编辑面板
 */
public class TempDlog {
    private HDialog parentDig,dialog;
    
    private String standardSql;     //标准sql
    
    /**
     * 模版编辑面板
     * @param value 用户输入的sql
     * @param sql   标准sql
     * @param hint  提示词
     * @param parentDig 父面板
     */
    public TempDlog(String value, String sql, String hint, HDialog parentDig) {
        this.parentDig = parentDig;
        this.standardSql = sql;
        String str = StringUtils.isNotBlank(value) ? value : sql;
        init(str,hint);
    }
    
    private void init(String sql,String hint){
        if (StringUtils.isBlank(sql)) return;
    
        //格式化
        String string = sql;
        try {
            StringBuffer sb = new StringBuffer();
            SqlFmtParser parser=new SqlFmtParser(sql);
            List<AbsSqlCode> list=parser.toCodeList();
            StmtFmtTool fmtTool=new StmtFmtTool(list);
            fmtTool.fmt2Lines().forEach(a -> sb.append(a+"\n"));
            string = sb.toString().trim();
        } catch (Exception e) {
            Lg.error(TempDlog.class, e);
        }
    
        HTextArea textArea = new HTextArea();
        textArea.getRSyntaxTextArea().setLineWrap(true);
        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        textArea.setConf(conf);
        textArea.setText(StringUtils.isNotBlank(string) ? string : sql);
    
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
                dialog.dispose();
            }
        }, new HButton(LangMgr.lang(Word2.RESET)) {  //重置
            @Override
            public void onClick() {
                textArea.setText(standardSql);
            }
        });
    
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(textArea.getComp());
        lastPanel.setFoot(new LabelInput(hint, AlignEnum.LEFT).getComp());
        dialog = new HDialog(parentDig,800,400);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(LangMgr.lang(Word2.TEMPLATE));
        dialog.setRootPanel(lastPanel);
        dialog.setStatusBar(hTool);
        dialog.show();
    }
    
    protected void save(String sql){
    
    }
    
}
