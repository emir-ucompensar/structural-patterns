/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Menú principal de la aplicación e invocador del patrón Command: muestra los
 * títulos de los comandos, ejecuta el elegido sin saber qué operación es y
 * maneja sus errores de una sola manera, de modo que ninguno aborta el programa.
 * Cualquier comando se puede cancelar con Esc o con el símbolo de volver: el
 * menú regresa al grupo del que vino sin guardar nada, y desde un grupo regresa
 * al menú principal.
 */
public class Menu {

    private static final String TITULO = "LOS ATUENDOS";
    private static final String SUBTITULO = "Alquiler de vestuario";
    private static final String MSG_INESPERADO = "Ocurrió un error inesperado. La operación no se completó.";
    private static final String MSG_OPCION_NO_VALIDA = "Opción no válida.";
    private static final String MSG_DESPEDIDA = "Hasta pronto.";
    private static final String MSG_CANCELADA = "Operación cancelada. No se guardó ningún cambio.";
    /** Elección que significa volver al menú anterior. */
    private static final int VOLVER = -2;

    private final Terminal terminal;
    private final List<GrupoComandos> grupos;
    private String aviso;

    /**
     * Crea el menú sobre una terminal y los grupos de comandos que muestra.
     *
     * @param terminal terminal de entrada y salida
     * @param grupos grupos de comandos, en el orden del menú principal
     */
    public Menu(Terminal terminal, List<GrupoComandos> grupos) {
        this.terminal = terminal;
        this.grupos = List.copyOf(grupos);
    }

    /**
     * Muestra el menú principal hasta que se elige salir o se acaba la entrada.
     */
    public void ejecutar() {
        boolean primera = true;
        try {
            while (true) {
                if (terminal.limpiar() || primera) {
                    terminal.banner(TITULO, SUBTITULO);
                }
                primera = false;
                terminal.seccion("Menú principal");
                for (int i = 0; i < grupos.size(); i++) {
                    terminal.opcion(String.valueOf(i + 1), grupos.get(i).titulo());
                }
                terminal.opcion("0", "Salir");
                mostrarAviso();
                int eleccion = leerOpcion();
                if (eleccion == 0) {
                    break;
                }
                if (eleccion == VOLVER) {
                    continue;
                }
                if (eleccion < 0 || eleccion > grupos.size()) {
                    aviso = MSG_OPCION_NO_VALIDA;
                    continue;
                }
                atenderGrupo(grupos.get(eleccion - 1));
            }
        } catch (NoSuchElementException e) {
            terminal.linea("");
        }
        terminal.limpiar();
        terminal.info(MSG_DESPEDIDA);
    }

    private void atenderGrupo(GrupoComandos grupo) {
        boolean cancelada = false;
        while (true) {
            terminal.limpiar();
            terminal.seccion(grupo.titulo());
            List<Comando> comandos = grupo.comandos();
            for (int i = 0; i < comandos.size(); i++) {
                terminal.opcion(String.valueOf(i + 1), comandos.get(i).titulo());
            }
            terminal.opcion("0", "Volver");
            if (cancelada) {
                terminal.info(MSG_CANCELADA);
            }
            int eleccion = leerOpcion();
            if (eleccion == 0 || eleccion == VOLVER) {
                return;
            }
            if (eleccion < 0 || eleccion > comandos.size()) {
                aviso = MSG_OPCION_NO_VALIDA;
                return;
            }
            cancelada = !ejecutarComando(comandos.get(eleccion - 1));
            if (!cancelada) {
                return;
            }
        }
    }

    private void mostrarAviso() {
        if (aviso != null) {
            terminal.error(aviso);
            aviso = null;
        }
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(terminal.leer("Opción"));
        } catch (NumberFormatException e) {
            return -1;
        } catch (OperacionCancelada e) {
            return VOLVER;
        }
    }

    /**
     * Ejecuta un comando y maneja su resultado.
     *
     * @param comando comando elegido
     * @return falso si se canceló, para volver al grupo sin pausa
     */
    private boolean ejecutarComando(Comando comando) {
        terminal.limpiar();
        terminal.indicarComoVolver();
        try {
            comando.ejecutar();
        } catch (OperacionCancelada e) {
            return false;
        } catch (AtuendosException e) {
            terminal.error(e.getMessage());
        } catch (NoSuchElementException e) {
            throw e;
        } catch (RuntimeException e) {
            terminal.error(MSG_INESPERADO);
        }
        terminal.linea("");
        terminal.pausar();
        return true;
    }
}
