/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.comun;

/**
 * Constantes compartidas por toda la aplicación.
 */
public final class Constantes {

    /** URL JDBC de la base de datos, un archivo en el directorio de ejecución. */
    public static final String URL_BASE_DATOS = "jdbc:sqlite:atuendos.db";

    /** Ruta del esquema dentro del classpath. */
    public static final String RECURSO_ESQUEMA = "/esquema.sql";

    /** Prefijo de los comentarios del esquema SQL. */
    public static final String COMENTARIO_SQL = "--";

    /** Separador de sentencias del esquema SQL. */
    public static final String SEPARADOR_SQL = ";";

    /** Mensaje cuando no se puede abrir la base de datos. */
    public static final String MSG_ERROR_CONEXION = "No se pudo abrir la base de datos.";

    /** Mensaje cuando no se puede leer el esquema empaquetado. */
    public static final String MSG_ERROR_ESQUEMA = "No se pudo leer el esquema de la base de datos.";

    private Constantes() {
    }
}
