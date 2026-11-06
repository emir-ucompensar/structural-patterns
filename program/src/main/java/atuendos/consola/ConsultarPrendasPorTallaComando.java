/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;
import atuendos.negocio.NegocioAlquiler;
import java.util.List;
import java.util.Map;

/**
 * Consulta las prendas de una o varias tallas, separadas por tipo.
 */
final class ConsultarPrendasPorTallaComando extends ComandoConsola {

    private static final String TITULO = "Consultar prendas por talla";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    ConsultarPrendasPorTallaComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        List<String> tallas = terminal.elegirVarias("Tallas", Constantes.TALLAS, 1).stream()
                .map(Constantes.TALLAS::get).toList();
        Map<TipoPrenda, List<Prenda>> porTipo = negocio.consultarPrendasPorTallas(tallas);
        String etiquetaTallas = (tallas.size() == 1 ? "talla " : "tallas ") + String.join(", ", tallas);
        for (Map.Entry<TipoPrenda, List<Prenda>> entrada : porTipo.entrySet()) {
            terminal.seccion(Vistas.titulo(entrada.getKey()) + " - " + etiquetaTallas);
            if (entrada.getValue().isEmpty()) {
                terminal.linea("Sin prendas.");
            } else {
                Vistas.tablaPrendas(terminal, entrada.getValue());
            }
        }
    }
}
