package com.hh.hhdb_admin.test;

import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

public class MainTestMgr extends AbsGuiMgr {
    public static final String PARAM_TEST_CONN_ID = "testConnId";

    public enum ObjType {
        DIALOG
    }

    public final static String CMD_SHOW = CsMgrEnum.MAIN_FRAME.name() + "_SHOW";
    private String dialogId;
    AbsMainTestComp mainFrame;

    @Override
    public void init(JsonObject jObj) {
        String clazz = jObj.getString("comp_class");
        if (clazz == null || clazz.isEmpty()) {
            mainFrame = new AbsMainTestComp() {
                @Override
                public void init() {
                    HPanel panel = new HPanel();
                    panel.add(new LabelInput("请构造属于自己的测试面板"));
                    tFrame.setRootPanel(panel);
                }
            };
        } else {
            try {
                mainFrame = (AbsMainTestComp) Class.forName(clazz).newInstance();
            } catch (Exception e) {
                mainFrame = new AbsMainTestComp() {
                    @Override
                    public void init() {
                        HPanel panel = new HPanel();
                        panel.add(new LabelInput("测试面板不存在，请检查conf.json中\"comp_class\"值是否正确"));
                        tFrame.setRootPanel(panel);
                    }
                };
            }
        }
        mainFrame.init();
    }

    @Override
    public String getHelp() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.MAIN_FRAME;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        if (GuiJsonUtil.toStrCmd(msg).equals(CMD_SHOW)) {
            mainFrame.tFrame.getWindow().addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    String connId = GuiJsonUtil.toPropValue(msg, PARAM_TEST_CONN_ID);
                    if (!StringUtils.isBlank(connId)) {
                        LoginBean bean = (LoginBean) StartUtil.eng.getSharedObj(connId);
                        Connection conn = bean.getConn();
                        StartUtil.eng.rmFromSharedMap(connId);
                        System.out.println("关闭测试连接：" + conn.toString());
                        ConnUtil.close(conn);
                    }
                }
            });
            mainFrame.show();

        }

    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        String shareType = GuiJsonUtil.getShareIdType(msg);
        if (shareType.equals(ObjType.DIALOG.name())) {
            if (dialogId == null) {
                dialogId = StartUtil.eng.push2SharedMap(mainFrame.getDialog());
            }
            return GuiJsonUtil.toJsonSharedId(dialogId);
        }
        return null;
    }

}
