# KBeam

> An Apache Beam proof of concept using Kotlin

This repo aims to study the use of Kotlin with Apache Beam's Java SDK. Much of the WordCount example was inspired by [Apache Beam kotlin blog examples](https://github.com/Dan-Dongcheol-Lee/apachebeam-kotlin-blog-examples).

Multiple pipelines can be defined by implementing additional sealed `KPipeline` subclass object instances defined in `pipelines/KPipeline.kt`:
```kotlin
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
```

Then, depending on the program arguments passed, namely of the form `--pipeline=WordCountPipeline`, the `Main.main` will dispatch the appropriate sealed subclass object instance defined previously:

A build script is available to facilitate the CLI flow:
```sh
$ ./make.sh <run|build> <pipelineName> [<...otherPipelineOptions>]
$ ./make.sh build
$ ./make.sh run Main WordCountPipeline
$ ./make.sh run Main WordCountPipeline --inputFile=./src/main/resources/input.txt --output=./output.txt
```
