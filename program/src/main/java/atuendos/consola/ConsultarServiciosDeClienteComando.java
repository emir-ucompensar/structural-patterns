/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.negocio.NegocioAlquiler;
import atuendos.negocio.ServiciosDeCliente;

/**
 * Consulta un cliente y sus servicios vigentes.
 */
final class ConsultarServiciosDeClienteComando extends ComandoConsola {

    private static final String TITULO = "Consultar servicios de un cliente";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    ConsultarServiciosDeClienteComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        ServiciosDeCliente resultado = negocio.consultarServiciosDeCliente(
                terminal.leerObligatorio("Identificación del cliente"));
        Vistas.fichaCliente(terminal, resultado.cliente());
        terminal.seccion("Servicios vigentes");
        Vistas.tablaServicios(terminal, resultado.vigentes());
    }
}
