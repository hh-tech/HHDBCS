package com.hhdb.csadmin.plugin.backup_recovery.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.hhdb.csadmin.common.bean.ServerBean;
import com.hhdb.csadmin.plugin.backup_recovery.HBackupRecovery;
/**
 * 数据库恢复界面
 * @author zl
 *
 */
public class RestoreDataBasePanel extends RestorePanel{
	
	private static final long serialVersionUID = 1L;
	public HBackupRecovery bkr;
	private ServerBean server;
	
	private File file = null;
	private JPanel jpl = new JPanel();
	private RestoreDataBasePanel bpanel;
	private Thread mainthred;
	public RestoreDataBasePanel(HBackupRecovery bkr){
		super(460, 405);
		this.bkr = bkr;
		bpanel = this;
		ok = new BaseButton("开始");
		cancle = new BaseButton("关闭");
		ok.addActionListener(this);
		cancle.addActionListener(this);
		jpl.add(ok);
		jpl.add(cancle);
		add(tab,new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 10, 0, 0), 0, 0));
		add(tab1,new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 10, 10, 10), 0, 0));
		add(scroll,new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 8, 0,8), 0, 0));
		add(progressBar, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		add(jpl, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		initPanel();
	}
	private void initPanel() {
		try {
			server = bkr.serv.getServerbean();
			setTlabel11(server.getHost());
			setTlabel21(server.getDBName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("jz")) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);// 设置选择模式，既可以选择文件又可以选择文件夹
			int result = chooser.showOpenDialog(this); // 打开"打开文件"对话框
			if (result == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				textfd.setText(file.getPath());
			}
		}else if (e.getSource() == ok) {
			if (textfd.getText().trim().equals("")) {
				JOptionPane.showMessageDialog(bpanel, "请选择文件路径");
				return;
			}
			mainthred=new Thread(new Runnable() {
				@Override
				public void run() {
//					HHDBRestore restore = new HHDBRestore(treeNode, bpanel, new File(bpanel.getTextfd().toString()));
//					try {
//						restore.restore();
//					} catch (Exception ex) {
//						ex.printStackTrace();
//					} finally{
//						cancle.setText("关闭");
//					}
				}
			});
			mainthred.start();
		}else if (e.getSource() == cancle){
//			if(cancle.getText().equals("停止")){
//				cancle.setText("关闭");
//				if(Task.getActiveCount()>0){
//					Task.shutdown();
//				}
//				if(mainthred!=null&&mainthred.isAlive()){
//					mainthred.stop();
//				}
//			}else{
//				closeTask();
//			}
		}
	}
//	@Override
//	public void closeTask(){
//		if(Task.getActiveCount()>0){
//			Task.shutdown();
//		}
//		if(mainthred!=null&&mainthred.isAlive()){
//			mainthred.stop();
//		}
//		BaseDialogInstance instance=((BaseDialogInstance)bpanel.getParent().getParent().getParent().getParent());
//		instance.dispose();
//	}
	
	public BaseButton getCancle() {
		return cancle;
	}
}
