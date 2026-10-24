/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Cliente;

import java.util.Optional;

/**
 * Almacén de clientes.
 */
public interface ClienteRepositorio {

    /**
     * Registra un cliente nuevo.
     *
     * @param cliente cliente a registrar
     * @throws AtuendosException si ya existe o falla la base de datos
     */
    void guardar(Cliente cliente) throws AtuendosException;

    /**
     * Busca un cliente por su identificación.
     *
     * @param identificacion documento del cliente
     * @return el cliente, o vacío si no está registrado
     * @throws AtuendosException si falla la base de datos
     */
    Optional<Cliente> buscar(String identificacion) throws AtuendosException;
}
