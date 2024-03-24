import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class CalculatorClient {

    public static void main(String[] args) {
        final String SERVER_IP = "localhost";
        final int SERVER_PORT = 5555;

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             Scanner scanner = new Scanner(System.in);
             InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {

            System.out.println("Connected to the server.");

            while (true) {
                System.out.print("Enter an expression (or 'exit' to close): ");
                String expression = scanner.nextLine();

                output.write((expression + "\n").getBytes());

                if (expression.equals("exit")) {
                    break;
                }

                String result = new Scanner(input).nextLine();
                System.out.println("Result: " + result);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
