/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.Prenda;
import atuendos.modelo.PrendaEnLavanderia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista de lavandería en SQLite. Una prenda enviada deja de estar pendiente y
 * puede volver a registrarse.
 */
public class LavanderiaRepositorioSqlite implements LavanderiaRepositorio {

    private static final String INSERTAR = "INSERT INTO lavanderia (prenda_ref, prioritaria) VALUES (?, ?)";
    private static final String CONTAR_PENDIENTE =
            "SELECT COUNT(*) FROM lavanderia WHERE prenda_ref = ? AND enviada = 0";
    private static final String PENDIENTES = "SELECT orden, prenda_ref, prioritaria FROM lavanderia "
            + "WHERE enviada = 0 ORDER BY prioritaria DESC, orden";
    private static final String MARCAR_ENVIADA =
            "UPDATE lavanderia SET enviada = 1, fecha_envio = ? WHERE orden = ?";

    private final Connection conexion;
    private final PrendaRepositorio prendas;

    /**
     * Crea la lista sobre una conexión abierta.
     *
     * @param conexion conexión a la base de datos
     * @param prendas almacén de prendas, para reconstruirlas
     */
    public LavanderiaRepositorioSqlite(Connection conexion, PrendaRepositorio prendas) {
        this.conexion = conexion;
        this.prendas = prendas;
    }

    @Override
    public PrendaEnLavanderia registrar(Prenda prenda, boolean prioritaria) throws AtuendosException {
        try {
            try (PreparedStatement sentencia = conexion.prepareStatement(CONTAR_PENDIENTE)) {
                sentencia.setString(1, prenda.getReferencia());
                try (ResultSet fila = sentencia.executeQuery()) {
                    if (fila.next() && fila.getInt(1) > 0) {
                        throw new AtuendosException(
                                String.format(Constantes.MSG_YA_EN_LAVANDERIA, prenda.getReferencia()));
                    }
                }
            }
            try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR,
                    Statement.RETURN_GENERATED_KEYS)) {
                sentencia.setString(1, prenda.getReferencia());
                sentencia.setInt(2, prioritaria ? 1 : 0);
                sentencia.executeUpdate();
                try (ResultSet claves = sentencia.getGeneratedKeys()) {
                    claves.next();
                    return new PrendaEnLavanderia(claves.getInt(1), prenda, prioritaria);
                }
            }
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }

    @Override
    public List<PrendaEnLavanderia> pendientes() throws AtuendosException {
        List<int[]> filas = new ArrayList<>();
        List<String> referencias = new ArrayList<>();
        try (PreparedStatement sentencia = conexion.prepareStatement(PENDIENTES);
                ResultSet resultado = sentencia.executeQuery()) {
            while (resultado.next()) {
                filas.add(new int[] {resultado.getInt(1), resultado.getInt(3)});
                referencias.add(resultado.getString(2));
            }
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
        List<PrendaEnLavanderia> lista = new ArrayList<>();
        for (int i = 0; i < filas.size(); i++) {
            String referencia = referencias.get(i);
            Prenda prenda = prendas.buscar(referencia).orElseThrow(() -> new AtuendosException(
                    String.format(Constantes.MSG_LAVANDERIA_INCONSISTENTE, referencia)));
            lista.add(new PrendaEnLavanderia(filas.get(i)[0], prenda, filas.get(i)[1] == 1));
        }
        return lista;
    }

    @Override
    public List<PrendaEnLavanderia> enviar(int cantidad, LocalDate fecha) throws AtuendosException {
        List<PrendaEnLavanderia> enviadas = new ArrayList<>(pendientes().stream().limit(cantidad).toList());
        try {
            boolean autoConfirmar = conexion.getAutoCommit();
            conexion.setAutoCommit(false);
            try (PreparedStatement sentencia = conexion.prepareStatement(MARCAR_ENVIADA)) {
                for (PrendaEnLavanderia registro : enviadas) {
                    sentencia.setString(1, fecha.toString());
                    sentencia.setInt(2, registro.orden());
                    sentencia.addBatch();
                }
                sentencia.executeBatch();
                conexion.commit();
            } catch (SQLException e) {
                conexion.rollback();
                throw e;
            } finally {
                conexion.setAutoCommit(autoConfirmar);
            }
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
        return enviadas;
    }
}
