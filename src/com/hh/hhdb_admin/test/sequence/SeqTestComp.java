package com.hh.hhdb_admin.test.sequence;

import java.sql.Connection;

import javax.swing.JOptionPane;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
import com.hh.hhdb_admin.test.AbsMainTestComp;
import com.hh.hhdb_admin.test.MgrTestUtil;


public class SeqTestComp extends AbsMainTestComp{
	public JdbcBean jdbc;
	public Connection conn;
	
	public SeqTestComp() {
		getConn();
		init();
		
	}
	
	public Connection getConn(){
		
		try {
			jdbc=MgrTestUtil.getJdbcBean();
			SequenceMgr.conn = ConnUtil.getConn(jdbc);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
			return conn;
	}

	public void init() {
		HBarPanel toolBar=new HBarPanel();
		
			HButton hbt = new HButton("测试新增序列") {
				@Override
				protected void onClick() {
					JsonObject obj = GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_CREATE);
					String schemaName=JOptionPane.showInputDialog(null, "获取模式名","public");
					System.out.println("新增序列从树节点获取模式名: "+schemaName);
					obj.set(StartUtil.PARAM_SCHEMA,schemaName);
		            StartUtil.eng.doPush(CsMgrEnum.SEQUENCE,obj);
				}
			};
			
			HButton hbt1 = new HButton("测试设计序列") {
				@Override
				protected void onClick() {
					String schemaName=JOptionPane.showInputDialog(null, "获取模式名","public");
					String seqName=JOptionPane.showInputDialog(null, "获取序列名",null);
					System.out.println("设计序列从树节点获取模式名: "+schemaName+",序列名："+seqName);
		            StartUtil.eng.doPush(CsMgrEnum.SEQUENCE,GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_DESIGN)
		            	.set(StartUtil.PARAM_SCHEMA,schemaName)
				        .set(SequenceMgr.SEQ_NAME,seqName));
				}
			};
			HButton hbt2 = new HButton("重命名") {
				@Override
				protected void onClick() {
					JsonObject obj = GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_RENAME);
					String schemaName=JOptionPane.showInputDialog(null, "模式名","public");
					String seqName=JOptionPane.showInputDialog(null, "序列名",null);
					System.out.println("重命名序列从树节点获取模式名: "+schemaName+",序列名："+seqName);
					obj.set(StartUtil.PARAM_SCHEMA,schemaName)
		            .set(SequenceMgr.SEQ_NAME,seqName);
		            StartUtil.eng.doPush(CsMgrEnum.SEQUENCE,obj);
		          
				}
			};
			HButton hbt3 = new HButton("删除") {
				@Override
				protected void onClick() {
					JsonObject obj = GuiJsonUtil.toJsonCmd(SequenceMgr.CMD_DELETE);
					String schemaName=JOptionPane.showInputDialog(null, "输入模式名","public");
					String seqName=JOptionPane.showInputDialog(null, "输入要删除的序列名",null);
					System.out.println("删除序列从树节点获取模式名: "+schemaName+",序列名："+seqName);
					obj.set(StartUtil.PARAM_SCHEMA,schemaName);
		            obj.set(SequenceMgr.SEQ_NAME,seqName);
		            StartUtil.eng.doPush(CsMgrEnum.SEQUENCE,obj);
		    
				}
			};
			
			toolBar.add(hbt);
			toolBar.add(hbt1);
			toolBar.add(hbt2);
			toolBar.add(hbt3);
			tFrame.setToolBar(toolBar);
			
		
	}
}
