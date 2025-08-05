package com.hh.hhdb_admin.mgr.mongo;

import org.bson.BsonType;

public class DocBean {
    public final String id;
    public final BsonType type;
    public final String key;
    public final String content;

    public DocBean(String id, BsonType type, String key, String content) {
        this.id = id;
        this.type = type;
        this.key = key;
        this.content = content;
    }


}
