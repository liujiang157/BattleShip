package com.shipbattle.client;



import com.shipbattle.message.MissileNewMessage;
import com.shipbattle.message.ShipMoveMessage;
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
 * 船的类
 */
public class Ship {

    private int shipID;//船id
    //船的坐标
    private int x;
    private int y;
    //船上一步的位置
    private int oldX;
    private int oldY;

    //船宽度和高度
    public static final int WIDTH=30;
    public static final int HEIGHT=30;
    //船的速度
    private int xSpeed=8;
    private int ySpeed=8;
    //判断按下的方向键，上、下、左右
    private boolean isUpKey=false,isDownKey=false,isLeftKey=false,isRightKey=false;
    //控制船的方向
    private Direction direction= Direction.Down;
    //定义船大炮方向
    private Direction gunDirection= Direction.Down;
    //获取到ShipClient对象
    private ShipClient shipClient;

    private boolean isEnemy;//判断是否是敌方船
    private boolean isLive=true;//判断船是否存活
    private int life=100;//设置船生命值
    private BloodBar bloodBar=new BloodBar();//显示船血条

    private static Random random=new Random();//创建一个随机数产生器
    private static Direction[] directions= Direction.values();//用来让船随机选择运动的方向的数组
    private int step=random.nextInt(12)+3;//敌方每次选定方向后运动的距离


    private static Toolkit toolkit=Toolkit.getDefaultToolkit();//工具包
    //船的图片
    private static Image[] shipImages=null;
    private static Map<String,Image> shipImagesMap =new HashMap<>();


    private static URL music = null;

    private static Media _media;

    final static JFXPanel fxPanel = new JFXPanel();

    private static MediaPlayer _mediaPlayer;

    /**
     * 静态代码块，用来加载资源
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
        //将图片加载map中，方便查询
        shipImagesMap.put("Up", shipImages[0]);
        shipImagesMap.put("Down", shipImages[1]);
        shipImagesMap.put("Left", shipImages[2]);
        shipImagesMap.put("Right", shipImages[3]);
        shipImagesMap.put("LeftAndUp", shipImages[4]);
        shipImagesMap.put("LeftAndDown", shipImages[5]);
        shipImagesMap.put("RightAndUP", shipImages[6]);
        shipImagesMap.put("RightAndDown", shipImages[7]);

        music = Ship.class.getClassLoader().getResource("music/sea.mp3");
        music = Ship.class.getClassLoader().getResource("music/sea.mp3");
        try {
            _media = new Media(music.toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        _mediaPlayer = new MediaPlayer(_media);
        _mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    private static boolean isplay = false;

    public Ship(int x, int y, boolean isEnemy, Direction direction, ShipClient shipClient) {
        this.x = x;
        this.y = y;
        this.oldX=x;
        this.oldY=y;
        this.isEnemy =isEnemy;
        this.direction=direction;
        this.shipClient = shipClient;
    }

    public int getShipID() {
        return shipID;
    }

    public void setShipID(int shipID) {
        this.shipID = shipID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * 返回船是否是敌方的
     * @return
     */
    public boolean isEnemy() {
        return isEnemy;
    }

