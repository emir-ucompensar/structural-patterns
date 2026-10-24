/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.modelo.Altura;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Pruebas de la lectura y la presentación de la terminal, sin colores.
 */
class TerminalTest {

    private final ByteArrayOutputStream capturada = new ByteArrayOutputStream();

    private Terminal terminal(String entrada) {
        return new Terminal(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(capturada, true, StandardCharsets.UTF_8), false);
    }

    private String salida() {
        return capturada.toString(StandardCharsets.UTF_8);
    }

    @Test
    void leerObligatorioRepiteHastaTenerTexto() {
        assertEquals("Ana", terminal("\n  \nAna\n").leerObligatorio("Nombre"));
        assertTrue(salida().contains("obligatorio"));
    }

    @Test
    void leerEnteroRepiteSiNoEsNumero() {
        assertEquals(7, terminal("abc\n7\n").leerEntero("Piezas"));
        assertTrue(salida().contains("entero"));
    }

    @Test
    void leerDecimalAceptaComaYPunto() {
        assertEquals(80000.5, terminal("80000,5\n").leerDecimal("Valor"));
        assertEquals(80000.5, terminal("80000.5\n").leerDecimal("Valor"));
    }

    @Test
    void leerDecimalRechazaNoNumericos() {
        assertEquals(10.0, terminal("NaN\nInfinity\n10\n").leerDecimal("Valor"));
    }

    @Test
    void leerFechaAceptaDosFormatosYRechazaInvalidas() {
        assertEquals(LocalDate.of(2026, 11, 1), terminal("01/11/2026\n").leerFecha("Fecha"));
        assertEquals(LocalDate.of(2026, 11, 1), terminal("2026-11-01\n").leerFecha("Fecha"));
        assertEquals(LocalDate.of(2026, 3, 5), terminal("31/02/2026\nxx\n5/3/2026\n").leerFecha("Fecha"));
    }

    @Test
    void confirmarEntiendeSiYNo() {
        assertTrue(terminal("s\n").confirmar("¿Seguro?"));
        assertFalse(terminal("no\n").confirmar("¿Seguro?"));
        assertTrue(terminal("tal vez\nSí\n").confirmar("¿Seguro?"));
    }

    @Test
    void elegirRepiteSiLaOpcionNoExiste() {
        assertEquals(1, terminal("5\nx\n2\n").elegir("Elige", List.of("uno", "dos")));
    }

    @Test
    void elegirVariasAceptaNumerosSeparadosPorComa() {
        assertEquals(List.of(0, 2), terminal("3, 1\n").elegirVarias("Tallas", List.of("XS", "S", "M"), 1));
    }

    @Test
    void elegirVariasRepiteSiNoCumpleElMinimoOHayErrores() {
        assertEquals(List.of(1), terminal("\n9\nx\n2\n").elegirVarias("Tallas", List.of("XS", "S", "M"), 1));
        assertTrue(salida().contains("Elige al menos 1"));
        assertTrue(salida().contains("Opción no válida"));
    }

    @Test
    void elegirEnumDevuelveElValor() {
        assertEquals(Altura.CORTO, terminal("2\n").elegirEnum("Altura", Altura.class));
    }

    @Test
    void tablaAlineaLasColumnas() {
        Terminal terminal = terminal("");
        terminal.tabla(new String[] {"Ref", "Color"},
                List.of(new String[] {"F-01", "verde"}, new String[] {"D-100", "rojo"}));
        List<String> lineas = salida().lines().toList();
        assertEquals("  Ref    Color", lineas.get(0));
        assertEquals("  -----  -----", lineas.get(1));
        assertEquals("  F-01   verde", lineas.get(2));
        assertEquals("  D-100  rojo", lineas.get(3));
    }

    @Test
    void formateaMonedaColombiana() {
        assertEquals("$ 80.000", Terminal.moneda(80000));
    }
}
