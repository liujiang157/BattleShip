package com.shipbattle.message;



import com.shipbattle.client.Direction;
import com.shipbattle.client.Missile;
import com.shipbattle.client.ShipClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 处理子弹消息的类
 */
public class MissileNewMessage implements Message {


    private int messageType= Message.Missile_NEW_MESSAGE;
    private ShipClient shipClient;
    private Missile missile;

    public MissileNewMessage() {
    }

    public MissileNewMessage(Missile missile) {
        this.missile = missile;
    }

    public MissileNewMessage(ShipClient shipClient) {
        this.shipClient = shipClient;
    }

    /**
     * 发送子弹诞生消息
     * @param datagramSocket
     * @param IP
     * @param udpPort
     */
    @Override
    public void send(DatagramSocket datagramSocket, String IP, int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(messageType);
            dataOutputStream.writeInt(missile.getShipID());
            dataOutputStream.writeInt(missile.getX());
            dataOutputStream.writeInt(missile.getY());
            dataOutputStream.writeInt(missile.getDirection().ordinal());
            dataOutputStream.writeBoolean(missile.isEnemy());
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buffer=byteArrayOutputStream.toByteArray();
        DatagramPacket datagramPacket=new DatagramPacket(buffer,buffer.length,new InetSocketAddress(IP,udpPort));
        try {
            datagramSocket.send(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析子弹诞生消息
     * @param dataInputStream
     */
    @Override
    public void parse(DataInputStream dataInputStream) {
        try {
            int shipID=dataInputStream.readInt();

            if(shipID==shipClient.getMyShip().getShipID())
                return;

            int x=dataInputStream.readInt();
            int y=dataInputStream.readInt();
            int dir=dataInputStream.readInt();
            Direction direction=Direction.values()[dir];
            boolean isEnemy=dataInputStream.readBoolean();
            Missile missile=new Missile(shipID,x,y,true,direction,shipClient);
            shipClient.addMissile(missile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
