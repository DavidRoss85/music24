package graphics;

import java.awt.*;
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
    public static class V {

        public int x,y;
        public V(int x, int y){this.set(x,y);}
        public void set(int x, int y){this.x = x; this.y = y;}
        public void set(V v){x=v.x; y=v.y;}
        public void add(V v){x += v.x; y += v.y;} // vector addition

    }

    //-----------------------VS-----------------------
    public static class VS{
        public V loc, size;

        public VS(int x, int y, int w, int h){
            loc = new V(x,y); size = new V(w,h);
        }

        public void fill(Graphics g, Color c){
            g.setColor(c); g.fillRect(loc.x,loc.y,size.x,size.y);
        }
        public boolean hit(int x, int y){
            return loc.x<=x && loc.y<=y && x<=(loc.x+size.x) && y<=(loc.y+size.y);
        }
        public int xL(){return loc.x;}
        public int xH(){return loc.x + size.x;}
        public int xM(){return (loc.x + loc.x + size.x)/2;}
        public int yL(){return loc.y;}
        public int yH(){return loc.y + size.y;}
        public int yM(){return (loc.y + loc.y + size.y)/2;}

    }

    //-----------------------LoHi---------------------
    public static class LoHi{}
    //-----------------------BBox---------------------
    public static class BBox{}
    //-----------------------PL-----------------------
    public static class PL{}
}
  