package com.hh.hhdb_admin.mgr.tool.comp;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import com.hh.hhdb_admin.mgr.tool.ToolUtil;
import org.apache.commons.lang3.StringUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * @author ouyangxu
 * @date 2021-12-22 0024 10:44:50
 */
public class CommonButtons {
	private HButton formatBtn,formatBreakWidthBtn,copyBtn,clearBtn;

	public CommonButtons(HFrame frame) {
		formatBtn = new HButton(LangMgr.sLang(CsL.SQL_FORMAT));
		formatBtn.setIcon(FaSolidEnum.WAND_MAGIC_SPARKLES,16);
		
		formatBreakWidthBtn = new HButton(LangMgr.sLang(CsL.FORMATTED_WIDTH));
		formatBreakWidthBtn.setIcon(FaSolidEnum.ARROWS_LEFT_RIGHT_TO_LINE,16);
		formatBreakWidthBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				String width = PopDlgUtil.nameInput(frame,LangMgr.sLang(CsL.FORMATTED_WIDTH),CsVarUtil.CONF.getSqlFormatWith()+"");
				if (StringUtils.isNotBlank(width) && ToolUtil.verifyBreakWidth(width)) {
					CsVarUtil.CONF.setSqlFormatWith(Integer.parseInt(width));
					CsVarUtil.saveConfig();
				}
			}
		});
		
		copyBtn = new HButton(LangMgr.lang(Word2.COPY));
		copyBtn.setIcon(FaSolidEnum.COPY,16);
		
		clearBtn = new HButton(LangMgr.lang(Word2.EMPTY));
		clearBtn.setIcon(FaSolidEnum.TRASH_CAN,16);
	}

	public List<AbsHComp> getButtons() {
		return Arrays.asList(formatBtn, formatBreakWidthBtn, copyBtn, clearBtn);
	}

	public HButton getFormatBtn() {
		return formatBtn;
	}

	public HButton getCopyBtn() {
		return copyBtn;
	}

	public HButton getClearBtn() {
		return clearBtn;
	}
}
