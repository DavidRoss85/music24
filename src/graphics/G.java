package graphics;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class G{
    public static void fillBack(Graphics g){g.setColor(Color.WHITE); g.fillRect(0,0,3000,3000);}

    public static Random RND = new Random();
    public static int rnd(int max){return RND.nextInt(max);}
    //--------------These are from Tetris----------------
    public static V LEFT = new V(-1,0);
    public static V RIGHT = new V(1,0);
    public static V UP = new V(0,-1);
    public static V DOWN = new V(0,1);
    //-------------------------------------------------
    public static Color rndColor(){return new Color(rnd(256),rnd(256),rnd(256)); }



    //-----------------------V------------------------
    /**Point vector class*/
    public static class V implements Serializable {
        public static Transform T = new Transform();
        public int x,y;

        public V(int x, int y){this.set(x,y);}

        /**Set x and y to provided values
         * @param x as int
         * @param y as int
         */
        public void set(int x, int y){this.x = x; this.y = y;}

        /**
         * Set x and y using a V object
         * @param v as {@code V}
         */
        public void set(V v){x=v.x; y=v.y;}

        /**
         * Add vectors to create new vector
         * @param v
         */
        public void add(V v){x += v.x; y += v.y;} // vector addition

        public void blend(V v, int nBlend){
            set((nBlend*x + v.x)/(nBlend+1),(nBlend*y + v.y)/(nBlend+1));
        }

        //Transforms:
        /**
         * Set the x and y to transformed coordinates
         * @param v point vector as {@code V}
         */
        public void setT(V v){
            set(v.tx(),v.ty());
        }

        /**
         * Transform x by T's scale
         * @return transformed x coord
         */
        public int tx(){return x*T.n/T.d+T.dx;}
        /**
         * Transform y by T's scale
         * @return transformed y coord
         */
        public int ty(){return y*T.n/T.d+T.dy;}


        //----------------Transform-----------------
        /*** Used to transform points and coordinates*/
        public static class Transform{
            int dx, dy, n, d; //n and d represent a ratio old to new

            /**
             * Scales a rectangle while maintaining coords.
             * Sets dx and dy to new x,y coords
             * @param oVS old rect
             * @param nVS new rect
             */
            public void set(VS oVS, VS nVS){
                setScale(oVS.size.x,oVS.size.y,nVS.size.x,nVS.size.y);
                dx=setOff(oVS.loc.x,nVS.loc.x);
                dy=setOff(oVS.loc.y,nVS.loc.y);
            }
            public void set(BBox oB,VS nVS){
                setScale(oB.h.size(),oB.v.size(),nVS.size.x,nVS.size.y);
                dx=setOff(oB.h.lo,nVS.loc.x);
                dy=setOff(oB.v.lo,nVS.loc.y);
            }

            /**
             * Sets n to height or width of old values, whichever is larger.
             * Sets d to the height or width of new values, whichever is larger.
             * Together n/d can determine scale
             * @param oW old width
             * @param oH old height
             * @param nW new width
             * @param nH new height
             */
            public void setScale(int oW, int oH, int nW, int nH){
                n= Math.max(nW, nH);
                d= Math.max(oW, oH);
            }

            /**
             * When the object is scaled (Multiplied by n/d) the coordinate will
             * increase or decrease accordingly. To keep the coordinate in the same place,
             * after scale, negate it and then add new coordinate
             * @param oX old coordinate
             * @param nX new coordinate
             * @return adjusted coordinate as {@code int}
             */
            public int setOff(int oX, int nX){return (-oX * n/d) + nX;}
        }

    }

    //-----------------------VS-----------------------
    /**
     * Vector point plus width & height.
     * loc is a vector containing the location.
     * size is a vector containing the width and height.
     */
    public static class VS implements Serializable{
        public V loc, size;

        /**
         * Constructor
         * @param x x coord
         * @param y y coord
         * @param w width
         * @param h height
         */
        public VS(int x, int y, int w, int h){
            loc = new V(x,y); size = new V(w,h);
        }

        /**
         * Draws a filled rectangle onto {@code g} in color {@code c}
         * @param g grahics object to draw onto as {@code Graphics}
         * @param c color to draw in as {@code Color}
         */
        public void fill(Graphics g, Color c){
            g.setColor(c); g.fillRect(loc.x,loc.y,size.x,size.y);
        }

        /**
         * Determines whether a point falls within the VS area
         * @param x x coord
         * @param y y coord
         * @return true if (x, y) falls between (loc, loc+size)
         */
        public boolean hit(int x, int y){
            return loc.x<=x && loc.y<=y && x<=(loc.x+size.x) && y<=(loc.y+size.y);
        }

        /**@return left (x) coord*/
        public int xL(){return loc.x;}
        /**@return right (x+width) coord*/
        public int xH(){return loc.x + size.x;}
        /**@return median x coord (x+width)/2*/
        public int xM(){return (loc.x + loc.x + size.x)/2;}
        /**@return top (y) coord*/
        public int yL(){return loc.y;}
        /**@return bottom (y+height) coord*/
        public int yH(){return loc.y + size.y;}
        /**@return median y coord (y+height)/2*/
        public int yM(){return (loc.y + loc.y + size.y)/2;}

    }

    //-----------------------LoHi---------------------
    /**Keeps a record of the lowest and highest values*/
    public static class LoHi implements Serializable{
        public int lo,hi;
        public LoHi(int min, int max){lo=min;hi=max;}

        /**
         * Set high and low to v
         * @param v as {@code int}
         */
        public void set(int v){lo=v;hi=v;}

        /**
         * Keep track of high and low
         * @param v new number as {@code int}
         */
        public void add(int v){if (v < lo) {lo=v;};if(v>hi){hi=v;}}

        /**@return difference of hi and lo*/
        public int size(){return hi-lo>0?hi-lo:1;}
    }
    //-----------------------BBox---------------------
    /**Bounding box (Keeps track of the maximum bounds ie high and low coords)*/
    public static class BBox implements Serializable{
        public LoHi h, v; //horizontal and vertical
        public BBox(){h=new LoHi(0,0);v=new LoHi(0,0);}
        public void set(int x, int y){h.set(x);v.set(y);}
        public void add(int x, int y){h.add(x);v.add(y);}
        public void add(V v){add(v.x,v.y);}
        public VS getNewVS(){return new VS(h.lo, v.lo,h.hi-h.lo,v.hi-v.lo);}
        public void draw(Graphics g){g.drawRect(h.lo,v.lo,h.hi-h.lo,v.hi-v.lo);}
    }
    //-----------------------PL-----------------------

    /**
     * PL -  Point Line Object is a collection of points
     * that we can use to draw various shapes or lines.
     * Each point is an object of type V which contains an x, and y coord.
     */
    public static class PL implements Serializable {
        public V[] points;
        public PL(int count){
            points = new V[count];
            for(int i=0;i<count;i++){
                points[i]=new V(0,0);
            }
        }

        /**
         * Returns number of points in collection
         * @return number of points as {@code int}
         */
        public int size(){return points.length;}

        /**
         * Connects points with lines to draw a shape/figure (up to n points).
         * @param g graphic target to draw on as {@code Graphics}
         * @param n number of points to connect as {@code int}
         */
        public void drawN(Graphics g, int n){
            for(int i=1;i<n;i++){
                g.drawLine(points[i-1].x,points[i-1].y,points[i].x,points[i].y);
            }
            //drawNDots(g,n);
        }

        /**
         * Draws points in the collection onto a target (up to n points)
         * @param g graphic target to draw on as {@code Graphics}
         * @param n number of points to draw as {@code int}
         */
        public void drawNDots(Graphics g, int n){
//            g.setColor(Color.BLUE);
            for(int i=0;i<n;i++){g.drawOval(points[i].x-2,points[i].y-2,4,4);};
        }

        /**
         * Connects points with lines to draw a shape/figure (All points).
         * @param g graphic target to draw on as {@code Graphics}
         */
        public void draw(Graphics g){drawN(g,points.length);}

        /** Transform all points coords by V's static T object*/
        public void transform(){for(int i=0;i<points.length;i++){points[i].setT(points[i]);}}
    }

    //------------------------HC--------------------------------------
    public static class HC{
        public static HC ZERO = new HC(null,0);
        public HC dad;
        public int dv;

        public HC(HC dad, int dv){this.dad=dad; this.dv=dv;}

        public int v(){return dad==ZERO?dv:dad.v()+dv; }
    }
}
  