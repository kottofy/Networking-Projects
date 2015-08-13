
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    private DatagramSocket serverSocket;
    private String file_name;
    private byte[] buf = new byte[512];
    private int proxy_port;
    private InetAddress address;
    private int port;
    private byte[] previousBuffer = new byte[512];
    
    public Server(int port){
        this.port = port;
    }
    
    public void start(){

        try{
            //bind port
            serverSocket = new DatagramSocket(port);
            System.out.println("[Server] Port binded");
            
            //Create the server_dir directory
            //createDirectory();
            //System.out.println("[Server] Directory created");
            
            ArrayList<byte[]> packets = new ArrayList<byte[]>();
            while(true){
                //Receive Packet
                DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
                serverSocket.receive(receivedPacket);

                //Print out data     
                //buf = receivedPacket.getData();
                System.out.println ("[Server] Received packet from client");
                packets.add(buf);

                //Prints the packet data to the screen
                //printPacketContents(buf);
                
                //Reply to client
                sendAck(receivedPacket);
                
                
                writeToFile(buf);
                buf = new byte[512];
            }    
            
        }
        catch(SocketException e ){
            System.out.println("Server.start() error: " + e);
        }
        catch(IOException e ){
            System.out.println("Server.start() error: " + e);
        }
         
    }
    
    //public checkTheArrayListAndIfTwoEqualThenDisragrdOne
    /*public void checkArrays(ArrayList<byte[]> packets){
        try{
            for(int i = 0; i < packets.size(); i++){
                if(i == packets.size()){
                    if(packets.get(i) != packets.get(i-1))
                        writeToFile(packets.get(i));
                }
                
                if(packets.get(i) != packets.get(i+1))
                    writeToFile(packets.get(i));          
                if(i == 0)
                    writeToFile(packets.get(i));
            }
        }
        catch(IOException e){
            System.out.println("Server.checkArrays() error: " + e);
        }
    }*/
    
    public void writeToFile(byte[] data) throws IOException{ 
        FileWriter fstream = new FileWriter("server_dir/moby_dick.txt", true);
        BufferedWriter out = new BufferedWriter(fstream);
       
        for(int i = 0; i < data.length; i++){
            out.write(data[i]);
            out.flush();       
        }
        fstream.close();
        out.close();
    }
    
    public void createDirectory(){
        File dir = new File("server_dir");  
        dir.mkdir();      
    }
    
    
    public void printPacketContents(byte[] b){
        byte[] bytePacket = b;
        
        for(int i = 0; i < b.length; i++){
            System.out.print((char)bytePacket[i]);
        }
    }
   
    public boolean sendAck(DatagramPacket receivedPacket){
        try{
            address = receivedPacket.getAddress();
            //address = InetAddress.getByName("localhost");
            proxy_port = receivedPacket.getPort();
            DatagramPacket repliedPacket = new DatagramPacket(buf, buf.length, address, port);
            
            System.out.println("address: " + address);
            System.out.println("port: " + port);
            
            serverSocket.send(repliedPacket);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public boolean checkData(byte[] buffer){
        if(buffer == previousBuffer)
            return true; 
        else
            return false;
    }
    
    
}
