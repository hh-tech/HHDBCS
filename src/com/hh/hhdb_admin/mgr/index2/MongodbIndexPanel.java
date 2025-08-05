package com.hh.hhdb_admin.mgr.index2;

import java.awt.Window;
import java.util.Collections;

import javax.swing.JDialog;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.mongo.obj.MongoColl;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

public class MongodbIndexPanel extends LastPanel {
    private CheckBoxInput soleInput;
    private TextInput nameInput;
    
    
    private String schemaName;
    private String tableName;
    public MongodbIndexPanel(String schema, String tabName)  {
    	this.tableName = tabName;
		this.schemaName = schema;
        HBarLayout barLayout = new HBarLayout();
        barLayout.setTopHeight(10);
        barLayout.setBottomHeight(10);
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton button = new HButton(LangMgr.lang(Word2.SAVE)) {
            @Override
            protected void onClick() {
                if (checkForm()) {
                    try {
                        MongoColl mongoColl = new MongoColl(CsVarUtil.conn,schemaName);
                        mongoColl.createIndex(tableName, Collections.singletonList(nameInput.getValue()),soleInput.isChecked());
                        inform();
                        Window w = HDialog.getWindowForComponent(this.comp);
                    	if(w instanceof JDialog) {
                    		w.dispose();
                    	}
                        PopDlgUtil.info(this.getComp(), LangMgr.lang(Word4.SAVED_SUCCESSFULLY));
                    } catch (Exception e) {
                        Lg.error(MongodbIndexPanel.class, e);
                        PopDlgUtil.error(CsVarUtil.parentFrame,e);
                    }
                }
            }
        };
        button.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
        
        HButton cancelButton = new HButton(LangMgr.lang(Word2.CANCEL)) {
            @Override
            protected void onClick() {
            	Window w = HDialog.getWindowForComponent(this.comp);
            	if(w instanceof JDialog) {
            		w.dispose();
            	}
            	
            }
        };
        cancelButton.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
        
       
        barPanel.add(button,cancelButton);
        
        setFoot(barPanel.getComp());
        
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C3);
        divLayout.setyGap(10);
        divLayout.setxBorderWidth(30);
        divLayout.setTopHeight(20);
        HPanel panel = new HPanel(divLayout);
        nameInput = new TextInput("name", "");
        panel.add(new LabelInput(LangMgr.lang(Word2.KEY_NAME2) + " * : "), nameInput);
        soleInput = new CheckBoxInput("");
        panel.add(new LabelInput("唯一索引 : "), soleInput);
        
        set(panel.getComp());
    
    }
    protected void inform() {
		System.out.println("通知更新");
	}
    private boolean checkForm() {
        if (StringUtils.isBlank(nameInput.getValue())) {
            PopDlgUtil.info(this.comp, "请填写键名！");
            return false;
        }
        return true;
    }

	
    

	
    
   
}
