package proyecto_algoritmos;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

final class VentanaGrafo {
    private final Stage ventana;
    private final EditorGrafo editor;

    private final Label resumen = new Label();
    private final Label estado = new Label();
    private final TextArea conexiones = new TextArea();
    private final ComboBox<Vertice> vertices = selector("Vértice a editar", Vertice::nombre);
    private final TextField nombre = new TextField();
    private final ComboBox<Arista> aristas = selector("Arista a editar",
            arista -> arista.origen().nombre() + (arista.bidireccional() ? " ↔ " : " → ")
                    + arista.destino().nombre()
                    + " (" + arista.peso() + ")");
    private final ComboBox<Vertice> origen = selector("Origen", Vertice::nombre);
    private final ComboBox<Vertice> destino = selector("Destino", Vertice::nombre);
    private final TextField peso = new TextField();
    private final CheckBox ambosSentidos = new CheckBox("Permitir ambos sentidos");
    private final ComboBox<Vertice> inicioRecorrido = selector("Vértice inicial", Vertice::nombre);
    private final ComboBox<Vertice> objetivoRecorrido = selector("Vértice objetivo (opcional)", Vertice::nombre);
    private final TextArea resultadoRecorrido = new TextArea();

    VentanaGrafo(Stage ventana, EditorGrafo editor) {
        this.ventana = ventana;
        this.editor = editor;
    }

    void mostrar() {
        conexiones.setEditable(false);
        conexiones.setWrapText(true);
        conexiones.setAccessibleText("Lista de adyacencia");
        estado.setWrapText(true);
        VBox vista = new VBox(8, resumen, conexiones, new Separator(), crearPanelRecorridos());
        VBox.setVgrow(conexiones, Priority.ALWAYS);
        HBox contenido = new HBox(20, crearEditor(), vista);
        HBox.setHgrow(vista, Priority.ALWAYS);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        VBox panel = new VBox(12, crearBarraArchivos(), new Separator(), contenido, estado);
        panel.setPadding(new Insets(16));

        actualizarVista();
        ventana.setTitle("Sistema de gestión y análisis de rutas");
        ventana.setScene(new Scene(panel, 920, 480));
        ventana.setMinWidth(780);
        ventana.setMinHeight(480);
        ventana.setOnCloseRequest(evento -> {
            if (!confirmarReemplazo()) {
                evento.consume();
            }
        });
        ventana.show();
    }

    private HBox crearBarraArchivos() {
        Button nuevo = new Button("Nuevo grafo");
        Button importar = new Button("Importar CSV");
        Button exportar = new Button("Exportar CSV");
        nuevo.setOnAction(evento -> crearGrafo());
        importar.setOnAction(evento -> importar());
        exportar.setOnAction(evento -> exportar());
        HBox barra = new HBox(12, nuevo, importar, exportar);
        barra.setAlignment(Pos.CENTER_LEFT);
        return barra;
    }

    private VBox crearEditor() {
        VBox formularios = new VBox(8, crearFormularioVertices(), new Separator(), crearFormularioAristas());
        formularios.setPrefWidth(300);
        return formularios;
    }

    private VBox crearFormularioVertices() {
        nombre.setPromptText("Nombre del vértice");
        vertices.valueProperty().addListener((observable, anterior, seleccionado) ->
                nombre.setText(seleccionado == null ? "" : seleccionado.nombre()));

        Button agregarVertice = new Button("Agregar");
        agregarVertice.setOnAction(evento -> editar(() -> editor.agregarVertice(nombre.getText()),
                "Vértice agregado."));
        Button renombrar = new Button("Renombrar");
        renombrar.disableProperty().bind(vertices.valueProperty().isNull());
        renombrar.setOnAction(evento -> editar(
                () -> editor.renombrarVertice(vertices.getValue(), nombre.getText()), "Vértice actualizado."));
        Button eliminarVertice = new Button("Eliminar");
        eliminarVertice.disableProperty().bind(vertices.valueProperty().isNull());
        eliminarVertice.setOnAction(evento -> editar(() -> editor.eliminarVertice(vertices.getValue()),
                "Vértice eliminado junto con sus conexiones."));

        return new VBox(8, new Label("Vértices"), vertices, nombre,
                new HBox(8, agregarVertice, renombrar, eliminarVertice));
    }

