package com.hh.hhdb_admin.mgr.table.column;

import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.json.JsonObject;
import com.hh.frame.json.JsonValue;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import com.hh.hhdb_admin.mgr.table.common.TableUtil;

/**
 * @author oyx
 * @date 2020-10-23  0023 10:59:12
 */
public class ForeignTableColumn extends JsonCol {
	private final Connection conn;
	public static final String FOREIGN_SCHEMA_NAME = "foreignSchemaName";
	public static final String FOREIGN_TABLE_NAME = "foreignTableName";
//	public static final String FOREIGN_TABLE_ID = "foreignTableId";

//	private AbsHComp baseComp;
	AbsTableObjFun tableObjFun;

	public ForeignTableColumn(String name, String value, HTable hTab, Connection conn) {
		super(name, value);
		this.conn = conn;
		tableObjFun = TableComp.getCreateTabTool().getTableObjFun();
	}


	/**
	 * 根据自己的业务要求进行覆盖
	 */
	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {
		String selectSchema = null, selectTable = null;
		JsonObject res = null;

		try {
			if (conn == null) {
				PopPaneUtil.info(TableComp.getLang("connFailed"));
				return null;
			}
			if (json != null) {
				JsonValue jsonTable = json.get(FOREIGN_TABLE_NAME);
				JsonValue jsonSchema = json.get(FOREIGN_SCHEMA_NAME);
				selectSchema = jsonSchema.isNull() ? null : json.getString(FOREIGN_SCHEMA_NAME);
				selectTable = jsonTable.isNull() ? null : json.getString(FOREIGN_TABLE_NAME);
			}
			JPanel myPanel = new JPanel(new GridLayout(0, 1));
			List<JComboBox<String>> jComboBoxList = createSchemaBox(selectSchema);
			JComboBox<String> schemaBox = jComboBoxList.get(0);
			JComboBox<String> tableBox = jComboBoxList.get(1);
			if (selectTable != null) {
				tableBox.setSelectedItem(selectTable);
			}
			schemaBox.addItemListener(e -> {
				try {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						tableBox.removeAllItems();
						for (String table : tableObjFun.getAllTables(conn, (String) e.getItem())) {
							tableBox.addItem(table);
						}
					}
				} catch (SQLException sqlException) {
					PopPaneUtil.error(sqlException.getMessage());
				}
			});
			myPanel.add(new JLabel(TableComp.getLang("schema")));
			myPanel.add(schemaBox);
			myPanel.add(new JLabel(TableComp.getLang("table")));
			myPanel.add(tableBox);

			final JDialog dialog = new JDialog();
			dialog.setAlwaysOnTop(dialog.isAlwaysOnTopSupported());

			int result = JOptionPane.showConfirmDialog(dialog, myPanel, "请选外键表", JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				selectSchema = (String) schemaBox.getSelectedItem();
				selectTable = (String) tableBox.getSelectedItem();
				if (selectSchema == null) {
					return null;
				}
				res = new JsonObject();
				res.add(__TEXT, selectSchema + (selectTable != null ? "." + selectTable : ""));
				res.add(FOREIGN_SCHEMA_NAME, selectSchema);
				res.add(FOREIGN_TABLE_NAME, selectTable);
			}
		} catch (Exception exception) {
			PopPaneUtil.error(exception);
			return null;
		}
		return res;
	}

	private List<JComboBox<String>> createSchemaBox(String selectSchema) {
		JComboBox<String> schemaBox = new JComboBox<>();
		JComboBox<String> tableBox = new JComboBox<>();
		try {
			if (selectSchema == null) {
				selectSchema = TableUtil.getDefSchema(TableComp.getDbType(), TableComp.jdbcBean);
			}

			List<String> schemaList = tableObjFun.getAllSchemas(conn);
			if(schemaList.size()<=0 &&selectSchema!=null){
				schemaList.add(selectSchema);
			}
			List<String> tableList = tableObjFun.getAllTables(conn, StringUtils.isNoneBlank(selectSchema) ? selectSchema : schemaList.get(0));
			schemaBox = new JComboBox<>(schemaList.toArray(new String[0]));
			schemaBox.setEnabled(TableUtil.comboBoxIsEnable(TableComp.getDbType()));
			tableBox = new JComboBox<>(tableList.toArray(new String[0]));
			schemaBox.setSelectedItem(selectSchema);
			if (selectSchema == null && schemaList.size() > 0) {
				schemaBox.setSelectedItem(schemaList.get(0));
			}

		} catch (Exception throwables) {
			throwables.printStackTrace();
		}
		return Arrays.asList(schemaBox, tableBox);
	}


}
