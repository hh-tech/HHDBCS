package com.hh.hhdb_admin.mgr.attribute;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.create_dbobj.attributeMr.AttrMrNode;
import com.hh.frame.create_dbobj.attributeMr.mr.AbsAttributeMr;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HSplitPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.frame.swingui.view.textarea2.base.HTAreaConf;
import com.hh.frame.swingui.view.textarea2.base.TAreaDocType;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.lang3.StringUtils;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class AttributeComp {
	
    
    public void showAttr(AttributeBean bean, JdbcBean jdbcBean, Connection conn) throws Exception {
    	HBarPanel status=new HBarPanel(); 
    	LabelInput statusLabel=new LabelInput();
    	status.setLastHComp(statusLabel);
        statusLabel.setAlign(AlignEnum.LEFT);
        HDialog dialog = new HDialog(CsVarUtil.parentFrame, 800, 600);
        dialog.setWindowTitle(LangMgr.lang(Word2.ATTRIBUTION));
        dialog.setIconImage(IconFileUtil.getLogo());
        
        HTable table = new HTable();
        table.setNullSymbol("");
        table.setRowHeight(25);
        SearchToolBar searchBar=new SearchToolBar(table);
        table.setSearchToolBar(searchBar);
        AttrMrNode attrMrNode = new AttrMrNode(bean.getDatabaseName(), bean.getSchema(),
                bean.getTableName(), bean.getName(), bean.getOid(),bean.getNodeType());
        
        long begin=System.currentTimeMillis();
        
        AbsAttributeMr attributeMr = AbsAttributeMr.genAttrMr(jdbcBean);
        if (attributeMr == null) {
            return;
        }
        Map<String,String> m=attributeMr.getTableHeader(attrMrNode);
        for(String k:m.keySet()) {
        	table.addCols(new DataCol(k.toUpperCase(), m.get(k)));
        }
        LastPanel panelOne = new LastPanel(false);
        panelOne.setHead(searchBar.getComp());
        panelOne.setWithScroll(table.getComp());
        List<Map<String, String>>  tmp=attributeMr.getAttribution(attrMrNode, conn);
        List<Map<String, String>> list=new ArrayList<>();
        for(Map<String, String> map:tmp) {
        	Map<String,String> tMap=new HashMap<>();
        	for(String k:map.keySet()) {
        		tMap.put(k.toUpperCase(), map.get(k));
        	}
        	list.add(tMap);
        }
        table.load(list, 0);
        String createSql = attributeMr.getCreateSql(attrMrNode, conn);
        
        if(StringUtils.isBlank(createSql)) {
        	dialog.setRootPanel(panelOne);
        }else {
        	 HTextArea textArea = new HTextArea();
             textArea.setText(createSql);
             HTAreaConf conf = new HTAreaConf();
             conf.setDocType(TAreaDocType.SYNTAX_STYLE_SQL);
             textArea.setConf(conf);
             HSplitPanel splitPanel = new HSplitPanel(false);
             
             
             LastPanel panelTwo = new LastPanel(false);
             panelTwo.set(textArea.getComp());
             
             splitPanel.setLastComp4One(panelOne);
             splitPanel.setLastComp4Two(panelTwo);
             splitPanel.setDividerLocation(250);
             dialog.setRootPanel(splitPanel);
        }
        long end=System.currentTimeMillis();
        statusLabel.setValue(String.format("%s : %d ms", LangMgr.lang(Word2.LOAD),end-begin));
        dialog.setStatusBar(status);
        dialog.show();
    }
    
    

}
