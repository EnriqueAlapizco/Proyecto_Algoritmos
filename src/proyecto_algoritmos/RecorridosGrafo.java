package proyecto_algoritmos;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public final class RecorridosGrafo {
    private RecorridosGrafo() {
    }

    public static List<Vertice> bfs(Grafo grafo, Vertice inicio) {
        return bfs(grafo, inicio, null);
    }

    public static List<Vertice> bfs(Grafo grafo, Vertice inicio, Vertice objetivo) {
        List<Vertice> orden = new ArrayList<>();
        Set<Vertice> visitados = new HashSet<>();
        Queue<Vertice> pendientes = new ArrayDeque<>();
        visitados.add(inicio);
        pendientes.add(inicio);

        while (!pendientes.isEmpty()) {
            Vertice actual = pendientes.remove();
            orden.add(actual);
            if (actual.equals(objetivo)) {
                break;
            }
            for (Arista arista : grafo.conexionesDesde(actual)) {
                Vertice vecino = arista.destino();
                if (visitados.add(vecino)) {
                    pendientes.add(vecino);
                }
            }
        }
        return List.copyOf(orden);
    }

    public static List<Vertice> dfs(Grafo grafo, Vertice inicio) {
        return dfs(grafo, inicio, null);
    }

    public static List<Vertice> dfs(Grafo grafo, Vertice inicio, Vertice objetivo) {
        if (inicio.equals(objetivo)) {
            return List.of(inicio);
        }
        List<Vertice> orden = new ArrayList<>();
        Set<Vertice> visitados = new HashSet<>();
        Deque<Iterator<Arista>> pendientes = new ArrayDeque<>();
        visitados.add(inicio);
        orden.add(inicio);
        pendientes.push(grafo.conexionesDesde(inicio).iterator());

        // Cada iterador conserva el próximo vecino al que se vuelve después de explorar una rama.
        while (!pendientes.isEmpty()) {
            Iterator<Arista> vecinos = pendientes.peek();
            if (!vecinos.hasNext()) {
                pendientes.pop();
                continue;
            }
            Vertice vecino = vecinos.next().destino();
            if (visitados.add(vecino)) {
                orden.add(vecino);
                if (vecino.equals(objetivo)) {
                    break;
                }
                pendientes.push(grafo.conexionesDesde(vecino).iterator());
            }
        }
        return List.copyOf(orden);
    }
}
