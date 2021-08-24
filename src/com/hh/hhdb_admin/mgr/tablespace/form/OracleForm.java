package com.hh.hhdb_admin.mgr.tablespace.form;

import com.hh.frame.common.base.AlignEnum;
import com.hh.frame.json.Json;
import com.hh.frame.json.JsonObject;
import com.hh.frame.swingui.view.abs.AbsInput;
import com.hh.frame.swingui.view.container.*;
import com.hh.frame.swingui.view.ctrl.HButton;
import com.hh.frame.swingui.view.input.CheckBoxInput;
import com.hh.frame.swingui.view.input.LabelInput;
import com.hh.frame.swingui.view.input.SelectBox;
import com.hh.frame.swingui.view.input.TextInput;
import com.hh.frame.swingui.view.layout.GridSplitEnum;
import com.hh.frame.swingui.view.layout.HGridLayout;
import com.hh.frame.swingui.view.layout.bar.HBarLayout;
import com.hh.frame.swingui.view.tab.HTabRowBean;
import com.hh.frame.swingui.view.tab.HTable;
import com.hh.frame.swingui.view.tab.RowStatus;
import com.hh.frame.swingui.view.tab.col.DataCol;
import com.hh.frame.swingui.view.tab.col.ListCol;
import com.hh.frame.swingui.view.tab.col.bool.BoolCol;
import com.hh.frame.swingui.view.textEditor.HTextArea;
import com.hh.frame.swingui.view.tree.HTree;
import com.hh.frame.swingui.view.tree.HTreeNode;
import com.hh.frame.swingui.view.ui.HHSwingUi;
import com.hh.hhdb_admin.common.icon.IconFileUtil;
import com.hh.hhdb_admin.common.util.StartUtil;
import com.hh.hhdb_admin.mgr.constraint.ConstraintComp;
import com.hh.hhdb_admin.mgr.tablespace.TableSpaceUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author YuSai
 */
public class OracleForm extends AbsForm {

    private final String PATH = "path";
    private final String AUTO_EXTEND = "autoExtend";
    private final String NEXT_SIZE = "nextSize";
    private final String NEXT_SIZE_UNIT = "nextSizeUnit";
    private final String UNLIMITED = "unlimited";
    private final String MAX_SIZE = "maxSize";
    private final String MAX_SIZE_UNIT = "maxSizeUnit";

    private HTable table;
    private final HPanel panel;
    private TextInput spaceNameInput;
    private SelectBox typeSelectBox;
    private TextInput pathInput;
    private SelectBox autoExtendBox;
    private TextInput nextSizeInput;
    private SelectBox nextSizeUnitBox;
    private CheckBoxInput unlimitedInput;
    private TextInput maxSizeInput;
    private SelectBox maxSizeUnitBox;

    private SelectBox fileTypeBox;
    private TextInput blockSizeInput;
    private SelectBox blockSizeUnitBox;
    private TextInput resizeInput;
    private SelectBox resizeUnitBox;
    private SelectBox compressBox;
    private SelectBox areaBox;

    private CheckBoxInput manualInput;
    private SelectBox localAreaBox;
    private TextInput uniformSizeInput;
    private SelectBox uniformUnitBox;
    private TextInput initSizeInput;
    private SelectBox initUnitBox;
    private TextInput nextStepInput;
    private SelectBox nextStepUnitBox;
    private TextInput minExtentsInput;
    private TextInput maxExtentsInput;
    private CheckBoxInput maxExtentsCheck;
    private TextInput maxExtentsSizeInput;
    private SelectBox maxExtentsSizeUnitBox;
    private CheckBoxInput maxExtentsSizeCheck;
    private TextInput increaseInput;
    private CheckBoxInput encryptInput;

    private SelectBox recordBox;
    private CheckBoxInput focLogInput;
    private CheckBoxInput offlineInput;
    private SelectBox flashBackBox;
    private CheckBoxInput encryptionInput;
    private SelectBox algorithmBox;
    private CheckBoxInput guaranteeInput;

    private final AtomicInteger atomicId = new AtomicInteger();
    private final List<String> unitLists = Arrays.asList("", "K", "M", "G", "T", "P", "E");
    private final List<String> onOffLists = Arrays.asList("", "ON", "OFF");
    private HPanel storagePanel;
    private HPanel seniorPanel;
    private HTree tree;

    public static void main(String[] args) throws Exception {
        HHSwingUi.init();
        IconFileUtil.setIconBaseDir(new File(StartUtil.getEtcFile(), "icon"));
        HFrame frame = new HFrame();
        frame.setRootPanel(new OracleForm().getPanel());
        frame.maximize();
        frame.show();
    }

