/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.fabrica;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;

/**
 * Creador del patrón Factory Method: valida los datos comunes y delega en cada
 * subclase la creación de la prenda concreta.
 */
public abstract class FabricaPrenda {

    /**
     * Crea la fábrica base; solo la usan las fábricas concretas.
     */
    protected FabricaPrenda() {
    }

    /**
     * Elige la fábrica que corresponde al tipo de prenda.
     *
     * @param tipo tipo de prenda
     * @return la fábrica de ese tipo
     */
    public static FabricaPrenda para(TipoPrenda tipo) {
        return switch (tipo) {
            case DAMA -> new FabricaVestidoDama();
            case CABALLERO -> new FabricaTrajeCaballero();
            case DISFRAZ -> new FabricaDisfraz();
        };
    }

    /**
     * Crea una prenda después de validar los datos comunes.
     *
     * @param datos datos de la prenda
     * @return la prenda creada
     * @throws AtuendosException si falta un dato, es inválido o no corresponde al tipo
     */
    public final Prenda crearPrenda(DatosPrenda datos) throws AtuendosException {
        if (datos.tipo() != tipoProducto()) {
            throw new AtuendosException(String.format(Constantes.MSG_TIPO_INCORRECTO, tipoProducto()));
        }
        obligatorio(Constantes.CAMPO_REFERENCIA, datos.referencia());
        obligatorio(Constantes.CAMPO_COLOR, datos.color());
        obligatorio(Constantes.CAMPO_MARCA, datos.marca());
        obligatorio(Constantes.CAMPO_TALLA, datos.talla());
        if (datos.valorAlquiler() < 0) {
            throw new AtuendosException(String.format(Constantes.MSG_VALOR_NEGATIVO, Constantes.CAMPO_VALOR_ALQUILER));
        }
        return crearProducto(datos);
    }

    /**
     * Tipo de prenda que produce esta fábrica.
     *
     * @return el tipo de prenda
     */
    protected abstract TipoPrenda tipoProducto();

    /**
     * Factory Method: crea la prenda concreta con sus datos propios.
     *
     * @param datos datos de la prenda, con los comunes ya validados
     * @return la prenda concreta
     * @throws AtuendosException si falta un dato propio del tipo o es inválido
     */
    protected abstract Prenda crearProducto(DatosPrenda datos) throws AtuendosException;

    private static void obligatorio(String campo, String valor) throws AtuendosException {
        if (valor == null || valor.isBlank()) {
            throw new AtuendosException(String.format(Constantes.MSG_CAMPO_OBLIGATORIO, campo));
        }
    }
}
