package com.hh.hhdb_admin.common.icon;

import org.apache.commons.lang3.StringUtils;

public class IconBean {
	private IconSizeEnum size = IconSizeEnum.SIZE_16;
	private boolean disabled = false;
	private String name;
	private String context;

	public IconBean(String context, String name) {
		setName(name);
		setContext(context);
	}
	
	public IconBean(String context, String name,IconSizeEnum size) {
		this(context,name);
		this.size=size;
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
		name=name.toLowerCase();
		if(name.endsWith(".png")) {
			this.name=name.substring(0, name.length()-4);
		}else {
			this.name = name;
		}
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context.toLowerCase();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(!StringUtils.isBlank(context)) {
			sb.append(context).append("/");
		}
		sb.append(name);
		if (size != IconSizeEnum.OTHER) {
			String s = StringUtils.substringAfter(size.name(), "_");
			sb.append('_').append(s);
		}
		if (disabled) {
			sb.append('_').append("disabled");
		}
		sb.append(".png");
		return sb.toString();
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof IconBean) {
			IconBean iconBean=(IconBean)o;
			if(!iconBean.getName().equals(name))return false;
			if(!iconBean.getSize().equals(size))return false;
			if(iconBean.isDisabled()!=disabled)return false;
            return iconBean.getName().equals(context);
        }
		return false;
		
	}

}