    public OracleForm() {
        HSplitPanel splitPanel = new HSplitPanel();
        splitPanel.setSplitWeight(0.2);
        HPanel generalPanel = getGeneralPanel();
        initTree(splitPanel, generalPanel);
        HPanel lPanel = new HPanel();
        lPanel.add(tree);
        splitPanel.setPanelOne(lPanel);
        splitPanel.setPanelTwo(generalPanel);
        panel = new HPanel();
        LastPanel lastPanel = new LastPanel();
        lastPanel.set(splitPanel.getComp());
        panel.setLastPanel(lastPanel);
    }

    @Override
    public JsonObject getData() {
        return null;
    }

    @Override
    public void reset() {

    }

    @Override
    public String getSql() {
        StringBuilder saveSql = new StringBuilder("CREATE ");
        String type = typeSelectBox.getValue();
        String fileType = fileTypeBox.getValue();
        String blockSize = blockSizeInput.getValue();
        String blockSizeUnit = blockSizeUnitBox.getValue();
        String resize = resizeInput.getValue();
        String resizeUnit = resizeUnitBox.getValue();
        String compress = compressBox.getValue();
        String record = recordBox.getValue();
        String forceLogging = focLogInput.getValue();
        String offline = offlineInput.getValue();
        String flashBack = flashBackBox.getValue();
        String encryption = encryptionInput.getValue();
        String algorithm = algorithmBox.getValue();
        if (StringUtils.isNotEmpty(fileType)) {
            saveSql.append(fileType).append(" ");
        }
        String spaceName = StringUtils.isEmpty(spaceNameInput.getValue()) ? "[spaceName]" : spaceNameInput.getValue();
        if ("TEMPORARY".equals(type)) {
            saveSql.append("TEMPORARY TABLESPACE ").append(spaceName).append(" TEMPFILE\n");
        } else if ("UNDO".equals(type)) {
            saveSql.append("UNDO TABLESPACE ").append(spaceName).append(" DATAFILE\n");
        } else {
            saveSql.append("TABLESPACE ").append(spaceName).append(" DATAFILE\n");
        }
        List<HTabRowBean> rowBeans = table.getRowBeans(RowStatus.ADD);
        for (int i = 0; i < rowBeans.size(); i++) {
            Map<String, String> rowMap = rowBeans.get(i).getCurrRow();
            String name = rowMap.get("name");
            String size = rowMap.get("size");
            String unit = rowMap.get("unit");
            String online = rowMap.get("online");
            String reuse = rowMap.get("reuse");
            JsonObject attrJson = Json.parse(rowMap.get("attr")).asObject();
            String path = attrJson.getString(PATH);
            String autoExtend = attrJson.getString(AUTO_EXTEND);
            String nextSize = attrJson.getString(NEXT_SIZE);
            String nextSizeUnit = attrJson.getString(NEXT_SIZE_UNIT);
            String unlimited = attrJson.getString(UNLIMITED);
            String maxSize = attrJson.getString(MAX_SIZE);
            String maxSizeUnit = attrJson.getString(MAX_SIZE_UNIT);
            if (i > 0) {
                if (rowBeans.size() > 1) {
                    saveSql.append(", \n");
                }
            }
            if (i == rowBeans.size() - 1) {
                path = pathInput.getValue();
                autoExtend = autoExtendBox.getValue();
                nextSize = nextSizeInput.getValue();
                nextSizeUnit = nextSizeUnitBox.getValue();
                unlimited = String.valueOf(unlimitedInput.isChecked());
                maxSize = maxSizeInput.getValue();
                maxSizeUnit = maxSizeUnitBox.getValue();
            }
            saveSql.append("'").append(StringUtils.isEmpty(path) ? "[path" + (i + 1) + "]" : path).append(File.separator);
            saveSql.append(StringUtils.isEmpty(name) ? "[name" + (i + 1) + "]" : name);
            saveSql.append("' SIZE ").append(StringUtils.isEmpty(size) ? "[size" + (i + 1) + "]" : size);
            saveSql.append(" ").append(StringUtils.isEmpty(unit) ? "[unit" + (i + 1) + "]" : unit).append(Boolean.parseBoolean(reuse) ? " REUSE" : "");
            if ("ON".equals(autoExtend)) {
                saveSql.append(" AUTOEXTEND ").append(autoExtend).append(" NEXT ");
                saveSql.append(StringUtils.isEmpty(nextSize) ? "[nextSize" + (i + 1) + "]" : nextSize);
                saveSql.append(" ").append(StringUtils.isEmpty(nextSizeUnit) ? "[nextSizeUnit" + (i + 1) + "]" : nextSizeUnit);
                saveSql.append(" MAXSIZE ");
                if (Boolean.parseBoolean(unlimited)) {
                    saveSql.append(Boolean.parseBoolean(unlimited) ? "UNLIMITED" : "");
                } else {
                    saveSql.append(StringUtils.isEmpty(maxSize) ? "[maxSize" + (i + 1) + "]" : maxSize);
                    saveSql.append(" ").append(StringUtils.isEmpty(maxSizeUnit) ? "[maxSizeUnit" + (i + 1) + "]" : maxSizeUnit);
                }
            }

            if (!Boolean.parseBoolean(online)) {
                saveSql.append(", \nALTER DATABASE");
                if ("TEMPORARY".equals(type)) {
                    saveSql.append(" TEMPFILE '");
                } else {
                    saveSql.append(" DATAFILE '");
                }
                saveSql.append(StringUtils.isEmpty(path) ? "[path" + (i + 1) + "]" : path);
                saveSql.append(File.separator).append(StringUtils.isEmpty(name) ? "[name" + (i + 1) + "]" : name).append("' OFFLINE");
            }
        }
        if (StringUtils.isNotEmpty(blockSize)) {
            saveSql.append("\nBLOCKSIZE ").append(blockSize);
            saveSql.append(" ").append(StringUtils.isEmpty(blockSizeUnit) ? "[blockSizeUnit]" : blockSizeUnit);
        }
        if (StringUtils.isNotEmpty(record)) {
            saveSql.append("\n").append(record);
        }
        if (Boolean.parseBoolean(forceLogging)) {
            saveSql.append("\nFORCE LOGGING");
        }
        if (Boolean.parseBoolean(encryption)) {
            saveSql.append("\nENCRYPTION");
            if (StringUtils.isNotEmpty(algorithm)) {
                saveSql.append(" USING '").append(algorithm).append("'");
            }
        }
        if (StringUtils.isNotEmpty(compress)) {
            saveSql.append("\nDEFAULT ").append(compress);
        }
        if ("LOCAL".equals(areaBox.getValue())) {
            if (Boolean.parseBoolean(offline)) {
                saveSql.append("\nOFFLINE");
            }
            saveSql.append("\nEXTENT MANAGEMENT LOCAL ");
            if ("UNIFORM".equals(localAreaBox.getValue())) {
                String uniformSize = uniformSizeInput.getValue();
                String uniformUnit = uniformUnitBox.getValue();
                saveSql.append("UNIFORM SIZE ");
                saveSql.append(StringUtils.isEmpty(uniformSize) ? "[uniformSize]" : uniformSize);
                saveSql.append(" ").append(StringUtils.isEmpty(uniformUnit) ? "[uniformUnit]" : uniformUnit);
            } else {
                saveSql.append("AUTOALLOCATE");
            }

            if (manualInput.isChecked()) {
                saveSql.append("\nSEGMENT SPACE MANAGEMENT MANUAL");
            }
        } else if ("DICTIONARY".equals(areaBox.getValue())) {
            if (initSizeInput != null) {
                StringBuilder builder = new StringBuilder();
                if (!saveSql.toString().contains("DEFAULT")) {
                    saveSql.append("\nDEFAULT ");
                }
                String initSize = initSizeInput.getValue();
                if (StringUtils.isNotEmpty(initSize)) {
                    String initUnit = initUnitBox.getValue();
                    builder.append("\n  INITIAL ").append(StringUtils.isEmpty(initSize) ? "[initSize]" : initSize);
                    builder.append(" ").append(StringUtils.isEmpty(initUnit) ? "[initUnit]" : initUnit);
                }
                String next = nextStepInput.getValue();
                if (StringUtils.isNotEmpty(next)) {
                    String nextUnit = nextStepUnitBox.getValue();
                    builder.append("\n  NEXT ").append(StringUtils.isEmpty(next) ? "[next]" : next);
                    builder.append(" ").append(StringUtils.isEmpty(nextUnit) ? "[nextUnit]" : nextUnit);
                }
                String minExtents = minExtentsInput.getValue();
                if (StringUtils.isNotEmpty(minExtents)) {
                    builder.append("\n  MINEXTENTS ").append(minExtents);
                }
                String maxExtents = maxExtentsInput.getValue();
                boolean checked = maxExtentsCheck.isChecked();
                if (StringUtils.isNotEmpty(maxExtents) || checked) {
                    builder.append("\n  MAXEXTENTS").append(checked ? " UNLIMITED" : maxExtents);
                }
                String maxExtentsSize = maxExtentsSizeInput.getValue();
                String maxExtentsSizeUnit = maxExtentsSizeUnitBox.getValue();
                checked = maxExtentsSizeCheck.isChecked();
                if (StringUtils.isNotEmpty(maxExtentsSize) || checked) {
                    if (checked) {
                        builder.append("\n  MAXSIZE ").append("UNLIMITED");
                    } else {
                        builder.append("\n  MAXSIZE ").append(StringUtils.isEmpty(maxExtentsSize) ? "[maxSize]" : maxExtentsSize);
                        builder.append(" ").append(StringUtils.isEmpty(maxExtentsSizeUnit) ? "[maxSizeUnit]" : maxExtentsSizeUnit);
                    }
                }
                String increase = increaseInput.getValue();
                if (StringUtils.isNotEmpty(increase)) {
                    builder.append("\n  PCTINCREASE ").append(increase);
                }
                boolean encrypt = encryptInput.isChecked();
                if (encrypt) {
                    builder.append("\n  ENCRYPT ");
                }
                if (!builder.toString().isEmpty()) {
                    saveSql.append("\nSTORAGE ( ").append(builder).append("\n)");
                }
                if (Boolean.parseBoolean(offline)) {
                    saveSql.append("\nOFFLINE");
                }
            }
            saveSql.append("\nEXTENT MANAGEMENT DICTIONARY ");
        }
        if (StringUtils.isNotEmpty(flashBack)) {
            saveSql.append("\nFLASHBACK ").append(flashBack);
        }
        boolean guarantee = guaranteeInput.isChecked();
        if (guarantee) {
            saveSql.append("\nRETENTION GUARANTEE ");
        }
        saveSql.append(";");
        if (StringUtils.isNotEmpty(resize)) {
            saveSql.append("\nALTER TABLESPACE ").append(spaceName).append(" RESIZE ");
            saveSql.append(resize).append(StringUtils.isEmpty(resizeUnit) ? " [resizeUnit]" : " " + resizeUnit);
        }
        return saveSql.toString();
    }

