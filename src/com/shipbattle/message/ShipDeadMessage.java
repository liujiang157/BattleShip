package com.shipbattle.message;



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
 * 发送船死亡的消息
 */
public class ShipDeadMessage implements Message {

    private int messageType= Message.TANK_DEAD_MESSAGE;
    private ShipClient shipClient;
    private int shipID;//船id

    public ShipDeadMessage() {
    }

    public ShipDeadMessage(int shipID) {
        this.shipID = shipID;
    }

    public ShipDeadMessage(ShipClient shipClient) {
        this.shipClient = shipClient;
    }

    /**
     * 发送船死亡消息
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
     * 解析船死亡消息
     * @param dataInputStream
     */
    @Override
    public void parse(DataInputStream dataInputStream) {
        try {

            int shipID=dataInputStream.readInt();
            if(shipID==shipClient.getMyShip().getShipID())
                return;

            boolean exist=false;
            List<Ship> ships=shipClient.getEnemyShips();
            for (int i = 0; i <ships.size(); i++) {
                Ship ship=ships.get(i);
                if(ship.getShipID()==shipID){
                    ship.setLive(false);
                    exist=true;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
