package proyecto_algoritmos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Grafo {
    private final boolean dirigido;
    private final Map<Vertice, Map<Vertice, Arista>> adyacencias = new LinkedHashMap<>();
    private final List<Arista> aristas = new ArrayList<>();

    public Grafo(boolean dirigido) {
        this.dirigido = dirigido;
    }

    public void registrarVertice(Vertice vertice) {
        adyacencias.computeIfAbsent(vertice, clave -> new LinkedHashMap<>());
    }

    public void registrarArista(Arista arista) {
        Vertice origen = arista.origen();
        Vertice destino = arista.destino();
        Map<Vertice, Arista> salientes = adyacencias.get(origen);
        Map<Vertice, Arista> entrantes = adyacencias.get(destino);
        if (salientes == null || entrantes == null) {
            throw new IllegalArgumentException("Registra ambos vértices antes de conectar la arista.");
        }
        if (salientes.containsKey(destino)) {
            throw new IllegalArgumentException(
                    "Conexión duplicada entre «" + origen.nombre() + "» y «" + destino.nombre() + "».");
        }
        salientes.put(destino, arista);
        if (!dirigido && !origen.equals(destino)) {
            entrantes.put(origen, new Arista(destino, origen, arista.peso()));
        }
        aristas.add(arista);
    }

    public List<Vertice> vertices() {
        return List.copyOf(adyacencias.keySet());
    }

    public List<Arista> aristas() {
        return List.copyOf(aristas);
    }

    public List<Arista> conexionesDesde(Vertice vertice) {
        Map<Vertice, Arista> conexiones = adyacencias.get(vertice);
        if (conexiones == null) {
            throw new IllegalArgumentException("El vértice no está registrado en el grafo.");
        }
        return List.copyOf(conexiones.values());
    }

    /**
     * Las filas y columnas siguen el orden de vertices().
     * Las celdas con Double.NaN indican que no existe una conexión.
     */
    public double[][] matrizAdyacencia() {
        List<Vertice> vertices = vertices();
        Map<Vertice, Integer> posiciones = new LinkedHashMap<>();
        double[][] matriz = new double[vertices.size()][vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            posiciones.put(vertices.get(i), i);
            Arrays.fill(matriz[i], Double.NaN);
        }
        for (int i = 0; i < vertices.size(); i++) {
            for (Arista arista : adyacencias.get(vertices.get(i)).values()) {
                matriz[i][posiciones.get(arista.destino())] = arista.peso();
            }
        }
        return matriz;
    }
}
