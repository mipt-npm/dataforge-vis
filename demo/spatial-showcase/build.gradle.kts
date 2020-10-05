import ru.mipt.npm.gradle.DependencyConfiguration
import ru.mipt.npm.gradle.FXModule
import ru.mipt.npm.gradle.useFx

plugins {
    id("ru.mipt.npm.mpp")
    application
}

kscience {
    val fxVersion: String by rootProject.extra
    useFx(FXModule.CONTROLS, version = fxVersion, configuration = DependencyConfiguration.IMPLEMENTATION)
    application()
}

kotlin {

    jvm {
        withJava()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":visionforge-solid"))
                api(project(":visionforge-gdml"))
            }
        }
    }
}

application {
    mainClassName = "hep.dataforge.vision.solid.demo.FXDemoAppKt"
}