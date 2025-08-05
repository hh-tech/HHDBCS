package com.hh.hhdb_admin.mgr.table_open3.gui;

import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word1;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word3;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.IntSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.ProgressBarInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;

public class PkgPageBar extends HBarPanel {
	private LabelInput statusLabel;
	//第一页
	private HImgButton firstPage;
	//上一页
	private HImgButton prePage;
	//当前页数
	private TextInput curPageNum;
	//设置页大小
	private HImgButton pageSizeBut;
	//下一页
	private HImgButton nextPage;
	//最后一页
	private HImgButton lastPage;
	//获取总行数
	private HImgButton totalPageBtn;
	//总行数显示label
	private LabelInput pageTotalLabel;
//	//总页码数记录
//	private int pageCount;
	//总行数加载进度条
	private ProgressBarInput progressBar;

	private long pageSizeNum;

	private final int curPageNumColumns = 5;
	private OpenTableComp otc;

	public PkgPageBar(OpenTableComp otc) {
		this.otc = otc;
		init();
		
		Border emptyBorder = BorderFactory.createEmptyBorder(3, 2, 0, 3);
		getComp().setBorder(emptyBorder);
	}

	private void init() {
		statusLabel = new LabelInput();

		totalPageBtn = new HImgButton() {
			@Override
			protected void onClick() {
				replace(pageTotalLabel, progressBar);
				progressBar.setValue(50);
				otc.loadCount();
			}
		};
		totalPageBtn.setMouseExitedIcon(FaSolidEnum.MAGNIFYING_GLASS_CHART,16);
		totalPageBtn.setMouseEnteredIcon(FaSolidEnum.MAGNIFYING_GLASS_CHART,16);
		totalPageBtn.setToolTipText(LangMgr.sLang(CsL.GET_HEAD_OFFICE));
		progressBar = new ProgressBarInput();

		
		pageTotalLabel = new LabelInput(pageSizeNum + "+", AlignEnum.LEFT);

		firstPage = new HImgButton() {
			@Override
			protected void onClick() {
				if(otc.isCancelSaveData()) {
					otc.page.setCurrentPage(1);
					otc.loadData();
				}
			}
		};
		firstPage.setMouseExitedIcon(FaSolidEnum.ANGLES_LEFT,16);
		firstPage.setMouseEnteredIcon(FaSolidEnum.ANGLES_LEFT,16);
		firstPage.setToolTipText(LangMgr.lang(Word3.FIRST_PAGE));

		prePage = new HImgButton() {
			@Override
			protected void onClick() {
				if(otc.isCancelSaveData()) {
					int curPage = Integer.parseInt(curPageNum.getValue());
					otc.page.setCurrentPage(curPage-1);
					otc.loadData();
				}
			}
		};
		prePage.setMouseExitedIcon(FaSolidEnum.ANGLE_LEFT,16);
		prePage.setMouseEnteredIcon(FaSolidEnum.ANGLE_LEFT,16);
		prePage.setToolTipText(LangMgr.lang(Word3.PREVIOUS));

		curPageNum = new TextInput("1", "1") {
			@Override
			public void setValue(String value) {
				if(otc.isCancelSaveData()) {
					super.setValue(value);
					int len = value.trim().length();
					if(len>curPageNumColumns) {
						curPageNum.getComp().setColumns(len);
					}
				}
			}
			
			
		};
		
		//回车监听
		curPageNum.getComp().addActionListener(e -> {
			String value = curPageNum.getValue();
				try {
					int p = Integer.parseInt(value);
					if(p>otc.page.getTotalPages()) {
						PopDlgUtil.error(getComp(), "已超出最大页数");
					}else if(p<1) {
						PopDlgUtil.error(getComp(), "页码数不能小于1");
					}else {
						otc.page.setCurrentPage(p);
        				otc.loadData();
					}
				}catch (Exception e1) {
					PopDlgUtil.error(getComp(), "请输入正确的页码");
				}
		});
		curPageNum.getComp().setColumns(curPageNumColumns);
		curPageNum.getComp().setHorizontalAlignment(JTextField.CENTER);

		//设置页大小按钮
        pageSizeBut=new HImgButton() {
        	@Override
        	protected void onClick() {
        		IntSpinner intSpinner = new IntSpinner(1, null, null, 1);
        		HDialog dialog = new HDialog(getComp(),500,120,false) {
        			@Override
        			protected void onConfirm() {
        				if(otc.isCancelSaveData()) {
	        				otc.page.setNumPerPage(intSpinner.getInt());
	        				otc.page.setCurrentPage(1);
	        				otc.loadData();
        				}
        			}
        			
        			@Override
        			protected void onCancel() {
        				dispose();
        			}
        		};
        		dialog.setWindowTitle(LangMgr.lang(Word2.SETTINGS) + LangMgr.lang(Word4.PAGE_SIZE));
        		HBarLayout layout = new HBarLayout();
        		layout.setTopHeight(15);
        		layout.setBottomHeight(15);
        		layout.setAlign(AlignEnum.LEFT);
        		HBarPanel barPanel  = new HBarPanel(layout);
        		
        		
        		
        		intSpinner.setValue(otc.page.getNumPerPage());
        		barPanel.add(new LabelInput(LangMgr.lang(Word4.PAGE_SIZE)+":"));
        		barPanel.setLastHComp(intSpinner);
        		
        		HDivLayout hl = new HDivLayout();
        		hl.setLeftWidth(50);
        		hl.setRightWidth(50);
        		HPanel panel = new HPanel(hl);
        		panel.add(barPanel);
        		
        		dialog.setRootPanel(panel);
        		dialog.show();
        		
        		
        		
        	}
        };
		pageSizeBut.setMouseExitedIcon(FaSolidEnum.SCREWDRIVER_WRENCH,16);
		pageSizeBut.setMouseEnteredIcon(FaSolidEnum.SCREWDRIVER_WRENCH,16);
		nextPage = new HImgButton() {
			@Override
			protected void onClick() {
				if(otc.isCancelSaveData()) {
					int curPage = Integer.parseInt(curPageNum.getValue());
					otc.page.setCurrentPage(curPage+1);
					otc.loadData();
				}
			}
		};
		nextPage.setMouseExitedIcon(FaSolidEnum.ANGLE_RIGHT,16);
		nextPage.setMouseEnteredIcon(FaSolidEnum.ANGLE_RIGHT,16);
		nextPage.setToolTipText(LangMgr.lang(Word3.NEXT));

		lastPage = new HImgButton() {
			@Override
			protected void onClick() {
				if(otc.isCancelSaveData()) {
					otc.page.setCurrentPage(otc.page.getTotalPages());
					otc.loadData();
				}
			}
		};
		lastPage.setMouseExitedIcon(FaSolidEnum.ANGLES_RIGHT,16);
		lastPage.setMouseEnteredIcon(FaSolidEnum.ANGLES_RIGHT,16);
		lastPage.setToolTipText(LangMgr.lang(Word4.LAST_PAGE));

		LabelInput label1 = new LabelInput(LangMgr.lang(Word1.FIRST));
		LabelInput label2 = new LabelInput(LangMgr.lang(Word1.PAGE));

		add(statusLabel, totalPageBtn, pageTotalLabel);
		add(firstPage, prePage);
		add(label1, curPageNum, label2);
		add(pageSizeBut, nextPage, lastPage);

		prePage.setEnabled(false);
		firstPage.setEnabled(false);
		
	}
	
	
	
