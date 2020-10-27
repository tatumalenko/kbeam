package extensions

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses

inline fun <reified T : Annotation, reified R : Any> KClass<in R>.findSuperConstructorParameterAnnotation(
    parameter: KParameter
): T? =
    parameter.findAnnotation<T>() ?: this.memberProperties.firstOrNull {
        it.name.toLowerCase() == parameter.name?.toLowerCase()
    }?.findAnnotation<T>() ?: this.superclasses.flatMap { superclass ->
        superclass.constructors.flatMap { it.parameters }
    }.firstOrNull {
        it.name?.toLowerCase() == parameter.name?.toLowerCase()
    }?.findAnnotation<T>() ?: this.superclasses.flatMap { superclass ->
        superclass.memberProperties
    }.firstOrNull {
        it.name.toLowerCase() == parameter.name?.toLowerCase()
    }?.findAnnotation<T>()
