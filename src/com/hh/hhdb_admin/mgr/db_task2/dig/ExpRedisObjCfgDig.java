package com.hh.hhdb_admin.mgr.db_task2.dig;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.dbtask2.bean.BaseTaskBean;
import com.hh.frame.dbtask2.redis_exp.RedisExpBean;
import com.hh.frame.dbtask2.redis_exp.RedisObjExpTask;
import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.lang2.conf.Word4;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.input.BigDecimalSpinner;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.with_label.LabelConf;
import com.hh.frame.swingui.view.with_label.WithLabelInput;
import com.hh.hhdb_admin.common.CsL;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * redis对象导出
 */
public class ExpRedisObjCfgDig extends AbsCfgDig {
    
    private TextInput keyPatternInput = new TextInput();
    private BigDecimalSpinner maxKeyLoad = new BigDecimalSpinner();
    private FileChooserInput dirPathInput = new FileChooserInput("dirPath");

    public ExpRedisObjCfgDig(BaseTaskBean bean) {
        super(bean);
        
        keyPatternInput.setValue("*");
        maxKeyLoad.setValue(10000);

        dirPathInput.setDialogTitle(LangMgr.lang(Word2.SAVE));
        dirPathInput.setDefaultName("redis_data.sql");
        
        dialog.setWindowTitle(LangMgr.lang(Word4.EXPORT_CONFIGURATION));
        setJdbcPanel();
        rootPanel.add(initPanel());
        rootPanel.add(getToolBar());
        dialog.setRootPanel(rootPanel);
        setSize(rootPanel.getHeight() + 50);
        setTaskName(bean.getName());
    }

    @Override
    protected void resetForm() {
        super.resetForm();
        keyPatternInput.setValue("*");
        maxKeyLoad.setValue(10000);
        dirPathInput.setValue("");
    }

    @Override
    protected void setTask() throws Exception {
        String key = keyPatternInput.getValue();
        String rowStr = maxKeyLoad.getValue();
        String url = dirPathInput.getValue();
        
        if (StringUtils.isBlank(key)) key = "*";
        if (StringUtils.isBlank(url)) {
            throw new Exception(LangMgr.sLang(CsL.URL_NOT_NULL));
        }
    
        RedisExpBean bean = new RedisExpBean(0,getTaskName(),getJdbc(),new File(url));
        bean.setKeyPattern(key);
        bean.setMaxKeyLoad(Integer.parseInt(rowStr));
        task = new RedisObjExpTask(bean) {
            @Override
            protected void taskDone() {
                System.out.println("任务结束");
            }
        };
    }

    private HPanel initPanel() {
        LabelConf labelConf = new LabelConf(GridSplitEnum.C3, AlignEnum.RIGHT,AlignEnum.LEFT);
        HDivLayout layout = new HDivLayout(0, 5, GridSplitEnum.C12);
        layout.setRightWidth(50);
        HPanel panel = new HPanel(layout);
        panel.setTitle(LangMgr.sLang(CsL.EXPORT_CONFIGURATION));
        panel.add(new HeightComp(10));
        panel.add(new WithLabelInput(labelConf,new LabelInput(LangMgr.sLang(CsL.FILTER_KEYWORDS) + "："), keyPatternInput));
        panel.add(new WithLabelInput(labelConf,new LabelInput(LangMgr.sLang(CsL.MAXIMUM_QUANTITY) + "："), maxKeyLoad));
        panel.add(new WithLabelInput(labelConf,new LabelInput(LangMgr.lang(Word4.SAVE_CONTENTS) + "："), dirPathInput));
        panel.add(new HeightComp(10));
        
        return panel;
    }
}
