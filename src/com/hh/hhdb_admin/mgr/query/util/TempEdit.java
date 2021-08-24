package com.hh.hhdb_admin.mgr.query.util;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.dbobj2.DataUtil;
import com.hh.frame.dbquery.QueryTool;
import com.hh.frame.parser.sql_fmt2.StmtFmtTool;
import com.hh.frame.parser.sql_fmt2.base.AbsSqlCode;
import com.hh.frame.parser.sql_fmt2.gen.SqlFmtParser;
import com.hh.frame.sqlwin.WinMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模版编辑面板
 * @author hexu
 */
public class TempEdit {
	private HDialog parentDig;
    
    /**
     *  模版编辑面板
     * @param value 编辑内容
     * @param jdbc
     * @param sql   用户运行的sql
     * @param hint  提示词
     * @param parentDig
     */
    public TempEdit(String value, JdbcBean jdbc,String sql,String hint,HDialog parentDig) {
        this.parentDig = parentDig;
        String str = StringUtils.isNotBlank(value) ? value : geneTemplate(jdbc,sql);
        init(str,hint);
    }
    
    /**
     * 模版编辑面板
     * @param value 编辑内容
     * @param sql   用户运行的sql
     * @param hint  提示词
     * @param parentDig
     */
    public TempEdit(String value,String sql,String hint,HDialog parentDig) {
        this.parentDig = parentDig;
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
            PopPaneUtil.error(parentDig.getWindow(),e.getMessage());
        }finally {
            HTextArea textArea = new HTextArea(false, true);
            textArea.getArea().getTextArea().setLineWrap(true);
            textArea.showBookMask(false);
            textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);
            textArea.setText(StringUtils.isNotBlank(string) ? string : sql);
    
            HDialog dialog = new HDialog(parentDig,800, 530);
            HBarPanel hTool = new HBarPanel();
            String finalString = string;
            hTool.add(new HButton("确定") {
                @Override
                public void onClick() {
                    save(textArea.getArea().getTextArea().getText());
                    dialog.dispose();
                }
            }, new HButton("重置") {
                @Override
                public void onClick() {
                    textArea.setText(StringUtils.isNotBlank(finalString) ? finalString : sql);
                }
            });
    
            HPanel hp = new HPanel();
            hp.add(textArea);
            hp.add(new LabelInput(hint, AlignEnum.LEFT),hTool);
    
            dialog.setIconImage(IconFileUtil.getLogo());
            dialog.setWindowTitle("模版");
            dialog.setRootPanel(hp);
            dialog.show();
        }
    }
    
    /**
     * 根据查询语句获取模版
     */
    private String geneTemplate(JdbcBean jdbc,String sql) {
        String res = "";
        String exportFile = WinMgr.workDir + File.separator + new Date().getTime() + File.separator;  //数据临时文件夹
        Connection conn = null;
        try {
            conn = ConnUtil.getConn(jdbc);
            QueryTool expdata = new QueryTool(conn, sql, new File(exportFile), 1);
            expdata.first();
            Map<String, String> defMap = new LinkedHashMap<>();
            expdata.getColNames().forEach(a -> defMap.put(a,a));
            res = DataUtil.getInsertVm(defMap, jdbc.getSchema(), "new_table");
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(parentDig.getWindow(),e.getMessage());
        } finally {
            try {
                if (new File(exportFile).exists()) FileUtils.forceDelete(new File(exportFile));
                ConnUtil.close(conn);
            } catch (Exception e) {
                PopPaneUtil.error(parentDig.getWindow(),e);
            }
        }
        return res;
    }
    
    protected void save(String sql){
    
    }
}
