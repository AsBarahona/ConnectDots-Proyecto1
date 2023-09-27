import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Pruebamaya extends Application {

    private static final int ANCHO_VENTANA = 400;
    private static final int ALTO_VENTANA = 400;
    private static final int NUMERO_DE_FILAS = 15;
    private static final int NUMERO_DE_COLUMNAS = 15;
    private int puntosSeleccionados = 0;
    private String primerPunto = "";
    private String segundoPunto = "";

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Matriz de Puntos con Letras");

        Group root = new Group();
        Scene scene = new Scene(root, ANCHO_VENTANA, ALTO_VENTANA);

        dibujarMatrizDePuntosConLetras(root);

        scene.setOnMouseClicked(this::manejarClicDeRaton);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void dibujarMatrizDePuntosConLetras(Group root) {
        double espacioHorizontal = ANCHO_VENTANA / (double) (NUMERO_DE_COLUMNAS + 1);
        double espacioVertical = ALTO_VENTANA / (double) (NUMERO_DE_FILAS + 1);

        for (int fila = 1; fila <= NUMERO_DE_FILAS; fila++) {
            for (int columna = 1; columna <= NUMERO_DE_COLUMNAS; columna++) {
                double x = columna * espacioHorizontal;
                double y = fila * espacioVertical;

                Circle punto = new Circle(x, y, 3, Color.BLUE);
                root.getChildren().add(punto);
            }
        }
    }

    private void manejarClicDeRaton(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        int fila = (int) (y / (ALTO_VENTANA / (double) (NUMERO_DE_FILAS + 1)));
        int columna = (int) (x / (ANCHO_VENTANA / (double) (NUMERO_DE_COLUMNAS + 1)));

        String puntoActual = fila + "," + columna;

        if (puntosSeleccionados == 0) {
            primerPunto = puntoActual;
            puntosSeleccionados = 1;
        } else if (puntosSeleccionados == 1 && !primerPunto.equals(puntoActual)) {
            segundoPunto = puntoActual;
            puntosSeleccionados = 2;
            System.out.println("Puntos seleccionados: " + primerPunto + " y " + segundoPunto);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}



















































/* 
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
//import javafx.scene.shape.Line;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Pruebamaya extends Application {

    private static final int ANCHO_VENTANA = 400;
    private static final int ALTO_VENTANA = 400;
    private static final int NUMERO_DE_FILAS = 15;
    private static final int NUMERO_DE_COLUMNAS = 15;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Matriz de Puntos con Letras");

        Group root = new Group();
        Scene scene = new Scene(root, ANCHO_VENTANA, ALTO_VENTANA);

        dibujarMatrizDePuntosConLetras(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void dibujarMatrizDePuntosConLetras(Group root) {
        double espacioHorizontal = ANCHO_VENTANA / (double) (NUMERO_DE_COLUMNAS + 1);
        double espacioVertical = ALTO_VENTANA / (double) (NUMERO_DE_FILAS + 1);

        for (int fila = 1; fila <= NUMERO_DE_FILAS; fila++) {
            for (int columna = 1; columna <= NUMERO_DE_COLUMNAS; columna++) {
                double x = columna * espacioHorizontal;
                double y = fila * espacioVertical;

                Circle punto = new Circle(x, y, 3, Color.BLUE);
                root.getChildren().add(punto); */

                // Conectar con el punto anterior (si no es la primera columna)
                /*if (columna > 1) {
                    double xAnterior = (columna - 1) * espacioHorizontal;
                    double yAnterior = fila * espacioVertical;
                    Line linea = new Line(x, y, xAnterior, yAnterior);
                    root.getChildren().add(linea);
                }

                // Conectar con el punto superior (si no es la primera fila)
                if (fila > 1) {
                    double xSuperior = columna * espacioHorizontal;
                    double ySuperior = (fila - 1) * espacioVertical;
                    Line linea = new Line(x, y, xSuperior, ySuperior);
                    root.getChildren().add(linea);
                }

                // Agregar letra en el cuadro
                String letra = String.format("%c", 'A' + columna - 2);
                Text texto = new Text(x - 7.5, y + 7.5, letra);
                texto.setFont(Font.font(12));
                root.getChildren().add(texto);*/ /* 
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} */







































/*import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class Pruebamaya extends Application {

    private static final int ANCHO_VENTANA = 400;
    private static final int ALTO_VENTANA = 400;
    private static final int NUMERO_DE_FILAS = 15;
    private static final int NUMERO_DE_COLUMNAS = 15;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Matriz de Puntos");

        Group root = new Group();
        Scene scene = new Scene(root, ANCHO_VENTANA, ALTO_VENTANA);

        dibujarMatrizDePuntos(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void dibujarMatrizDePuntos(Group root) {
        double espacioHorizontal = ANCHO_VENTANA / (double) (NUMERO_DE_COLUMNAS + 1);
        double espacioVertical = ALTO_VENTANA / (double) (NUMERO_DE_FILAS + 1);

        for (int fila = 1; fila <= NUMERO_DE_FILAS; fila++) {
            for (int columna = 1; columna <= NUMERO_DE_COLUMNAS; columna++) {
                double x = columna * espacioHorizontal;
                double y = fila * espacioVertical;

                Circle punto = new Circle(x, y, 3, Color.BLUE);
                root.getChildren().add(punto);

                // Conectar con el punto anterior (si no es la primera columna)
                if (columna > 1) {
                    double xAnterior = (columna - 1) * espacioHorizontal;
                    double yAnterior = fila * espacioVertical;
                    Line linea = new Line(x, y, xAnterior, yAnterior);
                    root.getChildren().add(linea);
                }

                // Conectar con el punto superior (si no es la primera fila)
                if (fila > 1) {
                    double xSuperior = columna * espacioHorizontal;
                    double ySuperior = (fila - 1) * espacioVertical;
                    Line linea = new Line(x, y, xSuperior, ySuperior);
                    root.getChildren().add(linea);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/
