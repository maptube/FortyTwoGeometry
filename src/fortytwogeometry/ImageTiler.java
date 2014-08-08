/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fortytwogeometry;

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
  private BufferedImage img=null;
  private String baseFilename;
  //TODO: add an octtree or quadtree recursion switch
  private Font tileFont = new Font("Serif", Font.BOLD, 32);
  
  public void loadImage() {
    try {
      //hack
      img = ImageIO.read(new File("C:\\richard\\GitHub\\GeoGL\\data\\BlueMarble\\land_ocean_ice_8192.png"));
    }
    catch (IOException e) {
    }
    baseFilename="land_ocean_ice_QUAD_"; //hack
  }
  
  public void tileImage(int depth,int tx,int ty) {
    int width = img.getWidth();
    int height = img.getHeight();
    
    //kick off recursion
    makeTiles(depth,0,0,0,0,0,width,height,tx,ty);
  }
  
  //depth is depth of tiles required. recursion stops at tileZ==depth
  //tileZ, tileX, tileY are the tile code
  //x0y0 is the image box top left in the source image
  //sx sy is the size of the tile at this depth
  //tx=tile size x, ty=tile size y
  private void makeTiles(int depth, int tileZ, int tileX, int tileY, float x0, float y0, float sx, float sy, int tx, int ty) {
    
    if (tileZ>=depth) return; //guard case
    
    try {
      BufferedImage tile = new BufferedImage(tx,ty,img.getType()); //BufferedImage.TYPE_INT_ARGB
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
      g2d.drawImage(img,
              0, 0, tx, ty, //destination
              //(int)x0, (int)y0, (int)(x0+sx), (int)(y0+sy), //source, absolute pixel box
              (int)x0, (int)(y0+sy), (int)(x0+sx), (int)y0, //source, absolute pixel box
              null);
      //tile outlines in green
      g2d.setColor(Color.GREEN);
      g2d.drawRect(0, 0, tx, ty);
      
      g2d.setColor(Color.BLACK);
      g2d.fillRect(0,0,128,32); //black box for tile text
      
      g2d.setColor(Color.WHITE); //tile number text in white
      
      //upside down text
      g2d.scale(1, -1); //going to throw the g2d away in a minute, so not point saving state
      g2d.drawString(tileZ+"_"+tileX+"_"+tileY, 0, -8); //text bottom left of tile here
      
      //right way up text (top left of tile)
      //g2d.drawString(tileZ+"_"+tileX+"_"+tileY, 0, 24); //note font drawing from the baseline
      
      g2d.dispose();
      
      //this is supposed to be a bad idea
      //BufferedImage tile = (BufferedImage)img.getScaledInstance(tx, ty, Image.SCALE_SMOOTH);
      
      //"jpg" or "png" for type
      ImageIO.write(tile, "jpg", new File("C:\\richard\\GitHub\\GeoGL\\data\\BlueMarble\\land_ocean_ice_QUAD_"+tileZ+"_"+tileX+"_"+tileY+".jpg"));
      
      //quadtree recursion
      float sizeX = sx/2;
      float sizeY = sy/2;
      //makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1),   x0,       y0,       sizeX, sizeY, tx, ty);
      //makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1)+1, x0,       y0+sizeY, sizeX, sizeY, tx, ty);
      //makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1)+1, x0+sizeX, y0+sizeY, sizeX, sizeY, tx, ty);
      //makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1),   x0+sizeX, y0,       sizeX, sizeY, tx, ty);
      //inverted tiles
      makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1)+1, x0,       y0,       sizeX, sizeY, tx, ty);
      makeTiles(depth, tileZ+1, (tileX<<1),   (tileY<<1),   x0,       y0+sizeY, sizeX, sizeY, tx, ty);
      makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1),   x0+sizeX, y0+sizeY, sizeX, sizeY, tx, ty);
      makeTiles(depth, tileZ+1, (tileX<<1)+1, (tileY<<1)+1, x0+sizeX, y0,       sizeX, sizeY, tx, ty);
      
      //octtree recursion
      //TODO:
    }
    catch (IOException e) {
    }

  }
  
  
}
