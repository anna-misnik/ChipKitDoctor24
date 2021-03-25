package tcpclient;
import java.net.*; //defines socket class
import java.io.*;
import java.nio.charset.StandardCharsets;
/**
Author: Anna Misnik
Latest Update: 15.02.2021
OBS! Some of the code part are borrowed from the literature or the lectures **/

public class TCPClient {
    private static int BUFFERSIZE = 1024; // where the sent/received data will be placed


    /*Function askServer takes 3 parameters: domain name, port number to connect and the user message to the server.
    * Firstly, it checks if there's an incoming message, otherwise jumping to alternative func. askServer w. no user input.
    * Secondly, creating socket and connecting to the server. After fuccessfull connection, starting sending data to the server.
    * All the input/output data is an byte array.
    * Thirdly, receiving incoming data from the server. OBS! no timer set here, instead >> catching an Timeout exception
    * and returning whatever data exists.
    * Lastly, closing the connection and returning the decoded output from the server as a string.
    * */
    public static String askServer(String hostname, int port, String ToServer) throws IOException {
        //pre-allocated arrays for input\output
        byte[] fromUserBuffer = new byte[BUFFERSIZE];
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        //check if there's a user input. If NO >> jump to askServer\2
        if (ToServer == null) {
            askServer(hostname, port);
        }

        // step 1: open connection
        Socket clientSocket = new Socket(hostname, port);   //Creates a stream socket and connects it to the specified port number on the named host.
        clientSocket.setSoTimeout(3000); //wait until connected

        // STEP 2 send data to the server
        String ToServer2 = ToServer + '\n'; //user message
        fromUserBuffer = ToServer2.getBytes(StandardCharsets.UTF_8);  //encode a string into a byte array

        DataOutputStream outputstream = new DataOutputStream(clientSocket.getOutputStream());
        outputstream.write(fromUserBuffer); //sending client message to the server

        //STEP 3 read the data
        DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream()); //open inputstream channel
        StringBuilder string = new StringBuilder(); //future output
        int fromserverLength;
        try{
            while((fromserverLength = inputStream.read(fromServerBuffer)) > 0) { //run until no data incoming
                String decodedString = new String(fromServerBuffer, 0, fromserverLength, StandardCharsets.UTF_8); //decode the string
                string.append(decodedString);
            }
            string.append('\n');
            }

        catch (SocketTimeoutException exception) { //catching IO error: read timed out & returning data what the program has
        }

        catch (IOException exception) { //great for debugging >> shows if there are any errors
        String error = "IO error: " + exception.getMessage();

          clientSocket.close(); // closes connection
          return error; //returning string with the detailes about the error cathced
        }

        //STEP 4 close the connection
        clientSocket.close(); //closes connection
       return string.toString(); //returning server (whatever) output exists
    }

    /*An alternative function to askServer\3 but w. 2 parameters instead: domain name & port number.
    * Firstly, creating socket and establishing the connection.
    * Secondly, reading the input stream and saving the incoming data as an byte array (decoded and returned as a string afterwards).
    * OBS! No timer set here, instead timer exception catched and the existing data returned.
    * Lastly, closing the connection and returning a string. */
    public static String askServer(String hostname, int port) throws IOException {
        //pre-allocated array
        byte[] fromServerBuffer = new byte[BUFFERSIZE];

        //STEP 1 open connection
        Socket clientSocket = new Socket(hostname, port); //Creates a stream socket and connects it to the specified port number on the named host.


        //STEP 2 read the data from the server
        DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream()); //open inputstream channel
        clientSocket.setSoTimeout(3000); //wait until connected

        int fromserverLength;
        StringBuilder string = new StringBuilder();// future output
        try{
            while((fromserverLength = inputStream.read(fromServerBuffer)) > 0) { //run until no data incoming
                String decodedString = new String(fromServerBuffer, 0, fromserverLength, StandardCharsets.UTF_8); //decode the string
                string.append(decodedString);
            }
            string.append('\n');
            }

        catch (SocketTimeoutException exception) { //catching IO error: read timed out & returning data what the program has
        }

        catch (IOException exception) {
        String error = "IO error: " + exception.getMessage();

          clientSocket.close();
          return error;
        }

        //STEP 4 close the connection
        clientSocket.close(); //closes connection
       return string.toString();
    }
}