    /**
     * 返回船的存活状态
     * @return
     */
    public boolean isLive() {
        return isLive;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setEnemy(boolean enemy) {
        isEnemy = enemy;
    }

    /**
     * 用来画船的方法
     * @param g
     */
    public void draw(Graphics g){

        //如果船已经被消灭
        if(!isLive) {
            if(isEnemy)//敌方船
                shipClient.removeEnemyShip(this);
            return;
        }

        move();//船移动

        //绘制血条
        bloodBar.draw(g);


        //根据船炮筒的方向画出船来
        switch (gunDirection) {
            case Up://上
                g.drawImage(shipImagesMap.get("Up"),x,y,null);
                break;
            case Down://下
                g.drawImage(shipImagesMap.get("Down"),x,y,null);
                break;
            case Left://左
                g.drawImage(shipImagesMap.get("Left"),x,y,null);
                break;
            case Right://右
                g.drawImage(shipImagesMap.get("Right"),x,y,null);
                break;
            case LeftAndUp://左上
                g.drawImage(shipImagesMap.get("LeftAndUp"),x,y,null);
                break;
            case LeftAndDown://左下
                g.drawImage(shipImagesMap.get("LeftAndDown"),x,y,null);
                break;
            case RightAndUp://右上
                g.drawImage(shipImagesMap.get("RightAndUP"),x,y,null);
                break;
            case RightAndDown://右下
                g.drawImage(shipImagesMap.get("RightAndDown"),x,y,null);
                break;
        }

    }

    private void move(){

        //记录船上一步的位置
        this.oldX=x;
        this.oldY=y;

        //根据船的方向控制船移动
        switch (direction){
            case Up://上
                y-=ySpeed;
                break;
            case Down://下
                y+=ySpeed;
                break;
            case Left://左
                x-=xSpeed;
                break;
            case Right://右
                x+=xSpeed;
                break;
            case LeftAndUp://左上
                x-=xSpeed;
                y-=ySpeed;
                break;
            case LeftAndDown://左下
                x-=xSpeed;
                y+=ySpeed;
                break;
            case RightAndUp://右上
                x+=xSpeed;
                y-=ySpeed;
                break;
            case RightAndDown://右下
                x+=xSpeed;
                y+=ySpeed;
                break;
            case Stop://停止
                break;
        }

        //改变炮筒方向
        if(direction!= Direction.Stop)
            this.gunDirection=this.direction;

        //阻止船出界
        if(x<0)
            x=0;
        if(y<30)
            y=30;
        if(x+WIDTH> ShipClient.GAME_WIDTH)
            x= ShipClient.GAME_WIDTH-WIDTH;
        if(y+HEIGHT> ShipClient.GAME_HEIGHT)
            y= ShipClient.GAME_HEIGHT-HEIGHT;

    }

    /**
     * 按下方向键，船做出相应反应
     * @param e
     */
    public void keyPress(KeyEvent e){
        int key=e.getKeyCode();
        switch (key){
            case KeyEvent.VK_LEFT://按下了左方向键
                isLeftKey=true;
                break;
            case KeyEvent.VK_UP://按下了上方向键
                isUpKey=true;
                break;
            case KeyEvent.VK_RIGHT://按下了右方向键
                isRightKey=true;
                break;
            case KeyEvent.VK_DOWN://按下了下方向键
                isDownKey=true;
                break;
        }
        locateDirection();//重新确定船方向
    }

    /**
     * 手离开键盘
     * @param e
     */
    public void keyRelease(KeyEvent e) {
        int key=e.getKeyCode();
        switch (key){
            case KeyEvent.VK_CONTROL://按下control键
                fire();//发射炮弹
                break;
            case KeyEvent.VK_LEFT://放开了左方向键
                isLeftKey=false;
                break;
            case KeyEvent.VK_UP://放开了上方向键
                isUpKey=false;
                break;
            case KeyEvent.VK_RIGHT://放开了右方向键
                isRightKey=false;
                break;
            case KeyEvent.VK_DOWN://放开了下方向键
                isDownKey=false;
                break;
            case KeyEvent.VK_A:
                superFire();//超级火力
                break;
            case KeyEvent.VK_M:
                playmusic();
                break;
        }
        locateDirection();//重新确定船方向
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

    //确定船方向
    private void locateDirection(){

        Direction oldDirection=direction;

        if(isUpKey&&!isDownKey&&!isLeftKey&&!isRightKey)
            direction= Direction.Up;//上
        else if(!isUpKey&&isDownKey&&!isLeftKey&&!isRightKey)
            direction= Direction.Down;//下
        else if(!isUpKey&&!isDownKey&&isLeftKey&&!isRightKey)
            direction= Direction.Left;//左
        else if(!isUpKey&&!isDownKey&&!isLeftKey&&isRightKey)
            direction= Direction.Right;//右
        else if(isUpKey&&!isDownKey&&isLeftKey&&!isRightKey)
            direction= Direction.LeftAndUp;//左上
        else if(!isUpKey&&isDownKey&&isLeftKey&&!isRightKey)
            direction= Direction.LeftAndDown;//左下
        else if(isUpKey&&!isDownKey&&!isLeftKey&&isRightKey)
            direction= Direction.RightAndUp;//右上
        else if(!isUpKey&&isDownKey&&!isLeftKey&&isRightKey)
            direction= Direction.RightAndDown;//右下
        else
            direction= Direction.Stop;//停止

        if(direction!=oldDirection) {
            ShipMoveMessage moveMessage=new ShipMoveMessage(shipID,x,y,direction);
            shipClient.getConnect().send(moveMessage);
        }
    }


    /**
     * 发射炮弹
     */
    public void fire(){
        if(!isLive)//死后不能发射炮弹
            return;
        int x=this.x+WIDTH/2- Missile.WIDTH/2;
        int y=this.y+HEIGHT/2+ Missile.HEIGHT/2;
        Missile missile=new Missile(shipID,x,y,isEnemy,gunDirection,shipClient);//根据炮筒方向开炮
        shipClient.addMissile(missile);

        MissileNewMessage missileNewMessage=new MissileNewMessage(missile);
        shipClient.getConnect().send(missileNewMessage);
    }

    /**
     * 根据方向发射炮弹
     * @param direction
     */
    private void fireByDirection(Direction direction){
        if(!isLive)//死后不能发射炮弹
            return;
        int x=this.x+WIDTH/2- Missile.WIDTH/2;
        int y=this.y+HEIGHT/2+ Missile.HEIGHT/2;
        Missile missile=new Missile(shipID,x,y,isEnemy,direction,shipClient);//根据炮筒方向开炮
        shipClient.addMissile(missile);

        MissileNewMessage missileNewMessage=new MissileNewMessage(missile);
        shipClient.getConnect().send(missileNewMessage);
    }

    /**
     * 按键A，向八个方向发射炮弹
     */
    public void superFire(){
        for (int i = 0; i <8; i++) {
            fireByDirection(directions[i]);
        }
    }

    /**
     *辅助计算船是否被炮弹击中的矩形
     * @return
     */
    public Rectangle getRectangle(){
        return new Rectangle(x,y,WIDTH,HEIGHT);
    }

    //设置船的存活状态
    public void setLive(boolean live) {
        isLive = live;
    }

    /**
     * 船位置移动一步
     */
    private void backStep(){
        this.x=this.oldX;
        this.y=this.oldY;
    }

    /**
     * 判断船是否撞墙
     * @param wall
     * @return
     */
    public boolean collidesWithWall(Wall wall){
        if(this.isLive&&this.getRectangle().intersects(wall.getRectangle())) {
            //船移动一步位置
            backStep();
            return true;
        }
        return false;
    }

    /**
     * 判断船之间是否相撞
     * @param ships
     * @return
     */
    public boolean collidesWithShips(List<Ship> ships){
        for (int i = 0; i <ships.size(); i++) {
            Ship ship=ships.get(i);
            if(this!=ship&&this.isLive&&ship.isLive()&&this.getRectangle().intersects(ship.getRectangle())) {
                //船回退到上一步的位置
                this.backStep();
                ship.backStep();
                return true;
            }
        }
        return false;
    }




    /**
     * 获取船生命值
     * @return
     */
    public int getLife() {
        return life;
    }

    /**
     * 设置船生命值
     * @param life
     */
    public void setLife(int life) {
        this.life = life;
    }

    /**
     * 显示船血条的类
     */
    private class BloodBar{

        public void draw(Graphics g){
            Color color=g.getColor();
            if(!isEnemy)
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.RED);
            g.drawRect(x+10,y-10,WIDTH,10);
            int width=WIDTH*life/100;
            g.fillRect(x+10,y-10,width,10);
            g.setColor(color);
        }
    }

}
