package com.shipbattle.singleplay;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Ship's Class
 */
public class Ship {

    //Ship coordinates
    private int x;
    private int y;
    //The position of the ship's previous step
    private int oldX;
    private int oldY;
    private int count=30;//Used to count the number of steps the ship has not moved in place
    //Ship width and height
    public static final int WIDTH=30;
    public static final int HEIGHT=30;
    //Ship speeds
    private int xSpeed=8;
    private int ySpeed=8;
    //Determine the direction key pressed, up, down, left and right
    private boolean isUpKey=false,isDownKey=false,isLeftKey=false,isRightKey=false;
    //Control the direction of the ship
    private Direction direction= Direction.Down;
    //Define the direction of the ship and artillery
    private Direction gunDirection= Direction.Down;
    //get ShipClient object
    private ShipClient shipClient;

    private boolean isEnemy;//Determine whether it is an enemy ship
    private boolean isLive=true;//Determine if the ship is alive
    private int life=100;//Set ship health
    private BloodBar bloodBar=new BloodBar();//show ship's blood

    private static Random random=new Random();//Create a random number generator
    private static Direction[] directions= Direction.values();//The array used to let the ship randomly choose the direction of movement
    private int step=random.nextInt(12)+3;//The distance the enemy moves each time after choosing a direction


    private static Toolkit toolkit=Toolkit.getDefaultToolkit();//util package
    //ship's image
    private static Image[] shipImages =null;
    private static Map<String,Image> shipImagesMap =new HashMap<>();

    private static URL music = null;

    private static Media _media;

    final static JFXPanel fxPanel = new JFXPanel();

    private static MediaPlayer _mediaPlayer;

