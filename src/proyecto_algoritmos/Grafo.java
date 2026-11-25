package proyecto_algoritmos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Grafo {
    private final Map<Vertice, Map<Vertice, Arista>> adyacencias = new LinkedHashMap<>();
    private final List<Arista> aristas = new ArrayList<>();

    public boolean registrarVertice(Vertice vertice) {
        if (adyacencias.containsKey(vertice)) {
            return false;
        }
        adyacencias.put(vertice, new LinkedHashMap<>());
        return true;
    }

    public void renombrarVertice(Vertice actual, Vertice nuevo) {
        if (!adyacencias.containsKey(actual)) {
            throw new IllegalArgumentException("El vértice seleccionado ya no existe.");
        }
        if (actual.equals(nuevo)) {
            return;
        }
        if (adyacencias.containsKey(nuevo)) {
            throw new IllegalArgumentException("Ya existe un vértice con ese nombre.");
        }

        Grafo actualizado = new Grafo();
        for (Vertice vertice : adyacencias.keySet()) {
            actualizado.registrarVertice(vertice.equals(actual) ? nuevo : vertice);
        }
        for (Arista arista : aristas) {
            Vertice origen = arista.origen().equals(actual) ? nuevo : arista.origen();
            Vertice destino = arista.destino().equals(actual) ? nuevo : arista.destino();
            actualizado.registrarArista(new Arista(origen, destino, arista.peso(), arista.bidireccional()));
        }
        adyacencias.clear();
        adyacencias.putAll(actualizado.adyacencias);
        aristas.clear();
        aristas.addAll(actualizado.aristas);
    }

    public void eliminarVertice(Vertice vertice) {
        aristas.removeIf(arista -> {
            if (!arista.origen().equals(vertice) && !arista.destino().equals(vertice)) {
                return false;
            }
            quitarAdyacencias(arista);
            return true;
        });
        adyacencias.remove(vertice);
    }

    public void registrarArista(Arista arista) {
        validarArista(arista, null);
        guardarAdyacencias(arista);
        aristas.add(arista);
    }

    public void modificarArista(Arista actual, Arista nueva) {
        int posicion = aristas.indexOf(actual);
        if (posicion < 0) {
            throw new IllegalArgumentException("La arista seleccionada ya no existe.");
        }
        validarArista(nueva, actual);
        if (!mismoSentido(actual, nueva.origen(), nueva.destino())
                || actual.bidireccional() != nueva.bidireccional()) {
            quitarAdyacencias(actual);
        }
        guardarAdyacencias(nueva);
        aristas.set(posicion, nueva);
    }

    public void eliminarArista(Arista arista) {
        if (aristas.remove(arista)) {
            quitarAdyacencias(arista);
        }
    }

    private void validarArista(Arista arista, Arista actual) {
        Vertice origen = arista.origen();
        Vertice destino = arista.destino();
        if (!adyacencias.containsKey(origen) || !adyacencias.containsKey(destino)) {
            throw new IllegalArgumentException("Registra ambos vértices antes de conectar la arista.");
        }
        validarSentido(origen, destino, actual);
        if (arista.bidireccional() && !origen.equals(destino)) {
            validarSentido(destino, origen, actual);
        }
    }

    private void validarSentido(Vertice origen, Vertice destino, Arista actual) {
        if (adyacencias.get(origen).containsKey(destino) && !ocupaSentido(actual, origen, destino)) {
            throw new IllegalArgumentException(
                    "Conexión duplicada entre «" + origen.nombre() + "» y «" + destino.nombre() + "».");
        }
    }

    private static boolean ocupaSentido(Arista arista, Vertice origen, Vertice destino) {
        return arista != null && (mismoSentido(arista, origen, destino)
                || arista.bidireccional() && mismoSentido(arista, destino, origen));
    }

    private static boolean mismoSentido(Arista arista, Vertice origen, Vertice destino) {
        return arista.origen().equals(origen) && arista.destino().equals(destino);
    }

    private void guardarAdyacencias(Arista arista) {
        Vertice origen = arista.origen();
        Vertice destino = arista.destino();
        adyacencias.get(origen).put(destino, arista);
        if (arista.bidireccional() && !origen.equals(destino)) {
            adyacencias.get(destino).put(origen, new Arista(destino, origen, arista.peso(), true));
        }
    }

    private void quitarAdyacencias(Arista arista) {
        adyacencias.get(arista.origen()).remove(arista.destino());
        if (arista.bidireccional() && !arista.origen().equals(arista.destino())) {
            adyacencias.get(arista.destino()).remove(arista.origen());
        }
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
