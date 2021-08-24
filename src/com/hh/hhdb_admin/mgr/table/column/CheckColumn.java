package com.hh.hhdb_admin.mgr.table.column;

import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.TextAreaInput;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.util.StartUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * @author oyx
 * @date 2020-10-14  0014 16:26:06
 */
public class CheckColumn extends JsonCol {
	private final String title;

	public CheckColumn(String name, String value, String title) {
		super(name, value);
		this.title = title;
	}

	@Override
	public JsonObject onClick(JsonObject json, int row, int column) {
		final JsonObject[] res = {null};
		String initValue = null;
		if (json != null) {
			initValue = json.getString(__TEXT);
		}
		TextAreaInput textArea = new TextAreaInput("editCheck");
		textArea.setRows(6);
		if (initValue != null) {
			textArea.setValue(initValue);
		}
		HPanel pane = new HPanel();
		pane.add(textArea);
		HDialog d1 = new HDialog(StartUtil.getMainDialog(), 500, 270) {
			@Override
			protected void onConfirm() {
				String value = textArea.getValue();
				if (StringUtils.isNoneBlank(value)) {
					res[0] = new JsonObject();
					res[0].add(__TEXT, value == null ? "" : value);
				}
			}
		};
		d1.setTitle(title);
		d1.setOption();
		d1.setRootPanel(pane);
		d1.show();
		return res[0];
	}

}
