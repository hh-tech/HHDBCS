package com.hh.hhdb_admin.mgr.database2.form;

import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.col.json.JsonCol;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.icon.IconFileUtil;

import java.awt.event.ActionEvent;

/**
 * SQLServer数据库文件增量设置
 */
public class GrowthSettingPanel extends JsonCol {
    private TextInput incrementInput;
    private TextInput maxInput;
    private CheckBoxInput unInput;
    private CheckBoxInput autoInput;
    private static final SelectBox incrementBox;
    private static final SelectBox maxBox;
    
    private JsonObject valJson;
    
    static {
        incrementBox = new SelectBox();
        incrementBox.addOption("KB", "KB");
        incrementBox.addOption("MB", "MB");
        incrementBox.addOption("GB", "GB");
        incrementBox.addOption("%", "%");
        maxBox = new SelectBox();
        maxBox.addOption("KB", "KB");
        maxBox.addOption("MB", "MB");
        maxBox.addOption("GB", "GB");
    }

    public GrowthSettingPanel(String name, String value) {
        super(name, value);
    }

    @Override
    public JsonObject onClick(JsonObject json, int row, int column) {
        this.valJson = json;
        incrementInput = new TextInput("", json.getString("fileGrowth"));
        incrementInput.setEnabled(!json.getString("fileGrowth").equals("0"));
        maxInput = new TextInput("",json.getString("maxSize").equals("UNLIMITED") ? "100" : json.getString("maxSize"));
        maxInput.setEnabled(!json.getString("maxSize").equals("UNLIMITED"));
        unInput = new CheckBoxInput("check",LangMgr.sLang(CsL.UNLIMITED_FILE_SIZE)) {
            @Override
            protected void onClick(ActionEvent e) {
                maxInput.setEnabled(!isChecked());
            }
        };
        unInput.setValue(json.getString("maxSize").equals("UNLIMITED"));
        autoInput = new CheckBoxInput("check",LangMgr.sLang(CsL.ENABLE_AUTO_GROWTH)) {
			@Override
			protected void onClick(ActionEvent e) {
                incrementInput.setEnabled(isChecked());
			}
        };
        autoInput.setValue(!json.getString("fileGrowth").equals("0"));
        incrementBox.setValue(json.getString("fileGrowthUnit"));
        maxBox.setValue(json.getString("maxSizeUnit"));
        
    
        HDivLayout layout = new HDivLayout(GridSplitEnum.C3,GridSplitEnum.C7);
        layout.setTopHeight(30);
        layout.setyGap(8);
        layout.setBottomHeight(10);
        layout.setRightWidth(20);
        HPanel incrementPanel = new HPanel(layout);
        incrementPanel.add(new LabelInput(LangMgr.lang(Word2.INCREMENT2)+":"), incrementInput, incrementBox);
        incrementPanel.add(new LabelInput(LangMgr.sLang(CsL.MAXIMUM_SIZE)+":"), maxInput, maxBox);
        incrementPanel.add(new LabelInput(""), unInput,null);
        incrementPanel.add(new LabelInput(""), autoInput,null);
    
        JsonObject res = valJson;
        HDialog dialog = new HDialog(new HWindow() {
			@Override
			public void setResizable(boolean resizable) {
				
			}
        	
        },400, 320,true){
            protected void onConfirm() {
                submitClick(res);
                dispose();
            }
        };
        dialog.setWindowTitle(LangMgr.sLang(CsL.AUTO_INCR_SETTING));
        dialog.setRootPanel(incrementPanel);
        dialog.setIconImage(IconFileUtil.getLogo());
        dialog.show();
        return res;
    }

    public void submitClick(JsonObject data) {
        StringBuilder text = new StringBuilder(LangMgr.sLang(CsL.INCREMENTS));
        data.set("fileGrowth", autoInput.isChecked() ? incrementInput.getValue() : "0");
        data.set("fileGrowthUnit",autoInput.isChecked() ? incrementBox.getValue() : "KB");
        text.append(autoInput.isChecked() ? incrementInput.getValue()+incrementBox.getValue() : "0");
        
        data.set("maxSize",unInput.isChecked() ? "UNLIMITED" : maxInput.getValue());
        data.set("maxSizeUnit",unInput.isChecked() ? "KB" : maxBox.getValue());
        text.append(unInput.isChecked() ? ","+LangMgr.sLang(CsL.UNLIMITED_FILE_GROWTH) : ","+LangMgr.sLang(CsL.MAXIMUM_SIZE)+ maxInput.getValue() + maxBox.getValue());
        
        data.set(JsonCol.__TEXT, text.toString());
    }
}
