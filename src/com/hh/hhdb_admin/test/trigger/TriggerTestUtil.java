package com.hh.hhdb_admin.test.trigger;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.hhdb_admin.test.MgrTestUtil;
import org.apache.commons.lang3.StringUtils;

public class TriggerTestUtil {

    public static  String getTestSchema(){
        JdbcBean bean = MgrTestUtil.getJdbcBean();
        if(DriverUtil.getDbType(bean) == DBTypeEnum.oracle){
            if(StringUtils.isBlank(bean.getSchema())){
                return bean.getUser();
            }
        }
        return bean.getSchema();
    }
}
