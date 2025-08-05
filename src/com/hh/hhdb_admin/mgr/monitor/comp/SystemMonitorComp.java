package com.hh.hhdb_admin.mgr.monitor.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.SshBean;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.cs_desk.SshPanel;
import com.hh.frame.swingui.cs_desk.mon.SSHMonitor;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.container.tab_panel.HTabPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.fa.FaSolidEnum;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;

/**
 * @author YuSai
 */
public class SystemMonitorComp  {
    private final HPanel panel = new HPanel();
    
    
    private SSHMonitor monitor;
    
    
    public SystemMonitorComp() {
        HDivLayout playout=new HDivLayout(GridSplitEnum.C3);
        playout.setTopHeight(50);
        playout.setLeftWidth(150);
        playout.setRightWidth(150);
        playout.setBottomHeight(20);
        playout.setyGap(20);
        SshPanel sshPanel = new SshPanel(playout);
        HBarLayout layout = new HBarLayout();
        layout.setAlign(AlignEnum.CENTER);
        layout.setTopHeight(10);
        layout.setBottomHeight(10);
        layout.setxGap(20);
        HBarPanel stBar=new HBarPanel(layout);
        HButton okBtn=new HButton("登入") {
            @Override
            protected void onClick() {
                try {
                    SshBean sshBean = sshPanel.getFormData();
                    monitor = new SSHMonitor(sshBean);
                    initPanel();
                }catch (Exception e){
                    Lg.error(SystemMonitorComp.class, e);
                    PopDlgUtil.error(CsVarUtil.parentFrame,e);
                }
            }
        };
        okBtn.setIcon(FaSolidEnum.RIGHT_TO_BRACKET,16);
        stBar.add(okBtn);
    
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(sshPanel.getComp());
        lastPanel.set(stBar.getComp());
        panel.setLastPanel(lastPanel);
    }

    public HPanel getPanel() {
        return panel;
    }

    public void closeRunnable() {
        try {
            if (monitor != null) {
            	monitor.close();
            }
        }catch (Exception e){
//            LM.error(SystemMonitorComp.class, e);
        	Lg.error(SystemMonitorComp.class, e);
            PopDlgUtil.error(CsVarUtil.parentFrame,e);
        }
    }
    
    private void initPanel() throws Exception {
       
        HTabPanel tabPane = monitor.getHComp();
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(tabPane.getComp());
        panel.setLastPanel(lastPanel);
        panel.updateUI();
    }
    

}
