package com.hh.hhdb_admin.common.util.textEditor;


import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * 提示词详情提示弹出面板
 */
public class TipsPopup extends JPopupMenu {
	private static final long serialVersionUID = -8196069127190657206L;
	private JTextArea jTextArea = new JTextArea(8, 20);
    
    public TipsPopup() {
        setLayout(new BorderLayout());
        JScrollPane jsp = new JScrollPane(jTextArea);
        jsp.setBorder(null);
        jTextArea.setEditable(true);
		jTextArea.setLineWrap(true);        		//激活自动换行功能
		jTextArea.setWrapStyleWord(true);            // 激活断行不断字功能
  
		add(jsp, BorderLayout.CENTER);
    }
    
    public void show(JPopupMenu jmp, Keyword key) {
		if (StringUtils.isNotBlank(key.getValue())) {
            Point p = jmp.getLocationOnScreen();
            //获取屏幕大小
            GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
            Rectangle rect=ge.getMaximumWindowBounds();
            //根据光标位置计算提示框弹出坐标
            int x = rect.getWidth() <= p.x + jmp.getWidth()+this.getWidth() ? p.x - this.getWidth() : p.x + jmp.getWidth();
            int y = p.y;
            
            jTextArea.setText(key.getValue());
            show(null, x, y);
		}
    }
    
    public void hid() {
        if (isVisible()) {
            jTextArea.removeAll();
            setVisible(false);
        }
    }
}
