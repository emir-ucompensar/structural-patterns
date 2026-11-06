/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.negocio.NegocioAlquiler;

/**
 * Consulta un servicio por su número.
 */
final class ConsultarServicioComando extends ComandoConsola {

    private static final String TITULO = "Consultar servicio por número";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    ConsultarServicioComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        Vistas.fichaServicio(terminal, negocio.consultarServicio(terminal.leerEntero("Número del servicio")));
    }
}
