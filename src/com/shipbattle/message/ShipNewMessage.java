package com.shipbattle.message;


import com.shipbattle.client.Direction;
import com.shipbattle.client.Ship;
import com.shipbattle.client.ShipClient;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * 记录船诞生的消息的类
 */
public class ShipNewMessage implements Message {
    private Ship ship;
    private ShipClient shipClient;
    private static int messageType=Message.TANK_NEW_MESSAGE;

    public ShipNewMessage(Ship ship) {
        this.ship = ship;
    }

    public ShipNewMessage() {
    }

    public ShipNewMessage(ShipClient shipClient) {
        this.shipClient=shipClient;
        this.ship=this.shipClient.getMyShip();
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    /**
     * 发送船诞生消息
     * @param datagramSocket
     * @param IP
     * @param udpPort
     */
    @Override
    public void send(DatagramSocket datagramSocket,String IP,int udpPort) {
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        DataOutputStream dataOutputStream=new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeInt(messageType);
            dataOutputStream.writeInt(ship.getShipID());
            dataOutputStream.writeInt(ship.getX());
            dataOutputStream.writeInt(ship.getY());
            dataOutputStream.writeInt(ship.getDirection().ordinal());
            dataOutputStream.writeBoolean(ship.isEnemy());
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
     * 解析消息
     * @param dataInputStream
     */
    @Override
    public void parse(DataInputStream dataInputStream) {

        try {
            int id=dataInputStream.readInt();
            if(id==ship.getShipID()){//自己船的消息
                return;//直接返回，无需响应
            }

            int x=dataInputStream.readInt();
            int y=dataInputStream.readInt();
            int dir=dataInputStream.readInt();
            Direction direction=Direction.values()[dir];
            boolean isEnemy=dataInputStream.readBoolean();

            boolean exist=false;
            List<Ship> ships=shipClient.getEnemyShips();
            for (int i = 0; i <ships.size(); i++) {
                Ship ship=ships.get(i);
                if(ship.getShipID()==id){
                    exist=true;
                    break;
                }
            }

            if(!exist){//船不存在再加入

                //每当有一辆船加入，将己方船的信息也发出去
                ShipNewMessage shipNewMessage=new ShipNewMessage(shipClient.getMyShip());
                shipClient.getConnect().send(shipNewMessage);

                //其他的船全是敌人 Ship ship=new Ship(x,y,isEnemy,direction,shipClient);
                Ship ship=new Ship(x,y,isEnemy,direction,shipClient);
                ship.setShipID(id);
                shipClient.addEnemyShip(ship);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
