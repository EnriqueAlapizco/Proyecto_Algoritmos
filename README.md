# Proyecto Algoritmos

Sistema de gestión y análisis de rutas mediante grafos. El proyecto usa Java 24, JavaFX y NetBeans con Ant.

## Abrir en NetBeans

1. Abre esta carpeta como proyecto y selecciona JDK 24 como plataforma de Java.
2. Agrega `javafx.sdk.dir` a `nbproject/private/private.properties` con la ruta al SDK de JavaFX, sin incluir `lib`. Por ejemplo, `javafx.sdk.dir=C:/SDK/javafx-sdk-26`.
3. Ejecuta el proyecto desde NetBeans.

## Importar conexiones

Pulsa **Importar CSV** y selecciona `datos/conexiones.csv` para probar la importación. Con tres columnas, cada fila se lee de origen a destino. Puedes escribir la vuelta en otra fila con su propio peso. Con la columna opcional `sentido`, el valor `ambos` registra ida y vuelta como una sola arista con el mismo peso.

## Editar y exportar

1. Pulsa **Nuevo grafo** o importa un CSV existente.
2. Teclea el nombre de cada vértice y pulsa **Agregar** en la sección **Vértices**. Selecciona un vértice para renombrarlo o eliminarlo.
3. Selecciona el origen y el destino, escribe el peso y marca **Permitir ambos sentidos** si quieres ida y vuelta con el mismo peso. Pulsa **Agregar**. Para modificar una arista, selecciónala, cambia los campos y pulsa **Actualizar**.
4. Pulsa **Exportar CSV**. Cada vértice debe participar en al menos una conexión. Si el grafo contiene una arista de ida y vuelta, el CSV añade la columna `sentido` con los valores `unico` o `ambos`.
