/*
 * PropertyNode.java
 *
 * Copyright (C) 2002-2013 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.hhdb.csadmin.plugin.menu.util;

import java.util.ArrayList;
import java.util.List;

public class PropertyNode {

	private int nodeID;
	private String label;
	private String name;
	private List<PropertyNode> children;

	public PropertyNode() {
	}

	public PropertyNode(int nodeID, String label,String name) {
		this.nodeID = nodeID;
		this.label = label;
		this.name=name;
	}

	public void addChild(PropertyNode node) {
		if (children == null) {
			children = new ArrayList<PropertyNode>();
		}
		children.add(node);
	}

	public boolean hasChildren() {
		return children != null && children.size() > 0;
	}

	public List<PropertyNode> getChildren() {
		return children;
	}

	public int getNodeId() {
		return nodeID;
	}

	public String getLabel() {
		return label;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return label;
	}

}
