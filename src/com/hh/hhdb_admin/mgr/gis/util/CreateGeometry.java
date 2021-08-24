package com.hh.hhdb_admin.mgr.gis.util;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 * 创建几何对象
 */
public class CreateGeometry {
    public Point createPoint() {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Coordinate coord = new Coordinate(109.013388, 32.715519);
        return geometryFactory.createPoint(coord);
    }

    /**
     * @param wktstr="POINT (109.013388 32.715519)"
     * @return
     * @throws ParseException
     */
    public static Point createPointByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (Point) reader.read(wktstr);
    }

    /**
     * @param wktstr="MULTIPOINT(109.013388 32.715519,119.32488 31.435678)"
     * @return
     * @throws ParseException
     */
    public static MultiPoint createMulPointByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (MultiPoint) reader.read(wktstr);
    }

    public static LineString createLine() {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Coordinate[] coords = new Coordinate[]{new Coordinate(2, 2), new Coordinate(2, 2)};
        return geometryFactory.createLineString(coords);
    }

    /**
     * @param wktstr="LINESTRING(0 0, 2 0)"
     * @return
     * @throws ParseException
     */
    public static LineString createLineByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (LineString) reader.read(wktstr);
    }

    public MultiLineString createMLine() {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        Coordinate[] coords1 = new Coordinate[]{new Coordinate(2, 2), new Coordinate(2, 2)};
        LineString line1 = geometryFactory.createLineString(coords1);
        Coordinate[] coords2 = new Coordinate[]{new Coordinate(2, 2), new Coordinate(2, 2)};
        LineString line2 = geometryFactory.createLineString(coords2);
        LineString[] lineStrings = new LineString[2];
        lineStrings[0] = line1;
        lineStrings[1] = line2;
        return geometryFactory.createMultiLineString(lineStrings);
    }

    /**
     * @param wktstr
     * @return
     * @throws ParseException
     */
    public static MultiLineString createMLineByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (MultiLineString) reader.read(wktstr);
    }

    /**
     * @param wktstr="POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))"
     * @return
     * @throws ParseException
     */
    public static Polygon createPolygonByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (Polygon) reader.read(wktstr);
    }

    /**
     * @param wktstr="MULTILINESTRING((0 0, 2 0))"
     * @return
     * @throws ParseException
     */
    public static MultiLineString createMultiLineByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (MultiLineString) reader.read(wktstr);
    }

    /**
     * @param wktstr = "MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))"
     * @return
     * @throws ParseException
     */
    public static MultiPolygon createMulPolygonByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (MultiPolygon) reader.read(wktstr);
    }

    /**
     * @param wktstr = "MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))"
     * @return
     * @throws ParseException
     */
    public static GeometryCollection createGeometryCollectionByWKT(String wktstr) throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        WKTReader reader = new WKTReader(geometryFactory);
        return (GeometryCollection) reader.read(wktstr);
    }

    /**
     * ����geo����
     *
     * @return
     * @throws ParseException
     */
    public GeometryCollection createGeoCollect() throws ParseException {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        LineString line = createLine();
        Polygon poly = createPolygonByWKT("");
        Geometry g1 = geometryFactory.createGeometry(line);
        Geometry g2 = geometryFactory.createGeometry(poly);
        Geometry[] garray = new Geometry[]{g1, g2};
        return geometryFactory.createGeometryCollection(garray);
    }

    /**
     * @param x
     * @param y
     * @param RADIUS
     * @return
     */
    public static Polygon createCircle(double x, double y, final double RADIUS) {
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
        final int SIDES = 32;//Բ����ĵ����    
        Coordinate[] coords = new Coordinate[SIDES + 1];
        for (int i = 0; i < SIDES; i++) {
            double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;
            double dx = Math.cos(angle) * RADIUS;
            double dy = Math.sin(angle) * RADIUS;
            coords[i] = new Coordinate((double) x + dx, (double) y + dy);
        }
        coords[SIDES] = coords[0];
        LinearRing ring = geometryFactory.createLinearRing(coords);
        return geometryFactory.createPolygon(ring, null);
    }
}
