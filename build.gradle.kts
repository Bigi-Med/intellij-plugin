plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.0"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "com.plugin"
version = "1.1-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "16"
    modules("javafx.controls", "javafx.graphics", "javafx.media", "javafx.fxml")
}

dependencies {
    implementation("org.openjfx:javafx-controls:16")
    implementation("org.openjfx:javafx-graphics:16")
    implementation("org.openjfx:javafx-media:16")
    implementation("org.openjfx:javafx-fxml:16")
}


// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2021.3.3")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf(/* Plugin Dependencies */))
}




tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("213")
        untilBuild.set("223.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
