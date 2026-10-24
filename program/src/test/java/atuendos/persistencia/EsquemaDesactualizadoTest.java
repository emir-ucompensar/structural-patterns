/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Accesorio;
import atuendos.modelo.TipoTraje;
import atuendos.modelo.TrajeCaballero;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Reproduce una base creada con una versión anterior del esquema y comprueba
 * que el error se explique en lugar de ser genérico.
 */
class EsquemaDesactualizadoTest {

    @TempDir
    Path directorio;

    @Test
    void explicaQueElEsquemaNoCoincide() throws SQLException {
        try (Connection conexion = DriverManager.getConnection("jdbc:sqlite:" + directorio.resolve("vieja.db"))) {
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.execute("CREATE TABLE prenda (referencia TEXT PRIMARY KEY, tipo TEXT, color TEXT, "
                        + "marca TEXT, talla TEXT, valor_alquiler REAL, pedreria INTEGER, altura TEXT, "
                        + "cantidad_piezas INTEGER, tipo_traje TEXT, aderezo TEXT, nombre_disfraz TEXT)");
            }
            PrendaRepositorio prendas = new PrendaRepositorioSqlite(conexion);
            AtuendosException error = assertThrows(AtuendosException.class, () -> prendas.guardar(
                    new TrajeCaballero("REF067", "Azul", "Supreme", "M", 130000, TipoTraje.CONVENCIONAL,
                            Accesorio.CORBATA)));
            assertTrue(error.getMessage().contains("no coincide con el programa"));
            assertTrue(error.getMessage().contains("atuendos.db"));
        }
    }
}
