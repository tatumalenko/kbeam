package configurations

import org.apache.beam.sdk.options.Default
import org.apache.beam.sdk.options.Description
import org.apache.beam.sdk.options.PipelineOptions

interface WordCountOptions : PipelineOptions {
    @get:Description("Path of the file to read pipelines.from")
    @get:Default.String("./src/main/resources/input.txt")
    var inputFile: String

    @get:Description("Path of the file to write to")
    @get:Default.String("./output.txt")
    var output: String

    @get:Description("Pipeline")
    @get:Default.String("WordCountPipeline")
    var pipeline: String
}
