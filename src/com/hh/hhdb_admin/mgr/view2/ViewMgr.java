package com.hh.hhdb_admin.mgr.view2;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.modifyinfo.ModifyViewPanel;
import com.hh.hhdb_admin.mgr.tree.RefreshNodeBean;

public class ViewMgr extends AbsGuiMgr {
  


    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.VIEW;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
      
            ViewBean csGuiBean = new ViewBean(bj.getJobj());
            String schemaName = csGuiBean.getSchema();
            String viewName = csGuiBean.getName();
            CsVarUtil.jdbc.setSchema(schemaName);
            switch (csGuiBean.getOprType()) {
                case ADD:
                case ADD_MVIEW:
                    ViewPanel panel = getViewPanel(schemaName,csGuiBean.getOprType() == ViewBean.OprType.ADD_MVIEW,csGuiBean.getRefreshNodeId());
                    String title = csGuiBean.getOprType() == ViewBean.OprType.ADD_MVIEW ? LangMgr.sLang(CsL.ADD_MVIEW_TITLE) : LangMgr.lang(Word4.CREATE_VIEW);
                    showDialog(title,panel);
                    break;
                case UPDATE:
                    panel = getViewPanel(schemaName,false,csGuiBean.getRefreshNodeId());
                    panel.loadView(viewName);
                    showDialog(LangMgr.lang(Word4.DESIGN_VIEW) + "(" + viewName + ")",panel);
                    break;
                case SHOW:
                    break;
                case MODIFY:
                    ModifyViewPanel modifyViewPanel = new ModifyViewPanel(schemaName, viewName);
                    HDialog hDialog = modifyViewPanel.createDialog(LangMgr.lang(Word2.MODIFY));
                    modifyViewPanel.setAfterSave(() -> {
                        hDialog.dispose();
                        CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(csGuiBean.getRefreshNodeId()));
                    });
                    hDialog.show();
                    break;
                default:
                    break;
            }
        
        return null;
    }
    
    private ViewPanel getViewPanel(String schema,boolean isMaterialized,String id) {
        return new ViewPanel(CsVarUtil.jdbcCloneWithSsh(),schema,isMaterialized) {
            @Override
            protected void inform(String schemaName, boolean isMaterialized) {
                CsVarUtil.eng2.doCall(CsMgrEnum.TREE,new RefreshNodeBean(id));
            }
        };
    }
    
    private void showDialog(String title, ViewPanel panel) {
        HDialog dialog = new HDialog(CsVarUtil.parentFrame, 750, 550);
        dialog.getWindow().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (null != panel.mcomp) panel.mcomp.close();
            }
        });
        dialog.setWindowTitle(title);
        dialog.setRootPanel(panel);
        dialog.setStatusBar(new HBarPanel());
        dialog.show();
    }


}
