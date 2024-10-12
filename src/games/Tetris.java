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
    public static int scoreMultiplier = 1, levelMultiplier = 1, lineMultiplier = 0, lastLineMultiplier;
    public static boolean challengeMode = false;
    public static final int POINT_VALUE = 10;
    public static final int LINE_CLEAR_VALUE = 100;
    public static final int LEVEL_THRESHOLD = 50000;
    public static final int LABEL_Y_OFFSET = 100;
    public static final int LABEL_X_OFFSET = 300;
    public static final Color GAME_FONT_COLOR=Color.WHITE, GAME_LABEL_COLOR=Color.RED,LABEL_FONT_COLOR=Color.BLACK;
    public static final Color LEVEL_WALL_COLOR=Color.DARK_GRAY, BREAKING_COLOR=Color.PINK;

    //Control Speed
    public static int gameDelay = 100;
    public static boolean gameIsOver=false, gamePaused=false;
    public static boolean breakingBricks=false, advancingLevel=false;
    public static int time=0, inputDelay=0, aniBreakFrame=0, aniLevelUpFrame=0; //iShape =0;

    //Key check
    public static boolean dnPressed = false, upPressed = false, spcPressed = false;
    public static boolean lfPressed = false, rtPressed = false, btnPressed = false;

    //Next Shape
    public static Shape nextShape;
    public static int nextShapeIndex = 0; //Using 2 different shape arrays for shape and next shape
    public static final int nXOffset = 12, nYOffset = 6;
    //****************************



    public static Timer timer;
    public static final int xM = 50, yM = 100;
    public static final int H =20, W =10, C = 25;
    public static final int NUM_SHAPES = 7;
    public static Color[] color = {Color.RED,Color.GREEN,Color.BLUE,Color.ORANGE,Color.CYAN,Color.YELLOW,Color.MAGENTA,Color.LIGHT_GRAY,Color.BLACK,Color.BLACK,Color.BLACK};
    public static Shape[] shapes = {Shape.Z,Shape.S,Shape.J, Shape.L,Shape.I,Shape.O,Shape.T};
    public static Shape[] shapes2 = {Shape.Z2,Shape.S2,Shape.J2, Shape.L2,Shape.I2,Shape.O2,Shape.T2};
    public static Shape shape;
    public static final int greyColor=7, iBkColor=8, zap=9, zapAnimate=10;
    public static int[][] well = new int[W][H];

    public Tetris() {
        super("Tetris", 1000, 700);
        startNewGame();
        timer = new Timer(0,  this);
        timer.start();
    }

    public void startNewGame(){

        //Generate next shapes:
        nextShapeIndex= G.rnd(NUM_SHAPES);
        nextShape=shapes2[nextShapeIndex]; //Next shape
        nextShape.loc.set(nXOffset,nYOffset);
        nextShape.fakeShape=true;
        shape=shapes[G.rnd(NUM_SHAPES)];

        //reset score:
        levelMultiplier=1;
        scoreMultiplier=1;
        score=0;

        //reset board
        gameIsOver=false;
        clearWell();

        //generate gray blocks if challenge mode is on
        if(challengeMode){randomizeWell(levelMultiplier,2);}

    }


    public void paintComponent(Graphics g){
        if(!gameIsOver){
            if(!gamePaused){
                G.fillBack(g); //clears the screen
                if(!breakingBricks && !advancingLevel){ //Ensure no actions while animations occurring

                    inputDelay++;inputDelay=registerPlayerInput(inputDelay,4); //Player input will reset the delay timer
                    time++;if(time>=gameDelay){time=0;shape.drop();} //Controls the speed of the game
                }
                unzapWell();
                showWell(g);
                shape.show(g);
                nextShape.show(g);
                showLabels(g);//Show score
                //Placed under showWell for timing purposes
                if(breakingBricks){
                    aniBreakFrame++;
                    aniBreakFrame = animateZapWell(g,aniBreakFrame);
                }else if(advancingLevel){
                    aniLevelUpFrame++;
                    aniLevelUpFrame = animateLevelUp(g,aniLevelUpFrame);
                    showGameMessage(g, new String[] {"LEVEL UP", "Next: " + levelMultiplier});
                }
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
        String gameObjective = challengeMode? "- Clear the gray blocks to advance": "- Score Challenge";
        Font gFont = new Font("Courier New",Font.BOLD,fSize);
        g.setColor(LABEL_FONT_COLOR);
        g.setFont(gFont);
        g.drawString("Level: " + levelMultiplier + " " + gameObjective,xM,yM-5);
        g.drawString("Last line Combo: " + lastLineMultiplier,xM+LABEL_X_OFFSET,yM+LABEL_Y_OFFSET-(fSize*2)-10);
        g.drawString("Key multiplier x" + scoreMultiplier,xM+LABEL_X_OFFSET,yM+LABEL_Y_OFFSET-fSize-10);
        g.drawString("Score: " + score,xM+LABEL_X_OFFSET,yM+LABEL_Y_OFFSET);
        g.drawString("Next Shape: ",xM+LABEL_X_OFFSET,yM+LABEL_Y_OFFSET+fSize+10);
    }
    public void showGameMessage(Graphics g, String[] message){
        g.setColor(GAME_LABEL_COLOR);
        g.fillRect(xM,yM+200,W*C,65);
        Font gFont1 = new Font("Courier New",Font.BOLD,45);
        Font gFont2 = new Font("Courier New",Font.BOLD,25);
        g.setColor(GAME_FONT_COLOR);
        g.setFont(gFont1);
        g.drawString(message[0],xM,yM+230);
        g.setFont(gFont2);
        g.drawString(message[1],xM,yM+260);
    }
    public static void checkLevel(){
        if(challengeMode){
            if(!checkForGrey()){
                score+=LEVEL_THRESHOLD*levelMultiplier;
                increaseLevel();
            }
        }else if(score-lastScore>(LEVEL_THRESHOLD*levelMultiplier)){
            increaseLevel();
            lastScore=score;
        }
    }
    public static void increaseLevel(){
        levelMultiplier++;
        advancingLevel=true;
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
    public static void randomizeWell(int layers, int c){
        for(int x=0;x<W;x++){
            for(int y=(H-1);y>=H-layers;y--){
                well[x][y]= 8 - G.rnd(c);
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
        //Called whenever there is a collision after a drop
        int rowsZapped = 0;
        for(int y=0;y<H;y++){
            rowsZapped += zapRow(y); //Count number of rows zapped
        }
        //****David Ross****
        if(rowsZapped==0) {
            lineMultiplier = 0;
        }else{
            lineMultiplier+=rowsZapped; //Every consecutive tetris racks up a line combo
            lastLineMultiplier = lineMultiplier;
            score+=levelMultiplier*scoreMultiplier*LINE_CLEAR_VALUE * (lineMultiplier*lineMultiplier); //square the line multiplier for higher bonuses the higher the combo
        }
        scoreMultiplier=1; //reset the bonus for pressing the down key
        checkLevel(); //Check the score and increase difficulty
        //******************
    }
    public static int zapRow(int y){
        int numZapped = 0;
        for(int x=0;x<W;x++){if(well[x][y]==iBkColor){return numZapped;}}
        numZapped++;
        breakingBricks=true;
        for(int x=0;x<W;x++){well[x][y]=zapAnimate;}

        return numZapped;
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
    //******************David Ross******************************
    public static boolean checkForGrey(){
        for(int y=0;y<H;y++){
            for(int x=0;x<W;x++){
                if(well[x][y]==greyColor){return true;}
            }
        }
        return false;

    }
    public static int animateLevelUp(Graphics g,int frame){
        int aniFactor = 10;
        int deltaY = frame * aniFactor;
        int pxHeight = H*C;
        int moveFactor = deltaY%pxHeight;
        if(deltaY>=pxHeight) {
            //update level while shield is up:
            if(challengeMode){
                randomizeWell(levelMultiplier,2);
            }else{
                clearWell();
            }
            if(pxHeight-moveFactor<=aniFactor){
                advancingLevel=false;
                return 0;
            }

            moveFactor=pxHeight-moveFactor;
        }
        g.setColor(LEVEL_WALL_COLOR);
        g.fillRect(xM,yM+(pxHeight - moveFactor),W*C,moveFactor);

        return frame;
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

            g.setColor(BREAKING_COLOR);
            g.fillRect(xM + C*x,yM + (C*y+(C-(C/f))),C,C/f);
            g.setColor(Color.BLACK);
            g.drawRect(xM + C*x,yM + (C*y+(C-(C/f))),C,C/f);
        }
    }
    public static void finishAnimateZapRow(int y){
        for(int x=0;x<W;x++){if(well[x][y]==zapAnimate){well[x][y]=zap;}} //return; removed return
        //for(int x=0;x<W;x++){well[x][y]=zap;}
        breakingBricks=false;
    }

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
        public static Shape Z, S, J, L, I, O, T,Z2, S2, J2, L2, I2, O2, T2;

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

            Z2=new Shape(new int[] {0,0, 1,0, 1,1, 2,1},0);
            S2=new Shape(new int[] {0,1, 1,0, 1,1, 2,0},1);
            J2=new Shape(new int[] {0,0, 0,1, 1,1, 2,1},2);
            L2=new Shape(new int[] {0,1, 1,1, 2,1, 2,0},3);
            I2=new Shape(new int[] {0,0, 1,0, 2,0, 3,0},4);
            O2=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},5);
            T2=new Shape(new int[] {0,1, 1,0, 1,1, 2,1},6);

            //Just O blocks for Testing:
            /*Z=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},0);
            S=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},1);
            J=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},2);
            L=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},3);
            I=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},4);
            O=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},5);
            T=new Shape(new int[] {0,0, 1,0, 0,1, 1,1},6);*/
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
            shape=shapes[nextShapeIndex];
            shape.loc.set(4,0);
            shape.fakeShape=false;
            dnPressed=false;

            nextShapeIndex= G.rnd(NUM_SHAPES);
            nextShape= shapes2[nextShapeIndex];
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
