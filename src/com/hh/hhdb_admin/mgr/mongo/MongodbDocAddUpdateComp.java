package com.hh.hhdb_admin.mgr.mongo;

import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.mongo.obj.MongoDocUtil;
import com.hh.frame.swingui.cs_desk.jsonEditor.JsonEditorPanel;
import com.hh.frame.swingui.laf.base.AbstractLookAndFeel;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.fa.FaIconUtil;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tree.AddMongoDocBean;
import com.mongodb.client.result.InsertOneResult;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;

import java.sql.Connection;

public class MongodbDocAddUpdateComp extends JsonEditorPanel {
    protected final String schemaName;
    protected final String tableName;
    protected final String docId;
    protected final MongoColl mongoColl;
    protected HDialog dialog;
    private String refreshId;

    public MongodbDocAddUpdateComp(Connection conn, String schema, String tableName, String docId,String refreshId) throws Exception {
        super(null);
        this.schemaName = schema;
        this.tableName = tableName;
        this.docId = docId;
        this.mongoColl = new MongoColl(conn, schemaName);
        this.refreshId=refreshId;

        if (StringUtils.isNotBlank(docId))
            init(mongoColl.getObj(tableName, docId).toPrettyString());
    }

    public void showDialog() {
        dialog = new HDialog(CsVarUtil.parentFrame, 800, 600);
        dialog.setIconImage(FaIconUtil.getIcon(FaSolidEnum.BARS, AbstractLookAndFeel.getTheme().getIconColor(), 16));
        dialog.setWindowTitle(LangMgr.lang(StringUtils.isNotBlank(docId) ? Word2.EXAMINE : Word2.ADD));
        dialog.setRootPanel(this);
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }

    @Override
    protected void save() {
        try {
        	//id空的时候为新增
            if (StringUtils.isBlank(docId)) {
            	InsertOneResult r=mongoColl.insertOne(tableName, area.getText());
            	Document doc=new Document(MongoDocUtil.RAW_ID,r.getInsertedId());
            	JsonObject docIdObj=Json.parse(doc.toJson()).asObject();
            	AddMongoDocBean b=new AddMongoDocBean(refreshId,docIdObj);
            	CsVarUtil.eng2.doCall(CsMgrEnum.TREE, b);
            	refresh(true);
            }else {
                mongoColl.replaceOne(tableName, docId, area.getText());
                refresh(false);
            }
            dialog.dispose();
            PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
        } catch (Exception e) {
            Lg.error(MongodbDocAddUpdateComp.class, e);
            PopDlgUtil.error(dialog, e);
        }
    }

    protected void refresh(boolean isAdded) {
    	
    }
}
