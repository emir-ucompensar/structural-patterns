/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;

import java.sql.SQLException;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la traducción de errores de SQLite a mensajes para el usuario.
 */
class ErroresBaseDatosTest {

    private static String mensaje(String textoSqlite) {
        return ErroresBaseDatos.mensaje(new SQLException(textoSqlite));
    }

    @Test
    void reconoceUnEsquemaDesactualizado() {
        String mensaje = mensaje("[SQLITE_ERROR] SQL error or missing database (no such column: accesorio)");
        assertTrue(mensaje.contains("no coincide con el programa"));
        assertTrue(mensaje.contains("no such column: accesorio"));
        assertTrue(mensaje.contains("atuendos.db"));
        assertTrue(mensaje("table prenda has no column named accesorio").contains("no coincide"));
        assertTrue(mensaje("[SQLITE_ERROR] SQL error (no such table: servicio)").contains("no coincide"));
    }

    @Test
    void reconoceLaBaseOcupadaYDeSoloLectura() {
        assertEquals(Constantes.MSG_BD_OCUPADA,
                mensaje("[SQLITE_BUSY] The database file is locked (database is locked)"));
        assertEquals(Constantes.MSG_BD_SOLO_LECTURA,
                mensaje("[SQLITE_READONLY] Attempt to write a readonly database"));
    }

    @Test
    void reconoceLasRestricciones() {
        assertEquals(Constantes.MSG_BD_DUPLICADO,
                mensaje("[SQLITE_CONSTRAINT_PRIMARYKEY] A PRIMARY KEY constraint failed"));
        assertEquals(Constantes.MSG_BD_DUPLICADO,
                mensaje("[SQLITE_CONSTRAINT_UNIQUE] UNIQUE constraint failed: lavanderia.prenda_ref"));
        assertEquals(Constantes.MSG_BD_REFERENCIA,
                mensaje("[SQLITE_CONSTRAINT_FOREIGNKEY] FOREIGN KEY constraint failed"));
        assertEquals(Constantes.MSG_BD_RESTRICCION,
                mensaje("[SQLITE_CONSTRAINT_CHECK] A CHECK constraint failed (CHECK constraint failed: x)"));
        assertEquals(Constantes.MSG_BD_RESTRICCION,
                mensaje("[SQLITE_CONSTRAINT_NOTNULL] NOT NULL constraint failed: cliente.nombre"));
    }

    @Test
    void conservaElDetalleDeUnErrorDesconocido() {
        String mensaje = mensaje("[SQLITE_IOERR] Some disk I/O error occurred (disk I/O error)");
        assertTrue(mensaje.startsWith("Error al acceder a la base de datos:"));
        assertTrue(mensaje.contains("disk I/O error"));
    }

    @Test
    void manejaUnMensajeNulo() {
        assertTrue(ErroresBaseDatos.mensaje(new SQLException()).startsWith("Error al acceder"));
    }

    @Test
    void conservaElErrorOriginalComoCausa() {
        SQLException original = new SQLException("[SQLITE_BUSY] database is locked");
        AtuendosException traducida = ErroresBaseDatos.traducir(original);
        assertSame(original, traducida.getCause());
        assertEquals(Constantes.MSG_BD_OCUPADA, traducida.getMessage());
    }
}
