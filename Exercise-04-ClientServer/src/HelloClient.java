import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class HelloClient {
    private static final int CONNECT_TIMEOUT_MS = 1000;

    public static void main(String[] args) {
        String message = args.length > 0 ? args[0] : "Hello World";
        String host = args.length > 1 ? args[1] : "localhost";
        int port = args.length > 2 ? Integer.parseInt(args[2]) : HelloServer.DEFAULT_PORT;

        try (Socket socket = new Socket()) {
            InetAddress netAddr = InetAddress.getByName(host);
            SocketAddress sockAddr = new InetSocketAddress(netAddr, port);
            socket.connect(sockAddr, CONNECT_TIMEOUT_MS);

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true); // Autoflush
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            writer.println(message);
            System.out.println("Sent: " + message);

            String response = reader.readLine();
            System.out.println("Server response: " + response);
        }
        catch (IOException ex) {
            System.err.println("Could not communicate with server " + host + ":" + port + ": " + ex.getMessage());
        }
    }
}
