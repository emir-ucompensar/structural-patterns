/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.fabrica.DatosPrenda;
import atuendos.modelo.Aderezo;
import atuendos.modelo.Altura;
import atuendos.modelo.Cliente;
import atuendos.modelo.Empleado;
import atuendos.modelo.Prenda;
import atuendos.modelo.ServicioAlquiler;
import atuendos.modelo.TipoPrenda;
import atuendos.modelo.TipoTraje;
import atuendos.negocio.NegocioAlquiler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Menú principal de la aplicación. Lee los datos, llama a la fachada y
 * muestra el resultado; nunca deja que un error aborte el programa.
 */
public class Menu {

    private static final String TITULO = "LOS ATUENDOS";
    private static final String SUBTITULO = "Alquiler de vestuario";
    private static final String MSG_INESPERADO = "Ocurrió un error inesperado. La operación no se completó.";
    private static final String MSG_OPCION_NO_VALIDA = "Opción no válida.";
    private static final String MSG_DESPEDIDA = "Hasta pronto.";

    /** Acción de una opción del menú. */
    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws AtuendosException;
    }

    private record Opcion(String texto, Accion accion) {
    }

    private final Terminal terminal;
    private final NegocioAlquiler negocio;
    private final List<Opcion> opciones;

    /**
     * Crea el menú sobre una terminal y la fachada de negocio.
     *
     * @param terminal terminal de entrada y salida
     * @param negocio fachada de la aplicación
     */
    public Menu(Terminal terminal, NegocioAlquiler negocio) {
        this.terminal = terminal;
        this.negocio = negocio;
        this.opciones = List.of(
                new Opcion("Registrar prenda", this::registrarPrenda),
                new Opcion("Registrar cliente", this::registrarCliente),
                new Opcion("Registrar empleado", this::registrarEmpleado),
                new Opcion("Registrar servicio de alquiler", this::registrarServicio));
    }

    /**
     * Muestra el menú y atiende opciones hasta que se elige salir o se acaba la entrada.
     */
    public void ejecutar() {
        terminal.banner(TITULO, SUBTITULO);
        try {
            while (true) {
                terminal.seccion("Menú principal");
                for (int i = 0; i < opciones.size(); i++) {
                    terminal.opcion(String.valueOf(i + 1), opciones.get(i).texto());
                }
                terminal.opcion("0", "Salir");
                int eleccion = leerOpcion();
                if (eleccion == 0) {
                    break;
                }
                if (eleccion < 0 || eleccion > opciones.size()) {
                    terminal.error(MSG_OPCION_NO_VALIDA);
                    continue;
                }
                ejecutarAccion(opciones.get(eleccion - 1).accion());
            }
        } catch (NoSuchElementException e) {
            terminal.linea("");
        }
        terminal.info(MSG_DESPEDIDA);
    }

    private int leerOpcion() {
        try {
            return Integer.parseInt(terminal.leer("Opción"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void ejecutarAccion(Accion accion) {
        try {
            accion.ejecutar();
        } catch (AtuendosException e) {
            terminal.error(e.getMessage());
        } catch (NoSuchElementException e) {
            throw e;
        } catch (RuntimeException e) {
            terminal.error(MSG_INESPERADO);
        }
    }

    private void registrarPrenda() throws AtuendosException {
        terminal.seccion("Registrar prenda");
        TipoPrenda tipo = terminal.elegirEnum("Tipo de prenda", TipoPrenda.class);
        String referencia = terminal.leerObligatorio("Referencia");
        String color = terminal.leerObligatorio("Color");
        String marca = terminal.leerObligatorio("Marca");
        String talla = terminal.leerObligatorio("Talla");
        double valor = terminal.leerDecimal("Valor del alquiler");
        Map<String, String> especificos = new HashMap<>();
        switch (tipo) {
            case DAMA -> {
                especificos.put(Constantes.CAMPO_PEDRERIA, String.valueOf(terminal.confirmar("¿Lleva pedrería?")));
                especificos.put(Constantes.CAMPO_ALTURA, terminal.elegirEnum("Altura", Altura.class).name());
                especificos.put(Constantes.CAMPO_CANTIDAD_PIEZAS,
                        String.valueOf(terminal.leerEntero("Cantidad de piezas")));
            }
            case CABALLERO -> {
                especificos.put(Constantes.CAMPO_TIPO_TRAJE,
                        terminal.elegirEnum("Tipo de traje", TipoTraje.class).name());
                especificos.put(Constantes.CAMPO_ADEREZO, terminal.elegirEnum("Aderezo", Aderezo.class).name());
            }
            case DISFRAZ -> especificos.put(Constantes.CAMPO_NOMBRE_DISFRAZ,
                    terminal.leerObligatorio("Nombre del disfraz"));
        }
        Prenda prenda = negocio.registrarPrenda(
                new DatosPrenda(tipo, referencia, color, marca, talla, valor, especificos));
        terminal.exito("Prenda registrada.");
        terminal.ficha("Prenda", List.of(
                new String[] {"Referencia", prenda.getReferencia()},
                new String[] {"Tipo", prenda.getTipo().name()},
                new String[] {"Color", prenda.getColor()},
                new String[] {"Marca", prenda.getMarca()},
                new String[] {"Talla", prenda.getTalla()},
                new String[] {"Alquiler", Terminal.moneda(prenda.getValorAlquiler())},
                new String[] {"Detalle", prenda.detalle()}));
    }

    private void registrarCliente() throws AtuendosException {
        terminal.seccion("Registrar cliente");
        Cliente cliente = negocio.registrarCliente(
                terminal.leerObligatorio("Identificación"),
                terminal.leerObligatorio("Nombre"),
                terminal.leerObligatorio("Dirección"),
                terminal.leerObligatorio("Teléfono"),
                terminal.leerObligatorio("Correo"));
        terminal.exito("Cliente registrado.");
        terminal.ficha("Cliente", List.of(
                new String[] {"Identificación", cliente.getIdentificacion()},
                new String[] {"Nombre", cliente.getNombre()},
                new String[] {"Dirección", cliente.getDireccion()},
                new String[] {"Teléfono", cliente.getTelefono()},
                new String[] {"Correo", cliente.getCorreo()}));
    }

    private void registrarEmpleado() throws AtuendosException {
        terminal.seccion("Registrar empleado");
        Empleado empleado = negocio.registrarEmpleado(
                terminal.leerObligatorio("Identificación"),
                terminal.leerObligatorio("Nombre"),
                terminal.leerObligatorio("Dirección"),
                terminal.leerObligatorio("Teléfono"),
                terminal.leerObligatorio("Cargo"));
        terminal.exito("Empleado registrado.");
        terminal.ficha("Empleado", List.of(
                new String[] {"Identificación", empleado.getIdentificacion()},
                new String[] {"Nombre", empleado.getNombre()},
                new String[] {"Dirección", empleado.getDireccion()},
                new String[] {"Teléfono", empleado.getTelefono()},
                new String[] {"Cargo", empleado.getCargo()}));
    }

    private void registrarServicio() throws AtuendosException {
        terminal.seccion("Registrar servicio de alquiler");
        String clienteId = terminal.leerObligatorio("Identificación del cliente");
        String empleadoId = terminal.leerObligatorio("Identificación del empleado");
        LocalDate fecha = terminal.leerFecha("Fecha de alquiler (dd/mm/aaaa)");
        terminal.linea("Referencias de las prendas, una por línea; vacío para terminar.");
        List<String> referencias = new ArrayList<>();
        while (true) {
            String referencia = terminal.leer("Prenda " + (referencias.size() + 1));
            if (referencia.isBlank()) {
                break;
            }
            referencias.add(referencia);
        }
        ServicioAlquiler servicio = negocio.registrarServicio(clienteId, empleadoId, referencias, fecha);
        terminal.exito("Servicio de alquiler registrado.");
        DateTimeFormatter formato = DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA);
        List<String[]> filas = new ArrayList<>();
        filas.add(new String[] {"Número", String.valueOf(servicio.getNumero())});
        filas.add(new String[] {"Solicitud", servicio.getFechaSolicitud().format(formato)});
        filas.add(new String[] {"Alquiler", servicio.getFechaAlquiler().format(formato)});
        filas.add(new String[] {"Cliente",
                servicio.getCliente().getNombre() + " (" + servicio.getCliente().getIdentificacion() + ")"});
        filas.add(new String[] {"Empleado",
                servicio.getEmpleado().getNombre() + " (" + servicio.getEmpleado().getIdentificacion() + ")"});
        for (Prenda prenda : servicio.getPrendas()) {
            filas.add(new String[] {"Prenda", prenda.getReferencia() + " - " + prenda.getTipo()});
        }
        terminal.ficha("Servicio", filas);
    }
}
