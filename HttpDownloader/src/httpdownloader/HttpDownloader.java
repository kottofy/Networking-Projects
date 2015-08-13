/*
 * Author: Kristin Ottofy
 * Class: CSCI 4760 - Networks
 * Project: HttpDownloader
 * Date: February 16, 2012
 * 
 * Directions:
 * HttpDownloader will spawn 5 threads, each thread will open one TCP connection 
 * to www.cs.uga.edu on port 80, and retrieve a part of the .gif file. Each of 
 * the 5 parts must be of an approximately equal length. Finally, the program 
 * will put the parts together and write the output into a file called 
 * "cs_template_r2_c2.gif". You should name the files containing the parts of 
 * downloaded content as "part_i", where i is an index. In the example above, 
 * the program will output the parts into 5 different files called "part_1", 
 * "part_2", ..., "part_5", along with the reconstructed "cs_template_r2_c2.gif" 
 * file. DO NOT delete the "part_i" fields after you are done recomposing the 
 * original file.
 */

package httpdownloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *  TODO: remove static variables
 *  TODO: organize into methods
 *  TODO: remove inner class Runnable
 * @author kottofy
 */
public class HttpDownloader
{

    static int averageSize = 0; //size of all parts except the last one
    static int lastSize = 0;    //size of the last part (averageSize + mod)
    static int parts = 0;       //number of parts from args[1]

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // TEST NUMBER OF ARGS
        if (args.length != 2)
        {
            System.out.println("Incorrect number of arguements");
            System.exit(-1);
        }


        // TEST ARGS[1] FOR PART NUMEBR
        try
        {
            parts = Integer.parseInt(args[1]);
        }
        catch (Exception e)
        {
            System.out.println("Parsing Parts Error");
            System.exit(-2);
        }

        // PARSE URL
        int www = args[0].indexOf("www");
        String subString = args[0];

        if (www != -1)
        {
            subString = args[0].substring(www);
        }
        else
        {
            www = 0;
        }
        //System.out.println("subString: " + subString);

        int charAt = subString.indexOf("/");

        final String link = subString.substring(charAt);
        //System.out.println("link: " + link);

        final String host = subString.substring(0, charAt);
        //System.out.println("host: " + host);

        String fileName = link;
        int fileNameIndex = link.indexOf("/");

        while (fileNameIndex != -1)
        {
            fileName = fileName.substring(fileNameIndex + 1);
            fileNameIndex = fileName.indexOf("/");
        }
        //System.out.println("fileName: " + fileName);


        // GET HEAD
        try
        {
            String sentence = "";
            int length = 0;

            // HEAD 
            Socket socket1 = new Socket(host, 80);

            BufferedReader in1 = new BufferedReader(
                    new InputStreamReader(socket1.getInputStream()));

            BufferedWriter out1 = new BufferedWriter(
                    new OutputStreamWriter(socket1.getOutputStream()));

            String headRequest = "HEAD " + link + " HTTP/1.1\r\n"
                    + "Host: " + host + "\r\n"
                    + "Connection: close\r\n"
                    + "\r\n";
            System.out.println("\n" + headRequest);

            out1.write(headRequest);
            out1.flush();

            // GET CONTENT LENGTH
            while ((sentence = in1.readLine()) != null)
            {
                System.out.println(sentence);

                if (sentence.startsWith("Content-Length"))
                {
                    try
                    {
                        length = Integer.parseInt(sentence.substring(16));
                    }
                    catch (Exception e)
                    {
                        System.out.println("Parsing Length Error");
                        System.exit(-2);
                    }
                }
            }

            //System.out.println("\nLength: " + length);

            socket1.close();
            in1.close();
            out1.close();


            // DETERMINE SIZES OF PARTS
            if (length % parts == 0)
            {
                averageSize = length / parts;
                lastSize = averageSize;
            }
            else
            {
                averageSize = length / parts;
                lastSize = length / parts + (length % parts);
            }

            //System.out.println("averageSize: " + averageSize);
            //System.out.println("lastSize: " + lastSize);
        }
        catch (Exception e)
        {
            System.out.println("Error occured getting head");
        }

