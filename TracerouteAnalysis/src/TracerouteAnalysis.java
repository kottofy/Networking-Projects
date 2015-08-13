/*
 * Author: Kristin Ottofy
 * File name: TracerouteAnalysis.java
 * Program Name: TracerouteAnalysis
 * Due Date: January 16, 2012
 * Class: CSCI 4760 - Networks
 * 
 * Project Description: a program that takes in input a textual tcpdump trace of
 * traffic generated by Traceroute and computes the time between a TCP packet 
 * sent by the client and the related ICMP "Time exceeded in-transit" message
 * 
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TracerouteAnalysis
{

    public static void main(String[] args)
    {
        if (args.length == 1)
        {
            //the tcpdump text file 
            String filename = args[0];

            try
            {
                //open file
                FileInputStream fis = new FileInputStream(filename);
                DataInputStream in = new DataInputStream(fis);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                
                ArrayList<TracerouteAnalysis> hops = new ArrayList();
                String str = "";
                String newStr = "";
                boolean newLine = false;
                int i = 0;  //line count
                
                //begin reading the file
                while ((str = br.readLine()) != null)
                {
                    newLine = false;
                    String resetString = newStr + str;
                    str = resetString;

                    //deal with finding where the new line starts.
                    //new lines are determined by whether or not the 
                    //first character in a line is an integer
                    while (!newLine)
                    {
                        if ((newStr = br.readLine()) != null)
                        {
                            String cStr = "";
                            cStr += newStr.charAt(0);
                            
                            try
                            {
                                Integer.parseInt(cStr);
                                newLine = true;
                            }
                            catch (Exception e)
                            {
                                str += newStr;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                    //System.out.println("line " + i + ": " + str);

                    //find hops (TracerouteAnalyis) 
                    int icmpIndex = str.indexOf("ICMP");
                    if (icmpIndex > -1) //ICMP OMG OMG OMG
                    {
                        //find second time
                        String time2 = "";
                        for (int n = 0; str.charAt(n) != ' '; n++)
                        {
                            time2 += str.charAt(n);
                        }
                        //System.out.println("time2: " + time2);

                        //find second id
                        int id1Index = str.indexOf("id");
                        str = str.substring(id1Index + 1);
                        int idIndex = str.indexOf("id");
                        String idStr = "";
                        for (int n = idIndex + 3; str.charAt(n) != ','; n++)
                        {
                            idStr += str.charAt(n);
                        }
                        //System.out.println("id: " + idStr);

                        //if id = 0, don't care
                        if (!idStr.matches("0"))
                        {
                            int id = 0;
                            double time2Double = 0;

                            try
                            {
                                id = Integer.parseInt(idStr);
                                time2Double = Double.parseDouble(time2);
                            }
                            catch (Exception e)
                            {
                                System.out.println("An error has occured parsing.");
                            }

                            //find IP
                            int ipIndex = str.indexOf("length");
                            str = str.substring(ipIndex);
                            ipIndex = str.indexOf(")") + 1;
                            str = str.substring(ipIndex);
                            int count = 0;

                            //values 48-57 are numbers
                            for (int k = 0; !(Character.valueOf(str.charAt(k)) >= 48 && Character.valueOf(str.charAt(k)) <= 57); k++)
                            {
                                count++;
                            }

                            ipIndex = count;
                            str = str.substring(ipIndex);

                            String IP = "";
                            for (int n = 0; (Character.valueOf(str.charAt(n)) >= 48 && Character.valueOf(str.charAt(n)) <= 57) || Character.valueOf(str.charAt(n)) == 46; n++)
                            {
                                IP += str.charAt(n);
                            }
                            //System.out.println("IP: " + IP);

                            //if second id matches a first id, add it to the list of hops
                            for (int k = 0; k < hops.size(); k++)
                            {
                                int idComp = hops.get(k).getId();

                                if (k != i && idComp == id)
                                {
                                    hops.get(k).setTime2(time2Double);
                                    hops.get(k).setIP(IP);
                                }
                            }
                        }
                    }
                    else //NOT ICMP..act normal
                    {
                        //find first id
                        int idChar = str.indexOf("id");
                        String idStr = "";
                        for (int n = idChar + 3; str.charAt(n) != ','; n++)
                        {
                            idStr += str.charAt(n);
                        }
                        //System.out.println("id: " + idStr);

                        //if id = 0, don't care
                        if (!idStr.matches("0"))
                        {
                            //find first time
                            String time1 = "";
                            for (int n = 0; str.charAt(n) != ' '; n++)
                            {
                                time1 += str.charAt(n);
                            }
                            //System.out.println("time1: " + time1);

                            //find TTL
                            int ttlCharAt = str.indexOf("ttl");
                            String ttlStr = "";
                            for (int n = ttlCharAt + 4; str.charAt(n) != ','; n++)
                            {
                                ttlStr += str.charAt(n);
                            }
                            //System.out.println("ttl: " + ttlStr);

                            int ttl = 0;
                            int id = 0;
                            double time1Double = 0;

                            try
                            {
                                ttl = Integer.parseInt(ttlStr);
                                id = Integer.parseInt(idStr);
                                time1Double = Double.parseDouble(time1);
                            }
                            catch (Exception e)
                            {
                                System.out.println("An error has occured parsing.");
                            }

                            //if a valid hop has been found, add it to the list
                            if (ttl > 0 && id > 0 && time1.length() > 0)
                            {
                                TracerouteAnalysis hop = new TracerouteAnalysis(ttl, id, "", time1Double);
                                hops.add(hop);
                            }
                        }
                    }/*
                    int counter = 1;
                    for (int h = 0; h < hops.size(); h++)
                    {
                    if (hops.get(h).getTime2() != 0)
                    {
                    System.out.println("\nhop " + counter++);
                    System.out.println("TTL " + hops.get(h).getTtl());
                    System.out.println("IP " + hops.get(h).getIP());
                    System.out.println("Time " + ((hops.get(h).getTime2() - hops.get(h).getTime1()) * 1000) + " ms");
                    }
                    }*/
                    i++;
                }

                //find the max TTL number
                int maxTTL = 0;
                for (int h = 0; h < hops.size(); h++)
                {
                    int tempTTL = hops.get(h).getTtl();
                    if (tempTTL > maxTTL)
                    {
                        maxTTL = tempTTL;
                    }
                }                

                ArrayList<TracerouteAnalysis> list = new ArrayList();

                //find the rounded time difference, add new hop with new time to a new list
                for (int t = 0; t < maxTTL; t++)
                {
                    for (int h = 0; h < hops.size(); h++)
                    {
                        int ttl = hops.get(h).getTtl();

                        if (ttl == t)
                        {
                            double time1 = hops.get(h).getTime1();
                            double time2 = hops.get(h).getTime2();
                            String timeRounded = "";

                            if (time2 != 0)
                            {
                                try
                                {
                                    double time = (time2 - time1) * 1000;
                                    BigDecimal bd = new BigDecimal(Double.toString(time));
                                    bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
                                    DecimalFormat myFormatter = new DecimalFormat("0.000");
                                    timeRounded = myFormatter.format(Double.parseDouble(bd.toString()));

                                    TracerouteAnalysis hop = new TracerouteAnalysis(hops.get(h).getTtl(), hops.get(h).getIP(), timeRounded);
                                    list.add(hop);
                                }
                                catch (Exception e)
                                {
                                    System.out.println("An error occured while calculating time.");
                                }
                                /*
                                System.out.println("TTL " + hops.get(h).getTtl());
                                System.out.println(hops.get(h).getIP());
                                System.out.println(timeRounded + " ms");
                                 */
                            }
                        }
                    }
                }

                //print list 
                ArrayList<TracerouteAnalysis> ttls = new ArrayList(); //a temp list of hops by the same ttl
                ArrayList print = new ArrayList();  //a list of what to print. probably don't need this.

                for (int t = 0; t < maxTTL; t++)
                {
                    for (int h = 0; h < list.size(); h++)
                    {
                        if (t == list.get(h).getTtl())
                        {
                            ttls.add(list.get(h));
                        }
                    }

                    //ttls is now a list of hops with the same ttl

                    boolean firstPrint = true;  //keeps track if the TTL needs to be printed
                    
                    while (!ttls.isEmpty())
                    {
                        //probably don't need toAdd but it made for easier debugging
                        ArrayList toAdd = new ArrayList(); 
                        
                        if (firstPrint)
                        {
                            toAdd.add("TTL " + ttls.get(0).getTtl());
                        }
                        
                        toAdd.add(ttls.get(0).getIP());
                        toAdd.add(ttls.get(0).getTime() + " ms");

                        String ip = ttls.get(0).getIP();
                        ttls.remove(0);
                        //compare ips with the same ttl and add to string
                        for (int a = 0; a < ttls.size(); a++)
                        {
                            if (ip.matches(ttls.get(a).getIP()))
                            {
                                toAdd.add(ttls.get(a).getTime() + " ms");
                                ttls.remove(a);
                                a--;
                            }
                        }
                       
                        print.addAll(toAdd);
                        firstPrint = false;
                    }
                }


                for (int p = 0; p < print.size(); p++)
                {
                    System.out.println(print.get(p));
                }

                in.close();
            }
            catch (Exception e)
            {
                System.err.println(e);
            }
        }
        else
        {
            System.out.println("Please enter java  TracerouteAnalysis  inputfile.txt");
            System.exit(1);
        }
    }

    private int ttl;
    private int id;
    private String IP;
    private double time1;
    private double time2;
    private String time;

    /*
     * A Hop
     */
    public TracerouteAnalysis(int ttl, int id, String IP, double time1)
    {
        this.ttl = ttl;
        this.id = id;
        this.IP = IP;
        this.time1 = time1;
    }

    /*
     * A hop with information needed pertaining to printing
     */
    public TracerouteAnalysis(int ttl, String IP, String time)
    {
        this.ttl = ttl;
        this.IP = IP;
        this.time = time;
    }

    /* The following are getters and setters pertaining to the "hops"  */
    public String getTime()
    {
        return time;
    }

    public void setTime2(double time2)
    {
        this.time2 = time2;
    }

    public void setIP(String IP)
    {
        this.IP = IP;
    }

    public int getId()
    {
        return id;
    }

    public double getTime1()
    {
        return time1;
    }

    public void setTime1(double time1)
    {
        this.time1 = time1;
    }

    public int getTtl()
    {
        return ttl;
    }

    public String getIP()
    {
        return IP;
    }

    public double getTime2()
    {
        return time2;
    }
}