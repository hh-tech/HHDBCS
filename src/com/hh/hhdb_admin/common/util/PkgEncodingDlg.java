package com.hh.hhdb_admin.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.hh.frame.chardet.ChardetUtil;
import com.hh.frame.chardet.jchardet.Constants;
import com.hh.frame.common.base.FileEncodingBean;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;

class PkgEncodingDlg extends HDialog {
	private FileEncodingBean feBean=null;
	private FileChooserInput fileChooseInput =null;
	private SelectBox encodingBox ;
	public PkgEncodingDlg() {
		super(CsVarUtil.parentFrame, 500, 200, true);
		HDivLayout layout = HDivLayout.newFormLayout();
		layout.setLeftWidth(20);
		layout.setRightWidth(20);
		HPanel panel = new HPanel(layout);
		encodingBox = new SelectBox();
		List<String> list = new ArrayList<String>();
		list.add(Constants.CHARSET_UTF_8);
		list.add(Constants.CHARSET_GB18030);
		list.add(Constants.CHARSET_GB2312);
		list.add(Constants.CHARSET_BIG5);
		list.add(Constants.CHARSET_US_ASCCI);
		list.add(Constants.CHARSET_SHIFT_JIS);
		for (String str : list) {
			encodingBox.addOption(str, str);
		}
		encodingBox.setValue(Constants.CHARSET_UTF_8);
		fileChooseInput= new FileChooserInput() {
			protected void onTextChange() {
				if (!StringUtils.isBlank(getValue()) && new File(getValue()).exists()) {
					try {
						String charsetName = ChardetUtil.detectCharset(new File(getValue()));
						if (list.contains(charsetName)) {
							encodingBox.setValue(charsetName);
						}
					} catch (IOException e) {
						Lg.error(getClass(), e);
					}
				}
			}
		};
		fileChooseInput.addFilter("脚本文件(txt|sh|bash|sql|py)", "txt",  "text",  "sql");
		fileChooseInput.addFilter("脚本文件(txt|sh|bash|sql|py)", "TXT", "TEXT", "SQL");
		panel.add(new LabelInput(LangMgr.lang(Word2.FILE)), fileChooseInput);
		
		panel.add(new LabelInput(LangMgr.lang(Word2.ENCODING)), encodingBox);
		this.setRootPanel(panel);
	}
	@Override
	protected void onConfirm() {
		feBean=new FileEncodingBean();
		File f=new File(fileChooseInput.getValue());
		if(!f.exists()) {
			PopDlgUtil.error(f.getAbsolutePath()+":不存在");
			feBean=null;
			return;
		}
		feBean.setCharset(Charset.forName(encodingBox.getValue()));
		feBean.setFile(f);
	}
	
	@Override
	protected void onCancel() {
		feBean=null;
	}
	
	protected FileEncodingBean getFileEncodingBean(){
		return this.feBean;
	}
}
