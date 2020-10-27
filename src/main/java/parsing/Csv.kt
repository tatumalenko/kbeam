package parsing

import extensions.findSuperConstructorParameterAnnotation
import kotlin.reflect.KFunction
import kotlin.reflect.javaType

class Csv(
    val csvConfiguration: CsvConfiguration = CsvConfiguration()
) {
    @ExperimentalStdlibApi
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> decodeFromString(input: String): List<T> {
        val (header, rows) = input.trimIndent()
            .replace("\n", csvConfiguration.rowDelimiter)
            .split(csvConfiguration.rowDelimiter)
            .let { lines ->
                val headerColumns = lines.first().split(csvConfiguration.columnDelimiter)
                Pair(
                    headerColumns,
                    lines.drop(1).mapIndexed { index, row ->
                        row.split(csvConfiguration.columnDelimiter).let { rowColumns ->
                            if (rowColumns.size != headerColumns.size) {
                                throw IllegalStateException("Number of columns (${headerColumns.size}) in header is different from number of columns (${rowColumns.size}) in row number ${index + 2}")
                            }
                            rowColumns
                        }
                    }
                )
            }

        return when {
            T::class.isData -> decodeDataFromString(header, rows, T::class.constructors.first())
            T::class.isSealed -> decodeSealedFromString(header, rows)
                ?: throw IllegalStateException("No sealed subclasses for ${T::class.simpleName} could be parsed.")
            else -> throw IllegalStateException("decodeFromString can only be used on data classes and sealed class hierarchies")
        }
    }

    @ExperimentalStdlibApi
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> decodeSealedFromString(header: List<String>, rows: List<List<String>>): List<T>? {
        val sealedSubclasses = T::class.sealedSubclasses
        return sealedSubclasses.mapNotNull { sealedSubclass ->
            try {
                decodeDataFromString(header, rows, sealedSubclass.constructors.first())
            } catch (e: Exception) {
                // TODO: log exception in debug
                null
            }
        }.firstOrNull()
    }

    @ExperimentalStdlibApi
    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Any> decodeDataFromString(
        header: List<String>, rows: List<List<String>>,
        ctor: KFunction<T>
    ): List<T> {
        val parameters = ctor.parameters
        val indexMap = parameters.map { parameter ->
            val name =
                T::class.findSuperConstructorParameterAnnotation<SerialName, T>(parameter)?.value?.toLowerCase()
                    ?: parameter.name?.toLowerCase()
            when (val index = header.indexOfFirst { it.toLowerCase() == name }) {
                -1 -> throw IllegalStateException("Missing parameter: ${parameter.name}")
                else -> parameter to index
            }
        }.toMap()

        return rows.map { columns ->
            val valueMap = indexMap.mapValues { (parameter, index) ->
                val value = columns[index]
                val enumClass = parameter.type.javaType as? Class<out Enum<*>>
                when {
                    enumClass?.isEnum ?: false -> {
                        val enumConstants = enumClass?.enumConstants
                        val enumField = enumClass?.declaredFields?.first { declaredField ->
                            declaredField.annotations.any { annotation ->
                                (annotation as? SerialName)?.value == value.toLowerCase()
                            }
                        }
                        val enumConstant =
                            enumConstants?.firstOrNull { enumField?.name == it.name || it.name.toLowerCase() == value.toLowerCase() }
                        enumConstant
                    }
                    else -> {
                        // TODO: implement type casting (Int)
                        value
                    }
                }
            }
            ctor.callBy(valueMap)
        }
    }
}
