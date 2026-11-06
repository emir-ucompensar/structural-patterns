/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.negocio.NegocioAlquiler;
import java.time.LocalDate;

/**
 * Consulta los servicios de una fecha de alquiler.
 */
final class ConsultarServiciosPorFechaComando extends ComandoConsola {

    private static final String TITULO = "Consultar servicios por fecha";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    ConsultarServiciosPorFechaComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        LocalDate fecha = terminal.leerFecha("Fecha de alquiler (dd/mm/aaaa)");
        terminal.seccion("Servicios del " + Vistas.formatear(fecha));
        Vistas.tablaServicios(terminal, negocio.consultarServiciosPorFecha(fecha));
    }
}
