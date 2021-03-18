package com.shipbattle.singleplay;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *炮弹的类
 */
public class Missile {
    //炮弹宽度和高度
    public static final int WIDTH =10;
    public static final int HEIGHT =10;
    //炮弹坐标
    private int x;
    private int y;
    //炮弹移动速度
    private int xSpeed=15;
    private int ySpeed=15;
    private Direction direction;//方向
    private boolean isLive =true;//炮弹存活还是死亡
    private ShipClient shipClient;//创建一个ShipClient对象

    private boolean isEnemy;//判断是否是敌方子弹

    private static Toolkit toolkit=Toolkit.getDefaultToolkit();//工具包
    //子弹的图片
    private static Image[] missileImages =null;
    private static Map<String,Image> missileImagesMap =new HashMap<>();

    /**
     * 静态代码块，用来加载资源
     */
    static {
        missileImages =new Image[]{
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileU.gif")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileD.gif")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileL.gif")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileR.gif")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileLU.gif")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileLD.gif")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileRU.gif")),
                toolkit.getImage(Ship.class.getClassLoader().getResource("images/missileRD.gif"))
        };
        //将图片加载map中，方便查询
        missileImagesMap.put("Up", missileImages[0]);
        missileImagesMap.put("Down", missileImages[1]);
        missileImagesMap.put("Left", missileImages[2]);
        missileImagesMap.put("Right", missileImages[3]);
        missileImagesMap.put("LeftAndUp", missileImages[4]);
        missileImagesMap.put("LeftAndDown", missileImages[5]);
        missileImagesMap.put("RightAndUP", missileImages[6]);
        missileImagesMap.put("RightAndDown", missileImages[7]);
    }

    public Missile(int x, int y, boolean isEnemy, Direction direction, ShipClient shipClient) {
        this.x = x;
        this.y = y;
        this.isEnemy=isEnemy;
        this.direction = direction;
        this.shipClient=shipClient;
    }

    //显示炮弹
    public void draw(Graphics g){

        if(!isLive) //炮弹消亡了
        {
            shipClient.removeMissile(this);
            return;
        }

        //根据子弹的方向画出子弹来
        switch (direction) {
            case Up://上
                g.drawImage(missileImagesMap.get("Up"),x+10,y,null);
                break;
            case Down://下
                g.drawImage(missileImagesMap.get("Down"),x+10,y,null);
                break;
            case Left://左
                g.drawImage(missileImagesMap.get("Left"),x+10,y,null);
                break;
            case Right://右
                g.drawImage(missileImagesMap.get("Right"),x+10,y,null);
                break;
            case LeftAndUp://左上
                g.drawImage(missileImagesMap.get("LeftAndUp"),x+10,y,null);
                break;
            case LeftAndDown://左下
                g.drawImage(missileImagesMap.get("LeftAndDown"),x+10,y,null);
                break;
            case RightAndUp://右上
                g.drawImage(missileImagesMap.get("RightAndUp"),x+10,y,null);
                break;
            case RightAndDown://右下
                g.drawImage(missileImagesMap.get("RightAndDown"),x+10,y,null);
                break;
        }

        //炮弹移动
        move();
    }

    //炮弹存活还是死亡
    public boolean isLive() {
        return isLive;
    }


    //炮弹移动
    private void move() {
        //根据船的方向发射炮弹
        switch (direction) {
            case Up://上
                y -= ySpeed;
                break;
            case Down://下
                y += ySpeed;
                break;
            case Left://左
                x -= xSpeed;
                break;
            case Right://右
                x += xSpeed;
                break;
            case LeftAndUp://左上
                x -= xSpeed;
                y -= ySpeed;
                break;
            case LeftAndDown://左下
                x -= xSpeed;
                y += ySpeed;
                break;
            case RightAndUp://右上
                x += xSpeed;
                y -= ySpeed;
                break;
            case RightAndDown://右下
                x += xSpeed;
                y += ySpeed;
                break;
        }

        //判断炮弹是否出了边界
        if(x<0||y<0||x> ShipClient.GAME_WIDTH||y> ShipClient.GAME_HEIGHT)
            isLive =false;

    }

    /**
     *辅助计算炮弹是否击中船的矩形
     * @return
     */
    public Rectangle getRectangle(){
        return new Rectangle(x,y,WIDTH,HEIGHT);
    }

    /**
     * 判断是否击中敌方船
     * @param ship
     * @return
     */
    public boolean hitShip(Ship ship){

        //炮弹活着并且船活着并且被敌方船击中了
        if(this.isLive&&ship.isLive()&&getRectangle().intersects(ship.getRectangle())&&this.isEnemy!=ship.isEnemy()){

            if(!ship.isEnemy()){//我方船
                //减去20的生命值
                ship.setLife(ship.getLife()-20);
                if(ship.getLife()==0)
                    ship.setLive(false);
            }else {//敌方船
                ship.setLive(false);
            }

            isLive=false;
            Explode explode=new Explode(x,y,shipClient);
            shipClient.addExplode(explode);
            return true;
        }
        return false;
    }

    public boolean hitShips(List<Ship> ships){
        for (int i = 0; i <ships.size(); i++) {
            if(hitShip(ships.get(i)))
                return true;
        }
        return false;
    }

    /**
     * 判断炮弹是否撞墙,如果撞墙，让子弹被消灭
     * @param wall
     * @return
     */
    public boolean hitWall(Wall wall){

        if(this.isLive&&this.getRectangle().intersects(wall.getRectangle())) {
            this.isLive=false;
            return true;
        }
        return false;
    }

}
