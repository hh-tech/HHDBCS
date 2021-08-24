package com.hh.hhdb_admin.common.util.textEditor;

import com.hh.frame.json.JsonArray;
import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rsyntaxtextarea.RSyntaxTextArea;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryMgr;
import com.hh.hhdb_admin.mgr.quick_query.QuickQueryMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询器编辑器
 */
public class QueryEditorTextArea extends HPanel implements DocumentListener, KeyListener {
	public HTextArea hTextArea;
	public final ListPopup popup;

	private FindingReplacing find;		//查找替换面板
	private JMenuItem queryItem;		//对象查询菜单

	private String prefix = "";
	private int pos = 0;
	private static final String COMMIT_ACTION = "commit";

	//提示象集合
	private List<Keyword> kList = new ArrayList<>();
	//快捷键
	private String keyword = "";			//关键词快捷键
	public String table_keyword = "";		//表快捷键
	public String view_keyword = "";		//视图快捷键
	public String fun_keyword = "";			//函数快捷键
	public String synonym_keyword = "";		//同义词快捷键
	//对象图标
	private ImageIcon tabIcon = QuickQueryMgr.getIcon("table");
	private ImageIcon viewIcon = QuickQueryMgr.getIcon("view");
	private ImageIcon keyIcon = QuickQueryMgr.getIcon("keys");
	private ImageIcon funIcon = QuickQueryMgr.getIcon("function");
	private ImageIcon synonymIcon = QuickQueryMgr.getIcon("list");
	
	private boolean automatic = true;		//自动开启提示
	private boolean fromInputMethod = true;	//输入方式：正常输入与输入法输入
	
	private String keyStr = "";  //用户当前按快捷键对应提示词类型的标识

	//编辑器类型：q(查询器编辑器),v(模板编辑器)
	private String type="q";


	/**
	 * @param bool 是否可以编辑
	 */
	public QueryEditorTextArea(Boolean bool){
		getComp().setLayout(new BorderLayout());

		hTextArea = new HTextArea(false, bool){
			@Override
			public void bookmarksAction(){
				bookmarksAc();
			}
		};
		hTextArea.setConstants(ConstantsEnum.SYNTAX_STYLE_SQL);

		getComp().add(hTextArea.getArea(), BorderLayout.CENTER);
		 getComp().setBorder(null);
		getTextArea().setHighlightCurrentLine(false);

		popup = new ListPopup();
		popup.setPopupSize(400, 200); //弹出框大小

		getTextArea().addKeyListener(this);
		getTextArea().setFont(new JButton().getFont());
		getTextArea().getDocument().addDocumentListener(this);

		// 创建快捷键(回车选择提示词)
		InputMap im = getTextArea().getInputMap();
		ActionMap am = getTextArea().getActionMap();
		im.put(KeyStroke.getKeyStroke("ENTER"), COMMIT_ACTION); // 绑定回车键
		am.put(COMMIT_ACTION, new AbstractAction() {			//按回车选取提示词
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent ev) {
				if (popup.isVisible()) {
					if (popup.isSelected()) {
						selectedValue();
					}
				} else {
					getTextArea().replaceSelection("\n");
				}
			}
		});

