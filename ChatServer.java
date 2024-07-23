import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ChatServer {
    private static Map<String, PrintWriter> clients = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("El servidor está en funcionamiento.");

            while (true) {
                Socket clientSocket = serverSocket.accept();

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String sender, String message) {
        for (Map.Entry<String, PrintWriter> entry : clients.entrySet()) {
            String clientName = entry.getKey();
            PrintWriter clientWriter = entry.getValue();

            if (!sender.equals(clientName)) {
                clientWriter.println(sender + ": " + message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter clientWriter;
        private Scanner in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            try {
                in = new Scanner(clientSocket.getInputStream());
                String clientName = in.nextLine();
                clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.put(clientName, clientWriter);

                while (true) {
                    String message = in.nextLine();

                    if (message.equals("exit")) {
                        clients.remove(clientName);
                        break;
                    }

                    if (message.startsWith("mensaje:")) {
                        String[] parts = message.split(" ", 2);
                        String[] recipientParts = parts[0].split(":");
                        String recipient = recipientParts[1];
                        String content = parts[1];
                        PrintWriter recipientWriter = clients.get(recipient);

                        if (recipientWriter != null) {
                            recipientWriter.println(clientName + ": " + content);
                        } else {
                            clientWriter.println("El cliente '" + recipient + "' no está conectado.");
                        }
                    } else {
                        broadcast(clientName, message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    in.close();
                }
                if (clientWriter != null) {
                    clientWriter.close();
                }
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
