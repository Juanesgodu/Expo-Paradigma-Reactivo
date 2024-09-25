import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

// Modelo de datos
data class Proyecto(val nombre: String, val costo: Double, val gastos: Double, val riesgo: Double, val impuestos: Double) {
    // Función para calcular el valor total del proyecto sumando todos los factores
    fun valorTotalProyecto(): Double {
        return costo + gastos + riesgo + impuestos
    }
}

// Funciones para calcular cada aspecto

fun calcularCosto(esfuerzoHoras: Int, valorHora: Double, viaticos: Double, infraestructura: Double): Double {
    return (esfuerzoHoras * valorHora) + viaticos + infraestructura
}

fun calcularGastos(gastosFijos: Double, papeleria: Double, servicios: Double): Double {
    return gastosFijos + papeleria + servicios
}

fun calcularRiesgo(costoTotal: Double, porcentajeRiesgo: Double): Double {
    return if (porcentajeRiesgo <= 50) {
        costoTotal * (porcentajeRiesgo / 100)
    } else {
        throw IllegalArgumentException("El porcentaje de riesgo no puede superar el 50%")
    }
}

fun calcularImpuestos(costo: Double, gastos: Double, riesgo: Double): Double {
    val totalBase = costo + gastos + riesgo
    val retencionFuente = totalBase * 0.11
    val reteica = retencionFuente * 0.01
    val iva = (totalBase + retencionFuente + reteica) * 0.19
    return retencionFuente + reteica + iva
}

// Función principal utilizando Kotlin Flow
fun flujoCosteoProyecto(
    nombreProyecto: String,
    esfuerzoHoras: Int,
    valorHora: Double,
    viaticos: Double,
    infraestructura: Double,
    gastosFijos: Double,
    papeleria: Double,
    servicios: Double,
    porcentajeRiesgo: Double
): Flow<Proyecto> = flow {
    val costo = calcularCosto(esfuerzoHoras, valorHora, viaticos, infraestructura)
    emit(Proyecto(nombreProyecto, costo, 0.0, 0.0, 0.0))

    val gastos = calcularGastos(gastosFijos, papeleria, servicios)
    emit(Proyecto(nombreProyecto, costo, gastos, 0.0, 0.0))

    val riesgo = calcularRiesgo(costo + gastos, porcentajeRiesgo)
    emit(Proyecto(nombreProyecto, costo, gastos, riesgo, 0.0))

    val impuestos = calcularImpuestos(costo, gastos, riesgo)
    emit(Proyecto(nombreProyecto, costo, gastos, riesgo, impuestos))
}

// Función para correr el flujo de manera reactiva
fun calcularProyectoReactivo() = runBlocking {
    flujoCosteoProyecto(
        nombreProyecto = "Desarrollo de Software X",
        esfuerzoHoras = 200,
        valorHora = 50.0,
        viaticos = 500.0,
        infraestructura = 1000.0,
        gastosFijos = 300.0,
        papeleria = 50.0,
        servicios = 100.0,
        porcentajeRiesgo = 30.0
    ).collect { proyecto ->
        val valorTotal = proyecto.valorTotalProyecto()
        println("Proyecto: ${proyecto.nombre}, Costo: ${proyecto.costo}, Gastos: ${proyecto.gastos}, Riesgo: ${proyecto.riesgo}, Impuestos: ${proyecto.impuestos}, Valor Total: $valorTotal")
    }
}

// Ejecutar la función de cálculo reactivo
fun main() {
    calcularProyectoReactivo()
}
