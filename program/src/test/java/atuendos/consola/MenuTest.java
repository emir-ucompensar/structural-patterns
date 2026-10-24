/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

    private static final String CLIENTE = "2\n100\nAna Pérez\nCalle 1\n300\nana@correo.co\n";
    private static final String EMPLEADO = "3\n200\nLuis Gómez\nCarrera 2\n301\nAdministrador\n";
    private static final String DISFRAZ = "1\n3\nF-01\nverde\nMarca\nS\n40000\nDinosaurio\n";

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
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + "4\n100\n200\n01/11/2026\nF-01\n\n0\n");
        assertTrue(salida.contains("Prenda registrada"));
        assertTrue(salida.contains("Servicio de alquiler registrado"));
        assertTrue(salida.contains("01/11/2026"));
    }

    @Test
    void informaCuandoLaPrendaYaEstaAlquilada() {
        String servicio = "4\n100\n200\n01/11/2026\nF-01\n\n";
        String salida = ejecutar(CLIENTE + EMPLEADO + DISFRAZ + servicio + servicio + "0\n");
        assertTrue(salida.contains("no está disponible"));
    }

    @Test
    void informaSiElClienteDelServicioNoExiste() {
        String salida = ejecutar(EMPLEADO + DISFRAZ + "4\n100\n200\n01/11/2026\nF-01\n\n0\n");
        assertTrue(salida.contains("El cliente 100 no está registrado"));
    }
}
