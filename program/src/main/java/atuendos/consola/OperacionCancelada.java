/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

/**
 * Aviso de que quien usa la consola pidió volver sin terminar la operación, con
 * Esc o escribiendo el símbolo de volver. Como los comandos guardan solo al
 * final, nada de lo escrito hasta ese momento queda guardado.
 */
final class OperacionCancelada extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Crea el aviso de cancelación, sin traza, porque no es un error.
     */
    OperacionCancelada() {
        super("Operación cancelada", null, false, false);
    }
}
