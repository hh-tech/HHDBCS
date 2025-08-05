package com.hh.hhdb_admin.common.bean;

public enum BaseOpEnum {
	INIT,				//初始化
	SHOW,				//显示
	CLOSE,				//关闭
	CLOSE_ALL_TAB,		//关闭所有TAB
    ADD,				//添加
    DELETE,             //删除
    DISPOSE,			//销毁
	GET_PATH,			//获取路径
	
	LOGIN_SWITCH,		//切换登录
	SWITCH_SCHEMA,		//切换模式
	
	SHOW_ABOUT,			//显示关于
	SHOW_LICENSE,		//显示LICENSE
	LOCK,				//锁定
	SET_TOOLBAR_VISIBLE,//工具栏可见设置
	SET_STATUSBAR_VISIBLE,//状态栏可见设置
	
	SQL_CONVERSION,		//sql格式化
	SQL_FORMAT,			//sql转换
	DATA_TO_SQL,		//转为插入语句
	
    PUBLISHER,          //发布
    SUBSCRIBER,         //订阅
}
