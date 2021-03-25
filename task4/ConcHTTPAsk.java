import java.net.ServerSocket;
import java.net.Socket;

public class ConcHTTPAsk {
    public static void main( String[] args) throws Exception {

        try{
            int port_number = Integer.parseInt(args[0]); //port number from the user
            ServerSocket welcomeSocket = new ServerSocket(port_number); //creates new welcome socket; listens and binds

            while (true) { //server works in infinite loop; always ready for new connections
                Socket connectionSocket = welcomeSocket.accept(); //new socket for the client, connecten accepted
                new Thread(new MyRunnable(connectionSocket)).start();
            } //end of the loop state, go back & wait for another client
        } catch (Exception exception){
            System.out.println("Something is wrong!");
        }
    }
}

