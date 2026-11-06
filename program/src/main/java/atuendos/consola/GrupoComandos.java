/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import java.util.List;

/**
 * Grupo de comandos que el menú principal muestra como una sola opción.
 *
 * @param titulo texto del grupo en el menú principal
 * @param comandos comandos del grupo, en el orden en que se muestran
 */
public record GrupoComandos(String titulo, List<Comando> comandos) {

    /**
     * Crea el grupo con una copia inmutable de sus comandos.
     *
     * @param titulo texto del grupo en el menú principal
     * @param comandos comandos del grupo, en el orden en que se muestran
     */
    public GrupoComandos {
        comandos = List.copyOf(comandos);
    }
}
