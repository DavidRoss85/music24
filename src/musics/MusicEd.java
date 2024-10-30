package musics;

import graphics.G;
import graphics.I;
import graphics.WinApp;
import reaction.*;
import reaction.Shape;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.security.Key;

public class MusicEd extends WinApp {



    //public Layer BACK = new Layer("BACK"), FORE = new Layer("FORE");
    static{ //static blocks will run before other code
        new Layer("BACK");
        new Layer("NOTE");
        new Layer("FORE");
    }
    public static boolean training = false;
    public static I.Area curArea = Gesture.AREA;
    public static Page PAGE;


    public MusicEd(){
        super("Music Editor",UC.screenWidth,UC.screenHeight);
        Reaction.intialReactions.addReaction(new Reaction("W-W") {
            public int bid(Gesture g) {return 0;}
            public void act(Gesture g) {
                int y=g.vs.yM();
                PAGE=new Page(y);
                this.disable();
            }
        });
    }

    public void paintComponent(Graphics g){
        G.fillBack(g);
        if(training){Shape.TRAINER.show(g);}
        g.setColor(Color.BLACK);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
        g.drawString(Gesture.recognized,900,30);
        if(PAGE!=null){
            Glyph.CLEF_G.showAt(g,8,100,PAGE.margins.top+4*8);
//            Glyph.HEAD_HALF.showAt(g,8,200,PAGE.margins.top+4*8);
//            int h=32;
//            Glyph.HEAD_Q.showAt(g,h,200,PAGE.margins.top+4*h);
//            g.setColor(Color.RED);
//            g.drawRect(200,PAGE.margins.top+3*h,24*h/10,2*h);
        }
    }

    public void trainButton(MouseEvent me){
        if(me.getX()>UC.screenWidth-40 && me.getY()<40){
            training=!training;
            curArea=training?Shape.TRAINER:Gesture.AREA;
        }
    }

    public void mousePressed(MouseEvent me){curArea.dn(me.getX(),me.getY());repaint();}
    public void mouseDragged(MouseEvent me){curArea.drag(me.getX(),me.getY());repaint();}
    public void mouseReleased(MouseEvent me){
        curArea.up(me.getX(),me.getY());
        trainButton(me);
        repaint();
    }

    public void keyTyped(KeyEvent ke){if(training){Shape.TRAINER.keyTyped(ke);repaint();}}

    public static void main(String[] args){
        PANEL= new MusicEd();
        WinApp.launch();
    }
}
