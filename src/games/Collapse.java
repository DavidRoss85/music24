package games;

import graphics.WinApp;
import musics.UC;

import java.awt.*;

public class Collapse extends WinApp {

    public static final int nC = 13, nR =15;
    public static final int W = 60, H =40;
    public static int xM=100, yM=100;
    public static Color[] color = {Color.LIGHT_GRAY,Color.CYAN,Color.GREEN,
    Color.YELLOW,Color.RED,Color.PINK};
    public static int[][] grid = new int[nC][nR];


    public Collapse(){
        super("Collapse", UC.screenWidth,UC.screenHeight);
        rndColors(3);
    }

    public void paintComponent (Graphics g){
        g.setColor(color[0]);
        g.fillRect(0,0,5000,5000);
        showGrid(g);
    }

    public void mouseClicked(MouseEvent me){
        int x = me.getX(), y = me.getY();
        if(x < xM || y < yM){return;}
        int r = r(y), c = c(x);
        if(r<nR && c <nC){
            crAction(c,r);
        }
        repaint();
    }

    public static void crAction(int c, int r){
        //System.out.println("("+c+","+r+")");
        if(infectable(c,r)){infect(c,r,grid[c][r]);}
    }

    public static boolean infectable(int c, int r){
        int v =  grid[c][r];
        if( v==0){return false;}
        if(r>0){if(grid[c][r-1]== v){return true;}}
        if(c>0){if(grid[c-1][r]== v){return true;}}
        if(r>nR-1){if(grid[c][r+1]== v){return true;}}
        if(c>nC-1){if(grid[c+1][r]== v){return true;}}
        return false;
    }

    public static void infect(int c,int r, int v){
        if(grid[c][r]!= v){return;}
        grid[c][r]=0;
        if(r>0){infect(c,r-1,v);}
        if(c>0){infect(c-1,r,v);}
        if(r<nR-1){infect(c,r+1,v);}
        if(c<nC-1){infect(c+1,r,v);}
    }

    public static void rndColors(int k){
        for (int c=0; c<nC; c++){
            for(int r=0;r<nR;r++){
                grid[c][r]=1+G.rnd(k);
            }
        }
    }

    public static void showGrid(Graphics g){
        for (int c=0; c<nC; c++){
            for(int r=0;r<nR;r++){
                g.setColor(color[grid[c][r]]);
                g.fillRect(x(c),y(r),W,H);
            }
        }

    }

    public static int x(int c){return xM + c * W;}
    public static int y(int r){return yM + c * H;}
    private static int c(int x){return (x-xM)/W;} //unsafe fixed in mouseClicked
    private static int r(int y){return (y-yM)/H;}


    public static void main (String[] args){
        PANEL = new Collapse();
        WinApp.launch();
    }
}
