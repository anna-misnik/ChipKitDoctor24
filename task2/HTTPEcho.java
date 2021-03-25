import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
/**
 Author: Anna Misnik
 Latest Update: 22.02.2021
 OBS! Some of the code part are borrowed from the literature or the lectures **/

public class HTTPEcho {
    public static void main( String[] args) throws IOException {

        int BUFFERSIZE = 1024; //size of the package to sent

        int port_number = Integer.parseInt(args[0]); //port number from the user
        ServerSocket welcomeSocket = new ServerSocket(port_number); //creates new welcome socket; listens and binds

        while (true) { //server works in infinite loop; always ready for new connections
            Socket connectionSocket = welcomeSocket.accept(); //new socket for the client, connecten accepted

            //READING & SAVING CLIENT INPUT

            byte[] fromClientBuffer = new byte[BUFFERSIZE]; //user encoded input
            StringBuilder clientdata = new StringBuilder(); //user decoded input

            int fromclientLength; //nr of bytes received or -1 if end of data

                while((fromclientLength = connectionSocket.getInputStream().read(fromClientBuffer)) > 0) { //run until no data incoming
                    String decodedString = new String(fromClientBuffer, 0, fromclientLength, StandardCharsets.UTF_8); //decode the string
                    clientdata.append(decodedString + "\r\n");

                    if(decodedString.contains("\r\n\r\n") || decodedString.contains("\n\n")) //used to break a while loop, debugg using telnet
                        break;
                }
            //CREATING SERVER OUTPUT/ANSWER
            String decoded_clientdata = "HTTP/1.1 200 OK" + "\r\n\r\n" + clientdata.toString(); //add statusline + empty line + data fr the client
            byte[] toClientBuffer = decoded_clientdata.getBytes(StandardCharsets.UTF_8); //sending back clients input as a encoded string
            connectionSocket.getOutputStream().write(toClientBuffer); //sends answer
            connectionSocket.close(); //closing connection with the current client
        } //end of the loop state, go back & wait for another client
    }
}

