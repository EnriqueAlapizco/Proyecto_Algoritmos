package proyecto_algoritmos;

import java.io.File;
import java.io.IOException;
import java.util.StringJoiner;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Proyecto_Algoritmos extends Application {
    @Override
    public void start(Stage ventana) {
        Button importar = new Button("Importar CSV");
        CheckBox dirigido = new CheckBox("Importar como grafo dirigido");
        Label estado = new Label("Selecciona un CSV con las columnas origen,destino,peso.");
        estado.setWrapText(true);
        TextArea conexiones = new TextArea();
        conexiones.setEditable(false);
        conexiones.setWrapText(true);

        FileChooser selector = new FileChooser();
        selector.setTitle("Importar conexiones");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        importar.setOnAction(evento -> {
            File archivo = selector.showOpenDialog(ventana);
            if (archivo == null) {
                return;
            }
            try {
                Grafo grafo = ImportadorCsv.importar(archivo.toPath(), dirigido.isSelected());
                conexiones.setText(formatearConexiones(grafo));
                estado.setText("%s | %d vértices | %d aristas | %s".formatted(
                        archivo.getName(), grafo.vertices().size(), grafo.aristas().size(),
                        dirigido.isSelected() ? "Grafo dirigido" : "Grafo no dirigido"));
            } catch (IOException error) {
                conexiones.clear();
                estado.setText("Error al importar «" + archivo.getName() + "». " + error.getMessage());
            }
        });

        HBox controles = new HBox(12, importar, dirigido);
        controles.setAlignment(Pos.CENTER_LEFT);
        VBox panel = new VBox(12, controles, estado, conexiones);
        panel.setPadding(new Insets(16));
        VBox.setVgrow(conexiones, Priority.ALWAYS);
        ventana.setTitle("Sistema de gestión y análisis de rutas");
        ventana.setScene(new Scene(panel, 720, 440));
        ventana.show();
    }

    private static String formatearConexiones(Grafo grafo) {
        StringJoiner lineas = new StringJoiner(System.lineSeparator());
        for (Vertice vertice : grafo.vertices()) {
            StringJoiner destinos = new StringJoiner(", ");
            destinos.setEmptyValue("Sin conexiones salientes");
            for (Arista arista : grafo.conexionesDesde(vertice)) {
                destinos.add(arista.destino().nombre() + " (" + arista.peso() + ")");
            }
            lineas.add(vertice.nombre() + " → " + destinos);
        }
        return lineas.toString();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
