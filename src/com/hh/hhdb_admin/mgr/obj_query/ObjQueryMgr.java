package com.hh.hhdb_admin.mgr.obj_query;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: Jiang
 * @Date: 2021/7/9 10:33
 */
public class ObjQueryMgr extends AbsGuiMgr {

    public static final String SHOW_QUERY = "show_query";
    public static final String QUERY_WITH_WORD = "queryWithWord";
    public static final String KEY_WORD = "keyWord";

    @Override
    public void init(JsonObject jObj) {

    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public Enum<?> getType() {
        return null;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        String action = GuiJsonUtil.toStrCmd(msg);
        LoginBean loginBean = StartUtil.getLoginBean();
        if (action.equals(SHOW_QUERY)) {
            new ObjQueryComp(loginBean).show();
        }
        if (action.equals(QUERY_WITH_WORD)) {
            String keyWord = msg.getString(KEY_WORD);
            if (StringUtils.isBlank(keyWord)) {
                return;
            }
            new ObjQueryComp(loginBean, keyWord).show();
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        return null;
    }
}
