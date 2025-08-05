package com.hh.hhdb_admin.test.vm_editor;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.hhdb_admin.mgr.vm_editor.VmComp;
import com.hh.hhdb_admin.test.MgrTestUtil;

public class TestVMComp {

	public static void main(String[] args) throws Exception {
		MgrTestUtil.initConfLang();
		MgrTestUtil.showComp(new VmComp(DBTypeEnum.oracle,"").getLastPanel());
	}
}
