/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Empleado;

import java.util.List;
import java.util.Optional;

/**
 * Almacén de empleados.
 */
public interface EmpleadoRepositorio {

    /**
     * Registra un empleado nuevo.
     *
     * @param empleado empleado a registrar
     * @throws AtuendosException si ya existe o falla la base de datos
     */
    void guardar(Empleado empleado) throws AtuendosException;

    /**
     * Busca un empleado por su identificación.
     *
     * @param identificacion documento del empleado
     * @return el empleado, o vacío si no está registrado
     * @throws AtuendosException si falla la base de datos
     */
    Optional<Empleado> buscar(String identificacion) throws AtuendosException;

    /**
     * Lista todos los empleados registrados.
     *
     * @return los empleados ordenados por nombre
     * @throws AtuendosException si falla la base de datos
     */
    List<Empleado> listar() throws AtuendosException;
}
