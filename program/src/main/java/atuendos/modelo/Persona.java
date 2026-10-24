/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

/**
 * Persona que interviene en un alquiler: base de clientes y empleados.
 */
public abstract class Persona {

    private final String identificacion;
    private final String nombre;
    private final String direccion;
    private final String telefono;

    /**
     * Crea una persona.
     *
     * @param identificacion documento que la identifica
     * @param nombre nombre completo
     * @param direccion dirección de residencia
     * @param telefono número de teléfono
     */
    protected Persona(String identificacion, String nombre, String direccion, String telefono) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
    }

    /**
     * Devuelve el documento que identifica a la persona.
     *
     * @return la identificación
     */
    public String getIdentificacion() {
        return identificacion;
    }

    /**
     * Devuelve el nombre completo de la persona.
     *
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Devuelve la dirección de residencia de la persona.
     *
     * @return la dirección
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Devuelve el número de teléfono de la persona.
     *
     * @return el teléfono
     */
    public String getTelefono() {
        return telefono;
    }
}
