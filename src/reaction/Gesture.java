package reaction;

import graphics.G;
import graphics.I;

public class Gesture {

    public static String recognized = "NULL";
    public static I.Area AREA = new I.Area() {
        public boolean hit(int x, int y) {return true;}
        public void dn(int x, int y) {Ink.BUFFER.dn(x,y);}
        public void drag(int x, int y) {Ink.BUFFER.drag(x,y);}
        public void up(int x, int y) {
            Ink.BUFFER.up(x,y);
            Ink ink = new Ink();
            Gesture gest = Gesture.getNew(ink);
            Ink.BUFFER.clear();
            recognized= gest==null?"NULL":gest.shape.name;
            if(gest!=null){
                Reaction r = Reaction.best(gest);
                if(r!=null){r.act(gest);}else{recognized+=" NO BIDS";}
            }
        }
    };
    public Shape shape;
    public G.VS vs;

    private Gesture(Shape shape, G.VS vs){
        this.shape=shape;
        this.vs=vs;
    }

    public static Gesture getNew(Ink ink){
        Shape s = Shape.recognize(ink);
        return s==null? null:new Gesture(s,ink.vs);
    }
}
