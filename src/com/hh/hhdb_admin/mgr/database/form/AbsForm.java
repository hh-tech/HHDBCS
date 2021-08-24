package com.hh.hhdb_admin.mgr.database.form;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;

import java.sql.Connection;

/**
 * @author YuSai
 */
public abstract class AbsForm {

    public static AbsForm getForm(Connection conn, DBTypeEnum dbTypeEnum, String user) throws Exception {
        switch (dbTypeEnum) {
            case hhdb:
            case pgsql:
                return new HhPgForm(conn, dbTypeEnum, user);
            case mysql:
                return new MysqlForm(conn);
            case sqlserver:
                return new SQLServerForm(conn, user);
            case db2:
                return new Db2Form();
            default:
                return null;
        }
    }

    HGridPanel getWithLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C3);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    public abstract HPanel getPanel();

    public abstract JsonObject getData();

    public abstract void reset();
}
