/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeSet;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Attributes;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

/**
 * Entrada y salida de la interfaz de texto. En una terminal real dibuja con
 * colores y marcos Unicode, edita las líneas de texto y deja elegir con las
 * flechas; con la entrada redirigida usa texto plano y opciones numeradas.
 */
public class Terminal implements AutoCloseable {

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
    private static final String MSG_SELECCION_MINIMA = "Elige al menos %d.";
    private static final String AYUDA_UNA = "↑↓ mover · enter elegir";
    private static final String AYUDA_VARIAS = "↑↓ mover · espacio marcar · enter confirmar";
    private static final String AYUDA_UNA_PLANA = "arriba/abajo mover, enter elegir";
    private static final String AYUDA_VARIAS_PLANA = "arriba/abajo mover, espacio marcar, enter confirmar";
    private static final int ESPERA_TECLA_MS = 100;

    private enum Tecla {
        ARRIBA, ABAJO, ESPACIO, ENTER, OTRA
    }

    private final Scanner entrada;
    private final PrintStream salida;
    private final boolean ansi;
    private final org.jline.terminal.Terminal jline;
    private final LineReader lector;

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
        this.jline = null;
        this.lector = null;
    }

    /**
     * Crea una terminal interactiva sobre una terminal de JLine.
     *
     * @param jline terminal de JLine, en modo crudo para las elecciones
     * @param salida flujo de escritura
     * @param ansi si se usan colores y marcos Unicode
     */
    Terminal(org.jline.terminal.Terminal jline, PrintStream salida, boolean ansi) {
        this.entrada = null;
        this.salida = salida;
        this.ansi = ansi;
        this.jline = jline;
        this.lector = LineReaderBuilder.builder().terminal(jline).build();
    }

    /**
     * Crea la terminal estándar. Con una terminal real es interactiva, y los
     * colores se desactivan si el usuario define NO_COLOR; con la entrada
     * redirigida usa texto plano.
     *
     * @return la terminal estándar
     */
    public static Terminal estandar() {
        boolean real = System.console() != null && !"dumb".equals(System.getenv("TERM"));
        if (real) {
            try {
                org.jline.terminal.Terminal jline = TerminalBuilder.builder().system(true).build();
                if (!jline.getType().startsWith(org.jline.terminal.Terminal.TYPE_DUMB)) {
                    return new Terminal(jline, System.out, System.getenv("NO_COLOR") == null);
                }
                jline.close();
            } catch (IOException | RuntimeException e) {
                // Sin terminal interactiva se usa el modo de texto.
            }
        }
        return new Terminal(System.in, System.out, false);
    }

    /**
     * Libera la terminal interactiva y devuelve la terminal a su estado normal.
     */
    @Override
    public void close() {
        if (jline != null) {
            try {
                jline.close();
            } catch (IOException e) {
                // Al cerrar no hay nada más que hacer con la terminal.
            }
        }
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
        String indicador = "  " + estilo(NEGRITA, etiqueta) + estilo(CIAN, " › ");
        if (lector == null) {
            salida.print(indicador);
            salida.flush();
            return entrada.nextLine().trim();
        }
        try {
            return lector.readLine(indicador).trim();
        } catch (EndOfFileException | UserInterruptException e) {
            throw new NoSuchElementException();
        }
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
        if (jline != null) {
            return seleccionar(titulo, opciones, false, 1).get(0);
        }
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
     * Pide elegir una o varias opciones de una lista. En una terminal real se
     * mueve con las flechas y se marca con la barra espaciadora; en texto plano
     * se escriben los números separados por coma.
     *
     * @param titulo texto sobre la lista
     * @param opciones textos de las opciones
     * @param minimo cantidad mínima de opciones a elegir
     * @return los índices elegidos, desde cero y en orden
     */
    public List<Integer> elegirVarias(String titulo, List<String> opciones, int minimo) {
        if (jline != null) {
            return seleccionar(titulo, opciones, true, minimo);
        }
        linea(estilo(NEGRITA, titulo));
        for (int i = 0; i < opciones.size(); i++) {
            opcion(String.valueOf(i + 1), opciones.get(i));
        }
        while (true) {
            String texto = leer("Números separados por coma");
            try {
                TreeSet<Integer> elegidas = new TreeSet<>();
                for (String parte : texto.split("[,\\s]+")) {
                    if (parte.isBlank()) {
                        continue;
                    }
                    int numero = Integer.parseInt(parte);
                    if (numero < 1 || numero > opciones.size()) {
                        throw new NumberFormatException();
                    }
                    elegidas.add(numero - 1);
                }
                if (elegidas.size() >= minimo) {
                    return new ArrayList<>(elegidas);
                }
                error(String.format(MSG_SELECCION_MINIMA, minimo));
            } catch (NumberFormatException e) {
                error(MSG_OPCION);
            }
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

    private List<Integer> seleccionar(String titulo, List<String> opciones, boolean varias, int minimo) {
        int cantidad = opciones.size();
        boolean[] marcadas = new boolean[cantidad];
        int cursor = 0;
        String aviso = null;
        Attributes anterior = jline.enterRawMode();
        try {
            NonBlockingReader teclas = jline.reader();
            dibujar(titulo, opciones, varias, marcadas, cursor, aviso, false);
            while (true) {
                Tecla tecla = leerTecla(teclas);
                aviso = null;
                if (tecla == Tecla.ARRIBA) {
                    cursor = (cursor + cantidad - 1) % cantidad;
                } else if (tecla == Tecla.ABAJO) {
                    cursor = (cursor + 1) % cantidad;
                } else if (tecla == Tecla.ESPACIO && varias) {
                    marcadas[cursor] = !marcadas[cursor];
                } else if (tecla == Tecla.ENTER) {
                    if (!varias) {
                        marcadas[cursor] = true;
                    }
                    List<Integer> elegidas = indices(marcadas);
                    if (elegidas.size() >= minimo) {
                        resumir(titulo, opciones, elegidas);
                        return elegidas;
                    }
                    aviso = String.format(MSG_SELECCION_MINIMA, minimo);
                }
                dibujar(titulo, opciones, varias, marcadas, cursor, aviso, true);
            }
        } catch (IOException e) {
            throw new NoSuchElementException();
        } finally {
            jline.setAttributes(anterior);
        }
    }

    private static Tecla leerTecla(NonBlockingReader teclas) throws IOException {
        int c = teclas.read();
        if (c == NonBlockingReader.EOF || c == 3 || c == 4) {
            throw new NoSuchElementException();
        }
        if (c == 27) {
            int segundo = teclas.read(ESPERA_TECLA_MS);
            if (segundo == '[' || segundo == 'O') {
                int tercero = teclas.read(ESPERA_TECLA_MS);
                return tercero == 'A' ? Tecla.ARRIBA : tercero == 'B' ? Tecla.ABAJO : Tecla.OTRA;
            }
            return Tecla.OTRA;
        }
        return switch (c) {
            case 'k' -> Tecla.ARRIBA;
            case 'j' -> Tecla.ABAJO;
            case ' ' -> Tecla.ESPACIO;
            case '\r', '\n' -> Tecla.ENTER;
            default -> Tecla.OTRA;
        };
    }

    private void dibujar(String titulo, List<String> opciones, boolean varias, boolean[] marcadas, int cursor,
            String aviso, boolean redibujar) {
        StringBuilder pantalla = new StringBuilder();
        if (redibujar) {
            pantalla.append("\u001B[").append(opciones.size() + 2).append('A');
        }
        pantalla.append("\r\u001B[2K  ").append(estilo(NEGRITA, titulo)).append('\n');
        for (int i = 0; i < opciones.size(); i++) {
            boolean actual = i == cursor;
            String texto = (actual ? (ansi ? "❯" : ">") : " ") + " " + (varias ? (marcadas[i] ? "[*] " : "[ ] ") : "")
                    + opciones.get(i);
            String estilo = actual ? NEGRITA + CIAN : varias && marcadas[i] ? VERDE : "";
            pantalla.append("\r\u001B[2K  ").append(estilo.isEmpty() ? texto : estilo(estilo, texto)).append('\n');
        }
        String ayuda = ansi ? (varias ? AYUDA_VARIAS : AYUDA_UNA) : (varias ? AYUDA_VARIAS_PLANA : AYUDA_UNA_PLANA);
        pantalla.append("\r\u001B[2K  ").append(aviso != null ? estilo(ROJO, aviso) : estilo(TENUE, ayuda))
                .append('\n');
        salida.print(pantalla);
        salida.flush();
    }

    private void resumir(String titulo, List<String> opciones, List<Integer> elegidas) {
        salida.print("\u001B[" + (opciones.size() + 2) + "A\r\u001B[J");
        List<String> textos = new ArrayList<>();
        for (int indice : elegidas) {
            textos.add(opciones.get(indice));
        }
        linea(estilo(TENUE, titulo) + "  " + String.join(", ", textos));
        salida.flush();
    }

    private static List<Integer> indices(boolean[] marcadas) {
        List<Integer> elegidas = new ArrayList<>();
        for (int i = 0; i < marcadas.length; i++) {
            if (marcadas[i]) {
                elegidas.add(i);
            }
        }
        return elegidas;
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
