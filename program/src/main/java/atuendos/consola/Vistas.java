/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.consola;

import atuendos.comun.Constantes;
import atuendos.modelo.Cliente;
import atuendos.modelo.Empleado;
import atuendos.modelo.Prenda;
import atuendos.modelo.PrendaEnLavanderia;
import atuendos.modelo.ServicioAlquiler;
import atuendos.modelo.TipoPrenda;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fichas y tablas que varios comandos comparten, para no repetirlas.
 */
final class Vistas {

    /** Aviso cuando una consulta de servicios no trae resultados. */
    static final String MSG_SIN_SERVICIOS = "No hay servicios para mostrar.";

    /** Aviso cuando no hay prendas registradas. */
    static final String MSG_SIN_PRENDAS = "No hay prendas para mostrar.";

    /** Aviso cuando la lista de lavandería está vacía. */
    static final String MSG_SIN_PENDIENTES = "No hay prendas pendientes de envío.";

    private static final String MSG_SIN_REGISTROS = "No hay registros para mostrar.";
    private static final String[] CABECERA_SERVICIOS = {"Número", "Solicitud", "Alquiler", "Cliente",
        "Empleado", "Prendas"};
    private static final String[] CABECERA_LAVANDERIA = {"Turno", "Referencia", "Tipo", "Color", "Talla",
        "Prioridad", "Detalle"};
    private static final String[] CABECERA_PRENDAS = {"Referencia", "Tipo", "Color", "Marca", "Talla",
        "Alquiler", "Detalle"};

    private Vistas() {
    }

    /**
     * Muestra la ficha de una prenda.
     *
     * @param terminal terminal donde se muestra
     * @param prenda prenda a mostrar
     */
    static void fichaPrenda(Terminal terminal, Prenda prenda) {
        terminal.ficha("Prenda", List.of(
                new String[] {"Referencia", prenda.getReferencia()},
                new String[] {"Tipo", prenda.getTipo().name()},
                new String[] {"Color", prenda.getColor()},
                new String[] {"Marca", prenda.getMarca()},
                new String[] {"Talla", prenda.getTalla()},
                new String[] {"Alquiler", Terminal.moneda(prenda.getValorAlquiler())},
                new String[] {"Detalle", prenda.detalle()}));
    }

    /**
     * Muestra la ficha de un cliente.
     *
     * @param terminal terminal donde se muestra
     * @param cliente cliente a mostrar
     */
    static void fichaCliente(Terminal terminal, Cliente cliente) {
        terminal.ficha("Cliente", List.of(
                new String[] {"Identificación", cliente.getIdentificacion()},
                new String[] {"Nombre", cliente.getNombre()},
                new String[] {"Dirección", cliente.getDireccion()},
                new String[] {"Teléfono", cliente.getTelefono()},
                new String[] {"Correo", cliente.getCorreo()}));
    }

    /**
     * Muestra la ficha de un empleado.
     *
     * @param terminal terminal donde se muestra
     * @param empleado empleado a mostrar
     */
    static void fichaEmpleado(Terminal terminal, Empleado empleado) {
        terminal.ficha("Empleado", List.of(
                new String[] {"Identificación", empleado.getIdentificacion()},
                new String[] {"Nombre", empleado.getNombre()},
                new String[] {"Dirección", empleado.getDireccion()},
                new String[] {"Teléfono", empleado.getTelefono()},
                new String[] {"Cargo", empleado.getCargo()}));
    }

    /**
     * Muestra la ficha completa de un servicio, con cliente, empleado y prendas.
     *
     * @param terminal terminal donde se muestra
     * @param servicio servicio a mostrar
     */
    static void fichaServicio(Terminal terminal, ServicioAlquiler servicio) {
        List<String[]> filas = new ArrayList<>();
        filas.add(new String[] {"Número", String.valueOf(servicio.getNumero())});
        filas.add(new String[] {"Solicitud", formatear(servicio.getFechaSolicitud())});
        filas.add(new String[] {"Alquiler", formatear(servicio.getFechaAlquiler())});
        filas.add(new String[] {"Cliente", servicio.getCliente().getNombre() + " ("
            + servicio.getCliente().getIdentificacion() + "), " + servicio.getCliente().getTelefono() + ", "
            + servicio.getCliente().getCorreo()});
        filas.add(new String[] {"Empleado", servicio.getEmpleado().getNombre() + " ("
            + servicio.getEmpleado().getIdentificacion() + "), " + servicio.getEmpleado().getCargo()});
        for (Prenda prenda : servicio.getPrendas()) {
            filas.add(new String[] {"Prenda", prenda.getReferencia() + ", " + prenda.getTipo() + ", "
                + prenda.getColor() + ", talla " + prenda.getTalla() + ", "
                + Terminal.moneda(prenda.getValorAlquiler()) + ", " + prenda.detalle()});
        }
        terminal.ficha("Servicio", filas);
    }

