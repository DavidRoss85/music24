package sandbox;

import graphics.I;
import graphics.WinApp;
import graphics.G;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Squares extends WinApp implements ActionListener {
    public static Timer timer;
    public static G.VS theVS=new G.VS(100,100,200,300);
    public static Color color = G.rndColor();
    public static Square.List squares = new Square.List();
    public static Square lastSquare;
    private static boolean dragging = false;
    private static G.V mouseDelta = new G.V(0,0);
    public static G.V pressedLoc = new G.V(0,0);
    public static I.Area curArea;

    public Squares(){
        super("Squares",1000,800);
        timer = new Timer(30,this);
        timer.setInitialDelay(5000);
        timer.start();
    }


    @Override
    public void paintComponent(Graphics g){
        G.fillBack(g);
        squares.draw(g);
    }
    @Override
    public void mousePressed(MouseEvent me){
        int x=me.getX(), y=me.getY();
        /*lastSquare = squares.hit(x,y);
        if(lastSquare==null){
            dragging=false;
            lastSquare=new Square(x,y);
            squares.add(lastSquare);
        }else{
            dragging=true;
            lastSquare.dv.set(0,0);
            pressedLoc.set(x,y);
            mouseDelta.set(lastSquare.loc.x-x, lastSquare.loc.y-y);
        }*/
        curArea=squares.hit(x,y);
        curArea.dn(x,y);
        repaint();
    }
    @Override
    public void mouseReleased(MouseEvent me){
        if(dragging){
            lastSquare.dv.set(me.getX()-pressedLoc.x,me.getY()-pressedLoc.y);
        }
    }
    @Override
    public void mouseDragged(MouseEvent me){
        /*int x= me.getX(), y=me.getY();
        if(dragging){
            lastSquare.moveTo(x + mouseDelta.x, y + mouseDelta.y);
        }else{
            lastSquare.resize(x,y);
        }*/
        curArea.drag(me.getX(),me.getY());
        repaint();
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        repaint();
    }

    public static void main(String[] args){
        PANEL=new Squares();
        WinApp.launch();
    }



    //-----------------Square-----------------
    public static class Square extends G.VS implements I.Area{
        public Color c = G.rndColor();
        //public G.V dv = new G.V(G.rnd(20)-10,G.rnd(20)-10); // random velocity between -10 and 10 in both x and y
        public G.V dv = new G.V(0,0);

        public Square(int x, int y){super(x,y,100,100);} // constructor
        private Square(){
            super(0,0,3000,3000);
            c=Color.WHITE;
        }
        public static Square BACKGROUND = new Square(){
            public void dn(int x, int y){lastSquare = new Square(x,y);squares.add(lastSquare);}
            public void drag(int x, int y){lastSquare.resize(x,y);}
            public void up(){}
        };

        public void resize(int x, int y){
            if(x>loc.x && y>loc.y){
                size.set(x-loc.x, y-loc.y);
            }
        }
        public void moveTo(int x, int y){
            loc.set(x,y);
        }
        public void moveAndBounce(){
            loc.add(dv);
            if(xL() < 0 && dv.x <0){dv.x = - dv.x;}
            if(xH() > 1000 && dv.x >0){dv.x = - dv.x;}
            if(yL() < 0 && dv.y <0){dv.y = - dv.y;}
            if(yH() > 700 && dv.y >0){dv.y = - dv.y;}
        }
        public void draw(Graphics g){
            fill(g,c);
            moveAndBounce();
        }

        @Override
        public void dn(int x, int y){mouseDelta.set(loc.x - x, loc.y - y);} // calculate drag offset
        @Override
        public void drag(int x, int y){loc.set(mouseDelta.x + x, mouseDelta.y + y);}
        @Override
        public void up(int x, int y){}

        //-------------------List-----------------
        public static class List extends ArrayList<Square>{
            public List(){
                super();
                add(Square.BACKGROUND);
            }
            public void draw(Graphics g){
                for(Square s: this){
                    s.draw(g);
                }
            }
            public void addNew(int x, int y){
                add(new Square(x,y));
            }
            public Square hit(int x, int y){
                Square res = null;
                for(Square s: this){
                    if(s.hit(x,y)){
                        res=s;
                    }
                }
                return res;
            }
        }

    }

}
