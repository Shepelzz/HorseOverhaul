
plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.2.0"
}

group = "club.tesseract"
version = "2.0.2"


repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"){
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    implementation("net.kyori:adventure-api:4.10.0")
    implementation("net.kyori:adventure-text-minimessage:4.10.0")
    implementation("net.kyori:adventure-platform-bukkit:4.2.0")

    compileOnly("org.spigotmc:spigot-api:1.14.4-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:16.0.2")
}

tasks{
    test {
        workingDir = file("./run")
    }
    shadowJar{
        archiveBaseName.set(project.name)
        archiveClassifier.set("")
    }

    runServer{
        minecraftVersion("1.20.2")
    }

    processResources {
        filter<org.apache.tools.ant.filters.ReplaceTokens>("tokens" to mapOf("version" to project.version))
    }
    compileJava{
        options.encoding = "UTF-8"
    }
    compileTestJava{
        options.encoding = "UTF-8"
    }
    javadoc{
        options.encoding = "UTF-8"
    }
}


java {
    targetCompatibility = JavaVersion.VERSION_11
    sourceCompatibility = JavaVersion.VERSION_11
}
