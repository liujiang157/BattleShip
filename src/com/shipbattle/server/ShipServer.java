package com.shipbattle.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * server
 */
public class ShipServer {

    public static final int TCP_PORT=8888;//tcp port
    public static final int UDP_PORT=9999;//udp port
    private List<Client> clients=new ArrayList<>();//save client info

    private static int shipID =100;//ships's id


    public void init(){
        //send and receive udp data
        new Thread(new UDPThread()).start();

        ServerSocket serverSocket= null;
        try {
            serverSocket = new ServerSocket(TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true){
            Socket socket=null;
            try {
                socket=serverSocket.accept();
                System.out.println("A client connect\taddress:"+socket.getInetAddress()+"\tport:"+socket.getPort());
                //receive client data
                DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());
                String IP=socket.getInetAddress().getHostName();
                int udpPort=dataInputStream.readInt();
                System.out.println("udpPort:"+udpPort);
                Client client=new Client(IP,udpPort);
                clients.add(client);//save
                //send to client
                DataOutputStream dataOutputStream=new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeInt(shipID++);
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
        }
    }

    public static void main(String[] args) {
        new ShipServer().init();
    }

    /**
     * Client Info
     */
    private class Client{
        private String IP;
        private int udpPort;//UDP port

        public Client(String IP, int udpPort) {
            this.IP = IP;
            this.udpPort = udpPort;
        }

        public String getIP() {
            return IP;
        }

        public void setIP(String IP) {
            this.IP = IP;
        }

        public int getUdpPort() {
            return udpPort;
        }

        public void setUdpPort(int udpPort) {
            this.udpPort = udpPort;
        }
    }

    /**
     * udp package
     */
    private class UDPThread implements Runnable{
        byte[] buffer=new byte[1024];
        @Override
        public void run() {
            System.out.println("UDP thread start port "+UDP_PORT);
            DatagramSocket datagramSocket=null;
            try {
                datagramSocket=new DatagramSocket(UDP_PORT);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            while (datagramSocket!=null){
                DatagramPacket datagramPacket=new DatagramPacket(buffer,buffer.length);
                try {
                    datagramSocket.receive(datagramPacket);
                    //将数据转发给其他所有客户端
                    for (int i = 0; i <clients.size(); i++) {
                        Client client=clients.get(i);
                        datagramPacket.setSocketAddress(new InetSocketAddress(client.getIP(),client.getUdpPort()));
                        datagramSocket.send(datagramPacket);
                    }
                    System.out.println("a packet receive from client....");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