    private void initTree(HSplitPanel splitPanel, HPanel generalPanel) {
        HTreeNode treeNode = new HTreeNode();
        HTreeNode generalNode = new HTreeNode();
        generalNode.setName(getLang("general"));
        generalNode.setType("general");
        generalNode.setOpenIcon(TableSpaceUtil.getIcon("general"));
        treeNode.add(generalNode);
        HTreeNode storageNode = new HTreeNode();
        storageNode.setName(getLang("storage"));
        storageNode.setType("storage");
        storageNode.setOpenIcon(TableSpaceUtil.getIcon("storage"));
        treeNode.add(storageNode);
        HTreeNode seniorNode = new HTreeNode();
        seniorNode.setName(getLang("senior"));
        seniorNode.setType("senior");
        seniorNode.setOpenIcon(TableSpaceUtil.getIcon("general"));
        treeNode.add(seniorNode);
        HTreeNode previewNode = new HTreeNode();
        previewNode.setName(getLang("preview"));
        previewNode.setType("preview");
        previewNode.setOpenIcon(TableSpaceUtil.getIcon("sql_view"));
        treeNode.add(previewNode);
        storagePanel = getStoragePanel();
        seniorPanel = getSeniorPanel();
        tree = new HTree(treeNode) {
            @Override
            protected void leftClickTreeNode(HTreeNode treeNode) {
                repaint(splitPanel, treeNode, generalPanel);
            }
        };
        tree.setRootVisible(false);
    }

