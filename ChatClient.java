import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner in = new Scanner(socket.getInputStream());
            Scanner scanner = new Scanner(System.in);

            System.out.print("Ingresa tu nombre: ");
            String name = scanner.nextLine();
            out.println(name);
            System.out.println("¡Conexión establecida! Escribe 'mensaje:<nombre> <mensaje>' para enviar un mensaje a un cliente específico.");

            Thread receiveThread = new Thread(() -> {
                while (true) {
                    String message = in.nextLine();
                    System.out.println(message);
                }
            });
            receiveThread.start();

            Thread sendThread = new Thread(() -> {
                while (true) {
                    String message = scanner.nextLine();

                    if (message.startsWith("mensaje:")) {
                        String[] parts = message.split(" ", 2);
                        String[] recipientParts = parts[0].split(":");
                        String recipient = recipientParts[1];
                        String content = parts[1];
                        out.println(message);
                    } else {
                        out.println(message);
                    }
                }
            });
            sendThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * 
 * Teniamos que hacer un comentario
 */
