package com.hh.hhdb_admin.mgr.about;

import com.hh.frame.lang.LangEnum;
import com.hh.frame.lang.LangMgr;
import com.hh.frame.lang.LangUtil;
import com.hh.frame.swingui.view.container.HDialog;
import com.hh.frame.swingui.view.container.HPanel;
import com.hh.frame.swingui.view.ctrl.HImage;
import com.hh.frame.swingui.view.ctrl.HImgButton;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.hhdb_admin.CsMgrEnum;
import com.hh.hhdb_admin.common.icon.IconBean;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.icon.IconSizeEnum;
import com.hh.hhdb_admin.common.util.StartUtil;

import javax.swing.*;
import java.awt.*;

public class AboutComp {
    private final HDialog dialog;
    private final ImageIcon[] ICONS = {
            getIcon("splash1"),
            getIcon("splash2"),
            getIcon("splash3"),
            getIcon("splash4")
    };
    private int imgIndex;

    private static final String domainName = AboutComp.class.getName();

    private final static String LK_NEXT = "NEXT";
    private final static String LK_PREV = "PREV";
    private final static String LK_VERSION = "VERSION";
    private final static String LK_PRODUCT = "PRODUCT";
    private final static String LK_TITLE = "TITLE";


    static {
        LangMgr.merge(domainName, LangUtil.loadLangRes(AboutComp.class));
    }

    public AboutComp() {
        dialog = new HDialog(null,ICONS[0].getIconWidth(), ICONS[0].getIconHeight() + 25,true);
        dialog.setWindowTitle(LangMgr.getValue(domainName, LK_TITLE));
        dialog.setIconImage(IconFileUtil.getLogo(IconSizeEnum.SIZE_16).getImage());
        HPanel panel = new HPanel();
        panel.add(getImg());
        dialog.setRootPanel(panel);
    }

    public void show() {
        dialog.show();
    }


    private HImage getImg() {
        HImage img = new HImage(ICONS[imgIndex]);
        HImgButton imgBtn1 = new HImgButton() {
            @Override
            public void onClick() {
                if (3 == imgIndex) {
                    imgIndex = -1;
                }
                imgIndex++;
                img.setImg(ICONS[imgIndex]);
            }

        };
        imgBtn1.setMouseExitedIconIcon(getBtnIcon("next", true));
        imgBtn1.setMouseEnteredIcon(getBtnIcon("next", false));
        imgBtn1.setToolTipText(LangMgr.getValue(domainName, LK_NEXT));
        imgBtn1.setSize(16, 16);

        HImgButton imgBtn2 = new HImgButton() {
            @Override
            public void onClick() {
                if (0 == imgIndex) {
                    imgIndex = 4;
                }
                imgIndex--;
                img.setImg(ICONS[imgIndex]);
            }

        };


        imgBtn2.setMouseExitedIconIcon(getBtnIcon("pre", true));
        imgBtn2.setMouseEnteredIcon(getBtnIcon("pre", false));
        imgBtn2.setToolTipText(LangMgr.getValue(domainName, LK_PREV));

        img.add(0, 145, 30, 50, imgBtn2);
        img.add(455, 145, 30, 50, imgBtn1);

        LabelInput product = new LabelInput(LangMgr.getValue(domainName, LK_PRODUCT));

        LabelInput version = new LabelInput(LangMgr.getValue(domainName, LK_VERSION) + StartUtil.CS_VERSION);


        if (StartUtil.default_language == LangEnum.EN) {
            product.setFont(new Font("arial", Font.BOLD, 17));
            version.setFont(new Font("arial", Font.BOLD, 17));
            img.add(15, 140, 320, 32, product);
            img.add(30, 180, 110, 32, version);
        } else {
            product.setFont(new Font("宋体", Font.BOLD, 17));
            version.setFont(new Font("宋体", Font.BOLD, 17));
            img.add(15, 140, 320, 32, product);
            img.add(30, 180, 100, 32, version);
        }

        return img;
    }

    private ImageIcon getIcon(String name) {
        return IconFileUtil.getIcon(new IconBean(CsMgrEnum.ABOUT.name(), name, IconSizeEnum.OTHER));
    }

    private ImageIcon getBtnIcon(String name, boolean disabled) {
        IconBean iconBean = new IconBean(CsMgrEnum.ABOUT.name(), name, IconSizeEnum.SIZE_32);
        iconBean.setDisabled(disabled);
        return IconFileUtil.getIcon(iconBean);
    }
}
