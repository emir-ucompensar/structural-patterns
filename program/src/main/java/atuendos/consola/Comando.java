/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;

/**
 * Operación del menú convertida en objeto (patrón Command). El menú, que es el
 * invocador, solo conoce esta interfaz.
 */
public interface Comando {

    /**
     * Texto con el que la operación aparece en el menú.
     *
     * @return el título de la opción
     */
    String titulo();

    /**
     * Ejecuta la operación completa: lee los datos, aplica la regla de negocio y
     * muestra el resultado.
     *
     * @throws AtuendosException si la operación no se puede completar
     */
    void ejecutar() throws AtuendosException;
}
