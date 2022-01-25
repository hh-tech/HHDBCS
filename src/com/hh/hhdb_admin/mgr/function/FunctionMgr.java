package com.hh.hhdb_admin.mgr.function;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DriverUtil;
import com.hh.frame.create_dbobj.function.mr.AbsFunMr;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrNode;
import com.hh.frame.create_dbobj.treeMr.base.TreeMrType;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.engine.AbsGuiMgr;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.function.ui.deBug_from.OrDebugForm;
import com.hh.hhdb_admin.mgr.function.util.DebugUtil;
import com.hh.hhdb_admin.mgr.function.util.FunUtil;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

import javax.swing.*;

public class FunctionMgr extends AbsGuiMgr {
    public static final String CMD_ADD_FUNCTION = "ADD_FUNCTION";           //添加函数
    public static final String CMD_EDIT_FUNCTION = "EDIT_FUNCTION";         //修改函数
    public static final String CMD_DELETE_FUNCTION = "DELETE_FUNCTION";     //删除函数
    public static final String DEBUG = "DEBUG";                             //调试
    public static final String CMD_DEBUG_FUNCTION = "DEBUG_FUNCTION";       //调试函数
    public static final String ADD_DEBUG_INFO = "ADD_DEBUG_INFO";           //添加调试信息
    public static final String RUN_FUNCTION = "RUN_FUNCTION";               //运行函数
    public static final String EXAMINE_FUNCTION = "EXAMINE_FUNCTION";       //检查函数

    public static final String PARAM_FUNC_NAME = "function_name";
    public static final String PARAM_FUNC_ID = "function_id";
    public static final String PARAM_PACKNAME = "packName";
    public static final String TYPE = "TYPE";                               //节点类型
    private JdbcBean jdbcBean;

    @Override
    public void init(JsonObject jObj) {
        try {
            LangMgr2.loadMerge(FunctionMgr.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public CsMgrEnum getType() {
        return CsMgrEnum.FUNCTION;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public void doPush(JsonObject msg) throws Exception {
        jdbcBean = StartUtil.getLoginBean().getJdbc();

        FunctionComp fun = null;
        if(!GuiJsonUtil.toStrCmd(msg).equals(CMD_DEBUG_FUNCTION) && !GuiJsonUtil.toStrCmd(msg).equals(RUN_FUNCTION) && !GuiJsonUtil.toStrCmd(msg).equals(DEBUG)){
            fun = new FunctionComp(jdbcBean,GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA)){
                @Override
                protected void refresh() {
                    StartUtil.eng.doPush(CsMgrEnum.TREE, GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH)
                            .add(TreeMgr.PARAM_NODE_TYPE, GuiJsonUtil.toPropValue(msg,TYPE).equals(TreeMrType.FUNCTION.name()) ? TreeMrType.FUNCTION_GROUP.name() : TreeMrType.PROCEDURE_GROUP.name())
                            .add(StartUtil.PARAM_SCHEMA, GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA)));
                }
            };
        }
        switch (GuiJsonUtil.toStrCmd(msg)) {
            case CMD_ADD_FUNCTION:
                fun.show(GuiJsonUtil.toPropValue(msg,TYPE));
                break;
            case CMD_EDIT_FUNCTION:
                fun.show(GuiJsonUtil.toPropValue(msg, PARAM_FUNC_NAME),GuiJsonUtil.toPropValue(msg, PARAM_FUNC_ID),GuiJsonUtil.toPropValue(msg,TYPE));
                break;
            case CMD_DELETE_FUNCTION:
                fun.delete(GuiJsonUtil.toPropValue(msg, PARAM_FUNC_NAME),GuiJsonUtil.toPropValue(msg, PARAM_FUNC_ID),GuiJsonUtil.toPropValue(msg,TYPE));
                break;
            case ADD_DEBUG_INFO:
                fun.addDebugInfo(GuiJsonUtil.toPropValue(msg, PARAM_FUNC_NAME),GuiJsonUtil.toPropValue(msg,TYPE));
                break;
            case CMD_DEBUG_FUNCTION:
                DebugUtil.examine(GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA),jdbcBean);
                FunUtil.getDebugBaseForm(msg,jdbcBean);
                break;
            case DEBUG:
                DebugUtil.examine(jdbcBean.getSchema(),jdbcBean);
                new OrDebugForm(jdbcBean);
                break;
            case RUN_FUNCTION:
                TreeMrNode treeNode = new TreeMrNode(GuiJsonUtil.toPropValue(msg, PARAM_FUNC_NAME),GuiJsonUtil.toPropValue(msg, PARAM_FUNC_ID), TreeMrType.valueOf(GuiJsonUtil.toPropValue(msg,TYPE)), "");
                treeNode.setSchemaName(GuiJsonUtil.toPropValue(msg, StartUtil.PARAM_SCHEMA));
                AbsFunMr funMr = AbsFunMr.genFunMr(DriverUtil.getDbType(jdbcBean),treeNode);
                FunUtil.getRunFun(funMr,jdbcBean,GuiJsonUtil.toPropValue(msg,PARAM_PACKNAME));
                break;
            case EXAMINE_FUNCTION:
                fun.examine(GuiJsonUtil.toPropValue(msg, PARAM_FUNC_NAME),GuiJsonUtil.toPropValue(msg, PARAM_FUNC_ID),GuiJsonUtil.toPropValue(msg,TYPE));
                break;
            default:
                unknowMsg(msg.toPrettyString());
        }
    }

    @Override
    public JsonObject doCall(JsonObject msg) throws Exception {
        return new JsonObject();
    }
    /**
     * 中英文
     *
     * @param key
     * @return
     */
    public static String getLang(String key) {
        return LangMgr2.getValue(FunctionMgr.class.getName(), key);
    }

    public static ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.FUNCTION.name(), name, IconSizeEnum.SIZE_16));
    }
}
