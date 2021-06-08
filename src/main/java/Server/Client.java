package Server;

import java.io.*;
import java.net.Socket;

public class Client {

    private long id;

    private Socket clientSocket;

    private BufferedReader reader;

    private BufferedWriter writer;

    public long getId() {
        return id;
    }

    public void writeMessage(String text) throws IOException {
        writer.write(text);
        writer.newLine();
        writer.flush();
    }



    public String readMessage() throws IOException {
        var responsible = reader.readLine();
        return responsible;
    }

    public Client(String host,int port, long id){
        this.id = id;
        try {
            clientSocket = new Socket(host,port);
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        reader.close();
        writer.close();
        clientSocket.close();
    }
}

