import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.logging.LogManager

object Logging {
    private var configured = false

    fun logger(lambda: () -> Unit): Lazy<Logger> = lazy {
        if (!configured) {
            configure()
        }
        LoggerFactory.getLogger(getClassName(lambda.javaClass))
    }

    private fun configure() {
        val configFile = {}::class.java.classLoader.getResource("logging.properties")
        configFile?.readText()?.byteInputStream()?.let { LogManager.getLogManager().readConfiguration(it) }
        configured = true
    }

    private fun <T : Any> getClassName(clazz: Class<T>): String = clazz.name.replace(Regex("""\$.*$"""), "")

    inline fun <reified T : Any> Logger.info(value: T) {
        info(value.toString())
    }

    inline fun <reified T : Any> Logger.warn(value: T) {
        warn(value.toString())
    }

    inline fun <reified T : Any> Logger.debug(value: T) {
        debug(value.toString())
    }

    inline fun <reified T : Any> Logger.trace(value: T) {
        trace(value.toString())
    }

    inline fun <reified T : Any> Logger.error(value: T) {
        error(value.toString())
    }

    inline fun <reified T : Any> Logger.error(value: T, throwable: Throwable) {
        error(value.toString(), throwable)
    }
}
