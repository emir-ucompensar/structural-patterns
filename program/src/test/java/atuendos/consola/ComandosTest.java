/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Pruebas del patrón Command: el catálogo, el menú como invocador y los
 * comandos concretos ejecutados por separado.
 */
class ComandosTest {

    @TempDir
    Path directorio;

    private NegocioAlquiler negocio;
    private ByteArrayOutputStream salida;

    @BeforeEach
    void preparar() throws AtuendosException {
        Clock reloj = Clock.fixed(Instant.parse("2026-10-22T15:00:00Z"), ZoneOffset.UTC);
        negocio = NegocioAlquiler.sobre(BaseTemporal.abrir(directorio), reloj);
    }

    @AfterEach
    void cerrar() {
        BaseTemporal.cerrar();
    }

    private Terminal terminal(String entrada) {
        salida = new ByteArrayOutputStream();
        return new Terminal(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(salida, true, StandardCharsets.UTF_8), false);
    }

    private String texto() {
        return salida.toString(StandardCharsets.UTF_8);
    }

    /** Comando de prueba que cuenta sus ejecuciones y puede fallar a propósito. */
    private static final class ComandoDePrueba implements Comando {

        private final RuntimeException fallaInesperada;
        private final boolean fallaDeNegocio;
        private int ejecuciones;

        ComandoDePrueba(boolean fallaDeNegocio, RuntimeException fallaInesperada) {
            this.fallaDeNegocio = fallaDeNegocio;
            this.fallaInesperada = fallaInesperada;
        }

        @Override
        public String titulo() {
            return "Operación de prueba";
        }

        @Override
        public void ejecutar() throws AtuendosException {
            ejecuciones++;
            if (fallaDeNegocio) {
                throw new AtuendosException("Falla de negocio de prueba.");
            }
            if (fallaInesperada != null) {
                throw fallaInesperada;
            }
        }
    }

    private String ejecutarMenuCon(Comando comando) {
        Terminal consola = terminal("1\n1\n0\n");
        new Menu(consola, List.of(new GrupoComandos("Pruebas", List.of(comando)))).ejecutar();
        return texto();
    }

    @Test
    void elCatalogoArmaLosCuatroGruposConLosCatorceComandos() {
        List<GrupoComandos> grupos = CatalogoComandos.grupos(terminal(""), negocio);
        assertEquals(List.of("Registros", "Servicios de alquiler", "Listados y consultas", "Lavandería"),
                grupos.stream().map(GrupoComandos::titulo).toList());
        Set<String> titulos = new HashSet<>();
        grupos.forEach(grupo -> grupo.comandos().forEach(comando -> titulos.add(comando.titulo())));
        assertEquals(14, titulos.size());
    }

    @Test
    void elMenuEjecutaElComandoElegidoSinConocerlo() {
        ComandoDePrueba comando = new ComandoDePrueba(false, null);
        String texto = ejecutarMenuCon(comando);
        assertEquals(1, comando.ejecuciones);
        assertTrue(texto.contains("Operación de prueba"));
    }

    @Test
    void elMenuMuestraElErrorDeNegocioDeCualquierComandoYSigue() {
        String texto = ejecutarMenuCon(new ComandoDePrueba(true, null));
        assertTrue(texto.contains("Falla de negocio de prueba."));
        assertTrue(texto.contains("Hasta pronto"));
    }

    @Test
    void elMenuNoSeCaeConUnErrorInesperadoDeUnComando() {
        String texto = ejecutarMenuCon(new ComandoDePrueba(false, new IllegalStateException("interno")));
        assertTrue(texto.contains("Ocurrió un error inesperado"));
        assertTrue(texto.contains("Hasta pronto"));
    }

    @Test
    void unComandoSeEjecutaSoloSinPasarPorElMenu() throws AtuendosException {
        Terminal consola = terminal("100\nAna Pérez\nCalle 1\n300\nana@correo.co\n");
        new RegistrarClienteComando(consola, negocio).ejecutar();
        assertTrue(texto().contains("Cliente registrado"));
        assertEquals(1, negocio.listarClientes().size());
    }
}
