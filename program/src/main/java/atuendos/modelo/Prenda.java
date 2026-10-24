/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Prenda que se alquila. Cada tipo de prenda agrega sus propios datos.
 */
public abstract class Prenda {

    private final String referencia;
    private final String color;
    private final String marca;
    private final String talla;
    private final double valorAlquiler;

    /**
     * Crea una prenda con los datos comunes a todos los tipos.
     *
     * @param referencia código que la identifica
     * @param color color de la prenda
     * @param marca marca de la prenda
     * @param talla talla de la prenda
     * @param valorAlquiler valor del alquiler
     */
    protected Prenda(String referencia, String color, String marca, String talla, double valorAlquiler) {
        this.referencia = referencia;
        this.color = color;
        this.marca = marca;
        this.talla = talla;
        this.valorAlquiler = valorAlquiler;
    }

    /**
     * Indica el tipo de la prenda.
     *
     * @return el tipo de prenda
     */
    public abstract TipoPrenda getTipo();

    /**
     * Describe los datos propios del tipo de prenda.
     *
     * @return los datos específicos en una línea
     */
    public abstract String detalle();

    /**
     * Devuelve el código que identifica a la prenda.
     *
     * @return la referencia
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Devuelve el color de la prenda.
     *
     * @return el color
     */
    public String getColor() {
        return color;
    }

    /**
     * Devuelve la marca de la prenda.
     *
     * @return la marca
     */
    public String getMarca() {
        return marca;
    }

    /**
     * Devuelve la talla de la prenda.
     *
     * @return la talla
     */
    public String getTalla() {
        return talla;
    }

    /**
     * Devuelve el valor del alquiler de la prenda.
     *
     * @return el valor del alquiler
     */
    public double getValorAlquiler() {
        return valorAlquiler;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s, %s, talla %s, alquiler %.2f, %s", getTipo(), referencia,
                color, marca, talla, valorAlquiler, detalle());
    }
}
