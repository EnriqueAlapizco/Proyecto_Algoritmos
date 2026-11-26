package proyecto_algoritmos;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public final class AnalizadorRendimiento {

    private static final int CALENTAMIENTO = 3;
    private static final int REPETICIONES = 10;

    private AnalizadorRendimiento() {
    }

    public static void main(String[] args) throws IOException {

        Path carpeta = Path.of("datos", "pruebas");
        Path salida = carpeta.resolve("resultados_rendimiento.csv");

        int[] tamanos = {20, 100, 500, 1000};

        try (BufferedWriter escritor = Files.newBufferedWriter(
                salida, StandardCharsets.UTF_8)) {

            escritor.write(
                    "vertices,aristas,operacion,tiempo_promedio_ns");
            escritor.newLine();

            for (int cantidad : tamanos) {

                Path archivo = carpeta.resolve(
                        "grafo_" + cantidad + ".csv");

                // Medir importación y construcción del grafo.
                long importacion = medir(() -> {
                    try {
                        ImportadorCsv.importar(archivo);
                    } catch (IOException error) {
                        throw new RuntimeException(error);
                    }
                });

                Grafo grafo = ImportadorCsv.importar(archivo);

                List<Vertice> vertices = grafo.vertices();
                List<Arista> aristas = grafo.aristas();

                Vertice inicio = vertices.getFirst();
                Vertice destino = vertices.getLast();

                escribir(escritor, vertices.size(), aristas.size(),
                        "Importacion y construccion", importacion);

                escribir(escritor, vertices.size(), aristas.size(),
                        "BFS", medir(() ->
                                RecorridosGrafo.bfs(grafo, inicio)));

                escribir(escritor, vertices.size(), aristas.size(),
                        "DFS", medir(() ->
                                RecorridosGrafo.dfs(grafo, inicio)));

                escribir(escritor, vertices.size(), aristas.size(),
                        "Dijkstra", medir(() ->
                                Dijkstra.calcular(grafo, inicio, destino)));

                escribir(escritor, vertices.size(), aristas.size(),
                        "QuickSort por peso", medir(() ->
                                OrdenadorAristas.quickSort(
                                        aristas,
                                        OrdenadorAristas.porPeso())));

                escribir(escritor, vertices.size(), aristas.size(),
                        "Shell Sort por peso", medir(() ->
                                OrdenadorAristas.shellSort(
                                        aristas,
                                        OrdenadorAristas.porPeso())));

                System.out.println(
                        "Mediciones terminadas para " + cantidad
                        + " vértices.");
            }
        }

        System.out.println("Resultados guardados en: " + salida);
    }

    private static long medir(Runnable operacion) {

        for (int i = 0; i < CALENTAMIENTO; i++) {
            operacion.run();
        }

        long total = 0;

        for (int i = 0; i < REPETICIONES; i++) {
            long inicio = System.nanoTime();

            operacion.run();

            long fin = System.nanoTime();
            total += fin - inicio;
        }

        return total / REPETICIONES;
    }

    private static void escribir(
            BufferedWriter escritor,
            int vertices,
            int aristas,
            String operacion,
            long tiempo) throws IOException {

        escritor.write(
                vertices + ","
                + aristas + ","
                + operacion + ","
                + tiempo);

        escritor.newLine();
    }
}
