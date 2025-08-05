package com.hh.hhdb_admin.mgr.multi_view.comp;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lg.Lg;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.mongo.obj.MongoDocUtil;
import com.hh.frame.swingui.laf.base.AbstractLookAndFeel;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.fa.FaIconUtil;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;

import org.apache.commons.collections4.CollectionUtils;
import org.bson.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class MongodbViewComp extends LastPanel {
    protected final HashMap<String, BsonDocument> docMap = new HashMap<>();
    protected final ArrayList<Map<String, String>> data = new ArrayList<>();
    protected final LinkedHashSet<String> headers = new LinkedHashSet<>();
    protected final MongoColl mongoColl;
    protected final String collName;
    protected final List<String> idList;
    protected BreadCrumb<BsonType> breadCrumb;
    protected DocTable docTable;
    protected final HashMap<String,String> headerTypeMap = new HashMap<>();
    private boolean isChange = false;
    
    public MongodbViewComp(MongoColl mongoColl, String collName, List<String> idList) {
        this.mongoColl = mongoColl;
        this.collName = collName;
//        this.idList = idList.isEmpty() ? mongoColl.findIds(collName, null) : idList;
        this.idList=idList;
        docTable = new DocTable(docMap) {
            @Override
            public List<String> getNavi() {
                return breadCrumb.copyNavi();
            }
        };
        docTable.getComp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                docTable.selectCell(e);
                if (e.getButton() == MouseEvent.BUTTON2)
                    enter();
            }
        });
        docTable.setRowPopMenu(new DocPopMenu(this));

        breadCrumb = new BreadCrumb<BsonType>(collName, BsonType.DOCUMENT) {
            @Override
            protected void onChange(List<String> oldNavi, List<String> newNavi) {
                refreshTable();
            }
        };
        SearchToolBar searchToolBar = new SearchToolBar(docTable);
        searchToolBar.setLastHComp(breadCrumb);

        refresh();

        setHead(searchToolBar.getComp());
        setWithScroll(docTable.getComp());
    }

    protected void enter() {
        DocBean docBean = docTable.getSelectedDocBean();
        if (docBean == null || (docBean.type != BsonType.DOCUMENT && docBean.type != BsonType.ARRAY) || MongoDocUtil.DOC_ID.equals(docBean.key))
            return;
        breadCrumb.forward(docBean.key, docBean.type);
    }

    public void refresh() {
        //获取所有文档
        docMap.clear();
        for (String id : idList) {
            Document doc = mongoColl.getBsonDoc(collName, id);
            if (doc == null)
                continue;
            docMap.put(id, doc.toBsonDocument());
        }
        refreshTable();
    }

    protected void refreshTable() {
        List<String> navi = breadCrumb.copyNavi();
        BsonType docType = breadCrumb.getCurrentInfo();

        if (docType != BsonType.DOCUMENT && docType != BsonType.ARRAY) {
            breadCrumb.back();
            return;
        }

        //刷新
        headers.clear();
        headerTypeMap.clear();
        data.clear();
        headers.add(MongoDocUtil.DOC_ID);
        if (docType == BsonType.DOCUMENT)
            initDocData(navi);
        else
            initArrayData(navi);
        docTable.clearCols();
        headers.forEach(header -> {
        	String type = headerTypeMap.get(header);
        	String name = type==null? header: header+"("+type+")";
        	docTable.addCols(new DocCol(header, name));
        });
        docTable.refreshToolBar();
        docTable.load(data, 1);
    }

    /**
     * 处理文档类型
     */
    private void initDocData(List<String> navi) {
        boolean baseDoc = CollectionUtils.isEmpty(navi);
        //刷新头
        for (String id : idList) {
            BsonDocument doc = docMap.get(id);
            if (!baseDoc) {
                BsonValue target = getNaviObj(doc, navi);
                if (target != null && target.isDocument())
                    doc = target.asDocument();
                else
                    continue;
            }
            headers.addAll(doc.keySet());
            for (String key : doc.keySet()) {
                BsonValue value = doc.get(key);
                headerTypeMap.put(key, value.getBsonType().toString());
            }
        }
        if (baseDoc)
            headers.remove(MongoDocUtil.RAW_ID);
        //刷新数据
        for (String id : idList) {
            BsonDocument doc = docMap.get(id);
            if (!baseDoc) {
                BsonValue target = getNaviObj(doc, navi);
                if (target != null && target.isDocument()) {
                    doc = target.asDocument();
                } else {
                    data.add(Collections.singletonMap(MongoDocUtil.DOC_ID, id));
                    continue;
                }
            }
            HashMap<String, String> map = new HashMap<>();
            map.put(MongoDocUtil.DOC_ID, id);
            for (String tableHeader : headers) {
                if (!doc.containsKey(tableHeader))
                    continue;
                BsonValue obj = doc.get(tableHeader);
                String text = MongoDocUtil.renderText(obj);
                map.put(tableHeader, text);
            }
            data.add(map);
        }
    }

    /**
     * 处理数组类型
     */
    private void initArrayData(List<String> navi) {
        //刷新头
        int max = 0;
        for (String id : idList) {
            BsonValue target = getNaviObj(docMap.get(id), navi);
            if (target != null && target.isArray())
                max = Math.max(max, target.asArray().size());
        }
        for (int i = 0; i < max; i++) {
            headers.add(i + "");
        }
        //刷新数据
        for (String id : idList) {
            HashMap<String, String> map = new HashMap<>();
            map.put(MongoDocUtil.DOC_ID, id);
            BsonValue target = getNaviObj(docMap.get(id), navi);
            if (target != null && target.isArray()) {
                BsonArray array = target.asArray();
                for (int i = 0; i < array.size(); i++) {
                    String text = MongoDocUtil.renderText(array.get(i));
                    map.put(i + "", text);
                }
            }
            data.add(map);
        }
    }

    public static BsonValue getNaviObj(BsonDocument doc, List<String> navi) {
        try {
            BsonValue obj = doc;
            for (String key : navi) {
                switch (obj.getBsonType()) {
                    case DOCUMENT:
                        obj = obj.asDocument().get(key);
                        break;
                    case ARRAY:
                        obj = obj.asArray().get(Integer.parseInt(key));
                        break;
                    default:
                        return null;
                }
            }
            return obj;
        } catch (Exception e) {
            Lg.debug(MongodbViewComp.class, "navi to nothing", doc, navi);
            return null;
        }
    }
	public boolean isCancelSaveData() {
		if (isChange) {
			if (PopDlgUtil.confirm(CsVarUtil.parentFrame,LangMgr.sLang(CsL.SAVE_OR_NOT))) {
				//TODO save data
				return false;
			}
		}
		return true;
	}
	
    public DocTable getDocTable() {
        return docTable;
    }
}
