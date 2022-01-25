package com.hh.hhdb_admin.mgr.login.base;

import com.alee.extended.button.WebSplitButton;
import com.alee.extended.language.LanguageChooser;
import com.alee.extended.language.LanguageChooserModel;
import com.alee.laf.menu.WebCheckBoxMenuItem;
import com.alee.laf.menu.WebMenuItem;
import com.alee.managers.language.UILanguageManager;
import com.alee.managers.style.StyleId;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.utils.swing.menu.PopupMenuGenerator;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.ui.other.BottomStatusBar;
import com.hh.frame.swingui.view.ui.other.OtherIconFactory;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.login.comp.CommonComp;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Objects;

/**
 * @author ouyangxu
 * @date 2021-12-13 0013 10:53:25
 */
public class LoginToolBar extends AbsHComp {
	private LanguageChooser languageChooser;
	private BottomStatusBar.SkinComboBox skinComboBox;
	private final ActionListener switchSkinListener;

	private JMenuBar toolBar;
	private JMenuItem saveItem;
	private JCheckBoxMenuItem autoSaveItem;
	private WebSplitButton resetButton;

	private ToolSplitButton toolSplitButton;

	public LoginToolBar(ActionListener switchSkinListener) {
		this(null, switchSkinListener);
	}

	public LoginToolBar(String id, ActionListener switchSkinListener) {
		super(id);
		this.switchSkinListener = switchSkinListener;
		init();
	}

	protected void init() {
		toolBar = new JMenuBar();
		toolBar.putClientProperty(StyleId.STYLE_PROPERTY, StyleId.menubar);
		skinComboBox = new BottomStatusBar.SkinComboBox(switchSkinListener, true);
		skinComboBox.setStyleId(StyleId.comboboxHover);
		UILanguageManager.setLocaleIcon(Locale.SIMPLIFIED_CHINESE, new ImageIcon(Objects.requireNonNull(OtherIconFactory.class.getResource("imgs/zh.png"))));
		languageChooser = new LanguageChooser(StyleId.languagechooserHover, new LanguageChooserModel(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH));
		languageChooser.setSelectedItem(Locale.forLanguageTag(StartUtil.default_language.name()));
		languageChooser.setFontSize(14);

		final PopupMenuGenerator generator = new PopupMenuGenerator();
		generator.getMenu().setFontSize(14);

		saveItem = new WebMenuItem(CommonComp.getLang("save"), CommonComp.getIcon("save"));
		saveItem.setEnabled(false);
		autoSaveItem = new WebCheckBoxMenuItem(CommonComp.getLang("defaultSave"), CommonComp.getIcon("auto_save"));
		autoSaveItem.setSelected(StartUtil.autoSave);
		generator.getMenu().add(saveItem);
		generator.getMenu().add(autoSaveItem);
		resetButton = new WebSplitButton(StyleId.splitbuttonHover, CommonComp.getLang("reset"), CommonComp.getIcon("reset"));
		resetButton.setFontSize(14);
		resetButton.setPopupMenu(generator.getMenu());

		toolSplitButton = new ToolSplitButton();

		TooltipManager.setTooltip(resetButton, CommonComp.getLang("reset"));
		TooltipManager.setTooltip(languageChooser, CommonComp.getLang("language"));
		TooltipManager.setTooltip(skinComboBox, CommonComp.getLang("skin"));

		toolBar.add(languageChooser);
		toolBar.add(skinComboBox);
		toolBar.add(resetButton);
		toolBar.add(toolSplitButton.getComp());

		comp = toolBar;
	}

	public void setShowToolSplitButton(boolean showToolSplitButton) {
		toolSplitButton.setVisible(showToolSplitButton);
	}

	public void setLocal() {
		getSaveItem().setText(CommonComp.getLang("save"));
		getResetButton().setText(CommonComp.getLang("reset"));
		getAutoSaveItem().setText(CommonComp.getLang("defaultSave"));
		toolSplitButton.setLocal();

		TooltipManager.setTooltip(getResetButton(), CommonComp.getLang("reset"));
		TooltipManager.setTooltip(getLanguageChooser(), CommonComp.getLang("language"));
		TooltipManager.setTooltip(getSkinComboBox(), CommonComp.getLang("skin"));
	}

	@Override
	public JMenuBar getComp() {
		return toolBar;
	}

	public LanguageChooser getLanguageChooser() {
		return languageChooser;
	}

	public BottomStatusBar.SkinComboBox getSkinComboBox() {
		return skinComboBox;
	}

	public JMenuItem getSaveItem() {
		return saveItem;
	}

	public JCheckBoxMenuItem getAutoSaveItem() {
		return autoSaveItem;
	}

	public WebSplitButton getResetButton() {
		return resetButton;
	}
}
