package games;

import graphics.WinApp;
import graphics.G;
import javax.swing.*;
import java.awt.*;
import  java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class Tetris extends WinApp implements ActionListener {


    //********David Ross:*********
    //Add a Score
    public static int score = 0;
    public static int lastScore = 0;
    public static int scoreMultiplier = 1;
    public static int levelMultiplier = 1;
    public static final int POINT_VALUE = 10;
    public static final int LINE_CLEAR_VALUE = 100;
    public static final int LABEL_Y_OFFSET = 50;
    public static final int LABEL_X_OFFSET = 300;

    //Control Speed
    public static int gameDelay = 100;
    public static boolean gameIsOver = false, gamePaused = false;
    public static boolean breakingBricks = false;
    public static int time = 0, inputDelay = 0, aniBreakFrame = 0; //iShape =0;

    //Key check
    public static boolean dnPressed = false, upPressed = false, spcPressed = false;
    public static boolean lfPressed = false, rtPressed = false, btnPressed = false;

    //Next Shape
    public static Shape nextShape;
    public static final int nXOffset = 12, nYOffset = 4;
    //****************************



    public static Timer timer;
    public static final int xM = 50, yM = 100;
    public static final int H =20, W =10, C = 25;
    public static Color[] color = {Color.RED,Color.GREEN,Color.BLUE,Color.ORANGE,Color.CYAN,Color.YELLOW,Color.MAGENTA,Color.BLACK,Color.BLACK,Color.BLACK};
    public static Shape[] shapes = {Shape.Z,Shape.S,Shape.J, Shape.L,Shape.I,Shape.O,Shape.T};

    public static Shape shape;
    public static final int iBkColor = 7, zap=8, zapAnimate=9;
    public static int[][] well = new int[W][H];

    public Tetris() {
        super("Tetris", 1000, 700);
        startNewGame();
        timer = new Timer(0,  this);
        timer.start();
    }

    public void startNewGame(){
        gameIsOver=false;
        nextShape=shapes[G.rnd(7)]; //Next shape
        nextShape.loc.set(nXOffset,nYOffset);
        nextShape.fakeShape=true;
        scoreMultiplier=1;
        score=0;

        shape=shapes[G.rnd(7)];
        clearWell();
    }


    public void paintComponent(Graphics g){
        if(!gameIsOver){
            if(!gamePaused){
                G.fillBack(g); //clears the screen
                if(!breakingBricks){
                    inputDelay++;inputDelay=registerPlayerInput(inputDelay,4);
                    time++;if(time>=gameDelay){time=0;shape.drop();}
                }
                unzapWell();
                showWell(g);
                //Placed under showWell for timing purposes
                if(breakingBricks){
                    aniBreakFrame++;
                    aniBreakFrame = animateZapWell(g,aniBreakFrame);
                }
                shape.show(g);
                nextShape.show(g);
                showLabels(g);//Show score
            }else{
                showGameMessage(g, new String[] {"GAME PAUSED", "PRESS ENTER KEY"});
            }
        }else {
            showGameMessage(g, new String [] {"GAME OVER","PRESS ENTER KEY"});
        }
    }

    //********David Ross:*********
    public void showLabels(Graphics g){
        int fSize = 20;
        Font gFont = new Font("Courier New",Font.BOLD,fSize);
        g.setColor(Color.BLACK);
        g.setFont(gFont);
        g.drawString("Level: " + levelMultiplier + " - Timer: "+ time,xM,yM);
        g.drawString("Key multiplier X" + scoreMultiplier,xM+LABEL_X_OFFSET,yM+LABEL_Y_OFFSET-fSize-10);
        g.drawString("Score: " + score,xM+LABEL_X_OFFSET,yM+LABEL_Y_OFFSET);
        g.drawString("Next Shape: ",xM+LABEL_X_OFFSET,yM+LABEL_Y_OFFSET+fSize+10);
    }
    public void showGameMessage(Graphics g, String[] message){
        g.setColor(Color.WHITE);
        g.fillRect(xM,yM+200,1000,60);
        Font gFont1 = new Font("Courier New",Font.BOLD,50);
        Font gFont2 = new Font("Courier New",Font.BOLD,30);
        g.setColor(Color.RED);
        g.setFont(gFont1);
        g.drawString(message[0],xM,yM+230);
        g.setFont(gFont2);
        g.drawString(message[1],xM,yM+260);
    }
    public static void checkLevel(){
        if(score-lastScore>(6000*levelMultiplier)){
            increaseLevel();
            lastScore=score;
        }
    }
    public static void increaseLevel(){
        levelMultiplier++;
        if(gameDelay>5) {
            gameDelay -= 5;
        }
    }

    //****************************


    public static void clearWell(){
        for(int x=0;x<W;x++){
            for(int y=0;y<H;y++){
                well[x][y]=iBkColor;
            }
        }
    }
    public static void showWell(Graphics g){
        for(int x=0;x<W;x++){
            for(int y=0;y<H;y++){
                int xx = xM + C*x, yy= yM + C*y;
                g.setColor(color[well[x][y]]);
                g.fillRect(xx,yy,C,C);
                g.setColor(Color.BLACK);
                g.drawRect(xx,yy,C,C);
            }
        }
    }
    public static void zapWell(){
        for(int y=0;y<H;y++){zapRow(y);}
    }
    public static void zapRow(int y){
        for(int x=0;x<W;x++){if(well[x][y]==iBkColor){return;}}
        breakingBricks=true;
        for(int x=0;x<W;x++){well[x][y]=zapAnimate;}

        //****David Ross****
        score+=levelMultiplier*scoreMultiplier*LINE_CLEAR_VALUE;
        scoreMultiplier=1;
        checkLevel();
        //******************
    }
    public static void unzapWell(){
        for(int y=1;y<H;y++){
            for(int x=0;x<W;x++){
                if(well[x][y-1]!=zap && well[x][y]==zap){
                    well[x][y]=well[x][y-1];
                    well[x][y-1]=(y==1)? iBkColor: zap;
                }
            }
        }
    }
    public static int animateZapWell(Graphics g,int frame){
        for(int y=0;y<H;y++){
            animateBreaking(g,frame,y);
        }
        if(frame>C){
            for(int y=0;y<H;y++){
                finishAnimateZapRow(y);}
            frame=0;
        }
        return frame;
    }
    public static void animateBreaking(Graphics g, int f, int y){
        for(int x=0;x<W;x++){if(well[x][y]!=zapAnimate){return;}}
        for(int x=0;x<W;x++){

            g.setColor(Color.PINK);
            g.fillRect(xM + C*x,yM + (C*y+(C-(C/f))),C,C/f);
            g.setColor(Color.BLACK);
            g.drawRect(xM + C*x,yM + (C*y+(C-(C/f))),C,C/f);
        }
    }
    public static void finishAnimateZapRow(int y){

        for(int x=0;x<W;x++){if(well[x][y]!=zapAnimate){return;}}
        for(int x=0;x<W;x++){well[x][y]=zap;}
        breakingBricks=false;
    }
    //*********David Ross******************************
    public static int registerPlayerInput(int delay, int max){
        if(delay>max){
            if(lfPressed){shape.slide(G.LEFT);delay=0;}
            if(rtPressed){shape.slide(G.RIGHT);delay=0;}
            if(delay>max*3){if(upPressed||spcPressed){shape.safeRot();delay=0;}}
        }
        if(dnPressed){shape.drop();}
        return delay;
    }
    //***************************************************
    public void keyPressed(KeyEvent ke){
        int vk = ke.getKeyCode();
        btnPressed = true;
        //executing code moved to registerPlayerInput
        if(vk==KeyEvent.VK_LEFT){lfPressed=true;}
        if(vk==KeyEvent.VK_RIGHT){rtPressed=true;}
        if(vk==KeyEvent.VK_UP){/*shape.safeRot();*/upPressed=true;}

        //***David Ross****************
        if(vk==KeyEvent.VK_SPACE){/*shape.safeRot();*/spcPressed=true;}
        if(vk==KeyEvent.VK_DOWN){/*shape.drop();*/ dnPressed=true;}
        if(vk==KeyEvent.VK_ENTER){
            if(gameIsOver){startNewGame();}else{gamePaused= !gamePaused;}
        }
        //*****************************
    }

    public void keyReleased(KeyEvent ke){
        int vk = ke.getKeyCode();
        if(vk==KeyEvent.VK_DOWN){dnPressed=false; scoreMultiplier = 1;}
        if(vk==KeyEvent.VK_LEFT){lfPressed=false;}
        if(vk==KeyEvent.VK_RIGHT){rtPressed=false;}
        if(vk==KeyEvent.VK_UP){upPressed=false;}
        if(vk==KeyEvent.VK_SPACE){spcPressed=false;}
    }
    public void mousePressed(MouseEvent me){
        if(gameIsOver){startNewGame();}
    }
    @Override
    public void actionPerformed(ActionEvent e){repaint();}

    public static void main(String[] args){
        PANEL=new Tetris();
        WinApp.launch();
    }

    //------------------SHAPE----------------------
    public static class Shape{
        public static Shape Z, S, J, L, I, O, T;

        //********David Ross:*********
        //Fake shapes are not copied to well
        public boolean fakeShape = false;
        //****************************

        public G.V[] a = new G.V[4];
        public int iColor;
        public G.V loc = new G.V(4,0);

        static {
            Z=new Shape(new int[] {0,0, 1,0, 1,1, 2,1},0);
            S=new Shape(new int[] {0,1, 1,0, 1,1, 2,0},1);
            J=new Shape(new int[] {0,0, 0,1, 1,1, 2,1},2);
            L=new Shape(new int[] {0,1, 1,1, 2,1, 2,0},3);
            I=new Shape(new int[] {0,0, 1,0, 2,0, 3,0},4);
            O=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},5);
            T=new Shape(new int[] {0,1, 1,0, 1,1, 2,1},6);

        }

        public static G.V temp = new G.V(0,0);

        public Shape(int[] xy, int iColor){
            this.iColor = iColor;
            for(int i=0;i<4;i++){
                a[i]=new G.V(xy[2*i], xy[2*i+1]);
            }
        }

        public void show(Graphics g){
            g.setColor(color[iColor]);
            for(int i = 0; i < 4; i++){g.fillRect(x(i),y(i),C,C);}
            g.setColor(Color.BLACK);
            for(int i = 0; i < 4; i++){g.drawRect(x(i),y(i),C,C);}
        }

        public int x(int i){return xM + C*(a[i].x+loc.x);} //Current location + relative block position multiplied by size then add the margin
        public int y(int i){return yM + C*(a[i].y+loc.y);}

        public void rot(){ //Unsafe - does not collision detect
            temp.set(0,0);
            for(int i=0; i<4; i++){
                a[i].set(-a[i].y,a[i].x);
                if(temp.x>a[i].x){temp.x=a[i].x;}
                if(temp.y>a[i].y){temp.y=a[i].y;}
            }

            temp.set(-temp.x,-temp.y);
            for(int i=0; i<4; i++){a[i].add(temp);}
        }

        public void safeRot(){
            rot(); //first assume we can rotate
            cdsSet();
            if(collisionDetected()){rot();rot();rot();}
        }

        public void drop(){
            this.cdsSet();
            this.cdsAdd(G.DOWN);
            if(dnPressed){scoreMultiplier++;}
            if(this.collisionDetected()){
                //*****David Ross****
                //Restart if there is a collision at the top
                if(shape.loc.y == 0){
                    gameIsOver=true;
                }
                copyToWell();
                zapWell();
                dropNewShape();
            }
            else{
                loc.add(G.DOWN);
            }
        }

        public void copyToWell(){
            if(!this.fakeShape) {
                for (int i = 0; i < 4; i++) {
                    well[a[i].x + loc.x][a[i].y + loc.y] = iColor;
                }
            }
        }
        public static void dropNewShape(){
            score+= levelMultiplier*scoreMultiplier*POINT_VALUE;
            scoreMultiplier=1;
            shape=nextShape;
            shape.loc.set(4,0);
            shape.fakeShape=false;
            dnPressed=false;

            nextShape=shapes[G.rnd(7)];
            nextShape.fakeShape=true;
            nextShape.loc.set(nXOffset,nYOffset); //Created boolean "fakeShape" to prevent copying to well with out of bounds index
        }

        public static Shape cds = new Shape(new int[] {0,0, 0,0, 0,0, 0,0}, 0);
        public static boolean collisionDetected (){
            for(int i = 0; i<4; i++){
                G.V v = cds.a[i];
                if(v.x<0 ||v.x>=W||v.y<0||v.y>=H){return true;}
                if(well[v.x][v.y]!=iBkColor && well[v.x][v.y]!=zap){return true;}
            }
            return false;
        }

        public void cdsSet(){for(int i=0; i<4; i++){cds.a[i].set(a[i]);cds.a[i].add(loc);}}
        public void cdsGet(){for(int i=0; i<4; i++){a[i].set(cds.a[i]);}}
        public void cdsAdd(G.V v){for(int i=0; i<4; i++){cds.a[i].add(v);}}
        public void slide(G.V dx){
            cdsSet();
            cdsAdd(dx);
            if(collisionDetected()){return;}
            loc.add(dx);
        }
    }
}
