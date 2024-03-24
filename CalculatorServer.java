import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class CalculatorServer {

    public static void main(String[] args) {
        final int PORT = 5555;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running and waiting for connections...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (InputStream input = clientSocket.getInputStream();
                 OutputStream output = clientSocket.getOutputStream()) {

                Scanner scanner = new Scanner(input);
                String clientMessage;

                do {
                    clientMessage = scanner.nextLine();
                    if (!clientMessage.equals("exit")) {
                        try {
                            double result = evaluateExpression(clientMessage);
                            output.write((result + "\n").getBytes());
                        } catch (ArithmeticException ex) {
                            output.write(("Error: " + ex.getMessage() + "\n").getBytes());
                        }
                    }
                } while (!clientMessage.equals("exit"));

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private double evaluateExpression(String expression) {

            String[] tokens = expression.split(" ");
            double operand1 = Double.parseDouble(tokens[0]);
            double operand2 = Double.parseDouble(tokens[2]);

            switch (tokens[1]) {
                case "+":
                    return operand1 + operand2;
                case "-":
                    return operand1 - operand2;
                case "*":
                    return operand1 * operand2;
                case "^":
                    return Math.pow(operand1,operand2);
                case "/":
                    if (operand2 == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return operand1 / operand2;
                default:
                    throw new ArithmeticException("Invalid operator");
            }
        }
    }
}
