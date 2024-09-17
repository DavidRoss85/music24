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
    public static int y(int c){return yM + c * H;}


    public static void main (String[] args){
        PANEL = new Collapse();
        WinApp.launch();
    }
}
