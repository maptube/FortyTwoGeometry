/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fortytwogeometry;

/**
 *
 * @author richard
 * Stores an image filename and geographic extents (and position) for tiling
 */
public class ImageRecord {
    public String filename;
    public int minX,minY,maxX,maxY;
    public ImageRecord(final String imageFilename,final int minX,final int minY,final int maxX,final int maxY) {
        this.filename=imageFilename;
        this.minX=minX;
        this.minY=minY;
        this.maxX=maxX;
        this.maxY=maxY;
    }
}
