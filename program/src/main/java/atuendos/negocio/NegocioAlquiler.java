/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.negocio;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.fabrica.DatosPrenda;
import atuendos.fabrica.FabricaPrenda;
import atuendos.modelo.Cliente;
import atuendos.modelo.Empleado;
import atuendos.modelo.Prenda;
import atuendos.modelo.ServicioAlquiler;
import atuendos.modelo.TipoPrenda;
import atuendos.persistencia.ClienteRepositorio;
import atuendos.persistencia.ClienteRepositorioSqlite;
import atuendos.persistencia.Conexion;
import atuendos.persistencia.EmpleadoRepositorio;
import atuendos.persistencia.EmpleadoRepositorioSqlite;
import atuendos.persistencia.PrendaRepositorio;
import atuendos.persistencia.PrendaRepositorioSqlite;
import atuendos.persistencia.ServicioRepositorio;
import atuendos.persistencia.ServicioRepositorioSqlite;

import java.sql.Connection;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fachada de la aplicación (patrón Facade): ofrece a la consola una interfaz
 * simple y oculta los almacenes, la fábrica de prendas y la conexión. Aquí
 * viven las reglas de negocio del registro de servicios.
 */
public class NegocioAlquiler {

    private final ClienteRepositorio clientes;
    private final EmpleadoRepositorio empleados;
    private final PrendaRepositorio prendas;
    private final ServicioRepositorio servicios;
    private final Clock reloj;

    /**
     * Crea la fachada con sus almacenes y el reloj del sistema.
     *
     * @param clientes almacén de clientes
     * @param empleados almacén de empleados
     * @param prendas almacén de prendas
     * @param servicios almacén de servicios
     * @param reloj reloj que da la fecha de solicitud
     */
    public NegocioAlquiler(ClienteRepositorio clientes, EmpleadoRepositorio empleados,
            PrendaRepositorio prendas, ServicioRepositorio servicios, Clock reloj) {
        this.clientes = clientes;
        this.empleados = empleados;
        this.prendas = prendas;
        this.servicios = servicios;
        this.reloj = reloj;
    }

    /**
     * Arma la fachada con la conexión única a la base de datos y la hora del sistema.
     *
     * @return la fachada lista para usar
     * @throws AtuendosException si no se puede abrir la base de datos
     */
    public static NegocioAlquiler crear() throws AtuendosException {
        return sobre(Conexion.getInstancia().getConexion(), Clock.systemDefaultZone());
    }

    /**
     * Arma la fachada sobre una conexión y un reloj dados.
     *
     * @param conexion conexión abierta a la base de datos
     * @param reloj reloj que da la fecha de solicitud
     * @return la fachada lista para usar
     */
    public static NegocioAlquiler sobre(Connection conexion, Clock reloj) {
        ClienteRepositorio clientes = new ClienteRepositorioSqlite(conexion);
        EmpleadoRepositorio empleados = new EmpleadoRepositorioSqlite(conexion);
        PrendaRepositorio prendas = new PrendaRepositorioSqlite(conexion);
        ServicioRepositorio servicios = new ServicioRepositorioSqlite(conexion, clientes, empleados, prendas);
        return new NegocioAlquiler(clientes, empleados, prendas, servicios, reloj);
    }

    /**
     * Registra un cliente con todos sus datos.
     *
     * @param identificacion documento del cliente
     * @param nombre nombre completo
     * @param direccion dirección de residencia
     * @param telefono número de teléfono
     * @param correo correo electrónico
     * @return el cliente registrado
     * @throws AtuendosException si falta un dato o el cliente ya existe
     */
    public Cliente registrarCliente(String identificacion, String nombre, String direccion, String telefono,
            String correo) throws AtuendosException {
        Cliente cliente = new Cliente(
                obligatorio(Constantes.CAMPO_IDENTIFICACION, identificacion),
                obligatorio(Constantes.CAMPO_NOMBRE, nombre),
                obligatorio(Constantes.CAMPO_DIRECCION, direccion),
                obligatorio(Constantes.CAMPO_TELEFONO, telefono),
                obligatorio(Constantes.CAMPO_CORREO, correo));
        clientes.guardar(cliente);
        return cliente;
    }

