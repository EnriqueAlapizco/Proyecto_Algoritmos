package proyecto_algoritmos;

public record Vertice(String nombre) {
    public Vertice {
        nombre = nombre.strip();
        if (nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del vértice está vacío.");
        }
        if (nombre.indexOf('\n') >= 0 || nombre.indexOf('\r') >= 0) {
            throw new IllegalArgumentException("El nombre del vértice debe ocupar una sola línea.");
        }
    }
}
