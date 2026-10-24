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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Almacén de clientes en SQLite.
 */
public class ClienteRepositorioSqlite implements ClienteRepositorio {

    private static final String INSERTAR =
            "INSERT INTO cliente (identificacion, nombre, direccion, telefono, correo) VALUES (?, ?, ?, ?, ?)";
    private static final String BUSCAR =
            "SELECT identificacion, nombre, direccion, telefono, correo FROM cliente WHERE identificacion = ?";

    private final Connection conexion;

    /**
     * Crea el almacén sobre una conexión abierta.
     *
     * @param conexion conexión a la base de datos
     */
    public ClienteRepositorioSqlite(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void guardar(Cliente cliente) throws AtuendosException {
        if (buscar(cliente.getIdentificacion()).isPresent()) {
            throw new AtuendosException(String.format(Constantes.MSG_CLIENTE_EXISTE, cliente.getIdentificacion()));
        }
        try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR)) {
            sentencia.setString(1, cliente.getIdentificacion());
            sentencia.setString(2, cliente.getNombre());
            sentencia.setString(3, cliente.getDireccion());
            sentencia.setString(4, cliente.getTelefono());
            sentencia.setString(5, cliente.getCorreo());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }

    @Override
    public Optional<Cliente> buscar(String identificacion) throws AtuendosException {
        try (PreparedStatement sentencia = conexion.prepareStatement(BUSCAR)) {
            sentencia.setString(1, identificacion);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Cliente(fila.getString(1), fila.getString(2), fila.getString(3),
                        fila.getString(4), fila.getString(5)));
            }
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }
}
