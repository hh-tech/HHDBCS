package com.hhdb.csadmin.plugin.tool_bar;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.common.util.StartUtil;

public class HToolBar extends AbstractPlugin {
	private JPanel jPanel = new JPanel();

	public HToolBar() {		
	}
	
	private void init(){
		JToolBar toolbar1 = new JToolBar();
		jPanel.setLayout(new GridLayout(1, 2));
		ImageIcon switchi = new ImageIcon(ClassLoader.getSystemResource("icon/add_server.png"));
		//ImageIcon permission = new ImageIcon(ClassLoader.getSystemResource("icon/permission.png"));
		ImageIcon query = new ImageIcon(ClassLoader.getSystemResource("icon/query.png"));
		ImageIcon cmd = new ImageIcon(ClassLoader.getSystemResource("icon/cmdicon.png"));
		ImageIcon sqlbook = new ImageIcon(ClassLoader.getSystemResource("icon/book.png"));
		ImageIcon test = new ImageIcon(ClassLoader.getSystemResource("icon/runview.png"));
		ImageIcon monitor = new ImageIcon(ClassLoader.getSystemResource("icon/sequenceindex.png"));
		//ImageIcon remote = new ImageIcon(ClassLoader.getSystemResource("icon/remotemanage.png"));
		List<JButton> jbList = new ArrayList<JButton>();
		JButton switchb = new JButton("切换连接", switchi);
		jbList.add(switchb);
		/*JButton permissionb = new JButton("权限", permission);
		jbList.add(permissionb);*/
		JButton queryb = new JButton("查询器", query);
		jbList.add(queryb);
		JButton cmdb = new JButton("命令行", cmd);
		jbList.add(cmdb);
		JButton sqlbookb = new JButton("sql宝典", sqlbook);
		jbList.add(sqlbookb);
		JButton testb = new JButton("测试事件", test);
		jbList.add(testb);
		JButton monitorb = new JButton("库监控", monitor);
		jbList.add(monitorb);
		JButton cpu = new JButton("CPU监控", monitor);
		jbList.add(cpu);
		JButton mem = new JButton("内存监控", monitor);
		jbList.add(mem);
		JButton disk = new JButton("硬盘监控", monitor);
		jbList.add(disk);
		JButton net = new JButton("网络监控", monitor);
		jbList.add(net);
	  /*JButton remoteb = new JButton("远程服务", remote);
		remoteb.setEnabled(false);
		jbList.add(remoteb);*/
		for (final JButton jb : jbList) {
			jb.setHorizontalTextPosition(SwingConstants.CENTER);
			jb.setVerticalTextPosition(SwingConstants.BOTTOM);
			jb.setMargin(new Insets(10, 2, 10, 2));
			Dimension d = jb.getPreferredSize();
			d.width = 62;
			d.height=60;
			jb.setMaximumSize(d); 
			jb.addMouseListener(new MouseAdapter() {
			   //鼠标移入
			   public void mouseEntered(MouseEvent e) {
				   jb.setToolTipText(jb.getText());
			   }
			});
		}

		toolbar1.add(switchb);
		// toolbar1.add(permissionb);
		toolbar1.add(queryb);
		toolbar1.add(cmdb);
		toolbar1.add(sqlbookb);
//		toolbar1.add(testb);
		CmdEvent getspflag = new CmdEvent(HToolBar.class.getPackage().getName(),
				"com.hhdb.csadmin.plugin.conn","getSuperuser");
		HHEvent relEvent = sendEvent(getspflag);
		String spflag = relEvent.getValue("superuser_value");
		
		if(StartUtil.prefix.equals("hh")){
			toolbar1.add(monitorb);
			toolbar1.add(cpu);
			toolbar1.add(mem);
			toolbar1.add(disk);
			toolbar1.add(net);
		}
		
		if(!spflag.equals("true")){
			monitorb.setVisible(false);
			cpu.setVisible(false);
			mem.setVisible(false);
			disk.setVisible(false);
			net.setVisible(false);
		}
		// toolbar1.add(remoteb);
		toolbar1.setAlignmentX(0);
		switchb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.switch_tree";
				HHEvent hhEvent = new HHEvent(fromID, toID,EventTypeEnum.COMMON.name());
				sendEvent(hhEvent);
			}
		});

		/*permissionb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			}
		});*/

		queryb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// 先判断是否数据库节点或以下子节点
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.query";
				HHEvent hhEvent = new HHEvent(fromID, toID, EventTypeEnum.COMMON.name());
				sendEvent(hhEvent);
			}
		});

		cmdb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// 先判断是否数据库节点或以下子节点
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.cmd";
				HHEvent hhEvent = new HHEvent(fromID, toID,EventTypeEnum.COMMON.name());
				sendEvent(hhEvent);
			}
		});

		sqlbookb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.sql_book";
				CmdEvent hhEvent = new CmdEvent(fromID, toID, "SQLBook");
				sendEvent(hhEvent);
			}
		});

		testb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.event_test";
				HHEvent testevent = new HHEvent(fromID, toID, EventTypeEnum.COMMON.name());
				sendEvent(testevent);
			}
		});

		monitorb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.monitor";
				CmdEvent hhEvent = new CmdEvent(fromID, toID, "DBMonitor");
				sendEvent(hhEvent);
			}
		});

		cpu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.monitor";
				CmdEvent hhEvent = new CmdEvent(fromID, toID, "CPUMinitor");
				sendEvent(hhEvent);
			}
		});

		mem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.monitor";
				CmdEvent hhEvent = new CmdEvent(fromID, toID, "MemMinitor");
				sendEvent(hhEvent);
			}
		});

		disk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.monitor";
				CmdEvent hhEvent = new CmdEvent(fromID, toID, "DiskMinitor");
				sendEvent(hhEvent);
			}
		});

		net.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String fromID = HToolBar.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.monitor";
				CmdEvent hhEvent = new CmdEvent(fromID, toID, "NetMonitor");
				sendEvent(hhEvent);
			}
		});

		/*remoteb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
			}
		});*/

		jPanel.add(toolbar1);
	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		HHEvent replyE=EventUtil.getReplyEvent(HToolBar.class, event);
		if(event.getType().equals(EventTypeEnum.GET_OBJ.name())){
			init();
			replyE.setObj(jPanel);
			return replyE;
		} else if (event.getType().equals(EventTypeEnum.CMD.name())) {
			CmdEvent cmdEvent = (CmdEvent) event;
			if (cmdEvent.getCmd().equals("ToolbarEnableEvent")) {
				String toolbarName = cmdEvent.getValue("toolbarName");
				String enableBar = cmdEvent.getValue("enableBar");
				JToolBar toolbar1 = (JToolBar) jPanel.getComponent(0);
				Component[] comps = toolbar1.getComponents();
				for (Component comp : comps) {
					JButton jButton = (JButton) comp;
					if (toolbarName.equals(jButton.getText())) {
						if ("true".equals(enableBar)) {
							jButton.setEnabled(true);
						} else if ("false".equals(enableBar)) {
							jButton.setEnabled(false);
						}
					}
				}
			}else if (cmdEvent.getCmd().equals("ToolbarShowEvent")) {
				String toolbarName = cmdEvent.getValue("toolbarName");
				String showBar = cmdEvent.getValue("showBar");
				JToolBar toolbar1 = (JToolBar) jPanel.getComponent(0);
				Component[] comps = toolbar1.getComponents();
				for (Component comp : comps) {
					JButton jButton = (JButton) comp;
					if (toolbarName.equals(jButton.getText())) {
						if ("true".equals(showBar)) {
							jButton.setVisible(true);
						} else if ("false".equals(showBar)) {
							jButton.setVisible(false);
						}
					}
				}
				
			}
		}
		return replyE;
	}

	@Override
	public Component getComponent() {
		return jPanel;
	}


}
