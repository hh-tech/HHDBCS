package com.hh.hhdb_admin.common.util.textEditor.tooltip;


import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.json.JsonArray;
import com.hh.hhdb_admin.common.util.textEditor.QueryEditUtil;
import com.hh.hhdb_admin.common.util.textEditor.base.FindingReplacing;
import com.hh.hhdb_admin.common.util.textEditor.base.IconListCellRenderer;
import com.hh.hhdb_admin.common.util.textEditor.base.Keyword;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.List;
import java.util.*;

/**
 * 自定义提示工具
 */
public class Tooltip extends JPopupMenu implements DocumentListener, KeyListener {
	private static final long serialVersionUID = 1L;
    public static final int tipMax = 500;         //最大显示提示词个数
	
    private Connection conn;
	private JdbcBean jdbc;
    private JTextArea textArea;
    private JList<Object> list;
    private FindingReplacing find;		//查找替换面板
    private TipsPopup tips;             //详情弹出框
    
    private String prefix = "";         //输入的字符串
    private int pos = 0;                //光标位置
    private String keyStr = "";  		//用户当前按快捷键对应提示词类型的标识
    private boolean automatic = true;	//自动开启提示
    private boolean disable = false;    //禁用提示功能
    
    private String keyword = "";                //关键词快捷键
    private String table_keyword = "";        //表快捷键
    private String view_keyword = "";        //视图快捷键
    private String fun_keyword = "";        //函数快捷键
    private String synonym_keyword = "";    //同义词快捷键
    private String package_keyword = "";     //包名称
    private Map<String,List<Keyword>> schemaSubMap = new LinkedHashMap<>();   //保存模式下对象集合, key:模式名
    private List<Keyword> kList = new ArrayList<>();        //提示对象集合
    private List<Keyword> subList = new LinkedList<>();     //子对象提示集合
    
    private String type;                    //编辑器类型：q(查询器编辑器),v(模板编辑器)
    private boolean inputMode = true;        //输入方式：正常输入与输入法输入
    
    
    /**
     * 自定义提示弹出框
     * @param textArea  编辑器
     * @param type      设置编辑器类型：q(查询器编辑器),v(模板编辑器)。默认q
     * @param automatic 是否自动提示
     */
    public Tooltip(JTextArea textArea, String type,boolean automatic) {
        this.textArea = textArea;
        this.type = type;
        this.automatic = automatic;
        setLayout(new BorderLayout());
        setPopupSize(400, 200); //弹出框大小
        textArea.addKeyListener(this);
        textArea.getDocument().addDocumentListener(this);
        
        list = new JList<>();
        list.setFont(new Font("SimSan", Font.PLAIN, 14));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setModel(new DefaultListModel<>());
        list.setCellRenderer(new IconListCellRenderer());  //设置显示效果
        JScrollPane pane = new JScrollPane(list);
        pane.setBorder(null);
        add(pane, BorderLayout.CENTER);
        
        tips = new TipsPopup();
        
        //弹出框显示关闭事件
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                keyStr = "";
                subList = new LinkedList<>();
            }
            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
        
