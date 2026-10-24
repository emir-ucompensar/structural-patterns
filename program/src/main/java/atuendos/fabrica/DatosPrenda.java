/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.fabrica;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.TipoPrenda;

import java.util.Locale;
import java.util.Map;

/**
 * Datos en bruto de una prenda, tal como llegan de la consola o de la base de
 * datos. Los datos propios de cada tipo viajan en un mapa por nombre de campo.
 *
 * @param tipo tipo de prenda
 * @param referencia código que la identifica
 * @param color color de la prenda
 * @param marca marca de la prenda
 * @param talla talla de la prenda
 * @param valorAlquiler valor del alquiler
 * @param especificos datos propios del tipo, por nombre de campo
 */
public record DatosPrenda(TipoPrenda tipo, String referencia, String color, String marca, String talla,
        double valorAlquiler, Map<String, String> especificos) {

    /**
     * Copia el mapa de datos específicos para que el registro no cambie.
     */
    public DatosPrenda {
        especificos = especificos == null ? Map.of() : Map.copyOf(especificos);
    }

    /**
     * Devuelve una copia con otra talla.
     *
     * @param nuevaTalla talla de la copia
     * @return los mismos datos con la talla indicada
     */
    public DatosPrenda conTalla(String nuevaTalla) {
        return new DatosPrenda(tipo, referencia, color, marca, nuevaTalla, valorAlquiler, especificos);
    }

    String texto(String campo) throws AtuendosException {
        String valor = especificos.get(campo);
        if (valor == null || valor.isBlank()) {
            throw new AtuendosException(String.format(Constantes.MSG_CAMPO_OBLIGATORIO, campo));
        }
        return valor.trim();
    }

    boolean booleano(String campo) throws AtuendosException {
        String valor = texto(campo);
        if (valor.equalsIgnoreCase("true")) {
            return true;
        }
        if (valor.equalsIgnoreCase("false")) {
            return false;
        }
        throw new AtuendosException(String.format(Constantes.MSG_VALOR_INVALIDO, campo, valor));
    }

    int enteroPositivo(String campo) throws AtuendosException {
        String valor = texto(campo);
        try {
            int numero = Integer.parseInt(valor);
            if (numero > 0) {
                return numero;
            }
        } catch (NumberFormatException e) {
            // Se informa abajo con el mismo mensaje.
        }
        throw new AtuendosException(String.format(Constantes.MSG_VALOR_INVALIDO, campo, valor));
    }

    <E extends Enum<E>> E enumerado(String campo, Class<E> clase) throws AtuendosException {
        String valor = texto(campo);
        try {
            return Enum.valueOf(clase, valor.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new AtuendosException(String.format(Constantes.MSG_VALOR_INVALIDO, campo, valor), e);
        }
    }
}
