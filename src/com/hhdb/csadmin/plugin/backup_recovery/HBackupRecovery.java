package com.hhdb.csadmin.plugin.backup_recovery;

import java.awt.Component;

import javax.swing.JOptionPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.backup_recovery.service.SqlOperationService;
import com.hhdb.csadmin.plugin.backup_recovery.ui.BackupDatabasePanel;
import com.hhdb.csadmin.plugin.backup_recovery.ui.BackupSchemaPanel;
import com.hhdb.csadmin.plugin.backup_recovery.ui.RestoreDataBasePanel;

/**
 * 视图处理
 * 
 * @author hhxd
 * 
 */
public class HBackupRecovery extends AbstractPlugin {
	public String PLUGIN_ID = HBackupRecovery.class.getPackage().getName();
	public SqlOperationService serv;
	public BackupRecoveryPanel backupRecoveryPanel;
	public String name;     //操作对象名称   
	
	public HBackupRecovery(){
		serv = new SqlOperationService(this);
	}
	
	/**
	 * 重写插件接收事件
	 */
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent hevent = EventUtil.getReplyEvent(HBackupRecovery.class, event);
		if (event.getType().equals(EventTypeEnum.CMD.name())) {
			try {
				if (event.getValue("CMD").equals("DatabaseEackup")) {			//数据库备份
					this.name = event.getValue("database");
					BackupDatabasePanel bdp = new BackupDatabasePanel(this);
					backupRecoveryPanel = new BackupRecoveryPanel("数据库备份",460,490,bdp,serv.getBaseFrame());
				} else if (event.getValue("CMD").equals("DatabaseRecovery")) {  //数据库恢复
					this.name = event.getValue("database");
					RestoreDataBasePanel rdp = new RestoreDataBasePanel(this);
					backupRecoveryPanel = new BackupRecoveryPanel("数据库恢复",460,490,rdp,serv.getBaseFrame());
				} else if (event.getValue("CMD").equals("BackupMode")) { 		//模式备份
					this.name = event.getValue("model");
					BackupSchemaPanel rdp = new BackupSchemaPanel(this);
					backupRecoveryPanel = new BackupRecoveryPanel("模式备份",460,490,rdp,serv.getBaseFrame());
				}else if (event.getValue("CMD").equals("ModelRestore")){		//模式恢复	
					this.name = event.getValue("model");
					RestoreDataBasePanel rdp = new RestoreDataBasePanel(this);
					backupRecoveryPanel = new BackupRecoveryPanel("模式恢复",460,490,rdp,serv.getBaseFrame());
				}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "错误信息：" + e.getMessage(),"错误", JOptionPane.ERROR_MESSAGE);
				return hevent;
			}
			return hevent;
		} else {
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下事件:\n"+ event.toString());
			return errorEvent;
		}
	}

	@Override
	public Component getComponent() {
		return null;
	}
}
