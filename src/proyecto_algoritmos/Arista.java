package proyecto_algoritmos;

public record Arista(Vertice origen, Vertice destino, double peso, boolean bidireccional) {
    public Arista(Vertice origen, Vertice destino, double peso) {
        this(origen, destino, peso, false);
    }

    public Arista {
        if (!Double.isFinite(peso) || peso < 0) {
            throw new IllegalArgumentException("El peso debe ser un número finito no negativo.");
        }
    }
}
