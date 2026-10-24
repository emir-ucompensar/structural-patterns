/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.fabrica;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Aderezo;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;
import atuendos.modelo.TipoTraje;
import atuendos.modelo.TrajeCaballero;

/**
 * Fábrica concreta de trajes de caballero.
 */
public class FabricaTrajeCaballero extends FabricaPrenda {

    @Override
    protected TipoPrenda tipoProducto() {
        return TipoPrenda.CABALLERO;
    }

    @Override
    protected Prenda crearProducto(DatosPrenda datos) throws AtuendosException {
        return new TrajeCaballero(datos.referencia().trim(), datos.color().trim(), datos.marca().trim(),
                datos.talla().trim(), datos.valorAlquiler(),
                datos.enumerado(Constantes.CAMPO_TIPO_TRAJE, TipoTraje.class),
                datos.enumerado(Constantes.CAMPO_ADEREZO, Aderezo.class));
    }
}
