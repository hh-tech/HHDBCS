package com.hh.hhdb_admin.mgr.login.base;

import com.alee.extended.layout.FormLayout;
import com.alee.extended.window.PopupAdapter;
import com.alee.extended.window.WebPopup;
import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.text.WebEditorPane;
import com.alee.laf.text.WebTextField;
import com.alee.managers.style.Skin;
import com.alee.managers.style.StyleAdapter;
import com.alee.managers.style.StyleId;
import com.alee.managers.style.StyleManager;
import com.alee.managers.tooltip.TooltipManager;
import com.hh.frame.swingui.event.HHEventUtil;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.ui.other.OtherIconFactory;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author ouyangxu
 * @date 2021-10-26 0026 15:47:53
 */
public class ExpandTextInput extends TextInput {
	protected Component[] components;
	protected WebTextField webTextField;
	protected WebPopup<?> expandPopup;

	protected WebPanel trailingPanel;
	protected WebPanel leadingPanel;
	protected String tips;

	public ExpandTextInput() {
		this((String) null);
	}

	public ExpandTextInput(Component... components) {
		this.components = components;
		init(null);
	}

	public ExpandTextInput(String id, Component... components) {
		setId(id);
		this.components = components;
		init(null);
	}

	public ExpandTextInput(String id, String value, Component... components) {
		setId(id);
		this.components = components;
		init(value);
	}

	@Override
	public void init(String value) {
		expandPopup = new WebPopup<>();
		webTextField = new WebTextField();
		initTrailingComponent();
		initLeadingComponent();
		if (trailingPanel != null) {
			webTextField.setTrailingComponent(trailingPanel);
		}
		if (leadingPanel != null) {
			webTextField.setLeadingComponent(leadingPanel);
		}
		textFieldAddListener();
		setPanelBackground();
		if (null != value) {
			webTextField.setText(value);
		}
		StyleManager.addStyleListener(webTextField, new StyleAdapter() {
			@Override
			public void skinChanged(JComponent component, Skin oldSkin, Skin newSkin) {
				super.skinChanged(component, oldSkin, newSkin);
				setPanelBackground();
			}
		});
		comp = webTextField;
	}

	protected void textFieldAddListener() {
		webTextField.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				super.componentResized(e);
				expandPopup.hidePopup();
			}
		});
		webTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				doChange();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				doChange();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				doChange();
			}
		});
	}


	/**
	 * 文本框末尾的组件
	 */
	protected void initTrailingComponent() {
		trailingPanel = new WebPanel(new FormLayout());
		trailingPanel.add(initExpandBtn());
		trailingPanel.setPadding(0);
		if (components != null) {
			trailingPanel.add(components);
		}
	}

	/**
	 * 文本框前面的组件
	 */
	protected void initLeadingComponent() {
	}

	/**
	 * 初始化扩展按钮面板
	 *
	 * @return JPanel
	 */
	protected JButton initExpandBtn() {
		WebButton expandButton = new WebButton(StyleId.buttonIconHover, OtherIconFactory.getInstance().getExpandIcon());
		expandButton.setCursor(Cursor.getDefaultCursor());
		expandButton.setToolTip("展开");
		expandButton.addActionListener(this::expandBtnClick);
		return expandButton;
	}

	protected void setPanelBackground() {
		if (trailingPanel != null) {
			trailingPanel.setBackground(webTextField.getBackground());
		}
		if (leadingPanel != null) {
			leadingPanel.setBackground(webTextField.getBackground());
		}
	}

	/**
	 * 展开按钮点击事件
	 *
	 * @param e
	 */
	protected void expandBtnClick(ActionEvent e) {
		expandPopup.setPadding(2);
		//final WebPanel container = new WebPanel(new BorderLayout(0, 0));
		WebEditorPane textPane = new WebEditorPane(StyleId.editorpane);

		textPane.setMinimumWidth(webTextField.getWidth());
		HHEventUtil.addUndoableEvent(textPane);

		WebScrollPane webScrollPane = new WebScrollPane(StyleId.scrollpaneTransparentHovering, textPane);
		webScrollPane.setPreferredSize(textPane.getMinimumWidth(), 180);

		//container.add(webScrollPane, BorderLayout.CENTER);
		expandPopup.addPopupListener(new PopupAdapter() {
			@Override
			public void popupClosed() {
				webTextField.setText(textPane.getText());
				expandPopup.addToolTip(textPane.getText());
				super.popupClosed();
			}

			@Override
			public void popupOpened() {
				textPane.setText(webTextField.getText());
			}
		});

		expandPopup.add(webScrollPane);
		expandPopup.setResizable(true);
		expandPopup.setDraggable(true);
		//popup.setFollowInvoker(true);

		expandPopup.pack();
		expandPopup.showPopup(webTextField, 0, 0);
	}

	public void setTips(String tips) {
		if (tips != null) {
			webTextField.setInputPrompt(tips);
			TooltipManager.setTooltip(webTextField, tips);
		}
	}

	@Override
	public String getValue() {
		return webTextField.getText();
	}

	@Override
	public void setValue(String value) {
		webTextField.setText(value);

	}

	@Override
	public JTextField getComp() {
		return webTextField;
	}

	public WebPanel getTrailingPanel() {
		return trailingPanel;
	}

	public WebPanel getLeadingPanel() {
		return leadingPanel;
	}
}
