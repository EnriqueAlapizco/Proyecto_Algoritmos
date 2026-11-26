package proyecto_algoritmos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class OrdenadorAristas {

    private OrdenadorAristas() {
    }

    public static List<Arista> quickSort(
            List<Arista> originales,
            Comparator<Arista> comparador) {

        List<Arista> copia = new ArrayList<>(originales);

        quickSort(copia, 0, copia.size() - 1, comparador);

        return copia;
    }

    private static void quickSort(
            List<Arista> lista, int inicio, int fin,
            Comparator<Arista> comparador) {

        if (inicio >= fin) {
            return;
        }

        int izquierda = inicio;
        int derecha = fin;

        Arista pivote = lista.get(inicio + (fin - inicio) / 2);

        while (izquierda <= derecha) {

            while (comparador.compare(
                    lista.get(izquierda), pivote) < 0) {
                izquierda++;
            }

            while (comparador.compare(
                    lista.get(derecha), pivote) > 0) {
                derecha--;
            }

            if (izquierda <= derecha) {
                Arista temporal = lista.get(izquierda);
                lista.set(izquierda, lista.get(derecha));
                lista.set(derecha, temporal);

                izquierda++;
                derecha--;
            }
        }

        if (inicio < derecha) {
            quickSort(lista, inicio, derecha, comparador);
        }

        if (izquierda < fin) {
            quickSort(lista, izquierda, fin, comparador);
        }
    }

    public static List<Arista> shellSort(
            List<Arista> originales,
            Comparator<Arista> comparador) {

        List<Arista> copia = new ArrayList<>(originales);
        int n = copia.size();

        // Secuencia de saltos: n/2, n/4, ..., 1.
        for (int salto = n / 2; salto > 0; salto /= 2) {

            for (int i = salto; i < n; i++) {
                Arista temporal = copia.get(i);
                int j = i;

                while (j >= salto
                        && comparador.compare(
                                copia.get(j - salto), temporal) > 0) {

                    copia.set(j, copia.get(j - salto));
                    j -= salto;
                }

                copia.set(j, temporal);
            }
        }

        return copia;
    }

    public static Comparator<Arista> porPeso() {
        return Comparator.comparingDouble(Arista::peso)
                .thenComparing(a -> a.origen().nombre())
                .thenComparing(a -> a.destino().nombre());
    }

    public static Comparator<Arista> porNombre() {
        return Comparator.comparing(
                (Arista a) -> a.origen().nombre(),
                String.CASE_INSENSITIVE_ORDER)
                .thenComparing(
                        a -> a.destino().nombre(),
                        String.CASE_INSENSITIVE_ORDER)
                .thenComparingDouble(Arista::peso);
    }
}
