package com.hh.hhdb_admin.mgr.login.base;

import com.alee.laf.combobox.ComboBoxCellParameters;
import com.alee.laf.combobox.WebComboBox;
import com.alee.laf.combobox.WebComboBoxRenderer;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ouyangxu
 * @date 2021-11-03 0003 10:52:03
 */
public class DatabaseTypeComboBox extends SelectBox {
	public static Collection<String> dbTypeList;
	private final TypeComboBox jComboBox;
	protected Map<String, String> optionMap;

	public DatabaseTypeComboBox(ActionListener switchTypeListener, Collection<String> dbTypeList) {
		this(null, switchTypeListener, dbTypeList);
	}

	public DatabaseTypeComboBox(String id, ActionListener switchTypeListener, Collection<String> dbTypeList) {
		super(id);
		optionMap = new LinkedHashMap<>();
		comp = jComboBox = new TypeComboBox(switchTypeListener, dbTypeList);
		jComboBox.addItemListener(this::onItemChange);
	}

	@Override
	public String getValue() {
		Object o = jComboBox.getSelectedItem();
		if (o == null) {
			return null;
		}
		if (optionMap == null || optionMap.size() == 0) {
			return o.toString();
		}
		return optionMap.get(o.toString());
	}

	@Override
	public void setValue(String value) {
		if (optionMap != null) {
			Set<String> itemValue = getKeysByStream(optionMap, value);
			if (itemValue != null) {
				jComboBox.setSelectedItem(itemValue.iterator().next());
			}
		} else {
			jComboBox.setSelectedItem(value);
		}

	}

	private <K, V> Set<K> getKeysByStream(Map<K, V> map, V value) {
		return map.entrySet()
				.stream()
				.filter(kvEntry -> Objects.equals(kvEntry.getValue(), value))
				.map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	@Override
	public JComboBox<String> getComp() {
		return jComboBox;
	}

	class TypeComboBox extends WebComboBox {
		private static final long serialVersionUID = -7923526642926442148L;

		public TypeComboBox(ActionListener switchTypeListener, Collection<?> items) {
			super(items);
			init(switchTypeListener);
		}

		private void init(ActionListener switchTypeListener) {
			setFontSize(14);
			addActionListener(switchTypeListener);
			setRenderer(new WebComboBoxRenderer<String, JList<?>, ComboBoxCellParameters<String, JList<?>>>() {
				private static final long serialVersionUID = 4893526723009197111L;

				@Override
				protected Icon iconForValue(final ComboBoxCellParameters<String, JList<?>> parameters) {
					String key;
					Map<String, String> optionMap = getOptionMap();
					if (optionMap == null || optionMap.size() == 0) {
						key = parameters.value();
					} else {
						key = optionMap.get(parameters.value());
					}
					return IconFileUtil.getIcon(new IconBean(CsMgrEnum.TREE.name(), key == null ? "" : key.toLowerCase(), IconSizeEnum.SIZE_16));
				}
			});
		}
	}

	public Map<String, String> getOptionMap() {
		return optionMap;
	}

	public void setOptionMap(Map<String, String> optionMap) {
		this.optionMap = optionMap;
	}
}
