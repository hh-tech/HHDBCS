package com.hh.hhdb_admin.test;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.test.FrameTestUtil;

import java.io.IOException;


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


    public static void main(String[] args) throws Exception {
        System.out.println(getJdbcBean(DBTypeEnum.mysql));

    }

}
