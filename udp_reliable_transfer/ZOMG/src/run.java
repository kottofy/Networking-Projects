import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class run {
   
    public static class ServerThread implements Runnable{

        private ServerThread() {

        }
        
        public void run() {
            Server s = new Server(4445);
            try{
                s.start();
            }
            catch(Exception e ){
                System.out.println(e);
            }
        }

    }
    
    public static class ClientThread implements Runnable{
                
        private ClientThread() {

        }
        
        public void run() {
            Client c = new Client(4445, "localhost", "moby_dick");
            try {
                c.start();
            } catch (InterruptedException ex) {
                System.out.println("rawr");
            }
        }

    }
    
    
    public static void main(String []args) throws IOException, InterruptedException{
        
        ExecutorService threadExecutor = Executors.newFixedThreadPool(2);
        ServerThread dt1 = new ServerThread();
        ClientThread dt2 = new ClientThread();
        threadExecutor.execute(dt1);
        threadExecutor.awaitTermination(3, TimeUnit.SECONDS);
        threadExecutor.execute(dt2);

        threadExecutor.shutdown();
        threadExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            
       

    }
}
