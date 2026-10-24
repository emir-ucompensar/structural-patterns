/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Cliente;
import atuendos.modelo.Empleado;
import atuendos.modelo.Prenda;
import atuendos.modelo.ServicioAlquiler;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Almacén de servicios de alquiler.
 */
public interface ServicioRepositorio {

    /**
     * Guarda un servicio nuevo con sus prendas y le asigna el siguiente
     * consecutivo. Si algo falla no queda nada guardado.
     *
     * @param fechaSolicitud fecha del sistema en que se registra
     * @param fechaAlquiler fecha para la que se alquilan las prendas
     * @param empleado empleado que atiende
     * @param cliente cliente que alquila
     * @param prendas prendas alquiladas
     * @return el servicio guardado, con su número
     * @throws AtuendosException si falla la base de datos
     */
    ServicioAlquiler guardar(LocalDate fechaSolicitud, LocalDate fechaAlquiler, Empleado empleado,
            Cliente cliente, List<Prenda> prendas) throws AtuendosException;

    /**
     * Busca un servicio por su número.
     *
     * @param numero consecutivo del servicio
     * @return el servicio, o vacío si no existe
     * @throws AtuendosException si falla la base de datos
     */
    Optional<ServicioAlquiler> buscar(int numero) throws AtuendosException;

    /**
     * Indica si una prenda ya está alquilada para una fecha.
     *
     * @param referencia código de la prenda
     * @param fecha fecha de alquiler
     * @return verdadero si algún servicio la incluye para esa fecha
     * @throws AtuendosException si falla la base de datos
     */
    boolean estaAlquilada(String referencia, LocalDate fecha) throws AtuendosException;

    /**
     * Lista los servicios de un cliente cuya fecha de alquiler es igual o
     * posterior a una fecha, ordenados por fecha de alquiler.
     *
     * @param clienteId identificación del cliente
     * @param desde primera fecha de alquiler que se incluye
     * @return los servicios vigentes del cliente
     * @throws AtuendosException si falla la base de datos
     */
    List<ServicioAlquiler> buscarPorCliente(String clienteId, LocalDate desde) throws AtuendosException;

    /**
     * Lista los servicios con una fecha de alquiler.
     *
     * @param fecha fecha de alquiler
     * @return los servicios de esa fecha, por número
     * @throws AtuendosException si falla la base de datos
     */
    List<ServicioAlquiler> buscarPorFecha(LocalDate fecha) throws AtuendosException;
}
