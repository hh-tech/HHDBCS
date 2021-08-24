package com.hh.hhdb_admin.common.util.textEditor;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ListPopup
 * @author: hyz
 * @Description: 自定提示弹出框
 */
public class ListPopup extends JPopupMenu implements MouseInputListener {
	private static final long serialVersionUID = 2546089629243743006L;
	
	public JList<String> list;
	private final ArrayList<ListSelectionListener> listeners = new ArrayList<>();
	
	//详情弹出框
	private TipsPopup tips;
	
	public ListPopup() {
		setLayout(new BorderLayout());
		list = new JList<>();
		list.setFont(new Font("SimSan", Font.PLAIN, 14));
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addMouseListener(this);
		list.addMouseMotionListener(this);
		list.setModel(new DefaultListModel<>());
		list.setCellRenderer(new IconListCellRenderer());  //设置显示效果
		JScrollPane pane = new JScrollPane(list);
		pane.setBorder(null);
		add(pane, BorderLayout.CENTER);
		
		tips = new TipsPopup();
	}
	
	public void setSelectedIndex(int index) {
		if (index >= list.getModel().getSize()) index = 0;
		if (index < 0) index = list.getModel().getSize() - 1;
		list.ensureIndexIsVisible(index);
		list.setSelectedIndex(index);
		
		tips.show(this,((Keyword) getSelectedValue()));
	}
	
	public Object getSelectedValue() {
		return list.getSelectedValue();
	}
	
	public int getSelectedIndex() {
		return list.getSelectedIndex();
	}
	
	public boolean isSelected() {
		return list.getSelectedIndex() != -1;
	}
	
	private void fireValueChanged(ListSelectionEvent e) {
		for (ListSelectionListener l : listeners) {
			l.valueChanged(e);
		}
	}
	
	public int getItemCount() {
		DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
		return model.getSize();
	}
	public Object getItem(int index) {
		DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
		return model.get(index);
	}
	
	/**
	 * 添加数据源
	 * @param li
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setList(List<Keyword> li) {
		DefaultListModel model = new DefaultListModel();
		for (Keyword key : li) {
			model.addElement(key);
		}
		list.setModel(model);
		list.repaint();
	}
	
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
		if (list.getSelectedIndex() != -1)
			fireValueChanged(new ListSelectionEvent(list, list.getSelectedIndex(), list.getSelectedIndex(), true));
	}
	public void mouseReleased(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mouseDragged(MouseEvent e) {
	}
	public void mouseMoved(MouseEvent anEvent) {
		if (anEvent.getSource() == list) {
			Point location = anEvent.getPoint();
			Rectangle r = new Rectangle();
			list.computeVisibleRect(r);
			if (r.contains(location)) {
				updateListBoxSelectionForEvent(anEvent, false);
			}
		}
	}
	protected void updateListBoxSelectionForEvent(MouseEvent anEvent, boolean shouldScroll) {
		Point location = anEvent.getPoint();
		if (list == null) {
			return;
		}
		int index = list.locationToIndex(location);
		if (index == -1) {
			if (location.y < 0) {
				index = 0;
			} else {
				index = list.getModel().getSize() - 1;
			}
		}
		if (list.getSelectedIndex() != index) {
			list.setSelectedIndex(index);
			if (shouldScroll) {
				list.ensureIndexIsVisible(index);
			}
		}
		tips.show(this,((Keyword) getSelectedValue()));
	}
	
	public void setVisible(boolean b) {
		super.setVisible(b);
		if (!b) tips.hid();
	}
}
