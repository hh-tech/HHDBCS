package com.hh.hhdb_admin.mgr.sequence.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.util.db.SqlExeUtil;
import com.hh.frame.common.util.db.SqlQueryUtil;
import com.hh.frame.create_dbobj.sequenceMr.SeqItem;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.container.tab_panel.HeaderConfig;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.mgr.sequence.common.AbsSeqComp;
import com.hh.hhdb_admin.mgr.sequence.common.PreviewComp;
import com.hh.hhdb_admin.mgr.sequence.common.SeqCompUtil;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.sql.SQLException;
/**
 * 整合序列基础面板及SQl预览，增删改查SQL执行
 * @author HuBingBing
 * @date 2020年12月22日上午9:16:17
 */
public abstract class SeqComp extends HPanel {

	 	private	HTabPanel htab;
	 	HBarPanel toolBar ;
	    private AbsSeqComp seqComp;
	    private PreviewComp previewComp;
	    private HButton save;
	    private boolean flag;
	    private HDialog hdialog;

	public SeqComp(DBTypeEnum type,String schema,String seq,boolean isEdit) {
		flag=isEdit;
    	seqComp = SeqCompUtil.getSeqBaseComp(type,schema, seq, isEdit);
    	previewComp = new PreviewComp();
    	HBarLayout l = new HBarLayout();
		l.setAlign(AlignEnum.LEFT);
		l.setxGap(2);
		toolBar = new HBarPanel(l);
    	htab  = new HTabPanel();
    	initComp();
	}



		public void initComp() {

	    	save  = new HButton(SequenceMgr.getLang("save")) {
	    		@Override
	    		protected void onClick() {
	    			boolean saveflag;
	    			if(!flag) {
	    			saveflag=createSeq();
	    			if(saveflag) {
	    				hdialog.dispose();
	    				refreshSeq();
	    			}

	    			}else {
	    			saveflag=updataSeq();
	    			if(saveflag)
	    				seqComp.setEditVal();
	    				previewComp.setSqlViewValue("");
	    				refreshSeq();
	    			}

	   		}
	    	};
	    	save.setIcon(getIcon("save"));
	    	toolBar.add(save);


	    	htab.addPanel("id_seqGen", seqComp,new HeaderConfig(SequenceMgr.getLang("norm")).setFixTab(true));
	    	htab.addPanel("id_seqPreview",  previewComp,new HeaderConfig(SequenceMgr.getLang("preview")).setFixTab(true));

	    	((JTabbedPane)htab.getComp()).addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(ChangeEvent e) {

		            	  if(!flag) {
		            		  previewComp.setSqlViewValue(seqComp.getCreateSql());
		            	  }else {
		            		  String updatesql=seqComp.getDesignSql().get(SeqItem.updateSQL.name());
		            		  String selectsql=seqComp.getDesignSql().get(SeqItem.selectSQL.name());
		            		  if(selectsql!=null) {
		            			  previewComp.setSqlViewValue(updatesql+selectsql);
		            		  }else {
		            			  previewComp.setSqlViewValue(updatesql);
		            		  }

		            	  }
		              }
			});

	    	add(toolBar);
	    	add(htab);

	    }

	    public void showCreate(HDialog dialog) {
	    	hdialog=dialog;
	    	show(hdialog, SequenceMgr.getLang("new"));

	    }

	    public void showDesgin(HDialog dialog) {
	    	show(dialog, SequenceMgr.getLang("design"));
	    }

	    public void show(HDialog dialog,String title) {
	    	dialog.setRootPanel(this);
			dialog.setWindowTitle(title);
			dialog.show();
	    }

		/**
		 * 重命名
		 * @author HuBingBing
		 * @date 2020年12月21日下午5:26:35
		 * @param newSeqName 新的序列名
		 * void
		 *
		 */
		public void reNameSeq(String newSeqName) {
			String sql=seqComp.getRenameSql();
			try {
				SqlExeUtil.executeUpdate(SequenceMgr.conn, String.format(sql,newSeqName));
				JOptionPane.showMessageDialog(null,SequenceMgr.getLang("modifySuccess"));
				refreshSeq();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), SequenceMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
			}

		}
		/**
		 * 删除序列
		 * @author HuBingBing
		 * @date 2020年12月21日下午5:26:15
		 * void
		 *
		 */
		public void deleteSeq() {
			String sql=seqComp.getDeleteSql();
			try {
				SqlExeUtil.executeUpdate(SequenceMgr.conn, sql);
				refreshSeq();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), SequenceMgr.getLang("error"), JOptionPane.ERROR_MESSAGE);
			}

		}
		/**
		 * 新增序列
		 * @author HuBingBing
		 * @date 2020年12月21日下午5:26:08
		 * @return
		 * boolean
		 *
		 */
		public boolean createSeq() {

			boolean flag=true;
			try {
				 SqlExeUtil.executeUpdate(SequenceMgr.conn,seqComp.getCreateSql());
				 JOptionPane.showMessageDialog(null,SequenceMgr.getLang("addSuccess"));
			} catch (Exception e) {
				flag=false;
				JOptionPane.showMessageDialog(null, e.getMessage()+SequenceMgr.getLang("addFail"), "",JOptionPane.ERROR_MESSAGE);

			}
			return flag;

		}
		/**
		 * 设计序列
		 * @author HuBingBing
		 * @date 2020年12月21日下午5:25:49
		 * @return
		 * boolean
		 *
		 */
		public boolean updataSeq() {

			boolean flag=true;
			String designsql=seqComp.getDesignSql().get(SeqItem.updateSQL.name());
			String selectsql=seqComp.getDesignSql().get(SeqItem.selectSQL.name());
			try {
				if(!"".equals(designsql)){
					SqlExeUtil.executeUpdate(SequenceMgr.conn, designsql);
				}
				if(!"".equals(selectsql)&&selectsql!=null){
					SqlQueryUtil.select(SequenceMgr.conn, selectsql);
				}
				JOptionPane.showMessageDialog(null,SequenceMgr.getLang("modifySuccess"));
			} catch (Exception e) {
				flag=false;
				JOptionPane.showMessageDialog(null, e.getMessage()+SequenceMgr.getLang("modifyFail"), SequenceMgr.getLang("error"),JOptionPane.ERROR_MESSAGE);

			}
			return flag;
		}
	    protected abstract void refreshSeq();

	    private ImageIcon getIcon(String name) {
	        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.SEQUENCE.name(), name,IconSizeEnum.SIZE_16));
	    }
}
