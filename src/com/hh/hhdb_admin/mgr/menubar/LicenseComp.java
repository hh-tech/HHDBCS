package com.hh.hhdb_admin.mgr.menubar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.lic.LicBean;
import com.hh.frame.lic.VerifyLicTool;
import com.hh.frame.swingui.view.container.HBarPanel;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HFrame;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.container.HWindow;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.fc.FileChooserInput;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.util.PopPaneUtil;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

/**
 * @author YuSai
 */
public class LicenseComp {

    private HWindow dialog;
    private HTable table;

    public LicenseComp(boolean flag) {
    	
    	if(!StartUtil.parentFrame.isVisible()) {
    		HFrame frame = new HFrame(800, 400);
    		frame.setCloseType(true);
    		dialog = frame;
    	}else {
	        dialog = new HDialog(StartUtil.parentFrame,800,600,true) {
	            @Override
	            protected void closeEvent() {
	                if (flag) {
	                    System.exit(0);
	                }
	            }
	        };
    	}
        dialog.setRootPanel(getPanel(flag));
        dialog.setIconImage(IconFileUtil.getLogo().getImage());
        dialog.getWindow().setLocationRelativeTo(null);
        dialog.setWindowTitle(MenubarComp.getLang("licenseInfo"));
        table.load(getTableData(), 1);
        dialog.show();
    }
    
    

    public HWindow getDialog() {
		return dialog;
	}



	private HPanel getPanel(boolean flag) {
        HPanel rootPanel = new HPanel();
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton updateBtn = new HButton(MenubarComp.getLang("updateLicense")) {
            @Override
            protected void onClick() {
                updateLicense();
            }
        };
        updateBtn.setIcon(getIcon("update"));
        barPanel.add(updateBtn);
        if (flag) {
            HButton nextBtn = new HButton(MenubarComp.getLang("next")) {
                @Override
                protected void onClick() {
                    try {
                        VerifyLicTool vt = StartUtil.getVt();
                        if (vt == null) {
                            PopPaneUtil.info(dialog.getWindow(), MenubarComp.getLang("PleUptLicense"));
                            return;
                        }
                        if (vt.expired()) {
                            PopPaneUtil.info(dialog.getWindow(), MenubarComp.getLang("PleUptLicenseExpire"));
                            return;
                        }
                        dialog.dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        PopPaneUtil.error(dialog.getWindow(), e);
                    }
                }
            };
            nextBtn.setIcon(getIcon("next"));
            barPanel.add(nextBtn);
        }
        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(barPanel.getComp());
        table = new HTable();
        table.setCellEditable(false);
        table.hideSeqCol();
        DataCol name = new DataCol("name", MenubarComp.getLang("attribute"));
        name.setWidth(150);
        table.addCols(name, new DataCol("value", MenubarComp.getLang("value")));
        lastPanel.setWithScroll(table.getComp());
        rootPanel.setLastPanel(lastPanel);
        return rootPanel;
    }

    public List<Map<String, String>> getTableData() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> status = new HashMap<>();
        status.put("name", MenubarComp.getLang("licenseStatus"));
        try {
            VerifyLicTool vt = StartUtil.getVt();
            if (vt == null) {
                status.put("value", MenubarComp.getLang("noLicense"));
                list.add(status);
            } else {
                if (vt.expired()) {
                    status.put("value", MenubarComp.getLang("licenseExpired"));
                } else {
                    status.put("value", MenubarComp.getLang("LicenseAvailable"));
                }
                list.add(status);
                getLicense(vt.getLicBean(), list);
            }
        } catch (Exception e) {
            status.put("value", MenubarComp.getLang("licenseError"));
            list.add(status);
        }
        return list;
    }

    private void getLicense(LicBean licBean, List<Map<String, String>> list) throws Exception {
        Map<String, String> compamy = new HashMap<>();
        compamy.put("name", MenubarComp.getLang("licenseCompany"));
        compamy.put("value", licBean.getCompany());
        Map<String, String> product = new HashMap<>();
        product.put("name", MenubarComp.getLang("licenseProduct"));
        product.put("value", licBean.getProduct());
        Map<String, String> version = new HashMap<>();
        version.put("name", MenubarComp.getLang("productVersion"));
        version.put("value", licBean.getProductVersion());
        Map<String, String> expireDate = new HashMap<>();
        expireDate.put("name", MenubarComp.getLang("expireDate"));
        expireDate.put("value", licBean.getExpDateStr());
        Map<String, String> syqx = new HashMap<>();
        syqx.put("name", MenubarComp.getLang("daysRemaining"));
        syqx.put("value", licBean.getDays2Expired() + "(D)");
        list.add(compamy);
        list.add(product);
        list.add(version);
        list.add(expireDate);
        list.add(syqx);
    }

    private void updateLicense() {
        HDialog uDialog = new HDialog(dialog, 600, 120);
        uDialog.setIconImage(IconFileUtil.getLogo().getImage());
        uDialog.getWindow().setLocationRelativeTo(null);
        uDialog.setWindowTitle(MenubarComp.getLang("chooseLicense"));
        HPanel panel = new HPanel();
        LastPanel lastPanel = new LastPanel();
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.CENTER);
        FileChooserInput chooserInput = new FileChooserInput("license");
        chooserInput.setBtnText(MenubarComp.getLang("btn_value"));
        //默认是dat文件
        chooserInput.addFilter(MenubarComp.getLang("licenseFile"), "dat");
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton submitBtn = new HButton(MenubarComp.getLang("Ok")) {
            @Override
            protected void onClick() {
                try {
                    String licenseFile = chooserInput.getValue();
                    if (StringUtils.isEmpty(licenseFile.trim())) {
                       PopPaneUtil.info(uDialog.getWindow(), MenubarComp.getLang("licenseFileNotNull"));
                       return;
                    }
                    File licFile = new File(System.getProperty("user.dir") , "etc/lic/");
                    File[] files = licFile.listFiles();
                    if (files != null) {
                        for (File f : files) {
                            if (f.getName().endsWith(".dat")) {
                                f.delete();
                            }
                        }
                    }
                    File source = new File(licenseFile);
                    FileUtils.copyFile(source, new File(System.getProperty("user.dir") , "etc/lic/" + source.getName()));
                    PopPaneUtil.info(uDialog.getWindow(), MenubarComp.getLang("updateSuccess"));
                    uDialog.dispose();
                    table.load(getTableData(), 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    PopPaneUtil.error(uDialog.getWindow(), e);
                }
            }
        };
        HButton cancelBtn = new HButton(MenubarComp.getLang("cancel")) {
            @Override
            protected void onClick() {
                uDialog.dispose();
            }
        };
        barPanel.add(submitBtn);
        barPanel.add(cancelBtn);
        lastPanel.set(chooserInput.getComp());
        lastPanel.setFoot(barPanel.getComp());
        panel.setLastPanel(lastPanel);
        uDialog.setRootPanel(panel);
        uDialog.show();
    }

    private ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.MENUBAR.name(), name, IconSizeEnum.SIZE_16));
    }

  
}
