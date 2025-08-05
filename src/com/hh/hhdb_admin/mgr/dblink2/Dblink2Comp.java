package com.hh.hhdb_admin.mgr.dblink2;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.JUrlUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.db_sql.info.SqlCreateDbLinkBean;
import com.hh.frame.db_sql.ora.OraDblinkSqlUtil;
import com.hh.frame.db_sql.ora.info.OraDblinkInfo;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.*;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HBorderLayout;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import org.apache.commons.lang3.StringUtils;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public abstract class Dblink2Comp extends LastPanel {
    private HTextArea area;
    private  String dblinkName;
    private final String schema;
    private final Connection conn;
    private final HDialog dialog;
    private TextInput ownerInput;
    private TextInput nameInput;
    private CheckBoxInput publicInput;
    private TextInput usernameInput;
    private PasswordInput userPassInput;
    private TextInput databaseInput;
    private TextInput addressInput;
    private TextInput serviceNameInput;
    private IntSpinner portInput;


    public Dblink2Comp(Connection conn, String schema, String dblinkName) {
        this.conn = conn;
        this.schema = schema;
        this.dblinkName = dblinkName;
        dialog = new HDialog(CsVarUtil.parentFrame, 630, 350);
        dialog.setWindowTitle(LangMgr.lang(StringUtils.isEmpty(dblinkName) ? Word2.ADD : Word2.EDIT));
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setStatusBar(new HBarPanel());
        initPanel();
    }

    public abstract void refreshTree();

    public void show() throws SQLException {
        dialog.setRootPanel(this);
        if (StringUtils.isNotEmpty(dblinkName)) {
        	String owner=StringUtils.substringBefore(dblinkName, ".");
        	String name=StringUtils.substringAfter(dblinkName, ".");
        	String sql=OraDblinkSqlUtil.getDblinkInfoSql(CsVarUtil.jdbc.getCurrentVersion(),OraDblinkInfo.class,false,owner,name);
        	Map<OraDblinkInfo, String> map=SqlQueryUtil.selectOneStrMap( conn, sql,OraDblinkInfo.class);

            ownerInput.setValue(owner);
            nameInput.setValue(map.get(OraDblinkInfo.DB_LINK));
            publicInput.setValue(String.valueOf("PUBLIC".equals(owner)));
            usernameInput.setValue(map.get(OraDblinkInfo.USERNAME));
            databaseInput.setValue(map.get(OraDblinkInfo.HOST));
        }
        dialog.show();
    }

    public void initPanel() {
        nameInput = new TextInput("name");
        ownerInput = new TextInput("owner", schema);
        publicInput = new CheckBoxInput("public");
        publicInput.getComp().addItemListener(e -> ownerInput.setValue(publicInput.isChecked() ? "PUBLIC" : schema));

        HButton saveBtn = new HButton(LangMgr.lang(Word2.SAVE),FaSolidEnum.FLOPPY_DISK) {
            @Override
            protected void onClick() {
                save();
            }
        };
        HButton sqlPreview = new HButton(LangMgr.lang(Word2.PREVIEW),FaSolidEnum.MAGNIFYING_GLASS) {
            @Override
            protected void onClick() {
                code();
            }
        };

        HBarPanel hBarPanel=new HBarPanel(HBarLayout.newLayout(AlignEnum.LEFT));
        nameInput = new TextInput();
        nameInput.getComp().setColumns(20);
        hBarPanel.add( new LabelInput(LangMgr.lang(Word2.NAME) + ":"));
        hBarPanel.add(nameInput);
        hBarPanel.add( new LabelInput(LangMgr.lang(Word2.PUBLIC)+":"));
        hBarPanel.add(publicInput);
        hBarPanel.add(sqlPreview, saveBtn);

        HGridPanel grid = new HGridPanel(new HGridLayout(GridSplitEnum.C6));

        HPanel pLeft = new HPanel(new HDivLayout(GridSplitEnum.C2));
        usernameInput = new TextInput("username", CsVarUtil.jdbc.getUser());
        userPassInput = new PasswordInput();
        databaseInput = new TextInput();
        pLeft.add(new LabelInput(LangMgr.lang(Word3.USERNAME),AlignEnum.LEFT),usernameInput);
        pLeft.add(new LabelInput(LangMgr.lang(Word2.PASSWORD),AlignEnum.LEFT),userPassInput);
        pLeft.setTitle((LangMgr.lang(Word4.USER_INFO) + "："));


        DBTypeEnum dbtype=CsVarUtil.getDbtype(); 

        HPanel pRight = new HPanel();

        HBarPanel adrressPanel = new HBarPanel(HBarLayout.newLayout(AlignEnum.RIGHT));
        addressInput = new TextInput();
        serviceNameInput = new TextInput("servicename",JUrlUtil.getDbName(dbtype, CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.jdbc.getDbUrl()));

        portInput = new IntSpinner(0,0,null,1);
        

        String port= JUrlUtil.getPort(dbtype, CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.jdbc.getDbUrl());
        if(StringUtils.isNumeric(port))
        	portInput.setValue(Integer.valueOf(port));
        

        String ip=JUrlUtil.getIp(dbtype,  CsVarUtil.jdbc.getCurrentVersion(), CsVarUtil.jdbc.getDbUrl());
        addressInput.setValue(ip);

        adrressPanel.add(portInput,60);
        adrressPanel.setLastHComp(addressInput,5,AlignEnum.CENTER);

        HGridPanel upPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C2));
        upPanel.setComp(1, new LabelInput(LangMgr.lang(Word2.ADDRESS),AlignEnum.LEFT));
        upPanel.setComp(2, adrressPanel);

        HGridPanel downPanel = new HGridPanel(new HGridLayout(GridSplitEnum.C2));
        downPanel.setComp(1, new LabelInput(LangMgr.lang(Word3.SERVICENAME),AlignEnum.LEFT));
        downPanel.setComp(2, serviceNameInput);

        pRight.add(upPanel, downPanel);
        pRight.setTitle(LangMgr.lang(Word4.HOST_INFO)+"：");

        grid.setComp(1, pLeft);
        grid.setComp(2, pRight);
        HBorderPanel borderPanel = new HBorderPanel();
        borderPanel.set(HBorderLayout.PositionType.NORTH, hBarPanel);
        borderPanel.set(HBorderLayout.PositionType.CENTER, grid);


        setHead(borderPanel.getComp());

        HTAreaConf conf = new HTAreaConf();
        conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
        conf.setLineWrap(true);  //自动换行
        area = new HTextArea(conf);

        HPanel areaPanel = new HPanel();
        LastPanel areaLast = new LastPanel();
        areaLast.set(area.getComp());
        areaPanel.setLastPanel(areaLast);
        areaLast.setTitle(LangMgr.sLang(CsL.SQL_AREA));
        ((JDialog) dialog.getWindow()).setResizable(true);
        set(areaPanel.getComp());

    }


    protected void code() {
        try {
            check();
            String name=nameInput.getValue();
            String userName=usernameInput.getValue();
            String password=userPassInput.getValue();
            String ip=addressInput.getValue();
            String port=portInput.getValue();
            boolean isPub=publicInput.isChecked();
            SqlCreateDbLinkBean dblink=new SqlCreateDbLinkBean(name,userName,password,ip);
            dblink.setPort(Integer.valueOf(port));
            dblink.setPublic(isPub);
            String sql=OraDblinkSqlUtil.getNewDblinkSql(dblink);
            if (sql == null)
                throw new Exception(LangMgr.lang(Word4.NOT_SUPPORT));
            area.setText(sql);
        } catch (Exception e) {
            PopDlgUtil.error(getComp(), e);
            Lg.error(getClass(), e);
        }
    }
    public void check() throws Exception {
    }
    /**
     * 执行sql域的代码，没有时调用{@code code()}
     */
    public void save() {
        try {
            String sql = area.getText();
            if (StringUtils.isBlank(sql))
                code();
            sql = area.getText();
            if (StringUtils.isBlank(sql) || GlobalWorkUtil.exeSql(CsVarUtil.jdbc, CsVarUtil.conn, sql)) {
                PopDlgUtil.info(getComp(), LangMgr.sLang(FrameL.TIP_SAVE_SUCCESS));
                refreshTree();
            }
        } catch (Exception e) {
            PopDlgUtil.error(getComp(), e);
            Lg.error(getClass(), e);
        }
    }



}
