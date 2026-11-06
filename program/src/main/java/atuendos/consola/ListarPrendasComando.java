/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Prenda;
import atuendos.negocio.NegocioAlquiler;
import java.util.List;

/**
 * Lista todas las prendas registradas.
 */
final class ListarPrendasComando extends ComandoConsola {

    private static final String TITULO = "Listar prendas";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    ListarPrendasComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion("Prendas registradas");
        List<Prenda> prendas = negocio.listarPrendas();
        if (prendas.isEmpty()) {
            terminal.info(Vistas.MSG_SIN_PRENDAS);
        } else {
            Vistas.tablaPrendas(terminal, prendas);
        }
    }
}
