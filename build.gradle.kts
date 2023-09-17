plugins {
    id("java")
    // ShadowJar (https://github.com/johnrengelman/shadow/releases)
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.zax71"
version = "1.2.0"

repositories {
    mavenCentral()
    // mavenLocal()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven("https://jitpack.io")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    // Velocity
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")

    // MiniMessage
    implementation("net.kyori:adventure-text-minimessage:4.13.1")

    // Endercube common lib
    implementation("com.github.ender-cube:endercubecommon:1e8da50caf")
    // implementation("net.endercube:EndercubeCommon:1.1.3")

    // HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Redis (Jedis)
    implementation("redis.clients:jedis:4.3.0")

    // MariaDB
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")

    // JSON stuff
    implementation("com.google.code.gson:gson:2.10.1")

    // ACF
    implementation("co.aikar:acf-velocity:0.5.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "me.zax71.endercubeProxyPlugin.EndercubeProxyPlugin"
        }
    }
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        relocate("co.aikar.commands", "me.zax71.endercubeProxyPlugin.acf")
        relocate("co.aikar.locales", "me.zax71.endercubeProxyPlugin.locales")
        mergeServiceFiles()
        archiveClassifier.set("") // Prevent the -all suffix
    }
}