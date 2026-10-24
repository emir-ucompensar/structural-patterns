/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;
import atuendos.negocio.NegocioAlquiler;
import atuendos.persistencia.BaseTemporal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas del menú con entrada escrita, sin colores y sobre una base temporal.
 */
class MenuTest {

    private static final String CLIENTE = "1\n2\n100\nAna Pérez\nCalle 1\n300\nana@correo.co\n";
    private static final String EMPLEADO = "1\n3\n200\nLuis Gómez\nCarrera 2\n301\nAdministrador\n";
    private static final String DISFRAZ = "1\n1\n3\nF-01\nverde\nMarca\nS\n40000\nDinosaurio\n";

    @TempDir
    Path directorio;

    private NegocioAlquiler negocio;

    @BeforeEach
    void preparar() throws AtuendosException {
        Clock reloj = Clock.fixed(Instant.parse("2026-10-22T15:00:00Z"), ZoneOffset.UTC);
        negocio = NegocioAlquiler.sobre(BaseTemporal.abrir(directorio), reloj);
    }

    @AfterEach
    void cerrar() {
        BaseTemporal.cerrar();
    }

    private String ejecutar(String entrada) {
        ByteArrayOutputStream capturada = new ByteArrayOutputStream();
        Terminal terminal = new Terminal(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(capturada, true, StandardCharsets.UTF_8), false);
        new Menu(terminal, negocio).ejecutar();
        return capturada.toString(StandardCharsets.UTF_8);
    }

    @Test
    void saleConCero() {
        assertTrue(ejecutar("0\n").contains("Hasta pronto"));
    }

    @Test
    void avisaSiLaOpcionNoExiste() {
        assertTrue(ejecutar("9\nabc\n0\n").contains("Opción no válida"));
    }

    @Test
    void terminaSinErrorSiSeAcabaLaEntrada() {
        assertDoesNotThrow(() -> ejecutar(""));
        assertTrue(ejecutar("").contains("Hasta pronto"));
    }

    @Test
    void registraUnCliente() {
        String salida = ejecutar(CLIENTE + "0\n");
        assertTrue(salida.contains("Cliente registrado"));
        assertTrue(salida.contains("Ana Pérez"));
    }

    @Test
    void muestraElErrorDeUnClienteRepetidoYSigue() {
        String salida = ejecutar(CLIENTE + CLIENTE + "0\n");
        assertTrue(salida.contains("Ya existe un cliente"));
        assertTrue(salida.contains("Hasta pronto"));
    }

