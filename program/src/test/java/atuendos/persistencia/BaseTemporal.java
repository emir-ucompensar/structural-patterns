/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;

import java.nio.file.Path;
import java.sql.Connection;

/**
 * Ayuda de pruebas: abre una base SQLite temporal con el esquema completo.
 */
public final class BaseTemporal {

    private BaseTemporal() {
    }

    /**
     * Abre una base nueva en el directorio dado.
     *
     * @param directorio directorio temporal
     * @return la conexión con el esquema creado
     * @throws AtuendosException si no se puede abrir
     */
    public static Connection abrir(Path directorio) throws AtuendosException {
        return Conexion.reiniciar("jdbc:sqlite:" + directorio.resolve("prueba.db")).getConexion();
    }

    /**
     * Cierra la base abierta.
     */
    public static void cerrar() {
        Conexion.cerrarInstancia();
    }
}
