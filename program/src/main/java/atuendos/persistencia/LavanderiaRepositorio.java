/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
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
     * Lista las prendas pendientes de envío: primero las prioritarias y luego
     * por orden de llegada. No las envía.
     *
     * @return las prendas pendientes, en el orden de envío
     * @throws AtuendosException si falla la base de datos
     */
    List<PrendaEnLavanderia> pendientes() throws AtuendosException;

    /**
     * Envía las primeras prendas pendientes y las marca como enviadas. Si hay
     * menos de las pedidas, envía las que haya.
     *
     * @param cantidad cantidad máxima de prendas a enviar
     * @param fecha fecha del envío
     * @return las prendas enviadas, en el orden en que salieron
     * @throws AtuendosException si falla la base de datos
     */
    List<PrendaEnLavanderia> enviar(int cantidad, LocalDate fecha) throws AtuendosException;
}
