/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;

import org.jline.terminal.TerminalBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de la elección con flechas sobre una terminal de JLine simulada.
 */
class TerminalInteractivaTest {

    private static final String ARRIBA = "\u001B[A";
    private static final String ABAJO = "\u001B[B";
    private static final List<String> TALLAS = List.of("XS", "S", "M", "L");

    private final ByteArrayOutputStream capturada = new ByteArrayOutputStream();
    private PipedOutputStream teclado;

    /**
     * Crea una terminal cuya entrada son las teclas dadas. La entrada queda
     * abierta, como en una terminal real, hasta que la prueba la cierre.
     */
    private Terminal terminal(String teclas) throws IOException {
        cerrarTeclado();
        teclado = new PipedOutputStream();
        PipedInputStream entrada = new PipedInputStream(teclado);
        teclado.write(teclas.getBytes(StandardCharsets.UTF_8));
        teclado.flush();
        org.jline.terminal.Terminal jline = TerminalBuilder.builder()
                .streams(entrada, capturada)
                .type("xterm-256color")
                .build();
        return new Terminal(jline, new PrintStream(capturada, true, StandardCharsets.UTF_8), true);
    }

    @AfterEach
    void cerrarTeclado() throws IOException {
        if (teclado != null) {
            teclado.close();
            teclado = null;
        }
    }

    private String salida() {
        return capturada.toString(StandardCharsets.UTF_8);
    }

    @Test
    void limpiaLaPantallaYEsperaEnter() throws IOException {
        try (Terminal terminal = terminal("\r")) {
            assertTrue(terminal.limpiar());
            terminal.pausar();
            assertTrue(salida().contains("\u001B[2J"));
        }
    }

    @Test
    void eligeUnaOpcionConLasFlechas() throws IOException {
        try (Terminal terminal = terminal(ABAJO + ABAJO + "\r")) {
            assertEquals(2, terminal.elegir("Talla", TALLAS));
        }
    }

    @Test
    void laPrimeraOpcionSeEligeConEnter() throws IOException {
        try (Terminal terminal = terminal("\r")) {
            assertEquals(0, terminal.elegir("Talla", TALLAS));
        }
    }

    @Test
    void lasFlechasDanLaVueltaAlLista() throws IOException {
        try (Terminal terminal = terminal(ARRIBA + "\r")) {
            assertEquals(3, terminal.elegir("Talla", TALLAS));
        }
        capturada.reset();
        try (Terminal terminal = terminal(ABAJO + ABAJO + ABAJO + ABAJO + "\r")) {
            assertEquals(0, terminal.elegir("Talla", TALLAS));
        }
    }

    @Test
    void marcaVariasOpcionesConLaBarraEspaciadora() throws IOException {
        try (Terminal terminal = terminal(" " + ABAJO + " " + ABAJO + ABAJO + "\r")) {
            assertEquals(List.of(0, 1), terminal.elegirVarias("Tallas", TALLAS, 1));
        }
        assertTrue(salida().contains("[*] XS"));
        assertTrue(salida().contains("  XS, S"));
    }

    @Test
    void desmarcaUnaOpcionYaMarcada() throws IOException {
        try (Terminal terminal = terminal("  " + ABAJO + " \r")) {
            assertEquals(List.of(1), terminal.elegirVarias("Tallas", TALLAS, 1));
        }
    }

    @Test
    void noDejaConfirmarSinElMinimo() throws IOException {
        try (Terminal terminal = terminal("\r" + ABAJO + " \r")) {
            assertEquals(List.of(1), terminal.elegirVarias("Tallas", TALLAS, 1));
        }
        assertTrue(salida().contains("Elige al menos 1"));
    }

    @Test
    void sinTeclasTerminaLaEntrada() throws IOException {
        try (Terminal terminal = terminal("")) {
            teclado.close();
            assertThrows(NoSuchElementException.class, () -> terminal.elegir("Talla", TALLAS));
        }
    }
}
