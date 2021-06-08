package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NServer implements Runnable {

    private final int port;

    protected ServerSocket serverSocket;

    protected boolean isStopped    = false;

    protected Thread runningThread;

    protected ExecutorService threadPool =
            Executors.newFixedThreadPool(3);

    public NServer(int port){
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {

        var server = new NServer(8081);
        new Thread(server).start();

    }

    @Override
    public void run() {

        synchronized (this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();

        while (!this.isStopped){
            Socket socket=null;
            try {
                socket= serverSocket.accept();

            } catch (IOException e) {
                if(isStopped){
                    System.out.println("Server stopped");
                    break;
                }
                e.printStackTrace();
            }
            this.threadPool.execute(new WorkerRunnable(socket));
        }
        this.threadPool.shutdown();
        System.out.println("Server stopped");
    }

    private void openServerSocket(){
        try{
            this.serverSocket = new ServerSocket(this.port);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private synchronized boolean isStopped(){
        return this.isStopped;
    }

    private synchronized void stop(){
        isStopped = true;
        try {
            this.serverSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}


