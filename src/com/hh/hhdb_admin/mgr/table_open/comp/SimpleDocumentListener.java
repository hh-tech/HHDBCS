package com.hh.hhdb_admin.mgr.table_open.comp;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author ouyangxu
 * @date 2021-12-02 0002 10:55:14
 * @description 文本框内容改变监听
 */
public interface SimpleDocumentListener extends DocumentListener {
	/**
	 * 文本框内容改变监听
	 *
	 * @param e
	 */
	void update(DocumentEvent e);

	@Override
	default void insertUpdate(DocumentEvent e) {
		update(e);
	}

	@Override
	default void removeUpdate(DocumentEvent e) {
		update(e);
	}

	@Override
	default void changedUpdate(DocumentEvent e) {
		update(e);
	}
}