	/**
	 * 
	 * @param haomiao 查询持续的毫秒数
	 */
	public void refreshData(long haomiao){
			replace(progressBar,pageTotalLabel);
			double t = haomiao/1000.0;
			String timeTips = (new DecimalFormat(t > 1 ? "#.##" : "#.###").format(t)) + "s";
			statusLabel.setValue(" " + timeTips);
			statusLabel.setIcon(FaSolidEnum.CLOCK_ROTATE_LEFT,16);
			statusLabel.getComp().setToolTipText(LangMgr.lang(Word4.LOAD_TIME)+":" + timeTips);
			
			firstPage.setEnabled(otc.page.getCurrentPage()>1);
			nextPage.setEnabled(otc.page.getCurrentPage() < otc.page.getTotalPages());
			prePage.setEnabled(otc.page.getCurrentPage()>1);
			curPageNum.setValue(otc.page.getCurrentPage()+"");
			String text = String.format(LangMgr.lang(Word1.COMMON)+"%s"+LangMgr.lang(Word1.ROW)+"(%s"+LangMgr.lang(Word1.PAGE)+")",  otc.page.getTotalNum(), otc.page.getTotalPages());
//			String text = String.format(LangMgr.lang(Word3.TOTAL_ROWS)+": %s+ ("+LangMgr.sLang(CsL.GET_HEAD_OFFICE)+")"
//					, otc.page.getTotalPages());
//			String rows = String.format(LangMgr.lang(Word3.CURRENT_PAGE)+LangMgr.lang(Word1.ROWS)+" : %s (%s "+LangMgr.lang(Word1.PAGE)+")", otc.page.getTotalNum(), otc.page.getTotalPages());
			pageTotalLabel.setValue(text);
			lastPage.setEnabled(otc.page.getCurrentPage() < otc.page.getTotalPages());
	}
	
}