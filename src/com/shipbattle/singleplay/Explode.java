package com.shipbattle.singleplay;

import java.awt.*;

/**
 * 显示爆炸的类
 */
public class Explode {
    private int x;
    private int y;
    private boolean isLive=true;//判断爆炸的存活状态
    private int step=0;//爆炸的步骤
    private static boolean isInit =false;//判断是否初始化了图片

    private static Toolkit toolkit=Toolkit.getDefaultToolkit();//工具包
    //爆炸的图片
    private static Image[] images={
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/0.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/1.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/2.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/3.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/4.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/5.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/6.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/7.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/8.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/9.gif")),
            toolkit.getImage(Explode.class.getClassLoader().getResource("images/10.gif"))
    };

    private ShipClient shipClient;//ShipClient的引用

    public Explode(int x, int y, ShipClient shipClient) {
        this.x = x;
        this.y = y;
        this.shipClient = shipClient;
    }

    /**
     * 画出爆炸
     * @param g
     */
    public void draw(Graphics g){

        if(!isInit){//先将图片加载到内存中
            for (int i = 0; i <images.length; i++) {
                g.drawImage(images[i],-100,-100,null);
            }
            isInit=true;
        }

        if(!isLive) {
            //将爆炸从存储爆炸的容器中移除
            shipClient.removeExplode(this);
            return;
        }
        if(step==images.length) {
            isLive=false;
            step=0;
            return;
        }

        g.drawImage(images[step],x,y,null);

        step++;

    }

}
