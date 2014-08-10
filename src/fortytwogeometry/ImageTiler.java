/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fortytwogeometry;

import java.util.ArrayList;
import java.awt.Dimension;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author richard
 */
public class ImageTiler {
  public boolean invertImage=false; //whether to invert the image - true for most texture mapping
  //holds the list of super tiles to build the tiling from
  private ArrayList<ImageRecord> superTiles = new ArrayList<ImageRecord>();
  private BufferedImage img=null; //this is the currently loaded image
  private ImageRecord imgRec; //record for the currently loaded image
  private int imgRun=0; //count which one of the supertiles we're currently on
  
  //TODO: need to improve the handling of the directories
  private String baseDir; //directroy where tiles will be created
  private String baseFilename; //pattern for the filenames of the tiles
  
  //TODO: add an octtree or quadtree recursion switch
  private Font tileFont = new Font("Serif", Font.BOLD, 32);
  
  public void loadImage(final ImageRecord imageRec) {
    try {
      //hack
      //img = ImageIO.read(new File("C:\\richard\\GitHub\\GeoGL\\data\\BlueMarble\\land_ocean_ice_8192.png"));
      //File f = new File("~/projects/github/GeoGL/data/BlueMarble/land_ocean_ice_8192.png");
      //File f = new File("/home/richard/projects/github/GeoGL/data/BlueMarble/land_ocean_ice_8192.png");
      //File f = new File("/home/richard/projects/github/GeoGL/data/BlueMarble/land_ocean_ice_QUAD_0_0_0.jpg");
      //if (f.exists()) {
      //  System.out.println("file exists");
      //}
      //img = ImageIO.read(f);
      //img = ImageIO.read(new File("~/projects/GitHub/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.A1.jpq"));
      System.out.println("Loading "+imageRec.filename);
      img=null; //hopefully garbage collect?
      img = ImageIO.read(new File(imageRec.filename));
      imgRec = imageRec;
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * convenience method for running the Blue Marble 500 m set
   */
  public void runBlueMarble500m() {
    baseDir = "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/"; //hack
    baseFilename="world_topo_QUAD_"; //hack
    invertImage=true;
    
    int size=21600;
    int X0=0, X1=size, X2=2*size, X3=3*size, X4=4*size;
    int Y0=0, Y1=size, Y2=2*size;
    //A1 tile (0,0)
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.A1.jpg",
            X0,Y0,X1-1,Y1-1
    );
    //A2 tile (0,1)
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.A2.jpg",
            X0,Y1,X1-1,Y2-1
    );
    //B1 tile (1,0)
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.B1.jpg",
            X1,Y0,X2-1,Y1-1
    );
    //B2 tile (1,1)
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.B2.jpg",
            X1,Y1,X2-1,Y2-1
    );
    //C1 tile (2,0)
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.C1.jpg",
            X2,Y0,X3-1,Y1-1
    );
    //C2 tile
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.C2.jpg",
            X2,Y1,X3-1,Y2-1
    );
    //D1 tile
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.D1.jpg",
            X3,Y0,X4-1,Y1-1
    );
    //D2 tile
    addSuperTile(
            "/home/richard/projects/github/GeoGL/data/BlueMarble/bluemarble_jan04/world.topo.200401.3x21600x21600.D2.jpg",
            X3,Y1,X4-1,Y2-1
    );
    //now do the tiling
    //superTileImage(3,1024,512);
    superTileImage(4,1024,512);
  }
  
  /**
   * Add a super tile along with its XY limits to define its location in the super tile grid set
   * @param filename
   * @param minX
   * @param minY
   * @param maxX
   * @param maxY 
   */
  public void addSuperTile(final String filename,final int minX,final int minY,final int maxX,final int maxY) {
    ImageRecord imgRecord = new ImageRecord(filename,minX,minY,maxX,maxY);
    superTiles.add(imgRecord);
  }
  
  /**
   * returns box fitting all current super tiles, which is the max image dimension for tiling
   */
  public Dimension getSuperTilesDimension() {
    Dimension D = new Dimension(0,0); //assumes (0,0) origin which is reasonable
    for (ImageRecord imgRecord : superTiles) {
      if (imgRecord.maxX>D.width) D.width=imgRecord.maxX+1;
      if (imgRecord.maxY>D.height) D.height=imgRecord.maxY+1;
    }
    //HACK!
    //D.width=21600*4;
    //D.height=21600*2;
    return D;
  }
  
  /**
   * return true if any part of the rectangle passed in params overlaps the imgRec one
   * @return 
   */
  public boolean overlaps(float x0,float y0,float x1,float y1) {
    //area = (max(r1.x1, r2.x1) - min(r1.x2, r2.x2)) * (max(r1.y1, r2.y1) - min(r1.y2, r2.y2));
    //return !(RectA.X1 < RectB.X2 && RectA.X2 > RectB.X1 &&
    //RectA.Y1 < RectB.Y2 && RectA.Y2 > RectB.Y1)
    return (imgRec.minX<x1 && imgRec.maxX>x0 && imgRec.minY<y1 && imgRec.maxY>y0);
  }
  
  /**
   * Go through all the super tiles and load and tile each one in turn
   */
  public void superTileImage(int depth,int tx,int ty) {
    imgRun=0; //initialise image run to zero, which is used to clear the tiles for first run
    for (ImageRecord imgRecord : superTiles) {
      loadImage(imgRecord);
      tileImage(depth,tx,ty);
      ++imgRun;
    }
  }
  
  public void tileImage(int depth,int tx,int ty) {
    //int width = img.getWidth();
    //int height = img.getHeight();
    Dimension dim = getSuperTilesDimension();
    int width=dim.width;
    int height=dim.height;
    
    //kick off recursion
    makeTiles(depth,0,0,0,0,0,width,height,tx,ty);
  }
  
  /**
   * Returns file name of tile with the following ZXY number
   * @param tileZ
   * @param tileX
   * @param tileY
   * @return 
   */
  public String getFilename(int tileZ,int tileX,int tileY) {
    //return "C:\\richard\\GitHub\\GeoGL\\data\\BlueMarble\\land_ocean_ice_QUAD_"+tileZ+"_"+tileX+"_"+tileY+".jpg";
    return baseDir+baseFilename+tileZ+"_"+tileX+"_"+tileY+".jpg";
  }
  
  //depth is depth of tiles required. recursion stops at tileZ==depth
  //tileZ, tileX, tileY are the tile code
  //x0y0 is the image box top left in the source image
  //sx sy is the size of the tile at this depth
  //tx=tile size x, ty=tile size y
  private void makeTiles(int depth, int tileZ, int tileX, int tileY, float x0, float y0, float sx, float sy, int tx, int ty) {
    
    if (tileZ>=depth) return; //guard case
    
    //TODO: check x0,y0,sx,sy for coverage and break recursion here if no visible portion
    //of supertile on this tile
    if (!overlaps(x0,y0,x0+sx,y0+sy)) return;
    
    try {
      BufferedImage tile;
      String tileFilename = getFilename(tileZ,tileX,tileY);
      File tileFile = new File(tileFilename);
      if ((imgRun>0)&&(tileFile.exists())) //so, don't load on the first super tile run - create new
        tile = ImageIO.read(tileFile);
      else
        tile = new BufferedImage(tx,ty,img.getType()); //BufferedImage.TYPE_INT_ARGB
      Graphics2D g2d = (Graphics2D)tile.getGraphics();
      g2d.setFont(tileFont);
      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
      //g2d.drawImage(img, 0, 0, tx,ty, null);
      
      //DEBUG - set background bright red!!!!
      //g2d.setColor(Color.RED);
      //g2d.fillRect(0,0,tx,ty);
      
      //right way up image
      //g2d.drawImage(img,
      //        0, 0, tx, ty, //destination
      //        (int)x0, (int)y0, (int)(x0+sx), (int)(y0+sy), //source, absolute pixel box
      //        null);
      
      //invert image
      //g2d.drawImage(img,
      //        0, 0, tx, ty, //destination
      //        //(int)x0, (int)y0, (int)(x0+sx), (int)(y0+sy), //source, absolute pixel box
      //        (int)x0, (int)(y0+sy), (int)(x0+sx), (int)y0, //source, absolute pixel box
      //        null);
      
      float src_x0,src_y0,src_x1,src_y1;
      //right way up image with supertiles
      if (!invertImage)
      {
        src_x0=x0-imgRec.minX;
        src_y0=y0-imgRec.minY;
        src_x1=x0-imgRec.minX+sx;
        src_y1=y0-imgRec.minY+sy;
      }
      else {
        //invert image with supertiles
        //x0,y0,x1,y1 are the box coords on the virtual canvas made from all the super tiles
        //this is the source box on the supertile
        src_x0=x0-imgRec.minX;
        src_y0=y0-imgRec.minY+sy;
        src_x1=x0-imgRec.minX+sx;
        src_y1=y0-imgRec.minY;
      }
      
      //this is the destination box on the small tile
      float dst_x0=0; //0
      float dst_y0=0; //0
      float dst_x1=tx; //tx
      float dst_y1=ty; //ty
      //clip offsets
      //if (src_x0<0) { //clip to left extent of supertile
      //  dst_x0=-src_x0/sx*tx;
      //  src_x0=0;
      //}
      //if (src_x1>imgRec.maxX) { //clip to right extent of supertile
      //  dst_x1=dst_x1-(src_x1-imgRec.maxX)/sx*tx;
      //  src_x1=imgRec.maxX;
      //}
      
      g2d.drawImage(img,
              (int)dst_x0, (int)dst_y0, (int)dst_x1, (int)dst_y1, //destination
              (int)src_x0, (int)src_y0, (int)src_x1, (int)src_y1, //source, absolute pixel box
              null);
      
      //tile outlines in green
      g2d.setColor(Color.GREEN);
      g2d.drawRect(0, 0, tx, ty);
      
      g2d.setColor(Color.BLACK);
      g2d.fillRect(0,0,128,32); //black box for tile text
      
      g2d.setColor(Color.WHITE); //tile number text in white
      
      if (!invertImage)
      {
        //right way up text (top left of tile)
        g2d.drawString(tileZ+"_"+tileX+"_"+tileY, 0, 24); //note font drawing from the baseline
      }
      else {
        //upside down text
        g2d.scale(1, -1); //going to throw the g2d away in a minute, so not point saving state
        g2d.drawString(tileZ+"_"+tileX+"_"+tileY, 0, -8); //text bottom left of tile here
      }
      
      g2d.dispose();
      
      //this is supposed to be a bad idea
      //BufferedImage tile = (BufferedImage)img.getScaledInstance(tx, ty, Image.SCALE_SMOOTH);
      
      //"jpg" or "png" for type
      //ImageIO.write(tile, "jpg", new File("C:\\richard\\GitHub\\GeoGL\\data\\BlueMarble\\land_ocean_ice_QUAD_"+tileZ+"_"+tileX+"_"+tileY+".jpg"));
      ImageIO.write(tile, "jpg", tileFile);
      
      //quadtree recursion
      float sizeX = sx/2;
      float sizeY = sy/2;
      if (!invertImage) {
        makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1),   x0,       y0,       sizeX, sizeY, tx, ty);
        makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1)+1, x0,       y0+sizeY, sizeX, sizeY, tx, ty);
        makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1)+1, x0+sizeX, y0+sizeY, sizeX, sizeY, tx, ty);
        makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1),   x0+sizeX, y0,       sizeX, sizeY, tx, ty);
      }
      else {
        //inverted tiles
        makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1)+1, x0,       y0,       sizeX, sizeY, tx, ty);
        makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1),   x0,       y0+sizeY, sizeX, sizeY, tx, ty);
        makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1),   x0+sizeX, y0+sizeY, sizeX, sizeY, tx, ty);
        makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1)+1, x0+sizeX, y0,       sizeX, sizeY, tx, ty);
      }
      
      //octtree recursion
      //TODO:
    }
    catch (IOException e) {
    }

  }
  
  
}
