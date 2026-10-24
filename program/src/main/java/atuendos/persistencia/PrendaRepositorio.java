/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Prenda;

import java.util.List;
import java.util.Optional;

/**
 * Almacén de prendas.
 */
public interface PrendaRepositorio {

    /**
     * Registra una prenda nueva.
     *
     * @param prenda prenda a registrar
     * @throws AtuendosException si ya existe o falla la base de datos
     */
    void guardar(Prenda prenda) throws AtuendosException;

    /**
     * Busca una prenda por su referencia.
     *
     * @param referencia código de la prenda
     * @return la prenda, o vacío si no está registrada
     * @throws AtuendosException si falla la base de datos
     */
    Optional<Prenda> buscar(String referencia) throws AtuendosException;

    /**
     * Lista todas las prendas registradas.
     *
     * @return las prendas ordenadas por tipo y referencia
     * @throws AtuendosException si falla la base de datos
     */
    List<Prenda> listar() throws AtuendosException;

    /**
     * Lista las prendas de una o varias tallas, sin distinguir mayúsculas.
     *
     * @param tallas tallas buscadas
     * @return las prendas ordenadas por tipo y referencia
     * @throws AtuendosException si falla la base de datos
     */
    List<Prenda> buscarPorTallas(List<String> tallas) throws AtuendosException;
}
