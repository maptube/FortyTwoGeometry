/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fortytwogeometry;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
//import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;

/**
 *
 * @author richard
 */
public class ColladaWriter {
  private SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private BufferedWriter bw;

  //use this to store the default shape geometry
  private class SimpleGeometry {
    float pts[][]; float normals[][]; int faces[][];
    SimpleGeometry(float pts[][],int faces[][], float normals[][]) { this.pts=pts; this.faces=faces; this.normals=normals; }
  }
  private Hashtable<String,SimpleGeometry> shapeGeometry=new Hashtable<String,SimpleGeometry>();
  
  //scene objects
  private ArrayList<String> nodeNames = new ArrayList<String>(); //plain text name in the scene
  private ArrayList<String> nodeShapes = new ArrayList<String>(); //links to geometry name in addGeometry

  public ColladaWriter(String filename) {
    //initialise the shape geometry hashtable
    //default is a turtle triangle
    //shapeGeometry.put("default", new SimpleGeometry(defaultTurtlePts,defaultTurtleFaces) );
    //shapeGeometry.put("circle", new SimpleGeometry(circlePts,circleFaces) );
    //shapeGeometry.put("circle 2", new SimpleGeometry(circle2Pts,circle2Faces) );
    
    //not much of a constructor!
  }
  
  /**
   * Add a geometry which will be written out later. Simple points and faces indexes.
   * @param name
   * @param pts
   * @param faces
   * @param normals
   */
  public void addGeometry(String name,float pts[][], int faces[][], float normals[][]) {
    //add to shapeGeometry
    shapeGeometry.put(name,new SimpleGeometry(pts,faces,normals));
  }
  
  /**
   * Add an object to the scene graph. This links a scene graph name to the name
   * of the geometry used to represent the shape.
   * @param objectName Plain text name of object
   * @param geometryName Name linking to the one used as the name in addGeometry
   */
  public void addSceneObject(String objectName, String geometryName) {
    nodeNames.add(objectName);
    nodeShapes.add(geometryName);
  }
  
