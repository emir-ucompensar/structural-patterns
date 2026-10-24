/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.comun;

/**
 * Error de negocio o de acceso a datos que la consola captura y muestra,
 * para que la aplicación no aborte.
 */
public class AtuendosException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Crea la excepción con un mensaje para el usuario.
     *
     * @param mensaje descripción del error
     */
    public AtuendosException(String mensaje) {
        super(mensaje);
    }

    /**
     * Crea la excepción con un mensaje y la causa técnica original.
     *
     * @param mensaje descripción del error
     * @param causa excepción que lo originó
     */
    public AtuendosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
