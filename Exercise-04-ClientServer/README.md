## Exercise 04 — Hello World Client-Server

### Targets

- Implement a client that sends a message to a server.
- The server reads the message and responds "I received " + message + "! " to the client.
- Use sockets to communicate between the two JVMs.
- Test the application and make sure it works.

### Solution

The solution follows the structure of the example web server shown in the lecture.

- `HelloServer` creates a `ServerSocket` on port 5000 (or the port given as the first argument) and waits for connections in a loop. Each time a client connects, it accepts the connection and hands the socket to a new `RequestProcessor` running in its own thread, so several clients can be served at the same time.
- `RequestProcessor` reads one line of text from the socket's input stream, builds the response "I received " + message + "! " and writes it back on the output stream. The writer uses autoflush so the response is sent right away. At the end only the socket is closed, because closing the socket also closes its streams (closing a stream first would close the socket and make the other stream fail, as warned in the lecture).
- `HelloClient` connects to the server with a 1000 ms timeout (the "timed client socket" from the lecture), sends its message as one line, waits for the response line and prints it. The message, host and port can be given as arguments; by default it sends "Hello World" to localhost on port 5000. If the server cannot be reached, it prints an error instead of crashing.

### Running

Start the server in one terminal, then run the client in another one, so the two programs run in separate JVMs:

```
java HelloServer
java HelloClient "my message"
```

The client prints `Server response: I received my message! `, and the server logs the connection, the received message and the response it sent.

### Test

The server and the client were run as two separate JVMs. The client sent "my message", then a second client sent the default "Hello World", and each one received the expected "I received ...! " response. The server handled both connections one after the other without restarting. Running the client while no server was listening printed a "Connection refused" error.
