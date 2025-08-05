package com.hh.hhdb_admin.mgr.table2.column;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlStrUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.comp.colsel.ColSelPanel;
import com.hh.hhdb_admin.mgr.table2.comp.TableComp;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class FKColumn extends JsonCol {
    public FKColumn(String name, String value) {
        super(name, value);
    }

    @Override
    public JsonObject onClick(JsonObject json, int row, int column) {
        StringBuffer refSchemaTable = new StringBuffer();
        
        ColSelPanel colSelPanel = new ColSelPanel();
        SearchToolBar searchToolBar = colSelPanel.getTable().getsToolbar();
        TextInput associationTableInput = new TextInput();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add(JsonCol.__TEXT, json == null ? "" : json.getString(JsonCol.__TEXT));
        HDialog foreignKeyDialog = new HDialog() {
            @Override
            protected void onConfirm() {
                List<String> selected = colSelPanel.getSelected();
                //表名(列1,列2)
                if (!selected.isEmpty())
                    jsonObject.add(JsonCol.__TEXT, refSchemaTable + "(" + StringUtils.join(selected, ",") + ")");
                else
                    PopDlgUtil.info(LangMgr.lang(Word4.NOT_NULL));
            }
        };

        HButton loadButton = new HButton(LangMgr.lang(Word2.LOAD), FaSolidEnum.DOWNLOAD) {
            @Override
            protected void onClick() {
                if (StringUtils.isBlank(associationTableInput.getValue())) {
                    PopDlgUtil.info(getComp(), LangMgr.sLang(CsL.ASSOCIATE_TABLE_NOT_NULL));
                    return;
                }
                String[] ref = StringUtils.split(associationTableInput.getValue(), ".");
                String refSchema;
                String refTabName;
                if (ref.length == 1) {
                    refSchema = TableComp.jdbcBean.getSchema();
                    refTabName = ref[0];
                } else {
                    refSchema = ref[0];
                    refTabName = ref[1];
                }
                //由于表格是用户输入的，所以需要根据数据库的规则进行大小写转换
                refSchema = SqlStrUtil.rmQuote4Sel(refSchema, TableComp.dbType, TableComp.ver);
                refTabName = SqlStrUtil.rmQuote4Sel(refTabName, TableComp.dbType, TableComp.ver);
                refSchemaTable.setLength(0);
                if (TableComp.dbType == DBTypeEnum.sqlite) {
                    refSchemaTable.append(SqlStrUtil.getQuotedFullName(TableComp.dbType,TableComp.ver,null,refTabName));
                } else {
                    refSchemaTable.append(SqlStrUtil.getQuotedFullName(TableComp.dbType,TableComp.ver,refSchema,refTabName));
                }
                try {
                    colSelPanel.loadData(TableComp.jdbcBean, TableComp.conn, refSchema, refTabName, null);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                colSelPanel.updateUI();
            }
        };
        searchToolBar.add(new LabelInput(LangMgr.lang(Word3.ASSOCIATE_TABLE)));
        searchToolBar.add(associationTableInput, 200);
        searchToolBar.add(loadButton);

        foreignKeyDialog.setRootPanel(colSelPanel);
        foreignKeyDialog.show();

        return jsonObject;
    }
}
