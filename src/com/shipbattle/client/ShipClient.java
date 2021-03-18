package com.shipbattle.client;


import com.shipbattle.message.ShipDeadMessage;
import com.shipbattle.server.ShipServer;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Ship Client
 */

public class ShipClient extends Frame {


    public static final int GAME_WIDTH = 800;//界面宽度
    public static final int GAME_HEIGHT = 600;//界面高度

    //创建我方船
    private Ship myShip;
    //用来存放多辆船
    private List<Ship> enemyShips = new ArrayList<Ship>();
    //创建容器，用来存放爆炸的
    private List<Explode> explodes = new ArrayList<>();
    //创建容器，用来存放多发炮弹。
    private List<Missile> missiles = new ArrayList<>();

    //创建墙
    private Wall wall1 = new Wall(100, 150, 20, 200, this);
    private Wall wall2 = new Wall(300, 150, 200, 20, this);

    //虚拟屏幕
    private Image offScreenImage = null;
    //用来连接服务端
    private ConnectClient connect = new ConnectClient(this);
    //用来输入连接信息的对话框
    private ConnectDialog connectDialog = new ConnectDialog();

    private boolean isAlone = false;

    public ShipClient() {
        Random random = new Random();
        int x = random.nextInt(700) + 50;
        int y = random.nextInt(500) + 50;
        myShip = new Ship(x, y, false, Direction.Stop, this);
    }


    public Ship getMyShip() {
        return myShip;
    }

    public List<Ship> getEnemyShips() {
        return enemyShips;
    }

    /**
     * 更新方法
     *
     * @param g
     */
    @Override
    public void update(Graphics g) {
        if (offScreenImage == null) {
            offScreenImage = this.createImage(GAME_WIDTH, GAME_HEIGHT);
        }
        Graphics gOffScreen = offScreenImage.getGraphics();
        Color c = gOffScreen.getColor();
        gOffScreen.setColor(Color.BLACK);
        gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
        gOffScreen.setColor(c);
        paint(gOffScreen);
        g.drawImage(offScreenImage, 0, 0, null);
    }

    /**
     * 画图方法,画出船和炮弹
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        Color color = g.getColor();
        g.setColor(Color.GREEN);
        g.drawString("missile count:" + missiles.size(), 650, 50);
        g.drawString("enemyShip count:" + enemyShips.size(), 650, 70);
        g.drawString("myShip life point:" + myShip.getLife(), 650, 90);
        g.drawString("myShip id:" + myShip.getShipID(), 650, 110);
        g.setColor(color);
        //画出炮弹
        for (int i = 0; i < missiles.size(); i++) {
            Missile m = missiles.get(i);

            if (m.hitShip(myShip) && !myShip.isLive()) {//己方船被击中且死亡了
                ShipDeadMessage shipDeadMessage = new ShipDeadMessage(myShip.getShipID());
                connect.send(shipDeadMessage);
            }
            m.hitShips(enemyShips);

            m.hitWall(wall1);
            m.hitWall(wall2);
            m.draw(g);
        }
        //画出爆炸
        for (int i = 0; i < explodes.size(); i++) {
            Explode explode = explodes.get(i);
            explode.draw(g);
        }

        //画出船
        myShip.draw(g);

        for (int i = 0; i < enemyShips.size(); i++) {
            Ship ship = enemyShips.get(i);
            ship.draw(g);
            ship.collidesWithWall(wall1);
            ship.collidesWithWall(wall2);
            ship.collidesWithShips(enemyShips);
        }

        //画出墙
        wall1.draw(g);
        wall2.draw(g);
    }


    /**
     * 启动程序显示图形和监听功能
     */
    public void launchFrame() {
        //输入连接需要的信息
        connectDialog.setVisible(true);
        if(isAlone){
            setVisible(false);
        }else {
            this.setLocation(300, 50);
            this.setSize(GAME_WIDTH, GAME_HEIGHT);
            this.setTitle("Ship Battle");

            this.setBackground(Color.BLACK);//设置背景色
            this.setResizable(false);//禁止改变窗口尺寸
            this.setVisible(true);

            //键盘的监听器
            this.addKeyListener(new KeyMonitor());
            //增加响应事件
            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });


            //创建线程用来移动船位置
            new Thread(new PaintThread()).start();
        }
    }


    /**
     * 向容器中添加一枚炮弹
     *
     * @param missile
     */
    public void addMissile(Missile missile) {
        missiles.add(missile);
    }

    /**
     * 向容器中添加爆炸
     *
     * @param explode
     */
    public void addExplode(Explode explode) {
        explodes.add(explode);
    }

    /**
     * 向容器中添加一辆船
     *
     * @param ship
     */
    public void addEnemyShip(Ship ship) {
        ship.setEnemy(true);
        enemyShips.add(ship);
    }

    /**
     * 从容器中一辆船
     *
     * @param ship
     */
    public void removeEnemyShip(Ship ship) {
        enemyShips.remove(ship);
    }

    /**
     * 从容器中移除一次爆炸
     *
     * @param explode
     */
    public void removeExplode(Explode explode) {
        explodes.remove(explode);
    }

    /**
     * 从容器中移除一枚炮弹
     *
     * @param missile
     */
    public void removeMissile(Missile missile) {
        missiles.remove(missile);
    }


    public ConnectClient getConnect() {
        return connect;
    }

    //建一个线程，用来每隔一段时间重画界面
    private class PaintThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                //repaint会先调用update方法，然后调用paint方法
                repaint();//调用外部包装类的重画方法
                try {
                    //休息一定时间后重画一次界面
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //键盘的监听类
    private class KeyMonitor extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            myShip.keyPress(e);//按下键盘方向键，船做出反应
        }

        @Override
        public void keyReleased(KeyEvent e) {
            //放开键盘,作出响应
            myShip.keyRelease(e);
        }
    }


    //用来输入连接信息的对话框
    class ConnectDialog extends Dialog {

        TextField IPTextField = new TextField("127.0.0.1", 12);
        TextField TCPTextField = new TextField("" + ShipServer.TCP_PORT, 4);
        TextField UDPTextField = new TextField("" + new Random().nextInt(10000), 4);
        Button button = new Button("connect");
        Button alone = new Button("alone");

        public ConnectDialog() {
            super(ShipClient.this, true);
            this.setLayout(new FlowLayout());
            this.add(new Label("IP:"));
            this.add(IPTextField);
            this.add(new Label("Port:"));
            this.add(TCPTextField);
            this.add(new Label("UDP port:"));
            this.add(UDPTextField);
            this.add(button);
            this.add(alone);
            this.setLocation(300, 300);
            pack();

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            });

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String IP = IPTextField.getText().trim();
                    int tcpPort = Integer.parseInt(TCPTextField.getText().trim());
                    int udpPort = Integer.parseInt(UDPTextField.getText().trim());
                    //连接服务端
                    connect.setUdpPort(udpPort);
                    connect.init();
                    connect.connect(IP, tcpPort);
                    setVisible(false);
                }
            });

            alone.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        setVisible(false);
                        isAlone = true;
                        com.shipbattle.singleplay.ShipClient shipClient = new com.shipbattle.singleplay.ShipClient();
                        shipClient.launchFrame();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });
        }
    }


    public static void main(String[] args) {
        ShipClient shipClient = new ShipClient();
        shipClient.launchFrame();
    }


}
