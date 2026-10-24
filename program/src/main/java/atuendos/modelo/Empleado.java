/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Empleado que atiende los alquileres.
 */
public class Empleado extends Persona {

    private final String cargo;

    /**
     * Crea un empleado.
     *
     * @param identificacion documento que lo identifica
     * @param nombre nombre completo
     * @param direccion dirección de residencia
     * @param telefono número de teléfono
     * @param cargo cargo que desempeña
     */
    public Empleado(String identificacion, String nombre, String direccion, String telefono, String cargo) {
        super(identificacion, nombre, direccion, telefono);
        this.cargo = cargo;
    }

    /**
     * Devuelve el cargo que desempeña el empleado.
     *
     * @return el cargo
     */
    public String getCargo() {
        return cargo;
    }

    @Override
    public String toString() {
        return String.format("%s (%s), %s, %s, %s, %s", getNombre(), getIdentificacion(),
                getDireccion(), getTelefono(), cargo);
    }
}
