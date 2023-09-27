import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject; // En el servidor
import com.google.gson.JsonParser;

public class Server {
    private static Queue<String> usuariosEnCola = new LinkedList<>(); //Creada para "rastrear" el nombre de usuario
    private static Queue<PrintWriter> clientesQueue = new LinkedList<>(); //Creada para "rastrear" las conexiones de cada cliente
    private static int clientCount = 0; // Contador de clientes conectados
    private static final int MAX_CLIENTS = 4; // Número máximo de clientes permitidos
    private static int numjugador = 1; // Contador para asignar el número de jugador de cada cliente 
    private static int turnoActual = 0;

    public static void main(String[] args) {
        ServerSocket serverSocket = null; // Declarar aquí para poder cerrarlo en el finally
        try {
            serverSocket = new ServerSocket(12345); // Port
            ExecutorService pool = Executors.newFixedThreadPool(10);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                if (clientCount < MAX_CLIENTS) { // Permitir conexiones si hay menos de 4 clientes
                    pool.execute(new ClientHandler(clientSocket));
                } else {
                    System.out.println("Número máximo de clientes alcanzado. Nueva conexión rechazada.");
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    out.println("Número máximo de clientes alcanzado. Intente más tarde.");
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally { //Cierra el socket cuando se termina la conexión 
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String nombreUsuario;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                try (Scanner in = new Scanner(socket.getInputStream())) {
                    out = new PrintWriter(socket.getOutputStream(), true);

                    if (clientCount < MAX_CLIENTS) {
                        nombreUsuario = in.nextLine();
                        clientesQueue.add(out);
                        usuariosEnCola.add(nombreUsuario);

                        broadcast("  Jugador " + numjugador +": "+ nombreUsuario);
                        numjugador++;

                        synchronized (Server.class) {  // Incrementa el contador de clientes
                            clientCount++;
                        }

                        printUserQueue(); // Imprimir la lista de usuarios en la cola

                        /*while (true) { //Envia como broadcast los usuarios de cada cliente a los demás
                            String message = in.nextLine();
                            if (((LinkedList<PrintWriter>) clientesQueue).get(turnoActual) == out) {
                                broadcast(nombreUsuario + ": " + message);
                                turnoActual = (turnoActual + 1) % clientesQueue.size();
                            }
                        }*/

                        while (true) {
                            String message = in.nextLine();
                            if (((LinkedList<PrintWriter>) clientesQueue).get(turnoActual) == out) {
                                JsonObject coordenadasJSON = JsonParser.parseString(message).getAsJsonObject();
                                int fila1 = coordenadasJSON.get("fila1").getAsInt();
                                int columna1 = coordenadasJSON.get("columna1").getAsInt();
                                int fila2 = coordenadasJSON.get("fila2").getAsInt();
                                int columna2 = coordenadasJSON.get("columna2").getAsInt();
                                
                                if (sonPuntosContinuos(fila1, columna1, fila2, columna2)) {
                                    // Si los puntos son continuos, envía la información para dibujar la línea
                                    JsonObject lineaJSON = new JsonObject();
                                    lineaJSON.addProperty("accion", "dibujarLinea");
                                    lineaJSON.addProperty("fila1", fila1);
                                    lineaJSON.addProperty("columna1", columna1);
                                    lineaJSON.addProperty("fila2", fila2);
                                    lineaJSON.addProperty("columna2", columna2);
                                    
                                    broadcast(lineaJSON.toString());
                                }
                                turnoActual = (turnoActual + 1) % clientesQueue.size();
                            }
                        }

                    } else { //Mensaje cuando ya se alcanzó el máximo de clientes
                        out.println("Número máximo de clientes alcanzado. Intente más tarde.");
                        socket.close(); 
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (nombreUsuario != null) {
                    clientesQueue.remove(out);
                    usuariosEnCola.remove(nombreUsuario);
                    broadcast(nombreUsuario + " abandonó el juego.");

                    synchronized (Server.class) { // Decrementa el contador de clientes
                        clientCount--;
                    }

                    printUserQueue(); // Imprime la lista de usuarios en la cola
                }
            }
        }

        private void broadcast(String message) { //método encargado de reenviar los mensajes a los cliente en la cola
            for (PrintWriter client : clientesQueue) {
                client.println(message);
            }
        }

        private void printUserQueue() { // Método para imprimir la lista de usuarios en la cola
            System.out.println("Lista de usuarios en cola:");
            for (String usuario : usuariosEnCola) {
                System.out.println(usuario);
            }
        }

        private boolean sonPuntosContinuos(int fila1, int columna1, int fila2, int columna2) { // Verifica si los puntos son adyacentes en horizontal o vertical
            if (Math.abs(fila1 - fila2) <= 1 && Math.abs(columna1 - columna2) <= 1) {
                return true;
            }
            return false;
        }
        
    }
}












/*import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static Queue<String> usuariosEnCola = new LinkedList<>();
    private static Queue<PrintWriter> clientesQueue = new LinkedList<>();

    public static void main(String[] args) {
        ServerSocket serverSocket = null; // Declarar aquí para poder cerrarlo en el finally
        try {
            serverSocket = new ServerSocket(12345); // Port
            ExecutorService pool = Executors.newFixedThreadPool(10);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String nombreUsuario;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                try (Scanner in = new Scanner(socket.getInputStream())) {
                    out = new PrintWriter(socket.getOutputStream(), true);

                    nombreUsuario = in.nextLine();
                    clientesQueue.add(out);
                    usuariosEnCola.add(nombreUsuario);

                    broadcast(nombreUsuario + " se ha unido al juego.");

                    // Imprimir la lista de usuarios en la cola
                    printUserQueue();

                    while (true) {
                        String message = in.nextLine();
                        broadcast(nombreUsuario + ": " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (nombreUsuario != null) {
                    clientesQueue.remove(out);
                    usuariosEnCola.remove(nombreUsuario);
                    broadcast(nombreUsuario + " abandonó el juego.");

                    // Imprimir la lista de usuarios en la cola
                    printUserQueue();
                }
            }
        }

        private void broadcast(String message) {
            for (PrintWriter client : clientesQueue) {
                client.println(message);
            }
        }

        // Método para imprimir la lista de usuarios en la cola
        private void printUserQueue() {
            System.out.println("Lista de usuarios en cola:");
            for (String usuario : usuariosEnCola) {
                System.out.println(usuario);
            }
        }
    }
}*/
