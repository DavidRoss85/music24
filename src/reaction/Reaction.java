package reaction;

import graphics.I;
import musics.UC;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Reaction implements I.React{
    private static Map byShape = new Map(); //A hashmap mapping a shape to all similar shapes for all Masses
    public static List intialReactions = new List(); //used by undo to restart everything

    public Shape shape;

    public Reaction(String shapeName){
        shape = Shape.DB.get(shapeName);
        if(shape==null){System.out.println("WTF?-shape.db does not know " + shapeName);}
    }

    /**
     * Enable or disable add or remove reaction from the byShape map.
     * Calling Reaction.enable() will add the reaction's shape to a static map.
     * This map can be accessed in the best() method to find who the best match is
     */
    public void enable(){
        List list = byShape.getList(shape);
        if (!list.contains(this)) {
            list.add(this);
        }

    }
    public void disable(){
        List list = byShape.getList(shape);
        list.remove(this);
    }
    public static void nuke(){
        byShape=new Map();
        intialReactions.enable();
    }

    /**
     * Searches the map for a list of all reactions with the appropriate shape
     * @param g
     * @return best shape from a list that matches gesture's shape
     */
    public static Reaction best(Gesture g){
        return byShape.getList(g.shape).loBid(g);
    }

    //----------------List------------------------------
    public static class List extends ArrayList<Reaction> {

        /**
         * Adds the reaction to the list and enables(Adds to hash map)
         * @param r the reaction to add
         */
        public void addReaction(Reaction r){add(r); r.enable();}

        /**
         * Removes and disables the reaction
         * @param r
         */
        public void removeReaction(Reaction r){remove(r); r.disable();}

        /**
         * Disables (remove from hash map) all reactions and deletes from this list
         */
        public void clearAll(){
            for(Reaction r: this){
                r.disable();
            }
            this.clear();
        }

        /**
         * Finds the best bid among the list of shapes
         * @param g
         * @return
         */
        public Reaction loBid(Gesture g){ //can return null
            Reaction res = null;
            int bestSoFar = UC.noBid;
            for(Reaction r: this){
                int b = r.bid(g); // This function is defined in each reaction definition
                if(b<bestSoFar){
                    bestSoFar=b;
                    res=r;
                }
            }
            return res;
        }
        public void enable(){
            for(Reaction r: this){
                r.enable();
            }
        }
    }
    //------------------Map-----------------------------
    public static class Map extends HashMap<Shape,List>{
        public List getList(Shape s){ //Always succeeds!!
            List res = get(s);
            if(res==null){
                res= new List();
                put(s,res);
            }
            return res;
        }
    }

}
