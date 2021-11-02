package com.hh.hhdb_admin.mgr.function.ui.from;

import com.hh.frame.create_dbobj.table.base.AbsTableObjFun;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;


public class TypeColumn extends JsonCol {
	private JsonObject jsonObject;
	
	public static final String JSON_LENGTH = "__length";
	public static final String JSON_SCALE = "__scale";
	AbsTableObjFun tableObjFun;
	
	public TypeColumn(String name, String value) {
		super(name, value);
		tableObjFun = FunBaseForm.createTabTool.getTableObjFun();
	}
	
	
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
		if (type[0] == null) type[0] = tableObjFun.getDefaultType();
		if (length[0] == null) length[0] = String.valueOf( tableObjFun.getTypeDefaultLength(tableObjFun.typeToEnum(type[0])) );
		
		JPanel myPanel = new JPanel(new GridLayout(0, 1));
		JTextField lengthField = new JTextField();
		lengthField.setText(length[0]);
		JTextField scaleField = new JTextField();
		JLabel scaleLabel = new JLabel("精度:");
		JComboBox<?> comboBox = new JComboBox<>(tableObjFun.getAllTypeStr().toArray());
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
				lengthField.setText(lengthValue != null ? String.valueOf(lengthValue) : "");
				scaleField.setText("");
				myPanel.updateUI();
			}
		});
		
		myPanel.add(new JLabel("数据类型:"));
		myPanel.add(comboBox);
		myPanel.add(new JLabel("长度:"));
		myPanel.add(lengthField);
		if (twoLengthType.contains(type[0].toUpperCase())) {
			myPanel.add(scaleLabel);
			scaleField.setText(StringUtils.isBlank(scale[0]) ? "" : scale[0]);
			myPanel.add(scaleField);
		}
		
		AtomicBoolean verify = new AtomicBoolean(true);
		HDialog hDialog = new HDialog(StartUtil.getMainDialog(), 460, 360,true) {
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
				if (verify.get()) dispose();
			}
			@Override
			protected void onCancel() {
				if (json != null) jsonObject = json;
			}
		};
		hDialog.setOnClickClose(false);
		hDialog.setWindowTitle("请选择数据类型");
		hDialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
		hDialog.setOption();
		HPanel panel =new HPanel();
		LastPanel lastPanel = new LastPanel(false);
		lastPanel.setHead(myPanel);
		panel.setLastPanel(lastPanel);
		hDialog.setRootPanel(panel);
		hDialog.getWindow().setAlwaysOnTop(hDialog.getWindow().isAlwaysOnTopSupported());
		hDialog.show();
		return jsonObject;
	}
	
	private boolean verify(String type, String[]... lengths) {
		boolean isNeedLength = tableObjFun.getNeedLengthType().contains(type.toUpperCase()) && (lengths == null || lengths[0] == null || StringUtils.isBlank(lengths[0][0]));
		boolean flag = true;
		if (isNeedLength) {
			PopPaneUtil.error("该类型必须填写长度");
			flag = false;
		} else {
			for (String[] length : lengths) {
				if (StringUtils.isNoneBlank(length[0])) {
					try {
						Integer.parseInt(length[0]);
					} catch (Exception e) {
						PopPaneUtil.error("数字格式错误");
						flag = false;
					}
				}
			}
		}
		return flag;
	}
}