    private void repaint(HSplitPanel panel, HTreeNode treeNode, HPanel generalPanel) {
        switch (treeNode.getType()) {
            case "general":
                panel.setPanelTwo(generalPanel);
                break;
            case "storage":
                panel.setPanelTwo(storagePanel);
                break;
            case "senior":
                panel.setPanelTwo(seniorPanel);
                break;
            case "preview":
                panel.setPanelTwo(getPreViewPanel());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + treeNode.getType());
        }
    }

    public HPanel getPanel() {
        return panel;
    }

    private HPanel getGeneralPanel() {
        spaceNameInput = new TextInput();
        typeSelectBox = new SelectBox() {
            @Override
            protected void onItemChange(ItemEvent e) {
                storagePanel = getStoragePanel();
                seniorPanel = getSeniorPanel();
            }
        };
        List<String> typeLists = Arrays.asList("PERMANENT", "TEMPORARY", "UNDO");
        typeLists.forEach(str -> typeSelectBox.addOption(str, str));
        table = new HTable();
        table.hideSeqCol();
        table.setNullSymbol("");
        ListCol unitCol = new ListCol("unit", getLang("unit"), unitLists);
        DataCol attrCol = new DataCol("attr", getLang("attribute"));
        attrCol.setShow(false);
        BoolCol online = new BoolCol("online", getLang("online"));
        table.addCols(new DataCol("name", getLang("name")),
                new DataCol("size", getLang("size")));
        table.addCols(unitCol, attrCol, online, new BoolCol("reuse", getLang("reuse")));
        table.load(new ArrayList<>(), 1);
        pathInput = new TextInput(PATH);
        pathInput.getComp().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateAttr(PATH, pathInput.getValue());
            }
        });
        nextSizeInput = new TextInput(NEXT_SIZE);
        nextSizeInput.getComp().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateAttr(NEXT_SIZE, nextSizeInput.getValue());
            }
        });
        nextSizeUnitBox = new SelectBox(NEXT_SIZE_UNIT) {
            @Override
            protected void onItemChange(ItemEvent e) {
                updateAttr(NEXT_SIZE_UNIT, this.getValue());
            }
        };
        unitLists.forEach(str -> nextSizeUnitBox.addOption(str, str));
        unlimitedInput = new CheckBoxInput(UNLIMITED);
        unlimitedInput.getComp().addItemListener(e -> {
            boolean checked = unlimitedInput.isChecked();
            maxSizeInput.setValue("");
            maxSizeInput.setEnabled(!checked);
            maxSizeUnitBox.setValue("");
            maxSizeUnitBox.setEnabled(!checked);
            updateAttr(UNLIMITED, String.valueOf(checked));
        });
        maxSizeInput = new TextInput(MAX_SIZE);
        maxSizeInput.getComp().addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateAttr(MAX_SIZE, maxSizeInput.getValue());
            }
        });
        maxSizeUnitBox = new SelectBox(MAX_SIZE_UNIT) {
            @Override
            protected void onItemChange(ItemEvent e) {
                updateAttr(MAX_SIZE_UNIT, this.getValue());
            }
        };
        unitLists.forEach(str -> maxSizeUnitBox.addOption(str, str));
        autoExtendBox = new SelectBox(AUTO_EXTEND) {
            @Override
            protected void onItemChange(ItemEvent e) {
                boolean b = !"OFF".equals(this.getValue());
                nextSizeInput.setValue("");
                nextSizeInput.setEnabled(b);
                nextSizeUnitBox.setValue("");
                nextSizeUnitBox.setEnabled(b);
                unlimitedInput.setValue("");
                unlimitedInput.setEnabled(b);
                maxSizeInput.setValue("");
                maxSizeInput.setEnabled(b);
                maxSizeUnitBox.setValue("");
                maxSizeUnitBox.setEnabled(b);
                updateAttr(AUTO_EXTEND, this.getValue());
            }
        };
        onOffLists.forEach(str -> autoExtendBox.addOption(str, str));
        table.getComp().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                List<HTabRowBean> list = table.getSelectedRowBeans();
                if (list.size() > 0) {
                    JsonObject attr = Json.parse(list.get(0).getCurrRow().get("attr")).asObject();
                    pathInput.setValue(attr.getString(PATH));
                    autoExtendBox.setValue(StringUtils.isEmpty(attr.getString(AUTO_EXTEND)) ? "" : attr.getString(AUTO_EXTEND));
                    nextSizeInput.setValue(attr.getString(NEXT_SIZE));
                    nextSizeUnitBox.setValue(StringUtils.isEmpty(attr.getString(NEXT_SIZE_UNIT)) ? "" : attr.getString(NEXT_SIZE_UNIT));
                    if (attr.getString(UNLIMITED) != null) {
                        unlimitedInput.getComp().setSelected(Boolean.parseBoolean(attr.getString(UNLIMITED)));
                    } else {
                        unlimitedInput.getComp().setSelected(false);
                    }
                    maxSizeInput.setValue(attr.getString(MAX_SIZE));
                    maxSizeUnitBox.setValue(StringUtils.isEmpty(attr.getString(MAX_SIZE_UNIT)) ? "" : attr.getString(MAX_SIZE_UNIT));
                }
            }
        });

        HPanel headPanel = new HPanel();
        headPanel.add(getGridPanelOracle(getLang("tableSpaceName"), spaceNameInput));
        headPanel.add(getGridPanelOracle(getLang("type"), typeSelectBox));
        HBarLayout barLayout = new HBarLayout();
        barLayout.setAlign(AlignEnum.LEFT);
        HBarPanel barPanel = new HBarPanel(barLayout);
        HButton addBtn = new HButton(getLang("add")) {
            @Override
            protected void onClick() {
                addClick();
            }
        };
        addBtn.setIcon(TableSpaceUtil.getIcon("add"));
        HButton delBtn = new HButton(getLang("delete")) {
            @Override
            protected void onClick() {
                List<HTabRowBean> rows = table.getSelectedRowBeans();
                if (rows.size() > 0) {
                    int result = JOptionPane.showConfirmDialog(null, getLang("sureDelete"),
                            ConstraintComp.getLang("hint"), JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        table.deleteSelectRow();
                    }
                }
            }
        };
        delBtn.setIcon(TableSpaceUtil.getIcon("delete"));
        barPanel.add(addBtn, delBtn);
        headPanel.add(barPanel);

        HPanel footPanel = new HPanel();
        footPanel.add(getGridPanelOracle(getLang("path"), pathInput));
        footPanel.add(getGridPanelOracle(getLang("autoExtend"), autoExtendBox));
        footPanel.add(getGridPanelOracle(getLang("nextSize"), nextSizeInput, nextSizeUnitBox));
        footPanel.add(getGridPanelOracle(getLang("unlimited"), unlimitedInput));
        footPanel.add(getGridPanelOracle(getLang("maxSize"), maxSizeInput, maxSizeUnitBox));

        LastPanel lastPanel = new LastPanel();
        lastPanel.setHead(headPanel.getComp());
        lastPanel.setWithScroll(table.getComp());
        lastPanel.setFoot(footPanel.getComp());
        HPanel panel = new HPanel();
        panel.setLastPanel(lastPanel);
        addClick();
        return panel;
    }

    private void updateAttr(String id, String value) {
        List<HTabRowBean> rowBeans = table.getSelectedRowBeans();
        for (HTabRowBean rowBean : rowBeans) {
            Map<String, String> map = rowBean.getCurrRow();
            JsonObject attrJson = Json.parse(map.get("attr")).asObject();
            attrJson.set(id, value);
            map.put("attr", attrJson.toString());
        }
    }

    private void addClick() {
        int rowCount = table.getRowCount();
        if (rowCount > 0) {
            HTabRowBean rowBean = table.getRowBean(rowCount - 1);
            Map<String, String> map = rowBean.getCurrRow();
            JsonObject attr = new JsonObject();
            attr.add(PATH, pathInput.getValue());
            attr.add(AUTO_EXTEND, autoExtendBox.getValue());
            attr.add(NEXT_SIZE, nextSizeInput.getValue());
            attr.add(NEXT_SIZE_UNIT, nextSizeUnitBox.getValue());
            attr.add(UNLIMITED, String.valueOf(unlimitedInput.isChecked()));
            attr.add(MAX_SIZE, maxSizeInput.getValue());
            attr.add(MAX_SIZE_UNIT, maxSizeUnitBox.getValue());
            pathInput.setValue("");
            autoExtendBox.setValue("");
            nextSizeInput.setValue("");
            nextSizeUnitBox.setValue("");
            unlimitedInput.setValue("");
            maxSizeInput.setValue("");
            maxSizeUnitBox.setValue("");
            map.put("attr", attr.toString());
        }
        table.add(getTableData().get(0));
    }

    private HPanel getStoragePanel() {
        blockSizeInput = new TextInput("blockSize");
        String BLOCK_SIZE_UNIT = "blockSizeUnit";
        blockSizeUnitBox = new SelectBox(BLOCK_SIZE_UNIT);
        unitLists.forEach(str -> blockSizeUnitBox.addOption(str, str));
        resizeInput = new TextInput("resize");
        resizeUnitBox = new SelectBox("resizeUnit");
        unitLists.forEach(str -> resizeUnitBox.addOption(str, str));
        HGridPanel sizeGrid = getGridPanelOracle("大小：", resizeInput, resizeUnitBox);
        String COMPRESS = "compress";
        compressBox = new SelectBox(COMPRESS);
        List<String> compressLists = Arrays.asList("", "COMPRESS", "NOCOMPRESS", "COMPRESS FOR OLTP",
                "COMPRESS FOR QUERY LOW", "COMPRESS FOR QUERY HIGH", "COMPRESS FOR ARCHIVE LOW", "COMPRESS FOR ARCHIVE HIGH");
        compressLists.forEach(str -> compressBox.addOption(str, str));
        LastPanel lastPanel = new LastPanel();
        areaBox = new SelectBox("area") {
            @Override
            protected void onItemChange(ItemEvent e) {
                if ("LOCAL".equals(this.getValue())) {
                    lastPanel.set(getLocalPanel().getComp());
                } else if ("DICTIONARY".equals(this.getValue())) {
                    if ("PERMANENT".equals(typeSelectBox.getValue())) {
                        lastPanel.set(getDirectoryPanel().getComp());
                    } else {
                        lastPanel.set(new LabelInput().getComp());
                    }
                } else {
                    lastPanel.set(new LabelInput().getComp());
                }
            }
        };
        List<String> areaLists = Arrays.asList("", "DICTIONARY", "LOCAL");
        areaLists.forEach(str -> areaBox.addOption(str, str));
        fileTypeBox = new SelectBox("fileType") {
            @Override
            protected void onItemChange(ItemEvent e) {
                sizeGrid.getComp().setVisible("BIGFILE".equals(this.getValue()));
            }
        };
        List<String> fileTypes = Arrays.asList("", "BIGFILE", "SMALLFILE");
        fileTypes.forEach(str -> fileTypeBox.addOption(str, str));
        HPanel panel = new HPanel();
        panel.add(getGridPanelOracle(getLang("fileType"), fileTypeBox));
        if ("PERMANENT".equals(typeSelectBox.getValue())) {
            panel.add(getGridPanelOracle(getLang("blockSize"), blockSizeInput, blockSizeUnitBox));
            panel.add(getGridPanelOracle(getLang("compress"), compressBox));
        }
        panel.add(getGridPanelOracle(getLang("district"), areaBox));
        panel.add(sizeGrid);
        panel.setLastPanel(lastPanel);
        return panel;
    }

    private HPanel getLocalPanel() {
        manualInput = new CheckBoxInput("manual");
        uniformSizeInput = new TextInput("uniformSize");
        uniformUnitBox = new SelectBox("uniformUnit");
        unitLists.forEach(str -> uniformUnitBox.addOption(str, str));
        HGridPanel uniformGrid = getGridPanelOracle(getLang("uniformSize"), uniformSizeInput, uniformUnitBox);
        localAreaBox = new SelectBox("localArea") {
            @Override
            protected void onItemChange(ItemEvent e) {
                uniformSizeInput.setValue("");
                uniformUnitBox.setValue("");
                uniformGrid.getComp().setVisible("UNIFORM".equals(this.getValue()));
            }
        };
        List<String> localAreaLists = Arrays.asList("", "AUTOALLOCATE", "UNIFORM");
        localAreaLists.forEach(str -> localAreaBox.addOption(str, str));
        HPanel panel = new HPanel();
        panel.setTitle("LOCAL");
        if ("PERMANENT".equals(typeSelectBox.getValue())) {
            panel.add(getGridPanelOracle(getLang("manual"), manualInput));
        }
        panel.add(getGridPanelOracle(getLang("localArea"), localAreaBox));
        panel.add(uniformGrid);
        return panel;
    }

    private HPanel getDirectoryPanel() {
        initSizeInput = new TextInput("initSize", "");
        initUnitBox = new SelectBox("initUnit");
        unitLists.forEach(str -> initUnitBox.addOption(str, str));
        nextStepInput = new TextInput("nextStep");
        nextStepUnitBox = new SelectBox("nextUnit");
        unitLists.forEach(str -> nextStepUnitBox.addOption(str, str));
        minExtentsInput = new TextInput("minExtents");
        maxExtentsInput = new TextInput("maxExtents");
        maxExtentsCheck = new CheckBoxInput("maxExtentsCheck", getLang("unLimit"));
        maxExtentsCheck.getComp().addItemListener(e -> {
            boolean checked = maxExtentsCheck.isChecked();
            maxExtentsInput.setValue("");
            maxExtentsInput.setEnabled(!checked);
        });
        maxExtentsSizeInput = new TextInput("maxExtentsSize");
        maxExtentsSizeUnitBox = new SelectBox("maxExtentsSizeUnit");
        unitLists.forEach(str -> maxExtentsSizeUnitBox.addOption(str, str));
        maxExtentsSizeCheck = new CheckBoxInput("maxExtentsSizeCheck", getLang("unLimit"));
        maxExtentsSizeCheck.getComp().addItemListener(e -> {
            boolean checked = maxExtentsSizeCheck.isChecked();
            maxExtentsSizeInput.setValue("");
            maxExtentsSizeInput.setEnabled(!checked);
            maxExtentsSizeUnitBox.setValue("");
            maxExtentsSizeUnitBox.setEnabled(!checked);
        });

        increaseInput = new TextInput("increase");
        encryptInput = new CheckBoxInput("encrypt");
        HPanel panel = new HPanel();
        panel.setTitle("DICTIONARY");
        panel.add(getGridPanelOracle(getLang("initial"), initSizeInput, initUnitBox));
        panel.add(getGridPanelOracle(getLang("nextStep"), nextStepInput, nextStepUnitBox));
        panel.add(getGridPanelOracle(getLang("minExtents"), minExtentsInput));
        panel.add(getGridPanelOracle(getLang("maxExtents"), maxExtentsInput, maxExtentsCheck));
        panel.add(getGridPanelOracle(getLang("maxSize"), maxExtentsSizeInput, maxExtentsSizeUnitBox, maxExtentsSizeCheck));
        panel.add(getGridPanelOracle(getLang("increase"), increaseInput));
        panel.add(getGridPanelOracle(getLang("encrypt"), encryptInput));
        return panel;
    }

    private HPanel getSeniorPanel() {
        recordBox = new SelectBox("record");
        List<String> recordLists = Arrays.asList("", "LOGGING", "NOLOGGING");
        recordLists.forEach(str -> recordBox.addOption(str, str));
        focLogInput = new CheckBoxInput("forceLogging");
        offlineInput = new CheckBoxInput("offline");
        flashBackBox = new SelectBox("flashBack");
        onOffLists.forEach(str -> flashBackBox.addOption(str, str));
        algorithmBox = new SelectBox("algorithm");
        List<String> algorithmLists = Arrays.asList("", "3DES168", "AES128", "AES192", "AES256");
        algorithmLists.forEach(str -> algorithmBox.addOption(str, str));
        HGridPanel gridPanel = getGridPanelOracle(getLang("algorithm"), algorithmBox);
        gridPanel.getComp().setVisible(false);
        encryptionInput = new CheckBoxInput("encryption");
        encryptionInput.getComp().addItemListener(e -> gridPanel.getComp().setVisible(e.getStateChange() == ItemEvent.SELECTED));
        guaranteeInput = new CheckBoxInput("guarantee");
        HPanel panel = new HPanel();
        if ("PERMANENT".equals(typeSelectBox.getValue())) {
            panel.add(getGridPanelOracle(getLang("record"), recordBox));
            panel.add(getGridPanelOracle(getLang("forceLogging"), focLogInput));
            panel.add(getGridPanelOracle(getLang("offline"), offlineInput));
            panel.add(getGridPanelOracle(getLang("flashBack"), flashBackBox));
            panel.add(getGridPanelOracle(getLang("encryption"), encryptionInput));
            panel.add(gridPanel);
            return panel;
        } else if ("UNDO".equals(typeSelectBox.getValue())) {
            panel.add(getGridPanelOracle(getLang("guarantee"), guaranteeInput));
            return panel;
        }
        return panel;
    }

    private HPanel getPreViewPanel() {
        HPanel panel = new HPanel();
        HTextArea textArea = new HTextArea(false, false);
        LastPanel lastPanel = new LastPanel();
        textArea.setText(getSql());
        lastPanel.set(textArea.getComp());
        panel.setLastPanel(lastPanel);
        return panel;
    }

    private HGridPanel getGridPanelOracle(String label, AbsInput... inputs) {
        HGridLayout gridLayout;
        if (inputs.length == 3) {
            gridLayout = new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C6, GridSplitEnum.C2, GridSplitEnum.C2);
        } else if (inputs.length == 2) {
            gridLayout = new HGridLayout(GridSplitEnum.C2, GridSplitEnum.C8, GridSplitEnum.C2);
        } else {
            gridLayout = new HGridLayout(GridSplitEnum.C2);
        }
        HGridPanel gridPanel = new HGridPanel(gridLayout);
        LabelInput labelInput = new LabelInput(label);
        gridPanel.setComp(1, labelInput);
        gridPanel.setComp(2, inputs[0]);
        if (inputs.length == 3) {
            gridPanel.setComp(3, inputs[1]);
            gridPanel.setComp(4, inputs[2]);
        } else if (inputs.length == 2) {
            gridPanel.setComp(3, inputs[1]);
        }
        return gridPanel;
    }

    private List<Map<String, String>> getTableData() {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(atomicId.incrementAndGet()));
        map.put("online", "true");
        map.put("attr", "{}");
        data.add(map);
        return data;
    }

}
