package reaction;

import graphics.*;
import musics.UC;

import java.awt.*;
import java.util.ArrayList;

public class Ink extends G.PL implements I.Show{
    public static Buffer BUFFER = new Buffer();
    public Ink(){
        super(BUFFER.n);
        for(int i=0;i<BUFFER.n;i++){
            points[i].set(BUFFER.points[i]);
        }
    }

    @Override
    public void show(Graphics g) {g.setColor(UC.inkColor); draw(g);}


    //---------Buffer---------
    public static class Buffer extends G.PL implements I.Show, I.Area{
        public static final int MAX = UC.inkBufferMax;
        public int n;
        private Buffer(){super(MAX);}
        public void add(int x, int y){
            if(n<MAX){points[n++].set(x,y);}
        }

        public void clear(){n=0;}
        public void show(Graphics g){drawN(g,n);}

        @Override
        public boolean hit(int x, int y) {return true;}

        public void dn(int x, int y){clear();add(x,y);}
        public void drag(int x, int y){add(x,y);}
        public void up(int x, int y){
            add(x,y);
        }
    }

    //---------List-----------
    public static class List extends ArrayList<Ink> implements I.Show{

        @Override
        public void show(Graphics g) {for(Ink ink: this){ink.show(g);}}
    }
}
