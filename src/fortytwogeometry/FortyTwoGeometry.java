/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fortytwogeometry;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.net.URL;
import java.net.URI;
import java.util.Map;
import java.io.StringWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Random;

import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureImpl;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
//import org.geotools.data.DefaultFeatureResults;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.JTSFactoryFinder;

import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.GeoJSONUtil;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Envelope;

import java.io.IOException;
import org.opengis.referencing.FactoryException;

import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 *
 * @author richard
 */
public class FortyTwoGeometry {
  
  //EPSG:4978
  //GEOCCS["WGS 84",
  //  DATUM["World Geodetic System 1984",
  //      SPHEROID["WGS 84",6378137.0,298.257223563,
  //          AUTHORITY["EPSG","7030"]],
  //      AUTHORITY["EPSG","6326"]],
  //  PRIMEM["Greenwich",0.0,
  //      AUTHORITY["EPSG","8901"]],
  //  UNIT["m",1.0],
  //  AXIS["Geocentric X",OTHER],
  //  AXIS["Geocentric Y",EAST],
  //  AXIS["Geocentric Z",NORTH],
  //  AUTHORITY["EPSG","4978"]]

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    // TODO code application logic here
    //readShapefile("C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS-0.3.shp");
    //reprojectShapefile(
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS-0.3.shp",
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS-0.3_CARTESIAN.shp",
    //        DefaultGeographicCRS.WGS84.toWKT()
    //        );
    //reprojectShapefile(
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3.shp",
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3_CARTESIAN.shp",
    //        DefaultGeographicCRS.WGS84.toWKT()
    //        );
    //exportGeoJSON(
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3_CARTESIAN.shp",
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3_CARTESIAN.json",
    //        false
    //        );
    //exportGeoJSON(
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3.shp",
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3_WGS84.geojson",
    //        true
    //        );
    //testPointOrdering(
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3.shp"
    //        );
    
    //World Borders workflow for 3DS Max
    //reprojectShapefile(
    //        "C:\\richard\\data\\world-maps\\TM_WORLD_BORDERS-0.3\\TM_WORLD_BORDERS_SIMPL-0.3\\TM_WORLD_BORDERS_SIMPL-0.3.shp",
    //        "C:\\richard\\temp\\TM_WORLD_BORDERS_SIMPL-0.3_WGS84.shp",
    //        DefaultGeographicCRS.WGS84.toWKT()
    //        );
    //exportCollada(
    //        "C:\\richard\\temp\\TM_WORLD_BORDERS_SIMPL-0.3_WGS84.shp",
    //        "C:\\richard\\temp\\TM_WORLD_BORDERS_SIMPL-0.3_CARTESIAN.dae"
    //        );
    
    //buildings workflow: 1. cut shapefile, 2. reproject to WGS84, 3. fix polys and export (geojson) collada
    //cutShapefile(
    //        "C:\\richard\\data\\osfree\\vmdvec_tq\\OS VectorMap District (ESRI Shape File) TQ\\data\\TQ_Building.shp",
    //        "C:\\richard\\temp\\TQ_Building_530000_180000.shp",
    //        new Envelope(530000,533000,180000,183000)
    //        );
    //reprojectShapefile(
    //          "C:\\richard\\temp\\TQ_Building_530000_180000.shp",
    //          "C:\\richard\\temp\\TQ_Building_530000_180000_WGS84.shp",
    //          DefaultGeographicCRS.WGS84.toWKT()
    //        );
    //exportGeoJSON(
    //        "C:\\richard\\temp\\TQ_Building_530000_180000_WGS84.shp",
    //        "C:\\richard\\temp\\TQ_Building_530000_180000_WGS84.geojson",
    //        true
    //        );
    //THIS ONE! we're using collada as it works better for 3D formats
    //exportBuildingCollada(
    //        "C:\\richard\\temp\\TQ_Building_530000_180000_WGS84.shp",
    //        "C:\\Users\\richard\\Documents\\3dsmax\\import\\42\\TQ_Building_530000_180000_CARTESIAN.dae"
    //        );
    
    //Thames workflow: 1. cut TidalWater.shp, 2. reproject to WGS84, 3. fix polys and export collada
    //was 530000,533000,180000,183000, changed to 503500,562500,155500,201000
    //cutShapefile(
    //        "C:\\richard\\data\\osfree\\vmdvec_tq\\OS VectorMap District (ESRI Shape File) TQ\\data\\TQ_TidalWater.shp",
    //        "C:\\richard\\temp\\TQ_TidalWater_503500_155500.shp",
    //        new Envelope(503500,562500,155500,201000)
    //        );
    //reprojectShapefile(
    //          "C:\\richard\\temp\\TQ_TidalWater_503500_155500.shp",
    //          "C:\\richard\\temp\\TQ_TidalWater_503500_155500_WGS84.shp",
    //          DefaultGeographicCRS.WGS84.toWKT()
    //        );
    //exportCollada(
    //        "C:\\richard\\temp\\TQ_TidalWater_503500_155500_WGS84.shp",
    //        "C:\\Users\\richard\\Documents\\3dsmax\\import\\42\\TQ_TidalWater_503500_155500_CARTESIAN.dae"
    //        );
    
    //London Borough Outline Workflow
    //reprojectShapefile(
    //          "C:\\inetpub\\wwwroot\\webgl\\42\\shapefiles\\London_dt_2001_area.shp",
    //          "C:\\richard\\temp\\London_dt_2001_area_WGS84.shp",
    //          DefaultGeographicCRS.WGS84.toWKT()
    //        );
    //exportCollada(
    //        "C:\\richard\\temp\\London_dt_2001_area_WGS84.shp",
    //        "C:\\Users\\richard\\Documents\\3dsmax\\import\\42\\London_dt_2001_area_CARTESIAN.dae"
    //        );
    