    private static boolean isplay = false;//default is false
    /**
     * static code
     */
    static {
        shipImages =new Image[]{
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipU.png")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipD.png")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipL.png")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipR.png")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipLU.png")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipLD.png")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipRU.png")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/shipRD.png"))
        };
        //Load the picture into the map for easy query
        shipImagesMap.put("Up", shipImages[0]);
        shipImagesMap.put("Down", shipImages[1]);
        shipImagesMap.put("Left", shipImages[2]);
        shipImagesMap.put("Right", shipImages[3]);
        shipImagesMap.put("LeftAndUp", shipImages[4]);
        shipImagesMap.put("LeftAndDown", shipImages[5]);
        shipImagesMap.put("RightAndUP", shipImages[6]);
        shipImagesMap.put("RightAndDown", shipImages[7]);

        music = com.shipbattle.client.Ship.class.getClassLoader().getResource("music/sea.mp3");
        try {
            _media = new Media(music.toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        _mediaPlayer = new MediaPlayer(_media);
        _mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    public Ship(int x, int y, boolean isEnemy, Direction direction, ShipClient shipClient) {
        this.x = x;
        this.y = y;
        this.oldX=x;
        this.oldY=y;
        this.isEnemy =isEnemy;
        this.direction=direction;
        this.shipClient = shipClient;
    }

    /**
     * Return whether the ship is enemy
     * @return
     */
    public boolean isEnemy() {
        return isEnemy;
    }

    /**
     * Return to the survival state of the ship
     * @return
     */
    public boolean isLive() {
        return isLive;
    }

    /**
     * draw ship method
     * @param g
     */
    public void draw(Graphics g){

        //If the ship has been destroyed
        if(!isLive) {
            if(isEnemy)// Enemy ship
                shipClient.removeEnemyShip(this);
            return;
        }

        move();//Ship move

        if(!isEnemy)//draw bloodBar
            bloodBar.draw(g);

        switch (gunDirection) {
            case Up:
                g.drawImage(shipImagesMap.get("Up"),x,y,null);
                break;
            case Down:
                g.drawImage(shipImagesMap.get("Down"),x,y,null);
                break;
            case Left:
                g.drawImage(shipImagesMap.get("Left"),x,y,null);
                break;
            case Right:
                g.drawImage(shipImagesMap.get("Right"),x,y,null);
                break;
            case LeftAndUp:
                g.drawImage(shipImagesMap.get("LeftAndUp"),x,y,null);
                break;
            case LeftAndDown:
                g.drawImage(shipImagesMap.get("LeftAndDown"),x,y,null);
                break;
            case RightAndUp:
                g.drawImage(shipImagesMap.get("RightAndUP"),x,y,null);
                break;
            case RightAndDown:
                g.drawImage(shipImagesMap.get("RightAndDown"),x,y,null);
                break;
        }

    }

    private void move(){

        //Record the position of the last step of the ship
        this.oldX=x;
        this.oldY=y;

        //Control the movement of the ship according to the direction of the ship
        switch (direction){
            case Up:
                y-=ySpeed;
                break;
            case Down:
                y+=ySpeed;
                break;
            case Left:
                x-=xSpeed;
                break;
            case Right:
                x+=xSpeed;
                break;
            case LeftAndUp:
                x-=xSpeed;
                y-=ySpeed;
                break;
            case LeftAndDown:
                x-=xSpeed;
                y+=ySpeed;
                break;
            case RightAndUp:
                x+=xSpeed;
                y-=ySpeed;
                break;
            case RightAndDown:
                x+=xSpeed;
                y+=ySpeed;
                break;
            case Stop:
                break;
        }

        if(direction!= Direction.Stop)
            this.gunDirection=this.direction;

        //Stop the ship from going out of bounds
        if(x<0)
            x=0;
        if(y<30)
            y=30;
        if(x+WIDTH> ShipClient.GAME_WIDTH)
            x= ShipClient.GAME_WIDTH-WIDTH;
        if(y+HEIGHT> ShipClient.GAME_HEIGHT)
            y= ShipClient.GAME_HEIGHT-HEIGHT;

        if(isEnemy) {//Enemy ships randomly change direction
            if(step==0){
                step=random.nextInt(12)+3;//The distance the enemy moves each time after choosing a direction
                int randomNum=random.nextInt(directions.length);
                direction=directions[randomNum];
            }

            //Use random numbers to control how often the enemy ship fires shells
            if(random.nextInt(50)>35)
                this.fire();

            //Step minus one after each move
            step--;
        }
    }

    /**
     * Press the arrow keys and the ship will react accordingly
     * @param e
     */
    public void keyPress(KeyEvent e){
        int key=e.getKeyCode();
        switch (key){
            case KeyEvent.VK_LEFT:
                isLeftKey=true;
                break;
            case KeyEvent.VK_UP:
                isUpKey=true;
                break;
            case KeyEvent.VK_RIGHT:
                isRightKey=true;
                break;
            case KeyEvent.VK_DOWN:
                isDownKey=true;
                break;
        }
        locateDirection();//Reorient the ship
    }

    /**
     * release keyboard
     * @param e
     */
    public void keyRelease(KeyEvent e) {
        int key=e.getKeyCode();
        switch (key){
            case KeyEvent.VK_CONTROL:
                fire();
                break;
            case KeyEvent.VK_LEFT:
                isLeftKey=false;
                break;
            case KeyEvent.VK_UP:
                isUpKey=false;
                break;
            case KeyEvent.VK_RIGHT:
                isRightKey=false;
                break;
            case KeyEvent.VK_DOWN:
                isDownKey=false;
                break;
            case KeyEvent.VK_A:
                superFire();
                break;
            case KeyEvent.VK_F:
                shipClient.createShips();
                break;
            case KeyEvent.VK_L:
                shipClient.reviveMyShip();
                break;
            case KeyEvent.VK_I:
                shipClient.invincible();
                break;
            case KeyEvent.VK_C:
                shipClient.clearAllEnemy();
                break;
            case KeyEvent.VK_M:
                playmusic();
                break;
        }
        locateDirection();//Reorient the ship
    }


    private void playmusic() {
        if(isplay){
            _mediaPlayer.stop();
            isplay = !isplay;
        }else {
            _mediaPlayer.play();
            isplay = !isplay;
        }
    }


    //Determine the direction of the ship
    private void locateDirection(){

        if(isUpKey&&!isDownKey&&!isLeftKey&&!isRightKey)
            direction= Direction.Up;
        else if(!isUpKey&&isDownKey&&!isLeftKey&&!isRightKey)
            direction= Direction.Down;
        else if(!isUpKey&&!isDownKey&&isLeftKey&&!isRightKey)
            direction= Direction.Left;
        else if(!isUpKey&&!isDownKey&&!isLeftKey&&isRightKey)
            direction= Direction.Right;
        else if(isUpKey&&!isDownKey&&isLeftKey&&!isRightKey)
            direction= Direction.LeftAndUp;
        else if(!isUpKey&&isDownKey&&isLeftKey&&!isRightKey)
            direction= Direction.LeftAndDown;
        else if(isUpKey&&!isDownKey&&!isLeftKey&&isRightKey)
            direction= Direction.RightAndUp;
        else if(!isUpKey&&isDownKey&&!isLeftKey&&isRightKey)
            direction= Direction.RightAndDown;
        else
            direction= Direction.Stop;
    }


    /**
     * Fire a missile
     */
    public void fire(){
        if(!isLive)//Cannonballs cannot be fired after death
            return;
        int x=this.x+WIDTH/2- Missile.WIDTH/2;
        int y=this.y+HEIGHT/2+ Missile.HEIGHT/2;
        Missile missile=new Missile(x,y,isEnemy,gunDirection, shipClient);
        shipClient.addMissile(missile);
    }

    /**
     * @param direction
     */
    private void fireByDirection(Direction direction){
        if(!isLive)//Cannonballs cannot be fired after death
            return;
        int x=this.x+WIDTH/2- Missile.WIDTH/2;
        int y=this.y+HEIGHT/2+ Missile.HEIGHT/2;
        Missile missile=new Missile(x,y,isEnemy,direction, shipClient);
        shipClient.addMissile(missile);
    }

    /**
     * superFire
     */
    public void superFire(){
        for (int i = 0; i <8; i++) {
            fireByDirection(directions[i]);
        }
    }

    /**
     * A rectangle that assists in calculating whether the ship was hit by a shell
     * @return
     */
    public Rectangle getRectangle(){
        return new Rectangle(x,y,WIDTH,HEIGHT);
    }

    //Set the survival status of the ship
    public void setLive(boolean live) {
        isLive = live;
    }

    /**
     * Move the ship position by one step
     */
    private void backStep(){
        if(x==oldX&&y==oldY&&(--count)==0){
            if(x+xSpeed< ShipClient.GAME_WIDTH- com.shipbattle.singleplay.Ship.WIDTH){
                direction= Direction.Right;
            }else if(y+ySpeed< ShipClient.GAME_HEIGHT- com.shipbattle.singleplay.Ship.HEIGHT){
                direction= Direction.Down;
            }else if(x-xSpeed>0){
                direction= Direction.Left;
            }else if(y-ySpeed>0){
                direction= Direction.Up;
            }else {
                int r=random.nextInt(8);
                direction=directions[r];
            }
            count=30;
        }else {
            this.x=this.oldX;
            this.y=this.oldY;
        }
    }

    /**
     * Determine if the ship hits the wall
     * @param wall
     * @return
     */
    public boolean collidesWithWall(Wall wall){
        if(this.isLive&&this.getRectangle().intersects(wall.getRectangle())) {
            //The ship moves one step position
            backStep();
            return true;
        }
        return false;
    }

    /**
     * Determine whether the ships collided
     * @param ships
     * @return
     */
    public boolean collidesWithShips(List<Ship> ships){
        for (int i = 0; i <ships.size(); i++) {
            Ship ship=ships.get(i);

            if(this!=ship&&this.isLive&&ship.isLive()&&this.getRectangle().intersects(ship.getRectangle())) {
                //The ship retreats to the position of the previous step
                this.backStep();
                ship.backStep();
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public int getLife() {
        return life;
    }

    /**
     *
     * @param life
     */
    public void setLife(int life) {
        this.life = life;
    }

    /**
     *  the class of Showing ship health bars
     */
    private class BloodBar{

        public void draw(Graphics g){
            Color color=g.getColor();
            g.setColor(Color.RED);
            g.drawRect(x+10,y-10,WIDTH,10);
            int width=WIDTH*life/100;
            g.fillRect(x+10,y-10,width,10);
            g.setColor(color);
        }
    }

    /**
     *
     * @param blood
     * @return
     */
    public boolean eatBlood(Blood blood){

        if(this.isLive&&blood.isLive()&&this.getRectangle().intersects(blood.getRectangle())){
            boolean isNeedBlood=(this.life<=0&&this.isLive)||(this.life==100);
            //The ship is not in an invincible state or full of health, at this time the ship needs to return blood
            if(!isNeedBlood) {
                this.life=100;//Return to full blood
                blood.setLive(false);
                return true;
            }
        }
        return false;
    }
}
