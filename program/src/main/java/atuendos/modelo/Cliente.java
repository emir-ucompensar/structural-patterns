/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Cliente que alquila prendas.
 */
public class Cliente extends Persona {

    private final String correo;

    /**
     * Crea un cliente.
     *
     * @param identificacion documento que lo identifica
     * @param nombre nombre completo
     * @param direccion dirección de residencia
     * @param telefono número de teléfono
     * @param correo correo electrónico
     */
    public Cliente(String identificacion, String nombre, String direccion, String telefono, String correo) {
        super(identificacion, nombre, direccion, telefono);
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    @Override
    public String toString() {
        return String.format("%s (%s), %s, %s, %s, %s", getNombre(), getIdentificacion(),
                getDireccion(), getTelefono(), correo);
    }
}
