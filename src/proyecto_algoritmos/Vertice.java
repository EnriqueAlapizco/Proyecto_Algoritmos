package proyecto_algoritmos;

public record Vertice(String nombre) {
    public Vertice {
        nombre = nombre.strip();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del vértice está vacío.");
        }
    }
}
