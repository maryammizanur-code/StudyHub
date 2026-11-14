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
    public String sendRequest(String request) throws IOException {
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress(HOST, PORT), 3000);
            socket.setSoTimeout(5000);
            try (
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(),StandardCharsets.UTF_8));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(),true,StandardCharsets.UTF_8))
            {
                input.readLine();
                input.readLine();
                output.println(request);
                if (output.checkError()){
                    throw new IOException("Could not send request.");
                }
                String response = input.readLine();
                if (response==null) {
                    throw new IOException("Server closed the connection.");
                }
                String upperRequest = request.trim().toUpperCase();
                if (upperRequest.startsWith("TASK_LIST|")&&response.equals("TASK_LIST_RESULT"))
                {
                    return readMultiLineResponse(input, "END_TASK_LIST");
                }
                if (upperRequest.startsWith("TASK_SEARCH|")&&response.equals("TASK_SEARCH_RESULT"))
                {
                    return readMultiLineResponse(input, "END_TASK_SEARCH");
                }
                if (upperRequest.startsWith("TASK_FILTER|")&&response.equals("TASK_FILTER_RESULT"))
                {
                    return readMultiLineResponse(input, "END_TASK_FILTER");
                }
                return response;
            }
        }
    }
    private String readMultiLineResponse(
            BufferedReader input,
            String endMarker
    ) throws IOException{
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = input.readLine()) != null){
            if (line.equals(endMarker)) {
                break;
            }
            if (result.length() > 0) {
                result.append("\n");
            }
            result.append(line);
        }
        return result.toString();
    }
    public void start(){
        try (
                BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8))
        ){
            while (true) {
                System.out.print("Enter command: ");
                String request = keyboard.readLine();
                if (request == null || request.trim().isEmpty())
                {
                    continue;
                }
                String response = sendRequest(request.trim());
                System.out.println("Server response:");
                System.out.println(response);
                if (request.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }}catch(IOException e){
                System.err.println("Client error: " + e.getMessage());
        }
    }
    public static void main(String[] args) {
        StudyClient client = new StudyClient();
        client.start();
    }
}
