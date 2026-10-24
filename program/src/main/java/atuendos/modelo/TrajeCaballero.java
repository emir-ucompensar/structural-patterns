/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Traje de caballero.
 */
public class TrajeCaballero extends Prenda {

    private final TipoTraje tipoTraje;
    private final Aderezo aderezo;

    /**
     * Crea un traje de caballero.
     *
     * @param referencia código que lo identifica
     * @param color color del traje
     * @param marca marca del traje
     * @param talla talla del traje
     * @param valorAlquiler valor del alquiler
     * @param tipoTraje tipo de traje
     * @param aderezo aderezo que lo acompaña
     */
    public TrajeCaballero(String referencia, String color, String marca, String talla, double valorAlquiler,
            TipoTraje tipoTraje, Aderezo aderezo) {
        super(referencia, color, marca, talla, valorAlquiler);
        this.tipoTraje = tipoTraje;
        this.aderezo = aderezo;
    }

    @Override
    public TipoPrenda getTipo() {
        return TipoPrenda.CABALLERO;
    }

    @Override
    public String detalle() {
        return String.format("traje %s, %s", tipoTraje, aderezo);
    }

    public TipoTraje getTipoTraje() {
        return tipoTraje;
    }

    public Aderezo getAderezo() {
        return aderezo;
    }
}
