package com.hh.hhdb_admin.mgr.multi_view.comp;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.laf.base.AbstractLookAndFeel;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.ctrl.FontIcon;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.fa.FaIconUtil;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.input.LabelInput;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BreadCrumb<T> extends AbsHComp {

    private HImgButton backBtn;
    private Color enterColor;
    private FontIcon sepIcon;
    protected JPanel panel;
    protected List<String> navi = new ArrayList<>();
    protected List<T> info = new ArrayList<>();

    public BreadCrumb(String base, T initInfo) {
        navi.add(base);
        info.add(initInfo);
        try {
            enterColor = AbstractLookAndFeel.getTheme().getTopImgSelectedBackgroundColor();
            sepIcon = FaIconUtil.getIcon(FaSolidEnum.ANGLE_RIGHT, AbstractLookAndFeel.getTheme().getIconColor(), 16);
        } catch (Exception e) {
            enterColor = Color.LIGHT_GRAY;
            sepIcon = null;
        }
        initPanel();
    }

    private void initPanel() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JScrollBar scrollBar = scrollPane.getHorizontalScrollBar();
        scrollBar.setPreferredSize(new Dimension(0, 5));
        scrollBar.setUnitIncrement(10);
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return getEmptyButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return getEmptyButton();
            }
        });

        backBtn = new HImgButton() {
            @Override
            protected void onClick() {
                back();
            }
        };
        backBtn.setEnabled(false);
        backBtn.setImg(FaSolidEnum.ARROW_LEFT, 24);
        backBtn.getComp().setToolTipText(LangMgr.lang(Word2.BACK));

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.X_AXIS));
        rootPanel.add(backBtn.getComp());
        rootPanel.add(scrollPane);

        comp = rootPanel;
        refresh();
    }

    public void forward(String text, T info) {
        ArrayList<String> old = new ArrayList<>(navi);
        navi.add(text);
        this.info.add(info);
        refresh();
        onChange(old);
    }

    public void back() {
        if (navi.size() <= 1)
            return;
        ArrayList<String> old = new ArrayList<>(navi);
        navi.remove(navi.size() - 1);
        refresh();
        onChange(old);
    }

    private void setNavi(List<String> navi) {
        ArrayList<String> old = new ArrayList<>(this.navi);
        this.navi = navi;
        refresh();
        onChange(old);
    }

    public List<String> copyNavi() {
        ArrayList<String> list = new ArrayList<>(navi);
        list.remove(0);
        return list;
    }

    /**
     * 自定义分割图案
     */
    protected LabelInput getSep() {
        LabelInput labelInput = new LabelInput();
        if (sepIcon == null) {
            labelInput.setValue(">");
        } else {
            labelInput.setIcon(sepIcon);
        }
        return labelInput;
    }

    protected void refresh() {
        panel.removeAll();
        for (int i = 0, naviSize = navi.size() - 1; i < naviSize; i++) {
            String text = navi.get(i);
            panel.add(createBreadItem(text, i));
            panel.add(getSep().getComp());
        }
        JLabel lastLabel = new JLabel(navi.get(navi.size() - 1));
        setLabelBorder(lastLabel);
        panel.add(lastLabel);
        panel.updateUI();
    }

    public T getCurrentInfo() {
        return info.get(info.size() - 1);
    }

    private void onChange(List<String> oldNavi) {
        backBtn.setEnabled(navi.size() > 1);
        if (navi.size() < info.size())
            info = info.subList(0, navi.size());
        onChange(oldNavi, navi);
    }

    /**
     * 导航变化回调
     */
    protected void onChange(List<String> oldNavi, List<String> newNavi) {

    }

    private OrderedJLabel createBreadItem(String text, int order) {
        OrderedJLabel label = new OrderedJLabel(text);
        label.setOrder(order);
        setLabelBorder(label);
        label.setBackground(enterColor);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int order = label.getOrder();
                setNavi(navi.subList(0, order + 1));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setOpaque(true);
                label.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setOpaque(false);
                label.repaint();
            }
        });
        return label;
    }

    private static void setLabelBorder(JLabel label) {
        label.setBorder(BorderFactory.createEmptyBorder(6, 3, 6, 3));
    }

    private JButton getEmptyButton() {
        return new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 0);
            }
        };
    }

    static class OrderedJLabel extends JLabel {
        private int order;

        public OrderedJLabel(String text) {
            super(text);
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }
    }
}
