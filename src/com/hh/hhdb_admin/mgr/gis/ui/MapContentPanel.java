package com.hh.hhdb_admin.mgr.gis.ui;

import com.hh.frame.common.base.JdbcBean;
import com.hh.frame.swingui.view.container.LastPanel;
import com.hh.hhdb_admin.common.util.logUtil;
import com.hh.hhdb_admin.mgr.gis.GisComp;
import com.hh.hhdb_admin.mgr.gis.GisMgr;
import com.hh.hhdb_admin.mgr.gis.util.GisUtil;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.*;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.coverage.grid.io.GridFormatFinder;
import org.geotools.data.*;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.Hints;
import org.geotools.filter.text.generated.parsers.ParseException;
import org.geotools.gce.geotiff.GeoTiffFormat;
import org.geotools.map.*;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Stroke;
import org.geotools.styling.*;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.JMapPane;
import org.geotools.swing.MapLayerTable;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.style.ContrastMethod;

import javax.swing.*;
import java.awt.Point;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * 地图内容面板
 */
public class MapContentPanel extends LastPanel {
    private static String logName = MapContentPanel.class.getSimpleName();
    private MapContentPanel mapcontent;
    /*
     * 我们将用于创建样式和过滤对象的工厂
     */
    private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();

    /*
     * 一些默认样式变量
     */
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 10.0f;

    private JdbcBean jdbcBean;

    //地图显示窗口
    private JMapFrame mapFrame;
    //地图面板
    private JMapPane mapPane;
    //图层列表面板
    private MapLayerTable mapLayerTable;
    //地图内容
    private MapContent map;
    //点击位置坐标对象
    private Point panePos;

    //图层风格
    private Style style;
    //图层读取连接集合
    private List<DataStore> datalist;

    private GridCoverage2DReader reader;

    //几何体属性名称
    private String geometryAttributeName;
    //几何的类型
    private GeomType geometryType;


    //几何的类型枚举
    private enum GeomType {
        POINT, LINE, POLYGON
    }

    //图层默认颜色
    private static Color[] DEFAULT_COLOR = new Color[]{
            Color.ORANGE
            , Color.PINK
            , Color.RED
            , Color.MAGENTA
            , new Color(199, 21, 133)
            , new Color(148, 0, 211)
            , new Color(221, 160, 221)
            , new Color(25, 25, 112)
            , new Color(255, 105, 180)
            , new Color(238, 130, 238)
            , new Color(138, 43, 226)
    };
    private int i = 0;
    private GisComp gismap;
    private Map<String, Color> fillcolormap;

    static {
        System.setProperty("com.sun.media.jai.disableMediaLib", "true");
    }


    public MapContentPanel(JdbcBean jdbcBean, GisComp gismap) throws IOException {
        super(false);
        mapcontent = this;
        this.gismap = gismap;
        this.jdbcBean = jdbcBean;

        init(new ArrayList<>());
    }

