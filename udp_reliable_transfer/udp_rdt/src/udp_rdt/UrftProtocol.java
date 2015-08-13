
/**
 * @nameAndExt UrftProtocol.java
 * @date Mar 24, 2012
 * @author Kristin Ottofy
 */
package udp_rdt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 */
public class UrftProtocol
{
    public static void checkArgs(String[] args, int num) throws Exception
    {
        if (args.length != num)
        {
            Exception e = new Exception("Incorrect number of args");
            throw e;
        }
    }
    
    public static int parseStringToInt (String str)
    {
        int num = 0;
        try
        {
            Integer.parseInt(str);
        }
        catch (Exception e)
        {
            System.out.print(e.toString());
            System.exit(-2);
        }
        return num;
    }
    
  public static void sendData(DatagramSocket clientSocket, InetAddress IPAddress,
            DatagramPacket sendPacket, int timeout) throws IOException
    {
        boolean lostPacket = false;
        int packet = 1;
        do
        {
            //DatagramSocket clientSocket = new DatagramSocket();
            //InetAddress IPAddress = InetAddress.getLocalHost();//.getByAddress(ip);
            byte[] receiveData = new byte[512];


            // SEND DATA
            //DatagramPacket sendPacket =
            //      new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);


            // START TIMER
            clientSocket.setSoTimeout(timeout);

            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);

            try
            {
                clientSocket.receive(receivePacket);
                //String modifiedSentence = new String(receivePacket.getData());
                System.out.println("packet " + packet);
                lostPacket = false;

            }
            catch (Exception e)
            {
                System.out.println("Lost packet " + packet + ", " + e.toString());
                lostPacket = true;
            }

            clientSocket.close();
        }
        while (lostPacket == true);
        packet++;
    }
    
}
