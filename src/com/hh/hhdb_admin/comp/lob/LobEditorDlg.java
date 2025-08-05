package com.hh.hhdb_admin.comp.lob;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JRadioButton;

import com.hh.frame.db_tabmg.util.LobColUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.lob_panel.LobEditor;
import com.hh.hhdb_admin.common.lob_panel.LobViewer;

public class LobEditorDlg extends HDialog{
	private JsonObject retObj;
	public LobEditorDlg(Component parent,JsonObject json,boolean isBlob, String dataType,File saveFile) throws IOException {
		super(parent,800,600,true);
		
		byte[] old = LobColUtil.getBytesForJson(json, isBlob);
		LastPanel panel = new LastPanel(false);
		HPanel radioPanel = new HPanel(new HDivLayout(GridSplitEnum.C1, GridSplitEnum.C1, GridSplitEnum.C1));
		RadioGroupInput rInput = new RadioGroupInput("RADIO_GROUP", radioPanel);
		LobEditor editor = new LobEditor() {
			@Override
			protected void openCallback() {
				rInput.setSelected(fileType);
			}
		};
		retObj = json;
		HButton cleanBtn = new HButton(LangMgr.lang(Word2.ELIMINATE)) {
			@Override
			protected void onClick() {
				if(editor.getComp().isShowing()) {
					retObj = null;
					dispose();
				}
				
			}
		};
		HButton saveBtn = new HButton(LangMgr.lang(Word2.SAVE)) {
			@Override
			protected void onClick() {
				try {
					if(editor.getComp().isShowing()) {
						
						String type = rInput.getValue();
						byte[] newb = editor.getData();
						if(type.equals( LobViewer.TEXT)) {
							newb = editor.getTextArea().getText().getBytes(editor.getCode());
						}
						if(!Arrays.equals(old, newb)) {
							retObj = LobColUtil.getEditorJson(newb,editor.getCode(), saveFile, isBlob, dataType);
						}
					}
					dispose();
				}catch (Exception e) {
					PopDlgUtil.error(getComp(), e);
					Lg.error(getClass(), e);
				}
			}
		};
		editor.addToolBarBtn(cleanBtn, saveBtn);
		editor.loadData(old);
		
		panel.set(editor.getPanel().getComp());
		
		
		
		rInput.add(LobViewer.TEXT, LangMgr.lang(Word2.TEXT));
		rInput.add(LobViewer.IMAGE, LangMgr.lang(Word2.PICTURE));
		rInput.add(LobViewer.UNKNOWN, LangMgr.lang(Word2.OTHER));
		rInput.setTitle(LangMgr.lang(Word2.TYPE));
		rInput.setSelected(editor.genType().equalsIgnoreCase(LobEditor.NULL) ? LobViewer.TEXT : editor.genType());
		rInput.addItemListener(e -> {
			if (e.getStateChange() != ItemEvent.SELECTED) {
				return;
			}
			try {
				String type = ((JRadioButton) e.getItem()).getName();
				editor.changeType(type);
				editor.fileType = type;
			} catch (IOException ioException) {
				Lg.error(getClass(), ioException);
				PopDlgUtil.error(ioException);
			}

		});
		panel.setHead(radioPanel.getComp());
		setRootPanel(panel);
		setStatusBar(null);
	}
	
	
	
	
	public JsonObject getRetObj() {
//		System.out.println(retObj);
		return retObj;
	}


}
