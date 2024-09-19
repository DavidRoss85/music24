package sandbox;

import graphics.G;
import graphics.WinApp;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends WinApp {
    public Paint(){super("Paint",1000,700);}

    public static Path thePath = new Path();
    public static Pic thePic = new Pic();

    @Override
    public void paintComponent(Graphics g){
        g.setColor(Color.WHITE); g.fillRect(0,0,9000,9000);
        Color c = G.rndColor();
        g.setColor(c);
        g.fillOval(100,150,200,300);

        g.setColor(Color.BLACK);
        g.drawLine(100,600,600,100);

        int x = 400, y = 200;   String msg = "Clicks = "+clicks;
        FontMetrics fm = g.getFontMetrics(); // local variable fm is information about the current font.
        int a = fm.getAscent(), d = fm.getDescent(); // get numbers from font metrics

        // the ascent is how far above the baseline the font extends,
        // .. descent is how far below the base line for letters with tails like
        // gyq

        // the entire height of the font will be a+d

        int w = fm.stringWidth(msg); // get width of msg from font metrics

        // note: since fonts can have variable character width, "iii" takes less space than "mmm", we must
        // tell fm, what string we are interested in measuring and fm will perform the calculation for us
        // and tell us how many pixels wide that string will be.

        // so now we know enough to draw the box.

        g.drawRect(x,y-a,w,a+d); // note: move y from baseline UP the page by the ascent
        g.drawString(msg,x,y);
        g.drawOval(x,y,3,3);
        thePic.draw(g);
        thePath.draw(g);

    }

    public static int clicks = 0; // we will total the mouse clicks
    public static String numPaths = "";// for now to see if pic array is growing

    @Override
    public void mousePressed(MouseEvent me){

        clicks++; // bump up the click counter.
        thePath = new Path();
        thePic.add(thePath);
        thePath.clear();
        thePath.add(me.getPoint());
        repaint();
    }
    @Override
    public void mouseDragged(MouseEvent me){
        thePath.add(me.getPoint());
        repaint(); // If you forgot this - you add points but do not SEE them! a bug!
    }
    public static void main(String[] args){PANEL=new Paint(); WinApp.launch();}

    //--------------------PATH----------------------------
    public static class Path extends ArrayList<Point> {
        public void draw(Graphics g){
            for(int i = 1; i<size(); i++){
                Point p = get(i-1), n = get(i); // the previous and the next point
                g.drawLine(p.x,p.y,n.x,n.y);
            }
        }
    }
    //------------------PIC-------------------------------
    public static class Pic extends ArrayList<Path>{
        public void draw(Graphics g){
            g.drawString("Number of Paths stored: "+size(),400,150);
            int i =0;
            for(Path x: this){
                i++;
                g.drawString(""+x.get(0),100,100+(i*20));
                x.draw(g);
            }
        }
    }


}
