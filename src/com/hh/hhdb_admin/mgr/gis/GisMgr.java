package com.hh.hhdb_admin.mgr.gis;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.mgr.main_frame.MainFrameMgr;

import javax.swing.*;

/**
 * GIS插件
 */
public class GisMgr extends AbsGuiMgr {
    public static final String CMD_SHOW_GIS = "CMD_SHOW_GIS";

    @Override
    public void init(JsonObject jObj) {
        try {
            LangMgr2.loadMerge(GisMgr.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.GIS;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case StartUtil.CMD_CLOSE:
                StartUtil.eng.rmFromSharedMap(GuiJsonUtil.toPropValue(msg,StartUtil.CMD_ID));
                //还原设置图层样式弹出窗口临时设置的字体
//                UIManager.put("Label.font",new JPanel().getFont());
//                UIManager.setLookAndFeel(HHSwingUi.getBeautyEyeLNFStrWindowsPlatform());
                break;
            case CMD_SHOW_GIS:
                JsonObject jsonObj= StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(LoginMgr.ObjType.LOGIN_BEAN));
                LoginBean logBean=(LoginBean)StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));
                JdbcBean jdbc = JdbcBean.toJdbc(logBean.getJdbc().toJson());

                GisComp gis = new GisComp(jdbc);
                StartUtil.eng.doPush(CsMgrEnum.MAIN_FRAME, GuiJsonUtil.toJsonCmd(MainFrameMgr.ADD_TAB_PANE_ITEM)
                        .add(StartUtil.CMD_ID,StartUtil.eng.push2SharedMap(gis)).add("title", "GIS")
                        .add(MainFrameMgr.PARAM_MGR_TYPE, CsMgrEnum.GIS.name()));
                break;
            default:
                unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        JsonObject res = new JsonObject();
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_SHOW_GIS:  //测试用例打开
                JsonObject jsonObj= StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
                LoginBean logBean=(LoginBean)StartUtil.eng.getSharedObj(GuiJsonUtil.toStrSharedId(jsonObj));
                String id = StartUtil.eng.push2SharedMap(new GisComp(logBean.getJdbc()));
                res.add(StartUtil.CMD_ID, id);
                break;
            default:
                unknowMsg(msg.toPrettyString());
        }
        return res;
    }

    /**
     * 中英文
     *
     * @param key
     * @return
     */
    public static String getLang(String key) {
        return LangMgr2.getValue(GisMgr.class.getName(), key);
    }

    public static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.GIS.name(), name, IconSizeEnum.SIZE_16));
    }
}
