import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


buildscript {
    var kotlin_version: String = "1.2.40"

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }

}

subprojects {

    group = "pt.um.tf.lab2"
    version = "1.0-SNAPSHOT"

    apply {
        plugin("kotlin")
        plugin("application")
    }

    repositories {
        mavenCentral()

    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.10"
    }
}

