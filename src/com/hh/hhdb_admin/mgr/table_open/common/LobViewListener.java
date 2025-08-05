package com.hh.hhdb_admin.mgr.table_open.common;

import com.hh.hhdb_admin.common.lob_panel.LobEditor;

import java.awt.event.ActionListener;

/**
 * @author ouyangxu
 * @date 2020-12-30 0030 15:44:24
 */
public abstract class LobViewListener implements ActionListener {
	public LobEditor viewer;

	public LobEditor getViewer() {
		return viewer;
	}

	public void setViewer(LobEditor viewer) {
		this.viewer = viewer;
	}
}
