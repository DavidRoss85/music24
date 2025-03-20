package reaction;

import graphics.G;
import graphics.I;

import java.util.ArrayList;

public class Gesture {

    public static String recognized = "NULL";
    private static List UNDO = new List();

    /**
     * Defines an I.Area object and assigns it to AREA.
     * AREA captures the mouse actions in the window.
     */
    public static I.Area AREA = new I.Area() {
        public boolean hit(int x, int y) {return true;}
        public void dn(int x, int y) {Ink.BUFFER.dn(x,y);}
        public void drag(int x, int y) {Ink.BUFFER.drag(x,y);}


        /**
         * Takes the ink buffer and calls getNew() which references the shape database and
         * returns a gesture which contains a recognized shape plus a vs (bbox of gesture)
         * @param x
         * @param y
         */
        public void up(int x, int y) {
            Ink.BUFFER.up(x,y);
            Ink ink = new Ink();
            Gesture gest = Gesture.getNew(ink);
            Ink.BUFFER.clear();
            recognized= gest==null?"NULL":gest.shape.name;
            if(gest!=null){
                if(gest.shape.name.equals("N-N")){undo();}else{gest.doGesture();}
            }
        }
    };
    public Shape shape;
    public G.VS vs;

    private Gesture(Shape shape, G.VS vs){
        this.shape=shape;
        this.vs=vs;
    }

    public static void undo(){
        System.out.println("UNDO");
        if(UNDO.size()>0){
            UNDO.remove(UNDO.size()-1);
            Layer.nuke();//eliminates all masses
            Reaction.nuke(); //clears out byShape and reloads initial reactions
            UNDO.redo();
        }
    }

    private void redoGesture(){
        Reaction r = Reaction.best(this);
        if(r!=null){r.act(this);}
    }
    private void doGesture(){
        Reaction r = Reaction.best(this);
        if(r!=null){UNDO.add(this);r.act(this);}else{recognized+=" NO BIDS";}
    }
    public static Gesture getNew(Ink ink){
        Shape s = Shape.recognize(ink);
        return s==null? null:new Gesture(s,ink.vs);
    }

    //---------------List-------------------------
    public static class List extends ArrayList<Gesture> {
        private void redo(){
            for(Gesture gest: this){
                gest.redoGesture();
            }
        }
    }
}
