package com.hh.hhdb_admin.mgr.modifyinfo;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.hhdb_admin.comp.BaseSqlPanel;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;

public abstract class BaseModifyPanel extends BaseSqlPanel {
    protected final String schemaName;
    protected final String tableName;
    protected TextInput newNameInput;
    protected TextAreaInput newCommentArea;
    protected String oldComment;

    public BaseModifyPanel(String schemaName, String tableName) {
        super();
        this.schemaName = schemaName;
        this.tableName = tableName;
    }

    @Override
    protected void initPanel() throws SQLException {
        super.initPanel();

        GridSplitEnum splitEnum = GridSplitEnum.C1;
        AlignEnum labelAnEnum = AlignEnum.LEFT;

        newNameInput = new TextInput();
        HBarPanel barPanel = createBarPanel();
        barPanel.setLastHComp(newNameInput);
        HGridPanel barGridPanel = new HGridPanel(new HGridLayout(splitEnum));
        barGridPanel.setComp(1, new LabelInput(LangMgr.lang(Word2.NAME) + ":", labelAnEnum));
        barGridPanel.setComp(2, barPanel);

        newCommentArea = new TextAreaInput();
        HGridPanel commentGridPanel = new HGridPanel(new HGridLayout(splitEnum));
        commentGridPanel.setComp(1, new LabelInput(LangMgr.lang(Word2.TABLE_COMMENT) + ":", labelAnEnum));
        commentGridPanel.setComp(2, newCommentArea);

        HPanel infoPanel = new HPanel();
        infoPanel.add(barGridPanel, commentGridPanel);
        infoPanel.setTitle(LangMgr.lang(Word2.INFO));
        setHead(infoPanel.getComp());

        LastPanel sqlPanel = createSqlPanel();
        set(sqlPanel.getComp());
    }

    @Override
    public void check() throws Exception {
        super.check();
        if (StringUtils.isBlank(newNameInput.getValue()))
            throw new Exception(LangMgr.lang(Word2.TABLE_NAME) + LangMgr.lang(Word4.NOT_NULL));
    }


}
