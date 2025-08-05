package com.hh.hhdb_admin.mgr.usr2.form;


import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.db_sql.info.SqlUserBean;
import com.hh.frame.db_sql.info.UsrItem;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.usr2.UsrBean;

import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;

public abstract class UsrBaseForm {
    protected Connection conn;
    protected UsrBean usrBean;
    
    public TextInput nameInput;
    public PasswordInput pwInput;
    public HTextArea area;
    protected GrantTableComp roleGranteComp;
    protected HTabPanel hTabPanel;
    
    protected SqlUserBean oldUser;
    
    
    public UsrBaseForm(UsrBean usrBean, Connection conn) {
        try {
            this.conn = conn;
            this.usrBean = usrBean;
    
            roleGranteComp = new GrantTableComp();
            
            nameInput = new TextInput();
            nameInput.getComp().setColumns(20);
            pwInput = new PasswordInput(UsrItem.usr_pwd.name());
            pwInput.getComp().setColumns(20);
    
            HTAreaConf conf = new HTAreaConf();
            conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
            conf.setLineWrap(true);
            area = new HTextArea(conf);
        } catch (Exception e) {
            PopDlgUtil.error(CsVarUtil.parentFrame, e);
        }

    }
    
    public Dimension getSize() {
        return new Dimension(750, 550);
    }
    
    public abstract LastPanel getLastPanel();
    
    /**
     * 检查form
     */
    public abstract String checkForm();

    /**
     * 获取sql
     */
    public abstract String getSql();
    
    /**
     * 修改用户时初始化form
     */
    public abstract void upInitForm(String usrName) throws Exception;
    
    public abstract void inform();

    protected HDivLayout getLayout() {
        HDivLayout layout = new HDivLayout(GridSplitEnum.C4);
        layout.setMaxWidth(600);
        return layout;
    }

    protected WithLabelInput getLabelInput(String label, AbsInput input) {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C4, AlignEnum.RIGHT,AlignEnum.LEFT);
        return new WithLabelInput(labelConf, new LabelInput(label), input);
    }

    /**
     * 是否符合日期格式
     */
    protected boolean isValidDate(String str) {
        boolean convertSuccess = true;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            format.parse(str);
        } catch (Exception e) {
            convertSuccess = false;
        }
        return convertSuccess;
    }
    
    protected HeaderConfig newHeaderConfig(String name){
        HeaderConfig headerConfig = new HeaderConfig(name);
        headerConfig.setTitleEditable(false);
        headerConfig.setFixTab(true);
        headerConfig.setDetachEnabled(false);
        return headerConfig;
    }
    
    /**
     * 根据截取usr@host的usr
     */
    protected static String subMysqlUsr(String userHost) {
        if(userHost.contains("@")){
            return userHost.substring(0,userHost.lastIndexOf("@"));
        }
        return userHost;
    }
}
