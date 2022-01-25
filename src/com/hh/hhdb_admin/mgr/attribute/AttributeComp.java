package com.hh.hhdb_admin.mgr.attribute;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.attributeMr.AttrMrNode;
import com.hh.frame.create_dbobj.attributeMr.mr.AbsAttributeMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.textEditor.base.ThemesEnum;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;

/**
 * @author: Jiang
 * @date: 2020/10/14
 */

public class AttributeComp {

    public static final String DOMAIN_NAME = AttributeComp.class.getName();

    static {
        try {
            LangMgr2.loadMerge(AttributeComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAttr(JsonObject msg, JdbcBean jdbcBean, Connection conn) throws Exception {
        AbsAttributeMr attributeMr = AbsAttributeMr.genAttrMr(jdbcBean);
        if (attributeMr == null) {
            return;
        }
        HDialog dialog = new HDialog(StartUtil.parentFrame, 800, 600);
        HTable table = new HTable();
        AttrMrNode attrMrNode = new AttrMrNode(msg.getString("databaseName"), msg.getString("schemaName"),
                msg.getString("tableName"), msg.getString("name"), msg.getString("oid"),
                TreeMrType.valueOf(msg.getString("nodeType")));
        attributeMr.getTableHeader(attrMrNode).forEach((k, v) -> table.addCols(new DataCol(k, v)));

        HTextArea textArea = new HTextArea(false, false);
        String createSql = attributeMr.getCreateSql(attrMrNode, conn);
        textArea.setText(createSql);
        textArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);
        if(HHSwingUi.isDarkSkin()) {
            textArea.setTheme(ThemesEnum.monokai);
        }
        HSplitPanel splitPanel = new HSplitPanel(false);
        splitPanel.setSplitWeight(0.6);
        LastPanel panelOne = new LastPanel(false);
        panelOne.setWithScroll(table.getComp());
        splitPanel.setLastComp4One(panelOne);

        LastPanel panelTwo = new LastPanel(false);
        panelTwo.set(textArea.getComp());
        splitPanel.setLastComp4Two(panelTwo);

        dialog.setRootPanel(splitPanel);
        dialog.setWindowTitle(LangMgr2.getValue(DOMAIN_NAME, "attribute"));
        dialog.setIconImage(IconFileUtil.getLogo());
        table.load(attributeMr.getAttribution(attrMrNode, conn), 0);
        table.setRowHeight(25);

        if (StringUtils.isBlank(createSql)) {
            splitPanel.setDividerLocation(600);
            splitPanel.setOneTouchBtnShow(false);
        }

        dialog.show();
    }

}
