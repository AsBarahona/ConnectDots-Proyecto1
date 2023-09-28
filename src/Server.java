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
    private static Set<Edge> conexiones = new HashSet<>();

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

                        while (true) {
                            String message = in.nextLine();
                            if (((LinkedList<PrintWriter>) clientesQueue).get(turnoActual) == out) {
                                System.out.println(turnoActual);
                                JsonObject coordenadasJSON = JsonParser.parseString(message).getAsJsonObject();
                                int fila1 = coordenadasJSON.get("fila1").getAsInt();
                                int columna1 = coordenadasJSON.get("columna1").getAsInt();
                                int fila2 = coordenadasJSON.get("fila2").getAsInt();
                                int columna2 = coordenadasJSON.get("columna2").getAsInt();

                                if (sonPuntosContinuos(fila1, columna1, fila2, columna2)) {
                                    if(fila1==fila2){
                                        int primY, secY;

                                        if (columna1 < columna2) {
                                            secY=columna2;
                                            primY = columna1;
                                        } else {
                                            secY=columna1;
                                            primY = columna2;
                                        }
                                        conexiones.add(new Edge(new Point(fila1, primY), new Point(fila1, secY)));
                                        if (verificarCuadrado(fila1, primY, fila1, secY)!="Nocuadros") {
                                        JsonObject lineaJSON = new JsonObject();
                                        lineaJSON.addProperty("accion", "dibujarLinea");
                                        lineaJSON.addProperty("fila1", fila1);
                                        lineaJSON.addProperty("columna1", columna1);
                                        lineaJSON.addProperty("fila2", fila2);
                                        lineaJSON.addProperty("columna2", columna2);
                                        broadcast(lineaJSON.toString());
                                        //System.out.println("Cuadradooo"); 
                                      
                                        broadcast(verificarCuadrado(fila1, primY, fila1, secY));

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
                                    else{
                                          int primX, secX;

                                        if (fila1 < fila2) {
                                            secX=fila2;
                                            primX = fila1;
                                        } else {
                                            secX=fila1;
                                            primX = fila2;
                                        }
                                        conexiones.add(new Edge(new Point(primX, columna1), new Point(secX, columna1)));
                                        if (verificarCuadrado(primX, columna1, secX,columna2)!="Nocuadros"){
                                            JsonObject lineaJSON = new JsonObject();
                                        lineaJSON.addProperty("accion", "dibujarLinea");
                                        lineaJSON.addProperty("fila1", fila1);
                                        lineaJSON.addProperty("columna1", columna1);
                                        lineaJSON.addProperty("fila2", fila2);
                                        lineaJSON.addProperty("columna2", columna2);
                                        broadcast(lineaJSON.toString());
                                        broadcast(verificarCuadrado(primX, columna1, secX, columna1));
                                        System.out.println("Cuadrado"); 
                                        //broadcast(cuadradoJSON.toString());
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


        private String verificarCuadrado(int fila, int columna, int fila2, int columna2) {
            // Verificar si se forma un cuadrado alrededor del punto dado (fila, columna)

            Point puntoArriba = new Point(fila - 1, columna);
            //Point puntoArribaIzquierda = new Point(fila - 1, columna - 1);
            Point puntoArribaDerecha = new Point(fila - 1, columna + 1);
            Point puntoAbajo = new Point(fila + 1, columna);
            Point puntoAbajoIzquierda = new Point(fila + 1, columna - 1);
            Point puntoAbajoDerecha = new Point(fila + 1, columna + 1);
            Point puntoIzquierda = new Point(fila, columna - 1);


            Point puntoDerecha = new Point(fila, columna + 1);

            if(fila==fila2){
                if(conexiones.contains(new Edge((new Point(fila,columna)),(new Point(fila2,columna2)))) &&
                conexiones.contains(new Edge(puntoAbajo, puntoAbajoDerecha)) &&
                conexiones.contains(new Edge(new Point(fila2,columna2), puntoAbajoDerecha)) &&
                conexiones.contains(new Edge((new Point(fila,columna)), puntoAbajo))){
                    JsonObject letraJSON = new JsonObject();
                    letraJSON.addProperty("accion", "dibujarLetra");
                    letraJSON.addProperty("fila1", fila);
                    letraJSON.addProperty("columna1", columna);
                    letraJSON.addProperty("fila2", fila);
                    letraJSON.addProperty("columna2", columna+1);
                    letraJSON.addProperty("fila3", fila2);
                    letraJSON.addProperty("columna3", columna2);
                    letraJSON.addProperty("fila4", fila2);
                    letraJSON.addProperty("columna4", columna2+1);
                    letraJSON.addProperty("letra", turnoActual+1);
                    return (letraJSON.toString()); 
                } else if(conexiones.contains(new Edge((new Point(fila,columna)),(new Point(fila2,columna2)))) &&
                conexiones.contains(new Edge(puntoArriba, puntoArribaDerecha)) &&
                conexiones.contains(new Edge(puntoArriba,(new Point(fila,columna)))) &&
                conexiones.contains(new Edge(puntoArribaDerecha, (new Point(fila2,columna2))))){
                    JsonObject letraJSON = new JsonObject();
                    letraJSON.addProperty("accion", "dibujarLetra");
                    letraJSON.addProperty("fila1", fila-1);
                    letraJSON.addProperty("columna1", columna);
                    letraJSON.addProperty("fila2", fila2-1);
                    letraJSON.addProperty("columna2", columna2);
                    letraJSON.addProperty("fila3", fila);
                    letraJSON.addProperty("columna3", columna);
                    letraJSON.addProperty("fila4", fila2);
                    letraJSON.addProperty("columna4", columna2);
                    letraJSON.addProperty("letra", turnoActual+1);
                    return (letraJSON.toString()); 
                } else{
                    return "Nocuadros";
                }
            }else if (columna==columna2){
                if (conexiones.contains(new Edge((new Point(fila,columna)),(new Point(fila2,columna2)))) &&
                conexiones.contains(new Edge(puntoIzquierda, puntoAbajoIzquierda)) &&
                conexiones.contains(new Edge(puntoIzquierda, (new Point(fila,columna)))) &&
                conexiones.contains(new Edge(puntoAbajoIzquierda, (new Point(fila2,columna2))))) {
                    JsonObject letraJSON = new JsonObject();
                    letraJSON.addProperty("accion", "dibujarLetra");
                    letraJSON.addProperty("fila1", fila);
                    letraJSON.addProperty("columna1", columna-1);
                    letraJSON.addProperty("fila2", fila);
                    letraJSON.addProperty("columna2", columna);
                    letraJSON.addProperty("fila3", fila2);
                    letraJSON.addProperty("columna3", columna2-1);
                    letraJSON.addProperty("fila4", fila2);
                    letraJSON.addProperty("columna4", columna2+1);
                    letraJSON.addProperty("letra", turnoActual+1);
                    return (letraJSON.toString()); 
                } else if(conexiones.contains(new Edge((new Point(fila,columna)),(new Point(fila2,columna2)))) &&
                conexiones.contains(new Edge(puntoDerecha, puntoAbajoDerecha)) &&
                conexiones.contains(new Edge((new Point(fila,columna)), puntoDerecha)) &&
                conexiones.contains(new Edge((new Point(fila2,columna2)), puntoAbajoDerecha))){
                    JsonObject letraJSON = new JsonObject();
                    letraJSON.addProperty("accion", "dibujarLetra");
                    letraJSON.addProperty("fila1", fila);
                    letraJSON.addProperty("columna1", columna);
                    letraJSON.addProperty("fila2", fila);
                    letraJSON.addProperty("columna2", columna+1);
                    letraJSON.addProperty("fila3", fila2);
                    letraJSON.addProperty("columna3", columna2);
                    letraJSON.addProperty("fila4", fila2);
                    letraJSON.addProperty("columna4", columna2-1);
                    letraJSON.addProperty("letra", turnoActual+1);
                    return (letraJSON.toString()); 
                } else{
                    return "Nocuadros";
                
                }
            } else{
                return "Nocuadros"; 
            }
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

class Edge {
    Point punto1;
    Point punto2;

    Edge(Point punto1, Point punto2) {
        this.punto1 = punto1;
        this.punto2 = punto2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (punto1.equals(edge.punto1) && punto2.equals(edge.punto2)) ||
               (punto1.equals(edge.punto2) && punto2.equals(edge.punto1));
    }

    @Override
    public int hashCode() {
        return Objects.hash(punto1, punto2);
    }
}
