package com.hh.hhdb_admin.mgr.sequence.common;

import java.util.Map;

import com.hh.frame.create_dbobj.sequenceMr.SeqItem;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.WithLabelInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.hhdb_admin.mgr.sequence.SequenceMgr;
/**
 * 序列基类
 * @author HuBingBing
 * @date 2020年12月22日上午10:19:21
 */
public abstract class AbsSeqComp extends HPanel{
	
	protected TextInput increase;
	protected TextInput startValue;
	protected TextInput lastValue;
	protected TextInput min;
	protected TextInput max;
	protected TextInput cache;
	protected CheckBoxInput checkCycle;
	protected String schema;
	protected String seq;
	protected boolean isEdit;
	
	public AbsSeqComp() {
		increase = new TextInput();
		startValue = new TextInput();
		lastValue = new TextInput();
		min = new TextInput();
		max = new TextInput();
		cache = new TextInput();
		checkCycle = new CheckBoxInput(SequenceMgr.getLang("loop"));
	}
	
	
	/**
	 * 初始化面板
	 * @author HuBingBing
	 * @date 2020年12月16日下午6:48:02
	 * void
	 *
	 */
	public abstract void initPanel();
	
	/**
	 * 操作序列时初始化数据
	 * @author HuBingBing
	 * @date 2020年12月16日下午6:49:13
	 * @param isEdit
	 * void
	 *
	 */
	public abstract void setStartVal(boolean isEdit);
	
	/**
	 * 获取面板数据
	 * @author HuBingBing
	 * @date 2020年12月16日下午6:51:47
	 * @return
	 * Map<SeqItem,String>
	 *
	 */
	public abstract Map<SeqItem,String> getCompData();
	/**
	 * 设计序列设置初始值
	 * @author HuBingBing
	 * @date 2020年12月17日上午10:35:58
	 * void
	 *
	 */
	public abstract void setEditVal();
	
	public abstract String getCreateSql();
	public abstract String getRenameSql();
	public abstract String getDeleteSql();
	
	public abstract Map<String,String> getDesignSql();
	/**
	 * 添加组件
	 * @author HuBingBing
	 * @date 2020年12月17日上午9:21:27
	 * @param label
	 * @param input
	 * @return
	 * WithLabelInput
	 *
	 */
	protected WithLabelInput getLabelInput(String label,AbsInput input) {
		HDivLayout hdiv = new HDivLayout(30,0,GridSplitEnum.C3);
		hdiv.setMaxWidth(400);
		HPanel hPane2 = new HPanel(hdiv);
		WithLabelInput winput =new WithLabelInput(hPane2,label,input);
		return winput;
	}
	
	
	

}
