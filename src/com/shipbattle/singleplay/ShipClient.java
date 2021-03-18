package com.shipbattle.singleplay;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ship Battle Main Class
 */

public class ShipClient extends Frame {

    public static final int GAME_WIDTH =800;//screen width
    public static final int GAME_HEIGHT=600;//screen height

    //create our ship
    private Ship myShip =new Ship(300,300,false, Direction.Stop,this);
    //Used to store multiple ships
    private List<Ship> enemyShips =new ArrayList<>();
    //Create a List to store the explosion
    private List<Explode> explodes=new ArrayList<>();
    //Create a List to store multiple shells.
    private List<Missile> missiles=new ArrayList<>();

    //create wall
    private Wall wall1=new Wall(100,150,20,200,this);
    private Wall wall2=new Wall(300,150,200,20,this);




    //show blood
    private Blood blood=new Blood();

    //virtual screen
    private Image offScreenImage =null;

    /**
     * update method
     * @param g
     */
    @Override
    public void update(Graphics g) {
        if(offScreenImage ==null){
            offScreenImage =this.createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        Graphics gOffScreen= offScreenImage.getGraphics();
        Color c=gOffScreen.getColor();
        gOffScreen.setColor(Color.BLACK);
        gOffScreen.fillRect(0,0, GAME_WIDTH, GAME_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage,0,0,null);
    }

    /**
     * Drawing method, draw ships and shells
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        Color color=g.getColor();
        g.setColor(Color.GREEN);
        g.drawString("missile count:"+missiles.size(),650,50);
        g.drawString("enemyShip count:"+ enemyShips.size(),650,70);
        g.drawString("myShip life point:"+ myShip.getLife(),650,90);
        g.setColor(color);
        //Draw missile
        for(int i=0;i<missiles.size();i++){
            Missile m=missiles.get(i);
            m.hitShips(enemyShips);
            m.hitShip(myShip);
            m.hitWall(wall1);
            m.hitWall(wall2);
            m.draw(g);
        }
        //Draw explpde
        for(int i=0;i<explodes.size();i++){
            Explode explode=explodes.get(i);
            explode.draw(g);
        }

        //Draw ship
        myShip.eatBlood(blood);
        myShip.draw(g);


        for(int i = 0; i< enemyShips.size(); i++){
            Ship ship= enemyShips.get(i);
            ship.draw(g);
            ship.collidesWithWall(wall1);
            ship.collidesWithWall(wall2);
            ship.collidesWithShips(enemyShips);
        }

        //Draw wall
        wall1.draw(g);
        wall2.draw(g);
        //Draw blood
        //blood.draw(g);

    }

    /**
     * Start the program to display graphics and monitor functions
     */
    public void launchFrame(){
        this.setLocation(300,50);
        this.setSize(GAME_WIDTH, GAME_HEIGHT);
        this.setTitle("Ship Battle");


        //Create some enemy ships
        createShips();
        this.setBackground(Color.BLACK);//Set background color
        this.setResizable(false);//Prohibit changing window size
        this.setVisible(true);

        //Keyboard listener
        this.addKeyListener(new KeyMonitor());

        //Increasing response events
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Create a thread to move the ship position
        new Thread(new PaintThread()).start();
    }

    /**
     * Create some enemy ships
     */
    public void createShips(){
        Random random=new Random();
        int shipNum=random.nextInt(8)+3;
        int randX=0;
        int randY=0;
        for (int i = 0; i <shipNum; i++) {
            randX=random.nextInt(400);
            randY=random.nextInt(120);
            Ship ship=new Ship(40+2*randX,50+5*randY,true,Direction.Down,this);
            if(!ship.collidesWithWall(wall1)&&!ship.collidesWithWall(wall2)) //不与墙相撞
                enemyShips.add(ship);
        }
    }

    /**
     * Add a missile to the list
     * @param missile
     */
    public void addMissile(Missile missile){
        missiles.add(missile);
    }
    /**
     * Add a explode to the list
     * @param explode
     */
    public void addExplode(Explode explode){
        explodes.add(explode);
    }
    /**
     * Add a enemyship to the list
     * @param ship
     */
    public void addEnemyShip(Ship ship){
        enemyShips.add(ship);
    }
    /**
     * remove a enemyship from the list
     * @param ship
     */
    public void removeEnemyShip(Ship ship){
        enemyShips.remove(ship);
    }
    /**
     * remove a explode from the list
     * @param explode
     */
    public void removeExplode(Explode explode){
        explodes.remove(explode);
    }
    /**
     * remove a missile from the list
     * @param missile
     */
    public void removeMissile(Missile missile){
        missiles.remove(missile);
    }

    //Create a thread to redraw the interface every once in a while
    private class PaintThread implements Runnable{
        @Override
        public void run() {
            while (true){
                //repaint will first call the update method, and then call the paint method
                repaint();//Call the repaint method of the outer packaging class
                try {
                    //Redraw the interface after a certain period of rest
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //Keyboard monitor class
    private class KeyMonitor extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            myShip.keyPress(e);//Press the arrow keys on the keyboard and the ship reacts
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //Let go of the keyboard and respond
            myShip.keyRelease(e);
        }
    }



    /**
     * Resurrect our ship
     */
    public void reviveMyShip(){
        myShip.setLive(true);
        myShip.setLife(100);
    }

    /**
     * Turn our ship into an invincible state
     */
    public void invincible(){
        myShip.setLive(true);
        myShip.setLife(-1);
    }

    /**
     * clear All enemy
     */
    public void clearAllEnemy(){
        for (int i = 0; i < enemyShips.size(); i++) {
            enemyShips.clear();
        }
    }
}
