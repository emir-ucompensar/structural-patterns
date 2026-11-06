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

/**
 * Registra un cliente con todos sus datos.
 */
final class RegistrarClienteComando extends ComandoConsola {

    private static final String TITULO = "Registrar cliente";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    RegistrarClienteComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        Cliente cliente = negocio.registrarCliente(
                terminal.leerObligatorio("Identificación"),
                terminal.leerObligatorio("Nombre"),
                terminal.leerObligatorio("Dirección"),
                terminal.leerObligatorio("Teléfono"),
                terminal.leerObligatorio("Correo"));
        terminal.exito("Cliente registrado.");
        Vistas.fichaCliente(terminal, cliente);
    }
}
