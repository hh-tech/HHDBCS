package com.hh.hhdb_admin.mgr.page_filter;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.IntSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;


public class PageFilterDlg extends HDialog {
	private TextInput filterInput;
	private IntSpinner numPerageInput;
	private IntSpinner currentPageInput;
	private TextInput totalPage;
	private PageFilterBean bean;

	public PageFilterDlg(int width, int height) {
		super(CsVarUtil.parentFrame,width, height);
		setWindowTitle(LangMgr.lang(Word2.FILTER));
		setIconImage(IconFileUtil.getLogo());
		setStatusBar(new HBarPanel());
		initPanel();
	}

	public PageFilterDlg() {
		this(300, 250);
	}

	public void initPanel() {
		HPanel rootPanel = new HPanel();
		HPanel dlgPanel = new HPanel();
		filterInput = new TextInput();
		numPerageInput = new IntSpinner(1,1,null,1);
		currentPageInput = new IntSpinner(1,1,null,1);
		totalPage = new TextInput();
		dlgPanel.add(new LabelInput(LangMgr.lang(Word4.FILTER_CONDITION) + ":", AlignEnum.LEFT), filterInput);
		dlgPanel.add(new LabelInput(LangMgr.lang(Word3.CURRENT_PAGE) + ":", AlignEnum.LEFT), currentPageInput);
		dlgPanel.add(new LabelInput(LangMgr.lang(Word3.NUM_PERAGE) + ":", AlignEnum.LEFT), numPerageInput);
		dlgPanel.add(new LabelInput(LangMgr.lang(Word2.INFO) + ":", AlignEnum.LEFT), totalPage);
		totalPage.setEnabled(false);

		HBarPanel barPanel = new HBarPanel(getLayout(AlignEnum.CENTER));
		HButton queryBtn = new HButton(LangMgr.lang(Word2.QUERY)) {
			protected void onClick() {
				bean.setFilter(filterInput.getValue());
				bean.setCurrentPage(Integer.parseInt(currentPageInput.getValue()));
				bean.setNumPerPage(Integer.parseInt(numPerageInput.getValue()));
				CsVarUtil.eng2.doCall(CsMgrEnum.TREE, bean);
				hide();
			}
		};
		queryBtn.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
		HButton cancelBtn = new HButton(LangMgr.lang(Word2.CANCEL)) {
			protected void onClick() {
				hide();
			}
		};
		cancelBtn.setIcon(FaSolidEnum.FLOPPY_DISK, 16);
		barPanel.add(queryBtn, cancelBtn);

		rootPanel.add(dlgPanel);
		rootPanel.add(barPanel);
		setResizable(true);
		setRootPanel(rootPanel);
	}

	public void show(PageFilterBean bean) {
		this.bean = bean;
		filterInput.setValue(bean.getFilter());
		currentPageInput.setValue(bean.getCurrentPage());
		numPerageInput.setValue(bean.getNumPerPage());
//		String totalPageNum = String.valueOf(bean.getTotalPages());
		totalPage.setValue(String.format("Pages[%d],Count[%d]", bean.getTotalPages(),bean.getTotalNum()));
		super.show();
	}

//	private void query() {
//		bean.setFilter(filterInput.getValue());
//		bean.setCurrentPage(Integer.parseInt(currentPageInput.getValue()));
//		bean.setNumPerPage(Integer.parseInt(numPerageInput.getValue()));
////		bean.setTotalPages(Integer.parseInt(totalPage.getValue()));
//		CsVarUtil.eng2.doCall(CsMgrEnum.TREE, bean);
//		super.hide();
//	}

//	protected void cancel() {
//		super.hide();
//	}

	private HBarLayout getLayout(AlignEnum align) {
		HBarLayout barLayout = HBarLayout.newLayout(align);
		barLayout.setTopHeight(5);
		barLayout.setBottomHeight(5);
		return barLayout;
	}

}
