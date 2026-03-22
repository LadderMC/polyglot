import fr.ladder.releasr.ReleasrContext

plugins {
    id("java")
    id("idea")
    id("fr.ladder.releasr") version "0.3.0"
    id("com.gradleup.shadow") version "9.3.2"
}

group = "fr.ladder.polyglot"

tasks.compileJava {
    options.encoding = "UTF-8"
    dependsOn(":shadowJar")
}

repositories {
    mavenCentral()
    maven {
        name = "maven-public"
        url = uri("https://repo.lylaw.fr/repository/maven-public/")
    }
}

dependencies {
    compileOnly("fr.snowtyy:papermc:1.8.8")

    implementation(rootProject)
}

tasks.shadowJar {
    archiveClassifier.set("")
}


tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    filesMatching("**/*.yml") {
        expand("version" to ReleasrContext.version)
    }
    outputs.upToDateWhen { false }
}