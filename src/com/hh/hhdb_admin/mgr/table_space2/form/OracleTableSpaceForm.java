package com.hh.hhdb_admin.mgr.table_space2.form;

import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.db_sql.TabSpaceSqlUtil;
import com.hh.frame.db_sql.ora.info.OraTableSpaceBean;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.List;
import java.util.*;

public class OracleTableSpaceForm extends AbsTableSpaceForm {
    private final String PATH = "path";
    private final String AUTO_EXTEND = "autoExtend";
    private final String NEXT_SIZE = "nextSize";
    private final String NEXT_SIZE_UNIT = "nextSizeUnit";
    private final String UNLIMITED = "unlimited";
    private final String MAX_SIZE = "maxSize";
    private final String MAX_SIZE_UNIT = "maxSizeUnit";
    
    private HTable table;
    private SelectBox typeSelectBox;
    private TextInput pathInput;
    private SelectBox autoExtendBox;
    private TextInput nextSizeInput;
    private SelectBox nextSizeUnitBox;
    private CheckBoxInput unlimitedInput;
    private TextInput maxSizeInput;
    private SelectBox maxSizeUnitBox;
    
    private final List<String> unitLists = Arrays.asList("", "K", "M", "G", "T", "P", "E");
    private final List<String> onOffLists = Arrays.asList("", "ON", "OFF");
    
    
    public OracleTableSpaceForm(Connection conn, JdbcInfo jdbc) throws Exception {
        super(conn, jdbc);
    
        typeSelectBox = new SelectBox(Arrays.asList("PERMANENT", "TEMPORARY", "UNDO"));
    
        table = new HTable();
        table.hideSeqCol();
        DataCol attrCol = new DataCol("attr", LangMgr.lang(Word2.ATTRIBUTE));
        attrCol.setShow(false);
        table.addCols(new DataCol("name", LangMgr.lang(Word2.NAME)),new DataCol("size", LangMgr.lang(Word2.SIZES)));
        table.addCols(new ListCol("unit", LangMgr.lang(Word2.UNIT), unitLists), attrCol, new BoolCol("online", LangMgr.lang(Word2.ON_LINE))
                , new BoolCol("reuse", LangMgr.lang(Word2.REUSE)));
        table.getComp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                List<HTabRowBean> list = table.getSelectedRowBeans();
                if (list.size() > 0) {
                    JsonObject attr = Json.parse(list.get(0).getCurrRow().get("attr")).asObject();
                    pathInput.setValue(attr.getString(PATH));
                    autoExtendBox.setValue(StringUtils.isEmpty(attr.getString(AUTO_EXTEND)) ? "" : attr.getString(AUTO_EXTEND));
                    nextSizeInput.setValue(attr.getString(NEXT_SIZE));
                    nextSizeUnitBox.setValue(StringUtils.isEmpty(attr.getString(NEXT_SIZE_UNIT)) ? "" : attr.getString(NEXT_SIZE_UNIT));
                    unlimitedInput.getComp().setSelected(attr.getString(UNLIMITED) != null && Boolean.parseBoolean(attr.getString(UNLIMITED)));
                    maxSizeInput.setValue(attr.getString(MAX_SIZE));
                    maxSizeUnitBox.setValue(StringUtils.isEmpty(attr.getString(MAX_SIZE_UNIT)) ? "" : attr.getString(MAX_SIZE_UNIT));
                }
    
                setPathEnabled(list.size() > 0,false);
                setSizeInputEnabled(list.size() > 0,false);
            }
        });
        table.load(new ArrayList<>(), 1);
    
        pathInput = new TextInput() {
            @Override
            protected void doChange() {
                updateAttr(PATH, pathInput.getValue());
            }
        };
    
        autoExtendBox = new SelectBox(onOffLists) {
            @Override
            protected void onItemChange(ItemEvent e) {
                setSizeInputEnabled(!"OFF".equals(this.getValue()),true);
                updateAttr(AUTO_EXTEND, this.getValue());
            }
        };
    
        nextSizeInput = new TextInput(){
            @Override
            protected void doChange() {
                updateAttr(NEXT_SIZE, nextSizeInput.getValue());
            }
        };
        nextSizeUnitBox = new SelectBox(unitLists) {
            @Override
            protected void onItemChange(ItemEvent e) {
                updateAttr(NEXT_SIZE_UNIT, this.getValue());
            }
        };
    
        unlimitedInput = new CheckBoxInput(UNLIMITED){
            @Override
            protected void onClick(ActionEvent e) {
                maxSizeInput.setValue("");
                maxSizeInput.setEnabled(!isChecked());
                maxSizeUnitBox.setValue("");
                maxSizeUnitBox.setEnabled(!isChecked());
                updateAttr(UNLIMITED, String.valueOf(isChecked()));
            }
        };
        maxSizeInput = new TextInput(){
            @Override
            protected void doChange() {
                updateAttr(MAX_SIZE, maxSizeInput.getValue());
            }
        };
        maxSizeUnitBox = new SelectBox(unitLists) {
            @Override
            protected void onItemChange(ItemEvent e) {
                updateAttr(MAX_SIZE_UNIT, this.getValue());
            }
        };
        
        addBtn = new HButton(LangMgr.sLang(CsL.ADD_DATA_FILE),FaSolidEnum.PLUS) {
            @Override
            protected void onClick() {
                Map<String, String> map = new HashMap<>();
                map.put("online", "true");
                map.put("attr", "{}");
                table.add(map);
                
                setPathEnabled(false,true);
                setSizeInputEnabled(false,true);
            }
        };
        delBtn = new HButton(LangMgr.sLang(CsL.DELETE_DATA_FILE),FaSolidEnum.MINUS) {
            @Override
            protected void onClick() {
                if (table.getSelectedRowBeans().size() > 0) {
                    if (PopDlgUtil.confirm(LangMgr.sLang(CsL.SURE_DELETE))) table.deleteSelectRow();
                }
            }
        };
        addBtn.getComp().doClick();
    }
    
    @Override
    public LastPanel getLastPanel() {
        LastPanel tablast = new LastPanel();
        tablast.getComp().setPreferredSize(new Dimension(tablast.getWidth(), 200));
        tablast.setWithScroll(table.getComp());
    
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setMaxWidth(700);
        HPanel panel = new HPanel(layout);
        panel.add(new HeightComp(5));
        panel.add(getLabelInput(LangMgr.lang(Word2.TYPE) + "：", typeSelectBox));
        panel.add(getLabelHPanel(LangMgr.sLang(CsL.DATABASE_FILE)+"：",tablast));
        panel.add(getLabelInput(LangMgr.lang(Word2.PATH) + "：", pathInput));
        panel.add(getLabelInput(LangMgr.lang(Word4.AUTOMATIC_EXTENSION) + "：", autoExtendBox));
        panel.add(getLabelHPanel(LangMgr.sLang(CsL.NEXT_SIZE)+"：",nextSizeInput,nextSizeUnitBox));
        panel.add(getLabelInput(LangMgr.lang(Word3.UNLIMITED) + "：", unlimitedInput));
        panel.add(getLabelHPanel(LangMgr.lang(Word4.MAX_SIZE) + "：",maxSizeInput,maxSizeUnitBox));
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
        List<OraTableSpaceBean.TableSpaceFileBean> list = new ArrayList<>();
        for (HTabRowBean rowBean : table.getRowBeans(RowStatus.ADD)) {
            Map<String, String> rowMap = rowBean.getCurrRow();
            if (StringUtils.isBlank(rowMap.get("name"))) continue;
            JsonObject attrJson = Json.parse(rowMap.get("attr")).asObject();
            
            OraTableSpaceBean.TableSpaceFileBean fileBean = new OraTableSpaceBean.TableSpaceFileBean(rowMap.get("name"), rowMap.get("size"), rowMap.get("unit"));
            fileBean.setOnline(rowMap.get("online"));
            fileBean.setReuse(rowMap.get("reuse"));
            fileBean.setPath(attrJson.getString(PATH));
            fileBean.setAutoExtend(attrJson.getString(AUTO_EXTEND));
            fileBean.setNextSize(attrJson.getString(NEXT_SIZE));
            fileBean.setNextSizeUnit(attrJson.getString(NEXT_SIZE_UNIT));
            fileBean.setUnlimited(attrJson.getString(UNLIMITED));
            fileBean.setMaxSize(attrJson.getString(MAX_SIZE));
            fileBean.setMaxSizeUnit(attrJson.getString(MAX_SIZE_UNIT));
            list.add(fileBean);
        }
        String name = SqlStrUtil.rmQuote4Sel(nameInput.getValue(),dbType, vers);
        OraTableSpaceBean bean = new OraTableSpaceBean(name,typeSelectBox.getValue(),list);
        return TabSpaceSqlUtil.getCreateSql(dbType,vers,bean);
    }
    
    private void updateAttr(String id, String value) {
        List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
        for (HTabRowBean rowBean : rowBeans) {
            Map<String, String> map = rowBean.getCurrRow();
            JsonObject attrJson = Json.parse(map.get("attr")).asObject();
            attrJson.set(id, value);
            map.put("attr", attrJson.toString());
        }
    }
    
    private void setSizeInputEnabled(boolean b, boolean initValue) {
        if ("OFF".equals(autoExtendBox.getValue())) {
            b = false;
        }
        nextSizeInput.setEnabled(b);
        nextSizeUnitBox.setEnabled(b);
        unlimitedInput.setEnabled(b);
        
        if (unlimitedInput.isChecked()) {
            b = false;
        }
        maxSizeInput.setEnabled(b);
        maxSizeUnitBox.setEnabled(b);
    
        if (initValue) {
            nextSizeInput.setValue("");
            nextSizeUnitBox.setValue("");
            unlimitedInput.setValue("");
            maxSizeInput.setValue("");
            maxSizeUnitBox.setValue("");
        }
    }
    
    private void setPathEnabled(boolean b,boolean initValue) {
        pathInput.setEnabled(b);
        autoExtendBox.setEnabled(b);
        
        if (initValue) {
            pathInput.setValue("");
            autoExtendBox.setValue("");
        }
    }
}
