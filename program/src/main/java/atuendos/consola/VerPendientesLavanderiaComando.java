/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.modelo.ColaLavanderia;
import atuendos.negocio.NegocioAlquiler;

/**
 * Muestra las prendas pendientes de envío a lavandería, sin enviarlas.
 */
final class VerPendientesLavanderiaComando extends ComandoConsola {

    private static final String TITULO = "Ver prendas pendientes de envío";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    VerPendientesLavanderiaComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion("Prendas pendientes de envío");
        ColaLavanderia pendientes = negocio.listarLavanderiaPendiente();
        if (pendientes.estaVacia()) {
            terminal.info(Vistas.MSG_SIN_PENDIENTES);
        } else {
            Vistas.tablaLavanderia(terminal, pendientes);
        }
    }
}
