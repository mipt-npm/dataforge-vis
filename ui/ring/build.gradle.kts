plugins {
    id("ru.mipt.npm.gradle.js")
}

val dataforgeVersion: String by rootProject.extra

kotlin{
    js{
        useCommonJs()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = false
            }
        }
    }
}

dependencies{
    api(project(":ui:react"))

    implementation(npm("@jetbrains/icons", "3.14.1"))
    implementation(npm("@jetbrains/ring-ui", "4.0.12"))
    implementation(npm("core-js","3.12.1"))
    implementation(npm("file-saver", "2.0.2"))
    compileOnly(npm("url-loader","4.1.1"))
    compileOnly(npm("postcss-loader","5.2.0"))
    compileOnly(npm("source-map-loader","2.0.1"))
}