package proyecto_algoritmos;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

final class EditorGrafo {
    private Grafo grafo = new Grafo();
    private boolean cambiosPendientes;

    void crear() {
        grafo = new Grafo();
        cambiosPendientes = false;
    }

    void importar(Path archivo) throws IOException {
        grafo = ImportadorCsv.importar(archivo);
        cambiosPendientes = false;
    }

    void exportar(Path archivo) throws IOException {
        ExportadorCsv.exportar(grafo, archivo);
        cambiosPendientes = false;
    }

    void agregarVertice(String nombre) {
        if (!grafo.registrarVertice(new Vertice(nombre))) {
            throw new IllegalArgumentException("Ya existe un vértice con ese nombre.");
        }
        cambiosPendientes = true;
    }

    void renombrarVertice(Vertice seleccionado, String nombre) {
        grafo.renombrarVertice(seleccionado, new Vertice(nombre));
        cambiosPendientes = true;
    }

    void eliminarVertice(Vertice seleccionado) {
        grafo.eliminarVertice(seleccionado);
        cambiosPendientes = true;
    }

    void agregarArista(Arista arista) {
        grafo.registrarArista(arista);
        cambiosPendientes = true;
    }

    void modificarArista(Arista seleccionada, Arista nueva) {
        grafo.modificarArista(seleccionada, nueva);
        cambiosPendientes = true;
    }

    void eliminarArista(Arista seleccionada) {
        grafo.eliminarArista(seleccionada);
        cambiosPendientes = true;
    }

    Dijkstra.Resultado calcularRutaMinima(Vertice origen, Vertice destino) {
    return Dijkstra.calcular(grafo, origen, destino);
    }

    double[][] matrizAdyacencia() {
    return grafo.matrizAdyacencia();
    }

    boolean tieneCambiosPendientes() {
        return cambiosPendientes;
    }

    List<Vertice> vertices() {
        return grafo.vertices();
    }

    List<Arista> aristas() {
        return grafo.aristas();
    }

    List<Arista> conexionesDesde(Vertice vertice) {
        return grafo.conexionesDesde(vertice);
    }

    List<Vertice> recorrerBfs(Vertice inicio, Vertice objetivo) {
        return RecorridosGrafo.bfs(grafo, inicio, objetivo);
    }

    List<Vertice> recorrerDfs(Vertice inicio, Vertice objetivo) {
        return RecorridosGrafo.dfs(grafo, inicio, objetivo);
    }
}
