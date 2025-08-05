package com.hh.hhdb_admin;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JWindow;

import com.hh.frame.common.util.SleepUtil;
import com.hh.frame.lg.Lg;
import com.hh.frame.swingui.cs_desk.license2.LicComp;
import com.hh.frame.swingui.cs_desk.license2.LicStatus;
import com.hh.frame.swingui.laf.LafMgr;
import com.hh.frame.swingui.view.container.HAnimPanel;
import com.hh.frame.swingui.view.pop_dlg.PopDlgUtil;
import com.hh.frame.swingui.view.util.CursorUtil;
import com.hh.frame.swingui.view.util.WindowUtil;
import com.hh.hhdb_admin.common.bean.BaseOpBean;
import com.hh.hhdb_admin.common.bean.BaseOpEnum;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.CsVarUtil;
public class HHdbAdmin {
	private static JWindow window = new JWindow();
	public static void main(String[] args) {
		HAnimPanel panel = null;
		try {
			CursorUtil.setWaitingCursor(window);
			ImageIcon icon= IconFileUtil.getTheme();
			LafMgr.setMenuIcon(IconFileUtil.getLogo());
			int w=icon.getIconWidth();
			int h=icon.getIconHeight();
			int rh=5;
			int th=h-17;
			Image baseImg=icon.getImage();
			String pName="PROGRESS_BAR";
			String tName="STARTING";
			String vers = "VERSION";
			panel=new HAnimPanel() {
				int i = 0;
				protected void drawGraphics(Graphics2D g2) {
					g2.setFont(g2.getFont().deriveFont((float)13));
					g2.drawImage(baseImg,0,0,getComp());
					i += 1;
					
					Point rp=getPoint(pName);
					rp.x += 4;
					rp.y=h;
					fillRect(g2,rp,w-rp.x,rh,Color.orange);
					
					Point tp=getPoint(tName);
					String s="启动中...";
					drawString(g2,s,tp);
					
					
					
					Point vep=getPoint(vers);
					drawString(g2,"版本:"+new CSProduct().getVersion(),vep);
					
					if(i==140 && getTimer().isRunning()) {
						SleepUtil.sleep1000();
						stop();
					}
				}
			};
			panel.putObject(baseImg, new Point(0,0));
			panel.putObject(pName, new Point(0,0));
			panel.putObject(tName, new Point(10,th));
			panel.putObject(vers, new Point(10,th-20));
			panel.start();
			window.add(panel.getComp());
			window.setSize(w, h+rh);
			window.setLocation(WindowUtil.getPoint4Center(window));
			window.setVisible(true);

            CsVarUtil.init();
            LicStatus status = CsVarUtil.licMgr.check().getStatus();
            if(status == LicStatus.no_license||status == LicStatus.license_error) {
                LicComp comp = new LicComp(CsVarUtil.parentFrame.getWindow(),CsVarUtil.licMgr,true);
                comp.show();
                LicStatus status2 = CsVarUtil.licMgr.check().getStatus();
                if(status2!=LicStatus.normal) {
                    System.exit(0);
                }
            }
			CsVarUtil.eng2.doCall(CsMgrEnum.LOGIN, new BaseOpBean(BaseOpEnum.INIT));
            
            if(status == LicStatus.expired) {
                CsVarUtil.task.setSource(CsVarUtil.parentFrame.getWindow());
                CsVarUtil.task.start();
            }
			while(!panel.isDone()) {
				SleepUtil.sleep10();
			}
			
		} catch (Exception e1) {
			if (null != panel) panel.stop();
			Lg.error(HHdbAdmin.class, e1);
			PopDlgUtil.error(e1);
			System.exit(0);
		}finally {
			window.dispose();
			CursorUtil.setDefaultCursor(window);
		}
	}
	
}
