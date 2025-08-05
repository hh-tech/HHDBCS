package com.hh.hhdb_admin.mgr.redis_obj;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.redis.base.RedisAbsKeyObj;
import com.hh.frame.redis.base.RedisObjType;
import com.hh.frame.redis.base.RedisUtil;
import com.hh.frame.redis.db.*;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bar.BarCol;
import com.hh.frame.swingui.view.tab.menu.body.DefTabBodyPopMenu;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.table2.TableUtil;
import com.hh.hhdb_admin.mgr.tree.RedisRefreshBean;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.*;

public class AddUpdRedisObjComp extends LastPanel {
    private Connection conn;
    private RedisObjType type;
    
    private HDialog dialog;
    private TextInput keyInput;
    private SelectBox typeBox;
    private HTextArea textArea;
    private HTable table;
    private HButton saveBtn;
    
    private String key;     //当前修改对象
    private String refreshId;  //刷新节点的ID
    
   
    
    
    public AddUpdRedisObjComp(String schema,String refreshId) throws Exception {
        this.conn=CsVarUtil.conn;
        this.refreshId = refreshId;
        RedisUtil.switch2Db(conn,schema);
        
        dialog = new HDialog(CsVarUtil.parentFrame,800, 600);
        keyInput = new TextInput();
        keyInput.getComp().setColumns(20);
        
        LastPanel lastPanel = new LastPanel(false);
        typeBox = new SelectBox() {
            @Override
            protected void onItemSelected(String value) {
                switch (type = RedisObjType.valueOf(value)) {
                    case string:case json:
                        HTAreaConf conf = new HTAreaConf();
                        conf.setDocType(type == RedisObjType.json ? TAreaDocType.SYNTAX_STYLE_JSON : TAreaDocType.SYNTAX_STYLE_NONE);
                        textArea = new HTextArea();
                        textArea.setConf(conf);
                        if (type == RedisObjType.json) textArea.setText("{\n    \"New key\": \"New value\"\n}");
                        lastPanel.set(textArea.getComp());
                        break;
                    case hash:
                        lastPanel.set(getTablePanel("Key","Value").getComp());
                        break;
                    case zset:
                        lastPanel.set(getTablePanel("Score","Member").getComp());
                        break;
                    case set:case list:
                        lastPanel.set(getTablePanel(null,"Value").getComp());
                        break;
                    default:
                }
            }
        };
        Arrays.asList(RedisObjType.values()).forEach(a -> {
            if (a != RedisObjType.none && a != RedisObjType.unsupport) typeBox.addOption(a.name(), a.name());
        });
        
        HBarLayout barLayout = HBarLayout.newLayout(AlignEnum.LEFT);
        barLayout.setTopHeight(5);
        barLayout.setBottomHeight(5);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(new LabelInput(LangMgr.lang(Word2.NAME) + ":"), keyInput);
        barPanel.add(new LabelInput(LangMgr.lang(Word2.TYPE) + ":"), typeBox);
        barPanel.add(saveBtn = new HButton(LangMgr.lang(Word2.SAVE), FaSolidEnum.FLOPPY_DISK){
            @Override
            protected void onClick() {
                try {
                    String name = keyInput.getValue().trim();
                    if (!StringUtils.isNotBlank(name)) {
                        PopDlgUtil.info(dialog, LangMgr.sLang(CsL.PLEASE_ENTER_NAME));
                        return;
                    }
                    
                    if (type == RedisObjType.zset) {
                        for (HTabRowBean hTabRowBean : table.getRowBeans(null)) {
                            if (!RedisObjUtil.isNumeric(hTabRowBean.getCurrRow().get("Key"),dialog)) return;
                        }
                    }
                    RedisAbsKeyObj obj = RedisObjUtil.getRedisObj(conn,name);
                    if (keyInput.isEnabled() && obj.exist()) {
                        if (PopDlgUtil.confirm(CsVarUtil.parentFrame, LangMgr.sLang(CsL.OVERWRITE_OR_NOT))) {
                            obj.delete();
                        } else {
                            return;
                        }
                    }
                    
                    RedisObjUtil.saveRdisData(conn,name,null == textArea ? "" : textArea.getText(),type,null == table ? new LinkedList<>() : table.getRowBeans(null));
//                    if (updateRefresh()) {
//                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RedisRefreshBean(nodeId,name));
//                    }
                    PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));

                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RedisRefreshBean(refreshId,name));
                    dialog.dispose();
                }catch (Exception e){
                    PopDlgUtil.logErr(getClass(), dialog, e);
                }
            }
        });
        
        setHead(barPanel.getComp());
        set(lastPanel.getComp());
    }
    
    public void showDialog(){
        dialog.setWindowTitle(LangMgr.lang(StringUtils.isNotBlank(key) ? Word2.UPDATE : Word2.ADD));
        dialog.setStatusBar(new HBarPanel());
        dialog.setRootPanel(this);
        dialog.show();
    }
    
    public void showDialog(String key) throws Exception{
        this.key = key;
        RedisObjType rObj = RedisUtil.getType(conn, key);
        if (rObj != RedisObjType.none && rObj != RedisObjType.unsupport) {
            typeBox.setValue(rObj.name());
            typeBox.setEnabled(false);
        }
        
        keyInput.setValue(key);
        keyInput.setEnabled(false);
        if (type != RedisObjType.string && type != RedisObjType.json) saveBtn.setEnabled(false);
        refreshTab();
        showDialog();
    }
    
    private LastPanel getTablePanel(String keyName,String valName) {
        BarCol toolBarCol = new BarCol("Operation", "Operation", new HButton("修改",FaSolidEnum.PEN) {
            @Override
            protected void onClick() {
                TableEditDialog editDialog = new TableEditDialog(key,keyName,valName,conn,type){
                    @Override
                    protected void refresh() {
                        try {
                            refreshTab();
                        }catch (Exception e){
                            PopDlgUtil.logErr(getClass(), getComp(), e);
                        }
                    }
                };
                editDialog.show(table);
            }
        }, new HButton("删除",FaSolidEnum.TRASH_CAN) {
            @Override
            protected void onClick() {
                TableUtil.stopEditing(table);
                if (PopDlgUtil.confirm(dialog,"确定删除选择行?")) {
                    if (StringUtils.isNotBlank(key)) {
                        RedisAbsKeyObj obj = RedisObjUtil.getRedisObj(conn,key);
                        if (obj.exist()) {
                            HTabRowBean bean = table.getSelectedRowBeans().get(0);
                            if (type == RedisObjType.hash) {
                                new RedisHashObj(conn,key).hdel(bean.getOldRow().get("Key"));
                            } else if (type == RedisObjType.zset) {
                                new RedisZsetObj(conn,key).zrem(bean.getOldRow().get("Value"));
                            } else if (type == RedisObjType.set) {
                                new RedisSetObj(conn,key).srem(bean.getOldRow().get("Value"));
                            } else if (type == RedisObjType.list) {
                                new RedisListObj(conn,key).lrem(bean.getOldRow().get("Value"));
                            }
                            if (!obj.exist()) {
                                dialog.dispose();
//                                if (updateRefresh()) {
//                                    CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(nodeId));
//                                }
                                CsVarUtil.eng2.doCall(CsMgrEnum.TREE, new RefreshNodeBean(refreshId));

                            }
                        }
                    }
                    table.deleteSelectRow();
                }
            }
        });
        toolBarCol.setSortPopMenu(null);
        toolBarCol.setWidth(30);
        
        table = new HTable();
        table.setRowHeight(30);
        table.setEvenBgColor(table.getOddBgColor());
        table.setSingleSelection(true);
        table.setRowPopMenu(new TabPopMenu());
        
        if (StringUtils.isNotBlank(keyName)) {
            DataCol keyCol = new DataCol("Key", keyName);
            keyCol.setCellEditable(false);
            table.addCols(keyCol);
        }
        DataCol valueCol = new DataCol("Value", valName);
        valueCol.setCellEditable(false);
        table.addCols(valueCol,toolBarCol);
        
        HBarPanel toolBar = new HBarPanel(TableUtil.newBarPan(AlignEnum.LEFT));
        toolBar.add(new HButton(LangMgr.lang(Word2.ADD),FaSolidEnum.PLUS) {
            @Override
            public void onClick() {
                RedisObjUtil.addCol(table);
            }
        });
        
        HPanel headPanel = new HPanel(RedisObjUtil.getLayout(5,10,5,10,GridSplitEnum.C6));
        headPanel.add(toolBar,new SearchToolBar(table));
        LastPanel lasPanel = new LastPanel(false);
        lasPanel.setHead(headPanel.getComp());
        lasPanel.setWithScroll(table.getComp());
        RedisObjUtil.addCol(table);
        return lasPanel;
    }
    
    private void refreshTab() throws Exception{
        List<Map<String, String>> data = new LinkedList<>();
        switch (type) {
            case string:
                textArea.setText(new RedisStringObj(conn,key).getValue());
                break;
            case json:
                JsonObject jObj= Json.parse(new RedisJsonObj(conn,key).getJsonValue()).asObject();
                textArea.setText(jObj.toPrettyString());
                break;
            case hash:
                Map<String, String> hashMap = new RedisHashObj(conn,key).getMap();
                hashMap.keySet().forEach(e -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("Key",e);
                    map.put("Value",hashMap.get(e));
                    map.put("Operation","");
                    data.add(map);
                });
                table.load(data, 1);
                break;
            case zset:
                Map<String, Double> zsetMap = new RedisZsetObj(conn,key).getValues();
                zsetMap.keySet().forEach(e -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("Key",zsetMap.get(e).toString());
                    map.put("Value",e);
                    map.put("Operation","");
                    data.add(map);
                });
                table.load(data, 1);
                break;
            case set:
            case list:
                Collection<String> coll = type==RedisObjType.set ? new RedisSetObj(conn,key).getValues() : new RedisListObj(conn,key).getValues();
                coll.forEach(e -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("Value",e);
                    map.put("Operation","");
                    data.add(map);
                });
                table.load(data, 1);
                return;
            default:
        }
    }
    
    class TabPopMenu extends DefTabBodyPopMenu {
        protected void addItems() {
            addItem(copyItem);
        }
    }
    
//    /**
//     * 对象修改后是否执行刷新操作
//     */
//    public boolean updateRefresh() {
//        return true;
//    }
}