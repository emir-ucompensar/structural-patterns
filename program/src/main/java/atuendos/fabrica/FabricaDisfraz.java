/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.fabrica;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Disfraz;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;

/**
 * Fábrica concreta de disfraces.
 */
public class FabricaDisfraz extends FabricaPrenda {

    @Override
    protected TipoPrenda tipoProducto() {
        return TipoPrenda.DISFRAZ;
    }

    @Override
    protected Prenda crearProducto(DatosPrenda datos) throws AtuendosException {
        return new Disfraz(datos.referencia().trim(), datos.color().trim(), datos.marca().trim(),
                datos.talla().trim(), datos.valorAlquiler(),
                datos.texto(Constantes.CAMPO_NOMBRE_DISFRAZ));
    }
}
