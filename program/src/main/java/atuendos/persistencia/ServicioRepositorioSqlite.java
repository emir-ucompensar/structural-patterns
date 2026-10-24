/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Cliente;
import atuendos.modelo.Empleado;
import atuendos.modelo.Prenda;
import atuendos.modelo.ServicioAlquiler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Almacén de servicios de alquiler en SQLite. Guarda el servicio y sus prendas
 * en una sola transacción, y rehidrata clientes, empleados y prendas con sus
 * propios almacenes.
 */
public class ServicioRepositorioSqlite implements ServicioRepositorio {

    private static final String INSERTAR_SERVICIO = "INSERT INTO servicio "
            + "(fecha_solicitud, fecha_alquiler, empleado_id, cliente_id) VALUES (?, ?, ?, ?)";
    private static final String INSERTAR_PRENDA =
            "INSERT INTO servicio_prenda (servicio_numero, prenda_ref) VALUES (?, ?)";
    private static final String BUSCAR = "SELECT fecha_solicitud, fecha_alquiler, empleado_id, cliente_id "
            + "FROM servicio WHERE numero = ?";
    private static final String PRENDAS_DEL_SERVICIO =
            "SELECT prenda_ref FROM servicio_prenda WHERE servicio_numero = ? ORDER BY prenda_ref";
    private static final String ALQUILADA = "SELECT COUNT(*) FROM servicio_prenda sp "
            + "JOIN servicio s ON s.numero = sp.servicio_numero "
            + "WHERE sp.prenda_ref = ? AND s.fecha_alquiler = ?";

    private static final String NUMEROS_POR_CLIENTE = "SELECT numero FROM servicio "
            + "WHERE cliente_id = ? AND fecha_alquiler >= ? ORDER BY fecha_alquiler, numero";
    private static final String NUMEROS_POR_FECHA =
            "SELECT numero FROM servicio WHERE fecha_alquiler = ? ORDER BY numero";

    private final Connection conexion;
    private final ClienteRepositorio clientes;
    private final EmpleadoRepositorio empleados;
    private final PrendaRepositorio prendas;

    /**
     * Crea el almacén sobre una conexión abierta.
     *
     * @param conexion conexión a la base de datos
     * @param clientes almacén de clientes
     * @param empleados almacén de empleados
     * @param prendas almacén de prendas
     */
    public ServicioRepositorioSqlite(Connection conexion, ClienteRepositorio clientes,
            EmpleadoRepositorio empleados, PrendaRepositorio prendas) {
        this.conexion = conexion;
        this.clientes = clientes;
        this.empleados = empleados;
        this.prendas = prendas;
    }

    @Override
    public ServicioAlquiler guardar(LocalDate fechaSolicitud, LocalDate fechaAlquiler, Empleado empleado,
            Cliente cliente, List<Prenda> prendasAlquiladas) throws AtuendosException {
        try {
            boolean autoConfirmar = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try {
                int numero;
                try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR_SERVICIO,
                        Statement.RETURN_GENERATED_KEYS)) {
                    sentencia.setString(1, fechaSolicitud.toString());
                    sentencia.setString(2, fechaAlquiler.toString());
                    sentencia.setString(3, empleado.getIdentificacion());
                    sentencia.setString(4, cliente.getIdentificacion());
                    sentencia.executeUpdate();
                    try (ResultSet claves = sentencia.getGeneratedKeys()) {
                        claves.next();
                        numero = claves.getInt(1);
                    }
                }
                try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR_PRENDA)) {
                    for (Prenda prenda : prendasAlquiladas) {
                        sentencia.setInt(1, numero);
                        sentencia.setString(2, prenda.getReferencia());
                        sentencia.addBatch();
                    }
                    sentencia.executeBatch();
                }
                conexion.commit();
                return new ServicioAlquiler(numero, fechaSolicitud, fechaAlquiler, empleado, cliente,
                        prendasAlquiladas);
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(autoConfirmar);
            }
        } catch (SQLException e) {
            throw ErroresBaseDatos.traducir(e);
        }
    }

    @Override
    public Optional<ServicioAlquiler> buscar(int numero) throws AtuendosException {
        LocalDate fechaSolicitud;
        LocalDate fechaAlquiler;
        String empleadoId;
        String clienteId;
        List<String> referencias = new ArrayList<>();
        try {
            try (PreparedStatement sentencia = conexion.prepareStatement(BUSCAR)) {
                sentencia.setInt(1, numero);
                try (ResultSet fila = sentencia.executeQuery()) {
                    if (!fila.next()) {
                        return Optional.empty();
                    }
                    fechaSolicitud = LocalDate.parse(fila.getString(1));
                    fechaAlquiler = LocalDate.parse(fila.getString(2));
                    empleadoId = fila.getString(3);
                    clienteId = fila.getString(4);
                }
            }
            try (PreparedStatement sentencia = conexion.prepareStatement(PRENDAS_DEL_SERVICIO)) {
                sentencia.setInt(1, numero);
                try (ResultSet filas = sentencia.executeQuery()) {
                    while (filas.next()) {
                        referencias.add(filas.getString(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw ErroresBaseDatos.traducir(e);
        }
        String inconsistente = String.format(Constantes.MSG_SERVICIO_INCONSISTENTE, numero);
        Empleado empleado = empleados.buscar(empleadoId).orElseThrow(() -> new AtuendosException(inconsistente));
        Cliente cliente = clientes.buscar(clienteId).orElseThrow(() -> new AtuendosException(inconsistente));
        List<Prenda> lista = new ArrayList<>();
        for (String referencia : referencias) {
            lista.add(prendas.buscar(referencia).orElseThrow(() -> new AtuendosException(inconsistente)));
        }
        return Optional.of(new ServicioAlquiler(numero, fechaSolicitud, fechaAlquiler, empleado, cliente, lista));
    }

    @Override
    public boolean estaAlquilada(String referencia, LocalDate fecha) throws AtuendosException {
        try (PreparedStatement sentencia = conexion.prepareStatement(ALQUILADA)) {
            sentencia.setString(1, referencia);
            sentencia.setString(2, fecha.toString());
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() && fila.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw ErroresBaseDatos.traducir(e);
        }
    }

    @Override
    public List<ServicioAlquiler> buscarPorCliente(String clienteId, LocalDate desde) throws AtuendosException {
        return cargar(NUMEROS_POR_CLIENTE, clienteId, desde.toString());
    }

    @Override
    public List<ServicioAlquiler> buscarPorFecha(LocalDate fecha) throws AtuendosException {
        return cargar(NUMEROS_POR_FECHA, fecha.toString());
    }

    private List<ServicioAlquiler> cargar(String sql, String... parametros) throws AtuendosException {
        List<Integer> numeros = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                sentencia.setString(i + 1, parametros[i]);
            }
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    numeros.add(filas.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw ErroresBaseDatos.traducir(e);
        }
        List<ServicioAlquiler> servicios = new ArrayList<>();
        for (int numero : numeros) {
            buscar(numero).ifPresent(servicios::add);
        }
        return servicios;
    }
}
