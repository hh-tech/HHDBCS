package com.hhdb.csadmin.plugin.user_create.ui;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * 按钮
 * 
 * @author ZL
 * 
 */
public class BaseButton extends JButton {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 文字图片左右布局
	 */
	public static final int HORIZONTAL = 1;
	/**
	 * 文字图片上下布局
	 */
	public static final int VERTICAL = 2;
	
	private String toolTip;

	public BaseButton() {
		super();
	}

	/**
	 * 创建一个按钮，其属性从所提供的 Action 中获取
	 */
	public BaseButton(Action a) {
		super(a);
	}

	/**
	 * 创建一个带图标的按钮
	 */
	public BaseButton(Icon icon) {
		super(icon);
	}

	/**
	 * 创建一个带文本的按钮
	 */
	public BaseButton(String text) {
		super(text);
	}

	/**
	 * 创建一个带初始文本和图标的按钮
	 */
	public BaseButton(String text, Icon icon) {
		super(text, icon);
	}

	public BaseButton(Action a,String toolTipText){
		super(a);
		toolTip=toolTipText;
		init();
	}
	/**
	 * 默认为1 图片在左文字在右，2 图片在上文字在下
	 * 
	 * @param type
	 */
	public void setLayout(int type) {
		if (type == VERTICAL) {
			setFocusPainted(false);
			setHorizontalTextPosition(JButton.CENTER);
			setVerticalTextPosition(JButton.BOTTOM);
		}
	}
    private void init() {
        
        setToolTipText(toolTip);
//        setBorderPainted(false);
//        setContentAreaFilled(false);
//        addMouseListener(this);
    }
}