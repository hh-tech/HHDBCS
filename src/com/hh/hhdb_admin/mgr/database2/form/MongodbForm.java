package com.hh.hhdb_admin.mgr.database2.form;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.common.CsL;

import java.sql.Connection;


public class MongodbForm extends AbsForm {
    private final TextInput clNmaeInput;
    
    public MongodbForm(JdbcBean jdbc, Connection conn) throws Exception {
        super(jdbc,conn);
        clNmaeInput = new TextInput("COLLECTION_NAME");
    }
    
    @Override
    public HPanel getPanel() {
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        hPanel = new HPanel(divLayout);
        hPanel.setTitle(LangMgr.sLang(CsL.BASIC_SETTING));
        hPanel.add(getWithLabelInput(LangMgr.lang(Word4.COLLECTION_NAME) + "* :", clNmaeInput));
        hPanel.add(new HPanel());
        return hPanel;
    }
    
    @Override
    public String getSql() {
        return clNmaeInput.getValue();
    }
    
}
