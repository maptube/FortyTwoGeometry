/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fortytwogeometry;

import com.vividsolutions.jts.geom.Geometry;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureWriter;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.geotools.referencing.crs.DefaultGeographicCRS;
//import org.opengis.geometry.Envelope;
import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.factory.FactoryFinder;
//import org.geotools.geometry.Envelope2D;
//import org.geotools.geometry.GeneralEnvelope;
//import org.geotools.geometry.DirectPosition2D;
import com.vividsolutions.jts.geom.Envelope;
//import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.jts.GeometryBuilder;
import com.vividsolutions.jts.geom.MultiPolygon;
//import org.geotools.geometry.GeometryFactoryFinder;
//import org.opengis.geometry.coordinate.GeometryFactory;
//import org.geotools.geometry.jts.ReferencedEnvelope;
//import org.opengis.geometry.DirectPosition;
//import org.opengis.geometry.PositionFactory;

import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.GeoJSONUtil;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;

/**
 *
 * @author richard
 * Tile vector data, for example shapefiles.
 * The tiling is WGS84 using a quadtree. Data might not be in this projection.
 */
public class VectorTiler {
  //public int minDepth,maxDepth; //tiling is only created between min depth and max depth
  public String baseDir; //directory containing the top level of the tile directory
  private GeometryBuilder geomBuilder = new GeometryBuilder();
  private SimpleFeatureTypeBuilder builder;
  private SimpleFeatureBuilder featureBuilder;
  
  
  public VectorTiler() {
    //constructor
  }
  
