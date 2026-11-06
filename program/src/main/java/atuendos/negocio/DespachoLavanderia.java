/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.negocio;

import atuendos.comun.AtuendosException;
import atuendos.comun.Constantes;
import atuendos.modelo.ColaLavanderia;
import atuendos.modelo.Prenda;
import atuendos.modelo.PrendaEnLavanderia;
import atuendos.persistencia.LavanderiaRepositorio;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Despacho de lavandería y notificador del patrón Observer. Registra prendas,
 * entrega la cola de pendientes y envía tandas; después de cada envío con
 * prendas avisa a todos sus suscriptores, sin saber quiénes son.
 */
public class DespachoLavanderia {

    private final List<ObservadorLavanderia> observadores = new ArrayList<>();
    private final LavanderiaRepositorio lavanderia;

    /**
     * Crea el despacho sobre la lista de lavandería.
     *
     * @param lavanderia almacén de la lista de lavandería
     */
    public DespachoLavanderia(LavanderiaRepositorio lavanderia) {
        this.lavanderia = lavanderia;
    }

    /**
     * Agrega un suscriptor a la lista de avisos.
     *
     * @param observador suscriptor que recibirá los envíos
     */
    public void suscribir(ObservadorLavanderia observador) {
        observadores.add(observador);
    }

    /**
     * Retira un suscriptor de la lista de avisos.
     *
     * @param observador suscriptor que deja de recibir los envíos
     */
    public void desuscribir(ObservadorLavanderia observador) {
        observadores.remove(observador);
    }

    /**
     * Registra una prenda en la lista de lavandería.
     *
     * @param prenda prenda registrada en el sistema
     * @param prioritaria si se envía antes que las demás
     * @return el registro creado
     * @throws AtuendosException si la prenda ya está pendiente o falla la base de datos
     */
    public PrendaEnLavanderia registrar(Prenda prenda, boolean prioritaria) throws AtuendosException {
        return lavanderia.registrar(prenda, prioritaria);
    }

    /**
     * Entrega las prendas pendientes, sin enviarlas.
     *
     * @return la cola de pendientes, que se recorre en el orden de envío
     * @throws AtuendosException si falla la base de datos
     */
    public ColaLavanderia pendientes() throws AtuendosException {
        return lavanderia.pendientes();
    }

    /**
     * Envía una tanda: recorre la cola con su iterador hasta completar la
     * cantidad o agotar las pendientes, las marca como enviadas y, si salió al
     * menos una, avisa a cada suscriptor.
     *
     * @param cantidad cantidad de prendas que se pueden enviar
     * @param fecha fecha del envío
     * @return la notificación con la fecha y las prendas enviadas
     * @throws AtuendosException si la cantidad no es positiva o falla la base de datos
     */
    public EnvioLavanderia enviar(int cantidad, LocalDate fecha) throws AtuendosException {
        if (cantidad < 1) {
            throw new AtuendosException(Constantes.MSG_CANTIDAD_INVALIDA);
        }
        List<PrendaEnLavanderia> enviadas = new ArrayList<>();
        Iterator<PrendaEnLavanderia> iterador = lavanderia.pendientes().iterator();
        while (iterador.hasNext() && enviadas.size() < cantidad) {
            enviadas.add(iterador.next());
        }
        lavanderia.marcarEnviadas(enviadas, fecha);
        EnvioLavanderia envio = new EnvioLavanderia(fecha, enviadas);
        if (!enviadas.isEmpty()) {
            notificar(envio);
        }
        return envio;
    }

    private void notificar(EnvioLavanderia envio) {
        for (ObservadorLavanderia observador : List.copyOf(observadores)) {
            observador.prendasEnviadas(envio);
        }
    }
}
