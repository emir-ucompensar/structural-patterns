/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.negocio.EnvioLavanderia;
import atuendos.negocio.ObservadorLavanderia;

/**
 * Suscriptor concreto del patrón Observer: muestra en la consola el mensaje de
 * notificación de cada tanda enviada a lavandería.
 */
final class NotificacionEnvioConsola implements ObservadorLavanderia {

    private final Terminal terminal;

    /**
     * Crea la notificación sobre la terminal donde se muestra.
     *
     * @param terminal terminal de salida
     */
    NotificacionEnvioConsola(Terminal terminal) {
        this.terminal = terminal;
    }

    @Override
    public void prendasEnviadas(EnvioLavanderia envio) {
        int cantidad = envio.prendas().size();
        terminal.exito(cantidad + (cantidad == 1 ? " prenda enviada" : " prendas enviadas") + " a lavandería el "
                + Vistas.formatear(envio.fecha()) + ".");
        terminal.seccion("Notificación de envío");
        Vistas.tablaLavanderia(terminal, envio.prendas());
    }
}