		popup.list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1 && popup.isSelected()) {
					selectedValue();
				}
			}
		});
		//输入法监控事件
		getTextArea().addInputMethodListener(new InputMethodListener() {
			@Override
			public void inputMethodTextChanged(InputMethodEvent event) {
				if(null != event.getText()) fromInputMethod = false;
				if(event.getCommittedCharacterCount()>0) fromInputMethod = true;
			}
			@Override
			public void caretPositionChanged(InputMethodEvent event) {
			}
		});
		//对象搜索
		if (type.equals("q")){
			getTextArea().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == 3) {	//右键
						if (null == queryItem && StringUtils.isNotBlank(getSelectedText())) {
							queryItem = new JMenuItem("对象搜索");
							queryItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									StartUtil.eng.doPush(CsMgrEnum.OBJ_QUERY, GuiJsonUtil.toJsonCmd(ObjQueryMgr.QUERY_WITH_WORD).add(ObjQueryMgr.KEY_WORD, getSelectedText()));
								}
							});
							getTextArea().getPopupMenu().add(queryItem);
						}
						if (null != queryItem && StringUtils.isBlank(getSelectedText())) {
							getTextArea().getPopupMenu().remove(queryItem);
							queryItem = null;
						}
					}
				}
			});
		}
	}

    /**
     * 设置编辑器类型：q(查询器编辑器),v(模板编辑器)。默认q
     * @param type 类型
     */
    public void setType(String type){
        this.type = type;
    }

	/**
	 * 设置快捷按钮,1:关键词，2：表名称，3：视图名称(例子：Alt+z)
	 * @param keyword 	关键字按键
	 */
	public void setKeyPressed(String... keyword) {
		this.keyword = keyword[0];
		if (type.equals("q")){
			this.table_keyword = keyword[1];
			this.view_keyword = keyword[2];
			this.fun_keyword = keyword[3];
			this.synonym_keyword = keyword[4];
		}
	}

	/**
	 * 设置关键字提示
	 */
	public void setkeyword(JsonArray keyWords) {
		List<Keyword> list = new ArrayList<>();
		keyWords.forEach(a-> {
			Keyword key;
			if (a.asObject().getString("meta").equals("table")) {
				key = new Keyword(a.asObject().getString("caption"),"t",tabIcon);
			}else if (a.asObject().getString("meta").equals("view")) {
				key = new Keyword(a.asObject().getString("caption"),"v",viewIcon);
			}else if (a.asObject().getString("meta").equals("function")) {
				key = new Keyword(a.asObject().getString("caption"),"f",funIcon);
			}else if (a.asObject().getString("meta").equals("synonym")) {
				key = new Keyword(a.asObject().getString("caption"),"s",synonymIcon);
			}else {
				if (type.equals("q")) {
					key = new Keyword(a.asObject().getString("caption"),"k",keyIcon);
				}else {
					key = new Keyword(a.asObject().getString("caption"),a.asObject().getString("value"),"k",keyIcon);
				}
			}
			list.add(key);
		});
		kList = list;
	}

	/**
     * 设置背景颜色
	 * @param bg bg
	 */
	public void setBackground(Color bg){
		getTextArea().setBackground(bg);
	}

	/**
	 * 获取编辑面板选中的内容
	 */
	public String getSelectedText() {
		return getTextArea().getSelectedText();
	}

    /**
     * 获取内容
	 * @return
     */
	public String getText() {
		return getTextArea().getText();
	}

	public void setText(String text) {
		getTextArea().setText(text);
	}

	/**
	 * 根据位置获取行号
	 *
	 * @param position
	 * @return
	 */
	public int getLineByPosition(int position) {
		Element root = getTextArea().getDocument().getDefaultRootElement();
		return root.getElementIndex(position) + 1;
	}

	/**
	 * 获得基础编辑器
	 */
	public RSyntaxTextArea getTextArea() {
		return hTextArea.getArea().getTextArea();
	}

	/**
	 * 获得所有书签所在行号
	 */
	public List<Integer> getbookmaskLines() {
		return hTextArea.getbookmaskLines();
	}

	/**
	 * 是否自动开始提示功能,默认开启
	 * @param bool
	 */
	public void setAutomatic(boolean bool){
		this.automatic = bool;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			//判断是否自动提示
			if (!popup.isVisible() && !automatic) return;
			//查找替换时不弹出
			if (null != find && find.dlog.isVisible()) return;

			Rectangle r = getTextArea().modelToView(getTextArea().getCaretPosition() - prefix.length());
			if (fromInputMethod && null != r) {   //防止初始化启动的时候获取不到出现异常
				if (e.getLength() != 1) return;
				int posinsert = e.getOffset();
				String content = null;
				try {
					content = posinsert>101 ? getTextArea().getText(posinsert-100, 101) : getTextArea().getText(0, posinsert + 1);
				} catch (BadLocationException be) {
					be.printStackTrace();
				}
				int w;
				for (w = content.length()-1; (w >= 0) && (!isCharacter(content.charAt(w))); w--);
				pos = posinsert;
				prefix = content.substring(w + 1);
				textChanged();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		if (fromInputMethod && popup.isVisible()) {
			if (e.getLength() != 1) return;
			int posremove = e.getOffset();
			String content = null;
			try {
				content = posremove>101 ? getTextArea().getText(posremove-100, 100) : getTextArea().getText(0, posremove);
			} catch (BadLocationException be) {
				be.printStackTrace();
			}
			int w;
			for (w = content.length() - 1; (w >= 0)&& (!isCharacter(content.charAt(w))); w--);
			pos = posremove - 1;
			prefix = content.substring(w + 1);
			textChanged();
		}
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
	}

	/**
	 * 键盘按键事件
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		//提示词快捷键
		if (e.isControlDown()){
			pressHandle("Ctrl",e);
		} else if (e.isAltDown()){
			pressHandle("Alt",e);
		} else if (e.isMetaDown()){
			pressHandle("command",e);
		}

		if (popup.isVisible()){
			if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 下
				getTextArea().setCaretColor(getTextArea().getBackground());  //设置光标为背景颜色，用于处理光标下移弹回
				popup.setSelectedIndex( popup.isSelected() ? (popup.getSelectedIndex() + 1) : 0);
			} else if (e.getKeyCode() == KeyEvent.VK_UP) { // 上
				getTextArea().setCaretColor(getTextArea().getBackground());
				popup.setSelectedIndex( popup.isSelected() ? (popup.getSelectedIndex() - 1) : 0);
			} else if (e.getKeyCode() == KeyEvent.VK_SPACE) { // 空格
				popup.setVisible(false);
			}
		}

		if (e.getKeyCode() == KeyEvent.VK_SPACE) { // 空格
			keyStr = "";   //用于自动弹出模式下初始化为显示所有类型
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (popup.isVisible()) {
			if (e.getKeyCode() == KeyEvent.VK_DOWN) { // 下
				getTextArea().setCaretColor(new Color(1, 1, 1, 226));
				getTextArea().setCaretPosition(pos+1);
			} else if (e.getKeyCode() == KeyEvent.VK_UP) { // 上
				getTextArea().setCaretColor(new Color(1, 1, 1, 226));
				getTextArea().setCaretPosition(pos+1);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	private void pressHandle(String type,KeyEvent e){
		String val = KeyEvent.getKeyText(e.getKeyCode());
		if (!keyword.isEmpty() && keyword.substring(0, keyword.indexOf("+")).equals(type)
				&& val.equals(keyword.substring(keyword.length()-1).toUpperCase()) ){
			keyStr = "k";
			showReminder();
		}else if (!table_keyword.isEmpty() && table_keyword.substring(0, table_keyword.indexOf("+")).equals(type)
				&& val.equals(table_keyword.substring(table_keyword.length()-1).toUpperCase()) ) {
			keyStr = "t";
			showReminder();
		}else if (!view_keyword.isEmpty() && view_keyword.substring(0, view_keyword.indexOf("+")).equals(type)
				&& val.equals(view_keyword.substring(view_keyword.length()-1).toUpperCase()) ){
			keyStr = "v";
			showReminder();
		}else if (!fun_keyword.isEmpty() && fun_keyword.substring(0, fun_keyword.indexOf("+")).equals(type)
				&& val.equals(fun_keyword.substring(fun_keyword.length()-1).toUpperCase()) ){
			keyStr = "f";
			showReminder();
		}else if (!synonym_keyword.isEmpty() && synonym_keyword.substring(0, synonym_keyword.indexOf("+")).equals(type)
				&& val.equals(synonym_keyword.substring(synonym_keyword.length()-1).toUpperCase()) ){
			keyStr = "s";
			showReminder();
		}else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F){
			try {
				if (null == find){
					find = new FindingReplacing(this);
					find.show(getSelectedText());
				}else {
					find.show(getSelectedText());
				}
			}catch (Exception e1){
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 按快捷键之后显示提示框之前的逻辑
	 */
	private void showReminder(){
		int posinsert = getTextArea().getCaretPosition();
		String content = null;
		try {
			content = posinsert>101 ? getTextArea().getText(posinsert-100, 100) : getTextArea().getText(0, posinsert);
		} catch (BadLocationException be) {
			be.printStackTrace();
		}
		int w;
		for (w = content.length()-1; (w >= 0) && (!isCharacter(content.charAt(w))); w--);
		prefix = content.substring(w + 1);
		pos = posinsert-1;
		textChanged();
	}

	/**
	 * 根据输入词显示提示框
	 */
	private void textChanged() {
		List<Keyword> array = getKeyWord(prefix);
		if (array.size() == 0) {
			if (popup.isVisible()) popup.setVisible(false);
		} else {
			if (!popup.isVisible()){	//判断是否显示弹出框
				showPopup();
				getTextArea().requestFocus();
			}
			//设置显示数据
			if (isListChange(array)) {
				popup.setList(array);
				popup.setSelectedIndex(0);
			}
		}
	}

	/**
	 * 显示提示内容框
	 */
	private void showPopup() {
		try {
			//获取屏幕大小
			GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
			Rectangle rect=ge.getMaximumWindowBounds();
			//光标相对编辑器坐标
			Rectangle r = getTextArea().modelToView(getTextArea().getCaretPosition());
			//获取光标相对屏幕坐标
			double caretHeight = r.getY() + getTextArea().getLocationOnScreen().getY();
			//根据光标位置计算提示框弹出坐标
			int x = r.x;
			int y = rect.getHeight() <= caretHeight + popup.getHeight() ? r.y - popup.getHeight() : r.y + 20;
			
			popup.show(getTextArea(), x, y);
		} catch (Exception e) {
			try {
				Rectangle r = getTextArea().modelToView(getTextArea().getCaretPosition() - prefix.length());
				popup.show(getTextArea(), r.x, r.y + 20);
			} catch (Exception ee) {
               ee.printStackTrace();
			}
		}
	}

	/**
	 * 判断是否与上次提示词相同
	 * @param array
	 * @return
	 */
	private boolean isListChange(List<Keyword> array) {
		if (array.size() != popup.getItemCount()) return true;

		for (int i = 0; i < array.size(); i++) {
			if ( !array.get(i).getName().equals(popup.getItem(i)) ) return true;
		}
		return false;
	}

	/**
	 * 根据快捷键获得与输入内容匹配的提示内容集合
	 * @param text	输入字符
	 * @return
	 */
	private List<Keyword> getKeyWord(String text) {
		List<Keyword> strlist = kList;
		if(null == strlist) return new ArrayList<>();

		List<Keyword> list = new ArrayList<>();
		for (Keyword sqlkey : strlist) {
			if (automatic){
				if(text.isEmpty()) return new ArrayList<>();
				//自动弹出模式下，初始状态默认查询所有类型数据，当用户按了对应类型快捷键则只显示对应数据，当弹框关闭后则初始化。
				if (StringUtils.isNotBlank(keyStr)){
					if (sqlkey.getType().equals(keyStr) && sqlkey.getName().toLowerCase().startsWith(text.toLowerCase())) list.add(sqlkey);
				}else {
					if (sqlkey.getName().toLowerCase().startsWith(text.toLowerCase())) list.add(sqlkey);
				}
			}else {
				if (type.equals("v")) {
					if(text.isEmpty()) {	//模板编辑器空字符的时候提示显示所有内容
						list.add(sqlkey);
					}else {
						if (sqlkey.getName().toLowerCase().startsWith(text.toLowerCase())) list.add(sqlkey);
					}
				}else {
					if(text.isEmpty()) {	//查询器器空字符的时候提示显示对应快捷键内容
						if ( sqlkey.getType().equals(keyStr) ) list.add(sqlkey);
					} else {
						if ( sqlkey.getType().equals(keyStr) && sqlkey.getName().toLowerCase().startsWith(text.toLowerCase()) ) list.add(sqlkey);
					}
				}
			}
		}
		return list;
	}

	/**
	 * 选取提示词之后的逻辑
	 */
	private void selectedValue() {
		SwingUtilities.invokeLater(new CompletionTask((Keyword) popup.getSelectedValue(), pos));
		popup.setVisible(false);
		keyStr = "";
	}

	/**
	 * 判断是否是特殊字符
	 * @param ca
	 * @return
	 */
	private boolean isCharacter(char ca) {
		String flag = "\n\t\r ";
		for (int i = 0; i < flag.length(); i++) {
			if (flag.charAt(i) == ca) {
				return true;
			}
		}
		return false;
	}

	private class CompletionTask implements Runnable {
		Keyword key;
		int position;

		CompletionTask(Keyword key, int position) {
			this.key = key;
			this.position = position;
		}

		public void run() {
			try {
				String str = type.equals("v") ? key.getValue() : key.getName();
				getTextArea().getDocument().remove(position-prefix.length()+1, prefix.length());
				getTextArea().getDocument().insertString(position-prefix.length()+1, str+" ", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			//getTextArea().setCaretPosition(position-prefix.length()+1 + completion.length());
		}
	}

	/**
	 * 点击书签事件
	 */
	protected void bookmarksAc(){
	}
}
