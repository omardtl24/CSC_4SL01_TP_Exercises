import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestProcessor implements Runnable {
    private Socket socket;

    public RequestProcessor(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        // Closing the socket closes both streams, so only the socket is closed here
        try (Socket clientSocket = socket) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true); // Autoflush

            String message = reader.readLine();
            System.out.println("Received: " + message);

            String response = "I received " + message + "! ";
            writer.println(response);
            System.out.println("Sent: " + response);
        }
        catch (IOException ex) {
            System.err.println("Error processing request: " + ex.getMessage());
        }
    }
}
