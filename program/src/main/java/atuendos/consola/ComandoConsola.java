/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.negocio.NegocioAlquiler;

/**
 * Base de los comandos de la consola. Guarda los dos receptores del patrón
 * Command: la terminal, que lee y muestra, y la fachada, que aplica las reglas.
 */
abstract class ComandoConsola implements Comando {

    /** Receptor que lee los datos y muestra los resultados. */
    protected final Terminal terminal;

    /** Receptor que aplica las reglas de negocio. */
    protected final NegocioAlquiler negocio;

    private final String titulo;

    /**
     * Crea el comando con su título y sus receptores.
     *
     * @param titulo texto de la opción en el menú
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    protected ComandoConsola(String titulo, Terminal terminal, NegocioAlquiler negocio) {
        this.titulo = titulo;
        this.terminal = terminal;
        this.negocio = negocio;
    }

    @Override
    public String titulo() {
        return titulo;
    }
}