        //鼠标点击选择提示词
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && !list.isSelectionEmpty()) toTextArea();
            }
        });
        list.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent anEvent) {
                if (anEvent.getSource() == list) {
                    Rectangle r = new Rectangle();
                    list.computeVisibleRect(r);
                    if (r.contains(anEvent.getPoint())) {
                        if (list == null) return;
                        Point location = anEvent.getPoint();
                        int index = list.locationToIndex(location);
                        if (index == -1) index = location.y < 0 ? 0 : list.getModel().getSize() - 1;
                        if (list.getSelectedIndex() != index) list.setSelectedIndex(index);
                        tips.show(getLocationOnScreen(),getWidth(), ((Keyword) list.getSelectedValue()));
                    }
                }
            }
        });
        
        //编辑器绑定回车选择提示词
        textArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "commit"); // 绑定回车键
        textArea.getActionMap().put("commit", new AbstractAction() {            //按回车选取提示词

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ev) {
                if (isVisible()) {
                    if (!list.isSelectionEmpty()) toTextArea();
                } else {
                    textArea.replaceSelection("\n");
                }
            }
        });
        
        //输入法监控事件
        textArea.addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                if (null != event.getText()) inputMode = false;
                if (event.getCommittedCharacterCount() > 0 || !textArea.getInputContext().isCompositionEnabled()) inputMode = true;
            }
            @Override
            public void caretPositionChanged(InputMethodEvent event) {}
        });
    }
    
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) tips.hid();
    }
    
    public void close() {
        ConnUtil.close(conn);
    }
    
    /**
     * 设置快捷按钮,1:关键词，2：表名称，3：视图名称(例子：Alt+z)
     * @param keyword 关键字按键
     */
    public void setKeyPressed(String... keyword) {
        this.keyword = keyword[0];
        if (type.equals("q")) {
            this.table_keyword = keyword[1];
            this.view_keyword = keyword[2];
            this.fun_keyword = keyword[3];
            this.synonym_keyword = keyword[4];
            this.package_keyword = keyword[5];
        }
    }
    
    /**
     * 设置提示内容
     */
    public void setkeyword(JsonArray keyWords) {
        List<Keyword> list = new ArrayList<>();
        keyWords.forEach(a-> {
            if (a.asObject().getString("meta").equals("table")) {
                list.add(new Keyword(a.asObject().getString("caption"),"t", QueryEditUtil.tableIcon));
            }else if (a.asObject().getString("meta").equals("view") || a.asObject().getString("meta").equals("mview")) {
                list.add(new Keyword(a.asObject().getString("caption"),"v",QueryEditUtil.viewIcon));
            }else if (a.asObject().getString("meta").equals("function") || a.asObject().getString("meta").equals("procedure")) {
                list.add(new Keyword(a.asObject().getString("caption"),"f",QueryEditUtil.functionIcon));
            }else if (a.asObject().getString("meta").equals("synonym")) {
                list.add(new Keyword(a.asObject().getString("caption"),"s",QueryEditUtil.synonymIcon));
            }else if (a.asObject().getString("meta").equals("pack")) {
                list.add(new Keyword(a.asObject().getString("caption"),"p",QueryEditUtil.packIcon));
            }else {
                if (type.equals("q")) {
                    list.add(new Keyword(a.asObject().getString("caption"),"k",QueryEditUtil.keyIcon));
                }else {
                    list.add(new Keyword(a.asObject().getString("caption"),a.asObject().getString("value"),"k",QueryEditUtil.keyIcon));
                }
            }
        });
        kList = list;
    }
    
    //键盘按键事件
    @Override
    public void keyPressed(KeyEvent e) {
        //提示词快捷键
        if (e.isControlDown()) {
            pressHandle("Ctrl", e);
        } else if (e.isAltDown()) {
            pressHandle("Alt", e);
        } else if (e.isMetaDown()) {
            pressHandle("command", e);
        }
        
        if (isVisible()) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 下
                textArea.setCaretColor(textArea.getBackground());  //设置光标为背景颜色，用于处理光标下移弹回
                setSelectedIndex(list.isSelectionEmpty() ? 0 : (list.getSelectedIndex() + 1));
            } else if (e.getKeyCode() == KeyEvent.VK_UP) { // 上
                textArea.setCaretColor(textArea.getBackground());
                setSelectedIndex(list.isSelectionEmpty() ? 0 : (list.getSelectedIndex() - 1));
            } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                setVisible(false);
            } else if (e.getKeyCode() == KeyEvent.VK_SPACE) { // 空格
                setVisible(false);
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (isVisible()) {
            if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 下
                textArea.setCaretColor(new Color(1, 1, 1, 226));
                textArea.setCaretPosition(pos + 1);
            } else if (e.getKeyCode() == KeyEvent.VK_UP) { // 上
                textArea.setCaretColor(new Color(1, 1, 1, 226));
                textArea.setCaretPosition(pos + 1);
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        try {
            if (e.getLength() != 1) return;
            //查找替换时不弹出
            if (null != find && find.dlog.isVisible()) return;
            //判断是否自动提示
            if (!isVisible() && !automatic) return;
            
            Rectangle r = textArea.modelToView(textArea.getCaretPosition() - prefix.length());
            if (inputMode && null != r && !disable) {   //防止初始化启动的时候获取不到出现异常
                pos = e.getOffset();
                String content = pos > 101 ? textArea.getText(pos - 100, 101) : textArea.getText(0, pos + 1);
                prefix = TipUtil.isCharacter(content);      //获取需要查找的字符
                showTip();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        try {
            if (e.getLength() != 1) return;
            if (inputMode && isVisible() && !disable) {
                int posremove = e.getOffset();
                String content = posremove > 101 ? textArea.getText(posremove - 100, 100) : textArea.getText(0, posremove);
                pos = posremove - 1;
                prefix = TipUtil.isCharacter(content);
                showTip();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
    }
    
	/**
     * 选取提示词之后显示到编辑器
     */
    private void toTextArea() {
        try {
            Keyword key = (Keyword) list.getSelectedValue();
            String str = type.equals("v") ? key.getValue() : key.getName();
            int pos2 = pos;
            String prefix2 = prefix;
            if (subList.isEmpty()) {
                textArea.getDocument().remove(pos2 - prefix2.length() + 1, prefix2.length());
                textArea.getDocument().insertString(pos2 - prefix2.length() + 1, str, null);
            } else {
                if (prefix2.endsWith(".")) { //.后未输入字符直接选择
                    textArea.getDocument().insertString(pos2+1, str, null);
                } else { //.后再输入了字符筛选子对象
                    String st = prefix2.split("\\.")[prefix2.split("\\.").length-1];
                    textArea.getDocument().remove(pos2-st.length()+1, st.length());
                    textArea.getDocument().insertString(pos2-st.length()+1, str, null);
                }
            }
            setVisible(false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 显示提示框
     */
    private void showTip() {
        List<Keyword> array = getKeyWord();
        if (array.size() == 0) {
            setVisible(false);
        } else {
            //显示提示框
            if (!isVisible()) {
                try {
                    //获取屏幕大小
                    Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
                    //光标相对编辑器坐标
                    Rectangle r = textArea.modelToView(textArea.getCaretPosition());
                    //获取光标相对屏幕坐标
                    double caretHeight = r.getY() + textArea.getLocationOnScreen().getY();
                    //根据光标位置计算提示框弹出坐标
                    show(textArea, r.x, rect.getHeight() <= caretHeight + getHeight() ? r.y - getHeight() : r.y + 20 );
                } catch (Exception e) {
                    try {
                        Rectangle r = textArea.modelToView(textArea.getCaretPosition() - prefix.length());
                        show(textArea, r.x, r.y + 20);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
                list.setFocusable(false);   //取消提示框的焦点
                textArea.requestFocus();
            }
            //设置显示数据
            DefaultListModel<Object> model = new DefaultListModel<Object>();
            array.forEach(model::addElement);
            list.setModel(model);
            list.repaint();
            setSelectedIndex(0);
        }
    }
    
    /**
     * 查询与输入内容匹配的提示集合
     * @param text 输入字符
     * @return
     */
    private List<Keyword> getKeyWord() {
        List<Keyword> list = new LinkedList<>();
        if (automatic && prefix.isEmpty()) return new ArrayList<>();
    
        if (prefix.endsWith(".")) {        //输入“.”后获取子元素集合
            if (type.equals("q") && null != jdbc) {
                String[] val = prefix.split("\\.");
                if (val.length > 1) return new ArrayList<>();
                if (schemaSubMap.containsKey(val[0])) {  //判断是否为查询模式下子对象
                    List<Keyword> st = schemaSubMap.get(val[0]);
                    subList = list = st.size() <= Tooltip.tipMax ? st : st.subList(0,Tooltip.tipMax);
                } else {
                    subList = list = TipUtil.getSubList(conn,jdbc.getSchema(),textArea.getText(),pos,prefix,schemaSubMap);
                }
            }
        } else {
            if (subList.isEmpty()) {
                for (Keyword sqlkey : kList) {
                    if (automatic) {
                        //自动弹出模式下，初始状态默认查询所有类型数据，当用户按了对应类型快捷键则只显示对应数据，当弹框关闭后则初始化。
                        if (StringUtils.isNotBlank(keyStr)) {
                            if (sqlkey.getType().equals(keyStr) && sqlkey.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                                list.add(sqlkey);
                        } else {
                            if (sqlkey.getName().toLowerCase().startsWith(prefix.toLowerCase())) list.add(sqlkey);
                        }
                    } else {
                        if (type.equals("v")) {
                            if (prefix.isEmpty()) {    //模板编辑器空字符的时候提示显示所有内容
                                list.add(sqlkey);
                            } else {
                                if (sqlkey.getName().toLowerCase().startsWith(prefix.toLowerCase())) list.add(sqlkey);
                            }
                        } else {
                            if (prefix.isEmpty()) {    //查询器器空字符的时候提示显示对应快捷键内容
                                if (sqlkey.getType().equals(keyStr)) list.add(sqlkey);
                            } else {
                                if (sqlkey.getType().equals(keyStr) && sqlkey.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                                    list.add(sqlkey);
                            }
                        }
                    }
                    if (list.size() == tipMax) break;  //默认只显示500条数据
                }
            } else {	//.后再输入了字符筛选子集合内容
                String[] val = prefix.split("\\.");
                String str = val[val.length-1];
                if(str.isEmpty()) return new ArrayList<>();
    
                String ss = TipUtil.analysisStr(textArea.getText(),pos,val[0]+".");
                //判断是否为查询模式下子对象
                List<Keyword> keyList = schemaSubMap.containsKey(ss) ? schemaSubMap.get(ss) : subList;
                for (Keyword sqlkey : keyList) {
                    if (sqlkey.getName().toLowerCase().startsWith(str.toLowerCase())) list.add(sqlkey);
                    if (list.size()>= Tooltip.tipMax) break;
                }
            }
        }
        return list;
    }
    
    private void pressHandle(String type, KeyEvent e) {
        String val = KeyEvent.getKeyText(e.getKeyCode());
        if (!keyword.isEmpty() && keyword.substring(0, keyword.indexOf("+")).equals(type)
                && val.equals(keyword.substring(keyword.length() - 1).toUpperCase())) {
            keyStr = "k";
            showReminder();
        } else if (!table_keyword.isEmpty() && table_keyword.substring(0, table_keyword.indexOf("+")).equals(type)
                && val.equals(table_keyword.substring(table_keyword.length() - 1).toUpperCase())) {
            keyStr = "t";
            showReminder();
        } else if (!view_keyword.isEmpty() && view_keyword.substring(0, view_keyword.indexOf("+")).equals(type)
                && val.equals(view_keyword.substring(view_keyword.length() - 1).toUpperCase())) {
            keyStr = "v";
            showReminder();
        } else if (!fun_keyword.isEmpty() && fun_keyword.substring(0, fun_keyword.indexOf("+")).equals(type)
                && val.equals(fun_keyword.substring(fun_keyword.length() - 1).toUpperCase())) {
            keyStr = "f";
            showReminder();
        } else if (!synonym_keyword.isEmpty() && synonym_keyword.substring(0, synonym_keyword.indexOf("+")).equals(type)
                && val.equals(synonym_keyword.substring(synonym_keyword.length() - 1).toUpperCase())) {
            keyStr = "s";
            showReminder();
        } else if (!package_keyword.isEmpty() && package_keyword.substring(0, package_keyword.indexOf("+")).equals(type)
                && val.equals(package_keyword.substring(package_keyword.length() - 1).toUpperCase())) {
            keyStr = "p";
            showReminder();
        } else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F) {
            try {
                if (null == find){
                    find = new FindingReplacing(textArea);
                    find.show(textArea.getSelectedText());
                }else {
                    find.show(textArea.getSelectedText());
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }
    
    /**
     * 按快捷键之后显示提示框之前的逻辑
     */
    private void showReminder() {
        if (!inputMode) return;
        int posinsert = textArea.getCaretPosition();
        String content = null;
        try {
            content = posinsert > 101 ? textArea.getText(posinsert - 100, 100) : textArea.getText(0, posinsert);
        } catch (BadLocationException be) {
            be.printStackTrace();
        }
        prefix = TipUtil.isCharacter(content);
        pos = posinsert - 1;
        showTip();
    }
    
    /**
     * 显示提示框中某条提示词
     * @param index
     */
    private void setSelectedIndex(int index) {
        if (index >= list.getModel().getSize()) index = 0;
        if (index < 0) index = list.getModel().getSize() - 1;
        list.ensureIndexIsVisible(index);
        list.setSelectedIndex(index);
        
        tips.show(getLocationOnScreen(),getWidth(), ((Keyword) list.getSelectedValue()));
    }
    
    
    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }
    
    public void setDisable(boolean disable) {
        this.disable = disable;
    }
    
    public void setJdbc(JdbcBean jdbc){
        try {
            this.jdbc = jdbc;
            ConnUtil.close(this.conn);
            this.conn = ConnUtil.getConn(jdbc);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
