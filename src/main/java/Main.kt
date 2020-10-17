import pipelines.KPipeline

object Main {
    private const val DEFAULT_PIPELINE_NAME = "WordCountPipeline"

    @JvmStatic
    fun main(args: Array<String>) {
        val pipelineObjectName = args
            .firstOrNull { it.startsWith("--pipeline=") }
            ?.replace("--pipeline=", "")
            ?: DEFAULT_PIPELINE_NAME

        println("pipelineObjectName: $pipelineObjectName")
        KPipeline.withName(pipelineObjectName)?.run(args) ?: println("No arg found with valid pipeline object name.")
    }
}
