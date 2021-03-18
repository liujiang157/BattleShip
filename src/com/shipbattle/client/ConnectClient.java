package com.shipbattle.client;



import com.shipbattle.message.*;
import com.shipbattle.server.ShipServer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;


/**
 * 用来连接服务端的类
 */
public class ConnectClient {

    private int udpPort;//UDP port
    private String IP;
    private ShipClient shipClient;
    private static Random random=new Random();

    private DatagramSocket datagramSocket=null;

    public ConnectClient(ShipClient shipClient) {
        this.shipClient=shipClient;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public void init(){
        try {
            datagramSocket=new DatagramSocket(udpPort);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接服务端的方法
     * @param IP
     * @param port
     */
    public void connect(String IP,int port){
        this.IP=IP;
        Socket socket=null;
        try {
            socket=new Socket(IP,port);
            DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeInt(udpPort);
            DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
            int shipID=dataInputStream.readInt();
            System.out.println("Connect to server....+shipID:"+shipID+"  udp port:"+udpPort);
            shipClient.getMyShip().setShipID(shipID);//将id保存
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ShipNewMessage message=new ShipNewMessage(shipClient.getMyShip());
        send(message);//发送消息给服务端
        //用来接收服务端消息的线程
        new Thread(new UDPReceiveThread()).start();
    }


    public void send(Message message){
        message.send(datagramSocket,IP, ShipServer.UDP_PORT);
    }

    /**
     * 接收udp消息的线程
     */
    private class UDPReceiveThread implements Runnable{

        byte[] buffer=new byte[1024];
        @Override
        public void run() {

            while (datagramSocket!=null){
                DatagramPacket datagramPacket=new DatagramPacket(buffer,buffer.length);
                try {
                    datagramSocket.receive(datagramPacket);
                    parse(datagramPacket);
                    System.out.println("a packet receive from server....");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void parse(DatagramPacket datagramPacket) {
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(buffer,0,datagramPacket.getLength());
            DataInputStream dataInputStream=new DataInputStream(byteArrayInputStream);
            int messageType=0;
            try {
                messageType=dataInputStream.readInt();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Message message=null;
            switch (messageType){
                case Message.TANK_NEW_MESSAGE:
                    message=new ShipNewMessage(shipClient);
                    break;
                case Message.TANK_MOVE_MESSAGE:
                    message=new ShipMoveMessage(shipClient);
                    break;
                case Message.Missile_NEW_MESSAGE:
                    message=new MissileNewMessage(shipClient);
                    break;
                case Message.TANK_DEAD_MESSAGE:
                    message=new ShipDeadMessage(shipClient);
                    break;
            }
            message.parse(dataInputStream);
        }
    }
}
