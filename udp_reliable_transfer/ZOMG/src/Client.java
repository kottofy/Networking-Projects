
import java.io.*;
import java.net.*;
import java.util.*;

public class Client
{

    private DatagramSocket clientSocket;
    private String file_name;
    private byte[] buf = new byte[512];
    private int proxy_port;
    private String address;

    public Client(int proxy_port, String address, String file_name)
    {
        this.proxy_port = proxy_port;
        this.address = address;
        this.file_name = file_name;
    }

    public void start() throws InterruptedException
    {
        try
        {
            //Bind port
            clientSocket = new DatagramSocket();
            System.out.println("[CLIENT] Port binded");

            //Open file and put data into packets
            ArrayList<DatagramPacket> packets = fillPackets();

            //Send packets
            for (int i = 0, y = 1; i < packets.size(); i++)
            {
                boolean lost = false;

                do
                {

                    System.out.println("[CLIENT] Sending packet number " + i);
                    clientSocket.send(packets.get(i));



                    //Wait for ACK
                    System.out.println("[CLIENT] Waiting for return packet");
                    clientSocket.setSoTimeout(1000);
                    byte[] receiveData = new byte[512];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                    try
                    {
                        //packets.get(i).setData(receiveData);
                        clientSocket.receive(receivePacket);

                        System.out.println("[CLIENT] ACK recevied");
                        //String receivedData = new String(receivePacket.getData()); 
                        //InetAddress packetAddress = receivePacket.getAddress();
                        //int packetPort = receivePacket.getPort();

                        lost = false;
                        //break;
                    }
                    catch (SocketTimeoutException ste)
                    {
                        System.out.println("[CLIENT] Timeout Occurred: Packet assumed lost");
                        System.out.println("[CLIENT] Message re-attempt " + y);
                        //clientSocket.send(packets.get(i));
                        lost = true;
                        y++;
                    }
                }
                while (lost == true);
                System.out.println("[CLIENT] Next packet is going to be sent");
                y = 1;
            }
            clientSocket.close();
        }
        catch (IOException e)
        {
            System.out.println("Error in Client.start(): " + e);
        }
    }

    public ArrayList<DatagramPacket> fillPackets()
    {
        try
        {
            ArrayList<DatagramPacket> packets = new ArrayList<DatagramPacket>();
            FileInputStream fstream = new FileInputStream("moby_dick");
            DataInputStream in = new DataInputStream(fstream);
            InetAddress address = InetAddress.getLocalHost();
            System.out.println("client address: " + address);
            byte b;
            int counter = 0;
            while (in.read(buf) >= 0)
            {
                packets.add(new DatagramPacket(buf, buf.length, address, proxy_port));
                buf = new byte[512];
            }

            return packets;
        }
        catch (UnknownHostException e)
        {
            System.out.println("Client.fillPacket error: " + e);
            System.exit(-1);
            return null;
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Client.fillPacket error: " + e);
            System.exit(-1);
            return null;
        }
        catch (IOException e)
        {
            System.out.println("Client.fillPacket error: " + e);
            System.exit(-1);
            return null;
        }
    }
}
