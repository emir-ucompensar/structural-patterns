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
import java.util.ArrayList;
import java.util.List;

/**
 * Lista todos los empleados registrados.
 */
final class ListarEmpleadosComando extends ComandoConsola {

    private static final String TITULO = "Listar empleados";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    ListarEmpleadosComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion("Empleados registrados");
        List<String[]> filas = new ArrayList<>();
        for (Empleado empleado : negocio.listarEmpleados()) {
            filas.add(new String[] {empleado.getIdentificacion(), empleado.getNombre(), empleado.getDireccion(),
                empleado.getTelefono(), empleado.getCargo()});
        }
        Vistas.tablaPersonas(terminal, new String[] {"Identificación", "Nombre", "Dirección", "Teléfono", "Cargo"},
                filas);
    }
}
