/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;

import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas de la conexión única y del esquema que crea.
 */
class ConexionTest {

    @TempDir
    Path directorio;

    @AfterEach
    void cerrar() {
        Conexion.cerrarInstancia();
    }

    private String url() {
        return "jdbc:sqlite:" + directorio.resolve("prueba.db");
    }

    @Test
    void devuelveSiempreLaMismaInstancia() throws AtuendosException {
        Conexion primera = Conexion.reiniciar(url());
        assertSame(primera, Conexion.getInstancia());
    }

    @Test
    void creaLasSeisTablas() throws AtuendosException, SQLException {
        Conexion conexion = Conexion.reiniciar(url());
        List<String> tablas = new ArrayList<>();
        try (Statement sentencia = conexion.getConexion().createStatement();
                ResultSet filas = sentencia.executeQuery(
                        "SELECT name FROM sqlite_master WHERE type = 'table' "
                                + "AND name NOT LIKE 'sqlite_%' ORDER BY name")) {
            while (filas.next()) {
                tablas.add(filas.getString(1));
            }
        }
        assertEquals(
                List.of("cliente", "empleado", "lavanderia", "prenda", "servicio", "servicio_prenda"),
                tablas);
    }

    @Test
    void activaLasClavesForaneas() throws AtuendosException, SQLException {
        Conexion conexion = Conexion.reiniciar(url());
        try (Statement sentencia = conexion.getConexion().createStatement();
                ResultSet fila = sentencia.executeQuery("PRAGMA foreign_keys")) {
            assertTrue(fila.next());
            assertEquals(1, fila.getInt(1));
        }
    }

    @Test
    void rechazaServicioConClienteInexistente() throws AtuendosException {
        Conexion conexion = Conexion.reiniciar(url());
        assertThrows(SQLException.class, () -> {
            try (Statement sentencia = conexion.getConexion().createStatement()) {
                sentencia.executeUpdate(
                        "INSERT INTO servicio (fecha_solicitud, fecha_alquiler, empleado_id, cliente_id) "
                                + "VALUES ('2026-10-23', '2026-11-01', 'e', 'c')");
            }
        });
    }

    @Test
    void elEsquemaSePuedeEjecutarDosVeces() throws AtuendosException {
        String url = url();
        Conexion.reiniciar(url);
        Conexion.reiniciar(url);
    }
}