    private VBox crearFormularioAristas() {
        peso.setPromptText("Peso");
        aristas.valueProperty().addListener((observable, anterior, seleccionada) -> {
            origen.setValue(seleccionada == null ? null : seleccionada.origen());
            destino.setValue(seleccionada == null ? null : seleccionada.destino());
            peso.setText(seleccionada == null ? "" : Double.toString(seleccionada.peso()));
            ambosSentidos.setSelected(seleccionada != null && seleccionada.bidireccional());
        });

        Button agregarArista = new Button("Agregar");
        agregarArista.setOnAction(evento -> editar(() -> editor.agregarArista(leerArista()),
                "Conexión agregada."));
        Button modificarArista = new Button("Actualizar");
        modificarArista.disableProperty().bind(aristas.valueProperty().isNull());
        modificarArista.setOnAction(evento -> editar(
                () -> editor.modificarArista(aristas.getValue(), leerArista()), "Conexión actualizada."));
        Button eliminarArista = new Button("Eliminar");
        eliminarArista.disableProperty().bind(aristas.valueProperty().isNull());
        eliminarArista.setOnAction(evento -> editar(() -> editor.eliminarArista(aristas.getValue()),
                "Conexión eliminada."));

        return new VBox(8, new Label("Aristas"), aristas, origen, destino, peso, ambosSentidos,
                new HBox(8, agregarArista, modificarArista, eliminarArista));
    }

    private Arista leerArista() {
        if (origen.getValue() == null || destino.getValue() == null) {
            throw new IllegalArgumentException("Selecciona un origen y un destino.");
        }
        try {
            return new Arista(origen.getValue(), destino.getValue(), Double.parseDouble(peso.getText()),
                    ambosSentidos.isSelected());
        } catch (NumberFormatException error) {
            throw new IllegalArgumentException("El peso debe ser numérico. Usa punto para los decimales.");
        }
    }

    private VBox crearPanelRecorridos() {
        Button bfs = new Button("BFS");
        Button dfs = new Button("DFS");
        Button quitarObjetivo = new Button("Quitar objetivo");
        bfs.disableProperty().bind(inicioRecorrido.valueProperty().isNull());
        dfs.disableProperty().bind(inicioRecorrido.valueProperty().isNull());
        quitarObjetivo.disableProperty().bind(objetivoRecorrido.valueProperty().isNull());
        bfs.setOnAction(evento -> mostrarRecorrido("BFS",
                editor.recorrerBfs(inicioRecorrido.getValue(), objetivoRecorrido.getValue())));
        dfs.setOnAction(evento -> mostrarRecorrido("DFS",
                editor.recorrerDfs(inicioRecorrido.getValue(), objetivoRecorrido.getValue())));
        quitarObjetivo.setOnAction(evento -> objetivoRecorrido.setValue(null));
        inicioRecorrido.valueProperty().addListener((observable, anterior, actual) -> resultadoRecorrido.clear());
        objetivoRecorrido.valueProperty().addListener((observable, anterior, actual) -> resultadoRecorrido.clear());

        HBox controles = new HBox(8, inicioRecorrido, bfs, dfs);
        controles.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(inicioRecorrido, Priority.ALWAYS);
        HBox seleccionObjetivo = new HBox(8, objetivoRecorrido, quitarObjetivo);
        seleccionObjetivo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(objetivoRecorrido, Priority.ALWAYS);
        resultadoRecorrido.setEditable(false);
        resultadoRecorrido.setWrapText(true);
        resultadoRecorrido.setPrefRowCount(3);
        resultadoRecorrido.setAccessibleText("Orden de visita");
        return new VBox(8, new Label("Recorridos"), controles, seleccionObjetivo, resultadoRecorrido);
    }

    private void mostrarRecorrido(String algoritmo, List<Vertice> orden) {
        StringJoiner nombres = new StringJoiner(" → ");
        for (Vertice vertice : orden) {
            nombres.add(vertice.nombre());
        }
        Vertice objetivo = objetivoRecorrido.getValue();
        String encabezado = algoritmo + " · Orden de visita";
        if (objetivo != null) {
            encabezado += orden.getLast().equals(objetivo)
                    ? " · Objetivo encontrado (" + objetivo.nombre() + ")"
                    : " · Objetivo inalcanzable (" + objetivo.nombre() + ")";
        }
        resultadoRecorrido.setText(encabezado + "\n" + nombres);
        resultadoRecorrido.positionCaret(0);
    }

    private void editar(Runnable operacion, String mensaje) {
        try {
            operacion.run();
            mostrarCambio(mensaje);
        } catch (IllegalArgumentException error) {
            estado.setText(error.getMessage());
        }
    }

