package server;



import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static ExecutorService executeIt = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(12123, 50, InetAddress.getByName("127.0.0.1"))) {
            Socket client = server.accept();
            executeIt.execute(new server(client));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
