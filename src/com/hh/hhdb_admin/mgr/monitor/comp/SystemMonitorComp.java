package com.hh.hhdb_admin.mgr.monitor.comp;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.common.base.DBTypeEnum;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.PasswordInput;
import com.hh.frame.swingui.view.input.RadioGroupInput;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.monitor.MonitorComp;
import com.hh.hhdb_admin.mgr.monitor.panel.CpuMonitor;
import com.hh.hhdb_admin.mgr.monitor.panel.DiskMonitor;
import com.hh.hhdb_admin.mgr.monitor.panel.MemMonitor;
import com.hh.hhdb_admin.mgr.monitor.panel.NetMonitor;
import com.hh.hhdb_admin.mgr.monitor.util.MonitorUtil;
import com.hh.hhdb_admin.mgr.monitor.util.ScriptRunner;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author YuSai
 */
public abstract class SystemMonitorComp extends MonitorComp {

    private static final String LOG_NAME = SystemMonitorComp.class.getSimpleName();

    private final HPanel panel = new HPanel();

    private final Connection conn;

    public RadioGroupInput rInput;

    private final HDialog dialog;

    public ScriptRunner scriptRunner;

    private CpuMonitor cpuMonitor;

    private MemMonitor memMonitor;

    public boolean openTab = false;

    public SystemMonitorComp(Connection conn, DBTypeEnum dbTypeEnum) {
        this.conn = conn;
        HPanel radioPanel = new HPanel(new HDivLayout(GridSplitEnum.C6));
        TextInput hostIp = new TextInput("hostIp");
        TextInput username = new TextInput("username");
        PasswordInput password = new PasswordInput("password");
        rInput = new RadioGroupInput("connectType", radioPanel) {
            @Override
            protected void stateChange(ItemEvent e) {
                JRadioButton jb = (JRadioButton) e.getSource();
                if ("SSH".equals(jb.getText())) {
                    hostIp.setEnabled(true);
                    username.setEnabled(true);
                    password.setEnabled(true);
                } else {
                    hostIp.setValue("");
                    hostIp.setEnabled(false);
                    username.setValue("");
                    username.setEnabled(false);
                    password.setValue("");
                    password.setEnabled(false);
                }
            }
        };
        rInput.add("SSH", "SSH");
        if (MonitorUtil.isHhOrPg(dbTypeEnum)) {
            rInput.add("sys_util", "sys_util");
        }
        rInput.setSelected("SSH");
        dialog = new HDialog(StartUtil.parentFrame, 400, 280);
        HButton submitBtn = new HButton(getLang("submit")) {
            @Override
            public void onClick() {
                try {
                    if ("SSH".equals(rInput.getValue())) {
                        if (StringUtils.isEmpty(hostIp.getValue())) {
                            JOptionPane.showMessageDialog(null, getLang("pleaseEnterHostIp"), getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        if (StringUtils.isEmpty(username.getValue())) {
                            JOptionPane.showMessageDialog(null, getLang("pleaseEnterUsername"), getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        if (StringUtils.isEmpty(password.getValue())) {
                            JOptionPane.showMessageDialog(null, getLang("pleaseEnterPassword"), getLang("hint"), JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        scriptRunner = new ScriptRunner(hostIp.getValue(), username.getValue(), password.getValue());
                        scriptRunner.connect();
                    } else {
                        List<Map<String, Object>> lists = MonitorUtil.getExtendList(conn, dbTypeEnum);
                        if (!MonitorUtil.isTrue(lists, "sys_util")) {
                            //为sys_util方式安装扩展
                            MonitorUtil.installExtend("sys_util", conn);
                        }
                    }
                    initPanel();
                    dialog.dispose();
                    repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                    logUtil.error(LOG_NAME, e);
                    JOptionPane.showMessageDialog(null, e.getMessage(), getLang("mistake"), JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        submitBtn.setIcon(getIcon("submit"));
        HButton cancelBtn = new HButton(getLang("cancel")) {
            @Override
            public void onClick() {
                dialog.dispose();
            }
        };
        cancelBtn.setIcon(getIcon("cancel"));
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        HBarPanel barPanel = new HBarPanel(barLayout);
        barPanel.add(submitBtn, cancelBtn);
        HDivLayout divLayout = new HDivLayout(GridSplitEnum.C12);
        divLayout.setyGap(10);
        divLayout.setTopHeight(20);
        HPanel panel = new HPanel(divLayout);
        dialog.setWindowTitle(getLang("connectType"));
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        if (MonitorUtil.isHhOrPg(dbTypeEnum)) {
            panel.add(getLabelInput(getLang("connectType"), rInput));
        }
        panel.add(getLabelInput(getLang("hostIp"), hostIp));
        panel.add(getLabelInput(getLang("username"), username));
        panel.add(getLabelInput(getLang("password"), password));
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.setHead(panel.getComp());
        lastPanel.setFoot(barPanel.getComp());
        HPanel rootPanel = new HPanel();
        rootPanel.setLastPanel(lastPanel);
        dialog.setRootPanel(rootPanel);
        dialog.show();
    }

    private HGridPanel getLabelInput(String label, AbsInput input) {
        HGridLayout gridLayout = new HGridLayout(GridSplitEnum.C4);
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, input);
        return gridPanel;
    }

    private void initPanel() {
        openTab = true;
        HTabPane tabPane = new HTabPane();
        tabPane.setCloseBtn(false);
        // cpu监控
        cpuMonitor = new CpuMonitor(this, conn);
        tabPane.addPanel("cpu", getLang("cpuMonitor"), cpuMonitor.getPanel().getComp(), false);
        // 内存监控
        memMonitor = new MemMonitor(this, conn);
        tabPane.addPanel("memory", getLang("memMonitor"), memMonitor.getPanel().getComp(), false);
        // 硬盘监控
        DiskMonitor diskMonitor = new DiskMonitor(this, conn);
        tabPane.addPanel("disk", getLang("diskMonitor"), diskMonitor.getPanel().getComp(), false);
        // 网络监控
        NetMonitor netMonitor = new NetMonitor(this, conn);
        tabPane.addPanel("network", getLang("networkMonitor"), netMonitor.getPanel().getComp(), false);
        LastPanel lastPanel = new LastPanel(false);
        lastPanel.set(tabPane.getComp());
        panel.setLastPanel(lastPanel);
    }

    public HPanel getPanel() {
        return panel;
    }

    public String getTitle() {
        return getLang("systemMonitor");
    }

    public void show(HDialog dialog) {
        dialog.setRootPanel(panel);
        dialog.setWindowTitle(getLang("systemMonitor"));
        dialog.show();
    }

    public void closeRunnable() {
        if (null != cpuMonitor) {
            cpuMonitor.closeRunnable();
        }
        if (null != memMonitor) {
            memMonitor.closeRunnable();
        }
        if (null != scriptRunner) {
            scriptRunner.close();
        }
    }

    public abstract void repaint();

}
