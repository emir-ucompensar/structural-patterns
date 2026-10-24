/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.modelo;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de alquiler: un cliente, un empleado y una o varias prendas para
 * una fecha.
 */
public class ServicioAlquiler {

    private final int numero;
    private final LocalDate fechaSolicitud;
    private final LocalDate fechaAlquiler;
    private final Empleado empleado;
    private final Cliente cliente;
    private final List<Prenda> prendas;

    /**
     * Crea un servicio de alquiler.
     *
     * @param numero consecutivo que lo identifica
     * @param fechaSolicitud fecha del sistema en que se registró
     * @param fechaAlquiler fecha para la que se alquilan las prendas
     * @param empleado empleado que lo atiende
     * @param cliente cliente que alquila
     * @param prendas prendas alquiladas
     */
    public ServicioAlquiler(int numero, LocalDate fechaSolicitud, LocalDate fechaAlquiler, Empleado empleado,
            Cliente cliente, List<Prenda> prendas) {
        this.numero = numero;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaAlquiler = fechaAlquiler;
        this.empleado = empleado;
        this.cliente = cliente;
        this.prendas = List.copyOf(prendas);
    }

    /**
     * Devuelve el consecutivo que identifica al servicio.
     *
     * @return el número del servicio
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Devuelve la fecha del sistema en que se registró el servicio.
     *
     * @return la fecha de solicitud
     */
    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    /**
     * Devuelve la fecha para la que se alquilan las prendas.
     *
     * @return la fecha de alquiler
     */
    public LocalDate getFechaAlquiler() {
        return fechaAlquiler;
    }

    /**
     * Devuelve el empleado que atendió el servicio.
     *
     * @return el empleado
     */
    public Empleado getEmpleado() {
        return empleado;
    }

    /**
     * Devuelve el cliente que alquila.
     *
     * @return el cliente
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Devuelve las prendas alquiladas en el servicio.
     *
     * @return las prendas, en una lista inmutable
     */
    public List<Prenda> getPrendas() {
        return prendas;
    }
}
