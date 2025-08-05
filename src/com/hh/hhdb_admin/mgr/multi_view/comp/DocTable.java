package com.hh.hhdb_admin.mgr.multi_view.comp;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.FrameL;
import com.hh.frame.mongo.obj.MongoDocUtil;
import com.hh.frame.swingui.cs_desk.jsonEditor.JsonEditorPanel;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.file_panel.HBinaryPanel;
import com.hh.frame.swingui.view.input.DateTimePickerInput;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.exp.TabExpUtil;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonType;
import org.bson.BsonValue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.*;

public class DocTable extends HTable {

    private final HashMap<String, BsonDocument> docMap;

    public DocTable(HashMap<String, BsonDocument> docMap) {
        super();
        this.docMap = docMap;
        setColumnSelectionAllowed(true);
        setCellEditable(false);

        jTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2)
                    view();
            }
        });
    }

    public void selectCell(MouseEvent e) {
        int row = jTab.rowAtPoint(e.getPoint());
        int col = jTab.columnAtPoint(e.getPoint());
        if (row != -1)
            jTab.setRowSelectionInterval(row, row);
        if (col != -1)
            jTab.setColumnSelectionInterval(col, col);
    }

    public void editRow() {
    	 int row = jTab.getSelectedRow();
    	 String id = getRowBean(row).getOldRow().get(MongoDocUtil.DOC_ID);
    	System.out.println(id);
    	/*
		MongoDocAddUpdateBean b=new MongoDocAddUpdateBean(id);
		b.setDbName(schemaName);
		b.setCollName(tableName);
		b.setDocId(name);
		sendMsg(CsMgrEnum.MONGO_MGR, b);
		*/
    }
    
    protected DocBean getSelectedDocBean() {
        int row = jTab.getSelectedRow();
        int column = jTab.getSelectedColumn();
        if (row != -1 && column != -1) {
            String value = jTab.getValueAt(row, column).toString();
            BsonType type = BsonType.STRING;
            if (StringUtils.isNotBlank(value))
                type = MongoDocUtil.getBsonType(value);
            String id = getRowBean(row).getOldRow().get(MongoDocUtil.DOC_ID);
            String key = StringUtils.substringBeforeLast(jTab.getColumnName(column), "(");
            String content = jTab.getValueAt(row, column).toString();
            return new DocBean(id, type, key, content);
        }
        return null;
    }

    public List<String> getNavi() {
        return new ArrayList<>();
    }

    public void view() {
        DocBean docBean = getSelectedDocBean();
        if (docBean == null || MongoDocUtil.DOC_ID.equals(docBean.key))
            return;

        List<String> navi = getNavi();
        navi.add(docBean.key);
        BsonValue obj = MongodbViewComp.getNaviObj(docMap.get(docBean.id), navi);
        if (obj == null)
            obj = new BsonString("");

        Component comp;
        HTAreaConf conf = new HTAreaConf();
        String text;
        switch (docBean.type) {
            case DOCUMENT:
            case ARRAY:
                try {
                    comp = new JsonEditorPanel(MongoDocUtil.getJson(obj), true).getComp();
                } catch (Exception e) {
                    PopDlgUtil.info(this.getComp(), LangMgr.sLang(FrameL.JSON_PARSE_ERROR));
                    return;
                }
                break;
            case DATE_TIME:
            case TIMESTAMP:
                DateTimePickerInput dateInput = new DateTimePickerInput("DateTime");
                dateInput.setDateTimeFormatter(DateTimeFormatter.ISO_INSTANT.withZone(null));
                dateInput.setDate(Date.from(MongoDocUtil.getInstant(obj)));

                comp = dateInput.getComp();
                break;
            case BINARY:
                HBinaryPanel BinaryPanel = new HBinaryPanel();
                byte[] data = obj.asBinary().getData();
                try {
                    BinaryPanel.loadBinaryFile(new ByteArrayInputStream(data));
                } catch (Exception e) {
                    PopDlgUtil.info(this.getComp(), LangMgr.sLang(FrameL.ERROR));
                    return;
                }

                text = new String(Base64.getEncoder().encode(data));
                conf.setLineWrap(true);
                LastPanel base64Panel = new LastPanel();
                base64Panel.set(getShowTextComp(text, conf));

                HTabPanel hTabPanel = new HTabPanel();
                hTabPanel.setPanel("1", base64Panel, TableUtil.newHeaderConfig("base64"));
                hTabPanel.setPanel("2", BinaryPanel, TableUtil.newHeaderConfig("Binary"));
                hTabPanel.selectPanel("2");

                comp = hTabPanel.getComp();
                break;
            case JAVASCRIPT:
                conf.setDocType(TAreaDocType.SYNTAX_STYLE_TYPESCRIPT);
                text = obj.asJavaScript().getCode();
                comp = getShowTextComp(text, conf);
                break;
            default:
                comp = getShowTextComp(MongoDocUtil.getShowText(obj));
                break;
        }
        show(docBean.key, comp);
    }

    private static Component getShowTextComp(String text) {
        return getShowTextComp(text, null);
    }

    private static Component getShowTextComp(String text, HTAreaConf conf) {
        HTextArea hTextArea = new HTextArea(conf);
        hTextArea.setText(text);
        return hTextArea.getComp();
    }

    protected void show(String title, Component comp) {
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(comp);
        HDialog dialog = new HDialog(this.getComp(), 800, 600, false);
        dialog.setRootPanel(lastPanel);
        dialog.setStatusBar(new HBarPanel());
        dialog.setWindowTitle(title);
        dialog.show();
    }

    @Override
    protected JTable initJTable() {
        return new JTable() {
            @Override
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                int row = this.rowAtPoint(e.getPoint());
                int col = this.columnAtPoint(e.getPoint());

                if (row > -1 && col > -1)
                    tip = this.getValueAt(row, col).toString();
                if (StringUtils.isBlank(tip))
                    return null;
                return MongoDocUtil.getBsonType(tip).toString();
            }
        };
    }

    public void copySelectedCell() {
        String content = MongoDocUtil.getContent(getSelectedDocBean().content);
        TabExpUtil.copyToClipboard(content);
    }

}

