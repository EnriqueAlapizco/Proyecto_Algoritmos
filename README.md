# Proyecto Algoritmos

Sistema de gestión y análisis de rutas mediante grafos. El proyecto usa Java 24, JavaFX y NetBeans con Ant.

## Abrir en NetBeans

1. Abre esta carpeta como proyecto y selecciona JDK 24 como plataforma de Java.
2. Agrega `javafx.sdk.dir` a `nbproject/private/private.properties` con la ruta al SDK de JavaFX, sin incluir `lib`. Por ejemplo, `javafx.sdk.dir=C:/SDK/javafx-sdk-26`.
3. Ejecuta el proyecto desde NetBeans.

## Importar conexiones

Pulsa **Importar CSV** y selecciona `datos/conexiones.csv` para probar la importación. Activa **Importar como grafo dirigido** si las conexiones deben recorrerse solo de origen a destino.
