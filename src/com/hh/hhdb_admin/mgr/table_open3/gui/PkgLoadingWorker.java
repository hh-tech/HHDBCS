package com.hh.hhdb_admin.mgr.table_open3.gui;

import javax.swing.SwingWorker;

import com.hh.frame.db_tabmg.task.TaskInf;

abstract class PkgLoadingWorker extends SwingWorker<Boolean , Void>{
	private TaskInf[] infs;
	private Throwable e;
	public PkgLoadingWorker(TaskInf... inf){
		this.infs = inf;
		
	}
	@Override
	protected Boolean doInBackground() throws Exception {
		try {
			for(TaskInf i:infs) {
				if(!i.execute()) {
					throw i.getError();
				};
			}
			return true;
		}catch (Throwable e) {
			this.e = e;
			return false;
		}
	}
	
	
	@Override
	protected void done() {
		try {
			if(get()) {
				loadingSuccess();
			}else {
				loadingFaild(e);
			}
		} catch (Exception e) {
			loadingFaild(e);
		}
	}
	protected abstract void loadingSuccess();
	protected abstract void loadingFaild(Throwable e);

}
