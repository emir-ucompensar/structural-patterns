/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.negocio;

import atuendos.modelo.Cliente;
import atuendos.modelo.ServicioAlquiler;

import java.util.List;

/**
 * Resultado de consultar un cliente: sus datos y sus servicios vigentes.
 *
 * @param cliente el cliente consultado
 * @param vigentes servicios con fecha de alquiler de hoy en adelante, por fecha
 */
public record ServiciosDeCliente(Cliente cliente, List<ServicioAlquiler> vigentes) {
}
