package com.hh.hhdb_admin.mgr.table_space2.form;

import java.sql.Connection;
import java.util.List;

import com.hh.frame.lang2.conf.Word2;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.TabSpaceSqlUtil;
import com.hh.frame.db_sql.db2.info.Db2TableSpaceBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;

public class Db2TableSpaceForm extends AbsTableSpaceForm {
    private final SelectBox bufferPool;
    private final SelectBox autoresize;

    public Db2TableSpaceForm(Connection conn, JdbcInfo jdbc)throws Exception {
        super(conn,jdbc);
        bufferPool = new SelectBox();
        autoresize = new SelectBox();
    
        List<String> list = SqlQueryUtil.selectOneColumn(conn, "select bp_name from sysibmadm.snapbp");
        bufferPool.addOption("","");
        list.forEach(a -> bufferPool.addOption(a, a));
        autoresize.addOption("","");
        autoresize.addOption("YES","YES");
        autoresize.addOption("NO","NO");
    }

    @Override
    public LastPanel getLastPanel() {
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(50);
        HPanel panel = new HPanel(layout);
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word3.BUFFER_POOL)+" ：", bufferPool));
        panel.add(getLabelInput(LangMgr.sLang(FrameL.AUTO_EXTENSIBLE)+" ：", autoresize));
        panel.add(new HeightComp(5));
        panel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
    
        LastPanel areaLast = new LastPanel();
        areaLast.setTitle(LangMgr.lang(Word2.CODE));
        areaLast.set(area.getComp());
        
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(panel.getComp());
        lastPanel.set(areaLast.getComp());
        return lastPanel;
    }
    
    @Override
    public String checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            return LangMgr.sLang(CsL.PLEASE_FILL_NAME);
        }
        return null;
    }
    
    @Override
    public String getSql() {
        String name = SqlStrUtil.rmQuote4Sel(nameInput.getValue(),dbType, vers);
        Db2TableSpaceBean bean = new Db2TableSpaceBean(name,bufferPool.getValue(),autoresize.getValue());
        return TabSpaceSqlUtil.getCreateSql(dbType,vers,bean);
    }
    
}
