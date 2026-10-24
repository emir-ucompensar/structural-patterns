-- Los Atuendos - esquema de la base de datos (SQLite).
--
-- Archivo de autoría propia, generado con asistencia de herramientas de
-- inteligencia artificial (Claude Code).
--
-- Las fechas se guardan como texto ISO 8601 (YYYY-MM-DD).
-- SQLite exige activar las claves foráneas en cada conexión.

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS cliente (
    identificacion TEXT PRIMARY KEY,
    nombre         TEXT NOT NULL,
    direccion      TEXT NOT NULL,
    telefono       TEXT NOT NULL,
    correo         TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS empleado (
    identificacion TEXT PRIMARY KEY,
    nombre         TEXT NOT NULL,
    direccion      TEXT NOT NULL,
    telefono       TEXT NOT NULL,
    cargo          TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS prenda (
    referencia      TEXT PRIMARY KEY,
    tipo            TEXT NOT NULL CHECK (tipo IN ('DAMA', 'CABALLERO', 'DISFRAZ')),
    color           TEXT NOT NULL,
    marca           TEXT NOT NULL,
    talla           TEXT NOT NULL,
    valor_alquiler  REAL NOT NULL CHECK (valor_alquiler >= 0),
    -- dama
    pedreria        INTEGER CHECK (pedreria IN (0, 1)),
    altura          TEXT    CHECK (altura IN ('LARGO', 'CORTO')),
    cantidad_piezas INTEGER CHECK (cantidad_piezas > 0),
    -- caballero
    tipo_traje      TEXT    CHECK (tipo_traje IN ('CONVENCIONAL', 'FRAC', 'SACOLEVA', 'OTRO')),
    accesorio       TEXT    CHECK (accesorio IN ('CORBATA', 'CORBATIN', 'PLASTRON')),
    -- disfraz
    nombre_disfraz  TEXT,
    CHECK (
        (tipo = 'DAMA'
            AND pedreria IS NOT NULL AND altura IS NOT NULL AND cantidad_piezas IS NOT NULL
            AND tipo_traje IS NULL AND accesorio IS NULL AND nombre_disfraz IS NULL)
        OR (tipo = 'CABALLERO'
            AND tipo_traje IS NOT NULL AND accesorio IS NOT NULL
            AND pedreria IS NULL AND altura IS NULL AND cantidad_piezas IS NULL AND nombre_disfraz IS NULL)
        OR (tipo = 'DISFRAZ'
            AND nombre_disfraz IS NOT NULL
            AND pedreria IS NULL AND altura IS NULL AND cantidad_piezas IS NULL
            AND tipo_traje IS NULL AND accesorio IS NULL)
    )
);

CREATE TABLE IF NOT EXISTS servicio (
    numero          INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha_solicitud TEXT NOT NULL,
    fecha_alquiler  TEXT NOT NULL,
    empleado_id     TEXT NOT NULL REFERENCES empleado (identificacion),
    cliente_id      TEXT NOT NULL REFERENCES cliente (identificacion)
);

CREATE TABLE IF NOT EXISTS servicio_prenda (
    servicio_numero INTEGER NOT NULL REFERENCES servicio (numero),
    prenda_ref      TEXT    NOT NULL REFERENCES prenda (referencia),
    PRIMARY KEY (servicio_numero, prenda_ref)
);

CREATE TABLE IF NOT EXISTS lavanderia (
    orden       INTEGER PRIMARY KEY AUTOINCREMENT,
    prenda_ref  TEXT    NOT NULL REFERENCES prenda (referencia),
    prioritaria INTEGER NOT NULL DEFAULT 0 CHECK (prioritaria IN (0, 1)),
    enviada     INTEGER NOT NULL DEFAULT 0 CHECK (enviada IN (0, 1)),
    fecha_envio TEXT
);

-- Una prenda no puede estar dos veces pendiente de envío.
CREATE UNIQUE INDEX IF NOT EXISTS ux_lavanderia_pendiente
    ON lavanderia (prenda_ref) WHERE enviada = 0;
