package com.hh.hhdb_admin.mgr.query.util;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.parser.sql_fmt2.StmtFmtTool;
import com.hh.frame.parser.sql_fmt2.base.AbsSqlCode;
import com.hh.frame.parser.sql_fmt2.gen.SqlFmtParser;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.query.QueryMgr;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 模版编辑面板
 * @author hexu
 */
public class TempEdit {
    private HDialog parentDig,dialog;
    
    private String standardSql;     //标准sql
    
    /**
     * 模版编辑面板
     * @param value 用户输入的sql
     * @param sql   标准sql
     * @param hint  提示词
     * @param parentDig 父面板
     */
    public TempEdit(String value,String sql,String hint,HDialog parentDig) {
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
            List<AbsSqlCode> list=parser.allCodeList();
            StmtFmtTool fmtTool=new StmtFmtTool(list);
            fmtTool.fmt2Lines().forEach(a -> sb.append(a+"\n"));
            string = sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        HTextArea textArea = new HTextArea(false, true);
        textArea.getArea().getTextArea().setLineWrap(true);
        textArea.showBookMask(false);
        textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);
        textArea.setText(StringUtils.isNotBlank(string) ? string : sql);
    
        dialog = new HDialog(parentDig,800){
            @Override
            protected void closeEvent() {
                dispose();
            }
        };
        HBarPanel hTool = new HBarPanel();
        hTool.add(new HButton(QueryMgr.getLang("determine")) {  //确定
            @Override
            public void onClick() {
                save(textArea.getArea().getTextArea().getText());
                dialog.dispose();
            }
        }, new HButton(QueryMgr.getLang("reset")) {  //重置
            @Override
            public void onClick() {
                textArea.setText(standardSql);
            }
        });
    
        HPanel hp = new HPanel();
        hp.add(textArea);
        hp.add(new LabelInput(hint, AlignEnum.LEFT),hTool);
    
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(QueryMgr.getLang("template"));
        dialog.setRootPanel(hp);
        dialog.setSize(800,hp.getHeight()+50);      //根据实际大小设置弹出框大小
        dialog.show();
    }
    
    protected void save(String sql){
    
    }
    
}
