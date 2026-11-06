/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.negocio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;
import atuendos.modelo.ColaLavanderia;
import atuendos.modelo.Disfraz;
import atuendos.modelo.Prenda;
import atuendos.modelo.PrendaEnLavanderia;
import atuendos.persistencia.LavanderiaRepositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del patrón Observer en el despacho de lavandería, con un almacén en
 * memoria y suscriptores de prueba.
 */
class DespachoLavanderiaTest {

    private static final LocalDate HOY = LocalDate.of(2026, 10, 22);

    /** Almacén en memoria con el mismo contrato que el de SQLite. */
    private static final class LavanderiaEnMemoria implements LavanderiaRepositorio {

        private final List<PrendaEnLavanderia> pendientes = new ArrayList<>();
        private int orden;

        @Override
        public PrendaEnLavanderia registrar(Prenda prenda, boolean prioritaria) {
            PrendaEnLavanderia registro = new PrendaEnLavanderia(++orden, prenda, prioritaria);
            pendientes.add(registro);
            return registro;
        }

        @Override
        public ColaLavanderia pendientes() {
            ColaLavanderia cola = new ColaLavanderia();
            pendientes.forEach(cola::agregar);
            return cola;
        }

        @Override
        public void marcarEnviadas(List<PrendaEnLavanderia> registros, LocalDate fecha) {
            pendientes.removeAll(registros);
        }
    }

    /** Suscriptor que guarda los envíos que recibe. */
    private static final class Registro implements ObservadorLavanderia {

        private final List<EnvioLavanderia> recibidos = new ArrayList<>();

        @Override
        public void prendasEnviadas(EnvioLavanderia envio) {
            recibidos.add(envio);
        }
    }

    private DespachoLavanderia despacho;

    private static Prenda prenda(String referencia) {
        return new Disfraz(referencia, "verde", "Marca", "S", 1000, "Uno");
    }

    @BeforeEach
    void preparar() throws AtuendosException {
        despacho = new DespachoLavanderia(new LavanderiaEnMemoria());
        despacho.registrar(prenda("A"), false);
        despacho.registrar(prenda("B"), true);
        despacho.registrar(prenda("C"), false);
    }

    private static List<String> referencias(EnvioLavanderia envio) {
        return envio.prendas().stream().map(registro -> registro.prenda().getReferencia()).toList();
    }

    @Test
    void avisaACadaSuscriptorConElMismoEnvio() throws AtuendosException {
        Registro primero = new Registro();
        Registro segundo = new Registro();
        despacho.suscribir(primero);
        despacho.suscribir(segundo);
        EnvioLavanderia envio = despacho.enviar(2, HOY);
        assertEquals(1, primero.recibidos.size());
        assertSame(envio, primero.recibidos.get(0));
        assertSame(envio, segundo.recibidos.get(0));
    }

    @Test
    void enviaEnElOrdenDeLaColaYRespetaLaCantidad() throws AtuendosException {
        EnvioLavanderia envio = despacho.enviar(2, HOY);
        assertEquals(List.of("B", "A"), referencias(envio));
        assertEquals(HOY, envio.fecha());
        assertEquals(1, despacho.pendientes().tamano());
    }

    @Test
    void noAvisaSiNoSaleNingunaPrenda() throws AtuendosException {
        Registro suscriptor = new Registro();
        despacho.suscribir(suscriptor);
        despacho.enviar(3, HOY);
        EnvioLavanderia vacio = despacho.enviar(3, HOY);
        assertTrue(vacio.prendas().isEmpty());
        assertEquals(1, suscriptor.recibidos.size());
    }

    @Test
    void quienSeRetiraDejaDeRecibirAvisos() throws AtuendosException {
        Registro suscriptor = new Registro();
        despacho.suscribir(suscriptor);
        despacho.enviar(1, HOY);
        despacho.desuscribir(suscriptor);
        despacho.enviar(1, HOY);
        assertEquals(1, suscriptor.recibidos.size());
    }

    @Test
    void sinSuscriptoresElEnvioIgualSale() throws AtuendosException {
        assertEquals(3, despacho.enviar(5, HOY).prendas().size());
        assertTrue(despacho.pendientes().estaVacia());
    }

    @Test
    void rechazaUnaCantidadNoPositiva() {
        assertThrows(AtuendosException.class, () -> despacho.enviar(0, HOY));
        assertThrows(AtuendosException.class, () -> despacho.enviar(-1, HOY));
    }
}
