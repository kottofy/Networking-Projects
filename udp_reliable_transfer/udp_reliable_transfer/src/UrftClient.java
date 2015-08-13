/**
 * Author: Kristin Ottofy
 * Author: Kendal Brown
 * Date: March 25, 2012
 * Project: udp_reliable_transfer
 * Class: CSCI 4760 - Computer Networks
 * 
 * UrftClient simulates a UDP Client with reliable data transfer using a 
 * stop-and-wait protocol. 
 * 
 * It works by sending the file name to the server first, then the data in the
 * file. The "header" is a 4 byte array that contains an integer of the number
 * of bytes read from the file. The file name header is 512 regardless. The max
 * packet size is 512 bytes, so including a header, the most data from the file
 * that can be sent at one time is 508 bytes. After UrftClient sends a packet
 * to the server, it waits to receive an ACK. The ACKS are numbered using with
 * 0 and 1 but are in the same form as the header. A timer (100 ms) is used to
 * determine whether a packet is lost. If an ACK takes too long to be sent, 
 * that packet will be considered lost. If a packet is lost, UrftClient will 
 * resend the packet up to a maximum number of times until the program stops or
 * the correct ACK is received.
 * 
 * This code was modeled from Computer Networking A Top-Down Approach,
 * Fifth Edition, by Kurose and Ross on page 172-173.
 */


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

class UrftClient 
{
    static int packet = 0;
    static int expectedAck = 0;
    static final int timeout = 1000;
    static int packetLoss = 0;
    
    public static void main(String args[]) throws Exception
    {
        //args = new String[3];
        //args[0] = "127.0.0.1";
        //args[1] = "1234";
        //args[2] = "moby-dick.pdf";
        
        // check for correct number of arguments (ip, port, file)
        UrftHelper.checkArgs(args, 3, "Incorrect number of args in UrftClient");

        // parse port number from argument string
        int port = UrftHelper.parseStringToInt(args[1], "Failed parsing " + args[1] + " in UrftClient");
        
        InetAddress IPAddress = InetAddress.getByName(args[0]);

        //InputStream is = new FileInputStream("client_dir/" + args[2]);
        InputStream is = new FileInputStream(args[2]);
        int length = 0;
        byte[] lengthByteArray = UrftHelper.int2ByteArray(512);
        byte[] sendData = Arrays.copyOf(args[2].getBytes(), 512);   
        sendData[508] = lengthByteArray[0];
        sendData[509] = lengthByteArray[1];
        sendData[510] = lengthByteArray[2];
        sendData[511] = lengthByteArray[3];

        //send file name to server
        System.out.println("Client sending file name packet");
        //DatagramPacket sendPacket = new DatagramPacket(fileName, fileName.length, IPAddress, port); 
        sendDataToServer(sendData, IPAddress, port, timeout);

        sendData = new byte[508];

        //send file data to server
        System.out.println("Client sending file data");
        
        while ((length = is.read(sendData)) >= 0)
        {
            lengthByteArray = UrftHelper.int2ByteArray(length);
            sendData = Arrays.copyOf(sendData, 512);
            sendData[508] = lengthByteArray[0];
            sendData[509] = lengthByteArray[1];
            sendData[510] = lengthByteArray[2];
            sendData[511] = lengthByteArray[3];
            //System.out.println("SENDING: " + new String(sendData));

            sendDataToServer(sendData, IPAddress, port, timeout);
            sendData = new byte[508];
        }
        
        is.close();
        System.out.println("Lost " + packetLoss + " packets");
    }
    
     public static void sendDataToServer(byte[] sendData, InetAddress IPAddress, int port, int timeout)
            throws IOException
    {
        boolean lostPacket = false;
        int retry = 0;

        byte[] receiveData = new byte[sendData.length];

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);

        // SEND DATA
        do
        {
            DatagramSocket clientSocket = new DatagramSocket();

            //System.out.println("CLIENT: sending packet " + packet);
            clientSocket.send(sendPacket);


            // START TIMER
            clientSocket.setSoTimeout(timeout);

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            try
            {
                clientSocket.receive(receivePacket);
                //String modifiedSentence = new String(receivePacket.getData());
                //System.out.println("CLIENT: Expected ACK " + expectedAck);
                        
                
                int receivedAck = UrftHelper.byteArray2Int(receivePacket.getData());
                
                //System.out.println("CLIENT: Received ACK " + receivedAck);
                        
                if (receivedAck == expectedAck)
                {
                    //System.out.println("CLIENT: Received ACK for packet " + packet);
                    lostPacket = false;
                    retry = 0;
                    expectedAck = (expectedAck + 1) % 2;
                }

            }
            catch (Exception e)
            {
                //System.out.println("CLIENT: lost packet " + packet + ", " + e.getMessage());
                packetLoss++;
                lostPacket = true;
                //retry++;
                if (retry == 50)
                {
                    System.out.println("Attempts exceeded. Goodbye.");
                    System.exit(3);
                }
            }
            clientSocket.close();
        }
        while (lostPacket == true);

        packet++;
    }
                
}