/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package fortytwogeometry;

/**
 *
 * @author Richard
 */
public class ColladaMatrix {
  float m[][]={ {1.0f,0.0f,0.0f,0.0f}, {0.0f,1.0f,0.0f,0.0f}, {0.0f,0.0f,1.0f,0.0f}, {0.0f,0.0f,0.0f,1.0f} };
    public void translate(float dx,float dy,float dz) {
      m[0][3]+=dx; m[1][3]+=dy; m[2][3]+=dz;
    }
    public void setXYZ(float x,float y,float z) {
      m[0][3]=x; m[1][3]=y; m[2][3]=z;
    }
    public void setHeading(float headingDegrees) {
      //Set the heading angle on the yx plane. The angle is given in degree.
      float theta=(float)Math.toRadians(headingDegrees);
      m[0][0]=(float)Math.cos(theta); m[0][1]=(float)Math.sin(theta);
      m[1][0]=-m[0][1];               m[1][1]=m[0][0];
    }
    public void scale(float scale) {
      m[0][0]*=scale; m[0][1]*=scale; m[0][2]*=scale;
      m[1][0]*=scale; m[1][1]*=scale; m[1][2]*=scale;
      m[2][0]*=scale; m[2][1]*=scale; m[2][2]*=scale;
      //we don't use the z line!
    }
    public String toString() {
      String result="";
      for (int row=0; row<4; row++) {
        if (row>0) result+="  ";
        for (int col=0; col<4; col++) {
          if (col>0) result+=" ";
          result+=m[row][col];
        }
      }
      return result;
    }

}
