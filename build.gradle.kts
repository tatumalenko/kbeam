import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.10"
    idea
}

group = "io.tatumalenko.kbeam"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    google()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
    implementation("org.apache.beam:beam-sdks-java-core:2.6.0")
    implementation("org.apache.beam:beam-runners-direct-java:2.6.0")
    implementation("org.slf4j:slf4j-api:1.7.28")
    implementation("org.slf4j:slf4j-jdk14:1.7.28")
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(kotlin("test-junit5"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    sourceSets["main"].apply {
        kotlin.srcDir("src/main/java")
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val jobClass: Any? by project
val options: Any? by project
val runJob by tasks.creating(JavaExec::class) {
    val opts = options?.let { (it as String).split(Regex("(\n|\\s)")) }?.filter { it.isNotBlank() }?.map { it.trim() }
        ?: listOf()
    args = opts
    main = "$jobClass"
    classpath = sourceSets["main"].runtimeClasspath

    doFirst {
        println("* main job class  : $jobClass")
        println("* pipeline options: \n${opts.joinToString("\n")}\n")
    }
}

runJob.dependsOn("compileKotlin")
