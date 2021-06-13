plugins {
    id("ru.mipt.npm.gradle.project")
    kotlin("multiplatform") apply false
    kotlin("jvm") apply false
    kotlin("js") apply false
    kotlin("jupyter.api") apply false
    id("ru.mipt.npm.gradle.js") apply false
}

val dataforgeVersion by extra("0.4.2")
val kotlinWrappersVersion by extra("pre.206-kotlin-1.5.10")
val fxVersion by extra("11")

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven("https://repo.kotlin.link")
        maven("https://maven.jzy3d.org/releases")
    }

    group = "space.kscience"
    version = "0.2.0-dev-20"
}

subprojects {
    if (name.startsWith("visionforge")) {
        plugins.apply("maven-publish")
    }
}

ksciencePublish{
    github("visionforge")
    space()
    sonatype()
}

apiValidation {
    validationDisabled = true
    ignoredPackages.add("info.laht.threekt")
}