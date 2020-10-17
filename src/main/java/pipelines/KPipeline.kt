package pipelines

import configurations.WordCountOptions
import extensions.countPerElement
import extensions.flatMap
import extensions.map
import extensions.toText
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.options.PipelineOptions
import org.apache.beam.sdk.options.PipelineOptionsFactory

sealed class KPipeline {
    abstract var name: String

    abstract fun run(args: Array<String>)

    inline fun <reified R : PipelineOptions> from(args: Array<String>): Pair<Pipeline, R> {
        val options = PipelineOptionsFactory.fromArgs(*args)
            .withValidation()
            .`as`(R::class.java)
        return Pipeline.create(options) to options
    }

    companion object {
        fun withName(name: String): KPipeline? {
            return KPipeline::class.sealedSubclasses
                .map { it.objectInstance }
                .firstOrNull { it?.name == name }
        }
    }

    object WordCountPipeline : KPipeline() {
        private const val TOKENIZER_PATTERN: String = "[^\\p{L}]+"
        override var name = "WordCountPipeline"

        override fun run(args: Array<String>) {
            val (pipe, options) = from<WordCountOptions>(args)

            pipe.apply(TextIO.read().from(options.inputFile))
                .flatMap { it.split(Regex(TOKENIZER_PATTERN)).filter { it.isNotEmpty() }.toList() }
                .countPerElement()
                .map { "${it.key}: ${it.value}" }
                .toText(filename = options.output)

            pipe.run().waitUntilFinish()
        }
    }
}