    /**
     * Muestra una tabla de prendas.
     *
     * @param terminal terminal donde se muestra
     * @param prendas prendas a mostrar
     */
    static void tablaPrendas(Terminal terminal, List<Prenda> prendas) {
        List<String[]> filas = new ArrayList<>();
        for (Prenda prenda : prendas) {
            filas.add(new String[] {prenda.getReferencia(), prenda.getTipo().name(), prenda.getColor(),
                prenda.getMarca(), prenda.getTalla(), Terminal.moneda(prenda.getValorAlquiler()),
                prenda.detalle()});
        }
        terminal.tabla(CABECERA_PRENDAS, filas);
    }

    /**
     * Muestra una tabla de servicios, o un aviso si no hay ninguno.
     *
     * @param terminal terminal donde se muestra
     * @param servicios servicios a mostrar
     */
    static void tablaServicios(Terminal terminal, List<ServicioAlquiler> servicios) {
        if (servicios.isEmpty()) {
            terminal.info(MSG_SIN_SERVICIOS);
            return;
        }
        List<String[]> filas = new ArrayList<>();
        for (ServicioAlquiler servicio : servicios) {
            filas.add(new String[] {
                String.valueOf(servicio.getNumero()),
                formatear(servicio.getFechaSolicitud()),
                formatear(servicio.getFechaAlquiler()),
                servicio.getCliente().getNombre(),
                servicio.getEmpleado().getNombre(),
                servicio.getPrendas().stream().map(Prenda::getReferencia).collect(Collectors.joining(", "))});
        }
        terminal.tabla(CABECERA_SERVICIOS, filas);
    }

    /**
     * Muestra una tabla de personas, o un aviso si no hay registros.
     *
     * @param terminal terminal donde se muestra
     * @param cabeceras títulos de las columnas
     * @param filas una fila por persona
     */
    static void tablaPersonas(Terminal terminal, String[] cabeceras, List<String[]> filas) {
        if (filas.isEmpty()) {
            terminal.info(MSG_SIN_REGISTROS);
        } else {
            terminal.tabla(cabeceras, filas);
        }
    }

    /**
     * Muestra registros de lavandería numerados por turno, en el orden en que se
     * recorren.
     *
     * @param terminal terminal donde se muestra
     * @param registros registros a mostrar
     */
    static void tablaLavanderia(Terminal terminal, Iterable<PrendaEnLavanderia> registros) {
        List<String[]> filas = new ArrayList<>();
        int turno = 1;
        for (PrendaEnLavanderia registro : registros) {
            Prenda prenda = registro.prenda();
            filas.add(new String[] {String.valueOf(turno++), prenda.getReferencia(), prenda.getTipo().name(),
                prenda.getColor(), prenda.getTalla(), registro.prioritaria() ? "Sí" : "No", prenda.detalle()});
        }
        terminal.tabla(CABECERA_LAVANDERIA, filas);
    }

    /**
     * Da el título en plural de un tipo de prenda.
     *
     * @param tipo tipo de prenda
     * @return el título para las secciones de la consulta por talla
     */
    static String titulo(TipoPrenda tipo) {
        return switch (tipo) {
            case DAMA -> "Vestidos de dama";
            case CABALLERO -> "Trajes de caballero";
            case DISFRAZ -> "Disfraces";
        };
    }

    /**
     * Escribe una fecha con el formato de la aplicación.
     *
     * @param fecha fecha a escribir
     * @return la fecha como dd/mm/aaaa
     */
    static String formatear(LocalDate fecha) {
        return fecha.format(DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA));
    }
}
