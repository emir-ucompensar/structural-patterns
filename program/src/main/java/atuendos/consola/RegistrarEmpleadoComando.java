/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Empleado;
import atuendos.negocio.NegocioAlquiler;

/**
 * Registra un empleado con todos sus datos.
 */
final class RegistrarEmpleadoComando extends ComandoConsola {

    private static final String TITULO = "Registrar empleado";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    RegistrarEmpleadoComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        Empleado empleado = negocio.registrarEmpleado(
                terminal.leerObligatorio("Identificación"),
                terminal.leerObligatorio("Nombre"),
                terminal.leerObligatorio("Dirección"),
                terminal.leerObligatorio("Teléfono"),
                terminal.leerObligatorio("Cargo"));
        terminal.exito("Empleado registrado.");
        Vistas.fichaEmpleado(terminal, empleado);
    }
}
