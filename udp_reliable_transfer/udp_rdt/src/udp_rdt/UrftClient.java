
/**
 * @nameAndExt UrftClient.java
 * @date Mar 24, 2012
 * @author Kristin Ottofy
 */
package udp_rdt;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 */
public class UrftClient
{
     public static void main(String args[]) throws Exception
    {
        args = new String[3];
        args[0] = "localhost";
        args[1] = "9876";
        args[2] = "moby-dick";
        int port;
        InetAddress IPAddress;
        boolean lostPacket = false;
        // byte[] ip = args[0].getBytes();
        InputStream is = new FileInputStream(args[2]);
        byte[] sendData = new byte[512];
        int length = 0;
        int packet = 1;
        byte[] fileName = args[2].getBytes();
        
        // check for correct number of arguments (ip, port, file)
        try 
        {
            UrftProtocol.checkArgs(args, 3);
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
            System.exit(-1);
        }

        // try to parse port number from argument string
        port = UrftProtocol.parseStringToInt(args[1]);
        
        

        if (args[0].equalsIgnoreCase("localhost"))
        {

            IPAddress = InetAddress.getByName("localhost");
        }
        else
        {
            IPAddress = InetAddress.getByAddress(args[0].getBytes());
        }
   
        while ((length = is.read(sendData)) >= 0)
        {
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] receiveData = new byte[512];


            // SEND DATA
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, port);

            do
            {
                System.out.println("CLIENT: sending packet " + packet);
                clientSocket.send(sendPacket);


                // START TIMER
                clientSocket.setSoTimeout(1000);

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                try
                {
                    clientSocket.receive(receivePacket);
                    //String modifiedSentence = new String(receivePacket.getData());
                    System.out.println("packet " + packet);
                    lostPacket = false;

                }
                catch (Exception e)
                {
                    System.out.println("CLIENT: lost packet " + packet + ", " + e.getMessage());
                    lostPacket = true;
                }
            }
            while (lostPacket == true);
            clientSocket.close();

            packet++;
        }

        is.close();

        //  os.close();

    }
      
}
