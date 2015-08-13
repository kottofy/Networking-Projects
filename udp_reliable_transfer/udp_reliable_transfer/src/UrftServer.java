/**
 * Author: Kristin Ottofy
 * Author: Kendal Brown
 * Date: March 25, 2012
 * Project: udp_reliable_transfer
 * Class: CSCI 4760 - Computer Networks
 * 
 * UrftServer simulates a UDP Server with reliable data transfer using a 
 * stop-and-wait protocol. 
 * 
 * The server listens on a provided port until it receives a packet. Once 
 * UrftServer receives a packet, the header will be trimmed (a 4 byte array of
 * the length of bytes read from the file or 512 for the file name) and the 
 * remaining data will be added to an ArrayList. UrftServer will stop listening
 * when a packet is received with less than 508 bytes of read data (meaning
 * the client reached the end of the file) or after a maximum number of seconds
 * (15 seconds). UrftServer will send an ACK in the same form as the header 
 * containing either a 0 or 1. If the same packet was received twice, UrftServer
 * will return the original ack, and therefore handles packet loss. After
 * UrftServer stops listening for packets, it will write the ArrayList of data
 * to a file and exit.
 * 
 * This code was modeled from Computer Networking A Top-Down Approach,
 * Fifth Edition, by Kurose and Ross on page 172-173.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;

class UrftServer
{
    
    static String fileName = "";
    static ArrayList<byte[]> packets = new ArrayList();
    static int previousAck = 0;
    
    public static void main(String args[]) throws Exception
    {
        //args = new String[1];
        //args[0] = "1234";
        int ack = 0;

        // check for correct number of arguments (ip, port, file)
        UrftHelper.checkArgs(args, 1, "Incorrect number of args in UrftServer");

        // try to parse port number from argument string
        int port = UrftHelper.parseStringToInt(args[0], "Failed parsing " + args[0] + " in UrftServer");
        
        byte[] previousData = new byte[512];

        //UrftProtocol.createDirectory("server_dir");
        DatagramSocket serverSocket = new DatagramSocket(port);
        System.out.println("Server ready to receive on port " + port);
        
        while (true)
        {
            byte[] receiveData = new byte[512];
            byte[] sendData = new byte[512];
            
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            
            serverSocket.setSoTimeout(30000);    //server times out after 15 seconds of not receiving

            try
            {
                serverSocket.receive(receivePacket);
            }
            catch (Exception e)
            {
                System.out.println("SERVER: Inactive for too long (15 seconds). Goodbye.");
                serverSocket.close();
                
                break;
            }
            
            byte[] receivedData = receivePacket.getData();
            
            //System.out.println("RECEIVED: " + (new String(receivedData)));
            //System.out.println("SERVER: received a packet");
            InetAddress IPAddress = receivePacket.getAddress();
            port = receivePacket.getPort();
            
            
            //System.out.println("SERVER: sending ACK");
            sendData = UrftHelper.int2ByteArray(ack);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            serverSocket.send(sendPacket);
            sendData = new byte[512];
            //System.out.println("SERVER: sent ACK " + ack);
            
            
            if (!Arrays.equals(receivedData, previousData))
            {
                int length = UrftHelper.byteArray2Int(new byte[] {receivedData[508], receivedData[509], receivedData[510], receivedData[511]});
                //System.out.println("length: " + length);
                packets.add(Arrays.copyOfRange(receivedData, 0, length));
                previousAck = ack;
                ack = (ack + 1) % 2;
                
                //System.out.println("ack changed: " + ack);
                
                if (length < 508)
                    break;
            }
            else
            {
                ack = previousAck;
                //System.out.println("ack changed to previous: " + ack);
            }
            previousData = receivedData;
            
            
        }
        if (packets.size()> 0)
            writeToFile();
        serverSocket.close();
        return;
    }
    
    private static void writeToFile()
    {
        int length = 0;
        if (fileName.isEmpty())
        {
            createDirectory("server_dir");
            
            fileName = new String(packets.get(0));
            File outputFile = new File("server_dir/" + fileName);
        }
        try
        {
            System.out.println("WRITING to file " + fileName + "\n");
            
            //File outputFile = new File("server_dir/" + fileName);
            FileOutputStream os = new FileOutputStream("server_dir/"+fileName);
            for (int i = 1; i < packets.size(); i++)
            {
                os.write(packets.get(i));
                os.flush();
            }
            os.close();
        }
        catch (Exception ex)
        {
            System.out.println("Problem occured writing to file");
        }
    }
    
    private static void createDirectory(String directoryName)
    {
        File dir = new File(directoryName);
        dir.mkdir();
    }
}
