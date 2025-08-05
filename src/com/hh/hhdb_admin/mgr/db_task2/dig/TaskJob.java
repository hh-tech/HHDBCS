package com.hh.hhdb_admin.mgr.db_task2.dig;


import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JobStatus;
import com.hh.frame.common.base.LogBean;
import com.hh.frame.dbtask2.AbsDbTask;
import com.hh.frame.swingui.view.job_panel.GuiJob;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.db_task2.TaskComp;
import com.hh.hhdb_admin.mgr.db_task2.TaskUtil;

import javax.swing.*;

public class TaskJob implements GuiJob {
    private AbsDbTask task = null;
    
    @Override
    public JobStatus getStatus() {
        return task.getStatus();
    }
    
    @Override
    public Throwable getExpt() {
        return task.getExpt();
    }
    
    @Override
    public String getName() {
        return task.getName() + "(" + TaskUtil.getTaskMap().get(task.getTaskType().name()) + ")";
    }
    
    @Override
    public void start() {
        task.start();
    }
    
    @Override
    public void stop() {
        task.stop();
    }
    
    @Override
    public void reset() {
        task.setStatus(JobStatus.PREPARE);
        showDlg(true);
    }
    
    @Override
    public boolean supportReset() {
        return true;
    }
    
    @Override
    public Integer getProcPercent() {
        return null;
    }
    

    
    @Override
    public Icon getIcon() {
        return IconFileUtil.getDbIcon(DBTypeEnum.valueOf(task.getDbType().name()));
    }
    
    @Override
    public void showInfoDlg() {
        showDlg(false);
    }
    
    @Override
    public boolean supportInfoDlg() {
        return true;
    }
    
	@Override
	public void showLogDlg() {
        LogBean logBean=new LogBean();
        logBean.addInfo(task.getInfo());
        PkgLogDlg pkgLogDlg=new PkgLogDlg(TaskComp.window.getWindow(), logBean) {
            @Override
            protected void refreshInform() {
                logBean.reset();
                logBean.addInfo(task.getInfo());
            }
        };
        pkgLogDlg.show();
	}

	@Override
	public boolean supportLogDlg() {
		return true;
	}
    
    public AbsDbTask getTask() {
        return task;
    }
    
    public void setTask(AbsDbTask task) {
        this.task = null;
        this.task = task;
    }
    
    
    protected void showDlg(Boolean isEdit) {
    
    }
}
