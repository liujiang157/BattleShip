package com.shipbattle.singleplay;

import java.awt.*;

/**
 * Blood back class
 */
public class Blood {
    //position of blood
    private int x;
    private int y;
    //blood width and height
    private int width;
    private int height;
    //记录血块的状态
    private boolean isLive=true;

    private int step=0;//记录步骤
    //让血块沿着固定路线移动
    private int[][] route={{350,300},{360,310},{330,340},{360,310},{400,390},{420,410},{440,420},{450,440}};

    ShipClient shipClient;

    public Blood(){
        x=route[0][0];
        y=route[0][1];
        width=15;
        height=15;
    }


    /**
     * 绘制出血块
     * @param g
     */
    public void draw(Graphics g){

        if(!isLive)//血块已经消亡了
            return;

        Color color=g.getColor();
        g.setColor(Color.GREEN);
        g.fillRect(x,y-10,width,10);
        g.setColor(color);
        move();
    }

    /**
     * 血块移动
     */
    private void move() {
        step++;
        if(step==route.length){
            step=0;
        }
        x=route[step][0];
        y=route[step][1];
    }


    public Rectangle getRectangle(){
        return new Rectangle(x,y,width,height);
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public boolean isLive() {
        return isLive;
    }
}
