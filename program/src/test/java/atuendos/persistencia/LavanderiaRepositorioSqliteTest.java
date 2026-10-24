/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Disfraz;
import atuendos.modelo.Prenda;
import atuendos.modelo.PrendaEnLavanderia;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas de la lista de lavandería sobre una base temporal.
 */
class LavanderiaRepositorioSqliteTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 22);

    @TempDir
    Path directorio;

    private LavanderiaRepositorio lavanderia;
    private final Prenda a = new Disfraz("A", "verde", "Marca", "S", 1000, "Uno");
    private final Prenda b = new Disfraz("B", "azul", "Marca", "S", 1000, "Dos");
    private final Prenda c = new Disfraz("C", "rojo", "Marca", "S", 1000, "Tres");
    private final Prenda d = new Disfraz("D", "negro", "Marca", "S", 1000, "Cuatro");

    @BeforeEach
    void abrir() throws AtuendosException {
        var conexion = BaseTemporal.abrir(directorio);
        PrendaRepositorio prendas = new PrendaRepositorioSqlite(conexion);
        for (Prenda prenda : List.of(a, b, c, d)) {
            prendas.guardar(prenda);
        }
        lavanderia = new LavanderiaRepositorioSqlite(conexion, prendas);
    }

    @AfterEach
    void cerrar() {
        BaseTemporal.cerrar();
    }

    private static List<String> referencias(List<PrendaEnLavanderia> registros) {
        return registros.stream().map(registro -> registro.prenda().getReferencia()).toList();
    }

    @Test
    void ordenaPrimeroLasPrioritariasYLuegoPorLlegada() throws AtuendosException {
        lavanderia.registrar(a, false);
        lavanderia.registrar(b, true);
        lavanderia.registrar(c, false);
        lavanderia.registrar(d, true);
        assertEquals(List.of("B", "D", "A", "C"), referencias(lavanderia.pendientes()));
    }

    @Test
    void verLaListaNoEnviaNada() throws AtuendosException {
        lavanderia.registrar(a, false);
        lavanderia.pendientes();
        lavanderia.pendientes();
        assertEquals(1, lavanderia.pendientes().size());
    }

    @Test
    void rechazaUnaPrendaYaPendiente() throws AtuendosException {
        lavanderia.registrar(a, false);
        assertThrows(AtuendosException.class, () -> lavanderia.registrar(a, true));
        assertEquals(1, lavanderia.pendientes().size());
    }

    @Test
    void enviaLaTandaEnElOrdenYDejaElResto() throws AtuendosException {
        lavanderia.registrar(a, false);
        lavanderia.registrar(b, true);
        lavanderia.registrar(c, false);
        List<PrendaEnLavanderia> enviadas = lavanderia.enviar(2, HOY);
        assertEquals(List.of("B", "A"), referencias(enviadas));
        assertEquals(List.of("C"), referencias(lavanderia.pendientes()));
    }

    @Test
    void enviaLasQueHayCuandoPidenMas() throws AtuendosException {
        lavanderia.registrar(a, false);
        assertEquals(1, lavanderia.enviar(10, HOY).size());
        assertTrue(lavanderia.pendientes().isEmpty());
    }

    @Test
    void sinPendientesNoEnviaNada() throws AtuendosException {
        assertTrue(lavanderia.enviar(3, HOY).isEmpty());
    }

    @Test
    void unaPrendaEnviadaPuedeVolverAlRegistrarse() throws AtuendosException {
        lavanderia.registrar(a, false);
        lavanderia.enviar(1, HOY);
        PrendaEnLavanderia otra = lavanderia.registrar(a, true);
        assertTrue(otra.prioritaria());
        assertFalse(lavanderia.pendientes().isEmpty());
    }
}
