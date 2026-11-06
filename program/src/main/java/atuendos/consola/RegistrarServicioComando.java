/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.ServicioAlquiler;
import atuendos.negocio.NegocioAlquiler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Registra un servicio de alquiler con su cliente, su empleado y sus prendas.
 */
final class RegistrarServicioComando extends ComandoConsola {

    private static final String TITULO = "Registrar servicio de alquiler";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    RegistrarServicioComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        String clienteId = terminal.leerObligatorio("Identificación del cliente");
        String empleadoId = terminal.leerObligatorio("Identificación del empleado");
        LocalDate fecha = terminal.leerFecha("Fecha de alquiler (dd/mm/aaaa)");
        terminal.linea("Referencias de las prendas (ejemplo: " + Constantes.EJEMPLO_REFERENCIA
                + "), una por línea; vacío para terminar.");
        List<String> referencias = new ArrayList<>();
        while (true) {
            String referencia = terminal.leer("Prenda " + (referencias.size() + 1));
            if (referencia.isBlank()) {
                break;
            }
            referencias.add(referencia);
        }
        ServicioAlquiler servicio = negocio.registrarServicio(clienteId, empleadoId, referencias, fecha);
        terminal.exito("Servicio de alquiler registrado.");
        Vistas.fichaServicio(terminal, servicio);
    }
}
