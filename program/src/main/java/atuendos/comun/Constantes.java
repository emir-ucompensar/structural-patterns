/*
 * Los Atuendos - administración de alquiler de vestuario.
 *
 * Archivo de autoría propia, generado con asistencia de herramientas de
 * inteligencia artificial (Claude Code).
 */
package atuendos.comun;

import java.util.List;

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

    /** Mensaje cuando el esquema de la base no coincide con el programa; recibe el detalle de SQLite. */
    public static final String MSG_BD_ESQUEMA_DISTINTO = "El esquema de la base de datos no coincide con el "
            + "programa (%s). Elimina atuendos.db para que se cree de nuevo.";

    /** Mensaje cuando otro proceso tiene bloqueada la base de datos. */
    public static final String MSG_BD_OCUPADA =
            "La base de datos está en uso por otro proceso. Intenta de nuevo en unos segundos.";

    /** Mensaje cuando la base de datos no se puede escribir. */
    public static final String MSG_BD_SOLO_LECTURA =
            "La base de datos es de solo lectura. Revisa los permisos de atuendos.db.";

    /** Mensaje cuando el registro ya existe en la base de datos. */
    public static final String MSG_BD_DUPLICADO = "El registro ya existe en la base de datos.";

    /** Mensaje cuando el registro apunta a datos que no existen. */
    public static final String MSG_BD_REFERENCIA = "El registro hace referencia a datos que no existen.";

    /** Mensaje cuando los datos incumplen una restricción de la base de datos. */
    public static final String MSG_BD_RESTRICCION = "Los datos no cumplen las restricciones de la base de datos.";

    /** Mensaje para un error de la base de datos que no se reconoce; recibe el detalle de SQLite. */
    public static final String MSG_BD_DESCONOCIDO = "Error al acceder a la base de datos: %s";

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

    /** Nombre del campo referencia, usado en datos y mensajes. */
    public static final String CAMPO_REFERENCIA = "referencia";
    /** Ejemplo de referencia que se muestra al pedirla. */
    public static final String EJEMPLO_REFERENCIA = "REF000";
    /** Nombre del campo color, usado en datos y mensajes. */
    public static final String CAMPO_COLOR = "color";
    /** Nombre del campo marca, usado en datos y mensajes. */
    public static final String CAMPO_MARCA = "marca";
    /** Nombre del campo talla, usado en datos y mensajes. */
    public static final String CAMPO_TALLA = "talla";
    /** Nombre del campo valor de alquiler, usado en datos y mensajes. */
    public static final String CAMPO_VALOR_ALQUILER = "valorAlquiler";
    /** Nombre del campo pedrería, usado en datos y mensajes. */
    public static final String CAMPO_PEDRERIA = "pedreria";
    /** Nombre del campo altura, usado en datos y mensajes. */
    public static final String CAMPO_ALTURA = "altura";
    /** Nombre del campo cantidad de piezas, usado en datos y mensajes. */
    public static final String CAMPO_CANTIDAD_PIEZAS = "cantidadPiezas";
    /** Nombre del campo tipo de traje, usado en datos y mensajes. */
    public static final String CAMPO_TIPO_TRAJE = "tipoTraje";
    /** Nombre del campo accesorio, usado en datos y mensajes. */
    public static final String CAMPO_ACCESORIO = "accesorio";
    /** Nombre del campo nombre del disfraz, usado en datos y mensajes. */
    public static final String CAMPO_NOMBRE_DISFRAZ = "nombreDisfraz";

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

    /** Nombre del campo tipo de prenda, usado en datos y mensajes. */
    public static final String CAMPO_TIPO = "tipo";
    /** Nombre del campo identificación, usado en datos y mensajes. */
    public static final String CAMPO_IDENTIFICACION = "identificacion";
    /** Nombre del campo nombre, usado en datos y mensajes. */
    public static final String CAMPO_NOMBRE = "nombre";
    /** Nombre del campo dirección, usado en datos y mensajes. */
    public static final String CAMPO_DIRECCION = "direccion";
    /** Nombre del campo teléfono, usado en datos y mensajes. */
    public static final String CAMPO_TELEFONO = "telefono";
    /** Nombre del campo correo, usado en datos y mensajes. */
    public static final String CAMPO_CORREO = "correo";
    /** Nombre del campo cargo, usado en datos y mensajes. */
    public static final String CAMPO_CARGO = "cargo";
    /** Nombre del campo fecha de alquiler, usado en datos y mensajes. */
    public static final String CAMPO_FECHA_ALQUILER = "fechaAlquiler";

    /** Mensaje cuando no existe un servicio con ese número; recibe el número. */
    public static final String MSG_SERVICIO_NO_EXISTE = "No existe un servicio con el número %d.";

    /** Mensaje cuando la prenda ya espera envío a lavandería; recibe la referencia. */
    public static final String MSG_YA_EN_LAVANDERIA = "La prenda %s ya está en la lista de lavandería.";

    /** Mensaje cuando la cantidad a enviar no es positiva. */
    public static final String MSG_CANTIDAD_INVALIDA = "La cantidad a enviar debe ser mayor que cero.";

    /** Mensaje cuando un registro de lavandería apunta a una prenda que no existe; recibe la referencia. */
    public static final String MSG_LAVANDERIA_INCONSISTENTE =
            "El registro de lavandería de la prenda %s es inconsistente.";

    /** Nombre del campo cantidad a enviar, usado en datos y mensajes. */
    public static final String CAMPO_CANTIDAD = "cantidad";

    /** Tallas admitidas para las prendas, de menor a mayor. */
    public static final List<String> TALLAS = List.of("XS", "S", "M", "L", "XL", "XXL");

    /** Mensaje cuando la talla no está entre las admitidas; recibe la talla y la lista. */
    public static final String MSG_TALLA_INVALIDA = "La talla '%s' no es válida. Tallas admitidas: %s.";

    private Constantes() {
    }
}
