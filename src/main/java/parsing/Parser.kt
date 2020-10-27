package parsing

@ExperimentalStdlibApi
object Parser {
    private val logger by Logging.logger { }

    private val csv = Csv()

    fun parse() {
        val parsedA = csv.decodeFromString<SuperClass>(
            """
            IDENTITY,NAME,blob,SHAPE
            1,tatum,poop,sqr
            """
        )
        val parsedB = csv.decodeFromString<SuperClass>(
            """
            IDENTITY,NAME,blob,COLOUR
            1,tatum,poop,prp
            """
        )
        logger.info(parsedA[0].toString())
        logger.info(parsedB[0].toString())
    }
}

sealed class SuperClass {
    @SerialName("IDENTITY")
    abstract val id: String
    @SerialName("NAME")
    abstract val name: String
}

data class SubClassA(
    override val id: String,
    override val name: String,
    @SerialName("SHAPE")
    val shape: Shape
) : SuperClass()

data class SubClassB(
    override val id: String,
    override val name: String,
    @SerialName("COLOUR")
    val color: Color
) : SuperClass()

enum class Color {
    @SerialName("ylw")
    YELLOW,
    @SerialName("prp")
    PURPLE
}

enum class Shape {
    @SerialName("circ")
    CIRCLE,
    @SerialName("sqr")
    SQUARE
}
