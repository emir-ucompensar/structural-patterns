/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Entrada y salida de la interfaz de texto. Dibuja con colores y marcos
 * Unicode cuando la terminal los admite, y con texto plano en los demás casos.
 */
public class Terminal {

    private static final String RESET = "\u001B[0m";
    private static final String NEGRITA = "\u001B[1m";
    private static final String TENUE = "\u001B[2m";
    private static final String ROJO = "\u001B[31m";
    private static final String VERDE = "\u001B[32m";
    private static final String CIAN = "\u001B[36m";

    private static final DateTimeFormatter FECHA_LATINA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final String MSG_OBLIGATORIO = "Este dato es obligatorio.";
    private static final String MSG_ENTERO = "Se esperaba un número entero.";
    private static final String MSG_DECIMAL = "Se esperaba un número, por ejemplo 80000 o 80000,50.";
    private static final String MSG_FECHA = "Fecha no válida. Formato esperado: dd/mm/aaaa.";
    private static final String MSG_SI_NO = "Se esperaba s o n.";
    private static final String MSG_OPCION = "Opción no válida.";

    private final Scanner entrada;
    private final PrintStream salida;
    private final boolean ansi;

    /**
     * Crea una terminal sobre los flujos dados.
     *
     * @param entrada flujo de lectura
     * @param salida flujo de escritura
     * @param ansi si se usan colores y marcos Unicode
     */
    public Terminal(InputStream entrada, PrintStream salida, boolean ansi) {
        this.entrada = new Scanner(entrada, StandardCharsets.UTF_8);
        this.salida = salida;
        this.ansi = ansi;
    }

    /**
     * Crea la terminal estándar, con colores solo si hay una terminal real y
     * el usuario no pidió desactivarlos con NO_COLOR.
     *
     * @return la terminal estándar
     */
    public static Terminal estandar() {
        boolean color = System.console() != null
                && System.getenv("NO_COLOR") == null
                && !"dumb".equals(System.getenv("TERM"));
        return new Terminal(System.in, System.out, color);
    }

    /**
     * Da formato de moneda colombiana a un valor.
     *
     * @param valor valor a mostrar
     * @return el valor con separador de miles y signo de pesos
     */
    public static String moneda(double valor) {
        return String.format(Locale.forLanguageTag("es-CO"), "$ %,.0f", valor);
    }

    /**
     * Muestra el encabezado de la aplicación dentro de un marco.
     *
     * @param titulo línea principal
     * @param subtitulo línea secundaria
     */
    public void banner(String titulo, String subtitulo) {
        int ancho = Math.max(titulo.length(), subtitulo.length()) + 4;
        String horizontal = ansi ? "─" : "-";
        salida.println();
        salida.println("  " + estilo(CIAN, (ansi ? "╭" : "+") + horizontal.repeat(ancho) + (ansi ? "╮" : "+")));
        salida.println("  " + marco() + "  " + estilo(NEGRITA + CIAN, rellenar(titulo, ancho - 2)) + marco());
        salida.println("  " + marco() + "  " + estilo(TENUE, rellenar(subtitulo, ancho - 2)) + marco());
        salida.println("  " + estilo(CIAN, (ansi ? "╰" : "+") + horizontal.repeat(ancho) + (ansi ? "╯" : "+")));
    }

    /**
     * Muestra el título de una sección con una línea debajo.
     *
     * @param titulo título de la sección
     */
    public void seccion(String titulo) {
        salida.println();
        salida.println("  " + estilo(NEGRITA + CIAN, titulo));
        salida.println("  " + estilo(TENUE, (ansi ? "─" : "-").repeat(titulo.length())));
    }

    /**
     * Muestra una opción de menú.
     *
     * @param clave número o letra que la elige
     * @param texto descripción de la opción
     */
    public void opcion(String clave, String texto) {
        salida.println("  " + estilo(NEGRITA + CIAN, clave) + "  " + texto);
    }

    /**
     * Muestra una línea de texto con sangría.
     *
     * @param texto texto a mostrar
     */
    public void linea(String texto) {
        salida.println("  " + texto);
    }

    /**
     * Muestra un mensaje de éxito.
     *
     * @param mensaje texto del mensaje
     */
    public void exito(String mensaje) {
        salida.println("  " + estilo(VERDE, (ansi ? "✔ " : "OK ") + mensaje));
    }

    /**
     * Muestra un mensaje de error.
     *
     * @param mensaje texto del mensaje
     */
    public void error(String mensaje) {
        salida.println("  " + estilo(ROJO, (ansi ? "✖ " : "ERROR ") + mensaje));
    }

    /**
     * Muestra un mensaje informativo.
     *
     * @param mensaje texto del mensaje
     */
    public void info(String mensaje) {
        salida.println("  " + estilo(CIAN, (ansi ? "• " : "- ") + mensaje));
    }

    /**
     * Muestra una ficha de pares etiqueta y valor alineados.
     *
     * @param titulo título de la ficha
     * @param filas pares {etiqueta, valor}
     */
    public void ficha(String titulo, List<String[]> filas) {
        seccion(titulo);
        int ancho = filas.stream().mapToInt(fila -> fila[0].length()).max().orElse(0);
        for (String[] fila : filas) {
            salida.println("  " + estilo(TENUE, rellenar(fila[0], ancho)) + "  " + fila[1]);
        }
    }

