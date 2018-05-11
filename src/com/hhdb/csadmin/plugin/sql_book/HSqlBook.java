package com.hhdb.csadmin.plugin.sql_book;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.hh.frame.common.log.LM;
import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.ErrorEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.sql_book.ui.ShortcutPanel;

/**
 * sql宝典插件
 * @author hhxd
 *
 */
public class HSqlBook extends AbstractPlugin {
	public String PLUGIN_ID = HSqlBook.class.getPackage().getName();
	
	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent relEvent= EventUtil.getReplyEvent(HSqlBook.class, event);
		if(event.getType().equals(EventTypeEnum.CMD.name())){       //打开sql宝典面板
			try {
				if(event.getValue("CMD").equals("SQLBook")){
						BooksPanel bookp = new BooksPanel(this,true);
						bookp.sqls.getTabPanelTable("sql宝典");
				}else if(event.getValue("CMD").equalsIgnoreCase("querySQLBook")) { //查询器打开sql宝典面板加载文件
						BooksPanel bookp = new BooksPanel(this,true);
						bookp.source = false; 
						bookp.jtext = (JTextArea) event.getObj();
						relEvent.setObj(bookp);
				}else if(event.getValue("CMD").equalsIgnoreCase("querySQLSave")) { //查询器中打开路径选择
						BooksPanel bookp = new BooksPanel(this,false);
						bookp.source = false;
						bookp.sql = event.getValue("sql");
						//获取路劲选择页面
						relEvent.setObj(new ShortcutPanel(bookp,-1,"选择保存地址",800,400,bookp.sqls.getBaseFrame()));
				}else if(event.getValue("CMD").equalsIgnoreCase("RemovePanelEvent")) { //关闭打开页面事件
					
				}
			} catch (Exception e) {
				LM.error(LM.Model.CS.name(), e);
				JOptionPane.showMessageDialog(null, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
				return relEvent;
			}
			return relEvent;
		}else{
			ErrorEvent errorEvent = new ErrorEvent(PLUGIN_ID,event.getFromID(),ErrorEvent.ErrorType.EVENT_NOT_VALID.name());
			errorEvent.setErrorMessage(PLUGIN_ID + "不能接受如下类型的事件:\n"+ event.toString());
			return errorEvent;
		}

	}
	
	/**
	 * 获取新的树面板
	 * @return
	 */
	public BooksPanel getTreePanel(Integer myDirId,ShortcutPanel shp,BooksPanel boop){
		BooksPanel bp = new BooksPanel(this,false);
		bp.myDirId = myDirId;
		bp.shp= shp;
		bp.source = boop.source; 
		bp.sql = boop.sql;
		return bp;
		
	}
	
	@Override
	public Component getComponent() {
		return null;
	}

}