    //Rail network
    //cutShapefile(
    //        "C:\\users\\richard\\desktop\\mapstomake\\osopendata\\railways\\railway_polyline.shp",
    //        "C:\\richard\\temp\\railway_polyline_503500_155500.shp",
    //        new Envelope(503500,562500,155500,201000)
    //        );
    //reprojectShapefile(
    //          "C:\\richard\\temp\\railway_polyline_503500_155500.shp",
    //          "C:\\richard\\temp\\railway_polyline_503500_155500_WGS84.shp",
    //          DefaultGeographicCRS.WGS84.toWKT()
    //        );
    //exportShapefileOBJSplines(
    //        "C:\\richard\\temp\\railway_polyline_503500_155500_WGS84.shp",
    //        "C:\\Users\\richard\\Documents\\3dsmax\\import\\42\\railway_polyline_503500_155500_CARTESIAN.obj"
    //        );
    
    //Tiling
    ImageTiler tiler = new ImageTiler();
    tiler.loadImage();
    //tiler.tileImage(3,256,128); //debug fixed settings
    tiler.tileImage(3,512,256); //debug fixed settings
    
  }
  
  /**
   * Test code to read in a shapefile and write out its schema
   * @param filename 
   */
  public static void readShapefile(String filename) {
    File f = new File(filename);
    URI shapeURI = f.toURI();
    try {
      ShapefileDataStore store = new ShapefileDataStore(shapeURI.toURL());
      String name = store.getTypeNames()[0];
      SimpleFeatureSource source = store.getFeatureSource(name);
      FeatureCollection fsShape = source.getFeatures();
      SimpleFeatureType ft = source.getSchema();
      
      //Print out the schema information
      System.out.println("FID\t");
      for (int i = 0; i < ft.getAttributeCount(); i++) {
        AttributeType at = ft.getDescriptor(i).getType();
        if (!Geometry.class.isAssignableFrom(at.getClass()))
          System.out.print(at.getBinding().getName() + "\n");
      }
      System.out.println();
      for (int i = 0; i < ft.getAttributeCount(); i++) {
        AttributeType at = ft.getDescriptor(i).getType();
        if (!Geometry.class.isAssignableFrom(at.getClass()))
          System.out.print(at.getName() + "\n");
      }
      System.out.println();
      
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  /**
   * Convert spherical coords (in degrees) to cartesian
   * Radius for WGS84 = 6378137 metres
   * See Wolfram Mathworld on Spherical Coordinates:
   * http://mathworld.wolfram.com/SphericalCoordinates.html
   * This is done using an explicit formula so I can guarantee the maths being
   * used. Ideally, this should be converted to an ECEF WKT, but the axes are the
   * wrong way round by default and it uses a flattened earth.
   * @param Lat
   * @param Lon
   * @param Height
   * @param Radius
   * @returns
   */
  public static float[] convertSphericalToCartesian(float lat, float lon, float height, float radius) {
    double theta = lon * Math.PI / 180.0;
    double phi = lat * Math.PI / 180.0;
    double cosTheta = Math.cos(theta);
    double sinTheta = Math.sin(theta);
    double cosPhi = Math.cos(phi);
    double sinPhi = Math.sin(phi);
    radius += height;
    //This is the Mathworld formula:
    //double x = Radius * CosTheta * SinPhi;
    //double y = Radius * SinTheta * SinPhi;
    //double z = Radius * CosPhi;
    //And this is the one I derived:
    double x = radius * cosPhi * sinTheta;
    double y = radius * sinPhi;
    double z = radius * cosPhi * cosTheta;
    return new float[] { (float)x, (float)y, (float)z };
  }
  
  /**
   * TODO: this is really a hack, added spherical to cartesian reprojection from the
   * outCRSWKT which must be WGS84 - really need to use proper ECEF with custom axes here.
   * Reproject a shapefile into a new projection given by outCRS as WKT
   * Destination CRS is currently wrong
   * @param inFilename
   * @param outFilename
   * @param outCRS 
   */
  public static void reprojectShapefile(String inFilename, String outFilename, String outCRSWKT) {
    File inFile = new File(inFilename);
    File outFile = new File(outFilename);
    //URI inShapeURI = inFile.toURI();
    //URI outShapeURI = outFile.toURI();
    try {
      ShapefileDataStore inStore = new ShapefileDataStore(inFile.toURI().toURL());
      String name = inStore.getTypeNames()[0];
      SimpleFeatureSource inSource = inStore.getFeatureSource(name);
      FeatureCollection inFSShape = inSource.getFeatures();
      SimpleFeatureType inFT = inSource.getSchema();
      
      //build a transformation between the source and dest CRS
      CoordinateReferenceSystem srcCRS = inFT.getCoordinateReferenceSystem();
      //CoordinateReferenceSystem worldCRS = map.getCoordinateReferenceSystem();
      CoordinateReferenceSystem destCRS = CRS.parseWKT(outCRSWKT);
      boolean lenient = true; // allow for some error due to different datums
      //TODO: used to be able to print a warining if the datum shift was lenient
      MathTransform transform = CRS.findMathTransform(srcCRS, destCRS, lenient);
      
      //build schema for the new file
      SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
      builder.setName(name);
      builder.setCRS(destCRS);
      builder.addAll(inFT.getAttributeDescriptors());
      // build the type
      final SimpleFeatureType FEATURETYPE = builder.buildFeatureType();
      
      //create new datastore
      ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
      Map<String, Serializable> params = new HashMap<String, Serializable>();
      params.put("url", outFile.toURI().toURL());
      params.put("create spatial index", Boolean.FALSE);
      ShapefileDataStore newDataStore = (ShapefileDataStore)dataStoreFactory.createNewDataStore(params);
      newDataStore.createSchema(FEATURETYPE);
      //SimpleFeatureSource outSource = newDataStore.getFeatureSource();
      
      //read through the existing shapefile feature by feature and write out the new one
      Transaction transaction = new DefaultTransaction("Reproject");
      FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newDataStore.getFeatureWriter(name, transaction);
      SimpleFeatureIterator fIT = (SimpleFeatureIterator)inFSShape.features();
      try {
        while (fIT.hasNext())
        {
          SimpleFeature feature = fIT.next();
          SimpleFeature copy = writer.next();
          copy.setAttributes(feature.getAttributes());
          //do the reprojection
          Geometry geometry = (Geometry) feature.getDefaultGeometry();
          Geometry geometry2 = JTS.transform(geometry, transform);
          //NOTE: section below was to create a custom ECEF transformation manually - removed
          //now go through geometry 2 and change the spherical lat lon coords to cartesian
          //Coordinate coords[]=geometry2.getCoordinates();
          //for (int i=0; i<coords.length; i++) {
          //  double x = coords[i].x;
          //  double y = coords[i].y;
          //  double z = coords[i].z;
          //  float xyz[]=convertSphericalToCartesian((float)y,(float)x,(float)z,6378137.0f);
          //  coords[i].x=xyz[0];
          //  coords[i].y=xyz[1];
          //  coords[i].z=xyz[2];
          //}
          //geometry2.geometryChanged(); //required to force update of various internal things
          //spherical to cartesian finished, now set the new geometry
          copy.setDefaultGeometry(geometry2);
          writer.write();
        }
      }
      finally {
        writer.close();
        fIT.close();
        transaction.commit();
        transaction.close();
      }
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    catch (org.opengis.referencing.FactoryException fac) {
      fac.printStackTrace();
    }
    catch (org.opengis.referencing.operation.TransformException tex) {
      tex.printStackTrace();
    }
  }
  
  /**
   * Cut a shapefile so that only everthing within the box is retained.
   * Very similar to the reproject code.
   * @param inFilename Filename of input shapefile
   * @param outFilename Filename of output shapefile
   * @param box The box to cut out in the CRS of the input shapefile
   */
  public static void cutShapefile(String inFilename, String outFilename, Envelope box) {
    //This is annoying, we need to convert the envelope into a geometry in order to do the intersect test
    GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
    Geometry boxGeom = gf.toGeometry(box);
    
    File inFile = new File(inFilename);
    File outFile = new File(outFilename);
    try {
      ShapefileDataStore inStore = new ShapefileDataStore(inFile.toURI().toURL());
      String name = inStore.getTypeNames()[0];
      SimpleFeatureSource inSource = inStore.getFeatureSource(name);
      FeatureCollection inFSShape = inSource.getFeatures();
      SimpleFeatureType inFT = inSource.getSchema();
      
      //create new datastore
      ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
      Map<String, Serializable> params = new HashMap<String, Serializable>();
      params.put("url", outFile.toURI().toURL());
      params.put("create spatial index", Boolean.FALSE);
      ShapefileDataStore newDataStore = (ShapefileDataStore)dataStoreFactory.createNewDataStore(params);
      newDataStore.createSchema(inFT); //copy schema from original shapefile
      //SimpleFeatureSource outSource = newDataStore.getFeatureSource();
      
      //read through the existing shapefile feature by feature and write out the new one
      Transaction transaction = new DefaultTransaction("Cut");
      FeatureWriter<SimpleFeatureType, SimpleFeature> writer = newDataStore.getFeatureWriter(name, transaction);
      SimpleFeatureIterator fIT = (SimpleFeatureIterator)inFSShape.features();
      try {
        while (fIT.hasNext())
        {
          SimpleFeature feature = fIT.next();
          Geometry geometry = (Geometry) feature.getDefaultGeometry();
          if (boxGeom.intersects(geometry)) {
            //TODO: you could chop the data in half here by doing some CSG ops
            //might be a good idea to do this later. I think that's newGeom=boxGeom.intersection(geometry)?
            SimpleFeature copy = writer.next();
            copy.setDefaultGeometry(geometry);
            copy.setAttributes(feature.getAttributes());
            writer.write();
          }
        }
      }
      finally {
        writer.close();
        fIT.close();
        transaction.commit();
        transaction.close();
      }
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  /**
   * Export a shapefile in GeoJSON format
   * @param shpFilename
   * @param geoJSONFilename
   * @param fixGeometry If true, fix the geometry if necessary
   */
  public static void exportGeoJSON(String shpFilename, String geoJSONFilename, boolean fixGeometry) {
    File f = new File(shpFilename);
    URI shapeURI = f.toURI();
    try {
      ShapefileDataStore store = new ShapefileDataStore(shapeURI.toURL());
      String name = store.getTypeNames()[0];
      SimpleFeatureSource source = store.getFeatureSource(name);
      FeatureCollection fsShape = source.getFeatures();
      SimpleFeatureType ft = source.getSchema();
      
      //Print out the schema information
      //System.out.println("FID\t");
      //for (int i = 0; i < ft.getAttributeCount(); i++) {
      //  AttributeType at = ft.getDescriptor(i).getType();
      //  if (!Geometry.class.isAssignableFrom(at.getClass()))
      //    System.out.print(at.getBinding().getName() + "\n");
      //}
      //System.out.println();
      //for (int i = 0; i < ft.getAttributeCount(); i++) {
      //  AttributeType at = ft.getDescriptor(i).getType();
      //  if (!Geometry.class.isAssignableFrom(at.getClass()))
      //    System.out.print(at.getName() + "\n");
      //}
      //System.out.println();
      
      FeatureJSON fjson = new FeatureJSON(new GeometryJSON(9)); //create with 8 decimal point precision - default is 4
      //StringWriter writer = new StringWriter();
      BufferedWriter bw = new BufferedWriter(new FileWriter(geoJSONFilename));
      //you can do this on one go as below by writing the entire feature collection,
      //but I need to fix the geometry of each feature as I go along
      //fjson.writeFeatureCollection(fsShape, bw); //easy way
      
      //write preamble for a feature collection - needed if we have to write features manually
      bw.write("{\"type\":\"FeatureCollection\",\"features\":[");
      SimpleFeatureIterator fIT = (SimpleFeatureIterator)fsShape.features();
      try {
        while (fIT.hasNext())
        {
          SimpleFeature feature = fIT.next();
          if (fixGeometry) {
            Geometry geom = (Geometry)feature.getDefaultGeometry();
            geom = fixInvalidGeometry(geom);
            feature.setDefaultGeometry(geom);
          }
          fjson.writeFeature(feature, bw);
          if (fIT.hasNext()) bw.write(","); //need a feature separator as writing feature collection manually
        }
      }
      finally {
        fIT.close();
      }
      bw.write("]}"); //post amble as we are writing the feature collection manually
      bw.close();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  /**
   * Export a shapefile as collada (DAE).
   * Note the Z/Y axis switch and -ve Z axis due to opengl being the other way around.
   * TODO: currently exporting polygon objects, might be better to export triangles
   * and do the triangulation myself
   * NO HOLE DATA IS USED, SO SOME BUILDINGS WILL BE WRONG
   * @param shpFilename
   * @param colladaFilename 
   */
  public static void exportBuildingCollada(String shpFilename, String colladaFilename) {
    Random rnd = new Random();
    ColladaWriter cw = new ColladaWriter(colladaFilename);
    
    File f = new File(shpFilename);
    URI shapeURI = f.toURI();
    try {
      ShapefileDataStore store = new ShapefileDataStore(shapeURI.toURL());
      String name = store.getTypeNames()[0];
      SimpleFeatureSource source = store.getFeatureSource(name);
      FeatureCollection fsShape = source.getFeatures();
      SimpleFeatureType ft = source.getSchema();
      
      int polyCount=0;
      SimpleFeatureIterator fIT = (SimpleFeatureIterator)fsShape.features();
      try {
        while (fIT.hasNext())
        {
          SimpleFeature feature = fIT.next();
          Geometry geom = (Geometry)feature.getDefaultGeometry();
          ArrayList<Polygon> polys = extractPolygons(geom);
          for (int i=0; i<polys.size(); i++) {
            //TODO: only using outer rings at the moment for testing
            LineString outer = polys.get(i).getExteriorRing();
            Coordinate coords[] = outer.getCoordinates();
            int numPoints = coords.length-1; //LinearRings are closed, so don't need last point
            float pts[][] = new float[numPoints*2][3]; //numPoints*2 for a lower and upper ring
            //bottom ring
            for (int j=0; j<numPoints; j++) {
              float x=(float)coords[j].x;
              float y=(float)coords[j].y;
              float z=0;
              float xyz[] = convertSphericalToCartesian(y,x,z,6378137);
              pts[j][0]=xyz[0]/1000.0f;
              pts[j][1]=-xyz[2]/1000.0f; //z is up in collada and the direction is reversed from opengl (-ve)
              pts[j][2]=xyz[1]/1000.0f;
            }
            //top ring
            float height = rnd.nextFloat()*100;
            for (int j=0; j<numPoints; j++) {
              float x = (float)coords[j].x;
              float y = (float)coords[j].y;
              float z=height; //Random number of metres above spheroid
              float xyz[] = convertSphericalToCartesian(y,x,z,6378137);
              pts[j+numPoints][0]=xyz[0]/1000.0f;
              pts[j+numPoints][1]=-xyz[2]/1000.0f; //z is up in collada and the direction is reversed from opengl (-ve)
              pts[j+numPoints][2]=xyz[1]/1000.0f;
            }
            //now create faces - one for each side and a top face
            int numFaces = numPoints+1;
            int faces[][] = new int[numFaces][];
            for (int j=0; j<numFaces; j++) {
              faces[j]=new int[4];
              faces[j][3]=j; //reverse order: 3210
              faces[j][2]=numPoints+j;
              faces[j][1]=numPoints+(j+numPoints-1)%numPoints;
              faces[j][0]=(j+numPoints-1)%numPoints;
            }
            //final face is the top face which is a simple ring of the top vertices
            faces[numPoints] = new int[numPoints];
            for (int j=0; j<numPoints; j++)
              //faces[numPoints][j]=j+numPoints; //forwards 56789
              faces[numPoints][j]=(numPoints-j-1)+numPoints; //backwards 98765 - need this for top face to be right way around
            //create normals, one for each face
            float normals[][] = new float[numFaces][3];
            for (int j=0; j<numFaces; j++) {
              //these are all planar faces, so only need to cross vectors from first 3 points
              //although, they could be equal, in which case the normal calculation goes wrong,
              //so cycle through triples of points around the loop until we find a normal that works.
              //TODO: it might be better to regularise the normals to an average for the top face as
              //some of them look obviously wrong
              //In fact, some are so wrong you don't see the top face, so don't write out the normals and
              //let three.js calculate them for me
              boolean validNormal = false;
              int k=0;
              while ((!validNormal) && k<numPoints-2) { //OK, could wrap the points around, but not going to bother
                int v0=faces[j][k];
                int v1=faces[j][k+1];
                int v2=faces[j][k+2];
                float a[] = new float[3];
                float b[] = new float[3];
                float axb[] = new float[3]; //a cross b
                a[0]=pts[v0][0]-pts[v1][0];
                a[1]=pts[v0][1]-pts[v1][1];
                a[2]=pts[v0][2]-pts[v1][2];
                b[0]=pts[v2][0]-pts[v1][0];
                b[1]=pts[v2][1]-pts[v1][1];
                b[2]=pts[v2][2]-pts[v1][2];
                //cross product - you would think JTS would have 3D vectors, but it doesn't
                axb[0]=a[1]*b[2]-a[2]*b[1];
                axb[1]=a[2]*b[0]-a[0]*b[2];
                axb[2]=a[0]*b[1]-a[1]*b[0];
                //normalise
                float mag = (float)Math.sqrt((axb[0]*axb[0]) + (axb[1]*axb[1]) + (axb[2]*axb[2]));
                if (mag>Float.MIN_VALUE) {
                  axb[0]/=mag; axb[1]/=mag; axb[2]/=mag;
                  normals[j][0]=-axb[0];
                  normals[j][1]=-axb[1];
                  normals[j][2]=-axb[2];
                  validNormal=true; //and exit the while loop at the next point
                }
                ++k;
              }
              //I'm going to assume the normal for this face is right here - can't do much if it isn't
            }
            
            //System.out.println("Write poly "+polyCount);
            cw.addGeometry("geom_poly"+polyCount, pts, faces, null/*normals*/); //works better without normals
            cw.addSceneObject("poly"+polyCount, "geom_poly"+polyCount); //TODO: check the -lib suffix
            //System.out.println("Written: "+polyCount);
            ++polyCount;
          }
          //if (polyCount>0) break; //HACK!
        }
      }
      finally {
        fIT.close();
      }
      cw.writeFile(colladaFilename);
      cw.close();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    
  }
  
  /**
   * Export a 2D shapefile (i.e. no height) as collada.
   * The buildings version of this function makes 3D boxes, while this version
   * just exports flat faces.
   * @param shpFilename
   * @param colladaFilename
   * TODO: do you need a model name prefix as all the names are shared between additional collada files?
   * You would have to hope that importing two models wouldn't be a problem.
   */
  public static void exportCollada(String shpFilename, String colladaFilename) {
    ColladaWriter cw = new ColladaWriter(colladaFilename);
    
    File f = new File(shpFilename);
    URI shapeURI = f.toURI();
    try {
      ShapefileDataStore store = new ShapefileDataStore(shapeURI.toURL());
      String name = store.getTypeNames()[0];
      SimpleFeatureSource source = store.getFeatureSource(name);
      FeatureCollection fsShape = source.getFeatures();
      SimpleFeatureType ft = source.getSchema();
      
      int polyCount=0;
      SimpleFeatureIterator fIT = (SimpleFeatureIterator)fsShape.features();
      try {
        while (fIT.hasNext())
        {
          SimpleFeature feature = fIT.next();
          Geometry geom = (Geometry)feature.getDefaultGeometry();
          ArrayList<Polygon> polys = extractPolygons(geom);
          for (int i=0; i<polys.size(); i++) {
            //TODO: only using outer rings at the moment for testing
            LineString outer = polys.get(i).getExteriorRing();
            Coordinate coords[] = outer.getCoordinates();
            int numPoints = coords.length-1; //LinearRings are closed, so don't need last point
            float pts[][] = new float[numPoints][3]; //numPoints*2 for a lower and upper ring
            //ring
            for (int j=0; j<numPoints; j++) {
              float x = (float)coords[j].x;
              float y = (float)coords[j].y;
              float z=0;
              float xyz[] = convertSphericalToCartesian(y,x,z,6378137);
              pts[j][0]=xyz[0]/1000.0f;
              pts[j][1]=-xyz[2]/1000.0f; //z is up in collada and the direction is reversed from opengl (-ve)
              pts[j][2]=xyz[1]/1000.0f;
            }
            //now create a top face - this is overkill for just one face, but we might handle internal polys later
            int numFaces = 1;
            int faces[][] = new int[numFaces][];
            //top face is a simple ring of the outer vertices
            faces[0] = new int[numPoints];
            for (int j=0; j<numPoints; j++)
              faces[0][j]=(numPoints-j-1); //backwards 43210 - need this for top face to be right way around
            //create normals, one for each face
            float normals[][] = new float[numFaces][3];
            for (int j=0; j<numFaces; j++) {
              //these are all planar faces, so only need to cross vectors from first 3 points
              //although, they could be equal, in which case the normal calculation goes wrong,
              //so cycle through triples of points around the loop until we find a normal that works.
              //TODO: it might be better to regularise the normals to an average for the top face as
              //some of them look obviously wrong
              //In fact, some are so wrong you don't see the top face, so don't write out the normals and
              //let three.js calculate them for me
              boolean validNormal = false;
              int k=0;
              while ((!validNormal) && k<numPoints-2) { //OK, could wrap the points around, but not going to bother
                int v0=faces[j][k];
                int v1=faces[j][k+1];
                int v2=faces[j][k+2];
                float a[] = new float[3];
                float b[] = new float[3];
                float axb[] = new float[3]; //a cross b
                a[0]=pts[v0][0]-pts[v1][0];
                a[1]=pts[v0][1]-pts[v1][1];
                a[2]=pts[v0][2]-pts[v1][2];
                b[0]=pts[v2][0]-pts[v1][0];
                b[1]=pts[v2][1]-pts[v1][1];
                b[2]=pts[v2][2]-pts[v1][2];
                //cross product - you would think JTS would have 3D vectors, but it doesn't
                axb[0]=a[1]*b[2]-a[2]*b[1];
                axb[1]=a[2]*b[0]-a[0]*b[2];
                axb[2]=a[0]*b[1]-a[1]*b[0];
                //normalise
                float mag = (float)Math.sqrt((axb[0]*axb[0]) + (axb[1]*axb[1]) + (axb[2]*axb[2]));
                if (mag>Float.MIN_VALUE) {
                  axb[0]/=mag; axb[1]/=mag; axb[2]/=mag;
                  normals[j][0]=-axb[0];
                  normals[j][1]=-axb[1];
                  normals[j][2]=-axb[2];
                  validNormal=true; //and exit the while loop at the next point
                }
                ++k;
              }
              //I'm going to assume the normal for this face is right here - can't do much if it isn't
            }
            
            //System.out.println("Write poly "+polyCount);
            cw.addGeometry("geom_poly"+polyCount, pts, faces, null/*normals*/); //works better without normals
            cw.addSceneObject("poly"+polyCount, "geom_poly"+polyCount); //TODO: check the -lib suffix
            //System.out.println("Written: "+polyCount);
            ++polyCount;
          }
        }
      }
      finally {
        fIT.close();
      }
      cw.writeFile(colladaFilename);
      cw.close();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    
  }
  
  /**
   * Write to console ordering of points in geometry
   * @param shpFilename 
   */
  public static void testPointOrdering(String shpFilename) {
    File f = new File(shpFilename);
    URI shapeURI = f.toURI();
    try {
      ShapefileDataStore store = new ShapefileDataStore(shapeURI.toURL());
      String name = store.getTypeNames()[0];
      SimpleFeatureSource source = store.getFeatureSource(name);
      FeatureCollection fsShape = source.getFeatures();
      SimpleFeatureType ft = source.getSchema();
      
      SimpleFeatureIterator fIT = (SimpleFeatureIterator)fsShape.features();
      try {
        while (fIT.hasNext())
        {
          SimpleFeature feature = fIT.next();
          String f_name = (String)feature.getAttribute("NAME");
          ArrayList<Polygon> polys = extractPolygons((Geometry)feature.getDefaultGeometry());
          for (int i=0; i<polys.size(); i++) {
            polygonOrdering(polys.get(i));
            if (!polys.get(i).isValid()) {
              System.out.println(f_name+" Invalid polygon");
              Polygon poly2 = fixInvalidPolygon(polys.get(i));
              //System.out.println("RETEST...");
              //if (!poly2.isValid()) System.out.println("ERROR: RETEST POLYGON STILL INVALID");
              //if (fixInvalidPolygon(poly2)!=poly2) {
              //  System.out.println("ERROR: problem not fixed");
              //}
            }
          }
        }
      }
      finally {
        fIT.close();
      }
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  /**
   * Extract all individual polygons from a geometry into a flat list
   */
  public static ArrayList<Polygon> extractPolygons(Geometry geom) {
    ArrayList<Polygon> result = new ArrayList<Polygon>();
    if (geom.getGeometryType().equals("Polygon")) {
      result.add((Polygon)geom);
    }
    else if (geom.getGeometryType().equals("MultiPolygon")) {
      MultiPolygon mp = (MultiPolygon)geom;
      for (int n=0; n<mp.getNumGeometries(); n++) {
        result.add((Polygon)mp.getGeometryN(n));
      }
    }
    //tail recursion
    for (int i=1; i<geom.getNumGeometries(); i++) { //NOTE 1 from SFS
      ArrayList<Polygon> children = extractPolygons(geom.getGeometryN(i));
      result.addAll(children);
    }
    return result;
  }
  
  /**
   * Same as extractPolygons except it extracts all the LineStrings
   * @param geom
   * @return 
   */
  public static ArrayList<LineString> extractLineStrings(Geometry geom) {
    ArrayList<LineString> result = new ArrayList<LineString>();
    if (geom.getGeometryType().equals("LineString")) {
      result.add((LineString)geom);
    }
    else if (geom.getGeometryType().equals("MultiLineString")) {
      MultiLineString ml = (MultiLineString)geom;
      for (int n=0; n<ml.getNumGeometries(); n++) {
        result.add((LineString)ml.getGeometryN(n));
      }
    }
    //tail recursion
    for (int i=1; i<geom.getNumGeometries(); i++) { //NOTE 1 from SFS
      ArrayList<LineString> children = extractLineStrings(geom.getGeometryN(i));
      result.addAll(children);
    }
    return result;
  }
  
  /**
   * Uses polygon area to determine clockwise or anticlockwise ordering.
   * See: http://mathworld.wolfram.com/PolygonArea.html
   * @param geom 
   */
  public static void polygonOrdering(Polygon poly) {
    LineString outer = poly.getExteriorRing();
    double signedArea = polygonSignedArea(outer.getCoordinates());
    if (signedArea>=0)
      System.out.println("outer="+signedArea);
    
    //for (int i=0; i<poly.getNumInteriorRing(); i++) {
    //  LineString inner = poly.getInteriorRingN(i);
    //  signedArea = polygonSignedArea(inner.getCoordinates());
    //  if (signedArea<=0)
    //    System.out.println("inner="+signedArea);
    //}
  }
  
  /**
   * Polygon area calculation.
   * @param coords
   * @return 
   */
  public static double polygonSignedArea(Coordinate coords[]) {
    double area=0;
    double x1,y1,x2,y2;
    x1=coords[0].x;
    y1=coords[0].y;
    //this relies on the fact that the polygons are closed, first point=last point
    for (int i=1; i<coords.length; i++) {
      x2=coords[i].x;
      y2=coords[i].y;
      double delta = (x1 * y2 - x2 * y1);
      //if (delta>=0)
      //  System.out.println("Delta="+delta);
      area += delta;
      x1=x2;
      y1=y2;
    }
    return area/2;
  }
  
  /**
   * Recursively fix invalid polygons and multipolygons and return a fixed
   * geometry object
   * @param geom The geometry that needs to be fixed
   * @return A new fixed geometry if required, otherwise passing a valid geometry
   * results in you getting the same object back again
   */
  public static Geometry fixInvalidGeometry(Geometry geom) {
    if (geom.isValid()) return geom;
    
    //OK, it's invalid so we need to do some fixing
    String geomType = geom.getGeometryType();
    if (geomType.equals("Point")) {
      return geom; //don't fix points
    }
    else if (geomType.equals("LineString")) {
      return geom; //don't fix lines
    }
    else if (geomType.equals("Polygon")) {
      return fixInvalidPolygon((Polygon)geom);
    }
    else {
      //multi or geometry collection type, so we need to fix recursively
      GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

      int numGeom = geom.getNumGeometries();
      Geometry children[]=new Geometry[numGeom];
      for (int n=0; n<numGeom; n++) {
        Geometry geomN = geom.getGeometryN(n);
        children[n]=fixInvalidGeometry(geomN);
      }
      
      return geometryFactory.createGeometryCollection(children);
    }
  }
  
  /**
   * Test validity of polygon and report on why if it isn't, returning a fixed
   * version if possible. At the moment this only fixes outer boundaries touching
   * themselves as this seems to fix all the problems with poly2tri building
   * a 3D mesh from the geometry. Could also fix holes touching the outer boundary
   * by removing the holes, but this has been disabled.
   * @param poly
   * @param fix
   * @return If polygon is valid then returns the original polygon, otherwise
   * returns the fixed polygon
   * TODO: should really write out a message saying where we've fixed things
   * so that it can be manually checked.
   */
  public static Polygon fixInvalidPolygon(Polygon poly) {
    final double coordEpsilon = 0.000001; //smallest valid distance between points
    GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    
    if (!poly.isValid()) {
      //OK, it's not valid so we need to figure out why
      
      //check coordinates are valid
      Coordinate coords[]=poly.getCoordinates();
      for (Coordinate coord:coords) {
        if (Double.isNaN(coord.x)) System.out.println("X coord is NaN");
        if (Double.isInfinite(coord.x)) System.out.println("X coord is infinite");
        if (Double.isNaN(coord.y)) System.out.println("Y coord is NaN");
        if (Double.isInfinite(coord.y)) System.out.println("Y coord is infinite");
      }
      
      //test for outer ring touching inner ring and complete containment of
      //all innner rings and closed rings
      LineString outer = poly.getExteriorRing();
      if (!outer.isClosed()) System.out.println("Outer ring is not closed"); //test closure - trivial
      if (outer.getNumPoints()<3) System.out.println("Outer ring only has "+outer.getNumPoints()+" points");
      int numRings = poly.getNumInteriorRing();
      System.out.println("num interior rings="+numRings);
      for (int i=0; i<numRings; i++) {
        LineString inner = poly.getInteriorRingN(i);
        if (!inner.isClosed()) {
          System.out.println("Inner ring "+ i +" is not closed");
        }
        if (inner.getNumPoints()<3) System.out.println("Inner ring only has "+inner.getNumPoints()+" points");
        if (outer.touches(inner)) {
          System.out.println("Inner ring " + i + " touches outer ring");
        }
        if (!poly.contains(inner)) {
          System.out.println("Outer ring does not fully contain inner ring "+i);
          //fix it... 
          //inner fix - make it smaller
          //drop out the offending ring as making it smaller causes too many problems
          /*LinearRing holes[] = new LinearRing[numRings-1];
          int ri=0;
          for (int r=0; r<numRings; r++) {
            if (r!=i) { holes[ri]=(LinearRing)poly.getInteriorRingN(r); ++ri; }
          }
          //switch polygons here so future operations are on the fixed one!
          poly = geometryFactory.createPolygon((LinearRing)outer, holes);
          //reset the number of rings and the index counter to avoid skipping
          --numRings;
          --i;*/
        }
      }
      //now test for inner rings touching each other
      for (int i=0; i<numRings-1; i++) {
        LineString innerI = poly.getInteriorRingN(i);
        for (int j=i+1; j<numRings; j++) {
          LineString innerJ = poly.getInteriorRingN(j);
          if (innerI.touches(innerJ)) {
            System.out.println("Inner ring "+i+" touches inner ring "+j);
          }
        }
      }
      
      //test for outer ring touching itself - this is not the most efficient way of doing this, it's brute force
      Coordinate ocoords[]=outer.getCoordinates();
      Coordinate op1 = ocoords[ocoords.length-1]; //using last coord so wrapping works
      Coordinate op2;
      for (int i=0; i<ocoords.length; i++) {
        op2 = ocoords[i];
        Coordinate c[]={op1,op2};
        LineString seg = geometryFactory.createLineString(c);
        if (seg.crosses(outer)) {
          System.out.println("Outer ring crosses itself");
        }
        op1=op2;
      }
      //test for repeated points in the outer ring (this should trigger touches?)
      for (int i=0; i<ocoords.length-1; i++) { //last point = first point so skip
        for (int j=i+1; j<ocoords.length-1; j++) {
          if ((Math.abs(ocoords[i].x-ocoords[j].x)<coordEpsilon)
            &&(Math.abs(ocoords[i].y-ocoords[j].y)<coordEpsilon)) {
            System.out.println("Outer ring duplicate coordinates length="+ocoords.length+" i="+i+" j="+j+" "+ocoords[i]+" "+ocoords[j]);
            Coordinate newCoords[] = removeBoundaryDuplicatePoints(ocoords,i,j);
            LinearRing shell = geometryFactory.createLinearRing(newCoords);
            LinearRing holes[] = new LinearRing[poly.getNumInteriorRing()];
            for (int h=0; h<poly.getNumInteriorRing(); h++) holes[h]=(LinearRing)poly.getInteriorRingN(h);
            //switch polygons, so all future operations are on the fixed one!
            poly = geometryFactory.createPolygon(shell, holes);
          }
        }
      }
      
      //test for inner rings touching themselves
      for (int r=0; r<numRings; r++) {
        LineString inner = poly.getInteriorRingN(r);
        Coordinate icoords[]=inner.getCoordinates();
        Coordinate ip1 = icoords[icoords.length-1]; //using last coord so wrapping works
        Coordinate ip2;
        for (int i=0; i<icoords.length; i++) {
          ip2 = icoords[i];
          Coordinate c[]={ip1,ip2};
          LineString seg = geometryFactory.createLineString(c);
          if (seg.crosses(inner)) {
            System.out.println("Inner ring "+ r +" crosses itself");
          }
          ip1=ip2;
        }
      }
      //test for repeated points in the inner ring (this should trigger touches?)
      for (int r=0; r<numRings; r++) {
        LineString inner = poly.getInteriorRingN(r);
        Coordinate icoords[]=inner.getCoordinates();
        for (int i=0; i<icoords.length-1; i++) { //last point = first point so skip
          for (int j=i+1; j<icoords.length-1; j++) {
            if ((Math.abs(icoords[i].x-icoords[j].x)<coordEpsilon)
              &&(Math.abs(icoords[i].y-icoords[j].y)<coordEpsilon)) {
              System.out.println("Inner ring "+ r +" duplicate coordinates length="+icoords.length+" i="+i+" j="+j+" "+icoords[i]+" "+icoords[j]);
            }
          }
        }
      }
      
      //from JTS operation/IsValidOp.checkValid(Polygon)
      //checkInvalidCoordinates(g);
      //if (validErr != null) return;
      //checkClosedRings(g);
      //if (validErr != null) return;
      //
      //GeometryGraph graph = new GeometryGraph(0, g);
      //
      //checkTooFewPoints(graph);
      //if (validErr != null) return;
      //checkConsistentArea(graph);
      //if (validErr != null) return;
      //
      //if (! isSelfTouchingRingFormingHoleValid) {
      //  checkNoSelfIntersectingRings(graph);
      //  if (validErr != null) return;
      //}
      //checkHolesInShell(g, graph);
      //if (validErr != null) return;
      ////SLOWcheckHolesNotNested(g);
      //checkHolesNotNested(g, graph);
      //if (validErr != null) return;
      //checkConnectedInteriors(graph);
    }
    
    return poly;
  }
  
  /**
   * Edit a LineString boundary to remove any points where the boundary touches
   * itself. There are two ways of doing this: 1. move the two touching points
   * apart so they no longer touch, or 2. drop out all the points between the
   * two touching points and turn it into a hole. This assumes holes can touch
   * boundaries though. A more robust solution would be to split the polygon.
   * I'm going to take option 1 as it seems the least problematic.
   * @param coords Coordinate list for the boundary (in order, either CW or CCW)
   * @param i index of first touch point in coords
   * @param j index of second touch point in coords
   * @return a new set of coords with the i and j points moved apart
   */
  public static Coordinate[] removeBoundaryDuplicatePoints(Coordinate coords[],int i,int j) {
    //make a copy of the coordinate array
    Coordinate newCoords[] = new Coordinate[coords.length];
    for (int c=0; c<coords.length; c++)
      newCoords[c] = new Coordinate(coords[c].x,coords[c].y);
    
    //move i and j apart
    //it's a complete ring so just look for the point before i and after j
    //THIS IS DEPENDENT ON ORDERING OF THE BOUNDARY TO GO IN THE CORRECT DIRECTION
    //NOTE +ve DELTA. THIS WORKS FOR SHAPEFILES
    int previousI = (i+coords.length-1)%coords.length;
    int followJ = (j+1)%coords.length;
    double delta = 0.01; //length to move along line (in CW or CCW directions)
    double dx,dy;
    //moving point i
    dx = coords[previousI].x-coords[i].x;
    dy = coords[previousI].y-coords[i].y;
    newCoords[i].x=coords[i].x+delta*dx;
    newCoords[i].y=coords[i].y+delta*dy;
    //moving point j
    dx = coords[followJ].x-coords[j].x;
    dy = coords[followJ].y-coords[j].y;
    newCoords[j].x=coords[j].x+delta*dx;
    newCoords[j].y=coords[j].y+delta*dy;
    
    return newCoords;
  }
  
  /**
   * Extract line features from a shapefile and save as an OBJ file
   */
  public static void exportShapefileOBJSplines(String shpFilename, String objFilename) {
    File fobj = new File(objFilename);
    File f = new File(shpFilename);
    
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(fobj));
      URI shapeURI = f.toURI();
      ShapefileDataStore store = new ShapefileDataStore(shapeURI.toURL());
      String name = store.getTypeNames()[0];
      SimpleFeatureSource source = store.getFeatureSource(name);
      FeatureCollection fsShape = source.getFeatures();
      SimpleFeatureType ft = source.getSchema();
      
      SimpleFeatureIterator fIT = (SimpleFeatureIterator)fsShape.features();
      try {
        int geomCount=0;
        int pointCount=1; //indexes start from 1 in obj files
        while (fIT.hasNext())
        {
          bw.write("o "+geomCount); bw.newLine();
          bw.write("g "+geomCount); bw.newLine(); //MAX doesn't import the o for object tag
          SimpleFeature feature = fIT.next();
          Geometry geom = (Geometry)feature.getDefaultGeometry();
          ArrayList<LineString> lines = extractLineStrings(geom);
          for (int i=0; i<lines.size(); i++) {
            Coordinate coords[] = lines.get(i).getCoordinates();
            int numPoints = coords.length;
            if (numPoints<=1) continue; //filter degenerates
            //write out vertices
            for (int j=0; j<numPoints; j++) {
              double x = coords[j].x;
              double y = coords[j].y;
              double z = coords[j].z;
              if (Double.isNaN(z)) z=0; //shapefiles generally have z=undefined
              float xyz[] = convertSphericalToCartesian((float)y,(float)x,(float)z,6378137);
              x=xyz[0]/1000.0f;
              y=xyz[1]/1000.0f; //THIS IS OBJ! z is up in collada and the direction is reversed from opengl (-ve)
              z=xyz[2]/1000.0f;
              bw.write("v "+x+" "+y+" "+z);
              bw.newLine();
            }
            //write out line segments
            bw.write("l");
            for (int j=0; j<numPoints; j++) {
              bw.write(" "+pointCount);
              ++pointCount;
            }
            bw.newLine();
          }
          ++geomCount;
        }
      }
      finally {
        fIT.close();
      }
      bw.close();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }
  
  
  
  
}