    private void crearGrafo() {
        if (confirmarReemplazo()) {
            editor.crear();
            mostrarCambio("Grafo nuevo.");
        }
    }

    private void importar() {
        File archivo = selectorCsv("Importar conexiones").showOpenDialog(ventana);
        if (archivo == null || !confirmarReemplazo()) {
            return;
        }
        try {
            editor.importar(archivo.toPath());
            mostrarCambio("Archivo importado · " + archivo.getName());
        } catch (IOException error) {
            estado.setText("Error al importar «" + archivo.getName() + "». " + error.getMessage());
        }
    }

    private void exportar() {
        FileChooser selector = selectorCsv("Exportar conexiones");
        selector.setInitialFileName("conexiones.csv");
        File archivo = selector.showSaveDialog(ventana);
        if (archivo == null) {
            return;
        }
        try {
            editor.exportar(archivo.toPath());
            estado.setText("Archivo exportado · " + archivo.getName());
        } catch (IOException error) {
            estado.setText("Error al exportar. " + error.getMessage());
        }
    }

    private boolean confirmarReemplazo() {
        if (!editor.tieneCambiosPendientes()) {
            return true;
        }
        ButtonType descartar = new ButtonType("Descartar cambios");
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "El grafo tiene cambios sin exportar.", descartar, ButtonType.CANCEL);
        confirmacion.initOwner(ventana);
        confirmacion.setHeaderText(null);
        return confirmacion.showAndWait().orElse(ButtonType.CANCEL) == descartar;
    }

    private void mostrarCambio(String mensaje) {
        actualizarVista();
        estado.setText(mensaje);
    }

    private void actualizarVista() {
        vertices.setValue(null);
        aristas.setValue(null);
        origen.setValue(null);
        destino.setValue(null);
        nombre.clear();
        peso.clear();
        ambosSentidos.setSelected(false);
        List<Vertice> ubicaciones = editor.vertices();
        List<Arista> rutas = editor.aristas();
        vertices.getItems().setAll(ubicaciones);
        origen.getItems().setAll(ubicaciones);
        destino.getItems().setAll(ubicaciones);
        aristas.getItems().setAll(rutas);
        inicioRecorrido.setValue(null);
        inicioRecorrido.getItems().setAll(ubicaciones);
        objetivoRecorrido.setValue(null);
        objetivoRecorrido.getItems().setAll(ubicaciones);
        resultadoRecorrido.clear();
        resumen.setText("%s · %d %s · %d %s".formatted(
                nombreTipoGrafo(rutas),
                ubicaciones.size(), ubicaciones.size() == 1 ? "vértice" : "vértices",
                rutas.size(), rutas.size() == 1 ? "arista" : "aristas"));
        conexiones.setText(formatearConexiones(ubicaciones));
    }

    private String nombreTipoGrafo(List<Arista> rutas) {
        if (rutas.isEmpty()) {
            return "Grafo sin aristas";
        }
        boolean todasBidireccionales = rutas.stream().allMatch(Arista::bidireccional);
        if (todasBidireccionales) {
            return "Grafo no dirigido";
        }
        return rutas.stream().anyMatch(Arista::bidireccional) ? "Grafo mixto" : "Grafo dirigido";
    }

    private static FileChooser selectorCsv(String titulo) {
        FileChooser selector = new FileChooser();
        selector.setTitle(titulo);
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV", "*.csv"));
        return selector;
    }

    private static <T> ComboBox<T> selector(String etiqueta, Function<T, String> texto) {
        ComboBox<T> opciones = new ComboBox<>();
        opciones.setMaxWidth(Double.MAX_VALUE);
        opciones.setPromptText(etiqueta);
        opciones.setAccessibleText(etiqueta);
        opciones.setConverter(new StringConverter<>() {
            @Override
            public String toString(T opcion) {
                return opcion == null ? "" : texto.apply(opcion);
            }

            @Override
            public T fromString(String entrada) {
                return null;
            }
        });
        return opciones;
    }

    private String formatearConexiones(List<Vertice> ubicaciones) {
        StringJoiner lineas = new StringJoiner(System.lineSeparator());
        for (Vertice vertice : ubicaciones) {
            StringJoiner destinos = new StringJoiner(", ");
            destinos.setEmptyValue("Sin conexiones salientes");
            for (Arista arista : editor.conexionesDesde(vertice)) {
                destinos.add(arista.destino().nombre() + " (" + arista.peso() + ")");
            }
            lineas.add(vertice.nombre() + " → " + destinos);
        }
        return lineas.toString();
    }
}
