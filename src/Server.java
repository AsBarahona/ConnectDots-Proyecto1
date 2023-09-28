import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
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
    private static Set<Point> puntosSeleccionados = new HashSet<>(); //Controla los puntos continuos de las jugadas

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
                                    puntosSeleccionados.add(new Point(fila1, columna1));
                                    puntosSeleccionados.add(new Point(fila2, columna2));
                                    if (verificarCuadrado(fila1, columna1, fila2, columna2)) {
                                        // Si cierra un cuadrado, envía información para dibujar el cuadrado y asignar el número de jugador
                                        /*JsonObject cuadradoJSON = new JsonObject();
                                        cuadradoJSON.addProperty("accion", "dibujarCuadrado");
                                        cuadradoJSON.addProperty("fila1", fila1);
                                        cuadradoJSON.addProperty("columna1", columna1);
                                        cuadradoJSON.addProperty("fila2", fila2);
                                        cuadradoJSON.addProperty("columna2", columna2);
                                        cuadradoJSON.addProperty("jugador", numjugador);*/
                                        JsonObject lineaJSON = new JsonObject();
                                        lineaJSON.addProperty("accion", "dibujarLinea");
                                        lineaJSON.addProperty("fila1", fila1);
                                        lineaJSON.addProperty("columna1", columna1);
                                        lineaJSON.addProperty("fila2", fila2);
                                        lineaJSON.addProperty("columna2", columna2);
                                        broadcast(lineaJSON.toString());
                                        System.out.println("Cuadrado"); 
                                        //broadcast(cuadradoJSON.toString());
                                
                                        // También puedes agregar código para asignar el número de jugador al centro del cuadrado
                                        // y llevar un seguimiento de los cuadrados completados por cada jugador
                                    } else {
                                        // Si no cierra un cuadrado, envía la información para dibujar la línea
                                        JsonObject lineaJSON = new JsonObject();
                                        lineaJSON.addProperty("accion", "dibujarLinea");
                                        lineaJSON.addProperty("fila1", fila1);
                                        lineaJSON.addProperty("columna1", columna1);
                                        lineaJSON.addProperty("fila2", fila2);
                                        lineaJSON.addProperty("columna2", columna2);
                                
                                        broadcast(lineaJSON.toString());
                                    }
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

        private boolean verificarCuadrado(int fila1, int columna1, int fila2, int columna2) {
            // Ordena las coordenadas para asegurarse de que fila1 <= fila2 y columna1 <= columna2
            if (fila1 == fila2){ //Horizontal 
                int filaDA = fila1+1;
                int filaIA = fila2+1;
                int columnaDA = columna1;
                int columnaIA = columna2;

                Point punto1 = new Point(filaDA, columnaDA);
                Point punto2 = new Point(filaIA, columnaIA);

                int filaDAr = fila1-1;
                int filaIAr = fila2-1;
                int columnaDAr = columna1;
                int columnaIAr = columna2;

                Point punto3 = new Point(filaDAr, columnaDAr);
                Point punto4 = new Point(filaIAr, columnaIAr);

                if (puntosSeleccionados.contains(punto1) && puntosSeleccionados.contains(punto2)){
                    return true; 
                }
                else if (puntosSeleccionados.contains(punto3) && puntosSeleccionados.contains(punto4)){
                    return true; 
                }
            }
            else { //Vertical 
                int filaD1 = fila1;
                int filaD2 = fila2;
                int columnaD1= columna1+1;
                int columnaD2 = columna2+1; 

                Point punto5 = new Point(filaD1, columnaD1);
                Point punto6 = new Point(filaD2, columnaD2);

                int filaI1 = fila1;
                int filaI2 = fila2;
                int columnaI1= columna1-1;
                int columnaI2 = columna2-1;

                Point punto7 = new Point(filaI1, columnaI1);
                Point punto8 = new Point(filaI2, columnaI2);

                if (puntosSeleccionados.contains(punto5) && puntosSeleccionados.contains(punto6)){
                    return true; 
                }
                else if (puntosSeleccionados.contains(punto7) && puntosSeleccionados.contains(punto8)){
                    return true; 
                }
            }
            return false;
            
        }
        
    }
}

class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
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
