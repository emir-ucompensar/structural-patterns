/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Vestido de dama.
 */
public class VestidoDama extends Prenda {

    private final boolean pedreria;
    private final Altura altura;
    private final int cantidadPiezas;

    /**
     * Crea un vestido de dama.
     *
     * @param referencia código que lo identifica
     * @param color color del vestido
     * @param marca marca del vestido
     * @param talla talla del vestido
     * @param valorAlquiler valor del alquiler
     * @param pedreria si lleva pedrería
     * @param altura si es largo o corto
     * @param cantidadPiezas cantidad de piezas que lo componen
     */
    public VestidoDama(String referencia, String color, String marca, String talla, double valorAlquiler,
            boolean pedreria, Altura altura, int cantidadPiezas) {
        super(referencia, color, marca, talla, valorAlquiler);
        this.pedreria = pedreria;
        this.altura = altura;
        this.cantidadPiezas = cantidadPiezas;
    }

    @Override
    public TipoPrenda getTipo() {
        return TipoPrenda.DAMA;
    }

    @Override
    public String detalle() {
        return String.format("pedrería %s, %s, %d piezas", pedreria ? "sí" : "no", altura, cantidadPiezas);
    }

    /**
     * Indica si el vestido lleva pedrería.
     *
     * @return verdadero si lleva pedrería
     */
    public boolean isPedreria() {
        return pedreria;
    }

    /**
     * Devuelve la altura del vestido.
     *
     * @return si es largo o corto
     */
    public Altura getAltura() {
        return altura;
    }

    /**
     * Devuelve la cantidad de piezas del vestido.
     *
     * @return la cantidad de piezas
     */
    public int getCantidadPiezas() {
        return cantidadPiezas;
    }
}