        Runnable r1 = new Runnable()
        {

            public void run()
            {
                try
                {
                    String name = Thread.currentThread().getName(); //name is part number

                    //determining start and end range bytes
                    int part = 1;

                    try
                    {
                        part = Integer.parseInt(name);
                    }
                    catch (Exception e)
                    {
                        System.out.println("Parsing thread name to part number error for thread " + name);
                        System.exit(-4);
                    }

                    System.out.println("part_" + part + " is starting\n");

                    int start = 0;

                    if (part != 1)
                    {
                        start = (part - 1) * averageSize;
                    }
                    int end = 0;

                    if (part == parts)
                    {
                        end = ((part - 1) * averageSize) + lastSize - 1;
                    }
                    else
                    {
                        end = (part) * averageSize - 1;
                    }

                    // starting GET Range request
                    Socket socket2 = new Socket(host, 80);

                    String getRequest = "GET " + link + " HTTP/1.1\r\n"
                            + "Host: " + host + "\r\n"
                            + "Range: bytes=" + start + "-" + end + "\r\n"
                            + "Connection: close\r\n"
                            + "\r\n";

                    System.out.println("\n" + getRequest);

                    InputStream input = socket2.getInputStream();
                    OutputStream output = socket2.getOutputStream();

                    output.write(getRequest.getBytes());
                    output.flush();

                    //used to copy the response to a file
                    File file = new File("part_" + part);
                    OutputStream toFile = new FileOutputStream(file);
                    byte[] ba = new byte[1024];
                    int l = 0;
                    boolean startCopy = false;
                    int index = 0;
                    int count = 0;

                    //used to check "CR NL CR NL" does not occur across byte arrays
                    byte b1 = 0;
                    byte b2 = 0;
                    byte b3 = 0;

                    //PROBLEM OCCURS IF "CR NL CR NL" OCCURS OVER MULTIPLE
                    //BYTE ARRAYS
                    while ((l = input.read(ba)) > 0)
                    {
                        /*
                        for (int k = 0; k < ba.length; k++)
                        {
                        System.out.print((char)ba[k]);
                        }
                         */

                        //if ba has been read once, check the previous byte array's 
                        //last bytes for "CR NL CR NL", save that index to start copying
                        if (count > 0)
                        {
                            if (b1 == '\r' && b2 == '\n' && b3 == '\r' && ba[0] == '\n')
                            {
                                index = 1;
                                startCopy = true;
                            }
                            else if (b2 == '\r' && b3 == '\n' && ba[0] == '\r' && ba[1] == '\n')
                            {
                                index = 2;
                                startCopy = true;
                            }
                            else if (b3 == '\r' && ba[0] == '\n' && ba[1] == '\r' && ba[2] == '\n')
                            {
                                index = 3;
                                startCopy = true;
                            }
                        }

                        //if "CR NL CR NL" hasn't been found yet, check the current
                        //byte array 
                        if (!startCopy)
                        {
                            for (int i = 0; i < ba.length; i++)
                            {
                                if (i + 4 < ba.length && ba[i] == '\r' && ba[i + 1] == '\n' && ba[i + 2] == '\r' && ba[i + 3] == '\n')
                                {
                                    index = i + 4;
                                    startCopy = true;
                                    break;
                                }
                            }


                            // if "CR NL CR NL" was not found in the byte array
                            // save last 3 bytes in case to check overlap
                            if (!startCopy)
                            {
                                b1 = ba[ba.length - 3];
                                b2 = ba[ba.length - 2];
                                b3 = ba[ba.length - 1];
                            }
                        }

                        //if "CR NL CR NL" was found, start copying to file after
                        if (startCopy)
                        {

                            toFile.write(ba, index, l - index);
                            toFile.flush();
                        }

                        count++;
                        index = 0;
                    }

                    toFile.close();
                    output.close();
                    input.close();
                    socket2.close();
                    System.out.println("part_" + part + " was completed\n");
                    //finished[part - 1] = true;
                }
                catch (Exception e)
                {
                    System.out.println("Runnable Error");
                    System.exit(-6);
                }
                // return;
            }
        };

        Thread[] threads = new Thread[parts];   //holds threads to be named by their part number

        //initiliaze and start threads
        for (int i = 1, j = 0; i <= parts; i++, j++)
        {
            Thread thread = new Thread(r1, Integer.toString(i));
            threads[j] = thread;
            thread.start();
        }

        //check that threads have finished
        System.out.println("WAITING until threads have finished before writing to file\n");
        
        for (int i = 1, j = 0; i <= parts; i++, j++)
        {
            try
            {
                threads[j].join();
            }
            catch (InterruptedException ex)
            {
                System.out.println("Problem joining thread " + j);
                
            }
        }   
        
        try
        {
            //WRITE PARTS TO SINGLE FILE
            byte[] ba = new byte[1024];
            int l = 0;
            System.out.println("WRITING to file " + fileName + "\n");
            File outputFile = new File(fileName);
            OutputStream os = new FileOutputStream(outputFile);

            int line = 1;

            for (int i = 1; i <= parts; i++)
            {
                InputStream is = new FileInputStream("part_" + i);
                ba = new byte[1024];
                l = 0;
                line = 0;

                //PROBLEM OCCURS IF "CR NL CR NL" OCCURS OVER MULTIPLE
                //BYTE ARRAYS
                while ((l = is.read(ba)) >= 0)
                {
                    /* 
                    System.out.println("\n" + line + "(" + l + "): ");
                    for (int j = 0; j < l; j++)
                    {
                    System.out.print((char) ba[j]);
                    }
                     */
                    os.write(ba, 0, l);
                    os.flush();
                    line++;
                }
                is.close();
            }
            os.close();
        }
        catch (Exception ex)
        {
            System.out.println("Problem occured writing to file");
        }
    }
}
