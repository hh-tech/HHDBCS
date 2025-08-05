package com.hh.hhdb_admin.test;

import java.io.IOException;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.base.JdbcInfo;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.swingui.laf.LafIdEnum;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.test.FrameTestUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.conf.CsConf;


public class MgrTestUtil {

    public static JdbcBean getJdbcBean() {
        try {
            return FrameTestUtil.getJdbcBean(MgrTestUtil.class, FrameTestUtil.JSON_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JdbcBean getJdbcBean(DBTypeEnum dbtype) {
        try {
            return FrameTestUtil.getJdbcBean(dbtype, MgrTestUtil.class, FrameTestUtil.JSON_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	public static void initConfLang() throws IOException {
		CsVarUtil.CONF=new CsConf();
		LangMgr.addLang(CsL.class);
		LafMgr.setLaf(LafIdEnum.FAST);
		CsVarUtil.parentFrame = new HFrame(HFrame.MIDDLE_WIDTH);
	}
		
	public static void doTestMgr(Class<?> c) throws IOException {
		CsVarUtil.init(c,"conf.json");
		CsVarUtil.jdbc = new JdbcInfo(MgrTestUtil.getJdbcBean().getJobj());
        CsVarUtil.eng2.doCall(CsMgrEnum.MAIN_FRAME, new BaseOpBean(BaseOpEnum.INIT));
	}
	
	public static void showComp(AbsHComp comp) throws IOException {
		CsVarUtil.parentFrame.setRootPanel(comp);
		CsVarUtil.parentFrame.show();
	}
	
	public static void initCsVarJdbc() throws Exception {
        CsVarUtil.jdbc = new JdbcInfo(MgrTestUtil.getJdbcBean().getJobj());
        CsVarUtil.conn= ConnUtil.getConn(CsVarUtil.jdbc);
	}
    public static void main(String[] args) throws Exception {
        System.out.println(getJdbcBean(DBTypeEnum.mysql));

    }

}
