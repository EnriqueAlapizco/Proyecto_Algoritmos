package proyecto_algoritmos;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ImportadorCsv {
    private static final String CAMPO = "[ \\t]*(\"(?:[^\"\\r\\n]|\"\")*+\"|[^\"\\r\\n,]*)";
    private static final Pattern FILA = Pattern.compile(
            CAMPO + "[ \\t]*," + CAMPO + "[ \\t]*," + CAMPO + "[ \\t]*");

    private ImportadorCsv() {
    }

    public static Grafo importar(Path archivo, boolean dirigido) throws IOException {
        Grafo grafo = new Grafo(dirigido);
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            validarEncabezado(lector.readLine());
            int numeroLinea = 1;
            String linea;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (!linea.isBlank()) {
                    importarConexion(grafo, linea, numeroLinea);
                }
            }
        }
        if (grafo.aristas().isEmpty()) {
            throw new IOException("El CSV no contiene conexiones.");
        }
        return grafo;
    }

    private static void validarEncabezado(String linea) throws IOException {
        if (linea == null) {
            throw new IOException("El CSV está vacío.");
        }
        Matcher campos = FILA.matcher(linea.startsWith("\uFEFF") ? linea.substring(1) : linea);
        if (!campos.matches() || !campo(campos, 1).equalsIgnoreCase("origen")
                || !campo(campos, 2).equalsIgnoreCase("destino")
                || !campo(campos, 3).equalsIgnoreCase("peso")) {
            throw new IOException("Línea 1. El encabezado debe contener origen,destino,peso.");
        }
    }

    private static void importarConexion(Grafo grafo, String linea, int numeroLinea)
            throws IOException {
        Matcher campos = FILA.matcher(linea);
        if (!campos.matches()) {
            throw new IOException("Línea " + numeroLinea + ". Se esperaban tres campos CSV válidos.");
        }
        try {
            Vertice origen = new Vertice(campo(campos, 1));
            Vertice destino = new Vertice(campo(campos, 2));
            double peso = Double.parseDouble(campo(campos, 3));
            Arista arista = new Arista(origen, destino, peso);
            grafo.registrarVertice(origen);
            grafo.registrarVertice(destino);
            grafo.registrarArista(arista);
        } catch (NumberFormatException error) {
            throw new IOException("Línea " + numeroLinea + ". El peso debe ser numérico.", error);
        } catch (IllegalArgumentException error) {
            throw new IOException("Línea " + numeroLinea + ". " + error.getMessage(), error);
        }
    }

    private static String campo(Matcher campos, int indice) {
        String valor = campos.group(indice).strip();
        if (valor.startsWith("\"")) {
            return valor.substring(1, valor.length() - 1).replace("\"\"", "\"");
        }
        return valor;
    }
}
