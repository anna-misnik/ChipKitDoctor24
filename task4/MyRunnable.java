import tcpclient.TCPClient;

import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MyRunnable implements Runnable {

    int BUFFERSIZE = 1024; //size of the package to sent
    private final Socket clientSocket;

    //constructor (assigns new connection to the clientSocket)
    public MyRunnable(Socket clientSocket) { //when MyRunnable is created a socket connection must be passed
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        /*READING & SAVING CLIENT INPUT
         * example: GET /ask?hostname=localhost&port=10002&string=The_time_is_Tue_Mar__2_20-54-39_2021 HTTP/1.1
         *     Host: localhost
         *     */
        byte[] fromClientBuffer; //user encoded input
        fromClientBuffer = new byte[BUFFERSIZE];
        StringBuilder clientdata = new StringBuilder(); //user decoded input

        int fromclientLength; //nr of bytes received or -1 if end of data

        try {
            while ((fromclientLength = clientSocket.getInputStream().read(fromClientBuffer)) > 0) { //run until no data incoming
                String decodedString = new String(fromClientBuffer, 0, fromclientLength, StandardCharsets.UTF_8); //decode the string
                clientdata.append(decodedString);
                clientdata.append("\r\n");

                if (decodedString.contains("\r\n\r\n") || decodedString.contains("\n\n")) //used to break a while loop, debugg using telnet
                    break;
            }

            //CREATING SERVER OUTPUT/ANSWER
            String decoded_clientdata = http_response(clientdata.toString()); //call another method which returns status line + answer from the server
            byte[] toClientBuffer = decoded_clientdata.getBytes(StandardCharsets.UTF_8); //sending back clients input as a encoded string
            clientSocket.getOutputStream().write(toClientBuffer); //sends answer
            clientSocket.close(); //closing connection with the current client
        } catch (Exception exception){
            System.out.println("Something is wrong!");
        }
    }


    public static String http_response(String clientdata) throws Exception {
        StringBuilder http_response = new StringBuilder();

        /*SPLITTING THE CLIENT STRING, ANALYZING FAILED REQUESTS & SENDING DETAILS TO TCPClient*/

        //example GET /ask?hostname=localhost&port=10001 HTTP/1.1
        String[] split_string = clientdata.split(" "); //split the GET request to different parts by empty space

        if (split_string.length < 3) {
            http_response.append("HTTP/1.1 400 Bad Request" + "\r\n");
            return http_response.toString();
        }

        if (!split_string[0].equals("GET")) {
            http_response.append("HTTP/1.1 501 Not Valid Method" + "\r\n");
            return http_response.toString();
        }

        String split_string2 = split_string[2]; //equals t.ex. HTTP/1.1
        if (!split_string2.contains("HTTP/1.1")) {
            http_response.append("HTTP/1.1 400 Bad Request " + "\r\n");
            return http_response.toString();
        }

        String http_details1 = split_string[1]; //equals this example (/ask?hostname=localhost&port=10002&string=The_time_is_Tue_Mar__2_20-54-39_2021)
        if (!http_details1.contains("/ask?")) {
            http_response.append("HTTP/1.1 400 Bad Request" + "\r\n"); // contains \ask?
            return http_response.toString();
        }

        String[] http_details2 = http_details1.split("&");
            /* http_details2[0] = \ask?hostname=localhost;
            http_details2[1] = port=10002 etc.  */

        String[] first_pair = http_details2[0].split("="); //askhostname = "hostname"
        String[] second_pair = http_details2[1].split("="); // portnumber = "port number"

        if (!(first_pair[0].contains("hostname") && second_pair[0].contains("port")) && !(first_pair[0].contains("port") || !second_pair[0].contains("hostname"))) {
            http_response.append("HTTP/1.1 400 Bad Request" + "\r\n");
            return http_response.toString();
        }
        else {
            String hostname;
            int port;

            //checks the position of the hostname and the port and initializes the values
            if (first_pair[0].contains("hostname")){
                hostname = first_pair[1];
                port = Integer.parseInt(second_pair[1]);
            }else {
                hostname = second_pair[1];
                port = Integer.parseInt(first_pair[1]);
            }

            try {
                if (http_details2.length == 3) { //check if there's a string
                    String[] third_pair = http_details2[2].split("=");
                    if (third_pair[0].contains("string")) {
                        http_response.append("HTTP/1.1 200 OK\r\n\r\n" + TCPClient.askServer(hostname, port, third_pair[1]));
                        return http_response.toString();
                    } else {
                        http_response.append("HTTP/1.1 400 Bad Request" + "\r\n");
                        return http_response.toString();
                    }
                }
                else {
                    http_response.append("HTTP/1.1 200 OK" + "\r\n\r\n" + TCPClient.askServer(hostname, port));
                    return http_response.toString();
                }
            }
            catch (Exception exception) { //if nothing before worked
                http_response.append("HTTP/1.1 404 Not found" + "\r\n");
                return http_response.toString();
            }
        }
    }
}

