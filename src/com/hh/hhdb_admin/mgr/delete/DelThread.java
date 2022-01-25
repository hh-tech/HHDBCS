package com.hh.hhdb_admin.mgr.delete;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.LoginBean;
import com.hh.hhdb_admin.mgr.tree.TreeMgr;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Jiang
 * @Date: 2021/9/17 16:20
 */
public class DelThread implements Runnable {

    protected static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");
    private final List<NodeInfo> nodeInfoList;
    private final LoginBean loginBean;
    private final DeleteComp deleteComp;

    public DelThread(List<NodeInfo> nodeInfoList, LoginBean loginBean, DeleteComp deleteComp) {
        this.nodeInfoList = nodeInfoList;
        this.loginBean = loginBean;
        this.deleteComp = deleteComp;
    }

    @Override
    public void run() {
        for (int i = 0; i < nodeInfoList.size(); i++) {
            NodeInfo nodeInfo = nodeInfoList.get(i);
            if (deleteComp.isStop) {
                break;
            }
            getDelHandler(nodeInfo).ifPresent(delHandler -> {
                String res;
                try {
                    delHandler.init(loginBean,deleteComp.stat);
                    delHandler.del(nodeInfo);
                    res = "成功";
                } catch (Exception e) {
                    e.printStackTrace();
                    res = "失败：" + e.getMessage();
                }
                for (Map<String, String> datum : deleteComp.data) {
                    if (datum.get("name").equals(nodeInfo.getName())) {
                        datum.put("res", res);
                    }
                }
                deleteComp.loadData();
            });
            deleteComp.progressBarInput.setValue(i + 1);
            if (i == nodeInfoList.size() - 1) {
                deleteComp.isStop = true;
                deleteComp.stopBtn.setEnabled(false);
            }
        }
        if (StartUtil.eng != null) {
            JsonObject res = GuiJsonUtil.toJsonCmd(TreeMgr.CMD_REFRESH);
            res.add(StartUtil.PARAM_SCHEMA, nodeInfoList.get(0).getSchemaName())
                    .add("isParent", true)
                    .add(TreeMgr.PARAM_NODE_TYPE, nodeInfoList.get(0).getTreeMrType().name());
            if (nodeInfoList.get(0).getTableName() != null) {
                res.add(StartUtil.PARAM_SCHEMA, nodeInfoList.get(0).getSchemaName());
            }
            StartUtil.eng.doPush(CsMgrEnum.TREE, res);
        }
        afterDelete();
    }


    private Optional<AbsDel> getDelHandler(NodeInfo nodeInfo) {
        String targetClass = this.getClass().getPackage().getName() + ".impl."
                + lineToHump(nodeInfo.getTreeMrType().name()) + lineToHump(nodeInfo.getOperType().name());
        try {
            return Optional.of((AbsDel) Class.forName(targetClass).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    public static String lineToHump(String type) {
        Matcher matcher = LINE_PATTERN.matcher(type.toLowerCase());
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return String.valueOf(sb.charAt(0)).toUpperCase() + sb.substring(1);
    }

    public void afterDelete() {

    }


}
