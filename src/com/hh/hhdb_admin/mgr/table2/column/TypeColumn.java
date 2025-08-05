package com.hh.hhdb_admin.mgr.table2.column;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.util.GlobalWorkUtil;
import com.hh.hhdb_admin.comp.column.DataTypePanel;
import com.hh.hhdb_admin.mgr.table2.comp.TableComp;
import org.apache.commons.lang3.StringUtils;

public class TypeColumn extends JsonCol {
    private DataTypePanel panel = null;
    
    public TypeColumn(String name, String value) {
        super(name, value);
    }
    
    @Override
    public JsonObject onClick(JsonObject json, int row, int column) {
        if(StringUtils.isNotBlank(json.getString(__TEXT))) {
            try {
                panel=new DataTypePanel(TableComp.dbType,TableComp.ver,json.getString(__TEXT));
            } catch (Exception e) {
                GlobalWorkUtil.err(getClass(), e);
            }
        }else {
            panel=new DataTypePanel(TableComp.dbType,TableComp.ver);
        }
        
        HDialog dialog = new HDialog(getTab().getComp(), 500,300,false) {
            @Override
            protected void onConfirm() {
                JsonObject obj = new JsonObject();
                obj.set(JsonCol.__TEXT, panel.getFullTypeDefine());
                htab.setValueAt(obj.toString(),row, column);
            }
        };
        dialog.setRootPanel(panel);
        dialog.show();
        return json;
        
    }
    
}
