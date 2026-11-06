/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.negocio;

/**
 * Interfaz suscriptora del patrón Observer: quien la implementa recibe un aviso
 * cada vez que sale una tanda hacia la lavandería.
 */
public interface ObservadorLavanderia {

    /**
     * Recibe el aviso de un envío que lleva al menos una prenda.
     *
     * @param envio fecha y prendas de la tanda enviada
     */
    void prendasEnviadas(EnvioLavanderia envio);
}
