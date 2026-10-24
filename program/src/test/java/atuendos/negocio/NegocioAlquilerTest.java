/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.fabrica.DatosPrenda;
import atuendos.modelo.Prenda;
import atuendos.modelo.ServicioAlquiler;
import atuendos.modelo.TipoPrenda;
import atuendos.persistencia.BaseTemporal;

import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas de la fachada: registro de personas, prendas y servicios.
 */
class NegocioAlquilerTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 22);
    private static final LocalDate ALQUILER = LocalDate.of(2026, 11, 1);

    @TempDir
    Path directorio;

    private NegocioAlquiler negocio;

    @BeforeEach
    void preparar() throws AtuendosException {
        Clock reloj = Clock.fixed(Instant.parse("2026-10-22T15:00:00Z"), ZoneOffset.UTC);
        negocio = NegocioAlquiler.sobre(BaseTemporal.abrir(directorio), reloj);
        negocio.registrarCliente("100", "Ana Pérez", "Calle 1", "300", "ana@correo.co");
        negocio.registrarEmpleado("200", "Luis Gómez", "Carrera 2", "301", "Administrador");
        negocio.registrarPrenda(disfraz("F-01"));
        negocio.registrarPrenda(disfraz("F-02"));
    }

    @AfterEach
    void cerrar() {
        BaseTemporal.cerrar();
    }

    private static DatosPrenda disfraz(String referencia) {
        return new DatosPrenda(TipoPrenda.DISFRAZ, referencia, "verde", "Marca", "S", 40000,
                Map.of(Constantes.CAMPO_NOMBRE_DISFRAZ, "Dinosaurio"));
    }

    @Test
    void registraUnServicioConFechaDelSistemaYConsecutivo() throws AtuendosException {
        ServicioAlquiler uno = negocio.registrarServicio("100", "200", List.of("F-01", "F-02"), ALQUILER);
        ServicioAlquiler dos = negocio.registrarServicio("100", "200", List.of("F-01"), ALQUILER.plusDays(1));
        assertEquals(1, uno.getNumero());
        assertEquals(2, dos.getNumero());
        assertEquals(HOY, uno.getFechaSolicitud());
        assertEquals(2, uno.getPrendas().size());
    }

    @Test
    void rechazaPrendaNoDisponibleParaLaFecha() throws AtuendosException {
        negocio.registrarServicio("100", "200", List.of("F-01"), ALQUILER);
        assertThrows(AtuendosException.class,
                () -> negocio.registrarServicio("100", "200", List.of("F-01"), ALQUILER));
        negocio.registrarServicio("100", "200", List.of("F-01"), ALQUILER.plusDays(1));
    }

    @Test
    void rechazaClienteEmpleadoOPrendaNoRegistrados() {
        assertThrows(AtuendosException.class,
                () -> negocio.registrarServicio("999", "200", List.of("F-01"), ALQUILER));
        assertThrows(AtuendosException.class,
                () -> negocio.registrarServicio("100", "999", List.of("F-01"), ALQUILER));
        assertThrows(AtuendosException.class,
                () -> negocio.registrarServicio("100", "200", List.of("X-99"), ALQUILER));
    }

    @Test
    void rechazaServicioSinPrendasORepetidas() {
        assertThrows(AtuendosException.class,
                () -> negocio.registrarServicio("100", "200", List.of(), ALQUILER));
        assertThrows(AtuendosException.class,
                () -> negocio.registrarServicio("100", "200", List.of("F-01", "F-01"), ALQUILER));
    }

    @Test
    void rechazaFechaDeAlquilerPasada() {
        assertThrows(AtuendosException.class,
                () -> negocio.registrarServicio("100", "200", List.of("F-01"), HOY.minusDays(1)));
    }

    @Test
    void aceptaAlquilerParaHoy() throws AtuendosException {
        assertEquals(1, negocio.registrarServicio("100", "200", List.of("F-01"), HOY).getNumero());
    }

    @Test
    void rechazaPersonasConDatosVacios() {
        assertThrows(AtuendosException.class,
                () -> negocio.registrarCliente("101", " ", "Calle 1", "300", "x@correo.co"));
        assertThrows(AtuendosException.class,
                () -> negocio.registrarEmpleado("201", "Nombre", "Calle 1", "300", ""));
    }

    @Test
    void consultaUnServicioPorNumero() throws AtuendosException {
        negocio.registrarServicio("100", "200", List.of("F-01"), ALQUILER);
        assertEquals("Ana Pérez", negocio.consultarServicio(1).getCliente().getNombre());
        assertThrows(AtuendosException.class, () -> negocio.consultarServicio(99));
    }

    @Test
    void consultaLosServiciosVigentesDeUnClienteOrdenadosPorFecha() throws AtuendosException {
        negocio.registrarServicio("100", "200", List.of("F-01"), ALQUILER.plusDays(3));
        negocio.registrarServicio("100", "200", List.of("F-02"), ALQUILER);
        ServiciosDeCliente resultado = negocio.consultarServiciosDeCliente("100");
        assertEquals("Ana Pérez", resultado.cliente().getNombre());
        assertEquals(2, resultado.vigentes().size());
        assertEquals(ALQUILER, resultado.vigentes().get(0).getFechaAlquiler());
        assertThrows(AtuendosException.class, () -> negocio.consultarServiciosDeCliente("999"));
    }

    @Test
    void consultaServiciosPorFecha() throws AtuendosException {
        negocio.registrarServicio("100", "200", List.of("F-01"), ALQUILER);
        assertEquals(1, negocio.consultarServiciosPorFecha(ALQUILER).size());
        assertEquals(0, negocio.consultarServiciosPorFecha(ALQUILER.plusDays(1)).size());
    }

    @Test
    void consultaPrendasPorTallaSeparadasPorTipo() throws AtuendosException {
        Map<TipoPrenda, List<Prenda>> porTipo = negocio.consultarPrendasPorTalla("s");
        assertEquals(2, porTipo.get(TipoPrenda.DISFRAZ).size());
        assertEquals(0, porTipo.get(TipoPrenda.DAMA).size());
        assertEquals(3, porTipo.size());
        assertThrows(AtuendosException.class, () -> negocio.consultarPrendasPorTalla(" "));
    }

    @Test
    void listaLoRegistrado() throws AtuendosException {
        assertEquals(1, negocio.listarClientes().size());
        assertEquals(1, negocio.listarEmpleados().size());
        assertEquals(2, negocio.listarPrendas().size());
    }

    @Test
    void registraVeYEnviaPrendasDeLavanderia() throws AtuendosException {
        negocio.registrarEnLavanderia("F-01", false);
        negocio.registrarEnLavanderia("F-02", true);
        assertEquals("F-02", negocio.listarLavanderiaPendiente().get(0).prenda().getReferencia());
        assertEquals(2, negocio.listarLavanderiaPendiente().size());
        EnvioLavanderia envio = negocio.enviarALavanderia(1);
        assertEquals(HOY, envio.fecha());
        assertEquals("F-02", envio.prendas().get(0).prenda().getReferencia());
        assertEquals(1, negocio.listarLavanderiaPendiente().size());
    }

    @Test
    void rechazaLavanderiaConDatosInvalidos() throws AtuendosException {
        assertThrows(AtuendosException.class, () -> negocio.registrarEnLavanderia("X-99", false));
        assertThrows(AtuendosException.class, () -> negocio.registrarEnLavanderia(" ", false));
        negocio.registrarEnLavanderia("F-01", false);
        assertThrows(AtuendosException.class, () -> negocio.registrarEnLavanderia("F-01", true));
        assertThrows(AtuendosException.class, () -> negocio.enviarALavanderia(0));
        assertThrows(AtuendosException.class, () -> negocio.enviarALavanderia(-2));
    }

    @Test
    void rechazaPrendaSinTipo() {
        DatosPrenda sinTipo = new DatosPrenda(null, "Z-01", "rojo", "Marca", "S", 1, Map.of());
        assertThrows(AtuendosException.class, () -> negocio.registrarPrenda(sinTipo));
    }
}