    private void init(List<Layer> list) {
        datalist = new ArrayList<DataStore>();
        map = new MapContent();
        mapFrame = new JMapFrame(map);
        mapFrame.enableToolBar(true);
        mapFrame.enableStatusBar(true);
        mapPane = mapFrame.getMapPane();
        fillcolormap = new HashMap<String, Color>();
        mapLayerTable = new MapLayerTable(mapPane);
        map = mapPane.getMapContent();
        if (!list.isEmpty() && list != null) {
            for (Layer layer : list) {
                map.addLayer(layer);
            }
        }

        JToolBar toolBar = mapFrame.getToolBar();
        toolBar.addSeparator();
        JButton rasterbtn = new JButton(GisMgr.getLang("GridShows"), GisMgr.getIcon("field"));
        toolBar.add(rasterbtn);
        rasterbtn.setToolTipText(GisMgr.getLang("resterDisplay"));
        toolBar.addSeparator();
        JButton refbtn = new JButton(GisMgr.getLang("Reload"), GisMgr.getIcon("reflash"));
        toolBar.add(refbtn);
        refbtn.setToolTipText(GisMgr.getLang("ReloadLayer"));
        toolBar.addSeparator();
        //sql查询
        JButton btn = new JButton(GisMgr.getLang("query"), GisMgr.getIcon("formatsql"));
        toolBar.add(btn);
        btn.setToolTipText(GisMgr.getLang("CompileSQL"));

        mapPane.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent ev) {
                panePos = ev.getPoint();
            }
        });
        //鼠标拖动地图监听
        mapPane.addMouseMotionListener(new MouseMotionAdapter() {
            //移动监听
            @Override
            public void mouseDragged(MouseEvent e) {
                Point pos = e.getPoint();
                if (!pos.equals(panePos)) {
                    mapPane.moveImage(pos.x - panePos.x, pos.y - panePos.y);
                    panePos = pos;
                }
            }
        });

        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SqlFrame(mapcontent,jdbcBean);
            }
        });
        refbtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reload(map.layers());
            }
        });
        rasterbtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == 0) {
                    RasterDisplay pm = new RasterDisplay(mapcontent, map.layers());
                    pm.showPopup(e);
                }
            }
        });

        setHead(toolBar);
        set(mapPane);
        setFoot(new JLabel(GisMgr.getLang("ReloadHint")));
    }

    /**
     * 图层列表面板
     */
    public MapLayerTable addMapLayerTable() {
        return mapLayerTable;
    }

    /**
     * 重新加载图层,当图层加载不出来时，调用此方法
     *
     * @param list 所有图层
     */
    public void reload(List<Layer> list) {
        removeAll();
        init(list);
        gismap.reload(mapLayerTable);
        updateUI();
    }

    /**
     * 添加图层
     *
     * @param name
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
	public synchronized void addLayer(String name) throws Exception {
        File file = new File(name);
        Color color = DEFAULT_COLOR[i];
        i++;
        if (i >= DEFAULT_COLOR.length) i = 0;
        style = SLD.createPolygonStyle(color, null, 0.0f);

        if (file.isFile() && file.toString().endsWith(".tif")) {  //tif文件
            //经测试，tif文件添加图层只能添加到最底层
            List<Layer> list = map.layers();
            AbstractGridFormat format = GridFormatFinder.findFormat(file);
            Hints tiffHints = new Hints();
            if (format instanceof GeoTiffFormat) {
                tiffHints.add(new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
                tiffHints.add(new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, DefaultGeographicCRS.WGS84));
            }
            reader = format.getReader(file, tiffHints);
            Style rasterStyle = createRGBStyle();
            Layer rasterLayer = new GridReaderLayer(reader, rasterStyle);
            map.addLayer(rasterLayer);
            map.addLayers(list);
            reader.dispose();
            //添加tif文件时需要重新定义mapcontent且重新添加图层
            mapFrame.setMapContent(map);
            mapLayerTable = new MapLayerTable(mapPane);
            gismap.reload(mapLayerTable);
        } else {  //数据库表或者.shp文件
            //这个方法就是将添加的图层储存在缓存中，这样操作起来就会更加迅速（放大，缩小，移动）
            CachingFeatureSource cachshapefileSource;
            DataStore dataStore;
            if (file.isFile() && file.toString().endsWith(".shp")) {
                dataStore = FileDataStoreFinder.getDataStore(file);
                SimpleFeatureSource shapefileSource = ((FileDataStore) dataStore).getFeatureSource();
                cachshapefileSource = new CachingFeatureSource(shapefileSource);
            } else {
                int x = name.indexOf(".");
                dataStore = DataStoreFinder.getDataStore(GisUtil.getLogInfo(jdbcBean, name.substring(0, x)));
                SimpleFeatureSource featureSource = dataStore.getFeatureSource(name.substring(x + 1));
                cachshapefileSource = new CachingFeatureSource(featureSource);
                //检索有关要素几何的信息
                GeometryDescriptor geomDesc = featureSource.getSchema().getGeometryDescriptor();
                geometryAttributeName = geomDesc.getLocalName();
                Class<?> clazz = geomDesc.getType().getBinding();

                if (Polygon.class.isAssignableFrom(clazz) || MultiPolygon.class.isAssignableFrom(clazz)) {
                    geometryType = GeomType.POLYGON;
                } else if (LineString.class.isAssignableFrom(clazz) || MultiLineString.class.isAssignableFrom(clazz)) {
                    geometryType = GeomType.LINE;
                } else {
                    geometryType = GeomType.POINT;
                }
            }
            Layer layerse = new FeatureLayer(cachshapefileSource, style);
            map.addLayer(layerse);
            datalist.add(dataStore);
            //因为添加图层时从数据库读取数据较慢，所以不能立刻关闭数据存储连接
            //此操作造成的后续影响就是数据库连接会越来越多
            //目前的解决办法是当用户关闭插件时或者重载图层时会关闭掉此时所有的数据存储连接
        }
        fillcolormap.put(name, color);
    }

    /**
     * 获取已添加图层
     *
     * @return
     */
    public List<String> getlayerlist() {
        List<String> list = new ArrayList<String>();
        for (Layer layer : map.layers()) {
            String str = layer.getFeatureSource().getSchema().getName().toString();
            list.add(str);
        }
        return list;
    }
    
    public void closedatastore() {
        for (DataStore ds : datalist) {
            ds.dispose();
        }
        map.dispose();
    }
    
    /**
     * 栅格图片颜色样式设置
     */
    public void changGrayStyle(int i) {
        Style style = i == 0 ? createRGBStyle() : createGreyscaleStyle(i);
        if (style != null) {
            for (Layer layer : map.layers()) {
                if (layer instanceof RasterLayer) {
                    ((StyleLayer) layer).setStyle(style);
                }
            }
            mapFrame.repaint();
        }
    }

    /**
     * 设置灰度风格
     * @param band
     * @return
     */
    private Style createGreyscaleStyle(int band) {
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        SelectedChannelType sct = sf.createSelectedChannelType(String.valueOf(band), ce);
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct);
        sym.setChannelSelection(sel);
        return SLD.wrapSymbolizers(sym);
    }
    
    /**
     * 设置RGB风格
     * @return
     */
    private Style createRGBStyle() {
        GridCoverage2D cov = null;
        try {
            cov = reader.read(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // We need at least three bands to create an RGB style
        int numBands = cov.getNumSampleDimensions();
        if (numBands < 3) {
            return null;
        }
        // Get the names of the bands
        String[] sampleDimensionNames = new String[numBands];
        for (int i = 0; i < numBands; i++) {
            GridSampleDimension dim = cov.getSampleDimension(i);
            sampleDimensionNames[i] = dim.getDescription().toString();
        }
        final int RED = 0, GREEN = 1, BLUE = 2;
        int[] channelNum = {-1, -1, -1};
        // We examine the band names looking for "red...", "green...", "blue...".
        // Note that the channel numbers we record are indexed from 1, not 0.
        for (int i = 0; i < numBands; i++) {
            String name = sampleDimensionNames[i].toLowerCase();
            if (name != null) {
                if (name.matches("red.*")) {
                    channelNum[RED] = i + 1;
                } else if (name.matches("green.*")) {
                    channelNum[GREEN] = i + 1;
                } else if (name.matches("blue.*")) {
                    channelNum[BLUE] = i + 1;
                }
            }
        }
        // If we didn't find named bands "red...", "green...", "blue..."
        // we fall back to using the first three bands in order
        if (channelNum[RED] < 0 || channelNum[GREEN] < 0 || channelNum[BLUE] < 0) {
            channelNum[RED] = 1;
            channelNum[GREEN] = 2;
            channelNum[BLUE] = 3;
        }
        // Now we create a RasterSymbolizer using the selected channels
        SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.NORMALIZE);
        for (int i = 0; i < 3; i++) {
            sct[i] = sf.createSelectedChannelType(String.valueOf(channelNum[i]), ce);
        }
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        ChannelSelection sel = sf.channelSelection(sct[RED], sct[GREEN], sct[BLUE]);
        sym.setChannelSelection(sel);

        return SLD.wrapSymbolizers(sym);
    }

    /**
     * 显示sql查询的内容
     *
     * @throws ParseException
     * @throws IOException
     */
    public void selectFeatures(List<Geometry> geometrys, String tableName, Color backcolor) throws ParseException, IOException {
        for (Layer layer : map.layers()) {      //判断打开的图层中是否包含查询的图层
            if (!tableName.contains(layer.getFeatureSource().getSchema().getName().toString())) return;
        }
        
        tableName = tableName.replace("\"", "");    //看看是否有模式名
        int i = tableName.indexOf(".");
        DataStore dataStore = DataStoreFinder.getDataStore(GisUtil.getLogInfo(jdbcBean, i > 0 ? tableName.substring(0, i).trim() : "public"));
        SimpleFeatureSource featureSource = dataStore.getFeatureSource(i > 0 ? tableName.substring(i + 1) : tableName.trim());
        
        try {
            Set<FeatureId> IDs = new HashSet<>();
            for (Geometry geometry : geometrys) {
                //过滤器从数据源获取数据
                Filter filter = ff.intersects(ff.property(geometryAttributeName),ff.literal(geometry));
                try {
                    SimpleFeatureCollection selectedFeatures = featureSource.getFeatures(filter);
                    try (SimpleFeatureIterator iter = selectedFeatures.features()) {
                        while (iter.hasNext()) {
                            SimpleFeature feature = iter.next();
                            IDs.add(feature.getIdentifier());
                        }
                    }
            
                    if (IDs.isEmpty()) {
                        JOptionPane.showMessageDialog(null, GisMgr.getLang("notFound"), GisMgr.getLang("error"), JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logUtil.error(logName, ex);
                }
            }
            //设置筛选内容的样式
            if (!IDs.isEmpty()) {
                Style style = createSelectedStyle(IDs, backcolor, tableName);
                for (Layer layer : map.layers()) {
                    if (tableName.contains(layer.getFeatureSource().getSchema().getName().toString())) {
                        ((FeatureLayer) layer).setStyle(style);
                    }
                }
                mapFrame.repaint();
            }
        }finally {
            dataStore.dispose();
        }
    }

    /**
     * 根据设置创建样式
     */
    private Style createSelectedStyle(Set<FeatureId> IDs, Color color, String tableName) {
        int i = tableName.indexOf(".");
        Color tablecolor = fillcolormap.get(i > 0 ? tableName : "public." + tableName);
        Rule otherRule = createRule(tablecolor, null);
        
        otherRule.setElseFilter(true);
        Rule selectedRule = createRule(tablecolor, color);
        selectedRule.setFilter(ff.id(IDs));

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();

        fts.rules().add(otherRule);
        fts.rules().add(selectedRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * helper for createXXXStyle方法。创建包含的新规则 一个符合几何类型的特征的符号化器 我们正在显示。
     */
    private Rule createRule(Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

        switch (geometryType) {
            case POLYGON:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;
            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
                break;
            case POINT:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));

                Mark mark = sf.getCircleMark();
                mark.setFill(fill);
                mark.setStroke(stroke);

                Graphic graphic = sf.createDefaultGraphic();
                graphic.graphicalSymbols().clear();
                graphic.graphicalSymbols().add(mark);
                graphic.setSize(ff.literal(POINT_SIZE));

                symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
        }

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }
}