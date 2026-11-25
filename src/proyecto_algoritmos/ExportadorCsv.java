package proyecto_algoritmos;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ExportadorCsv {
    private ExportadorCsv() {
    }

    public static void exportar(Grafo grafo, Path archivo) throws IOException {
        List<Arista> aristas = grafo.aristas();
        if (aristas.isEmpty()) {
            throw new IOException("Agrega al menos una conexión antes de exportar.");
        }
        Set<Vertice> conectados = new HashSet<>();
        for (Arista arista : aristas) {
            conectados.add(arista.origen());
            conectados.add(arista.destino());
        }
        for (Vertice vertice : grafo.vertices()) {
            if (!conectados.contains(vertice)) {
                throw new IOException("El CSV de conexiones no puede guardar el vértice aislado «"
                        + vertice.nombre() + "». Conéctalo o elimínalo antes de exportar.");
            }
        }

        Path destino = archivo.toAbsolutePath();
        Path temporal = Files.createTempFile(destino.getParent(), ".conexiones-", ".tmp");
        try {
            escribir(aristas, temporal);
            try {
                Files.move(temporal, destino, StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException error) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static void escribir(List<Arista> aristas, Path archivo) throws IOException {
        boolean conSentido = aristas.stream().anyMatch(Arista::bidireccional);
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            escritor.write(conSentido ? "origen,destino,peso,sentido" : "origen,destino,peso");
            escritor.newLine();
            for (Arista arista : aristas) {
                escritor.write(campo(arista.origen().nombre()) + ","
                        + campo(arista.destino().nombre()) + "," + arista.peso());
                if (conSentido) {
                    escritor.write(arista.bidireccional() ? ",ambos" : ",unico");
                }
                escritor.newLine();
            }
        }
    }

    private static String campo(String nombre) {
        if (nombre.indexOf(',') < 0 && nombre.indexOf('"') < 0) {
            return nombre;
        }
        return "\"" + nombre.replace("\"", "\"\"") + "\"";
    }
}
