package com.hh.hhdb_admin.test.login;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.login.LoginMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import org.apache.commons.lang3.StringUtils;

public class LoginTestComp extends AbsMainTestComp {
    @Override
    public void init() {
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton showBtn = new HButton("登陆") {
            @Override
            public void onClick() {
                StartUtil.eng.doPush(CsMgrEnum.LOGIN, GuiJsonUtil.toJsonCmd(LoginMgr.CMD_SHOW_LOGIN));
            }
        };

        HButton switchBtn = new HButton("切换") {
            @Override
            public void onClick() {
                StartUtil.eng.doPush(CsMgrEnum.LOGIN, GuiJsonUtil.toJsonCmd(LoginMgr.CMD_SHOW_SWITCH));
            }
        };


        HButton idBtn = new HButton("获取ID") {
            @Override
            public void onClick() {
                try {
                    String id = fetchLoginBeanSharedId();
                    PopPaneUtil.info(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        HButton loginBeanBtn = new HButton("获取LoginBean") {
            @Override
            public void onClick() {
                try {
                    String id = fetchLoginBeanSharedId();
                    if (StringUtils.isBlank(id)) {
                        PopPaneUtil.info("获取不到ID");
                    } else {
                        PopPaneUtil.info(StartUtil.eng.getSharedObj(id).toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        HButton testConn = new HButton("测试连接") {
            @Override
            public void onClick() {
                try {
                    String id = fetchLoginBeanSharedId();
                    if (StringUtils.isBlank(id)) {
                        PopPaneUtil.info("获取不到ID");
                    } else {
                        LoginBean logBean = (LoginBean) StartUtil.eng.getSharedObj(id);
                        PopPaneUtil.info("连接状态：" + ConnUtil.isConnected(logBean.getConn()));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        barPanel.add(showBtn);
        barPanel.add(switchBtn);
        barPanel.add(idBtn);
        barPanel.add(loginBeanBtn);
        barPanel.add(testConn);

        tFrame.setToolBar(barPanel);
    }

    private String fetchLoginBeanSharedId() throws Exception {
        JsonObject jsonObj = StartUtil.eng.doCall(CsMgrEnum.LOGIN, GuiJsonUtil.genGetShareIdMsg(null));
        return GuiJsonUtil.toStrSharedId(jsonObj);
    }
}
