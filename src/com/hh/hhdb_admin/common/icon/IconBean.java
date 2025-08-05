package com.hh.hhdb_admin.common.icon;

import com.hh.frame.swingui.view.util.ImgUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class IconBean {
	private IconSizeEnum size = IconSizeEnum.SIZE_16;
	private boolean disabled = false;
	private boolean dark = false;
	private String name;
	private String context;
	private ImgUtil.ImgType iconType = ImgUtil.ImgType.SVG;


	public IconBean(String context, String name) {
		setName(name);
		setContext(context);
	}

	public IconBean(String context, String name, ImgUtil.ImgType iconType) {
		setName(name);
		setContext(context);
		this.iconType = iconType;
	}

	public IconBean(String context, String name, IconSizeEnum size) {
		this(context, name);
		this.size = size;
	}

	public IconBean(String context, String name, IconSizeEnum size, ImgUtil.ImgType iconType) {
		this(context, name);
		this.size = size;
		this.iconType = iconType;
	}

	public IconSizeEnum getSize() {
		return size;
	}

	public void setSize(IconSizeEnum size) {
		this.size = size;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		name = name.toLowerCase();
		if (!name.endsWith(iconType.name().toLowerCase()) && name.lastIndexOf(".") > -1) {
			name = name.substring(0, name.lastIndexOf("."));
		}
		this.name = name;
	}

	public boolean isDark() {
		return dark;
	}

	public void setDark(boolean dark) {
		this.dark = dark;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context.toLowerCase();
	}

	public ImgUtil.ImgType getIconType() {
		return iconType;
	}

	public void setIconType(ImgUtil.ImgType iconType) {
		this.iconType = iconType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isBlank(context)) {
			sb.append(context).append("/");
		}
		sb.append(name);
		if (isDark()) {
			sb.append('_').append("dark");
		}
		if (size != IconSizeEnum.OTHER) {
			String s = StringUtils.substringAfter(size.name(), "_");
			sb.append('_').append(s);
		}
		if (disabled) {
			sb.append('_').append("disabled");
		}
		sb.append(".").append(iconType.name().toLowerCase());
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		IconBean iconBean = (IconBean) o;
		return disabled == iconBean.disabled && dark == iconBean.dark && size == iconBean.size && Objects.equals(name, iconBean.name) && Objects.equals(context, iconBean.context);
	}

	@Override
	public int hashCode() {
		return Objects.hash(size, disabled, dark, name, context);
	}
}
