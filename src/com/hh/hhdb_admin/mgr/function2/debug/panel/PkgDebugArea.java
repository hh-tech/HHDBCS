package com.hh.hhdb_admin.mgr.function2.debug.panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.BadLocationException;

import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;

import com.hh.frame.common.util.ErrorUtil;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;

class PkgDebugArea extends HTextArea {
	List<Integer> bookmarks;
	public PkgDebugArea() {
		super(getHTAreaConf());
		getRSyntaxTextArea().setRows(15);
		
	        Gutter gutter = getGutter();
	        if (gutter.getComponent(0) instanceof IconRowHeader) {
	            IconRowHeader iconRowHeader = (IconRowHeader) gutter.getComponent(0);
	            iconRowHeader.addMouseListener(new MouseAdapter() {
	                @Override
	                public synchronized void mouseClicked(MouseEvent e) {
	                    if (iconRowHeader.isBookmarkingEnabled() && iconRowHeader.getBookmarkIcon() != null) {
	                    	onSetBookMark(e);
	                    }  
	                }
	            });
	            
	            
	        }
	}
	
	
	
	protected void onSetBookMark(MouseEvent e) {
		
	}
	
	private static HTAreaConf getHTAreaConf() {
		HTAreaConf config = new HTAreaConf();
		config.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
		config.setViewbookMark(true);
		return config;
	}
	
	
	public static List<Integer> getBookMarks(HTextArea area){
		List<Integer> lines = new ArrayList<Integer>();
        for (GutterIconInfo info : area.getGutter().getBookmarks()) {
            try {
                lines.add(area.getRSyntaxTextArea().getLineOfOffset(info.getMarkedOffset()) + 1);
            } catch (BadLocationException e) {
                Lg.error(PkgDebugArea.class, ErrorUtil.getFull(e));
            }
        }
        return lines;
	}
	
	public void rmBookMarks(List<Integer> lines) throws BadLocationException {
		if(!lines.isEmpty()) {
        	for(Integer i:lines) {
        		GutterIconInfo[] infos = getGutter().getBookmarks();
        		for (GutterIconInfo info : infos) {
        			int offset = getRSyntaxTextArea()
							.getLineStartOffset(i - 1);
        			if (Arrays.asList(offset).contains(info.getMarkedOffset())) {
        				getGutter().removeTrackingIcon(info);
        			}
        		}
        	}
        }
	}
	
	
	public List<Integer> getbookmaskLines() {
		return getBookMarks(this);
    }
}
