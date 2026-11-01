
package studymate.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import studymate.service.TaskService;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final TaskService taskService = new TaskService();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (
                Socket clientSocket = socket;
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream(),
                                StandardCharsets.UTF_8
                        )
                );
                PrintWriter output = new PrintWriter(
                        clientSocket.getOutputStream(),
                        true,
                        StandardCharsets.UTF_8
                )
        ) {

            clientSocket.setSoTimeout(60000);

            output.println("CONNECTED|StudyHub TCP Server");
            output.println("Send HELP to see available commands.");

            String command;

            while ((command = input.readLine()) != null) {

                if (command.equalsIgnoreCase("QUIT")) {
                    output.println("BYE");
                    break;
                }

                try {

                    String response = processCommand(command);
                    String upperCommand = command.trim().toUpperCase();

                    boolean multiLineResponse =
                            (upperCommand.startsWith("TASK_LIST|")
                            && response.startsWith("TASK_LIST_RESULT\n"))
                            || (upperCommand.startsWith("TASK_SEARCH|")
                            && response.startsWith("TASK_SEARCH_RESULT\n"))
                            || (upperCommand.startsWith("TASK_FILTER|")
                            && response.startsWith("TASK_FILTER_RESULT\n"));

                    if (multiLineResponse) {

                        String[] lines = response.split("\n", -1);

                        for (String line : lines) {
                            output.println(line);
                        }

                    } else {
                        output.println(response);
                    }

                } catch (IllegalArgumentException e) {

                    output.println("ERROR|" + e.getMessage());

                } catch (SQLException e) {

                    System.err.println("Database error: " + e.getMessage());
                    output.println("ERROR|Database operation failed.");
                }
            }

        } catch (IOException e) {

            System.err.println(
                    "Client connection ended: " + e.getMessage()
            );
        }
    }

    private String processCommand(String command)
            throws SQLException {

        if (command == null || command.trim().isEmpty()) {
            return "ERROR|Command cannot be empty.";
        }

        if (command.equalsIgnoreCase("PING")) {
            return "PONG";
        }

        if (command.equalsIgnoreCase("STATUS")) {
            return "OK|StudyHub TCP server is running.";
        }

        if (command.equalsIgnoreCase("HELP")) {

            return "COMMANDS|PING|STATUS|HELP|ECHO|"
                    + "TASK_LIST|TASK_SEARCH|TASK_FILTER|"
                    + "TASK_ADD|TASK_UPDATE|TASK_DONE|TASK_DELETE|QUIT";
        }

        if (command.startsWith("ECHO|")) {
            return "ECHO|" + command.substring(5);
        }

        String[] parts = command.split("\\|", -1);

        switch (parts[0].toUpperCase()) {

            case "TASK_LIST": {

                requireParts(parts, 2);

                int userId = parsePositiveInt(parts[1], "user ID");

                String tasks = taskService.listTasks(userId);

                return "TASK_LIST_RESULT\n"
                        + tasks
                        + "\nEND_TASK_LIST";
            }

            case "TASK_UPDATE": {

                requireParts(parts, 7);

                int userId = parsePositiveInt(parts[1], "user ID");
                int taskId = parsePositiveInt(parts[2], "task ID");

                String title = parts[3];
                String description = parts[4];
                String taskType = parts[5];
                String deadline = parts[6];

                boolean updated = taskService.updateTask(
                        userId,
                        taskId,
                        title,
                        description,
                        taskType,
                        deadline
                );

                return updated
                        ? "OK|Task updated successfully."
                        : "NOT_FOUND|Task not found for this user.";
            }

            case "TASK_DONE": {

                requireParts(parts, 3);

                int userId = parsePositiveInt(parts[1], "user ID");
                int taskId = parsePositiveInt(parts[2], "task ID");

                boolean updated = taskService.markTaskDone(
                        userId,
                        taskId
                );

                return updated
                        ? "OK|Task marked as completed."
                        : "NOT_FOUND|Task not found for this user.";
            }

            case "TASK_SEARCH": {

                requireParts(parts, 3);

                int userId = parsePositiveInt(parts[1], "user ID");
                String keyword = parts[2];

                String results = taskService.searchTasks(
                        userId,
                        keyword
                );

                return "TASK_SEARCH_RESULT\n"
                        + results
                        + "\nEND_TASK_SEARCH";
            }

            case "TASK_DELETE": {

                requireParts(parts, 3);

                int userId = parsePositiveInt(parts[1], "user ID");
                int taskId = parsePositiveInt(parts[2], "task ID");

                boolean deleted = taskService.deleteTask(
                        userId,
                        taskId
                );

                return deleted
                        ? "OK|Task deleted."
                        : "NOT_FOUND|Task not found for this user.";
            }

            case "TASK_FILTER": {

                requireParts(parts, 3);

                int userId = parsePositiveInt(
                        parts[1], "user ID"
                );

                String status = parts[2];

                String results = taskService.filterTasksByStatus(
                        userId,
                        status
                );

                return "TASK_FILTER_RESULT\n"
                        + results
                        + "\nEND_TASK_FILTER";
            }

            case "TASK_ADD": {

                requireParts(parts, 6);

                int userId = parsePositiveInt(parts[1], "user ID");

                String title = parts[2];
                String description = parts[3];
                String taskType = parts[4];
                String deadline = parts[5];

                boolean added = taskService.addTask(
                        userId,
                        title,
                        description,
                        taskType,
                        deadline
                );

                return added
                        ? "OK|Task added successfully."
                        : "ERROR|Task could not be added.";
            }

            default:
                return "ERROR|Unknown command. Send HELP.";
        }
    }

    private void requireParts(String[] parts, int expected) {

        if (parts.length != expected) {

            throw new IllegalArgumentException(
                    "Incorrect command format. Send HELP."
            );
        }
    }

    private int parsePositiveInt(String value, String fieldName) {

        try {

            int number = Integer.parseInt(value);

            if (number <= 0) {
                throw new IllegalArgumentException(
                        fieldName + " must be greater than zero."
                );
            }

            return number;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Invalid " + fieldName + "."
            );
        }
    }
}
