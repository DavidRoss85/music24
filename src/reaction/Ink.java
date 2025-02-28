package reaction;

import graphics.*;
import musics.UC;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Ink implements I.Show, Serializable{
    public static Buffer BUFFER = new Buffer();
    public Norm norm;
    public G.VS vs;
    public Ink(){
        norm=new Norm();
        vs= BUFFER.bBox.getNewVS();
    }

    @Override
    public void show(Graphics g) {g.setColor(UC.inkColor); norm.drawAt(g,vs);}

    //---------Norm-----------
    /** Nested class in Ink which extends a PL(Point Line).
     * Stores the drawn shape in a subsampled version
     * N is the sample size, i.e. the number of points in the point line array
     * NCS is the drawing area*/
    public static class Norm extends G.PL implements Serializable {
        public static final int N = UC.normSampleSize, MAX = UC.normCoordMax;
        public static final G.VS NCS = new G.VS(0,0,MAX,MAX);

        /**
         * Constructor for Norm
         */
        public Norm(){
            super(N);
            BUFFER.subSample(this); //subsample the buffer and store in this
            G.V.T.set(BUFFER.bBox,NCS);
            transform();
        }

        /** Draws the norm at a target box location
         * @param g drawing surface
         * @param vs box dimensions to draw at
         */
        public void drawAt(Graphics g, G.VS vs){
            G.V.T.set(NCS, vs);
            for(int i=1;i<N;i++){
                g.drawLine(points[i-1].tx(),points[i-1].ty(),points[i].tx(),points[i].ty());
            }
        }

        /** Distribution (dist)
         * Takes the square of the distance of each point to the same index point
         * in the array and adds them together. The result is the distribution.
         * @param n a {@code Norm} to compare this one to
         * @return distribution as int
         */
        public int dist(Norm n){
            int res = 0;
            for(int i=0;i<N;i++){
                int dx = points[i].x - n.points[i].x;
                int dy = points[i].y - n.points[i].y;
                res+=dx*dx+dy*dy;
            }
            return res;
        }

        /**Calls to G.V's blend method for each point
         * Blends this object's points with norm
         * @param norm
         * @param nBlend
         */
        public void blend(Norm norm, int nBlend){
            for(int i=0;i<N;i++){
                points[i].blend(norm.points[i],nBlend);
            }
        }
    }
    //---------Buffer---------
    /** Static class to act as buffer for Norms*/
    public static class Buffer extends G.PL implements I.Show, I.Area{
        public static final int MAX = UC.inkBufferMax;
        public int n; //Counter for the number of points
        public G.BBox bBox = new G.BBox();
        /** Constructor */
        private Buffer(){super(MAX);}

        /**
         * Add a point to the array and increment n
         * Keep track of high and low bounds
         * @param x x coord
         * @param y y coord
         */
        public void add(int x, int y){
            if(n<MAX){points[n++].set(x,y);bBox.add(x,y);}
        }

        /**
         * Maps each point in {@code pl} to a point in the buffer. In other words,
         * maintains the general shape of the coordinates, with fewer points.
         * The more points in pl, the higher the sample.
         * @param pl the object to subsample to as {@code G.PL}
         */
        public void subSample(G.PL pl){
            int k = pl.size();
            for(int i=0;i<k;i++){pl.points[i].set(this.points[i*(n-1)/(k-1)]);}
        }

        /**
         * Clears the buffer*/
        public void clear(){n=0;}

        /** Show */
        public void show(Graphics g){drawN(g,n);/*bBox.draw(g);*/}

        @Override
        public boolean hit(int x, int y) {return true;}

        /** Clears the buffer and initialize bBox on mouse down*/
        @Override
        public void dn(int x, int y){clear();bBox.set(x,y);add(x,y);}
        /** Adds points to the buffer as mouse drags*/
        public void drag(int x, int y){add(x,y);}
        /** Adds the final point to buffer on mouse up*/
        public void up(int x, int y){
            add(x,y);
        }
    }

    //---------List-----------
    /** List of Inks*/
    public static class List extends ArrayList<Ink> implements I.Show, Serializable{

        @Override
        public void show(Graphics g) {for(Ink ink: this){ink.show(g);}}
    }
}
