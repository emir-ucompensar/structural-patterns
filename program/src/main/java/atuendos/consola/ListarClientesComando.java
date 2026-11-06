/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Cliente;
import atuendos.negocio.NegocioAlquiler;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista todos los clientes registrados.
 */
final class ListarClientesComando extends ComandoConsola {

    private static final String TITULO = "Listar clientes";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    ListarClientesComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion("Clientes registrados");
        List<String[]> filas = new ArrayList<>();
        for (Cliente cliente : negocio.listarClientes()) {
            filas.add(new String[] {cliente.getIdentificacion(), cliente.getNombre(), cliente.getDireccion(),
                cliente.getTelefono(), cliente.getCorreo()});
        }
        Vistas.tablaPersonas(terminal, new String[] {"Identificación", "Nombre", "Dirección", "Teléfono", "Correo"},
                filas);
    }
}