    /**
     * Registra un empleado con todos sus datos.
     *
     * @param identificacion documento del empleado
     * @param nombre nombre completo
     * @param direccion dirección de residencia
     * @param telefono número de teléfono
     * @param cargo cargo que desempeña
     * @return el empleado registrado
     * @throws AtuendosException si falta un dato o el empleado ya existe
     */
    public Empleado registrarEmpleado(String identificacion, String nombre, String direccion, String telefono,
            String cargo) throws AtuendosException {
        Empleado empleado = new Empleado(
                obligatorio(Constantes.CAMPO_IDENTIFICACION, identificacion),
                obligatorio(Constantes.CAMPO_NOMBRE, nombre),
                obligatorio(Constantes.CAMPO_DIRECCION, direccion),
                obligatorio(Constantes.CAMPO_TELEFONO, telefono),
                obligatorio(Constantes.CAMPO_CARGO, cargo));
        empleados.guardar(empleado);
        return empleado;
    }

    /**
     * Registra una prenda, creada con la fábrica de su tipo.
     *
     * @param datos datos de la prenda
     * @return la prenda registrada
     * @throws AtuendosException si falta un dato, es inválido o la prenda ya existe
     */
    public Prenda registrarPrenda(DatosPrenda datos) throws AtuendosException {
        if (datos.tipo() == null) {
            throw new AtuendosException(String.format(Constantes.MSG_CAMPO_OBLIGATORIO, Constantes.CAMPO_TIPO));
        }
        Prenda prenda = FabricaPrenda.para(datos.tipo()).crearPrenda(datos);
        prendas.guardar(prenda);
        return prenda;
    }

    /**
     * Registra un servicio de alquiler. Exige que el cliente, el empleado y
     * las prendas estén registrados, y que las prendas estén disponibles para
     * la fecha. La fecha de solicitud es la del sistema y el número es el
     * siguiente consecutivo.
     *
     * @param clienteId identificación del cliente
     * @param empleadoId identificación del empleado
     * @param referencias referencias de las prendas a alquilar
     * @param fechaAlquiler fecha para la que se alquilan
     * @return el servicio registrado
     * @throws AtuendosException si algo no existe, no está disponible o la fecha ya pasó
     */
    public ServicioAlquiler registrarServicio(String clienteId, String empleadoId, List<String> referencias,
            LocalDate fechaAlquiler) throws AtuendosException {
        LocalDate hoy = LocalDate.now(reloj);
        if (fechaAlquiler == null) {
            throw new AtuendosException(
                    String.format(Constantes.MSG_CAMPO_OBLIGATORIO, Constantes.CAMPO_FECHA_ALQUILER));
        }
        if (fechaAlquiler.isBefore(hoy)) {
            throw new AtuendosException(String.format(Constantes.MSG_FECHA_PASADA, formatear(hoy)));
        }
        String idCliente = obligatorio(Constantes.CAMPO_IDENTIFICACION, clienteId);
        String idEmpleado = obligatorio(Constantes.CAMPO_IDENTIFICACION, empleadoId);
        Cliente cliente = clientes.buscar(idCliente).orElseThrow(
                () -> new AtuendosException(String.format(Constantes.MSG_CLIENTE_NO_REGISTRADO, idCliente)));
        Empleado empleado = empleados.buscar(idEmpleado).orElseThrow(
                () -> new AtuendosException(String.format(Constantes.MSG_EMPLEADO_NO_REGISTRADO, idEmpleado)));
        if (referencias == null || referencias.isEmpty()) {
            throw new AtuendosException(Constantes.MSG_SIN_PRENDAS);
        }
        Set<String> vistas = new HashSet<>();
        List<Prenda> alquiladas = new ArrayList<>();
        for (String referencia : referencias) {
            String ref = obligatorio(Constantes.CAMPO_REFERENCIA, referencia);
            if (!vistas.add(ref)) {
                throw new AtuendosException(String.format(Constantes.MSG_PRENDA_REPETIDA, ref));
            }
            Prenda prenda = prendas.buscar(ref).orElseThrow(
                    () -> new AtuendosException(String.format(Constantes.MSG_PRENDA_NO_REGISTRADA, ref)));
            if (servicios.estaAlquilada(ref, fechaAlquiler)) {
                throw new AtuendosException(
                        String.format(Constantes.MSG_PRENDA_NO_DISPONIBLE, ref, formatear(fechaAlquiler)));
            }
            alquiladas.add(prenda);
        }
        return servicios.guardar(hoy, fechaAlquiler, empleado, cliente, alquiladas);
    }

