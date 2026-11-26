package proyecto_algoritmos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public final class Dijkstra {

    private Dijkstra() {
    }

    public record Resultado(List<Vertice> ruta, double costoTotal,
                            boolean alcanzable) {
        public Resultado {
            ruta = List.copyOf(ruta);
        }
    }

    private record Estado(Vertice vertice, double distancia) {
    }

    public static Resultado calcular(Grafo grafo, Vertice origen,
                                     Vertice destino) {
        if (grafo == null || origen == null || destino == null) {
            throw new IllegalArgumentException(
                    "El grafo, origen y destino son obligatorios.");
        }

        List<Vertice> vertices = grafo.vertices();

        if (!vertices.contains(origen) || !vertices.contains(destino)) {
            throw new IllegalArgumentException(
                    "El origen y destino deben existir en el grafo.");
        }

        if (origen.equals(destino)) {
            return new Resultado(List.of(origen), 0, true);
        }

        Map<Vertice, Double> distancias = new HashMap<>();
        Map<Vertice, Vertice> predecesores = new HashMap<>();

        for (Vertice vertice : vertices) {
            distancias.put(vertice, Double.POSITIVE_INFINITY);
        }

        PriorityQueue<Estado> pendientes = new PriorityQueue<>(
                Comparator.comparingDouble(Estado::distancia));

        distancias.put(origen, 0.0);
        pendientes.add(new Estado(origen, 0.0));

        while (!pendientes.isEmpty()) {
            Estado estado = pendientes.poll();
            Vertice actual = estado.vertice();

            // Ignorar estados que ya tienen una distancia anterior mejor.
            if (estado.distancia() > distancias.get(actual)) {
                continue;
            }

            if (actual.equals(destino)) {
                break;
            }

            for (Arista arista : grafo.conexionesDesde(actual)) {
                Vertice vecino = arista.destino();
                double nuevaDistancia =
                        distancias.get(actual) + arista.peso();

                if (Double.isFinite(nuevaDistancia)
                        && nuevaDistancia < distancias.get(vecino)) {

                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, actual);

                    pendientes.add(
                            new Estado(vecino, nuevaDistancia));
                }
            }
        }

        double costo = distancias.get(destino);

        if (!Double.isFinite(costo)) {
            return new Resultado(List.of(),
                    Double.POSITIVE_INFINITY, false);
        }

        List<Vertice> ruta = new ArrayList<>();
        Vertice actual = destino;

        while (actual != null) {
            ruta.add(actual);
            actual = predecesores.get(actual);
        }

        Collections.reverse(ruta);

        return new Resultado(ruta, costo, true);
    }
}
