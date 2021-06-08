package Spring.models;

import java.io.*;
import java.net.Socket;

public class SocketClient {

    private Socket clientSocket;

    public void writeMessage(String text) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        writer.write(text);
        writer.newLine();
        writer.flush();
        writer.close();
    }

    public void readMessage() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        var responsible = reader.readLine();
        System.out.println(responsible);
        reader.close();
        clientSocket.close();
    }

    public SocketClient(String host,int port,String message){
        try {
            var clientSocket = new Socket(host,port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
