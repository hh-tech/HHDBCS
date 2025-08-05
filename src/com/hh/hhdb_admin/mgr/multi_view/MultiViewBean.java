package com.hh.hhdb_admin.mgr.multi_view;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class MultiViewBean extends BaseJsonBean {
    private static final String TABLENAME = "tableName";
    private static final String SCHEMA = "schema";
    private static final String ID_LIST = "idList";

    public MultiViewBean() {
    }

    public MultiViewBean(JsonObject jobj) {
        this.jobj = jobj;
    }

    public String getTableName() {
        return jobj.getString(TABLENAME);
    }

    public void setTableName(String tableName) {
        jobj.set(TABLENAME, tableName);
    }

    public String getSchema() {
        return this.jobj.getString(SCHEMA);
    }

    public void setSchema(String schema) {
        this.jobj.set(SCHEMA, schema);
    }

    public void addId(String id) {
        JsonValue idList = this.jobj.get(ID_LIST);
        if (idList == null)
            idList = Json.array();
        idList.asArray().add(id);
        this.jobj.set(ID_LIST, idList);
    }

    public List<String> getIdList() {
        ArrayList<String> result = new ArrayList<>();
        JsonValue idList = this.jobj.get(ID_LIST);
        if (idList != null) {
            for (JsonValue jsonValue : idList.asArray()) {
                result.add(jsonValue.asString());
            }
        }
        return result;
    }
}
