package com.hh.hhdb_admin.mgr.tablespace.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceComp;

import java.sql.Connection;

/**
 * @author YuSai
 */
public abstract class AbsForm {

    public static AbsForm getForm(Connection conn, DBTypeEnum dbTypeEnum) {
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
                return new HhPgForm(conn, dbTypeEnum);
            case oracle:
                return new OracleForm();
            case db2:
                return new Db2Form(conn);
            default:
                return null;
        }
    }

    HGridPanel getGridPanel(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    public String getSql() {
        return "";
    }

    protected String getLang(String name){
        return TableSpaceComp.getLang(name);
    }

    public abstract HPanel getPanel();

    public abstract JsonObject getData();

    public abstract void reset();

}
