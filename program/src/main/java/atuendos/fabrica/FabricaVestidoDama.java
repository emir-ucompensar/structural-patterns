/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.fabrica;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Altura;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;
import atuendos.modelo.VestidoDama;

/**
 * Fábrica concreta de vestidos de dama.
 */
public class FabricaVestidoDama extends FabricaPrenda {

    @Override
    protected TipoPrenda tipoProducto() {
        return TipoPrenda.DAMA;
    }

    @Override
    protected Prenda crearProducto(DatosPrenda datos) throws AtuendosException {
        return new VestidoDama(datos.referencia().trim(), datos.color().trim(), datos.marca().trim(),
                datos.talla().trim(), datos.valorAlquiler(),
                datos.booleano(Constantes.CAMPO_PEDRERIA),
                datos.enumerado(Constantes.CAMPO_ALTURA, Altura.class),
                datos.enteroPositivo(Constantes.CAMPO_CANTIDAD_PIEZAS));
    }
}
