package com.hh.hhdb_admin.mgr.schema;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.lang.LangMgr2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.sql.Connection;


/**
 * @author: Jiang
 * @date: 2020/7/24
 */

public class SchemaComp {

    private static final String DOMAIN_NAME = SchemaComp.class.getName();

    private TextAreaInput commentInput;
    private TextInput schemaNameInput;
    private Connection conn;

    static {
    	try {
            LangMgr2.loadMerge(SchemaComp.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(Connection conn, JdbcBean jdbcBean) throws Exception {
        this.conn = conn;
        initPanel("", "", jdbcBean);
    }

    public void update(Connection conn, String schemaName) {
        this.conn = conn;
        try {
            String comment = SchemaUtil.getSchemaComment(conn, schemaName);
            initPanel(schemaName, comment == null ? "" : comment, null);
        } catch (Exception e) {
            e.printStackTrace();
            PopPaneUtil.error(StartUtil.parentFrame.getWindow(), e.getMessage());
        }
    }

    public void initPanel(String oldName, String oldComment, JdbcBean jdbcBean) throws Exception {
        HPanel rootPanel = new HPanel();
        rootPanel.add(new HeightComp(10));

        schemaNameInput = new TextInput("name");
        schemaNameInput.setEnabled(SchemaUtil.isNameEditable(oldName, conn));
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), "名称", schemaNameInput));
        commentInput = new TextAreaInput("comment");
        rootPanel.add(new WithLabelInput(new HPanel(new HDivLayout(GridSplitEnum.C2)), "注释", commentInput));
        if (oldName != null) {
            schemaNameInput.setValue(oldName);
        }
        commentInput.setValue(oldComment);

        HDialog dialog = new HDialog(StartUtil.parentFrame, 600, 400) {
            @Override
            protected void onConfirm() throws Exception {
                String newComment = commentInput.getValue();
                String newSchemaName = schemaNameInput.getValue();
                if (StringUtils.isBlank(newSchemaName)) {
                    throw new Exception("模式名称不能为空");
                }
                if (StringUtils.isNotBlank(oldName)) {
                    SchemaUtil.updateSchema(conn, oldName, newSchemaName, oldComment, newComment);
                } else {
                    SchemaUtil.addSchema(conn, jdbcBean.getUser(), newSchemaName, newComment);
                }
                refresh();
            }
        };
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.setWindowTitle(oldName == null ? "新建模式" : "修改模式");
        dialog.setOption();
        dialog.setRootPanel(rootPanel);
        dialog.show();
    }

    public void refresh() {

    }

    public static String getLang(String key) {
        return LangMgr2.getValue(DOMAIN_NAME, key);
    }

}
