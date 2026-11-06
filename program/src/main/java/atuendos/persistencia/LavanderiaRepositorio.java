/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.modelo.ColaLavanderia;
import atuendos.modelo.Prenda;
import atuendos.modelo.PrendaEnLavanderia;

import java.time.LocalDate;
import java.util.List;

/**
 * Lista de prendas pendientes de envío a lavandería.
 */
public interface LavanderiaRepositorio {

    /**
     * Registra una prenda en la lista, con o sin prioridad.
     *
     * @param prenda prenda registrada en el sistema
     * @param prioritaria si va antes que las demás
     * @return el registro creado, con su número de llegada
     * @throws AtuendosException si la prenda ya está pendiente o falla la base de datos
     */
    PrendaEnLavanderia registrar(Prenda prenda, boolean prioritaria) throws AtuendosException;

    /**
     * Entrega las prendas pendientes de envío dentro de una cola, que las
     * recorre en el orden de envío. No las envía.
     *
     * @return la cola de prendas pendientes
     * @throws AtuendosException si falla la base de datos
     */
    ColaLavanderia pendientes() throws AtuendosException;

    /**
     * Marca como enviados los registros dados, todos o ninguno.
     *
     * @param registros registros que salen hacia la lavandería
     * @param fecha fecha del envío
     * @throws AtuendosException si falla la base de datos
     */
    void marcarEnviadas(List<PrendaEnLavanderia> registros, LocalDate fecha) throws AtuendosException;
}
