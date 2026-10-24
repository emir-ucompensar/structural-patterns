/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.negocio;

import atuendos.modelo.PrendaEnLavanderia;

import java.time.LocalDate;
import java.util.List;

/**
 * Notificación de una tanda enviada a lavandería.
 *
 * @param fecha fecha del envío
 * @param prendas prendas enviadas, en el orden en que salieron
 */
public record EnvioLavanderia(LocalDate fecha, List<PrendaEnLavanderia> prendas) {
}
