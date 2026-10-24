/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.persistencia;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.fabrica.DatosPrenda;
import atuendos.fabrica.FabricaPrenda;
import atuendos.modelo.Disfraz;
import atuendos.modelo.Prenda;
import atuendos.modelo.TipoPrenda;
import atuendos.modelo.TrajeCaballero;
import atuendos.modelo.VestidoDama;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Almacén de prendas en SQLite. Al leer una fila, usa la fábrica del tipo de
 * prenda para reconstruir el objeto.
 */
public class PrendaRepositorioSqlite implements PrendaRepositorio {

    private static final String INSERTAR = "INSERT INTO prenda (referencia, tipo, color, marca, talla, "
            + "valor_alquiler, pedreria, altura, cantidad_piezas, tipo_traje, aderezo, nombre_disfraz) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String COLUMNAS = "SELECT referencia, tipo, color, marca, talla, valor_alquiler, "
            + "pedreria, altura, cantidad_piezas, tipo_traje, aderezo, nombre_disfraz FROM prenda ";
    private static final String BUSCAR = COLUMNAS + "WHERE referencia = ?";
    private static final String LISTAR = COLUMNAS + "ORDER BY tipo, referencia";
    private static final String POR_TALLAS = COLUMNAS + "WHERE UPPER(talla) IN (%s) ORDER BY tipo, referencia";

    private final Connection conexion;

    /**
     * Crea el almacén sobre una conexión abierta.
     *
     * @param conexion conexión a la base de datos
     */
    public PrendaRepositorioSqlite(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void guardar(Prenda prenda) throws AtuendosException {
        if (buscar(prenda.getReferencia()).isPresent()) {
            throw new AtuendosException(String.format(Constantes.MSG_PRENDA_EXISTE, prenda.getReferencia()));
        }
        Integer pedreria = null;
        String altura = null;
        Integer cantidadPiezas = null;
        String tipoTraje = null;
        String aderezo = null;
        String nombreDisfraz = null;
        if (prenda instanceof VestidoDama dama) {
            pedreria = dama.isPedreria() ? 1 : 0;
            altura = dama.getAltura().name();
            cantidadPiezas = dama.getCantidadPiezas();
        } else if (prenda instanceof TrajeCaballero traje) {
            tipoTraje = traje.getTipoTraje().name();
            aderezo = traje.getAderezo().name();
        } else if (prenda instanceof Disfraz disfraz) {
            nombreDisfraz = disfraz.getNombre();
        }
        try (PreparedStatement sentencia = conexion.prepareStatement(INSERTAR)) {
            sentencia.setString(1, prenda.getReferencia());
            sentencia.setString(2, prenda.getTipo().name());
            sentencia.setString(3, prenda.getColor());
            sentencia.setString(4, prenda.getMarca());
            sentencia.setString(5, prenda.getTalla());
            sentencia.setDouble(6, prenda.getValorAlquiler());
            poner(sentencia, 7, pedreria, Types.INTEGER);
            poner(sentencia, 8, altura, Types.VARCHAR);
            poner(sentencia, 9, cantidadPiezas, Types.INTEGER);
            poner(sentencia, 10, tipoTraje, Types.VARCHAR);
            poner(sentencia, 11, aderezo, Types.VARCHAR);
            poner(sentencia, 12, nombreDisfraz, Types.VARCHAR);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }

    @Override
    public Optional<Prenda> buscar(String referencia) throws AtuendosException {
        try (PreparedStatement sentencia = conexion.prepareStatement(BUSCAR)) {
            sentencia.setString(1, referencia);
            try (ResultSet fila = sentencia.executeQuery()) {
                return fila.next() ? Optional.of(desdeFila(fila)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }

    @Override
    public List<Prenda> listar() throws AtuendosException {
        try (PreparedStatement sentencia = conexion.prepareStatement(LISTAR)) {
            return leerTodas(sentencia);
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }

    @Override
    public List<Prenda> buscarPorTallas(List<String> tallas) throws AtuendosException {
        if (tallas.isEmpty()) {
            return new ArrayList<>();
        }
        String marcadores = String.join(", ", Collections.nCopies(tallas.size(), "?"));
        try (PreparedStatement sentencia = conexion.prepareStatement(String.format(POR_TALLAS, marcadores))) {
            for (int i = 0; i < tallas.size(); i++) {
                sentencia.setString(i + 1, tallas.get(i).toUpperCase(Locale.ROOT));
            }
            return leerTodas(sentencia);
        } catch (SQLException e) {
            throw new AtuendosException(Constantes.MSG_ERROR_BD, e);
        }
    }

    private static List<Prenda> leerTodas(PreparedStatement sentencia) throws SQLException, AtuendosException {
        List<Prenda> lista = new ArrayList<>();
        try (ResultSet filas = sentencia.executeQuery()) {
            while (filas.next()) {
                lista.add(desdeFila(filas));
            }
        }
        return lista;
    }

    private static Prenda desdeFila(ResultSet fila) throws SQLException, AtuendosException {
        TipoPrenda tipo = TipoPrenda.valueOf(fila.getString("tipo"));
        Map<String, String> especificos = new HashMap<>();
        String pedreria = fila.getString("pedreria");
        agregar(especificos, Constantes.CAMPO_PEDRERIA,
                pedreria == null ? null : String.valueOf("1".equals(pedreria)));
        agregar(especificos, Constantes.CAMPO_ALTURA, fila.getString("altura"));
        agregar(especificos, Constantes.CAMPO_CANTIDAD_PIEZAS, fila.getString("cantidad_piezas"));
        agregar(especificos, Constantes.CAMPO_TIPO_TRAJE, fila.getString("tipo_traje"));
        agregar(especificos, Constantes.CAMPO_ADEREZO, fila.getString("aderezo"));
        agregar(especificos, Constantes.CAMPO_NOMBRE_DISFRAZ, fila.getString("nombre_disfraz"));
        DatosPrenda datos = new DatosPrenda(tipo, fila.getString("referencia"), fila.getString("color"),
                fila.getString("marca"), fila.getString("talla"), fila.getDouble("valor_alquiler"), especificos);
        return FabricaPrenda.para(tipo).crearPrenda(datos);
    }

    private static void poner(PreparedStatement sentencia, int posicion, Object valor, int tipoSql)
            throws SQLException {
        if (valor == null) {
            sentencia.setNull(posicion, tipoSql);
        } else {
            sentencia.setObject(posicion, valor);
        }
    }

    private static void agregar(Map<String, String> datos, String campo, String valor) {
        if (valor != null) {
            datos.put(campo, valor);
        }
    }
}
