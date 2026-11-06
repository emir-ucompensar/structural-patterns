/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.fabrica.DatosPrenda;
import atuendos.modelo.Accesorio;
import atuendos.modelo.Altura;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;
import atuendos.modelo.TipoTraje;
import atuendos.negocio.NegocioAlquiler;
import java.util.HashMap;
import java.util.Map;

/**
 * Registra una prenda, con los datos propios de su tipo.
 */
final class RegistrarPrendaComando extends ComandoConsola {

    private static final String TITULO = "Registrar prenda";

    /**
     * Crea el comando con sus receptores.
     *
     * @param terminal terminal para leer y mostrar
     * @param negocio fachada de la aplicación
     */
    RegistrarPrendaComando(Terminal terminal, NegocioAlquiler negocio) {
        super(TITULO, terminal, negocio);
    }

    @Override
    public void ejecutar() throws AtuendosException {
        terminal.seccion(TITULO);
        TipoPrenda tipo = terminal.elegirEnum("Tipo de prenda", TipoPrenda.class);
        String referencia = leerReferenciaNueva();
        String color = terminal.leerObligatorio("Color");
        String marca = terminal.leerObligatorio("Marca");
        String talla = Constantes.TALLAS.get(terminal.elegir("Talla", Constantes.TALLAS));
        double valor = terminal.leerDecimal("Valor del alquiler");
        Map<String, String> especificos = new HashMap<>();
        switch (tipo) {
            case DAMA -> {
                especificos.put(Constantes.CAMPO_PEDRERIA, String.valueOf(terminal.confirmar("¿Lleva pedrería?")));
                especificos.put(Constantes.CAMPO_ALTURA, terminal.elegirEnum("Altura", Altura.class).name());
                especificos.put(Constantes.CAMPO_CANTIDAD_PIEZAS,
                        String.valueOf(terminal.leerEntero("Cantidad de piezas")));
            }
            case CABALLERO -> {
                especificos.put(Constantes.CAMPO_TIPO_TRAJE,
                        terminal.elegirEnum("Tipo de traje", TipoTraje.class).name());
                especificos.put(Constantes.CAMPO_ACCESORIO, terminal.elegirEnum("Accesorio", Accesorio.class).name());
            }
            case DISFRAZ -> especificos.put(Constantes.CAMPO_NOMBRE_DISFRAZ,
                    terminal.leerObligatorio("Nombre del disfraz"));
        }
        Prenda prenda = negocio.registrarPrenda(
                new DatosPrenda(tipo, referencia, color, marca, talla, valor, especificos));
        terminal.exito("Prenda registrada.");
        Vistas.fichaPrenda(terminal, prenda);
    }

    private String leerReferenciaNueva() throws AtuendosException {
        while (true) {
            String referencia = terminal.leerObligatorio("Referencia (ejemplo: " + Constantes.EJEMPLO_REFERENCIA + ")");
            if (!negocio.existePrenda(referencia)) {
                return referencia;
            }
            terminal.error(String.format(Constantes.MSG_PRENDA_EXISTE, referencia.trim()));
        }
    }
}
