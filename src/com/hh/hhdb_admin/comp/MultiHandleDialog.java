package com.hh.hhdb_admin.comp;

import com.hh.frame.lang2.LangMgr;
import com.hh.frame.lang2.conf.Word2;
import com.hh.frame.swingui.view.HeightComp;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.ProgressBarInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HDivLayout;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.mgr.tree.CsFolderBean;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

/**
 * 创建后show自动启动，可暂停和继续
 */
public class MultiHandleDialog extends HDialog {
    public static final String TREENODE_NAME = "name";
    public static final String HANDLE_RESULT = "res";
    private final Object lock = new Object();
    private final CsFolderBean[] treeNodes;
    private final Thread thread;
    private final HTable resTable = new HTable();
    private ProgressBarInput progressBarInput;
    private HButton switchBtn;
    private Runnable afterDone = () -> {
    };
    private boolean isPaused = false;
    private boolean isStop = false;

    /**
     * @param parent    父窗口
     * @param treeNodes 需要处理的节点数组
     * @param handleIt  处理数组的方法
     */
    public MultiHandleDialog(HWindow parent, CsFolderBean[] treeNodes, Predicate<CsFolderBean> handleIt) {
        super(parent, 600, 400);
        this.treeNodes = treeNodes;

        setIconImage(IconFileUtil.getLogo());
        setStatusBar(new HBarPanel());
//        setDisposeOnHide(false);

        initRootPanel();
//        AtomicInteger x = new AtomicInteger();

        thread = new Thread(() -> {
            final String failed = LangMgr.lang(Word2.FAILED);
            final String successfully = LangMgr.lang(Word2.SUCCESSFULLY);
            int failedCount = 0;
            LinkedList<Map<String, String>> data = new LinkedList<>();
            synchronized (lock) {
                for (CsFolderBean treeNode : treeNodes) {
                    if (isStop)
                        break;
                    if (isPaused) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    boolean success = handleIt.test(treeNode);
                    HashMap<String, String> one = new HashMap<>();
                    one.put(TREENODE_NAME, treeNode.getName());
                    if (success) {
                        one.put(HANDLE_RESULT, successfully);
                        data.addLast(one);
                    } else {
                        one.put(HANDLE_RESULT, failed);
                        data.addFirst(one);
                        failedCount++;
                    }
                    resTable.load(data, 1);
                    if (failedCount > 0)
                        resTable.getComp().setRowSelectionInterval(0, failedCount - 1);
                    progressBarInput.setValue(Integer.parseInt(progressBarInput.getValue()) + 1);
                }
                switchBtn.setEnabled(false);
                switchBtn.setText(LangMgr.lang(Word2.DONE));
                afterDone.run();
            }
        });
    }

    public void setAfterDone(Runnable afterDone) {
        this.afterDone = afterDone;
    }

    private void initRootPanel() {
        HPanel lineOne = new HPanel(new HDivLayout(GridSplitEnum.C10));

        progressBarInput = new ProgressBarInput();
        progressBarInput.setMaximum(treeNodes.length);
        progressBarInput.setValue(0);
        lineOne.add(progressBarInput);

        switchBtn = new HButton(LangMgr.lang(Word2.STOP)) {
            @Override
            protected void onClick() {
                if (isPaused)
                    resume();
                else
                    pause();
            }
        };
        lineOne.add(switchBtn);

        resTable.addCols(new DataCol(TREENODE_NAME, LangMgr.lang(Word2.NAME)), new DataCol(HANDLE_RESULT, LangMgr.lang(Word2.RESULT)));

        LastPanel resultPanel = new LastPanel();
        resultPanel.setTitle(LangMgr.lang(Word2.RESULT));
        resultPanel.setWithScroll(resTable.getComp());

        HPanel root = new HPanel();
        root.add(new HeightComp(5));
        root.add(lineOne);
        root.setLastPanel(resultPanel);

        setRootPanel(root);
    }

    @Override
    public void show() {
        super.show();
        thread.start();
    }

    @Override
    protected void closeEvent() {
        super.closeEvent();
        isStop = true;
    }

    private void pause() {
        isPaused = true;
        switchBtn.setText(LangMgr.lang(Word2.CONTINUE));
    }

    private void resume() {
        synchronized (lock) {
            isPaused = false;
            switchBtn.setText(LangMgr.lang(Word2.STOP));
            lock.notifyAll();
        }
    }
}