    @Test
    void registraUnServicioCompleto() {
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + "2\n1\n100\n200\n01/11/2026\nF-01\n\n0\n");
        assertTrue(salida.contains("Prenda registrada"));
        assertTrue(salida.contains("Servicio de alquiler registrado"));
        assertTrue(salida.contains("01/11/2026"));
    }

    @Test
    void informaCuandoLaPrendaYaEstaAlquilada() {
        String servicio = "2\n1\n100\n200\n01/11/2026\nF-01\n\n";
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + servicio + servicio + "0\n");
        assertTrue(salida.contains("no está disponible"));
    }

    @Test
    void informaSiElClienteDelServicioNoExiste() {
        String salida = ejecutar(EMPLEADO + DISFRAZ + "2\n1\n100\n200\n01/11/2026\nF-01\n\n0\n");
        assertTrue(salida.contains("El cliente 100 no está registrado"));
    }

    private static final String SERVICIO = "2\n1\n100\n200\n01/11/2026\nF-01\n\n";

    @Test
    void volverDesdeUnGrupoRegresaAlMenuPrincipal() {
        String salida = ejecutar("1\n0\n0\n");
        assertTrue(salida.contains("Registrar prenda"));
        assertTrue(salida.contains("Volver"));
        assertTrue(salida.contains("Hasta pronto"));
    }

    @Test
    void consultaUnServicioPorNumero() {
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + SERVICIO + "2\n2\n1\n0\n");
        assertTrue(salida.contains("ana@correo.co"));
        assertTrue(salida.contains("Administrador"));
        assertTrue(salida.contains("disfraz de Dinosaurio"));
    }

    @Test
    void avisaSiElServicioNoExiste() {
        assertTrue(ejecutar("2\n2\n7\n0\n").contains("No existe un servicio con el número 7"));
    }

    @Test
    void consultaLosServiciosDeUnCliente() {
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + SERVICIO + "2\n3\n100\n0\n");
        assertTrue(salida.contains("Servicios vigentes"));
        assertTrue(salida.contains("F-01"));
    }

    @Test
    void consultaLosServiciosPorFecha() {
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + SERVICIO + "2\n4\n01/11/2026\n0\n");
        assertTrue(salida.contains("Servicios del 01/11/2026"));
        assertTrue(salida.contains("Luis Gómez"));
    }

    @Test
    void avisaSiNoHayServiciosEnLaFecha() {
        assertTrue(ejecutar("2\n4\n05/12/2026\n0\n").contains("No hay servicios para mostrar"));
    }

    @Test
    void consultaLasPrendasPorTallaSeparadasPorTipo() {
        String salida = ejecutar(DISFRAZ + "3\n1\ns\n0\n");
        assertTrue(salida.contains("Vestidos de dama - talla s"));
        assertTrue(salida.contains("Sin prendas."));
        assertTrue(salida.contains("Disfraces - talla s"));
        assertTrue(salida.contains("F-01"));
    }

    @Test
    void listaLoGuardado() {
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + "3\n2\n3\n3\n3\n4\n0\n");
        assertTrue(salida.contains("Prendas registradas"));
        assertTrue(salida.contains("Clientes registrados"));
        assertTrue(salida.contains("Empleados registrados"));
        assertTrue(salida.contains("Dinosaurio"));
    }

    @Test
    void listaVaciaInformaQueNoHayRegistros() {
        String salida = ejecutar("3\n3\n0\n");
        assertTrue(salida.contains("No hay registros para mostrar"));
    }

    @Test
    void registraUnaPrendaParaLavanderiaConPrioridad() {
        String salida = ejecutar(DISFRAZ + "4\n1\nF-01\ns\n0\n");
        assertTrue(salida.contains("Prenda registrada con prioridad"));
    }

    @Test
    void registraUnaPrendaParaLavanderiaPorOrdenDeLlegada() {
        String salida = ejecutar(DISFRAZ + "4\n1\nF-01\nn\n0\n");
        assertTrue(salida.contains("Prenda registrada por orden de llegada"));
    }

    @Test
    void noRegistraDosVecesLaMismaPrendaEnLavanderia() {
        String salida = ejecutar(DISFRAZ + "4\n1\nF-01\nn\n4\n1\nF-01\nn\n0\n");
        assertTrue(salida.contains("ya está en la lista de lavandería"));
    }

    @Test
    void verLaListaPendienteNoLaEnvia() {
        String salida = ejecutar(DISFRAZ + "4\n1\nF-01\nn\n4\n2\n4\n2\n0\n");
        assertTrue(salida.contains("Prendas pendientes de envío"));
        assertFalse(salida.contains("Notificación de envío"));
        assertFalse(salida.contains("No hay prendas pendientes de envío"));
    }

    @Test
    void avisaSiNoHayPrendasPendientes() {
        assertTrue(ejecutar("4\n2\n0\n").contains("No hay prendas pendientes de envío"));
        assertTrue(ejecutar("4\n3\n5\n0\n").contains("No hay prendas pendientes de envío"));
    }

    @Test
    void enviaUnaTandaYMuestraLaNotificacion() {
        String salida = ejecutar(DISFRAZ + "4\n1\nF-01\nn\n4\n3\n5\n4\n2\n0\n");
        assertTrue(salida.contains("1 prenda enviada a lavandería el 22/10/2026"));
        assertTrue(salida.contains("Notificación de envío"));
        assertTrue(salida.contains("disfraz de Dinosaurio"));
        assertTrue(salida.contains("No hay prendas pendientes de envío"));
    }

    @Test
    void rechazaUnaCantidadNoPositiva() {
        assertTrue(ejecutar("4\n3\n0\n0\n").contains("mayor que cero"));
    }
}
