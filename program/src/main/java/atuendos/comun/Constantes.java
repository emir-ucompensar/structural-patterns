/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.comun;

/**
 * Constantes compartidas por toda la aplicación.
 */
public final class Constantes {

    /** URL JDBC de la base de datos, un archivo en el directorio de ejecución. */
    public static final String URL_BASE_DATOS = "jdbc:sqlite:atuendos.db";

    /** Ruta del esquema dentro del classpath. */
    public static final String RECURSO_ESQUEMA = "/esquema.sql";

    /** Prefijo de los comentarios del esquema SQL. */
    public static final String COMENTARIO_SQL = "--";

    /** Separador de sentencias del esquema SQL. */
    public static final String SEPARADOR_SQL = ";";

    /** Mensaje cuando no se puede abrir la base de datos. */
    public static final String MSG_ERROR_CONEXION = "No se pudo abrir la base de datos.";

    /** Mensaje cuando no se puede leer el esquema empaquetado. */
    public static final String MSG_ERROR_ESQUEMA = "No se pudo leer el esquema de la base de datos.";

    /** Mensaje cuando falta un dato obligatorio; recibe el nombre del campo. */
    public static final String MSG_CAMPO_OBLIGATORIO = "El campo '%s' es obligatorio.";

    /** Mensaje cuando un dato tiene un valor no admitido; recibe el campo y el valor. */
    public static final String MSG_VALOR_INVALIDO = "Valor inválido en '%s': %s.";

    /** Mensaje cuando un número no puede ser negativo; recibe el nombre del campo. */
    public static final String MSG_VALOR_NEGATIVO = "El campo '%s' no puede ser negativo.";

    /** Mensaje cuando la fábrica recibe datos de otro tipo de prenda; recibe el tipo. */
    public static final String MSG_TIPO_INCORRECTO = "Los datos no corresponden al tipo de prenda %s.";

    /** Nombres de los campos de una prenda, usados en datos y en mensajes. */
    public static final String CAMPO_REFERENCIA = "referencia";
    public static final String CAMPO_COLOR = "color";
    public static final String CAMPO_MARCA = "marca";
    public static final String CAMPO_TALLA = "talla";
    public static final String CAMPO_VALOR_ALQUILER = "valorAlquiler";
    public static final String CAMPO_PEDRERIA = "pedreria";
    public static final String CAMPO_ALTURA = "altura";
    public static final String CAMPO_CANTIDAD_PIEZAS = "cantidadPiezas";
    public static final String CAMPO_TIPO_TRAJE = "tipoTraje";
    public static final String CAMPO_ADEREZO = "aderezo";
    public static final String CAMPO_NOMBRE_DISFRAZ = "nombreDisfraz";

    /** Mensaje genérico cuando falla una operación en la base de datos. */
    public static final String MSG_ERROR_BD = "Error al acceder a la base de datos.";

    /** Mensaje cuando ya hay un cliente con esa identificación; recibe la identificación. */
    public static final String MSG_CLIENTE_EXISTE = "Ya existe un cliente con la identificación %s.";

    /** Mensaje cuando ya hay un empleado con esa identificación; recibe la identificación. */
    public static final String MSG_EMPLEADO_EXISTE = "Ya existe un empleado con la identificación %s.";

    /** Mensaje cuando ya hay una prenda con esa referencia; recibe la referencia. */
    public static final String MSG_PRENDA_EXISTE = "Ya existe una prenda con la referencia %s.";

    /** Formato de las fechas que se muestran al usuario. */
    public static final String FORMATO_FECHA = "dd/MM/yyyy";

    /** Mensaje cuando el cliente del servicio no existe; recibe la identificación. */
    public static final String MSG_CLIENTE_NO_REGISTRADO = "El cliente %s no está registrado.";

    /** Mensaje cuando el empleado del servicio no existe; recibe la identificación. */
    public static final String MSG_EMPLEADO_NO_REGISTRADO = "El empleado %s no está registrado.";

    /** Mensaje cuando una prenda del servicio no existe; recibe la referencia. */
    public static final String MSG_PRENDA_NO_REGISTRADA = "La prenda %s no está registrada.";

    /** Mensaje cuando una prenda ya está alquilada; recibe la referencia y la fecha. */
    public static final String MSG_PRENDA_NO_DISPONIBLE = "La prenda %s no está disponible para el %s.";

    /** Mensaje cuando una referencia se repite dentro del servicio; recibe la referencia. */
    public static final String MSG_PRENDA_REPETIDA = "La prenda %s está repetida en el servicio.";

    /** Mensaje cuando el servicio no incluye prendas. */
    public static final String MSG_SIN_PRENDAS = "El servicio debe incluir al menos una prenda.";

    /** Mensaje cuando la fecha de alquiler ya pasó; recibe la fecha de hoy. */
    public static final String MSG_FECHA_PASADA = "La fecha de alquiler no puede ser anterior a hoy (%s).";

    /** Mensaje cuando un servicio guardado referencia datos que no existen; recibe el número. */
    public static final String MSG_SERVICIO_INCONSISTENTE = "El servicio %d tiene datos inconsistentes.";

    /** Nombres de los campos de una persona y del servicio, usados en mensajes. */
    public static final String CAMPO_TIPO = "tipo";
    public static final String CAMPO_IDENTIFICACION = "identificacion";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_DIRECCION = "direccion";
    public static final String CAMPO_TELEFONO = "telefono";
    public static final String CAMPO_CORREO = "correo";
    public static final String CAMPO_CARGO = "cargo";
    public static final String CAMPO_FECHA_ALQUILER = "fechaAlquiler";

    private Constantes() {
    }
}
