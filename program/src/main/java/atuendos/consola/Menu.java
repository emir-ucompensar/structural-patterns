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
import atuendos.modelo.PrendaEnLavanderia;
import atuendos.modelo.ServicioAlquiler;
import atuendos.modelo.TipoPrenda;
import atuendos.modelo.TipoTraje;
import atuendos.negocio.EnvioLavanderia;
import atuendos.negocio.NegocioAlquiler;
import atuendos.negocio.ServiciosDeCliente;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

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
    private static final String MSG_SIN_SERVICIOS = "No hay servicios para mostrar.";
    private static final String MSG_SIN_PRENDAS = "No hay prendas para mostrar.";
    private static final String MSG_SIN_PENDIENTES = "No hay prendas pendientes de envío.";

    private static final String[] CABECERA_SERVICIOS = {"Número", "Solicitud", "Alquiler", "Cliente",
        "Empleado", "Prendas"};
    private static final String[] CABECERA_LAVANDERIA = {"Turno", "Referencia", "Tipo", "Color", "Talla",
        "Prioridad", "Detalle"};
    private static final String[] CABECERA_PRENDAS = {"Referencia", "Tipo", "Color", "Marca", "Talla",
        "Alquiler", "Detalle"};

    /** Acción de una opción del menú. */
    @FunctionalInterface
    private interface Accion {
        void ejecutar() throws AtuendosException;
    }

    private record Opcion(String texto, Accion accion) {
    }

    private record Grupo(String titulo, List<Opcion> opciones) {
    }

    private final Terminal terminal;
    private final NegocioAlquiler negocio;
    private final List<Grupo> grupos;

    /**
     * Crea el menú sobre una terminal y la fachada de negocio.
     *
     * @param terminal terminal de entrada y salida
     * @param negocio fachada de la aplicación
     */
    public Menu(Terminal terminal, NegocioAlquiler negocio) {
        this.terminal = terminal;
        this.negocio = negocio;
        this.grupos = List.of(
                new Grupo("Registros", List.of(
                        new Opcion("Registrar prenda", this::registrarPrenda),
                        new Opcion("Registrar cliente", this::registrarCliente),
                        new Opcion("Registrar empleado", this::registrarEmpleado))),
                new Grupo("Servicios de alquiler", List.of(
                        new Opcion("Registrar servicio de alquiler", this::registrarServicio),
                        new Opcion("Consultar servicio por número", this::consultarServicio),
                        new Opcion("Consultar servicios de un cliente", this::consultarServiciosDeCliente),
                        new Opcion("Consultar servicios por fecha", this::consultarServiciosPorFecha))),
                new Grupo("Listados y consultas", List.of(
                        new Opcion("Consultar prendas por talla", this::consultarPrendasPorTalla),
                        new Opcion("Listar prendas", this::listarPrendas),
                        new Opcion("Listar clientes", this::listarClientes),
                        new Opcion("Listar empleados", this::listarEmpleados))),
                new Grupo("Lavandería", List.of(
                        new Opcion("Registrar prenda para lavandería", this::registrarEnLavanderia),
                        new Opcion("Ver prendas pendientes de envío", this::verPendientesDeLavanderia),
                        new Opcion("Enviar prendas a lavandería", this::enviarALavanderia))));
    }

    /**
     * Muestra el menú y atiende opciones hasta que se elige salir o se acaba la entrada.
     */
    public void ejecutar() {
        terminal.banner(TITULO, SUBTITULO);
        try {
            while (true) {
                terminal.seccion("Menú principal");
                for (int i = 0; i < grupos.size(); i++) {
                    terminal.opcion(String.valueOf(i + 1), grupos.get(i).titulo());
                }
                terminal.opcion("0", "Salir");
                int eleccion = leerOpcion();
                if (eleccion == 0) {
                    break;
                }
                if (eleccion < 0 || eleccion > grupos.size()) {
                    terminal.error(MSG_OPCION_NO_VALIDA);
                    continue;
                }
                atenderGrupo(grupos.get(eleccion - 1));
            }
        } catch (NoSuchElementException e) {
            terminal.linea("");
        }
        terminal.info(MSG_DESPEDIDA);
    }

    private void atenderGrupo(Grupo grupo) {
        terminal.seccion(grupo.titulo());
        for (int i = 0; i < grupo.opciones().size(); i++) {
            terminal.opcion(String.valueOf(i + 1), grupo.opciones().get(i).texto());
        }
        terminal.opcion("0", "Volver");
        int eleccion = leerOpcion();
        if (eleccion == 0) {
            return;
        }
        if (eleccion < 0 || eleccion > grupo.opciones().size()) {
            terminal.error(MSG_OPCION_NO_VALIDA);
            return;
        }
        ejecutarAccion(grupo.opciones().get(eleccion - 1).accion());
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
        fichaPrenda(prenda);
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
        fichaCliente(cliente);
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
        fichaEmpleado(empleado);
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
        fichaServicio(servicio);
    }

    private void consultarServicio() throws AtuendosException {
        terminal.seccion("Consultar servicio por número");
        fichaServicio(negocio.consultarServicio(terminal.leerEntero("Número del servicio")));
    }

    private void consultarServiciosDeCliente() throws AtuendosException {
        terminal.seccion("Consultar servicios de un cliente");
        ServiciosDeCliente resultado = negocio.consultarServiciosDeCliente(
                terminal.leerObligatorio("Identificación del cliente"));
        fichaCliente(resultado.cliente());
        terminal.seccion("Servicios vigentes");
        mostrarServicios(resultado.vigentes());
    }

    private void consultarServiciosPorFecha() throws AtuendosException {
        terminal.seccion("Consultar servicios por fecha");
        LocalDate fecha = terminal.leerFecha("Fecha de alquiler (dd/mm/aaaa)");
        terminal.seccion("Servicios del " + fecha.format(DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA)));
        mostrarServicios(negocio.consultarServiciosPorFecha(fecha));
    }

    private void consultarPrendasPorTalla() throws AtuendosException {
        terminal.seccion("Consultar prendas por talla");
        String talla = terminal.leerObligatorio("Talla");
        Map<TipoPrenda, List<Prenda>> porTipo = negocio.consultarPrendasPorTalla(talla);
        for (Map.Entry<TipoPrenda, List<Prenda>> entrada : porTipo.entrySet()) {
            terminal.seccion(titulo(entrada.getKey()) + " - talla " + talla);
            if (entrada.getValue().isEmpty()) {
                terminal.linea("Sin prendas.");
            } else {
                mostrarPrendas(entrada.getValue());
            }
        }
    }

    private void listarPrendas() throws AtuendosException {
        terminal.seccion("Prendas registradas");
        List<Prenda> prendas = negocio.listarPrendas();
        if (prendas.isEmpty()) {
            terminal.info(MSG_SIN_PRENDAS);
        } else {
            mostrarPrendas(prendas);
        }
    }

    private void listarClientes() throws AtuendosException {
        terminal.seccion("Clientes registrados");
        List<String[]> filas = new ArrayList<>();
        for (Cliente cliente : negocio.listarClientes()) {
            filas.add(new String[] {cliente.getIdentificacion(), cliente.getNombre(), cliente.getDireccion(),
                cliente.getTelefono(), cliente.getCorreo()});
        }
        mostrarPersonas(new String[] {"Identificación", "Nombre", "Dirección", "Teléfono", "Correo"}, filas);
    }

    private void listarEmpleados() throws AtuendosException {
        terminal.seccion("Empleados registrados");
        List<String[]> filas = new ArrayList<>();
        for (Empleado empleado : negocio.listarEmpleados()) {
            filas.add(new String[] {empleado.getIdentificacion(), empleado.getNombre(), empleado.getDireccion(),
                empleado.getTelefono(), empleado.getCargo()});
        }
        mostrarPersonas(new String[] {"Identificación", "Nombre", "Dirección", "Teléfono", "Cargo"}, filas);
    }

    private void registrarEnLavanderia() throws AtuendosException {
        terminal.seccion("Registrar prenda para lavandería");
        String referencia = terminal.leerObligatorio("Referencia de la prenda");
        boolean prioritaria = terminal.confirmar("¿Con prioridad? (manchada, delicada o decisión del administrador)");
        PrendaEnLavanderia registro = negocio.registrarEnLavanderia(referencia, prioritaria);
        terminal.exito(prioritaria ? "Prenda registrada con prioridad." : "Prenda registrada por orden de llegada.");
        terminal.ficha("Lavandería", List.of(
                new String[] {"Referencia", registro.prenda().getReferencia()},
                new String[] {"Tipo", registro.prenda().getTipo().name()},
                new String[] {"Talla", registro.prenda().getTalla()},
                new String[] {"Prioridad", registro.prioritaria() ? "Sí" : "No"}));
    }

    private void verPendientesDeLavanderia() throws AtuendosException {
        terminal.seccion("Prendas pendientes de envío");
        List<PrendaEnLavanderia> pendientes = negocio.listarLavanderiaPendiente();
        if (pendientes.isEmpty()) {
            terminal.info(MSG_SIN_PENDIENTES);
        } else {
            mostrarEnvio(pendientes);
        }
    }

    private void enviarALavanderia() throws AtuendosException {
        terminal.seccion("Enviar prendas a lavandería");
        EnvioLavanderia envio = negocio.enviarALavanderia(terminal.leerEntero("Cantidad de prendas a enviar"));
        if (envio.prendas().isEmpty()) {
            terminal.info(MSG_SIN_PENDIENTES);
            return;
        }
        int cantidad = envio.prendas().size();
        terminal.exito(cantidad + (cantidad == 1 ? " prenda enviada" : " prendas enviadas") + " a lavandería el "
                + formatear(envio.fecha()) + ".");
        terminal.seccion("Notificación de envío");
        mostrarEnvio(envio.prendas());
    }

    private void mostrarEnvio(List<PrendaEnLavanderia> registros) {
        List<String[]> filas = new ArrayList<>();
        int turno = 1;
        for (PrendaEnLavanderia registro : registros) {
            Prenda prenda = registro.prenda();
            filas.add(new String[] {String.valueOf(turno++), prenda.getReferencia(), prenda.getTipo().name(),
                prenda.getColor(), prenda.getTalla(), registro.prioritaria() ? "Sí" : "No", prenda.detalle()});
        }
        terminal.tabla(CABECERA_LAVANDERIA, filas);
    }

    private void mostrarPersonas(String[] cabeceras, List<String[]> filas) {
        if (filas.isEmpty()) {
            terminal.info("No hay registros para mostrar.");
        } else {
            terminal.tabla(cabeceras, filas);
        }
    }

    private void mostrarServicios(List<ServicioAlquiler> servicios) {
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

    private void mostrarPrendas(List<Prenda> prendas) {
        List<String[]> filas = new ArrayList<>();
        for (Prenda prenda : prendas) {
            filas.add(new String[] {prenda.getReferencia(), prenda.getTipo().name(), prenda.getColor(),
                prenda.getMarca(), prenda.getTalla(), Terminal.moneda(prenda.getValorAlquiler()),
                prenda.detalle()});
        }
        terminal.tabla(CABECERA_PRENDAS, filas);
    }

    private void fichaPrenda(Prenda prenda) {
        terminal.ficha("Prenda", List.of(
                new String[] {"Referencia", prenda.getReferencia()},
                new String[] {"Tipo", prenda.getTipo().name()},
                new String[] {"Color", prenda.getColor()},
                new String[] {"Marca", prenda.getMarca()},
                new String[] {"Talla", prenda.getTalla()},
                new String[] {"Alquiler", Terminal.moneda(prenda.getValorAlquiler())},
                new String[] {"Detalle", prenda.detalle()}));
    }

    private void fichaCliente(Cliente cliente) {
        terminal.ficha("Cliente", List.of(
                new String[] {"Identificación", cliente.getIdentificacion()},
                new String[] {"Nombre", cliente.getNombre()},
                new String[] {"Dirección", cliente.getDireccion()},
                new String[] {"Teléfono", cliente.getTelefono()},
                new String[] {"Correo", cliente.getCorreo()}));
    }

    private void fichaEmpleado(Empleado empleado) {
        terminal.ficha("Empleado", List.of(
                new String[] {"Identificación", empleado.getIdentificacion()},
                new String[] {"Nombre", empleado.getNombre()},
                new String[] {"Dirección", empleado.getDireccion()},
                new String[] {"Teléfono", empleado.getTelefono()},
                new String[] {"Cargo", empleado.getCargo()}));
    }

    private void fichaServicio(ServicioAlquiler servicio) {
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

    private static String titulo(TipoPrenda tipo) {
        return switch (tipo) {
            case DAMA -> "Vestidos de dama";
            case CABALLERO -> "Trajes de caballero";
            case DISFRAZ -> "Disfraces";
        };
    }

    private static String formatear(LocalDate fecha) {
        return fecha.format(DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA));
    }
}
