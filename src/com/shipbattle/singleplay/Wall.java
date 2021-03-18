package com.shipbattle.singleplay;

import java.awt.*;

public class Wall {

    //Wall position
    private int x;
    private int y;
    //The width and height of the wall
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
     * Draw the wall
     * @param g
     */
    public void draw(Graphics g){
        Color color=g.getColor();
        g.setColor(Color.WHITE);
        g.fillRect(x,y,width,height);
        g.setColor(color);
    }

    /**
     * Use for collision detection
     * @return
     */
    public Rectangle getRectangle(){
        return new Rectangle(x,y,width,height);
    }
}