    /**
     * Muestra una tabla con encabezado y columnas alineadas.
     *
     * @param cabeceras títulos de las columnas
     * @param filas filas de la tabla, con una celda por columna
     */
    public void tabla(String[] cabeceras, List<String[]> filas) {
        int[] anchos = new int[cabeceras.length];
        for (int i = 0; i < cabeceras.length; i++) {
            anchos[i] = cabeceras[i].length();
            for (String[] fila : filas) {
                anchos[i] = Math.max(anchos[i], fila[i].length());
            }
        }
        StringBuilder encabezado = new StringBuilder("  ");
        StringBuilder separador = new StringBuilder("  ");
        for (int i = 0; i < cabeceras.length; i++) {
            encabezado.append(rellenar(cabeceras[i], anchos[i])).append("  ");
            separador.append((ansi ? "─" : "-").repeat(anchos[i])).append("  ");
        }
        salida.println(estilo(NEGRITA + CIAN, encabezado.toString().stripTrailing()));
        salida.println(estilo(TENUE, separador.toString().stripTrailing()));
        for (String[] fila : filas) {
            StringBuilder linea = new StringBuilder("  ");
            for (int i = 0; i < cabeceras.length; i++) {
                linea.append(rellenar(fila[i], anchos[i])).append("  ");
            }
            salida.println(linea.toString().stripTrailing());
        }
    }

    /**
     * Pide un texto, que puede quedar vacío.
     *
     * @param etiqueta nombre del dato
     * @return el texto sin espacios en los extremos
     */
    public String leer(String etiqueta) {
        salida.print("  " + estilo(NEGRITA, etiqueta) + estilo(CIAN, " › "));
        salida.flush();
        return entrada.nextLine().trim();
    }

    /**
     * Pide un texto hasta que no esté vacío.
     *
     * @param etiqueta nombre del dato
     * @return el texto sin espacios en los extremos
     */
    public String leerObligatorio(String etiqueta) {
        while (true) {
            String valor = leer(etiqueta);
            if (!valor.isBlank()) {
                return valor;
            }
            error(MSG_OBLIGATORIO);
        }
    }

    /**
     * Pide un número entero.
     *
     * @param etiqueta nombre del dato
     * @return el número leído
     */
    public int leerEntero(String etiqueta) {
        while (true) {
            try {
                return Integer.parseInt(leer(etiqueta));
            } catch (NumberFormatException e) {
                error(MSG_ENTERO);
            }
        }
    }

    /**
     * Pide un número decimal; acepta coma o punto como separador.
     *
     * @param etiqueta nombre del dato
     * @return el número leído
     */
    public double leerDecimal(String etiqueta) {
        while (true) {
            try {
                double valor = Double.parseDouble(leer(etiqueta).replace(',', '.'));
                if (Double.isFinite(valor)) {
                    return valor;
                }
            } catch (NumberFormatException e) {
                // Se informa abajo con el mismo mensaje.
            }
            error(MSG_DECIMAL);
        }
    }

    /**
     * Pide una fecha en formato dd/mm/aaaa o aaaa-mm-dd.
     *
     * @param etiqueta nombre del dato
     * @return la fecha leída
     */
    public LocalDate leerFecha(String etiqueta) {
        while (true) {
            String valor = leer(etiqueta);
            try {
                return LocalDate.parse(valor, FECHA_LATINA);
            } catch (DateTimeParseException e) {
                try {
                    return LocalDate.parse(valor);
                } catch (DateTimeParseException otro) {
                    error(MSG_FECHA);
                }
            }
        }
    }

    /**
     * Hace una pregunta de sí o no.
     *
     * @param pregunta texto de la pregunta
     * @return verdadero si responde sí
     */
    public boolean confirmar(String pregunta) {
        while (true) {
            String respuesta = leer(pregunta + " (s/n)").toLowerCase(Locale.ROOT);
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            error(MSG_SI_NO);
        }
    }

    /**
     * Muestra una lista numerada y pide elegir una opción.
     *
     * @param titulo texto sobre la lista
     * @param opciones textos de las opciones
     * @return el índice elegido, desde cero
     */
    public int elegir(String titulo, List<String> opciones) {
        linea(estilo(NEGRITA, titulo));
        for (int i = 0; i < opciones.size(); i++) {
            opcion(String.valueOf(i + 1), opciones.get(i));
        }
        while (true) {
            try {
                int eleccion = Integer.parseInt(leer("Opción"));
                if (eleccion >= 1 && eleccion <= opciones.size()) {
                    return eleccion - 1;
                }
            } catch (NumberFormatException e) {
                // Se informa abajo con el mismo mensaje.
            }
            error(MSG_OPCION);
        }
    }

    /**
     * Pide elegir un valor de una enumeración.
     *
     * @param titulo texto sobre la lista
     * @param clase enumeración de la que se elige
     * @param <E> tipo de la enumeración
     * @return el valor elegido
     */
    public <E extends Enum<E>> E elegirEnum(String titulo, Class<E> clase) {
        E[] valores = clase.getEnumConstants();
        List<String> nombres = java.util.Arrays.stream(valores).map(Terminal::etiqueta).toList();
        return valores[elegir(titulo, nombres)];
    }

    private static String etiqueta(Enum<?> valor) {
        String nombre = valor.name();
        return nombre.charAt(0) + nombre.substring(1).toLowerCase(Locale.ROOT);
    }

    private String marco() {
        return estilo(CIAN, ansi ? "│" : "|");
    }

    private static String rellenar(String texto, int ancho) {
        return texto + " ".repeat(Math.max(0, ancho - texto.length()));
    }

    private String estilo(String codigo, String texto) {
        return ansi ? codigo + texto + RESET : texto;
    }
}
