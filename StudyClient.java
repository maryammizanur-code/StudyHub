
package studymate.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class StudyClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5000;

    public void start() {

        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(HOST, PORT), 3000);
            socket.setSoTimeout(5000);
            try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
                PrintWriter output = new PrintWriter(
                        socket.getOutputStream(),
                        true,
                        StandardCharsets.UTF_8);

                BufferedReader keyboard = new BufferedReader(
                        new InputStreamReader(
                                System.in,
                                StandardCharsets.UTF_8))
            ) {

                // Read the two greeting lines from the server.
                String greeting1 = input.readLine();
                String greeting2 = input.readLine();

                System.out.println(greeting1);
                System.out.println(greeting2);

                while (true) {

                    System.out.print("Enter command: ");

                    String request = keyboard.readLine();

                    if (request == null) {
                        break;
                    }

                    request = request.trim();

                    if (request.isEmpty()) {
                        System.out.println(
                                "Please enter a command.");
                        continue;
                    }

                    output.println(request);

                    if (output.checkError()) {
                        System.out.println(
                                "Could not send request.");
                        break;
                    }

                    String response = input.readLine();
                    if (response == null) {
                        System.out.println("Server closed the connection.");
                        break;
                    }
                    String upperRequest = request.toUpperCase();
                    if (upperRequest.startsWith("TASK_LIST|") && response.equals("TASK_LIST_RESULT")) {
                        System.out.println("Server response: " + response);

    String line;

    while ((line = input.readLine()) != null) {
        System.out.println(line);

        if (line.equals("END_TASK_LIST")) {
            break;
        }
    }

} else if (upperRequest.startsWith("TASK_SEARCH|")
        && response.equals("TASK_SEARCH_RESULT")) {

    System.out.println("Server response: " + response);

    String line;

    while ((line = input.readLine()) != null) {
        System.out.println(line);

        if (line.equals("END_TASK_SEARCH")) {
            break;
        }
    }

} else if (upperRequest.startsWith("TASK_FILTER|")
        && response.equals("TASK_FILTER_RESULT")) {

    System.out.println("Server response: " + response);

    String line;

    while ((line = input.readLine()) != null) {
        System.out.println(line);

        if (line.equals("END_TASK_FILTER")) {
            break;
        }
    }

} else {
    System.out.println("Server response: " + response);
}
if (request.equalsIgnoreCase("QUIT")) {
                        break;
                    }
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Client error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        StudyClient client = new StudyClient();
        client.start();
    }
}
