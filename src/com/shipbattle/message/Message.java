package com.shipbattle.message;

import java.io.DataInputStream;
import java.net.DatagramSocket;

/**
 * 消息的接口
 */
public interface Message {
    public static final int TANK_NEW_MESSAGE=1;//船诞生消息
    public static final int TANK_MOVE_MESSAGE=2;//船移动消息
    public static final int TANK_DEAD_MESSAGE=3;//子弹死亡消息
    public static final int Missile_NEW_MESSAGE=4;//子弹诞生消息
    //public static final int Missile_MOVE_MESSAGE=5;//子弹移动消息

    /**
     * 发送消息
     * @param datagramSocket
     * @param IP
     * @param udpPort
     */
    void send(DatagramSocket datagramSocket,String IP,int udpPort);

    /**
     * 解析消息
     * @param dataInputStream
     */
    void parse(DataInputStream dataInputStream);
}
