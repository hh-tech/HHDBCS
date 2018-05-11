package com.hhdb.csadmin.plugin.event_test;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.hh.frame.swingui.base.AbstractPlugin;
import com.hh.frame.swingui.event.CmdEvent;
import com.hh.frame.swingui.event.EventTypeEnum;
import com.hh.frame.swingui.event.HHEvent;
import com.hhdb.csadmin.common.util.EventUtil;

public class EventTest extends AbstractPlugin {
	private JPanel jPanel = new JPanel(new GridBagLayout());
	private JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	// private JSplitPane jSplitPane1 = new
	// JSplitPane(JSplitPane.VERTICAL_SPLIT);
	// private JTextField textclassname = new JTextField();
	JScrollPane scrollPaneLeft = new JScrollPane();
	JScrollPane scrollPaneRight = new JScrollPane();
	private JTextPane leftTextPane = new JTextPane();
	private JTextPane rightTextPane = new JTextPane();
	private JButton button = new JButton("发送");

	public EventTest() {

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		c.weightx = 1.0;
		// c.ipady=600;
		jPanel.add(jSplitPane, c);
		// jSplitPane1.setBottomComponent(leftTextPane);
		// jSplitPane1.setLeftComponent(textclassname);
		scrollPaneLeft.setViewportView(leftTextPane);
		scrollPaneRight.setViewportView(rightTextPane);
		jSplitPane.setLeftComponent(scrollPaneLeft);
		jSplitPane.setRightComponent(scrollPaneRight);
		jSplitPane.setDividerLocation(500);
		c.fill = GridBagConstraints.VERTICAL;
		c.weighty = 0;
		c.gridy = 1;
		c.ipady = 10;
		jPanel.add(button, c);

		jPanel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				System.out.println("test mouse");
				String fromID = EventTest.class.getPackage().getName();
				String toID = "com.hhdb.csadmin.plugin.mouse_menu";
				Map<String, String> propMap = new HashMap<String, String>();
				propMap.put("新建文件/1", "新建txt文件/1|新建pdf文件/1|新建其他文件/0");
				propMap.put("复制/1", "");
				propMap.put("点一下/1", "再点一下/1|再点二下/0");
				propMap.put("coordinateX", String.valueOf(e.getX()));
				propMap.put("coordinateY", String.valueOf(e.getY()));
				HHEvent event = new HHEvent(fromID, toID, "mouserMenuEvent");
				event.setObj(e.getComponent());
				event.setPropMap(propMap);
				sendEvent(event);
			}
		});

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String text = leftTextPane.getText();
				try {
					HHEvent re_event;
					re_event = sendEvent(EventUtil.toEvent(text));
					rightTextPane.setText(re_event.toString());
				} catch (ParserConfigurationException | SAXException
						| IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public HHEvent receEvent(HHEvent event) {
		if (event.getType().equals(EventTypeEnum.COMMON.name())) {
			String fromID = EventTest.class.getPackage().getName();
			String toID = "com.hhdb.csadmin.plugin.tabpane";
			CmdEvent addPanelEvent = new CmdEvent(fromID, toID, "AddPanelEvent");
			addPanelEvent.setObj(jPanel);
			addPanelEvent.addProp("ICO", "chart.png");
			addPanelEvent.addProp("TAB_TITLE", "test");
			addPanelEvent.addProp("COMPONENT_ID", "tony");
			sendEvent(addPanelEvent);

		} else if (event.getType().equals("mouserMenuEvent")) {
			System.out.println("用户鼠标右击点击了:" + event.getValue("select"));
		}
		return EventUtil.getReplyEvent(EventTest.class, event);
	}

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return jPanel;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("asdfdafs");
		EventTest eventTest = new EventTest();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1024, 768);
		frame.getContentPane().add((Component) eventTest.getComponent(),
				BorderLayout.CENTER);
		frame.setVisible(true);
	}

}
