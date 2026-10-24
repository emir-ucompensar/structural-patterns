# Los Atuendos

Aplicación de consola en Java para administrar el alquiler de vestuario: prendas, clientes, empleados, servicios de alquiler y envío a lavandería. Usa SQLite como base de datos.

## Dependencias

- JDK 21 o superior
- Maven 3.9 o superior

En Debian: `sudo apt install openjdk-21-jdk maven`

## Compilación

```bash
mvn package
```

Compila, ejecuta las pruebas y deja el ejecutable en `target/atuendos.jar`. Para omitir las pruebas: `mvn package -DskipTests`

## Ejecución

```bash
java -jar target/atuendos.jar
```

La base de datos se crea como `atuendos.db` en el directorio de ejecución, si no existe.

## Entorno de desarrollo

| Componente | Versión |
| --- | --- |
| Sistema operativo | GNU/Linux Debian 13 (trixie) |
| JDK | OpenJDK 21.0.12 |
| Maven | 3.9.9 |
| SQLite JDBC | 3.46.1.3, resuelto por Maven |
| JUnit | 5.10.2, resuelto por Maven |

Las dependencias de Maven se descargan al compilar, así que el primer `mvn package` necesita conexión a internet.
