package parsing

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention
annotation class SerialName(val value: String)
