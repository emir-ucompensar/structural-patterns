/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.modelo.PrendaEnLavanderia;
import atuendos.negocio.NegocioAlquiler;
import java.util.List;

/**
 * Registra una prenda en la lista de lavandería, con o sin prioridad.
 */
final class RegistrarEnLavanderiaComando extends ComandoConsola {

    private static final String TITULO = "Registrar prenda para lavandería";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    RegistrarEnLavanderiaComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        String referencia = terminal.leerObligatorio("Referencia de la prenda");
        boolean prioritaria = terminal.confirmar("¿Con prioridad? (manchada, delicada o decisión del administrador)");
        PrendaEnLavanderia registro = negocio.registrarEnLavanderia(referencia, prioritaria);
        terminal.exito(prioritaria ? "Prenda registrada con prioridad." : "Prenda registrada por orden de llegada.");
        terminal.ficha("Lavandería", List.of(
                new String[] {"Referencia", registro.prenda().getReferencia()},
                new String[] {"Tipo", registro.prenda().getTipo().name()},
                new String[] {"Talla", registro.prenda().getTalla()},
                new String[] {"Prioridad", registro.prioritaria() ? "Sí" : "No"}));
    }
}
