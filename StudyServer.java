
package studymate.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudyServer {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 10;

    private final ExecutorService clientPool =
            Executors.newFixedThreadPool(MAX_CLIENTS);

    private ServerSocket serverSocket;

    public void start() throws IOException {

        serverSocket = new ServerSocket();
        serverSocket.setReuseAddress(true);

        serverSocket.bind(
                new InetSocketAddress(HOST, PORT)
        );

        System.out.println("StudyHub TCP Server Started");
        System.out.println("Host: " + HOST);
        System.out.println("Port: " + PORT);
        System.out.println("Waiting for clients...");

        try {
            while (!serverSocket.isClosed()) {

                Socket clientSocket = serverSocket.accept();

                System.out.println(
                        "Client connected: "
                        + clientSocket.getRemoteSocketAddress()
                );

                clientPool.execute(
                        new ClientHandler(clientSocket)
                );
            }

        } catch (IOException e) {

            if (!serverSocket.isClosed()) {
                throw e;
            }

        } finally {
            stop();
        }
    }

    public void stop() {

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println(
                    "Error closing server: " + e.getMessage()
            );
        }

        clientPool.shutdownNow();

        System.out.println("StudyHub TCP Server Stopped");
    }

    public static void main(String[] args) {

        StudyServer server = new StudyServer();

        Runtime.getRuntime().addShutdownHook(
                new Thread(server::stop)
        );

        try {
            server.start();

        } catch (IOException e) {

            System.err.println(
                    "Could not start server: " + e.getMessage()
            );

            server.stop();
        }
    }
}
