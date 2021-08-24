package com.hh.hhdb_admin.mgr.table.column;

import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.table.TableComp;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author oyx
 * @date 2020-9-25  15:09:16
 */
public class TypeColumn extends JsonCol {
	private JsonObject jsonObject;

	public static final String JSON_LENGTH = "__length";
	public static final String JSON_SCALE = "__scale";
	AbsTableObjFun tableObjFun;

	public TypeColumn(String name, String value) {
		super(name, value);
		tableObjFun = TableComp.getCreateTabTool().getTableObjFun();
	}


	/**
	 * 根据自己的业务要求进行覆盖
	 */
	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {
		List<String> twoLengthType = tableObjFun.getTwoLengthType();
		String[] type = {null};
		String[] length = {null};
		String[] scale = {null};
		if (json != null) {
			type[0] = json.getString(__TEXT);
			length[0] = json.getString(JSON_LENGTH);
			scale[0] = json.getString(JSON_SCALE);
		}
		LastPanel lastPanel = new LastPanel(false);
		JPanel myPanel = new JPanel(new GridLayout(0, 1));
		if (type[0] == null) {
			type[0] = tableObjFun.getDefaultType();
		}
		JComboBox<?> comboBox = new JComboBox<>(tableObjFun.getAllTypeStr().toArray());
		JTextField lengthField = new JTextField();
		JTextField scaleField = new JTextField();
		JLabel scaleLabel = new JLabel(TableComp.getLang("accuracy"));
//		JCheckBox checkBox = new JCheckBox("自动增长", false);
		AtomicBoolean verify = new AtomicBoolean(true);
		HDialog hDialog = new HDialog(StartUtil.getMainDialog(), 460, 360) {
			@Override
			protected void onConfirm() {
				type[0] = (String) comboBox.getSelectedItem();
				length[0] = lengthField.getText();
				scale[0] = scaleField.getText();
				verify.set(verify(type[0], length, scale));
				jsonObject = new JsonObject();
				jsonObject.add(__TEXT, type[0] == null ? "" : type[0]);
				jsonObject.add(JSON_LENGTH, length[0] == null ? "" : length[0]);
				jsonObject.add(JSON_SCALE, scale[0] == null ? "" : scale[0]);
				if (verify.get()) {
					dispose();
				}
			}

			@Override
			protected void onCancel() {
				if (json != null) {
					jsonObject = json;
				}
			}
		};
		hDialog.setOnClickClose(false);
		comboBox.setSelectedItem(type[0]);
		comboBox.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String select = Objects.requireNonNull(comboBox.getSelectedItem()).toString().toUpperCase();
				Integer lengthValue = tableObjFun.getTypeDefaultLength(tableObjFun.typeToEnum(select));
				if (twoLengthType.contains(select)) {
					myPanel.add(scaleLabel);
					myPanel.add(scaleField);
				} else {
					myPanel.remove(scaleLabel);
					myPanel.remove(scaleField);
				}
//				if (tableObjFun.showAutoIncrement() && tableObjFun.getNumberTypeList().contains(select)) {
//					myPanel.add(checkBox);
//				} else {
//					myPanel.remove(checkBox);
//				}
				lengthField.setText(lengthValue != null ? String.valueOf(lengthValue) : "");
				scaleField.setText("");
				myPanel.updateUI();
			}
		});

		myPanel.add(new JLabel(TableComp.getLang("dataType")));
		myPanel.add(comboBox);
		myPanel.add(new JLabel(TableComp.getLang("length")));
		lengthField.setText(length[0]);
		myPanel.add(lengthField);

		if (twoLengthType.contains(type[0].toUpperCase())) {
			myPanel.add(scaleLabel);
			scaleField.setText(StringUtils.isBlank(scale[0]) ? "" : scale[0]);
			myPanel.add(scaleField);
		}
//		if (tableObjFun.showAutoIncrement() && tableObjFun.getNumberTypeList().contains(type[0].toUpperCase())) {
//			myPanel.add(checkBox);
//		}
		lastPanel.setHead(myPanel);
		hDialog.setWindowTitle(TableComp.getLang("selectDataType"));
		hDialog.setOption();
		HPanel panel =new HPanel();
		panel.setLastPanel(lastPanel);
		hDialog.setRootPanel(panel);
		hDialog.show();
		return jsonObject;
	}

	private boolean verify(String type, String[]... lengths) {
		boolean isNeedLength = tableObjFun.getNeedLengthType().contains(type.toUpperCase()) && (lengths == null || lengths[0] == null || StringUtils.isBlank(lengths[0][0]));
		boolean flag = true;
		if (isNeedLength) {
			PopPaneUtil.error(TableComp.getLang("hasLength"));
			flag = false;
		} else {
			for (String[] length : lengths) {
				if (StringUtils.isNoneBlank(length[0])) {
					try {
						Integer.parseInt(length[0]);
					} catch (Exception e) {
						PopPaneUtil.error(TableComp.getLang("numberFormatError"));
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	public JsonObject getJsonObject() {
		return jsonObject;
	}

	public void setJsonObject(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
	}

	public static JsonObject getDefType() {
		AbsTableObjFun tableObjFun = TableComp.getCreateTabTool().getTableObjFun();
		JsonObject object = new JsonObject();
		object.add(__TEXT, (tableObjFun.getDefaultType()));
		Enum<?> anEnum = tableObjFun.typeToEnum(tableObjFun.getDefaultType());
		object.add(JSON_LENGTH, String.valueOf(tableObjFun.getTypeDefaultLength(anEnum)));
		return object;
	}

}
