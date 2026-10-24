/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos;

import atuendos.comun.AtuendosException;
import atuendos.consola.Menu;
import atuendos.consola.Terminal;
import atuendos.negocio.NegocioAlquiler;
import atuendos.persistencia.Conexion;

/**
 * Punto de entrada de la aplicación de consola.
 */
public final class Main {

    private Main() {
    }

    /**
     * Inicia la aplicación.
     *
     * @param args argumentos de línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        int codigo = 0;
        try (Terminal terminal = Terminal.estandar()) {
            try {
                new Menu(terminal, NegocioAlquiler.crear()).ejecutar();
            } catch (AtuendosException e) {
                terminal.error(e.getMessage());
                codigo = 1;
            } finally {
                Conexion.cerrarInstancia();
            }
        }
        if (codigo != 0) {
            System.exit(codigo);
        }
    }
}