  /**
   * Use this to write out everything to the file
   * @param filename 
   */
  public void writeFile(String filename) {
    try {
      bw=new BufferedWriter(new FileWriter(filename));
      bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"); bw.newLine();
      //bw.write("<COLLADA xmlns=\"http://www.collada.org/2008/03/COLLADASchema\" version=\"1.5.0\">"); bw.newLine();
      //switch to Collada version 1.4 which Max supports to avoid the weakly supported import warning when using 1.5
      bw.write("<COLLADA xmlns=\"http://www.collada.org/2005/11/COLLADASchema\" version=\"1.4.0\">"); bw.newLine();
      writeAssets(new Date(),new Date());
      
      writeGeometries();
      writeScene(nodeNames,nodeShapes);
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void close() {
    try {
      bw.write("</COLLADA>"); bw.newLine();
      bw.close();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void writeAssets(Date createDate, Date modifiedDate) {
    try {
      bw.write("<asset>"); bw.newLine();
      bw.write("<contributor>"); bw.newLine();
      bw.write("<author>CASA</author>"); bw.newLine();
      bw.write("<authoring_tool>CASA Collada Writer</authoring_tool>"); bw.newLine();
      bw.write("<comments></comments>"); bw.newLine();
      bw.write("</contributor>"); bw.newLine();

      bw.write("<created>"+df.format(createDate)+"</created>"); bw.newLine();
      bw.write("<modified>"+df.format(modifiedDate)+"</modified>"); bw.newLine();
      bw.write("<revision>1.0</revision>"); bw.newLine();
      bw.write("<up_axis>Z_UP</up_axis>"); bw.newLine();
      //bw.write("<unit meter=\"0.01\" name=\"centimeter\"/>"); bw.newLine();
      bw.write("</asset>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void writeEffects() {
    //<library_effects>
  }

  public void writeMaterials() {
    try {
      //bw.write("<library_materials>"); bw.newLine();
      //bw.write("<material id=\"whiteMaterial\">"); bw.newLine();
      //bw.write("<instance_effect url=\"#whitePhong\"/>"); bw.newLine();
      //bw.write("</material>"); bw.newLine();
      //bw.write("</library_materials>"); bw.newLine();

      //need a phong effect for this
      bw.write("<library_materials>"); bw.newLine();
      bw.write("<material id=\"Blue\">"); bw.newLine();
      bw.write("<instance_effect url=\"#phongEffect\">"); bw.newLine();
      bw.write("<setparam ref=\"AMBIENT\">"); bw.newLine();
      bw.write("<float3>0.0 0.0 0.1</float3>"); bw.newLine();
      bw.write("</setparam>"); bw.newLine();
      bw.write("<setparam ref=\"DIFFUSE\">"); bw.newLine();
      bw.write("<float3>0.15 0.15 0.1</float3>"); bw.newLine();
      bw.write("</setparam>"); bw.newLine();
      bw.write("<setparam ref=\"SPECULAR\">"); bw.newLine();
      bw.write("<float3>0.5 0.5 0.5</float3>"); bw.newLine();
      bw.write("</setparam>"); bw.newLine();
      bw.write("<setparam ref=\"SHININESS\">"); bw.newLine();
      bw.write("<float>16.0</float>"); bw.newLine();
      bw.write("</setparam>"); bw.newLine();
      bw.write("</instance_effect>"); bw.newLine();
      bw.write("</material>"); bw.newLine();
      bw.write("</library_materials>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void writeGeometries() {
    //generate all the geometries required for this model capture e.g. turtle and circle
    //so iterate through everything in the shapeGeometries hashtable
    try {
    //<library_geometries>
    //this is the mesh bit...
      bw.write("<library_geometries>"); bw.newLine();
      //writeBox();
      //writeTurtleGeometry();
      //writeGeometry("turtle",defaultTurtlePts,defaultTurtleFaces);
      //writeGeometry("circle",circlePts,circleFaces);
      int count=0;
      for (Enumeration<String> shapes=shapeGeometry.keys(); shapes.hasMoreElements(); ) {
        String shape=shapes.nextElement();
        SimpleGeometry geom=shapeGeometry.get(shape);
        writeGeometry(shape,geom);
      }
      bw.write("</library_geometries>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void writeBox() {
    //write out a basic box object - mainly for testing, superceded by the points/faces version
    try {
      bw.write("<geometry id=\"box\" name=\"box\">"); bw.newLine();
      bw.write("<mesh>"); bw.newLine();
      bw.write("<source id=\"box-Pos\">"); bw.newLine();
      bw.write("<float_array id=\"box-Pos-array\" count=\"24\">"); bw.newLine();
      bw.write("-0.5 0.5 0.5"); bw.newLine();
      bw.write("0.5 0.5 0.5"); bw.newLine();
      bw.write("-0.5 -0.5 0.5"); bw.newLine();
      bw.write("0.5 -0.5 0.5"); bw.newLine();
      bw.write("-0.5 0.5 -0.5"); bw.newLine();
      bw.write("0.5 0.5 -0.5"); bw.newLine();
      bw.write("-0.5 -0.5 -0.5"); bw.newLine();
      bw.write("0.5 -0.5 -0.5"); bw.newLine();
      bw.write("</float_array>"); bw.newLine();
      bw.write("<technique_common>"); bw.newLine();
      bw.write("<accessor source=\"#box-Pos-array\" count=\"8\" stride=\"3\">"); bw.newLine();
      bw.write("<param name=\"X\" type=\"float\" />"); bw.newLine();
      bw.write("<param name=\"Y\" type=\"float\" />"); bw.newLine();
      bw.write("<param name=\"Z\" type=\"float\" />"); bw.newLine();
      bw.write("</accessor>"); bw.newLine();
      bw.write("</technique_common>"); bw.newLine();
      bw.write("</source>"); bw.newLine();
      bw.write("<vertices id=\"box-Vtx\">"); bw.newLine();
      bw.write("<input semantic=\"POSITION\" source=\"#box-Pos\"/>"); bw.newLine();
      bw.write("</vertices>"); bw.newLine();
      //bw.write("<polygons count=\"6\" material=\"WHITE\">"); bw.newLine();
      bw.write("<polygons count=\"6\">"); bw.newLine();
      bw.write("<input semantic=\"VERTEX\" source=\"#box-Vtx\" offset=\"0\"/>"); bw.newLine();
      //bw.write("<input semantic=\"NORMAL\" source=\"#box-0-Normal\" offset=\"1\"/>"); bw.newLine();
      bw.write("<p>0 2 3 1</p>"); bw.newLine();
      bw.write("<p>0 1 5 4</p>"); bw.newLine();
      bw.write("<p>6 7 3 2</p>"); bw.newLine();
      bw.write("<p>0 4 6 2</p>"); bw.newLine();
      bw.write("<p>3 7 5 1</p>"); bw.newLine();
      bw.write("<p>5 7 6 4</p>"); bw.newLine();
      bw.write("</polygons>"); bw.newLine();
      bw.write("</mesh>"); bw.newLine();
      bw.write("</geometry>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  //public void writeTurtleGeometry() {
  //  writeGeometry("turtle",defaultTurtlePts,defaultTurtleFaces);
  //}

  /**
   * Write out geometry points, normals and faces. Pass normals=null to skip them.
   * @param name
   * @param geom 
   */
  public void writeGeometry(String name,SimpleGeometry geom) {
    //Write out generalised point and face geometry e.g. for turtle shapes
    //This writes out a library mesh which can be referenced in the scene
    //NOTE: we're using xy as the ground plane and z as height, so assuming stride=3
    float points[][]=geom.pts;
    int faces[][]=geom.faces;
    float normals[][]=geom.normals; //per face normals
    int pointCount=points.length;
    int faceCount=faces.length;
    int normalCount=0;
    if (normals!=null) normalCount = normals.length;
    try {
      bw.write("<geometry id=\""+name+"-lib\" name=\""+name+"Mesh\">"); bw.newLine();
      bw.write("<mesh>"); bw.newLine();
      
      //POINTS
      bw.write("<source id=\""+name+"-lib-Pos\">"); bw.newLine();
      bw.write("<float_array id=\""+name+"-lib-Pos-array\" count=\""+(pointCount*3)+"\">"); bw.newLine();
      for (int i=0; i<pointCount; i++) {
        bw.write(String.format("%f %f %f",points[i][0],points[i][1],points[i][2]));
        bw.newLine();
      }
      bw.write("</float_array>"); bw.newLine();
      bw.write("<technique_common>"); bw.newLine();
      bw.write("<accessor source=\"#"+name+"-lib-Pos-array\" count=\""+pointCount+"\" stride=\"3\">"); bw.newLine();
      bw.write("<param name=\"X\" type=\"float\" />"); bw.newLine();
      bw.write("<param name=\"Y\" type=\"float\" />"); bw.newLine();
      bw.write("<param name=\"Z\" type=\"float\" />"); bw.newLine();
      bw.write("</accessor>"); bw.newLine();
      bw.write("</technique_common>"); bw.newLine();
      bw.write("</source>"); bw.newLine();
      
      //NORMALS
      if (normals!=null) {
        bw.write("<source id=\""+name+"-lib-Normal\">"); bw.newLine();
        bw.write("<float_array id=\""+name+"-lib-Normal-array\" count=\""+(normalCount*3)+"\">"); bw.newLine();
        for (int i=0; i<normalCount; i++) {
          bw.write(String.format("%f %f %f",normals[i][0],normals[i][1],normals[i][2]));
          bw.newLine();
        }
        bw.write("</float_array>"); bw.newLine();
        bw.write("<technique_common>"); bw.newLine();
        bw.write("<accessor source=\"#"+name+"-lib-Normal-array\" count=\""+normalCount+"\" stride=\"3\">"); bw.newLine();
        bw.write("<param name=\"X\" type=\"float\" />"); bw.newLine();
        bw.write("<param name=\"Y\" type=\"float\" />"); bw.newLine();
        bw.write("<param name=\"Z\" type=\"float\" />"); bw.newLine();
        bw.write("</accessor>"); bw.newLine();
        bw.write("</technique_common>"); bw.newLine();
        bw.write("</source>"); bw.newLine();
      }
      
      //VERTICES
      bw.write("<vertices id=\""+name+"-Vtx\">"); bw.newLine();
      bw.write("<input semantic=\"POSITION\" source=\"#"+name+"-lib-Pos\"/>"); bw.newLine();
      bw.write("</vertices>"); bw.newLine();
      //bw.write("<polygons count=\"6\" material=\"WHITE\">"); bw.newLine();
      bw.write("<polygons count=\""+faceCount+"\">"); bw.newLine();
      bw.write("<input semantic=\"VERTEX\" source=\"#"+name+"-lib-Pos\" offset=\"0\"/>"); bw.newLine();
      if (normals!=null)
        bw.write("<input semantic=\"NORMAL\" source=\"#"+name+"-lib-Normal\" offset=\"1\"/>"); bw.newLine();
      //bw.write("<p>0 2 3 1</p>"); bw.newLine();
      //bw.write("<p>0 1 5 4</p>"); bw.newLine();
      //bw.write("<p>6 7 3 2</p>"); bw.newLine();
      //bw.write("<p>0 4 6 2</p>"); bw.newLine();
      //bw.write("<p>3 7 5 1</p>"); bw.newLine();
      //bw.write("<p>5 7 6 4</p>"); bw.newLine();
      for (int f=0; f<faceCount; f++) { //this writes out face index and per face normal index
        bw.write("<p>");
        for (int i=0; i<faces[f].length; i++) {
          if (normals!=null) bw.write(faces[f][i]+" "+f+" "); //normal is always index f as it matches the faces
          else bw.write(faces[f][i]+" "); //just the face number
        }
        bw.write("</p>"); bw.newLine();
      }
      bw.write("</polygons>"); bw.newLine();
      bw.write("</mesh>"); bw.newLine();
      bw.write("</geometry>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void startLibraryAnimations() {
    try {
      bw.write("<library_animations>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void finishLibraryAnimations() {
    try {
      bw.write("</library_animations>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void writeAnimation(String name,int initialFrameNum,int frameStep,List<ColladaMatrix> frames) {
    //called repeatedly for every animation sequence in the file e.g.
    //once for every turtle
    //name is the name of the turtle while frames is a list of the ColladaMatrix
    //for each frame of the sequence containing the orientation and position
    //initialFrameNum is the number of the first frame of this sequence as animations
    //don't necessarily start from the beginning if the turtle hasn't been born yet.
    //It is assumed that if initialFrameNum>0, then a scale zero frame has been
    //added at the start.
    //frameStep is the multiplier applied to the frames in the frames list e.g.
    //frameStep=2 means creates frames at frame=0,2,4,6,8...
    //It's basically the frame sampling rate to apply time scaling so you don't
    //have to sample every single frame.
    //NOTE: visibility (i.e. birth/death) is handled through the scaling on
    //the matrix - it appears that while the Max FBX exporter will export a
    //visibility channel, it won't import it. The nextGen exporter/importer doesn't
    //work at all.

    float timeStep=1/30.0f; //30 frames per second
    //int frameCount=50; //number of frames in the animation
    int frameCount=frames.size();
    //and pass in an array of frameCount x 4x4 matrices from logo...
    //String name="turtle1"; //need to pass these in as well...

    try {
      bw.write("<animation id=\""+name+"-anim\" name=\""+name+"\">"); bw.newLine();

      bw.write("<animation>"); bw.newLine(); //why do you have to nest animation tags?
      //format is Source1 (time), Source2 (matrix), Source3 (interpolations), Sampler, Channel

      //-Matrix-animation-input
      //These are the keyframe time values
      bw.write("<source id=\""+name+"-Matrix-animation-input\">"); bw.newLine();
        bw.write("<float_array id=\""+name+"-Matrix-animation-input-array\" count=\""+frameCount+"\">"); bw.newLine();
          for (int i=0; i<frameCount; i++)
            bw.write(Float.toString((i*frameStep+initialFrameNum)*timeStep)+" "); //could set a max line length here
          bw.newLine();
        bw.write("</float_array>"); bw.newLine();
        bw.write("<technique_common>"); bw.newLine();
          //source in line below points at float array block above
          bw.write("<accessor source=\"#"+name+"-Matrix-animation-input-array\" count=\""+frameCount+"\">"); bw.newLine();
            bw.write("param name=\"TIME\" type=\"float\"/>"); bw.newLine();
          bw.write("</accessor>"); bw.newLine();
        bw.write("</technique_common>"); bw.newLine();
      bw.write("</source>"); bw.newLine();

      //-Matrix-animation-output-transform
      //This is one animation matrix for every keyframe
      bw.write("<source id=\""+name+"-Matrix-animation-output-transform\">"); bw.newLine();
        bw.write("<float_array id=\""+name+"-Matrix-animation-output-transform-array\" count=\""+frameCount*16+"\">"); bw.newLine();
          //you could check frames.size() against frameCount but this is what it was set from earlier
          for (Iterator<ColladaMatrix> it=frames.listIterator(); it.hasNext(); ) {
            ColladaMatrix mat=it.next();
            bw.write(mat.toString()); bw.newLine();
          }

          //ColladaMatrix mat=new ColladaMatrix();
          //float heading=0;
          //for (int i=0; i<frameCount; i++) {
          //  //bw.write("1.0 0.0 0.0 0.0  0.0 1.0 0.0 0.0  0.0 0.0 1.0 0.0  0.0 0.0 0.0 1.0"); bw.newLine();
          //  mat.setHeading(heading); heading+=7.2f;
          //  bw.write(mat.toString()); bw.newLine();
          //  //mat.translate(0.0f,0.1f,0.0f);
          //}
        bw.write("</float_array>"); bw.newLine();
        bw.write("<technique_common>"); bw.newLine();
          bw.write("<accessor source=\"#"+name+"-Matrix-animation-output-transform-array\" count=\""+frameCount+"\" stride=\"16\">"); bw.newLine();
            bw.write("<param type=\"float4x4\"/>"); bw.newLine();
          bw.write("</accessor>"); bw.newLine();
        bw.write("</technique_common>"); bw.newLine();
      bw.write("</source>"); bw.newLine();

      //-Interpolations
      //This is one interpolation type for every keyframe e.g. LINEAR
      //TODO: can you drop this?
      bw.write("<source id=\""+name+"-Interpolations\">"); bw.newLine();
        bw.write("<Name_array id=\""+name+"-Interpolations-array\" count=\""+frameCount+"\">"); bw.newLine();
          for (int i=0; i<frameCount; i++)
            bw.write("LINEAR ");
          bw.newLine();
        bw.write("</Name_array>"); bw.newLine();
        bw.write("<technique_common>"); bw.newLine();
          bw.write("<accessor source=\"#"+name+"-Interpolations-array\" count=\""+frameCount+"\">"); bw.newLine();
            bw.write("<param type=\"name\"/>"); bw.newLine();
          bw.write("</accessor>");
        bw.write("</technique_common>"); bw.newLine();
      bw.write("</source>"); bw.newLine();

      //sampler -Matrix-animation-transform
      bw.write("<sampler id=\""+name+"-Matrix-animation-transform\">"); bw.newLine();
        bw.write("<input semantic=\"INPUT\" source=\"#"+name+"-Matrix-animation-input\"/>"); bw.newLine();
        bw.write("<input semantic=\"OUTPUT\" source=\"#"+name+"-Matrix-animation-output-transform\"/>"); bw.newLine();
        bw.write("<input semantic=\"INTERPOLATION\" source=\"#"+name+"-Interpolations\"/>"); bw.newLine();
      bw.write("</sampler>"); bw.newLine();

      //channel
      bw.write("<channel source=\"#"+name+"-Matrix-animation-transform\" target=\""+name+"/matrix\"/>"); bw.newLine();

      bw.write("</animation>"); bw.newLine(); //close inner animation tag


      bw.write("</animation>"); bw.newLine(); //close outer animation tag
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public void writeScene(List<String> sceneNames,List<String> sceneShapes) {
    //sceneNames is a list of object names in the scene e.g. turtle0, turtle1 etc
    //sceneShapes is a corresponding list of strings giving the object shape e.g.
    //the geometry - turtle, circle, circle 2
    try {
      bw.write("<library_visual_scenes>"); bw.newLine();
      bw.write("<visual_scene id=\"DefaultScene\">"); bw.newLine();
      Iterator<String> itNames=sceneNames.listIterator();
      Iterator<String> itShapes=sceneShapes.listIterator();
      while (itNames.hasNext()&&itShapes.hasNext()) {
        String name=itNames.next();
        String shape=itShapes.next();
        bw.write("<node id=\""+name+"\" name=\""+name+"\">"); bw.newLine();
        //bw.write("<matrix sid=\"matrix\">"); bw.newLine();
        //bw.write("1.0 0.0 0.0 1.0  0.0 0.0 1.0 0.0  0.0 1.0 0.0 1.0  0.0 0.0 0.0 1.0"); bw.newLine();
        //bw.write("</matrix>"); bw.newLine();
        //bw.write("<instance_geometry url=\"#turtle-lib\">"); bw.newLine();
        bw.write("<instance_geometry url=\"#"+shape+"-lib\">"); bw.newLine();
        bw.write("</instance_geometry>"); bw.newLine();
        bw.write("</node>"); bw.newLine();
      }
      bw.write("</visual_scene>"); bw.newLine();
      bw.write("</library_visual_scenes>"); bw.newLine();
      bw.write("<scene>"); bw.newLine();
      bw.write("<instance_visual_scene url=\"#DefaultScene\"/>"); bw.newLine();
      bw.write("</scene>"); bw.newLine();
    }
    catch (java.io.IOException ioe) {
      ioe.printStackTrace();
    }
  }

}