  /**
   * Build a geometry only feature type and set up the featureBuilder to build it.
   * Takes name and geometry type from the parent feature collection passed in
   * @param fc FeatureCollection to clone geometry type and schema name from
   * @return The OnlyGeometry feature type
   */
  public SimpleFeatureType makeFeatureType(final FeatureCollection fc) {
    builder = new SimpleFeatureTypeBuilder();
    builder.setName(fc.getSchema().getName());
    builder.setCRS(DefaultGeographicCRS.WGS84);
    //builder.add("the_geom",MultiPolygon.class);
    builder.add("the_geom",fc.getSchema().getGeometryDescriptor().getType().getBinding());
    // build the type
    final SimpleFeatureType OnlyGeometry = builder.buildFeatureType();
    featureBuilder = new SimpleFeatureBuilder(OnlyGeometry);
    return OnlyGeometry;
  }
  
  
  /**
   * Tile a shapefile between minDepth and maxDepth, putting files into baseDir
   * Output format is geojson (with height tag?)
   * Output coordinate system is cartesian
   * TODO: really need to pick up whether a file already exists and add to it so
   * that multiple shapefiles can be aggregated one at a time.
   * @param shpFilename 
   * @param zoomLevel
   */
  public void tileShapefileSingleZoom(final String shpFilename, final int zoomLevel) {
    //get numbers of tiles for this zoom level
    double N = Math.pow(2, zoomLevel);
    double size=360.0/N; //for world bounds (-180,-180),(180,180)
    
    //Hints hints = new Hints( Hints.CRS, DefaultGeographicCRS.WGS84 );
    //PositionFactory positionFactory = GeometryFactoryFinder.getPositionFactory( hints );
    //GeometryFactory geometryFactory = GeometryFactoryFinder.getGeometryFactory( hints );

    
    File inFile = new File(shpFilename);
    //URI inShapeURI = inFile.toURI();
    try {
      ShapefileDataStore inStore = new ShapefileDataStore(inFile.toURI().toURL());
      String name = inStore.getTypeNames()[0];
      SimpleFeatureSource inSource = inStore.getFeatureSource(name);
      FeatureCollection inFSShape = inSource.getFeatures();
      SimpleFeatureType inFT = inSource.getSchema();
      
      makeFeatureType(inFSShape); //initialise featureBuilder with geom only type
      
      //build a transformation between the source and dest CRS which is WGS84
      CoordinateReferenceSystem srcCRS = inFT.getCoordinateReferenceSystem();
      CoordinateReferenceSystem destCRS = DefaultGeographicCRS.WGS84;
      boolean lenient = true; // allow for some error due to different datums
      MathTransform transform = CRS.findMathTransform(srcCRS, destCRS, lenient);
      
      //get bounds of shapefile
      //work out which boxes it covers
      //for x for y make each box
      //Envelope env = inSource.getBounds(); //in src CRS obviously!
      Envelope env = inSource.getBounds(); //in src CRS obviously!
      double pts[]={
        env.getMinX(),
        env.getMinY(),
        env.getMaxX(),
        env.getMaxY()
      };
      double wgs84pts[]= new double[4];
      transform.transform(pts,0,wgs84pts,0,2); //inarray,offset,outarray,offset,numpoints
      //min max X Y etc rounded up and down appropriately - assumes (-180,-180) (180,180)
      int minX=(int)Math.floor((wgs84pts[0]+180.0)/360.0*N);
      int minY=(int)Math.floor((wgs84pts[1]+180.0)/360.0*N); //is N the same in both directions?
      int maxX=(int)Math.ceil((wgs84pts[2]+180.0)/360.0*N);
      int maxY=(int)Math.ceil((wgs84pts[3]+180.0)/360.0*N);
      System.out.println("Tile limits: "+minX+","+minY+" "+maxX+","+maxY);
      
      float total=(maxY-minY+1)*(maxX-minX+1);
      float count=0;
      for (int tileY=minY; tileY<=maxY; ++tileY) {
        for (int tileX=minX; tileX<=maxX; ++tileX) {
          float pct=count/total*100.0f;
          //This assumes a square world of (-180,-180), (180,180)
          System.out.println(count+"/"+total+" ("+pct+"%) tiles");
          Geometry tileGeom = geomBuilder.box(-180+tileX*size,-180+tileY*size,-180+(tileX+1)*size,-180+(tileY+1)*size);
          File file = new File(baseDir+zoomLevel+"_"+tileX+"_"+tileY+".geojson");
          makeGeoJSONTile(file,tileX,tileY,tileGeom,inFSShape,transform);
          count+=1;
        }
      }
      
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    catch (org.opengis.referencing.operation.TransformException te) {
      te.printStackTrace();
    }
    catch (org.opengis.referencing.FactoryException fac) {
      fac.printStackTrace();
    }
    
    
  }
  
  /**
   * Make a tile as a geojson file containing only geometry that intersects or is contained in the box
   * @param file
   * @param tileX
   * @param tileY
   * @param tileEnv
   * @param fc
   * @param trans 
   */
  private void makeGeoJSONTile(File file,int tileX,int tileY,Geometry tileGeom,FeatureCollection fc,MathTransform trans) {
    //read through the existing shapefile feature by feature and write out the new tile
    System.out.println("writing tile "+tileX+","+tileY+": "+file.getAbsolutePath());
    
    //this isn't as simple as you think, because the source CRS is not WGS84, so, rather
    //than back projecting the tile box, go through every feature, reproject and
    //test individually.
    
    
    //open a geojson file
    FeatureJSON fjson = new FeatureJSON(new GeometryJSON(9)); //create with 8 decimal point precision - default is 4
    
    SimpleFeatureIterator fIT = (SimpleFeatureIterator)fc.features();
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));
      //you can do this on one go as below by writing the entire feature collection,
      //but I need to fix the geometry of each feature as I go along
      //fjson.writeFeatureCollection(fsShape, bw); //easy way
      
      //write preamble for a feature collection - needed if we have to write features manually
      bw.write("{\"type\":\"FeatureCollection\",\"features\":[");
    
      boolean firstFeature = true;
      while (fIT.hasNext())
      {
        //todo: need to check whether feture fits bounding box here
        SimpleFeature feature = fIT.next();
        //do the reprojection
        Geometry geometry = (Geometry) feature.getDefaultGeometry();
        try {
          Geometry transGeom = JTS.transform(geometry, trans);
          if (tileGeom.intersects(transGeom)) {
            //if (fixGeometry) {
            //  transGeom = fixInvalidGeometry(transGeom);
            //  feature.setDefaultGeometry(transGeom);
            //}
            
            //Create a new feature containing only geometry which is used for writing to geojson
            SimpleFeature fGeomOnly = featureBuilder.buildFeature(feature.getID());
            fGeomOnly.setDefaultGeometry(transGeom);
            
            //TODO: convert to Cartesian here? Extrusion won't work if you do
            
            if (!firstFeature) bw.write(","); //need a feature separator as writing feature collection manually
            fjson.writeFeature(fGeomOnly, bw);
            firstFeature=false;
          }
        }
        catch (org.opengis.referencing.operation.TransformException tex) {
          tex.printStackTrace();
        }
        
        
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
          
      }
      bw.write("]}"); //post amble as we are writing the feature collection manually
      bw.close();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
    finally {
      fIT.close();
    }
  }
  
}
