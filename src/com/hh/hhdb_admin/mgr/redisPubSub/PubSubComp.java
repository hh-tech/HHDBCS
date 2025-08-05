package com.hh.hhdb_admin.mgr.redisPubSub;

import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.common.util.DateUtil;
import com.hh.frame.common.util.db.ConnUtil;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.lg.Lg;
import com.hh.frame.redis.pb.RedisPubBean;
import com.hh.frame.redis.pb.RedisPubTool;
import com.hh.frame.redis.pb.RedisSubBean;
import com.hh.frame.redis.pb.RedisSubTool;
import com.hh.frame.swingui.view.abs.AbsHComp;
import com.hh.frame.swingui.view.container.HGridPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.fileChooser.HFileChooser;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.SearchToolBar;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.bar.BarCol;
import com.hh.frame.swingui.view.textarea2.HTextArea;
import com.hh.hhdb_admin.common.CsL;
import com.hh.hhdb_admin.common.util.CsVarUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PubSubComp extends AbsHComp {
    private JdbcBean jdbc;
    private Connection subConn, pubConn;
    private RedisPubTool pubTool;
    private RedisSubTool subTool;
    private File pubDir;
    private File subDir;
    
    private LastPanel lastPanel;
    private TextInput channelInput;
    private HTextArea textArea;
    private HTable table;
    
    private boolean isPub;
    
    public PubSubComp(JdbcBean jdbc,boolean isPub) throws Exception {
        this.jdbc = jdbc;
        this.isPub = isPub;
        lastPanel = new LastPanel(false);
        lastPanel.setHead(init().getComp());
        lastPanel.set(getTablePanel().getComp());
        comp = lastPanel.getComp();
    
        if (isPub) {
            pubConn = CsVarUtil.newConn(jdbc);
            pubDir = RedisPubSubMgr.prepareDir(RedisPubSubMgr.PUB_TMP);
        } else {
            subConn = CsVarUtil.newConn(jdbc);
            subDir = RedisPubSubMgr.prepareDir(RedisPubSubMgr.SUB_TMP);
        }
    }
    
    public LastPanel getLastPanel() throws Exception {
        return lastPanel;
    }
    
    public void close() {
        if (isPub) {
            ConnUtil.close(pubConn);
            FileUtils.deleteQuietly(pubDir);
        } else {
            ConnUtil.close(subConn);
            FileUtils.deleteQuietly(subDir);
        }
    }
    
    private LastPanel init() {
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(initSqlFilePanel().getComp());
        if (isPub) {
            HPanel hp = new HPanel();
            hp.setTitle(LangMgr.sLang(CsL.TEXT_INPUT_FIELD));
            hp.add(textArea = new HTextArea());
            textArea.getRSyntaxTextArea().setRows(25);
            lastPanel.set(hp.getComp());
        }
        return lastPanel;
    }
    
    private LastPanel getTablePanel() {
        table = new HTable();
        table.setRowHeight(30);
        table.setEvenBgColor(table.getOddBgColor());
        table.setSingleSelection(true);
        table.addCols(getCol("id","id"), getCol("channelName",LangMgr.lang(Word4.CHANNEL_NAME)),
                getCol("date",LangMgr.lang(Word2.TIME)), getCol("file",LangMgr.lang(Word2.PATH)));
        if (isPub) {
            table.addCols(getCol("subNum",LangMgr.lang(Word4.NUMBER_SUBSCRIPTIONS)), getCol("byteLen",LangMgr.lang(Word2.SIZES)));
        } else {
            HButton openBtn = new HButton(LangMgr.lang(Word2.EXAMINE)){
                @Override
                protected void onClick() {
                    HTabRowBean bean = table.getSelectedRowBeans().get(0);
                    new ViewFileDialog(bean.getCurrRow().get("file"));
                }
            };
            openBtn.setIcon(FaSolidEnum.EYE,16);
            BarCol toolBarCol = new BarCol("Operation", LangMgr.lang(Word2.EXAMINE), openBtn);
            toolBarCol.setSortPopMenu(null);
            toolBarCol.setWidth(10);
            table.addCols(getCol("byteLen",LangMgr.lang(Word2.SIZES)),toolBarCol);
        }
        LastPanel lasPanel = new LastPanel(false);
        lasPanel.setHead(new SearchToolBar(table).getComp());
        lasPanel.setWithScroll(table.getComp());
        lasPanel.setTitle(LangMgr.lang(Word2.INFORMATION));
        table.load(new ArrayList<>(), 0);
        return lasPanel;
    }
    
    private HPanel initSqlFilePanel() {
        HGridPanel sqlFilePanel = new HGridPanel(new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C8,GridSplitEnum.C1));
        sqlFilePanel.setComp(1, new LabelInput(LangMgr.lang(Word4.CHANNEL_NAME)+":"));
        sqlFilePanel.setComp(2, channelInput = new TextInput());
        sqlFilePanel.setComp(3, saveBut(isPub ? LangMgr.lang(Word4.TEXT_PUBLISHING) : LangMgr.lang(Word2.SUBSCRIPTION)));
        sqlFilePanel.setComp(4, isPub ? saveBut(LangMgr.lang(Word4.FILE_PUBLISHING)) : new LabelInput());
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C9);
        divLayout.setTopHeight(10);
        divLayout.setBottomHeight(5);
        HPanel panel = new HPanel(divLayout);
        panel.add(sqlFilePanel);
        return panel;
    }
    
    private HButton saveBut(String text) {
        HButton saveBut = new HButton() {
            @Override
            protected void onClick() {
                try {
                    if (!StringUtils.isNotBlank(channelInput.getValue())) return;
                    if (isPub) {
                        if (null == pubTool) {
                            if (!ConnUtil.isConnected(DBTypeEnum.redis,null,pubConn)) pubConn = CsVarUtil.newConn(jdbc);
                            pubTool = new RedisPubTool(pubConn, pubDir);
                        }
                        RedisPubBean pubBean = null;
                        if (getComp().getText().equals(LangMgr.lang(Word4.TEXT_PUBLISHING))) {
                            if (!StringUtils.isNotBlank(textArea.getText())) return;
                            pubBean = pubTool.publish(channelInput.getValue().trim(), textArea.getText());
                        } else {
                            HFileChooser hfc = new HFileChooser(HFileChooser.FileType.BOTH);
                            hfc.setDialogTitle(LangMgr.lang(Word4.FILE_BROWSING));
                            hfc.setApproveButtonText(LangMgr.lang(Word2.CHOOSE));
                            if ( hfc.showOpenDialog() == HFileChooser.APPROVE_OPTION){
                                pubBean = pubTool.publish(channelInput.getValue().trim(), hfc.getSelectedFile());
                            } else {
                                return;
                            }
                        }
                        Map<String, String> line = new HashMap<>();
                        line.put("id", pubBean.getId() + "");
                        line.put("channelName", pubBean.getChannelName());
                        line.put("date", DateUtil.dateToStr(pubBean.getDate(), false));
                        line.put("file", pubBean.getFile().getAbsolutePath());
                        line.put("subNum", pubBean.getSubNum() + "");
                        line.put("byteLen", pubBean.getByteLen() + "");
                        table.add(line);
                    } else {
                        if (null == subTool) {
                            if (!ConnUtil.isConnected(DBTypeEnum.redis,null,subConn)) subConn = CsVarUtil.newConn(jdbc);
                            subTool = new RedisSubTool(subConn, channelInput.getValue().trim(), subDir) {
                                @Override
                                protected void onSubBean(RedisSubBean bean, Exception e) {
                                    Map<String, String> line = new HashMap<>();
                                    line.put("id", bean.getId() + "");
                                    line.put("channelName", bean.getChannelName());
                                    line.put("date", DateUtil.dateToStr(bean.getDate(), false));
                                    line.put("file", bean.getFile().getAbsolutePath());
                                    line.put("byteLen", bean.getBytes().length + "");
                                    line.put("Operation","");
                                    table.add(line);
                                }
                            };
                            subTool.subscribe();
                            setText(LangMgr.lang(Word4.UNSUBSCRIBE));
                            channelInput.setEnabled(false);
                        } else {
                            setText(LangMgr.lang(Word2.SUBSCRIPTION));
                            table.load(new ArrayList<>(), 0);
                            subTool.unsubscribe();
                            subTool = null;
                            channelInput.setEnabled(true);
                        }
                    }
                    PopDlgUtil.info(CsVarUtil.parentFrame, LangMgr.lang(Word4.OPERATE_SUCCESSFULLY)+"!");
                } catch (Exception e) {
                    Lg.error(PubSubComp.class, e);
                    PopDlgUtil.error(CsVarUtil.parentFrame, e);
                }
            }
        };
        saveBut.setText(text);
        return saveBut;
    }
    
    private DataCol getCol(String text,String name) {
        DataCol col = new DataCol(text, name);
        col.setCellEditable(false);
        if (text.equals("file")) col.setWidth(400);
        return col;
    }
}