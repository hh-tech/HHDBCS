package com.hh.hhdb_admin.mgr.pack;

import com.hh.frame.common.util.RandomUtil;
import com.hh.frame.json.BaseJsonBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.engine2.AbsGuiMgr;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.CloseBean;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.main_frame.SharePaneObj;

import org.apache.commons.lang3.StringUtils;

public class PackageMgr extends AbsGuiMgr {
    @Override
    public void init(JsonObject jObj) {
    }

    @Override
    public Enum<?> getType() {
        return CsMgrEnum.PACKAGE;
    }
    
    @Override
    public BaseJsonBean doCall(BaseJsonBean bj) throws Exception {
        if (bj instanceof CloseBean) return null;
        PackageBean csGuiBean = new PackageBean(bj.getJobj());
        PackageComp packageComp = new PackageComp(CsVarUtil.jdbc,CsVarUtil.conn, csGuiBean);
        switch (csGuiBean.getOprType()) {
            case ADD:
                String paname = PopDlgUtil.nameInput(LangMgr.sLang(CsL.PACKAGE_NAME),"");
                if (!StringUtils.isBlank(paname)) {
                    addTapPanel(paname,packageComp.getAddPanel(paname));
                }
                break;
            case UPDATE:
                String name = csGuiBean.getName();
                addTapPanel(name,packageComp.getDesignPanel(name, csGuiBean.getType()));
                break;
            case SHOW:
                packageComp.openFile(csGuiBean.getText(), csGuiBean.getFileName());
                break;
            default:
                break;
        }
        return null;
    }
    
    private void addTapPanel(String text,Object obj){
        SharePaneObj sharePaneObj = new SharePaneObj();
        sharePaneObj.setId(RandomUtil.getUUID());
        sharePaneObj.setTitle(text);
        sharePaneObj.setCsMgrEnum(CsMgrEnum.PACKAGE);
        sharePaneObj.setFixTab(false);
        sharePaneObj.setObj(obj);
        CsVarUtil.eng2.doShare(CsMgrEnum.MAIN_FRAME, sharePaneObj);
    }
}
