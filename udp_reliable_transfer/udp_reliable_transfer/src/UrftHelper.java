/**
 * Author: Kristin Ottofy
 * Author: Kendal Brown
 * Date: March 25, 2012
 * Project: udp_reliable_transfer
 * Class: CSCI 4760 - Computer Networks
 * 
 * UrftHelper contains methods used by both UrftClient and UrftServer.
 * 
 * This code was modeled from Computer Networking A Top-Down Approach,
 * Fifth Edition, by Kurose and Ross on page 172-173.
 */




public class UrftHelper
{

    public static void checkArgs(String[] args, int num, String message)
    {
        if (args.length != num)
        {
            System.out.println(message);
            System.exit(-1);
        }
    }

    public static int parseStringToInt(String str, String message)
    {
        int num = 0;
        try
        {
            num = Integer.parseInt(str);
        }
        catch (Exception ex)
        {
            System.out.print(message);
            System.exit(-2);
        }
        return num;
    }


    public static byte[] int2ByteArray(int value)
    {
        return new byte[]
        {
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value
        };
    } 

    
    public static int byteArray2Int(byte[] b)
    {
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (3 - i) * 8;
            value += (int) (b[i] & 0xFF) << shift;
        }

        return value;
    }
}
