/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.negocio.EnvioLavanderia;
import atuendos.negocio.NegocioAlquiler;

/**
 * Envía una tanda de prendas a lavandería. La notificación del envío la
 * muestran los suscriptores del despacho; el comando solo avisa si no salió nada.
 */
final class EnviarALavanderiaComando extends ComandoConsola {

    private static final String TITULO = "Enviar prendas a lavandería";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    EnviarALavanderiaComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        EnvioLavanderia envio = negocio.enviarALavanderia(terminal.leerEntero("Cantidad de prendas a enviar"));
        if (envio.prendas().isEmpty()) {
            terminal.info(Vistas.MSG_SIN_PENDIENTES);
        }
    }
}
