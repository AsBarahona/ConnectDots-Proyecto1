import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
//import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
//import com.google.gson.JsonSyntaxException;


public class Client extends Application {
    private String nombreUsuario;
    private Socket socket;
    private PrintWriter out;
    private TextArea EspMensajes;
    private Label jugador1Label;
    private Label jugador2Label;
    private Label jugador3Label;
    private Label jugador4Label;

    private Group roote; // Declarar roote como una variable miembro

    private static final int FILAS = 14; //Variables para la malla de puntos (lista de listas)
    private static final int COLUMNAS = 14;
    private static final double RADIO_PUNTO = 3.0;

    private int filaActual = 0;
    private int columnaActual = 0;
    private int filaSeleccionada1 = -1;
    private int columnaSeleccionada1 = -1;
    private int filaSeleccionada2 = -1;
    private int columnaSeleccionada2 = -1;
    private int Cont1 = 0;
    private int Cont2 = 0;
    private int Cont3 = 0;
    private int Cont4 = 0;

    private List<LinkedList<Circle>> puntos = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage pantPrincipalUsuario) {

        pantPrincipalUsuario.setTitle("ConnectToTheDots"); //Config de la ventana 
        pantPrincipalUsuario.setResizable(false);
        double ancho = 500; 
        double alto = 700;
        pantPrincipalUsuario.setWidth(ancho);
        pantPrincipalUsuario.setHeight(alto);

        //TextField messageField = new TextField();
        EspMensajes = new TextArea(); //Donde se ven los jugadores
        EspMensajes.setEditable(false); //Para que no se pueda editar
        double anchoM = 100;
        double altoM = 100;
        EspMensajes.setPrefWidth(anchoM);
        EspMensajes.setPrefHeight(altoM);
        EspMensajes.setMouseTransparent(true); // Hace el TextArea inaccesible para eventos del ratón
        EspMensajes.setFocusTraversable(false); // Hace que el TextArea no sea enfocable con la tecla Tab

        /*Button enviarBoton = new Button("Enviar");
        enviarBoton.setOnAction(e -> sendMessage(messageField.getText()));
        enviarBoton.setOnAction(e -> {
            sendMessage(messageField.getText());
            messageField.clear();
        });*/

        
        jugador1Label = new Label("Jugador 1: " + Cont1);
        jugador2Label = new Label("Jugador 2:" + Cont2);
        jugador3Label = new Label("Jugador 3:" + Cont3);
        jugador4Label = new Label("Jugador 4:" + Cont4);

        VBox nombresJugadoresBox = new VBox(jugador1Label, jugador2Label, jugador3Label, jugador4Label);
        nombresJugadoresBox.setAlignment(Pos.CENTER);

        roote = new Group();
        dibujarMatriz(roote);

        VBox root = new VBox(10, EspMensajes, roote, nombresJugadoresBox); //,messageField, enviarBoton
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 400, 500);

        scene.getRoot().requestFocus();

        // Capturar eventos de teclado
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                if (columnaActual < COLUMNAS - 1) {
                    //System.out.println("Mover hacia la derecha");
                    puntos.get(filaActual).get(columnaActual).setOpacity(1.0); // Restaurar opacidad del punto anterior
                    columnaActual++;
                    puntos.get(filaActual).get(columnaActual).setOpacity(0.5); // Cambiar opacidad del punto actual
                    //System.out.println("fila" + filaActual + ", columna " + columnaActual); //Para pruebas
            }
            } else if (event.getCode() == KeyCode.LEFT) {
                if (columnaActual > 0) {
                    //System.out.println("Mover hacia la izquierda");
                    puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
                    columnaActual--;
                    puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
                    //System.out.println("fila" + filaActual + ", columna " + columnaActual); //Para pruebas
            }
            } else if (event.getCode() == KeyCode.UP) {
                if (filaActual > 0) {
                    //System.out.println("Mover hacia arriba");
                    puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
                    filaActual--;
                    puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
                    //System.out.println("fila" + filaActual + ", columna " + columnaActual); //Para pruebas
                } 
            } else if (event.getCode() == KeyCode.DOWN) {
                if (filaActual < FILAS - 1) {
                    //System.out.println("Mover hacia abajo");   
                    puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
                    filaActual++;
                    puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
                    //System.out.println("fila" + filaActual + ", columna " + columnaActual); //Para pruebas
                }
            } else if (event.getCode() == KeyCode.ENTER) {
                if(filaSeleccionada1== -1 & columnaSeleccionada1== -1 ){
                    filaSeleccionada1 = filaActual;
                    columnaSeleccionada1 = columnaActual;
                }else if (filaSeleccionada2== -1 & columnaSeleccionada2== -1) {
                    filaSeleccionada2 = filaActual;
                    columnaSeleccionada2 = columnaActual;
                    mostrarPuntoActual();
                }
            }
            
        });

        pantPrincipalUsuario.setScene(scene);
        pantPrincipalUsuario.show();

        nombreUsuario = getUsername();
        ConexionServer();

        Thread readerThread = new Thread(this::readMessages);
        readerThread.start();
    }

    private String getUsername() { //Método para la pantalla para ingresar el username
        TextInputDialog textoNombre = new TextInputDialog();
        textoNombre.setHeaderText("Ingrese su nombre de usuario:");
        textoNombre.setTitle("Perfil de usuario");
        return textoNombre.showAndWait().orElse("Anonymous");
    }

    private void ConexionServer() { //Conexión con el server 
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(nombreUsuario);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void sendMessage(String message) { //Enviar mensajes 
        out.println(message);
    }*/

    private void readMessages() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            while (in.hasNextLine()) {
                String message = in.nextLine();
                Platform.runLater(() -> {
                    if (message.startsWith("{\"accion\":\"dibujarLinea\"")) {
                        JsonObject lineaJSON = JsonParser.parseString(message).getAsJsonObject();
                        int fila1 = lineaJSON.get("fila1").getAsInt();
                        int columna1 = lineaJSON.get("columna1").getAsInt();
                        int fila2 = lineaJSON.get("fila2").getAsInt();
                        int columna2 = lineaJSON.get("columna2").getAsInt();
    
                        // Llama al método para dibujar la línea entre los puntos (fila1, columna1) y (fila2, columna2)
                        dibujarLinea(fila1, columna1, fila2, columna2);
    
                        System.out.println("Recibido mensaje 'dibujarLinea': (" + fila1 + "," + columna1 + ") - (" + fila2 + "," + columna2 + ")");
                    
                    } else if (message.startsWith("{\"accion\":\"dibujarLetra\"")) {
                        JsonObject lineaJSON = JsonParser.parseString(message).getAsJsonObject();
                        int fila1L = lineaJSON.get("fila1").getAsInt();
                        int columna1L = lineaJSON.get("columna1").getAsInt();
                        int fila2L = lineaJSON.get("fila2").getAsInt();
                        int columna2L = lineaJSON.get("columna2").getAsInt();
                        int fila3L = lineaJSON.get("fila3").getAsInt();
                        int columna3L = lineaJSON.get("columna3").getAsInt();
                        int fila4L = lineaJSON.get("fila4").getAsInt();
                        int columna4L = lineaJSON.get("columna4").getAsInt();
                        String turnoPunto = lineaJSON.get("letra").getAsString();

                         System.out.println("letra" + fila1L);
                        escribirLetraEnCentro(fila1L, columna1L, fila2L, columna2L, fila3L, columna3L, fila4L, columna4L, turnoPunto);
                        actualizarPuntos(turnoPunto);
        
                    } else {
                        EspMensajes.appendText(message + "\n");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    private void dibujarMatriz(Group roote) {
        double espacioX = 400.0 / COLUMNAS;
        double espacioY = 400.0 / FILAS;
    
        for (int i = 0; i < FILAS; i++) {
            LinkedList<Circle> fila = new LinkedList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                double x = j * espacioX + 50;
                double y = i * espacioY + 50;
                Circle punto = new Circle(x, y, RADIO_PUNTO);
                fila.add(punto);
                roote.getChildren().add(punto);
            }
            puntos.add(fila);
        }
    }

    private void mostrarPuntoActual() { 
        if (filaSeleccionada1 == filaSeleccionada2 && columnaSeleccionada1 == columnaSeleccionada2) {
            filaSeleccionada2 = -1;
            columnaSeleccionada2 = -1;
        } else if (filaSeleccionada1 == filaSeleccionada2 || columnaSeleccionada1 == columnaSeleccionada2) {
            System.out.println("(" + filaSeleccionada1 + "," + columnaSeleccionada1 +"),(" + filaSeleccionada2 + "," + columnaSeleccionada2 +")");


        // Crear un objeto JSON que contenga las coordenadas seleccionadas
        JsonObject coordenadasJSON = new JsonObject();
        coordenadasJSON.addProperty("fila1", filaSeleccionada1);
        coordenadasJSON.addProperty("columna1", columnaSeleccionada1);
        coordenadasJSON.addProperty("fila2", filaSeleccionada2);
        coordenadasJSON.addProperty("columna2", columnaSeleccionada2);

        // Convertir el objeto JSON a una cadena
        String coordenadasStr = coordenadasJSON.toString();

        // Enviar las coordenadas al servidor
        out.println(coordenadasStr);
            // Reiniciar las coordenadas de selección
            filaSeleccionada1 = -1;
            columnaSeleccionada1 = -1;
            filaSeleccionada2 = -1;
            columnaSeleccionada2 = -1;
        }
    }

    private void dibujarLinea(int fila1, int columna1, int fila2, int columna2) {
    // Calcula las coordenadas de los puntos en la matriz
    double x1 = columna1 * (400.0 / COLUMNAS) + 50;
    double y1 = fila1 * (400.0 / FILAS) + 50;
    double x2 = columna2 * (400.0 / COLUMNAS) + 50;
    double y2 = fila2 * (400.0 / FILAS) + 50;

    // Crea una línea que conecta los dos puntos
    Line linea = new Line(x1, y1, x2, y2);

    // Puedes personalizar el estilo de la línea aquí, por ejemplo:
    linea.setStroke(Color.RED); // Cambia el color de la línea

    // Agrega la línea al Group roote
    roote.getChildren().add(linea);
    }

    private void escribirLetraEnCentro(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, String letra) {
        // Calcula el punto central del cuadrado
        double centerX = ((x1 + x2 + x3 + x4) / 4.0) + 55 + (29*(y1)) ;
        double centerY = ((y1 + y2 + y3 + y4) / 4.0) + 55 + (29*(x1));
    
        // Crea un objeto Text (letra) y establece su posición y tamaño
        Text texto = new Text(centerX, centerY, letra);
        texto.setFont(Font.font("Arial", 12)); // Puedes ajustar el tamaño y la fuente según tus necesidades
    
        // Agrega el texto al grupo root para que se muestre en la escena
        roote.getChildren().add(texto);
    }

    private void actualizarPuntos(String jugador) {
        if ("1".equals(jugador)) {
            Cont1++;
            jugador1Label.setText("Jugador 1: " + Cont1);
        } else if ("2".equals(jugador)) {
            Cont2++;
            jugador2Label.setText("Jugador 2: " + Cont2);
        } else if ("3".equals(jugador)) {
            Cont3++;
            jugador3Label.setText("Jugador 3: " + Cont3);
        } else if ("4".equals(jugador)) {
            Cont4++;
            jugador4Label.setText("Jugador 4: " + Cont4);
        }
    }
    

}