    /**
     * Consulta un servicio por su número.
     *
     * @param numero consecutivo del servicio
     * @return el servicio con todos sus datos
     * @throws AtuendosException si no existe
     */
    public ServicioAlquiler consultarServicio(int numero) throws AtuendosException {
        return servicios.buscar(numero).orElseThrow(
                () -> new AtuendosException(String.format(Constantes.MSG_SERVICIO_NO_EXISTE, numero)));
    }

    /**
     * Consulta un cliente y sus servicios vigentes, es decir, con fecha de
     * alquiler igual o posterior a la de hoy, ordenados por esa fecha.
     *
     * @param clienteId identificación del cliente
     * @return el cliente y sus servicios vigentes
     * @throws AtuendosException si el cliente no está registrado
     */
    public ServiciosDeCliente consultarServiciosDeCliente(String clienteId) throws AtuendosException {
        String id = obligatorio(Constantes.CAMPO_IDENTIFICACION, clienteId);
        Cliente cliente = clientes.buscar(id).orElseThrow(
                () -> new AtuendosException(String.format(Constantes.MSG_CLIENTE_NO_REGISTRADO, id)));
        return new ServiciosDeCliente(cliente, servicios.buscarPorCliente(id, LocalDate.now(reloj)));
    }

    /**
     * Consulta los servicios de una fecha de alquiler.
     *
     * @param fecha fecha de alquiler
     * @return los servicios de esa fecha, que pueden ser ninguno
     * @throws AtuendosException si falla la base de datos
     */
    public List<ServicioAlquiler> consultarServiciosPorFecha(LocalDate fecha) throws AtuendosException {
        if (fecha == null) {
            throw new AtuendosException(
                    String.format(Constantes.MSG_CAMPO_OBLIGATORIO, Constantes.CAMPO_FECHA_ALQUILER));
        }
        return servicios.buscarPorFecha(fecha);
    }

    /**
     * Consulta las prendas de una talla, separadas por tipo.
     *
     * @param talla talla buscada
     * @return las prendas por tipo; todos los tipos aparecen, aunque sin prendas
     * @throws AtuendosException si falta la talla o falla la base de datos
     */
    public Map<TipoPrenda, List<Prenda>> consultarPrendasPorTalla(String talla) throws AtuendosException {
        Map<TipoPrenda, List<Prenda>> porTipo = new EnumMap<>(TipoPrenda.class);
        for (TipoPrenda tipo : TipoPrenda.values()) {
            porTipo.put(tipo, new ArrayList<>());
        }
        for (Prenda prenda : prendas.buscarPorTalla(obligatorio(Constantes.CAMPO_TALLA, talla))) {
            porTipo.get(prenda.getTipo()).add(prenda);
        }
        return porTipo;
    }

    /**
     * Lista todas las prendas registradas.
     *
     * @return las prendas ordenadas por tipo y referencia
     * @throws AtuendosException si falla la base de datos
     */
    public List<Prenda> listarPrendas() throws AtuendosException {
        return prendas.listar();
    }

    /**
     * Lista todos los clientes registrados.
     *
     * @return los clientes ordenados por nombre
     * @throws AtuendosException si falla la base de datos
     */
    public List<Cliente> listarClientes() throws AtuendosException {
        return clientes.listar();
    }

    /**
     * Lista todos los empleados registrados.
     *
     * @return los empleados ordenados por nombre
     * @throws AtuendosException si falla la base de datos
     */
    public List<Empleado> listarEmpleados() throws AtuendosException {
        return empleados.listar();
    }

    private static String obligatorio(String campo, String valor) throws AtuendosException {
        if (valor == null || valor.isBlank()) {
            throw new AtuendosException(String.format(Constantes.MSG_CAMPO_OBLIGATORIO, campo));
        }
        return valor.trim();
    }

    private static String formatear(LocalDate fecha) {
        return fecha.format(DateTimeFormatter.ofPattern(Constantes.FORMATO_FECHA));
    }
}
