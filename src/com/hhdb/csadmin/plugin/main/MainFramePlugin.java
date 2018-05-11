package com.hhdb.csadmin.plugin.main;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.ui.BaseFrame;
import com.hhdb.csadmin.common.util.EventUtil;
import com.hhdb.csadmin.plugin.menu.Hmenu;
import com.hhdb.csadmin.plugin.output.OutputManger;
import com.hhdb.csadmin.plugin.status_bar.StatusBarManager;
import com.hhdb.csadmin.plugin.tabpane.TabbedPaneManager;
import com.hhdb.csadmin.plugin.tool_bar.HToolBar;
import com.hhdb.csadmin.plugin.tree.HTree;

public class MainFramePlugin extends AbstractPlugin {
	
	private BaseFrame frame = new BaseFrame();
	private JPanel leftJPanel = new JPanel();
	private JSplitPane rightJSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private JMenuBar jMenuBar = null;
	private static final int DIVIDER_SIZE = 4;

	@Override
	public HHEvent receEvent(HHEvent event) {
		
		HHEvent replyE=EventUtil.getReplyEvent(MainFramePlugin.class, event);
		if(event.getType().equals(EventTypeEnum.COMMON.name())){
			JSplitPane upJsplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			upJsplitPane.setDividerSize(DIVIDER_SIZE);
			upJsplitPane.setDividerLocation(frame.getLastWidth() / 5);
			upJsplitPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			leftJPanel.setLayout(new BorderLayout());
			upJsplitPane.setLeftComponent(leftJPanel);
			upJsplitPane.setRightComponent(rightJSplitPane);

			rightJSplitPane.setDividerLocation(frame.getLastHeight());
			rightJSplitPane.setOneTouchExpandable(true);

			frame.getContentPane().add(upJsplitPane, BorderLayout.CENTER);
			setTree();
			setMenuBar();
			setStatus();
			setToolBar();
			setContent();
			setOutPut();
			frame.setVisible(true);
			return replyE;
		}else if(event.getType().equals(EventTypeEnum.GET_OBJ.name())) {  //获取BaseFrame
			replyE.setObj(frame);
			return replyE;
		}else if(event.getType().equals(EventTypeEnum.CMD.name())){
			if(event.getValue(EventTypeEnum.CMD.name()).equals("getLeftPane")){
				replyE.setObj(leftJPanel);
				return replyE;
			}
		}
	
		return replyE;
	}
	
	private void setMenuBar(){
		String toID=Hmenu.class.getPackage().getName();
		HHEvent event=new HHEvent(getId(),toID,EventTypeEnum.GET_OBJ.name());
		jMenuBar=(JMenuBar)sendEvent(event).getObj();
		frame.setJMenuBar(jMenuBar);
	}
	
	private void setTree(){
		String toID=HTree.class.getPackage().getName();
		HHEvent event=new HHEvent(getId(),toID,EventTypeEnum.GET_OBJ.name());
		JComponent tree=(JComponent)sendEvent(event).getObj();
		leftJPanel.add(tree);
	}
	
	private void setStatus(){
		String toID=StatusBarManager.class.getPackage().getName();
		HHEvent event=new HHEvent(getId(),toID,EventTypeEnum.GET_OBJ.name());
		JComponent statusBar=(JComponent)sendEvent(event).getObj();
		frame.add(statusBar,BorderLayout.SOUTH);
	}
	
	private void setToolBar(){
		String toID=HToolBar.class.getPackage().getName();
		HHEvent event=new HHEvent(getId(),toID,EventTypeEnum.GET_OBJ.name());
		JComponent toolbar=(JComponent)sendEvent(event).getObj();
		frame.add(toolbar,BorderLayout.NORTH);
	}
	
	private void setContent(){
		String toID=TabbedPaneManager.class.getPackage().getName();
		HHEvent event=new HHEvent(getId(),toID,EventTypeEnum.GET_OBJ.name());
		JComponent tabPane=(JComponent)sendEvent(event).getObj();
		rightJSplitPane.setTopComponent(tabPane);
	}
	
	private void setOutPut(){
		String toID=OutputManger.class.getPackage().getName();
		HHEvent event=new HHEvent(getId(),toID,EventTypeEnum.GET_OBJ.name());
		JComponent output=(JComponent)sendEvent(event).getObj();
		rightJSplitPane.setBottomComponent(output);
	}
	
	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

}
