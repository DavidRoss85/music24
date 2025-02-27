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
import sounds.SimpleMidiPlayer;

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

    //Added by me
    public static SimpleMidiPlayer midiPlayer;

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
    public void testPoly(Graphics g){
        int H = -8, x1=100, x2=200;
        Beam.setMasterBeam(x1,100+G.rnd(100),x2,100+G.rnd(100));
        g.drawLine(0,Beam.mY1,x1,Beam.mY1);
        Beam.drawBeamStack(g,0,1,x1,x2,H);
        g.setColor(Color.ORANGE);
        Beam.drawBeamStack(g,1,3,x1+10,x2-10,H);
    }
    public void paintComponent(Graphics g){
        G.fillBack(g);
        if(training){Shape.TRAINER.show(g);}
        g.setColor(Color.BLACK);
        Ink.BUFFER.show(g);
        Layer.ALL.show(g);
        g.drawString(Gesture.recognized,900,30);
        if(PAGE!=null){
//            Staff staff = PAGE.sysList.get(0).staffs.get(0);
//            Key.drawOnStaff(g,7,Key.fF,110,Glyph.SHARP,staff);
//            Glyph.HEAD_HALF.showAt(g,8,200,PAGE.margins.top+4*8);
//            int h=32;
//            Glyph.HEAD_Q.showAt(g,h,200,PAGE.margins.top+4*h);
//            g.setColor(Color.RED);
//            g.drawRect(200,PAGE.margins.top+3*h,24*h/10,2*h);
        }
//        g.fillPolygon(poly);
//        poly.ypoints[3]++;
//        testPoly(g);
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

    public void keyTyped(KeyEvent ke){
        if(training){Shape.TRAINER.keyTyped(ke);repaint();}
    }

    public void keyPressed(KeyEvent ke){
       if(ke.getKeyCode()==KeyEvent.VK_0){
            //Playmusic code here
           this.renderAndPlay(PAGE);
        }
    }

    private void renderAndPlay(Page page){
        if(page==null || midiPlayer.sequencer.isRunning()){
            return;
        }else{
            midiPlayer.sequencer.close();
        }


        try {
            midiPlayer = new SimpleMidiPlayer(1, 16);
        }catch(Exception e){
            System.out.println(e);
        }

        //Set these to global variables later:
        int MKEY = 65;
        int[] keyArr = {-8,-7,-5,-3,-1,0,2,4,5,7,9,11};// piano scale starting from E
        int PPQ = 16;
        int lastDuration=0;
        int currTime=1;
        Sys.List sysList = page.sysList;

        try{
            //Each time
            for(Sys theSys: sysList ){

                for(Time time: theSys.times){

                    for(Rest rest: time.rests){
                        int nFlags = rest.nFlag;
                        int duration = convertFlagToTime(nFlags,PPQ); //length
                        lastDuration=duration;
                    }
                    //Each head in time
                    for(Head head: time.heads){

                        int note = MKEY - keyArr[head.line+1]; //note
                        int nFlags = head.stem==null? 0 : head.stem.nFlag;
                        int duration = convertFlagToTime(nFlags,PPQ); //length
                        System.out.println(duration);

                        midiPlayer.addToTrack(1,note,currTime,duration);
                        lastDuration= Math.max(lastDuration,duration);
                    }
                    System.out.println("\n\n");
                    currTime+=lastDuration;
                    lastDuration=0;
                }
            }

            //play track
            midiPlayer.playTrack();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Converts the flags on a stem to a duration (range -2 to 4).
     * A bpq of < 16 will cause shorter notes to play incorrectly.
     * @param nFlags as {@code int}
     * @param bpq beats per quarter note as {@code int}
     * @return duration as {@code int}
     */
    private int convertFlagToTime(int nFlags, int bpq){
        nFlags*=-1; //more flags... less time

        return (int) (Math.pow(2,nFlags) * bpq);
    }

    public static void main(String[] args){
        PANEL= new MusicEd();
        try{
            midiPlayer = new SimpleMidiPlayer(1,16);
        }catch(Exception e){
            //Silently fail:
            System.out.println(e);
        }
        WinApp.launch();
    }
}
