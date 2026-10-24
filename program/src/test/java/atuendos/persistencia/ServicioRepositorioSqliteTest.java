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
import atuendos.modelo.Cliente;
import atuendos.modelo.Disfraz;
import atuendos.modelo.Empleado;
import atuendos.modelo.Prenda;
import atuendos.modelo.ServicioAlquiler;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas del almacén de servicios sobre una base temporal.
 */
class ServicioRepositorioSqliteTest {

    private static final LocalDate SOLICITUD = LocalDate.of(2026, 10, 22);
    private static final LocalDate ALQUILER = LocalDate.of(2026, 11, 1);

    @TempDir
    Path directorio;

    private ServicioRepositorio servicios;
    private final Cliente cliente = new Cliente("100", "Ana Pérez", "Calle 1", "300", "ana@correo.co");
    private final Empleado empleado = new Empleado("200", "Luis Gómez", "Carrera 2", "301", "Administrador");
    private final Prenda primera = new Disfraz("F-01", "verde", "Marca", "S", 40000, "Dinosaurio");
    private final Prenda segunda = new Disfraz("F-02", "azul", "Marca", "M", 45000, "Pirata");

    @BeforeEach
    void abrir() throws AtuendosException {
        var conexion = BaseTemporal.abrir(directorio);
        ClienteRepositorio clientes = new ClienteRepositorioSqlite(conexion);
        EmpleadoRepositorio empleados = new EmpleadoRepositorioSqlite(conexion);
        PrendaRepositorio prendas = new PrendaRepositorioSqlite(conexion);
        servicios = new ServicioRepositorioSqlite(conexion, clientes, empleados, prendas);
        clientes.guardar(cliente);
        empleados.guardar(empleado);
        prendas.guardar(primera);
        prendas.guardar(segunda);
    }

    @AfterEach
    void cerrar() {
        BaseTemporal.cerrar();
    }

    @Test
    void asignaConsecutivosYGuardaLasPrendas() throws AtuendosException {
        ServicioAlquiler uno = servicios.guardar(SOLICITUD, ALQUILER, empleado, cliente, List.of(primera, segunda));
        ServicioAlquiler dos = servicios.guardar(SOLICITUD, ALQUILER.plusDays(1), empleado, cliente, List.of(primera));
        assertEquals(1, uno.getNumero());
        assertEquals(2, dos.getNumero());
        ServicioAlquiler leido = servicios.buscar(1).orElseThrow();
        assertEquals(SOLICITUD, leido.getFechaSolicitud());
        assertEquals(ALQUILER, leido.getFechaAlquiler());
        assertEquals("Ana Pérez", leido.getCliente().getNombre());
        assertEquals("Luis Gómez", leido.getEmpleado().getNombre());
        assertEquals(2, leido.getPrendas().size());
    }

    @Test
    void servicioInexistenteDevuelveVacio() throws AtuendosException {
        assertTrue(servicios.buscar(99).isEmpty());
    }

    @Test
    void detectaPrendasAlquiladasPorFecha() throws AtuendosException {
        servicios.guardar(SOLICITUD, ALQUILER, empleado, cliente, List.of(primera));
        assertTrue(servicios.estaAlquilada("F-01", ALQUILER));
        assertFalse(servicios.estaAlquilada("F-01", ALQUILER.plusDays(1)));
        assertFalse(servicios.estaAlquilada("F-02", ALQUILER));
    }

    @Test
    void siFallaNoQuedaNadaGuardado() throws AtuendosException {
        Prenda noRegistrada = new Disfraz("X-99", "rojo", "Marca", "S", 1000, "Fantasma");
        assertThrows(AtuendosException.class,
                () -> servicios.guardar(SOLICITUD, ALQUILER, empleado, cliente, List.of(primera, noRegistrada)));
        assertTrue(servicios.buscar(1).isEmpty());
        assertFalse(servicios.estaAlquilada("F-01", ALQUILER));
        assertEquals(1, servicios.guardar(SOLICITUD, ALQUILER, empleado, cliente, List.of(primera)).getNumero());
    }
}
