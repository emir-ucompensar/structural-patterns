/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Prenda registrada para enviar a lavandería.
 *
 * @param orden número de llegada, que desempata entre prendas de igual prioridad
 * @param prenda la prenda registrada
 * @param prioritaria si es manchada, delicada o el administrador la prioriza
 */
public record PrendaEnLavanderia(int orden, Prenda prenda, boolean prioritaria) {
}
