/*import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class gridpadelineas extends Application {

    private static final int FILAS = 5;
    private static final int COLUMNAS = 5;
    private static final double RADIO_PUNTO = 5.0;

    private int filaActual = 0;
    private int columnaActual = 0;

    private List<List<Circle>> puntos = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        Group root = new Group();
        Scene scene = new Scene(root, 500, 500);

        double espacioX = 400.0 / COLUMNAS;
        double espacioY = 400.0 / FILAS;

        for (int i = 0; i < FILAS; i++) {
            List<Circle> fila = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                double x = j * espacioX + 50;
                double y = i * espacioY + 50;
                Circle punto = new Circle(x, y, RADIO_PUNTO);
                fila.add(punto);
                root.getChildren().add(punto);
            }
            puntos.add(fila);
        }

        // Configurar la escena
        primaryStage.setTitle("Navegación de Puntos");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Capturar eventos de teclado
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                moverDerecha();
            } else if (event.getCode() == KeyCode.LEFT) {
                moverIzquierda();
            } else if (event.getCode() == KeyCode.UP) {
                moverArriba();
            } else if (event.getCode() == KeyCode.DOWN) {
                moverAbajo();
            } else if (event.getCode() == KeyCode.ENTER) {
                mostrarPuntoActual();
            }
        });
    }

    private void moverDerecha() {
        if (columnaActual < COLUMNAS - 1) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0); // Restaurar opacidad del punto anterior
            columnaActual++;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5); // Cambiar opacidad del punto actual
        }
    }

    private void moverIzquierda() {
        if (columnaActual > 0) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
            columnaActual--;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
        }
    }

    private void moverArriba() {
        if (filaActual > 0) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
            filaActual--;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
        }
    }

    private void moverAbajo() {
        if (filaActual < FILAS - 1) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
            filaActual++;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
        }
    }

    private void mostrarPuntoActual() {
        System.out.println("Punto seleccionado: Fila " + filaActual + ", Columna " + columnaActual);
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class gridpadelineas extends Application {

    private static final int FILAS = 5;
    private static final int COLUMNAS = 5;
    private static final double RADIO_PUNTO = 5.0;

    private int filaActual = 0;
    private int columnaActual = 0;

    private List<List<Circle>> puntos = new ArrayList<>();
    private Group root;

    @Override
    public void start(Stage primaryStage) {
        root = new Group();
        Scene scene = new Scene(root, 500, 500);

        double espacioX = 400.0 / COLUMNAS;
        double espacioY = 400.0 / FILAS;

        for (int i = 0; i < FILAS; i++) {
            List<Circle> fila = new ArrayList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                double x = j * espacioX + 50;
                double y = i * espacioY + 50;
                Circle punto = new Circle(x, y, RADIO_PUNTO);
                fila.add(punto);
                root.getChildren().add(punto);
            }
            puntos.add(fila);
        }

        // Configurar la escena
        primaryStage.setTitle("Navegación con Cuadrícula");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Capturar eventos de teclado
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                moverDerecha();
            } else if (event.getCode() == KeyCode.LEFT) {
                moverIzquierda();
            } else if (event.getCode() == KeyCode.UP) {
                moverArriba();
            } else if (event.getCode() == KeyCode.DOWN) {
                moverAbajo();
            }else if (event.getCode() == KeyCode.ENTER) {
                mostrarPuntoActual();
            }
        });

        // Dibujar líneas para formar la cuadrícula
        dibujarCuadricula();
    }

    private void moverDerecha() {
     if (columnaActual < COLUMNAS - 1) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0); // Restaurar opacidad del punto anterior
            columnaActual++;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5); // Cambiar opacidad del punto actual
        }
    }
  // ... (código de movimiento hacia la derecha)
    

    private void moverIzquierda() {
        if (columnaActual > 0) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
            columnaActual--;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
        }
    }
        // ... (código de movimiento hacia la izquierda)
    

    private void moverArriba() {
        if (filaActual > 0) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
            filaActual--;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
        }
    }


    private void moverAbajo() {
        // ... (código de movimiento hacia abajo)
        if (filaActual < FILAS - 1) {
            puntos.get(filaActual).get(columnaActual).setOpacity(1.0);
            filaActual++;
            puntos.get(filaActual).get(columnaActual).setOpacity(0.5);
        }
    }

    private void mostrarPuntoActual() {
    System.out.println("Punto seleccionado: Fila " + filaActual + ", Columna " + columnaActual);
    }

    private void dibujarCuadricula() {
        for (int i = 0; i < FILAS; i++) {
            for (int j = 0; j < COLUMNAS; j++) {
                // Dibujar líneas horizontales
                if (j < COLUMNAS - 1) {
                    Line lineaHorizontal = new Line(
                            puntos.get(i).get(j).getCenterX(),
                            puntos.get(i).get(j).getCenterY(),
                            puntos.get(i).get(j + 1).getCenterX(),
                            puntos.get(i).get(j + 1).getCenterY()
                    );
                    root.getChildren().add(lineaHorizontal);
                }

                // Dibujar líneas verticales
                if (i < FILAS - 1) {
                    Line lineaVertical = new Line(
                            puntos.get(i).get(j).getCenterX(),
                            puntos.get(i).get(j).getCenterY(),
                            puntos.get(i + 1).get(j).getCenterX(),
                            puntos.get(i + 1).get(j).getCenterY()
                    );
                    root.getChildren().add(lineaVertical);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


/*import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class gridpadelineas extends Application {

    private static final int FILAS = 5;
    private static final int COLUMNAS = 5;
    private static final double RADIO_PUNTO = 5.0;

    //private int filaActual = 0;
    //private int columnaActual = 0;

    private List<LinkedList<Circle>> puntos = new ArrayList<>();
    private Group root;

    @Override
    public void start(Stage primaryStage) {
        root = new Group();
        Scene scene = new Scene(root, 500, 500);

        double espacioX = 400.0 / COLUMNAS;
        double espacioY = 400.0 / FILAS;

        for (int i = 0; i < FILAS; i++) {
            LinkedList<Circle> fila = new LinkedList<>();
            for (int j = 0; j < COLUMNAS; j++) {
                double x = j * espacioX + 50;
                double y = i * espacioY + 50;
                Circle punto = new Circle(x, y, RADIO_PUNTO);
                fila.add(punto);
                root.getChildren().add(punto);
            }
            puntos.add(fila);
        }

        // Configurar la escena
        primaryStage.setTitle("Navegación con Cuadrícula");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Capturar eventos de teclado
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                moverDerecha();
            } else if (event.getCode() == KeyCode.LEFT) {
                moverIzquierda();
            } else if (event.getCode() == KeyCode.UP) {
                moverArriba();
            } else if (event.getCode() == KeyCode.DOWN) {
                moverAbajo();
            }
        });

        // Dibujar líneas para formar la cuadrícula
        dibujarCuadricula();
    }

    private void moverDerecha() {
        // ... (código de movimiento hacia la derecha)
    }

    private void moverIzquierda() {
        // ... (código de movimiento hacia la izquierda)
    }

    private void moverArriba() {
        // ... (código de movimiento hacia arriba)
    }

    private void moverAbajo() {
        // ... (código de movimiento hacia abajo)
    }

    private void dibujarCuadricula() {
        for (int i = 0; i < FILAS; i++) {
            LinkedList<Circle> fila = puntos.get(i);
            for (int j = 0; j < COLUMNAS; j++) {
                Circle punto = fila.get(j);

                // Dibujar líneas horizontales
                if (j < COLUMNAS - 1) {
                    Circle siguientePunto = fila.get(j + 1);
                    root.getChildren().add(new Line(
                            punto.getCenterX(),
                            punto.getCenterY(),
                            siguientePunto.getCenterX(),
                            siguientePunto.getCenterY()
                    ));
                }

                // Dibujar líneas verticales
                if (i < FILAS - 1) {
                    Circle puntoAbajo = puntos.get(i + 1).get(j);
                    root.getChildren().add(new Line(
                            punto.getCenterX(),
                            punto.getCenterY(),
                            puntoAbajo.getCenterX(),
                            puntoAbajo.getCenterY()
                    ));
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

*/