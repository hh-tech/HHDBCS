package com.hh.hhdb_admin.mgr.tree.handler.action;

import com.hh.frame.swingui.view.util.ClipboardUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

/**
 * 复制名称
 */
public class CopyNameHandler extends AbsHandler{
    @Override
    public void resolve(CsFolderBean treeNode) throws Exception {
        resolve(new CsFolderBean[]{treeNode});
    }
    
    @Override
    public void resolve(CsFolderBean[] treeNodes) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (CsFolderBean treeNode : treeNodes) {
            sb.append(sb.length()>0 ? "," : "");
            sb.append(treeNode.getName());
        }
        ClipboardUtil.putText(sb.toString());
    }
}
