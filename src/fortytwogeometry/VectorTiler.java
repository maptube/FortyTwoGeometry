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
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
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
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.LineString;
//import org.geotools.geometry.GeometryFactoryFinder;
//import org.opengis.geometry.coordinate.GeometryFactory;
//import org.geotools.geometry.jts.ReferencedEnvelope;
//import org.opengis.geometry.DirectPosition;
//import org.opengis.geometry.PositionFactory;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;


import org.geotools.geojson.GeoJSON;
import org.geotools.geojson.GeoJSONUtil;
import org.geotools.geojson.feature.FeatureJSON;
import org.geotools.geojson.geom.GeometryJSON;

//triangulation imports from poly2tri
import org.poly2tri.Poly2Tri;
//import org.poly2tri.geometry.polygon.Polygon; //clashes with JTS
import org.poly2tri.geometry.polygon.PolygonPoint;
import org.poly2tri.geometry.polygon.PolygonSet;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.transform.coordinate.CoordinateTransform;
//import org.poly2tri.triangulation.tools.ardor3d.ArdorMeshMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Level;

//YOU NEED:
//slf4j-1.7.7/slf4j-api-1.7.7.jar
//apache-log4j-2.0.2-bin/log4j-api-2.0.2.jar
//apache-log4j-2.0.2-bin/log4j-core-2.0.2.jar
//apache-log4j-2.0.2-bin/log4j-1.2-api-2.0.2.jar

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
  
  private ArrayList<PolygonPoint> points;
  private ArrayList<PolygonPoint> normals;
  private ArrayList<Integer> faces;
  
  
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
          //File file = new File(baseDir+zoomLevel+"_"+tileX+"_"+tileY+".geojson");
          File file = new File(baseDir+zoomLevel+"_"+tileX+"_"+tileY+".obj");
          if (!file.exists())
            //makeGeoJSONTile(file,tileX,tileY,tileGeom,inFSShape,transform);
            makeOBJTile(file,tileX,tileY,tileGeom,inFSShape,transform);
          else
            System.out.println("Skipping: "+file.getName());
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
  
  /**
   * Copy of ellipsoid toVector. Converts lon,lat,height into Cartesian WGS84
   * @param lon
   * @param lat
   * @param geodeticHeight
   * @return 
   */
  protected PolygonPoint toVector(double lon,double lat,double geodeticHeight) {
    //HACK for Lat/lon points
    //return new PolygonPoint(Math.toDegrees(lon),Math.toDegrees(lat),geodeticHeight);
    
    //WGS84 ellipsoid
	double a=6378137.0;
	double b=a;
	double c=6356752.314245;
	//squared
	double a2=a*a;
	double b2=b*b;
	double c2=c*c;
	//fourth power
	//double a4=a2*a2;
	//double b4=b2*b2;
	//double c4=c2*c2;
	//recipricals
	//double ra2=1/a2;
	//double rb2=1/b2;
	//double rc2=1/c2;
    
    //work out normal position on surface of unit sphere using Euler formula and lat/lon
	//The latitude in this case is a geodetic latitude, so it's defined as the angle between the equatorial plane and the surface normal.
	//This is why the following works.
	double CosLat = Math.cos(lat);
	double nx=CosLat*Math.cos(lon);
    double ny=CosLat*Math.sin(lon);
    double nz=Math.sin(lat);
	//so (nx,ny,nz) is the geodetic surface normal i.e. the normal to the surface at lat,lon

	//using |ns|=gamma * ns, find gamma ( where |ns| is normalised ns)
	//with ns=Xs/a^2 i + Ys/b^2 j + Zs/c^2 k
	//equation of ellipsoid Xs^2/a^2 + Ys^2/b^2 + Zs^2/c^2 = 1
	//So, basically, I've got two equations for the geodetic surface normal that are related by a linear factor gamma

	double kx=a2*nx;
    double ky=b2*ny;
    double kz=c2*nz;

	double gamma = Math.sqrt(kx*nx+ky*ny+kz*nz);
	double rSurfacex=kx/gamma;
    double rSurfacey=ky/gamma;
    double rSurfacez=kz/gamma;

	//NOTE: you do rSurface = rSurface + (geodetic.height * n) to add the height on if you need it
	rSurfacex = rSurfacex + geodeticHeight*nx;
    rSurfacey = rSurfacey + geodeticHeight*ny;
    rSurfacez = rSurfacez + geodeticHeight*nz;

	return new PolygonPoint(rSurfacex,rSurfacey,rSurfacez);
  }
  
  protected PolygonPoint cross(PolygonPoint A,PolygonPoint B) {
    double x= A.getY()*B.getZ()-A.getZ()*B.getY(); // u2v3-u3v2
    double y= A.getZ()*B.getX()-A.getX()*B.getZ(); // u3v1-u1v3
    double z= A.getX()*B.getY()-A.getY()*B.getX(); // u1v2-u2v1
    return new PolygonPoint(x,y,z);
  }
  
  /**
   * Test two points for equality which is based on x dist less than epsilon (repeated for y and z)
   * @param A
   * @param B
   * @return 
   */
  protected Boolean equal(PolygonPoint A,PolygonPoint B) {
    double e = 1e8; //Epsilon
    return (Math.abs(A.getX()-B.getX())<e)&&(Math.abs(A.getX()-B.getX())<e)&&(Math.abs(A.getX()-B.getX())<e);
  }
  
  /**
   * Make some sides from a ring which is extruded up by HeightMetres. This includes outer
   * and inner rings. Copy of C++ ExtrudeGeometry.
   * @param isClockwise
   * @param ring
   * @param HeightMetres 
   */
  protected void extrudeSidesFromRing(Boolean isClockwise,LineString ring,float heightMetres) {
    Coordinate SP0=ring.getCoordinateN(0); //need to keep the spherical lat/lon coords and the cartesian coords
	PolygonPoint P0 = toVector(Math.toRadians(SP0.x),Math.toRadians(SP0.y),0);
	for (Coordinate coord : ring.getCoordinates()) {
		Coordinate SP1=coord;
		PolygonPoint P1 = toVector(Math.toRadians(SP1.x),Math.toRadians(SP1.y),0);
		//is this an epsilon check? yes it is now
		if (!equal(P0,P1)) //OK, so skipping the first point like this isn't great programming
		{
			PolygonPoint P2 = toVector(Math.toRadians(SP1.x),Math.toRadians(SP1.y),heightMetres);
			PolygonPoint P3 = toVector(Math.toRadians(SP0.x),Math.toRadians(SP0.y),heightMetres);
            if (isClockwise)
			{
				//glm::vec3 N = glm::cross(P1-P0,P3-P0);
				//geom.AddFace(P1,P0,P3,green,green,green,N,N,N);
				//geom.AddFace(P1,P3,P2,green,green,green,N,N,N);
              //PolygonPoint N = cross(
              //        new PolygonPoint(P1.getX()-P0.getX(),P1.getY()-P0.getY(),P1.getZ()-P0.getZ()),
              //        new PolygonPoint(P3.getX()-P0.getX(),P3.getY()-P0.getY(),P3.getZ()-P0.getZ())
              //        );
              PolygonPoint N = computeNormal(P1,P0,P3);
              int idx=points.size();
              points.add(P0); normals.add(N);
              points.add(P1); normals.add(N);
              points.add(P2); normals.add(N);
              points.add(P3); normals.add(N);
              faces.add(idx+1); faces.add(idx+0); faces.add(idx+3);
              faces.add(idx+1); faces.add(idx+3); faces.add(idx+2);
			}
			else {
				//glm::vec3 N = glm::cross(P3-P0,P1-P0);
				//geom.AddFace(P3,P0,P1,green,green,green,N,N,N);
				//geom.AddFace(P2,P3,P1,green,green,green,N,N,N);
              //PolygonPoint N = cross(
              //        new PolygonPoint(P3.getX()-P0.getX(),P3.getY()-P0.getY(),P3.getZ()-P0.getZ()),
              //        new PolygonPoint(P1.getX()-P0.getX(),P1.getY()-P0.getY(),P1.getZ()-P0.getZ())
              //        );
              PolygonPoint N = computeNormal(P3,P0,P1);
              int idx=points.size();
              points.add(P0); normals.add(N);
              points.add(P1); normals.add(N);
              points.add(P2); normals.add(N);
              points.add(P3); normals.add(N);
              faces.add(idx+3); faces.add(idx+0); faces.add(idx+1);
              faces.add(idx+2); faces.add(idx+3); faces.add(idx+1);
			}
		}
		SP0=SP1;
		P0=P1;
	}
  }
  
  /**
   * Ensures a linestring only contains unique points (within a hardcoded epsilon)
   * @param points
   * @return 
   */
  public LineString uniquePoints(LineString originalPoints) {
    ArrayList<Coordinate> uniqueCoords = new ArrayList<Coordinate>();
    HashSet<String> uniqueKeys = new HashSet<String>(); //contains a hash of coordinates
    Coordinate coords[] = originalPoints.getCoordinates();
    for (Coordinate coord : coords) {
      double deci = 100000; //1,000000 GeoGL uses 10,000,000
      long x=(long)Math.floor(coord.x*deci);
      long y=(long)Math.floor(coord.y*deci);
      long z=(long)Math.floor(coord.z*deci);
      String key = x+"_"+y; //+"_"+z;
      if (!uniqueKeys.contains(key)) {
        uniqueCoords.add(new Coordinate(((double)x)/deci,((double)y)/deci,((double)z)/deci));
        uniqueKeys.add(key);
      }
    }
    Coordinate ucoords[] = uniqueCoords.toArray(new Coordinate[uniqueCoords.size()]);
    if (ucoords.length<3) {
      System.out.println("Degenerate unique LineString");
    }
    //very slow point distance check
    //System.out.println("uniquePoints distance check");
    //for (int i=0; i<ucoords.length-1; i++) {
    //  for (int j=i+1; j<ucoords.length; j++) {
    //    double dist = ucoords[i].distance(ucoords[j]);
    //    System.out.println("ucoords dist="+dist);
    //  }
    //}
    //System.out.println("uniquePoints: in="+originalPoints.getNumPoints()+" out="+ucoords.length);
    LineString uniqueLineString = originalPoints.getFactory().createLineString(ucoords);
    return uniqueLineString;
  }
  
  /**
   * Compute a normal from 3 planar face points. Return normalised vector.
   * @param P0
   * @param P1
   * @param P2
   * @return 
   */
  public PolygonPoint computeNormal(PolygonPoint P0,PolygonPoint P1,PolygonPoint P2) {
    PolygonPoint N = cross(
            new PolygonPoint(P0.getX()-P1.getX(),P0.getY()-P1.getY(),P0.getZ()-P1.getZ()),
            new PolygonPoint(P2.getX()-P1.getX(),P2.getY()-P1.getY(),P2.getZ()-P1.getZ())
            );
    //now normalise it
    double x=N.getX(), y=N.getY(), z=N.getZ();
    double mag = Math.sqrt(x*x+y*y+z*z);
    N.set(x/mag, y/mag, z/mag);
    return N;
  }
  
  //TODO: take a feature and extrude a set of points, normals and faces as a 3d object
  public void makeOBJTile(File file,int tileX,int tileY,Geometry tileGeom,FeatureCollection fc,MathTransform trans) {
    //copy of makeGeoJSONTile, but writes out a triangulated OBJ file instead in Cartesian coords
    System.out.println("writing tile "+tileX+","+tileY+": "+file.getAbsolutePath());
    
    points = new ArrayList<PolygonPoint>();
    normals = new ArrayList<PolygonPoint>();
    faces = new ArrayList<Integer>();
    
    SimpleFeatureIterator fIT = (SimpleFeatureIterator)fc.features();
    try {
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));
      //you can do this on one go as below by writing the entire feature collection,
      //but I need to fix the geometry of each feature as I go along
      //fjson.writeFeatureCollection(fsShape, bw); //easy way
      
      //write preamble for a feature collection - needed if we have to write features manually
      bw.write("#tile "+tileX+" "+tileY); bw.newLine();
    
      while (fIT.hasNext())
      {
        //todo: need to check whether feature fits bounding box here
        SimpleFeature feature = fIT.next();
        //do the reprojection
        Geometry geometry = (Geometry) feature.getDefaultGeometry();
        try {
          Geometry transGeom = JTS.transform(geometry, trans);
          if (tileGeom.intersects(transGeom)) {
            //if (fixGeometry)
            //  transGeom = FortyTwoGeometry.fixInvalidGeometry(transGeom);
            
            //create base ring and top ring here (extruded), with sides
            
            //OK, I'm only doing multipolygon for now
            //String geomtype = transGeom.getGeometryType();
            //System.out.println("geomtype="+geomtype);
            if (transGeom.getGeometryType().equals("MultiPolygon")) {
              for (int N=0; N<transGeom.getNumGeometries(); N++) {
                System.out.println("----POLYGON FID="+feature.getID()+" N="+N+"----");
                Polygon polygonN = (Polygon)transGeom.getGeometryN(N);
            
                //create two rings (one extruded from base) and triangulate top
                LineString outer = polygonN.getExteriorRing();
                extrudeSidesFromRing(true,outer,100); //TODO: 100 is the height //HEIGHT HEIGHT HEIGHT
                //for all holes: extrude an anticlockwise side
                for (int i=0; i<polygonN.getNumInteriorRing(); i++) {
                  extrudeSidesFromRing(false,polygonN.getInteriorRingN(i),100); //HEIGHT HEIGHT HEIGHT //TODO: make sure sides and top have same height
                }
                //OK, that's created all the side walls (internal and external), so move on to the top
            
                //for the top, we need to make a polygon of the outer ring, then add the holes to it
                //copy outer points in, but using a unique version of the outer ring this time where duplicate points are removed
                LineString uniqueouter = uniquePoints(polygonN.getExteriorRing());
                ArrayList<PolygonPoint> ring = new ArrayList<PolygonPoint>(uniqueouter.getNumPoints());
                for (int i=0; i<uniqueouter.getNumPoints(); i++) {
                  Coordinate coord = uniqueouter.getCoordinateN(i);
                  ring.add(new PolygonPoint(coord.x,coord.y,0));
                }
                //create polygon
                PolygonSet ps = new PolygonSet();
                org.poly2tri.geometry.polygon.Polygon poly = new org.poly2tri.geometry.polygon.Polygon(ring);
                //add holes to poly
                for (int i=0; i<polygonN.getNumInteriorRing(); i++) {
                  LineString uniqueinner = uniquePoints(polygonN.getInteriorRingN(i)); //unique points version of inner ring for triangulation
                  ArrayList<PolygonPoint> holepts = new ArrayList<PolygonPoint>(uniqueinner.getNumPoints());
                  for (int j=0; j<uniqueinner.getNumPoints(); j++) {
                    Coordinate coord = uniqueinner.getCoordinateN(j);
                    holepts.add(new PolygonPoint(coord.x,coord.y,0));
                  }
                  org.poly2tri.geometry.polygon.Polygon hole = new org.poly2tri.geometry.polygon.Polygon(holepts);
                  poly.addHole(hole);
                }
                //add polygon to polygon set
                ps.add(poly);
                try {
                  Poly2Tri.triangulate(ps); //note triangulation being done on WGS84 points
                  for( org.poly2tri.geometry.polygon.Polygon p : ps.getPolygons() )
                  {
                    HashMap<TriangulationPoint,Integer> pointMap = new HashMap<TriangulationPoint,Integer>(); //mapping between Triangulation points and mesh indexes
                    List<DelaunayTriangle> tris = p.getTriangles();
                    for (DelaunayTriangle tri : tris) {
                      for (int f=0; f<3; f++) {
                        TriangulationPoint triP = tri.points[f];
                        //faces add index NOTE: tri.index(A) only gives you 0..2
                        Integer ix = pointMap.get(triP);
                        if (ix==null) { //point doesn't exist, so create a new one
                          ix=new Integer(points.size()); //zero based index
                          points.add(toVector(triP.getX(),triP.getY(),100)); //HEIGHT HEIGHT HEIGHT!!!!
                          PolygonPoint VN = computeNormal(
                                    toVector(tri.points[0].getX(),tri.points[0].getY(),100), //HEIGHT HEIGHT HEIGHT!!!!
                                    toVector(tri.points[1].getX(),tri.points[1].getY(),100),
                                    toVector(tri.points[2].getX(),tri.points[2].getY(),100)
                                  );
                          normals.add(VN);//UP vector
                          pointMap.put(triP,ix); //push the point object and index (which is zero based here)
                        }
                        faces.add(ix);
                      }
                    }
                  }
                }
                catch (Exception e) {
                  //this is a trap for any triangulation exceptions - need to write out the error, drop the polygon and continue
                  System.out.println("--------------------TRIANGULATION EXCEPTION");
                  System.out.println("File: "+file.getName());
                  for (TriangulationPoint p : poly.getPoints()) {
                    System.out.println("v "+(p.getX()+0.00782)*10000+" "+(p.getY()-50.8)*10000+" "+p.getZ());
                  }
                  System.out.print("f ");
                  int size = poly.pointCount();
                  for (int i=1; i<=size; i++) System.out.print(i+" ");
                  System.out.println();
                  System.out.println("--------------------END TRIANGULATION EXCEPTION");
                  System.out.println();
                }
            
              }
            }
          }
        }
        catch (org.opengis.referencing.operation.TransformException tex) {
          tex.printStackTrace();
        } 
      }
      
      //HACK - point scaling - this scales the points to a 0..100 cube for 3DS Max to be able to load them
      /*float minX=points.get(0).getXf(), maxX=points.get(0).getXf();
      float minY=points.get(0).getYf(), maxY=points.get(0).getYf();
      float minZ=points.get(0).getZf(), maxZ=points.get(0).getZf();
      for (PolygonPoint pp: points) {
        float x=pp.getXf();
        float y=pp.getYf();
        float z=pp.getZf();
        if (x<minX) minX=x;
        if (x>maxX) maxX=x;
        if (y<minY) minY=y;
        if (y>maxY) maxY=y;
        if (z<minZ) minZ=z;
        if (z>maxZ) maxZ=z;
      }
      float SX=100.0f/(maxX-minX);
      float SY=100.0f/(maxY-minY);
      float SZ=100.0f/(maxZ-minZ);
      */
      
      //and finally, the points, normals and faces should be set, we just have to create an OBJ file
      System.out.println("points="+points.size()+" faces="+faces.size());
      for (PolygonPoint pp: points) {
        //float x=pp.getXf();
        //float y=pp.getYf();
        //float z=pp.getZf();
        //x=(x-minX)*SX; y=(y-minY)*SY; z=(z-minZ)*SZ;
        //bw.write("v "+x+" "+y+" "+z); bw.newLine();
        bw.write("v "+pp.getX()+" "+pp.getY()+" "+pp.getZ()); bw.newLine();
        //bw.flush();
      }
      for (PolygonPoint np: normals) {
        bw.write("vn "+np.getX()+" "+np.getY()+" "+np.getZ()); bw.newLine();
        //bw.flush();
      }
      //note faces are areo based in the faces array, so add 1 to everything for OBJ 1 based
      for (int i=0; i<faces.size(); i+=3) {
        int i0=faces.get(i), i1=faces.get(i+1), i2=faces.get(i+2);
        bw.write("f "+(i0+1)+" "+(i1+1)+" "+(i2+1)); bw.newLine();
        //bw.flush();
      }
      
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
