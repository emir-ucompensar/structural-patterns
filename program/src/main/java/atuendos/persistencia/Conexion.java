/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Conexión única a la base de datos SQLite (patrón Singleton).
 *
 * <p>Al crearse activa las claves foráneas y ejecuta el esquema empaquetado,
 * que es idempotente, así que la base queda lista aunque el archivo no exista.
 */
public final class Conexion {

    private static Conexion instancia;

    private final Connection conexion;

    private Conexion(String url) throws AtuendosException {
        try {
            conexion = DriverManager.getConnection(url);
            try (Statement sentencia = conexion.createStatement()) {
                sentencia.execute("PRAGMA foreign_keys = ON");
            }
            crearEsquema();
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_CONEXION, e);
        }
    }

    /**
     * Devuelve la conexión única, y la crea la primera vez.
     *
     * @return la instancia compartida
     * @throws AtuendosException si no se puede abrir la base de datos
     */
    public static synchronized Conexion getInstancia() throws AtuendosException {
        if (instancia == null) {
            instancia = new Conexion(Constantes.URL_BASE_DATOS);
        }
        return instancia;
    }

    /**
     * Cierra la instancia actual y abre otra sobre la URL indicada. Existe para
     * que las pruebas usen una base temporal.
     *
     * @param url URL JDBC de la base de datos
     * @return la nueva instancia compartida
     * @throws AtuendosException si no se puede abrir la base de datos
     */
    static synchronized Conexion reiniciar(String url) throws AtuendosException {
        cerrarInstancia();
        instancia = new Conexion(url);
        return instancia;
    }

    /**
     * Cierra la conexión única. La siguiente llamada a {@link #getInstancia()}
     * abre una nueva.
     */
    public static synchronized void cerrarInstancia() {
        if (instancia != null) {
            try {
                instancia.conexion.close();
            } catch (SQLException e) {
                // Al cerrar no hay nada más que hacer con la conexión.
            }
            instancia = null;
        }
    }

    /**
     * Entrega la conexión JDBC.
     *
     * @return la conexión abierta
     */
    public Connection getConexion() {
        return conexion;
    }

    private void crearEsquema() throws SQLException, AtuendosException {
        for (String sql : leerEsquema().split(Constantes.SEPARADOR_SQL)) {
            if (!sql.isBlank()) {
                try (Statement sentencia = conexion.createStatement()) {
                    sentencia.execute(sql);
                }
            }
        }
    }

    private static String leerEsquema() throws AtuendosException {
        try (InputStream entrada = Conexion.class.getResourceAsStream(Constantes.RECURSO_ESQUEMA)) {
            if (entrada == null) {
                throw new AtuendosException(Constantes.MSG_ERROR_ESQUEMA);
            }
            StringBuilder sql = new StringBuilder();
            for (String linea : new String(entrada.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                int comentario = linea.indexOf(Constantes.COMENTARIO_SQL);
                sql.append(comentario >= 0 ? linea.substring(0, comentario) : linea).append('\n');
            }
            return sql.toString();
        } catch (IOException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_ESQUEMA, e);
        }
    }
}
