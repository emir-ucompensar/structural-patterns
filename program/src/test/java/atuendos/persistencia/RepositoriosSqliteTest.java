/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;
import atuendos.modelo.Aderezo;
import atuendos.modelo.Altura;
import atuendos.modelo.Cliente;
import atuendos.modelo.Disfraz;
import atuendos.modelo.Empleado;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoTraje;
import atuendos.modelo.TrajeCaballero;
import atuendos.modelo.VestidoDama;

import java.nio.file.Path;
import java.sql.Connection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas de los almacenes de clientes, empleados y prendas sobre una base
 * temporal.
 */
class RepositoriosSqliteTest {

    @TempDir
    Path directorio;

    private ClienteRepositorio clientes;
    private EmpleadoRepositorio empleados;
    private PrendaRepositorio prendas;

    @BeforeEach
    void abrir() throws AtuendosException {
        Connection conexion = Conexion.reiniciar("jdbc:sqlite:" + directorio.resolve("prueba.db"))
                .getConexion();
        clientes = new ClienteRepositorioSqlite(conexion);
        empleados = new EmpleadoRepositorioSqlite(conexion);
        prendas = new PrendaRepositorioSqlite(conexion);
    }

    @AfterEach
    void cerrar() {
        Conexion.cerrarInstancia();
    }

    @Test
    void guardaYBuscaUnCliente() throws AtuendosException {
        clientes.guardar(new Cliente("100", "Ana Pérez", "Calle 1", "3001112233", "ana@correo.co"));
        Cliente cliente = clientes.buscar("100").orElseThrow();
        assertEquals("Ana Pérez", cliente.getNombre());
        assertEquals("ana@correo.co", cliente.getCorreo());
    }

    @Test
    void rechazaClienteRepetido() throws AtuendosException {
        clientes.guardar(new Cliente("100", "Ana Pérez", "Calle 1", "3001112233", "ana@correo.co"));
        assertThrows(AtuendosException.class,
                () -> clientes.guardar(new Cliente("100", "Otra", "Calle 2", "3", "o@correo.co")));
    }

    @Test
    void clienteInexistenteDevuelveVacio() throws AtuendosException {
        assertTrue(clientes.buscar("999").isEmpty());
    }

    @Test
    void guardaYBuscaUnEmpleado() throws AtuendosException {
        empleados.guardar(new Empleado("200", "Luis Gómez", "Carrera 2", "3004445566", "Administrador"));
        Empleado empleado = empleados.buscar("200").orElseThrow();
        assertEquals("Luis Gómez", empleado.getNombre());
        assertEquals("Administrador", empleado.getCargo());
    }

    @Test
    void rechazaEmpleadoRepetido() throws AtuendosException {
        empleados.guardar(new Empleado("200", "Luis Gómez", "Carrera 2", "3004445566", "Administrador"));
        assertThrows(AtuendosException.class,
                () -> empleados.guardar(new Empleado("200", "Otro", "Carrera 3", "3", "Vendedor")));
    }

    @Test
    void guardaYBuscaUnVestidoDama() throws AtuendosException {
        prendas.guardar(new VestidoDama("D-01", "rojo", "Marca", "M", 80000, true, Altura.LARGO, 2));
        VestidoDama vestido = assertInstanceOf(VestidoDama.class, prendas.buscar("D-01").orElseThrow());
        assertTrue(vestido.isPedreria());
        assertEquals(Altura.LARGO, vestido.getAltura());
        assertEquals(2, vestido.getCantidadPiezas());
        assertEquals(80000, vestido.getValorAlquiler());
    }

    @Test
    void guardaYBuscaUnVestidoSinPedreria() throws AtuendosException {
        prendas.guardar(new VestidoDama("D-02", "azul", "Marca", "S", 60000, false, Altura.CORTO, 1));
        VestidoDama vestido = assertInstanceOf(VestidoDama.class, prendas.buscar("D-02").orElseThrow());
        assertEquals(false, vestido.isPedreria());
    }

    @Test
    void guardaYBuscaUnTrajeCaballero() throws AtuendosException {
        prendas.guardar(new TrajeCaballero("C-01", "negro", "Marca", "L", 90000, TipoTraje.FRAC, Aderezo.CORBATIN));
        TrajeCaballero traje = assertInstanceOf(TrajeCaballero.class, prendas.buscar("C-01").orElseThrow());
        assertEquals(TipoTraje.FRAC, traje.getTipoTraje());
        assertEquals(Aderezo.CORBATIN, traje.getAderezo());
    }

    @Test
    void guardaYBuscaUnDisfraz() throws AtuendosException {
        prendas.guardar(new Disfraz("F-01", "verde", "Marca", "S", 40000, "Dinosaurio"));
        Disfraz disfraz = assertInstanceOf(Disfraz.class, prendas.buscar("F-01").orElseThrow());
        assertEquals("Dinosaurio", disfraz.getNombre());
    }

    @Test
    void rechazaPrendaRepetida() throws AtuendosException {
        Prenda prenda = new Disfraz("F-01", "verde", "Marca", "S", 40000, "Dinosaurio");
        prendas.guardar(prenda);
        assertThrows(AtuendosException.class, () -> prendas.guardar(prenda));
    }

    @Test
    void listaClientesYEmpleadosPorNombre() throws AtuendosException {
        clientes.guardar(new Cliente("2", "Zoe", "Calle 1", "3", "z@correo.co"));
        clientes.guardar(new Cliente("1", "Ana", "Calle 2", "4", "a@correo.co"));
        empleados.guardar(new Empleado("9", "Mario", "Carrera 1", "5", "Vendedor"));
        empleados.guardar(new Empleado("8", "Berta", "Carrera 2", "6", "Cajera"));
        assertEquals("Ana", clientes.listar().get(0).getNombre());
        assertEquals(2, clientes.listar().size());
        assertEquals("Berta", empleados.listar().get(0).getNombre());
    }

    @Test
    void listaPrendasYFiltraPorTallaSinDistinguirMayusculas() throws AtuendosException {
        prendas.guardar(new Disfraz("F-02", "azul", "Marca", "s", 1000, "Pirata"));
        prendas.guardar(new VestidoDama("D-01", "rojo", "Marca", "S", 80000, true, Altura.LARGO, 2));
        prendas.guardar(new TrajeCaballero("C-01", "negro", "Marca", "L", 90000, TipoTraje.FRAC, Aderezo.CORBATA));
        assertEquals(3, prendas.listar().size());
        assertEquals(2, prendas.buscarPorTalla("S").size());
        assertEquals(2, prendas.buscarPorTalla("s").size());
        assertEquals("C-01", prendas.buscarPorTalla("L").get(0).getReferencia());
        assertTrue(prendas.buscarPorTalla("XXL").isEmpty());
    }

    @Test
    void prendaInexistenteDevuelveVacio() throws AtuendosException {
        assertTrue(prendas.buscar("X-99").isEmpty());
    }
}
