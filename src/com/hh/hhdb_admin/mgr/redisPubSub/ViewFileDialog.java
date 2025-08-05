package com.hh.hhdb_admin.mgr.redisPubSub;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.util.CmdUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.lob_panel.LobEditor;
import com.hh.hhdb_admin.common.lob_panel.LobViewer;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;

/**
 * byte文件查看弹出框
 */
public class ViewFileDialog {
	private HDialog dialog;
	private LobViewer viewer;
	private RadioGroupInput rInput;
	private HButton cleanBtn;
	
	private String filePath;
	
	/**
	 * 显示byte文件内容弹框
	 * @param filePath 文件路径
	 */
	public ViewFileDialog(String filePath) {
		try {
			this.filePath = filePath;
			this.filePath = filePath;
			LastPanel panel = getLastPanel(FileUtils.readFileToByteArray(new File(filePath)));
			HPanel dPanel = new HPanel();
			dPanel.setLastPanel(panel);
			dialog = new HDialog(900, 900 / 4 * 3);
			dialog.setRootPanel(dPanel);
			dialog.setWindowTitle(LangMgr.lang(Word2.EXAMINE));
			dialog.setStatusBar(new HBarPanel());
			dialog.show();
			viewer.getTextArea().getRSyntaxTextArea().requestFocus();
			panel.updateUI();
		} catch (IOException e) {
			Lg.error(ViewFileDialog.class, e);
			PopDlgUtil.error(CsVarUtil.parentFrame,e);
		}
	}

	private LastPanel getLastPanel(byte[] bytes) throws IOException {
		cleanBtn = new HButton(LangMgr.sLang(CsL.SELECT_APP_OPEN)){
			@Override
			protected void onClick() {
				try {
					HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
					hfc.setDialogTitle(LangMgr.lang(Word2.CHOOSE));
					hfc.setApproveButtonText(LangMgr.lang(Word2.CONFIRM));
					if ( hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION){
						CmdUtil.exeOpenFile(hfc.getSelectedFile().getPath(),filePath);
						dialog.dispose();
					}
				}catch (Exception e1){
					Lg.error(ViewFileDialog.class, e1);
					PopDlgUtil.error(dialog,e1);
				}
			}
		};
		
		viewer = new LobViewer();
		viewer.setReadOnly(true);
		viewer.loadData(bytes);
		viewer.getExportBtn().setEnabled(true);
		
		HPanel radioPanel = new HPanel(new HDivLayout(GridSplitEnum.C1, GridSplitEnum.C1, GridSplitEnum.C1));
		rInput = new RadioGroupInput("RADIO_GROUP", radioPanel);
		rInput.add(LobViewer.TEXT, LangMgr.lang(Word2.TEXT));
		rInput.add(LobViewer.IMAGE, LangMgr.lang(Word2.PICTURE));
		rInput.add(LobViewer.UNKNOWN, LangMgr.lang(Word2.OTHER));
		rInput.setTitle(LangMgr.lang(Word2.TYPE));
		rInput.setSelected(viewer.genType().equalsIgnoreCase(LobEditor.NULL) ? LobViewer.TEXT : viewer.genType());
		rInput.addItemListener(e -> {
			try {
				if (e.getStateChange() != ItemEvent.SELECTED) return;
				String type = ((JRadioButton) e.getItem()).getName();
				if (type.equals(LobViewer.UNKNOWN)) {
					viewer.getToolBar().add(cleanBtn);
				} else {
					viewer.getToolBar().remove(cleanBtn);
				}
				viewer.changeType(type);
			} catch (IOException ioException) {
				Lg.error(ViewFileDialog.class, ioException);
				PopDlgUtil.error(CsVarUtil.parentFrame,ioException);
			}
		});
		
		LastPanel panel = new LastPanel();
		panel.setWithScroll(viewer.getComp());
		panel.setHead(radioPanel.getComp());
		return panel;
	}
}
