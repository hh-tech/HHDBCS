package com.hh.hhdb_admin.mgr.redis_obj;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.redis.base.RedisObjType;
import com.hh.frame.redis.db.RedisHashObj;
import com.hh.frame.redis.db.RedisListObj;
import com.hh.frame.redis.db.RedisSetObj;
import com.hh.frame.redis.db.RedisZsetObj;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.*;

public class TableEditDialog extends AbsHComp {
    private Connection conn;
    private RedisObjType type;
    
    private HDialog dialog;
    private LastPanel lastPanel;
    private TextInput keyInput;
    private TextAreaInput valArea;
    
    private String key;     //当前修改对象
    private String keyName; //键值对主键的名称
    
    /**
     * 打开修改面板
     */
    public TableEditDialog(String objKey, String keyName,String valName, Connection conn, RedisObjType type) {
        this.key = objKey;
        this.keyName = keyName;
        this.conn= conn;
        this.type = type;
    
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setMaxWidth(500);
        layout.setRightWidth(50);
        HPanel hPanel = new HPanel(layout);
        hPanel.add(new HeightComp(5));
        keyInput = new TextInput();
        if (type != RedisObjType.set && type != RedisObjType.list) hPanel.add(RedisObjUtil.getLabelInput(keyName + " ：", keyInput));
        hPanel.add(RedisObjUtil.getLabelInput(valName + " ：", valArea = new TextAreaInput("textArea", "", 10) ));
        hPanel.add(new HeightComp(5));
    
        lastPanel = new LastPanel(false);
        lastPanel.setHead(hPanel.getComp());
    }
    
    public void show(HTable table){
        HTabRowBean hTabRowBean = table.getSelectedRowBeans().get(0);
        String colStr = null != hTabRowBean.getOldRow() ? hTabRowBean.getOldRow().get("Key") : hTabRowBean.getCurrRow().get("Key");
        String colStr2 = null != hTabRowBean.getOldRow() ? hTabRowBean.getOldRow().get("Value") : hTabRowBean.getCurrRow().get("Value");
        if (type != RedisObjType.set && type != RedisObjType.list) {
            keyInput.setValue(colStr);
        }
        valArea.setValue(colStr2);
    
        dialog = new HDialog(500,300){
            @Override
            protected void onConfirm() {
                String name = keyInput.getValue().trim();
                if (!StringUtils.isNotBlank(name) && type != RedisObjType.set && type != RedisObjType.list) {
                    PopDlgUtil.info(dialog, "请输入"+keyName);
                    return;
                }
                if (null != key) {
                    if (type == RedisObjType.hash) {
                        RedisHashObj hashObj = new RedisHashObj(conn, key);
                        if (hTabRowBean.getStatus() != RowStatus.ADD) hashObj.hdel(colStr);
                        Map<String,String> map = new HashMap<>();
                        map.put(name, valArea.getValue());
                        hashObj.setMap(map);
                    } else if (type == RedisObjType.zset) {
                        if (!RedisObjUtil.isNumeric(name,dialog)) return;
                        RedisZsetObj hashObj = new RedisZsetObj(conn, key);
                        if (hTabRowBean.getStatus() != RowStatus.ADD) hashObj.zrem(colStr2);
                        Map<String,Double> map = new HashMap<>();
                        map.put(valArea.getValue(),Double.valueOf(name));
                        hashObj.setValues(map);
                    } else if (type == RedisObjType.set) {
                        RedisSetObj hashObj = new RedisSetObj(conn, key);
                        if (hTabRowBean.getStatus() != RowStatus.ADD) hashObj.srem(colStr2);
                        Set<String> set = new HashSet<>();
                        set.add(valArea.getValue());
                        hashObj.setValues(set);
                    } else if (type == RedisObjType.list) {
                        RedisListObj hashObj = new RedisListObj(conn, key);
                        if (hTabRowBean.getStatus() != RowStatus.ADD) hashObj.lrem(colStr2);
                        List<String> list = new ArrayList<>();
                        list.add(valArea.getValue());
                        hashObj.setValues(list);
                    }
                    refresh();
                    PopDlgUtil.info(dialog, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                } else {
                    Map<String,String> map = new HashMap<>();
                    if (type == RedisObjType.hash || type == RedisObjType.zset) {
                        if ((type == RedisObjType.zset) && !RedisObjUtil.isNumeric(name,dialog)) return;
                        map.put("Key",name);
                    }
                    map.put("Value", valArea.getValue());
                    map.put("Operation","");
                    hTabRowBean.setCurrRow(map);
                }
                dialog.dispose();
            }
        };
        dialog.setWindowTitle("修改行");
        dialog.setRootPanel(lastPanel);
        dialog.removeCancleBtn();
        dialog.setDisposeOnHide(false);
        dialog.show();
    }
    
    protected void refresh() {
    }
}
