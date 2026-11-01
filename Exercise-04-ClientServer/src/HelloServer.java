import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class HelloServer {
    public static final int DEFAULT_PORT = 5000;

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            do {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Connection accepted from " + socket.getRemoteSocketAddress());
                    Runnable reqProcessor = new RequestProcessor(socket);
                    new Thread(reqProcessor).start();
                }
                catch (IOException ex) {
                    System.err.println("Error accepting connection: " + ex.getMessage());
                }
            } while (true);
        }
        catch (IOException ex) {
            System.err.println("Could not start server on port " + port + ": " + ex.getMessage());
        }
    }
}
