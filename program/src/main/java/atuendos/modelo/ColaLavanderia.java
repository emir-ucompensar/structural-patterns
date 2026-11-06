/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Lista de prendas pendientes de lavandería (colección concreta del patrón
 * Iterator). Guarda las prioritarias y las demás en dos listas, y su iterador
 * las entrega en el orden de envío del caso: primero las prioritarias y luego
 * las demás, cada grupo por orden de llegada. Quien la recorre no conoce esa
 * estructura.
 */
public class ColaLavanderia implements Iterable<PrendaEnLavanderia> {

    private final List<PrendaEnLavanderia> prioritarias = new ArrayList<>();
    private final List<PrendaEnLavanderia> porLlegada = new ArrayList<>();

    /**
     * Crea una cola vacía.
     */
    public ColaLavanderia() {
    }

    /**
     * Agrega un registro al final de su grupo. Los registros deben llegar en su
     * orden de llegada.
     *
     * @param registro registro de lavandería a agregar
     */
    public void agregar(PrendaEnLavanderia registro) {
        if (registro.prioritaria()) {
            prioritarias.add(registro);
        } else {
            porLlegada.add(registro);
        }
    }

    /**
     * Entrega un iterador nuevo que recorre la cola en el orden de envío. Cada
     * iterador lleva su propia posición, así que se pueden hacer varios
     * recorridos a la vez.
     *
     * @return el iterador de la cola
     */
    @Override
    public Iterator<PrendaEnLavanderia> iterator() {
        return new IteradorLavanderia();
    }

    /**
     * Cuenta los registros pendientes.
     *
     * @return la cantidad de registros de la cola
     */
    public int tamano() {
        return prioritarias.size() + porLlegada.size();
    }

    /**
     * Indica si no hay registros pendientes.
     *
     * @return verdadero si la cola está vacía
     */
    public boolean estaVacia() {
        return tamano() == 0;
    }

    /**
     * Iterador concreto: recorre primero la lista de prioritarias y después la
     * de llegada, con una sola posición que avanza sobre las dos.
     */
    private final class IteradorLavanderia implements Iterator<PrendaEnLavanderia> {

        private int posicion;

        @Override
        public boolean hasNext() {
            return posicion < tamano();
        }

        @Override
        public PrendaEnLavanderia next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            int indice = posicion++;
            if (indice < prioritarias.size()) {
                return prioritarias.get(indice);
            }
            return porLlegada.get(indice - prioritarias.size());
        }
    }
}
