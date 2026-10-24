# Base de datos

Esquema relacional en SQLite, definido en [`esquema.sql`](esquema.sql). La aplicación lo ejecuta al conectarse, y crea `atuendos.db` si no existe.

## Diagrama

```mermaid
erDiagram
    CLIENTE ||--o{ SERVICIO : solicita
    EMPLEADO ||--o{ SERVICIO : atiende
    SERVICIO ||--|{ SERVICIO_PRENDA : incluye
    PRENDA ||--o{ SERVICIO_PRENDA : "se alquila en"
    PRENDA ||--o{ LAVANDERIA : "se registra en"

    CLIENTE {
        TEXT identificacion PK
        TEXT nombre
        TEXT direccion
        TEXT telefono
        TEXT correo
    }

    EMPLEADO {
        TEXT identificacion PK
        TEXT nombre
        TEXT direccion
        TEXT telefono
        TEXT cargo
    }

    PRENDA {
        TEXT referencia PK
        TEXT tipo "DAMA, CABALLERO o DISFRAZ"
        TEXT color
        TEXT marca
        TEXT talla
        REAL valor_alquiler
        INTEGER pedreria "solo dama, 0 o 1"
        TEXT altura "solo dama, LARGO o CORTO"
        INTEGER cantidad_piezas "solo dama"
        TEXT tipo_traje "solo caballero"
        TEXT accesorio "solo caballero"
        TEXT nombre_disfraz "solo disfraz"
    }

    SERVICIO {
        INTEGER numero PK "consecutivo"
        TEXT fecha_solicitud "fecha del sistema"
        TEXT fecha_alquiler
        TEXT empleado_id FK
        TEXT cliente_id FK
    }

    SERVICIO_PRENDA {
        INTEGER servicio_numero PK, FK
        TEXT prenda_ref PK, FK
    }

    LAVANDERIA {
        INTEGER orden PK "orden de llegada"
        TEXT prenda_ref FK
        INTEGER prioritaria "0 o 1"
        INTEGER enviada "0 o 1"
        TEXT fecha_envio
    }
```

## Tablas

| Tabla | Contenido |
| --- | --- |
| `cliente` | Clientes, identificados por su `identificacion`. |
| `empleado` | Empleados, identificados por su `identificacion`. |
| `prenda` | Todas las prendas. La columna `tipo` indica el subtipo, y solo las columnas de ese subtipo tienen valor. |
| `servicio` | Servicios de alquiler, con número consecutivo, un cliente y un empleado. |
| `servicio_prenda` | Prendas de cada servicio. |
| `lavanderia` | Registros de envío a lavandería, pendientes o enviados. |

## Reglas

- **Subtipos de prenda:** una restricción `CHECK` exige las columnas propias de cada tipo y deja nulas las demás.
- **Consecutivo:** `servicio.numero` es entero autoincremental, y no se reutiliza.
- **Disponibilidad:** una prenda está disponible para una fecha si ningún servicio con esa `fecha_alquiler` la incluye.
- **Lavandería:** la lista pendiente son las filas con `enviada = 0`, ordenadas por `prioritaria` descendente y luego por `orden`. Un índice único evita que una prenda quede dos veces pendiente.
- **Claves foráneas:** SQLite las ignora si no se activan en cada conexión con `PRAGMA foreign_keys = ON`.

## Inspección

```bash
sqlite3 atuendos.db ".schema"
```
