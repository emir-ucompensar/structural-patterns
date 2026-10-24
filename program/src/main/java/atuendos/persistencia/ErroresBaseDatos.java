/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;

import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Traduce los errores de SQLite a mensajes que el usuario entiende, y
 * conserva el error original como causa.
 */
final class ErroresBaseDatos {

    private static final Pattern DETALLE = Pattern.compile("\\(([^()]*)\\)\\s*$");

    private ErroresBaseDatos() {
    }

    /**
     * Convierte un error de SQLite en una excepción de la aplicación.
     *
     * @param error error original de la base de datos
     * @return la excepción con un mensaje claro y el error original como causa
     */
    static AtuendosException traducir(SQLException error) {
        return new AtuendosException(mensaje(error), error);
    }

    static String mensaje(SQLException error) {
        String texto = error.getMessage() == null ? "" : error.getMessage();
        String minusculas = texto.toLowerCase(Locale.ROOT);
        if (minusculas.contains("no such column") || minusculas.contains("no such table")
                || minusculas.contains("has no column named")) {
            return String.format(Constantes.MSG_BD_ESQUEMA_DISTINTO, detalle(texto));
        }
        if (minusculas.contains("locked") || minusculas.contains("busy")) {
            return Constantes.MSG_BD_OCUPADA;
        }
        if (minusculas.contains("readonly") || minusculas.contains("read-only")) {
            return Constantes.MSG_BD_SOLO_LECTURA;
        }
        if (minusculas.contains("unique constraint") || minusculas.contains("primary key")) {
            return Constantes.MSG_BD_DUPLICADO;
        }
        if (minusculas.contains("foreign key")) {
            return Constantes.MSG_BD_REFERENCIA;
        }
        if (minusculas.contains("check constraint") || minusculas.contains("not null constraint")) {
            return Constantes.MSG_BD_RESTRICCION;
        }
        return String.format(Constantes.MSG_BD_DESCONOCIDO, detalle(texto));
    }

    private static String detalle(String texto) {
        Matcher coincidencia = DETALLE.matcher(texto);
        return coincidencia.find() ? coincidencia.group(1) : texto;
    }
}
