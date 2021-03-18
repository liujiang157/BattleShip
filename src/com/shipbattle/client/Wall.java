package com.shipbattle.client;

import java.awt.*;

public class Wall {
    //墙的位置
    private int x;
    private int y;
    //墙的宽度和高度
    private int width;
    private int height;

    private ShipClient shipClient;

    public Wall(int x, int y, int width, int height, ShipClient shipClient) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shipClient = shipClient;
    }

    /**
     * 将墙画出来
     * @param g
     */
    public void draw(Graphics g){
        Color color=g.getColor();
        g.setColor(Color.WHITE);
        g.fillRect(x,y,width,height);
        g.setColor(color);
    }

    /**
     * 做碰撞检测使用
     * @return
     */
    public Rectangle getRectangle(){
        return new Rectangle(x,y,width,height);
    }
}
