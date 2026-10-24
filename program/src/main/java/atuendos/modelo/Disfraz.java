/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Disfraz.
 */
public class Disfraz extends Prenda {

    private final String nombre;

    /**
     * Crea un disfraz.
     *
     * @param referencia código que lo identifica
     * @param color color del disfraz
     * @param marca marca del disfraz
     * @param talla talla del disfraz
     * @param valorAlquiler valor del alquiler
     * @param nombre nombre del disfraz
     */
    public Disfraz(String referencia, String color, String marca, String talla, double valorAlquiler,
            String nombre) {
        super(referencia, color, marca, talla, valorAlquiler);
        this.nombre = nombre;
    }

    @Override
    public TipoPrenda getTipo() {
        return TipoPrenda.DISFRAZ;
    }

    @Override
    public String detalle() {
        return "disfraz de " + nombre;
    }

    /**
     * Devuelve el nombre del disfraz.
     *
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }
}
