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
 * 发送船移动的消息
 */
public class ShipMoveMessage implements Message {

    private static int messageType=Message.TANK_MOVE_MESSAGE;
    private ShipClient shipClient;
    private int shipID;
    //船位置
    private int x;
    private int y;
    private Direction direction;

    public ShipMoveMessage() {
    }

    public ShipMoveMessage(ShipClient shipClient) {
        this.shipClient = shipClient;
    }

    public ShipMoveMessage(int shipID, int x, int y, Direction direction) {
        this.shipID = shipID;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    /**
     * 发送船移动消息
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
            dataOutputStream.writeInt(shipID);
            dataOutputStream.writeInt(x);
            dataOutputStream.writeInt(y);
            dataOutputStream.writeInt(direction.ordinal());

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
            if(id==this.shipID){//自己船的消息
                return;//直接返回，无需响应
            }

            int x=dataInputStream.readInt();
            int y=dataInputStream.readInt();
            int dir=dataInputStream.readInt();
            Direction direction=Direction.values()[dir];

            boolean exist=false;
            List<Ship> ships=shipClient.getEnemyShips();
            for (int i = 0; i <ships.size(); i++) {
                Ship ship=ships.get(i);
                if(ship.getShipID()==id){
                    exist=true;
                    ship.setX(x);
                    ship.setY(y);
                    ship.setDirection(direction);
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
