/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Empleado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Almacén de empleados en SQLite.
 */
public class EmpleadoRepositorioSqlite implements EmpleadoRepositorio {

    private static final String INSERTAR =
            "INSERT INTO empleado (identificacion, nombre, direccion, telefono, cargo) VALUES (?, ?, ?, ?, ?)";
    private static final String BUSCAR =
            "SELECT identificacion, nombre, direccion, telefono, cargo FROM empleado WHERE identificacion = ?";

    private final Connection conexion;

    /**
     * Crea el almacén sobre una conexión abierta.
     *
     * @param conexion conexión a la base de datos
     */
    public EmpleadoRepositorioSqlite(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void guardar(Empleado empleado) throws AtuendosException {
        if (buscar(empleado.getIdentificacion()).isPresent()) {
            throw new AtuendosException(String.format(Constantes.MSG_EMPLEADO_EXISTE, empleado.getIdentificacion()));
        }
        try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR)) {
            sentencia.setString(1, empleado.getIdentificacion());
            sentencia.setString(2, empleado.getNombre());
            sentencia.setString(3, empleado.getDireccion());
            sentencia.setString(4, empleado.getTelefono());
            sentencia.setString(5, empleado.getCargo());
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }

    @Override
    public Optional<Empleado> buscar(String identificacion) throws AtuendosException {
        try (PreparedStatement sentencia = conexion.prepareStatement(BUSCAR)) {
            sentencia.setString(1, identificacion);
            try (ResultSet fila = sentencia.executeQuery()) {
                if (!fila.next()) {
                    return Optional.empty();
                }
                return Optional.of(new Empleado(fila.getString(1), fila.getString(2), fila.getString(3),
                        fila.getString(4), fila.getString(5)));
            }
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }
}
