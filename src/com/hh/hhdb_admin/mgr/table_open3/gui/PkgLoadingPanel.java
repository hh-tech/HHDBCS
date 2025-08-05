package com.hh.hhdb_admin.mgr.table_open3.gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.ProgressBarInput;

class PkgLoadingPanel extends JPanel{
	private static final long serialVersionUID = 7877334266740545174L;
	private LabelInput progressTimeLabel;
	private ProgressBarInput progressBar;

	private Long start;
	private int currentProgress;
	public PkgLoadingPanel() {
		super(new GridBagLayout());
		progressTimeLabel = new LabelInput();
		progressTimeLabel.getComp().setPreferredSize(new Dimension(200, 20));
		progressTimeLabel.setIcon(FaSolidEnum.CLOCK_ROTATE_LEFT,16);
		progressBar = new ProgressBarInput();
		progressBar.getComp().setPreferredSize(new Dimension(300, 15));
		progressBar.getComp().setIndeterminate(true);
		add(progressBar.getComp());
		add(new JLabel(" "));
		add(progressTimeLabel.getComp());
	}
	
	

	
	/**
	 * 启动进度条
	 */
	public void startProgress(SwingWorker<Boolean,Void> worker) {
		currentProgress = 0;
		progressBar.setValue(0);
		progressBar.getComp().setIndeterminate(false);
		progressTimeLabel.setValue("");
		start = System.currentTimeMillis();
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!(worker.isDone()||worker.isCancelled())) {
					currentProgress=Math.min(currentProgress+1, 99);
					progressBar.setValue(currentProgress);
					double t = (System.currentTimeMillis() - start) / 1000.0;
					String timeTips = (new DecimalFormat(t > 1 ? "#.##" : "#.###").format(t)) + "s";
					progressTimeLabel.setValue(timeTips);
					SleepUtil.sleep100();
				}
			}
		});
		t.start();
	}
	
	
	



	
	
	
	
}
