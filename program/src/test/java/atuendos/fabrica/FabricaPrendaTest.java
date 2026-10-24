/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.fabrica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Aderezo;
import atuendos.modelo.Altura;
import atuendos.modelo.Disfraz;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;
import atuendos.modelo.TipoTraje;
import atuendos.modelo.TrajeCaballero;
import atuendos.modelo.VestidoDama;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Pruebas del Factory Method que crea las prendas.
 */
class FabricaPrendaTest {

    private static DatosPrenda dama(Map<String, String> especificos) {
        return new DatosPrenda(TipoPrenda.DAMA, "D-01", "rojo", "Marca", "M", 80000, especificos);
    }

    private static Map<String, String> especificosDama() {
        return Map.of(Constantes.CAMPO_PEDRERIA, "true", Constantes.CAMPO_ALTURA, "largo",
                Constantes.CAMPO_CANTIDAD_PIEZAS, "2");
    }

    @Test
    void creaVestidoDama() throws AtuendosException {
        Prenda prenda = FabricaPrenda.para(TipoPrenda.DAMA).crearPrenda(dama(especificosDama()));
        VestidoDama vestido = assertInstanceOf(VestidoDama.class, prenda);
        assertEquals("D-01", vestido.getReferencia());
        assertTrue(vestido.isPedreria());
        assertEquals(Altura.LARGO, vestido.getAltura());
        assertEquals(2, vestido.getCantidadPiezas());
        assertEquals(TipoPrenda.DAMA, vestido.getTipo());
    }

    @Test
    void creaTrajeCaballero() throws AtuendosException {
        DatosPrenda datos = new DatosPrenda(TipoPrenda.CABALLERO, "C-01", "negro", "Marca", "L", 90000,
                Map.of(Constantes.CAMPO_TIPO_TRAJE, "FRAC", Constantes.CAMPO_ADEREZO, "corbatin"));
        TrajeCaballero traje = assertInstanceOf(TrajeCaballero.class,
                FabricaPrenda.para(TipoPrenda.CABALLERO).crearPrenda(datos));
        assertEquals(TipoTraje.FRAC, traje.getTipoTraje());
        assertEquals(Aderezo.CORBATIN, traje.getAderezo());
    }

    @Test
    void creaDisfraz() throws AtuendosException {
        DatosPrenda datos = new DatosPrenda(TipoPrenda.DISFRAZ, "F-01", "verde", "Marca", "S", 40000,
                Map.of(Constantes.CAMPO_NOMBRE_DISFRAZ, "Dinosaurio"));
        Disfraz disfraz = assertInstanceOf(Disfraz.class,
                FabricaPrenda.para(TipoPrenda.DISFRAZ).crearPrenda(datos));
        assertEquals("Dinosaurio", disfraz.getNombre());
    }

    @Test
    void cadaTipoUsaSuFabrica() {
        assertInstanceOf(FabricaVestidoDama.class, FabricaPrenda.para(TipoPrenda.DAMA));
        assertInstanceOf(FabricaTrajeCaballero.class, FabricaPrenda.para(TipoPrenda.CABALLERO));
        assertInstanceOf(FabricaDisfraz.class, FabricaPrenda.para(TipoPrenda.DISFRAZ));
    }

    @Test
    void rechazaReferenciaVacia() {
        DatosPrenda datos = new DatosPrenda(TipoPrenda.DAMA, " ", "rojo", "Marca", "M", 80000, especificosDama());
        assertThrows(AtuendosException.class, () -> FabricaPrenda.para(TipoPrenda.DAMA).crearPrenda(datos));
    }

    @Test
    void rechazaValorNegativo() {
        DatosPrenda datos = new DatosPrenda(TipoPrenda.DAMA, "D-01", "rojo", "Marca", "M", -1, especificosDama());
        assertThrows(AtuendosException.class, () -> FabricaPrenda.para(TipoPrenda.DAMA).crearPrenda(datos));
    }

    @Test
    void rechazaDatoPropioFaltante() {
        DatosPrenda datos = dama(Map.of(Constantes.CAMPO_PEDRERIA, "true"));
        assertThrows(AtuendosException.class, () -> FabricaPrenda.para(TipoPrenda.DAMA).crearPrenda(datos));
    }

    @Test
    void rechazaAlturaInvalida() {
        DatosPrenda datos = dama(Map.of(Constantes.CAMPO_PEDRERIA, "true", Constantes.CAMPO_ALTURA, "mediano",
                Constantes.CAMPO_CANTIDAD_PIEZAS, "2"));
        assertThrows(AtuendosException.class, () -> FabricaPrenda.para(TipoPrenda.DAMA).crearPrenda(datos));
    }

    @Test
    void rechazaCantidadDePiezasNoPositiva() {
        DatosPrenda datos = dama(Map.of(Constantes.CAMPO_PEDRERIA, "false", Constantes.CAMPO_ALTURA, "corto",
                Constantes.CAMPO_CANTIDAD_PIEZAS, "0"));
        assertThrows(AtuendosException.class, () -> FabricaPrenda.para(TipoPrenda.DAMA).crearPrenda(datos));
    }

    @Test
    void rechazaDatosDeOtroTipo() {
        DatosPrenda datos = new DatosPrenda(TipoPrenda.DISFRAZ, "F-01", "verde", "Marca", "S", 40000,
                Map.of(Constantes.CAMPO_NOMBRE_DISFRAZ, "Dinosaurio"));
        assertThrows(AtuendosException.class, () -> FabricaPrenda.para(TipoPrenda.DAMA).crearPrenda(datos));
    }
}
