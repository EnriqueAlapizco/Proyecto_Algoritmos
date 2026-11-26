package proyecto_algoritmos;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GeneradorCsvPruebas {

    private GeneradorCsvPruebas() {
    }

    public static void main(String[] args) throws IOException {

        Path carpeta = Path.of("datos", "pruebas");
        Files.createDirectories(carpeta);

        int[] tamanos = {20, 100, 500, 1000};

        for (int cantidad : tamanos) {
            generar(carpeta.resolve("grafo_" + cantidad + ".csv"),
                    cantidad);

            System.out.println(
                    "Generado: grafo_" + cantidad + ".csv");
        }
    }

    private static void generar(Path archivo, int cantidad)
            throws IOException {

        try (BufferedWriter escritor = Files.newBufferedWriter(
                archivo, StandardCharsets.UTF_8)) {

            escritor.write("origen,destino,peso");
            escritor.newLine();

            // Cadena que garantiza que todos los vértices
            // sean alcanzables desde el primero.
            for (int i = 0; i < cantidad - 1; i++) {
                escritor.write(
                        "V" + i + ",V" + (i + 1) + "," + (1 + i % 20));
                escritor.newLine();
            }

            // Conexiones adicionales para aumentar la densidad.
            for (int i = 0; i < cantidad; i++) {

                int destino = i + 3;

                if (destino < cantidad) {
                    escritor.write(
                            "V" + i + ",V" + destino + ","
                            + (2 + i % 15));
                    escritor.newLine();
                }

                destino = i + 7;

                if (destino < cantidad) {
                    escritor.write(
                            "V" + i + ",V" + destino + ","
                            + (3 + i % 12));
                    escritor.newLine();
                }
            }
        }
    }
}
