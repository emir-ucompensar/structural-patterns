/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

/**
 * Pruebas del patrón Iterator sobre la cola de lavandería.
 */
class ColaLavanderiaTest {

    private static PrendaEnLavanderia registro(int orden, String referencia, boolean prioritaria) {
        return new PrendaEnLavanderia(orden, new Disfraz(referencia, "verde", "Marca", "S", 1000, "Uno"),
                prioritaria);
    }

    private static ColaLavanderia cola(PrendaEnLavanderia... registros) {
        ColaLavanderia cola = new ColaLavanderia();
        for (PrendaEnLavanderia registro : registros) {
            cola.agregar(registro);
        }
        return cola;
    }

    private static List<String> recorrer(Iterable<PrendaEnLavanderia> registros) {
        List<String> referencias = new ArrayList<>();
        for (PrendaEnLavanderia registro : registros) {
            referencias.add(registro.prenda().getReferencia());
        }
        return referencias;
    }

    @Test
    void recorrePrimeroLasPrioritariasYLuegoPorLlegada() {
        ColaLavanderia cola = cola(registro(1, "A", false), registro(2, "B", true), registro(3, "C", false),
                registro(4, "D", true));
        assertEquals(List.of("B", "D", "A", "C"), recorrer(cola));
    }

    @Test
    void cadaIteradorLlevaSuPropiaPosicion() {
        ColaLavanderia cola = cola(registro(1, "A", false), registro(2, "B", true));
        Iterator<PrendaEnLavanderia> primero = cola.iterator();
        Iterator<PrendaEnLavanderia> segundo = cola.iterator();
        assertEquals("B", primero.next().prenda().getReferencia());
        assertEquals("B", segundo.next().prenda().getReferencia());
        assertEquals("A", primero.next().prenda().getReferencia());
        assertTrue(segundo.hasNext());
        assertFalse(primero.hasNext());
    }

    @Test
    void avisaCuandoNoQuedanElementos() {
        Iterator<PrendaEnLavanderia> iterador = cola(registro(1, "A", true)).iterator();
        iterador.next();
        assertThrows(NoSuchElementException.class, iterador::next);
    }

    @Test
    void unaColaVaciaNoTieneQueRecorrer() {
        ColaLavanderia cola = new ColaLavanderia();
        assertTrue(cola.estaVacia());
        assertEquals(0, cola.tamano());
        assertFalse(cola.iterator().hasNext());
    }

    @Test
    void cuentaLosRegistrosDeLosDosGrupos() {
        ColaLavanderia cola = cola(registro(1, "A", false), registro(2, "B", true), registro(3, "C", false));
        assertEquals(3, cola.tamano());
        assertFalse(cola.estaVacia());
    }
}
