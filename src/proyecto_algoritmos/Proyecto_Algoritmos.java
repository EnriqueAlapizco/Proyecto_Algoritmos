package proyecto_algoritmos;

import javafx.application.Application;
import javafx.stage.Stage;

public class Proyecto_Algoritmos extends Application {
    @Override
    public void start(Stage ventana) {
        EditorGrafo editor = new EditorGrafo();
        new VentanaGrafo(ventana, editor).mostrar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
