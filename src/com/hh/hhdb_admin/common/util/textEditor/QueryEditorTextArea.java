package com.hh.hhdb_admin.common.util.textEditor;

import com.hh.frame.swingui.engine.GuiJsonUtil;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.textEditor.base.ConstantsEnum;
import com.hh.frame.swingui.view.textEditor.rSyntaxTextArea.ui.rsyntaxtextarea.RSyntaxTextArea;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.obj_query.ObjQueryMgr;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * 查询器编辑器
 */
public class QueryEditorTextArea extends HPanel {
	public HTextArea hTextArea;
	public String type="q";				//编辑器类型：q(查询器编辑器),v(模板编辑器)


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
        getTextArea().setFont(new JButton().getFont());
        
		//对象搜索
		if (type.equals("q")){
			JMenuItem queryItem = new JMenuItem("对象搜索");
            queryItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    StartUtil.eng.doPush(CsMgrEnum.OBJ_QUERY, GuiJsonUtil.toJsonCmd(ObjQueryMgr.QUERY_WITH_WORD).add(ObjQueryMgr.KEY_WORD, getSelectedText()));
                }
            });
            getTextArea().getPopupMenu().add(queryItem);
			getTextArea().addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					if (e.getButton() == 3) {	//右键
                        queryItem.setVisible(StringUtils.isNotBlank(getSelectedText()));
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
     * 设置背景颜色
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
     */
	public String getText() {
		return getTextArea().getText();
	}

	public void setText(String text) {
		getTextArea().setText(text);
	}

	/**
	 * 根据位置获取行号
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
	 * 点击书签事件
	 */
	protected void bookmarksAc(){
	}
}
