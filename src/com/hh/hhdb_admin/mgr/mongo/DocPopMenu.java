package com.hh.hhdb_admin.mgr.mongo;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;

import com.hh.frame.swingui.view.hmenu.HMenuItem;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.menu.body.AbsTabBodyPopMenu;

import java.awt.event.MouseEvent;

public class DocPopMenu extends AbsTabBodyPopMenu {

    public final DocTable docTable;

    public DocPopMenu(MongodbOpenCollComp openCollComp) {
        this.docTable = openCollComp.getDocTable();
        this.tab = this.docTable;
    }

    @Override
    public void init(HTable tab) {
        addItem(createCopyItem());
    }

    @Override
    public void showPopup(MouseEvent e) {
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
}
