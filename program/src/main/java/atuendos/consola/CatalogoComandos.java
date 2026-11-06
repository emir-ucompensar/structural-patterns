/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.negocio.NegocioAlquiler;
import java.util.List;

/**
 * Cliente del patrón Command: crea cada comando con sus receptores y los agrupa
 * como los muestra el menú.
 */
public final class CatalogoComandos {

    private CatalogoComandos() {
    }

    /**
     * Arma los grupos de comandos del menú principal.
     *
     * @param terminal terminal que reciben los comandos
     * @param negocio fachada que reciben los comandos
     * @return los grupos, en el orden del menú principal
     */
    public static List<GrupoComandos> grupos(Terminal terminal, NegocioAlquiler negocio) {
        return List.of(
                new GrupoComandos("Registros", List.of(
                        new RegistrarPrendaComando(terminal, negocio),
                        new RegistrarClienteComando(terminal, negocio),
                        new RegistrarEmpleadoComando(terminal, negocio))),
                new GrupoComandos("Servicios de alquiler", List.of(
                        new RegistrarServicioComando(terminal, negocio),
                        new ConsultarServicioComando(terminal, negocio),
                        new ConsultarServiciosDeClienteComando(terminal, negocio),
                        new ConsultarServiciosPorFechaComando(terminal, negocio))),
                new GrupoComandos("Listados y consultas", List.of(
                        new ConsultarPrendasPorTallaComando(terminal, negocio),
                        new ListarPrendasComando(terminal, negocio),
                        new ListarClientesComando(terminal, negocio),
                        new ListarEmpleadosComando(terminal, negocio))),
                new GrupoComandos("Lavandería", List.of(
                        new RegistrarEnLavanderiaComando(terminal, negocio),
                        new VerPendientesLavanderiaComando(terminal, negocio),
                        new EnviarALavanderiaComando(terminal, negocio))));
    }
}
