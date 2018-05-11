package com.hhdb.csadmin.plugin.query;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.hhdb.csadmin.common.bean.DefaultSet;
import com.hhdb.csadmin.common.bean.DefaultSetting;
import com.hhdb.csadmin.common.ui.textEdit.QueryEditorUi2;

public class SetKeyFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private QueryEditorUi2 queryUi;

	public SetKeyFrame(QueryEditorUi2 queryUi){
		this.queryUi = queryUi;
		ImageIcon icon=new ImageIcon(System.getProperty("user.dir")+"/etc/icon/query.png");
		setIconImage(icon.getImage());
		setAutoRequestFocus(false);
		setResizable(false);
		setTitle("查询器快捷键设置");
		Dimension size = new Dimension(420, 280);
		setSize(size);
		// 设置Frame居中显示
		setLocationRelativeTo(null);
		
		init();
	}
	private void init(){
		DefaultSet textpaneSet = DefaultSetting.loadFontSettings();
		JPanel conterPanel = new JPanel();
		getContentPane().add(conterPanel);
		conterPanel.setBackground(Color.WHITE);
		conterPanel.setLayout(new GridBagLayout());
		JLabel upkeylb = new JLabel("设置前键");
		JLabel downkeylb = new JLabel("设置后键");
		JLabel betnkeylb = new JLabel("组合键");
		JLabel guanjianlb = new JLabel("关键字提示：");
		JLabel tablenamelb = new JLabel("表名提示：");
		JLabel viewnamelb = new JLabel("视图名提示：");
		JButton savebtn = new JButton("保存");
		
		JComboBox<String> guanjiancb = new JComboBox<String>();
		JComboBox<String> tablenamecb = new JComboBox<String>();
		JComboBox<String> viewnamecb = new JComboBox<String>();
		String[] upks = {"Alt","Ctrl"};
		for(String upk:upks){
			guanjiancb.addItem(upk);
			tablenamecb.addItem(upk);
			viewnamecb.addItem(upk);
		}
				
		JTextField guanjiantex = new JTextField();
		JTextField tablenametex = new JTextField();
		JTextField viewnametex = new JTextField();
		guanjiantex.setPreferredSize(new Dimension(60, 25));
		tablenametex.setPreferredSize(new Dimension(60, 25));
		viewnametex.setPreferredSize(new Dimension(60, 25));
		
		//=================================================================================================
		JTextField guanjianbet = new JTextField();
		JTextField tablenamebet = new JTextField();
		JTextField viewnamebet = new JTextField();
		guanjianbet.setPreferredSize(new Dimension(100, 25));
		tablenamebet.setPreferredSize(new Dimension(100, 25));
		viewnamebet.setPreferredSize(new Dimension(100, 25));
		guanjianbet.setEditable(false);
		tablenamebet.setEditable(false);
		viewnamebet.setEditable(false);	
		//==================================================================================================
		
		if(!textpaneSet.getQkeyguanjian().isEmpty()&&!textpaneSet.getQkeyguanjian().equals("无效")){
			String upkey = textpaneSet.getQkeyguanjian().substring(0,textpaneSet.getQkeyguanjian().indexOf("+"));
			char keychar = textpaneSet.getQkeyguanjian().charAt(textpaneSet.getQkeyguanjian().length()-1);
			guanjiancb.setSelectedItem(upkey);
			guanjiantex.setText(keychar+"");
			guanjianbet.setText(textpaneSet.getQkeyguanjian());
		}else{
			guanjianbet.setText("无效");
		}
		//=========================
		if(!textpaneSet.getQkeytablename().isEmpty()&&!textpaneSet.getQkeytablename().equals("无效")){
			String upkey = textpaneSet.getQkeytablename().substring(0,textpaneSet.getQkeytablename().indexOf("+"));
			char keychar = textpaneSet.getQkeytablename().charAt(textpaneSet.getQkeytablename().length()-1);
			tablenamecb.setSelectedItem(upkey);
			tablenametex.setText(keychar+"");
			tablenamebet.setText(textpaneSet.getQkeytablename());
		}else{
			tablenamebet.setText("无效");
		}
		//=========================
		if(!textpaneSet.getQkeyviewname().isEmpty()&&!textpaneSet.getQkeyviewname().equals("无效")){
			String upkey = textpaneSet.getQkeyviewname().substring(0,textpaneSet.getQkeyviewname().indexOf("+"));
			char keychar = textpaneSet.getQkeyviewname().charAt(textpaneSet.getQkeyviewname().length()-1);
			viewnamecb.setSelectedItem(upkey);
			viewnametex.setText(keychar+"");
			viewnamebet.setText(textpaneSet.getQkeyviewname());
		}else{
			viewnamebet.setText("无效");
		}
		
		//==================================================================================================
		guanjiantex.addKeyListener(new MyKeyListener(guanjiancb,guanjiantex,guanjianbet));
		tablenametex.addKeyListener(new MyKeyListener(tablenamecb,tablenametex,tablenamebet));
		viewnametex.addKeyListener(new MyKeyListener(viewnamecb,viewnametex,viewnamebet));
		
		guanjiancb.addItemListener(new MyItemListener(guanjiancb,guanjiantex,guanjianbet));
		tablenamecb.addItemListener(new MyItemListener(tablenamecb,tablenametex,tablenamebet));
		viewnamecb.addItemListener(new MyItemListener(viewnamecb,viewnametex,viewnamebet));
		
		//==================================================================================================
		savebtn.addActionListener(new MyActionListener(guanjianbet, tablenamebet, viewnamebet));		
		//==================================================================================================
		
		conterPanel.add(upkeylb, new GridBagConstraints(1 , 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(downkeylb, new GridBagConstraints(2 , 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(betnkeylb, new GridBagConstraints(3 , 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		
		conterPanel.add(guanjianlb, new GridBagConstraints(0 , 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(guanjiancb, new GridBagConstraints(1 , 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(guanjiantex, new GridBagConstraints(2 , 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(guanjianbet, new GridBagConstraints(3 , 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		
		conterPanel.add(tablenamelb, new GridBagConstraints(0 , 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(tablenamecb, new GridBagConstraints(1 , 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(tablenametex, new GridBagConstraints(2 , 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(tablenamebet, new GridBagConstraints(3 , 2, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		
		conterPanel.add(viewnamelb, new GridBagConstraints(0 , 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(20,20, 0, 0), 0, 0));
		conterPanel.add(viewnamecb, new GridBagConstraints(1 , 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(viewnametex, new GridBagConstraints(2 , 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		conterPanel.add(viewnamebet, new GridBagConstraints(3 , 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));
		
		conterPanel.add(savebtn, new GridBagConstraints(2 , 4, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20, 20, 0, 0), 0, 0));

		JPanel jpl = new JPanel();
		jpl.setBackground(Color.WHITE);
		conterPanel.add(jpl, new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
	}
	class MyActionListener implements ActionListener{
		private JTextField guanjianbet;
		private JTextField tablenamebet;
		private JTextField viewnamebet;
		public MyActionListener(JTextField guanjianbet,JTextField tablenamebet,JTextField viewnamebet){
			this.guanjianbet = guanjianbet;
			this.tablenamebet = tablenamebet;
			this.viewnamebet = viewnamebet;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!checkQkey(guanjianbet.getText())){
				JOptionPane.showMessageDialog(null, "关键字快捷键"+guanjianbet.getText()+"已被占用！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(!checkQkey(tablenamebet.getText())){
				JOptionPane.showMessageDialog(null, "表名快捷键"+tablenamebet.getText()+"已被占用！", "错误", JOptionPane.ERROR_MESSAGE);
				return;			
			}
			if(!checkQkey(viewnamebet.getText())){
				JOptionPane.showMessageDialog(null, "视图名快捷键"+viewnamebet.getText()+"已被占用！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(guanjianbet.getText().equals(tablenamebet.getText())&&!guanjianbet.getText().equals("无效")){
				JOptionPane.showMessageDialog(null, "关键字与表名快捷键不能重复！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(guanjianbet.getText().equals(viewnamebet.getText())&&!guanjianbet.getText().equals("无效")){
				JOptionPane.showMessageDialog(null, "关键字与视图名快捷键不能重复！", "错误", JOptionPane.ERROR_MESSAGE);		
				return;
			}
			if(tablenamebet.getText().equals(viewnamebet.getText())&&!tablenamebet.getText().equals("无效")){
				JOptionPane.showMessageDialog(null, "视图名与表名快捷键不能重复！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			DefaultSet ds = new DefaultSet();
			ds.setQkeyguanjian(guanjianbet.getText());
			ds.setQkeytablename(tablenamebet.getText());
			ds.setQkeyviewname(viewnamebet.getText());
			DefaultSetting.updateQkeySetting(ds);
			queryUi.setTextPanelSet(DefaultSetting.loadFontSettings());
			JOptionPane.showMessageDialog(null, "设置成功！", "消息", JOptionPane.INFORMATION_MESSAGE);
			setVisible(false);
		}		
	}
	
	private boolean checkQkey(String qkey){
		if(qkey.equals("Ctrl+e")){
			return false;
		}
		if(qkey.equals("Ctrl+r")){
			return false;		
		}
		if(qkey.equals("Ctrl+t")){
			return false;
		}
		if(qkey.equals("Ctrl+q")){
			return false;
		}
		return true;
	}
	
	class MyItemListener implements ItemListener{
		private JComboBox<String> upkey;
		private JTextField downtex;
		private JTextField bettex;
		
		public MyItemListener(JComboBox<String> upkey,JTextField downtex,JTextField bettex){
			this.upkey = upkey;
			this.downtex = downtex;
			this.bettex = bettex;
		}
		@Override
		public void itemStateChanged(ItemEvent e) {
			if(!downtex.getText().trim().equals("")){
				bettex.setText(upkey.getSelectedItem().toString()+"+"+downtex.getText().trim());
			}
		}
		
	}
	class MyKeyListener implements KeyListener{
		private JComboBox<String> upkey;
		private JTextField downtex;
		private JTextField bettex;
		
		public MyKeyListener(JComboBox<String> upkey,JTextField downtex,JTextField bettex){
			this.upkey = upkey;
			this.downtex = downtex;
			this.bettex = bettex;
		}
		@Override
			public void keyTyped(KeyEvent e) {
			downtex.setText("");
			}			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode()==16){
					return;
				}
				char keychar = e.getKeyChar();
				if(!(""+keychar+"").trim().equals("")&&keychar<127){
					downtex.setText(""+keychar+"");
					bettex.setText(upkey.getSelectedItem().toString()+"+"+keychar);
				}else{					
					bettex.setText("无效");
					downtex.setText("");
				}
			}			
			@Override
			public void keyPressed(KeyEvent e) {				
			}
	}

}
