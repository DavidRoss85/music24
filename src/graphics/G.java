package graphics;

import java.awt.*;
import java.util.Random;

public class G{
    public static Random RND = new Random();
    public static int rnd(int max){return RND.nextInt(max);}
    //--------------These are from Tetris----------------
    public static V LEFT = new V(-1,0);
    public static V RIGHT = new V(1,0);
    public static V UP = new V(0,-1);
    public static V DOWN = new V(0,1);
    //-------------------------------------------------
    public static Color rndColor(){return new Color(rnd(256),rnd(256),rnd(256)); }

    //Place this in static class V
    //public void set(V v){x=v.x, y=v.y}
}
  