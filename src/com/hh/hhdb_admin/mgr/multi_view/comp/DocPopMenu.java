package com.hh.hhdb_admin.mgr.multi_view.comp;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.menu.body.AbsTabBodyPopMenu;

import java.awt.event.MouseEvent;

public class DocPopMenu extends AbsTabBodyPopMenu {

    public final DocTable docTable;
    private final MongodbViewComp mongodbViewComp;

    private HMenuItem menuItemEditRow;

    public DocPopMenu(MongodbViewComp mongodbViewComp) {
        this.mongodbViewComp = mongodbViewComp;
        this.docTable = mongodbViewComp.getDocTable();
        this.tab = this.docTable;
    }

    @Override
    public void init(HTable tab) {
        addItem(createCopyItem());
        //addItem(createEditRow());
        addItem(createEditItem());
        addItem(createEnterItem());
        addItem(createRefreshItem());
        menuItemEditRow = createEditRow();
    }

    @Override
    public void showPopup(MouseEvent e) {
    	if(docTable.getComp().getSelectedRowCount()>1) {
    		this.remove(menuItemEditRow);
    	}else {
    		this.addItem(menuItemEditRow);
    	}
        docTable.selectCell(e);
        super.showPopup(e);
    }

    protected HMenuItem createCopyItem() {
        return new HMenuItem(LangMgr.lang(Word2.COPY)) {
            @Override
            protected void onAction() {
                docTable.copySelectedCell();
            }
        };
    }

    protected HMenuItem createEditRow() {
        return new HMenuItem("编辑记录") {
            @Override
            protected void onAction() {
                docTable.editRow();
            }
        };
    }
    
    protected HMenuItem createEditItem() {
        return new HMenuItem(LangMgr.lang(Word2.EXAMINE) + "(" + LangMgr.lang(Word4.LEFT_DOUBLE_CLICK) + ")") {
            @Override
            protected void onAction() {
                docTable.view();
            }
        };
    }

    protected HMenuItem createEnterItem() {
        return new HMenuItem(LangMgr.lang(Word2.ENTER) + "(" + LangMgr.lang(Word4.MIDDLE_CLICK) + ")") {
            @Override
            protected void onAction() {
                mongodbViewComp.enter();
            }
        };
    }

    protected HMenuItem createRefreshItem() {
        return new HMenuItem(LangMgr.lang(Word2.REFRESH)) {
            @Override
            protected void onAction() {
                mongodbViewComp.refresh();
            }
        };
    }
}
