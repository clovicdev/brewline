import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.4.1"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("io.github.apdevteam.github-packages") version "1.2.2"
}

group = "me.clovic"
version = "1.0.0"

val langVersion = 21
val bytecodeVersion = 17
val runTaskJavaVersion = 21
val encoding = "UTF-8"

repositories {
    mavenCentral()
    maven("https://repo.jsinco.dev/releases")
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://ci.ender.zone/plugin/repository/everything/")
    maven("https://repo.minebench.de/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/")
    maven("https://repo.projectshard.dev/repository/releases/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://repo.oraxen.com/releases")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven { githubPackage("apdevteam/movecraft")(this) }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT") {
        exclude("com.google.code.gson", "gson")
    }

    implementation("io.papermc:paperlib:1.0.8")
    implementation("com.zaxxer:HikariCP:7.0.2") {
        exclude("org.slf4j", "slf4j-api")
    }
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.github.Anon8281:UniversalScheduler:0.1.3-dev")
    implementation("org.mongodb:mongodb-driver-sync:5.3.0-beta0")
    implementation("eu.okaeri:okaeri-configs-yaml-snakeyaml:5.0.5") {
        exclude("org.yaml", "snakeyaml")
    }
    constraints {
        implementation("org.yaml:snakeyaml") {
            version {
                require("2.3")
                reject("1.33")
            }
        }
    }

    compileOnly("org.jetbrains:annotations:26.0.1")
    compileOnly("org.apache.logging.log4j:log4j-core:2.23.1")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    compileOnly("com.sk89q:worldguard:6.1")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT")
    compileOnly("com.griefcraft:lwc:2.3.2") {
        exclude("com.google")
    }
    compileOnly("com.github.TechFortress:GriefPrevention:16.18")
    compileOnly(files("lib/LogBlock.jar"))
    compileOnly("com.github.Slimefun:Slimefun4:RC-35")
    compileOnly("io.lumine:MythicLib-dist:1.6-SNAPSHOT")
    compileOnly("com.acrobot.chestshop:chestshop:3.12.2")
    compileOnly("com.palmergames.bukkit.towny:towny:0.100.3.0")
    compileOnly("com.github.Angeschossen:LandsAPI:7.11.10")
    compileOnly("com.nisovin.shopkeepers:ShopkeepersAPI:2.18.0")
    compileOnly("nl.rutgerkok:blocklocker:1.10.4")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("io.th0rgal:oraxen:1.163.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:3.6.1")
    compileOnly("net.countercraft:movecraft:8.0.0_beta-6")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    jar {
        enabled = false
    }

    withType<JavaCompile>().configureEach {
        options.encoding = encoding
        options.release.set(bytecodeVersion)
    }

    test {
        useJUnitPlatform()
    }

    processResources {
        outputs.upToDateWhen { false }
        filter<ReplaceTokens>(
            mapOf(
                "tokens" to mapOf("version" to project.version.toString()),
                "beginToken" to "\${",
                "endToken" to "}"
            )
        ).filteringCharset = encoding
    }

    shadowJar {
        val pack = "me.clovic.brewline.depend"
        exclude("META-INF/maven/**")
        relocate("com.google.gson", "$pack.google.gson")
        relocate("com.google.errorprone", "$pack.google.errorprone")
        relocate("com.github.Anon8281.universalScheduler", "$pack.universalScheduler")
        relocate("eu.okaeri", "$pack.okaeri")
        relocate("com.mongodb", "$pack.mongodb")
        relocate("org.bson", "$pack.bson")
        relocate("io.papermc.lib", "$pack.paperlib")
        relocate("com.zaxxer.hikari", "$pack.hikari")

        archiveBaseName.set("Brewline")
        archiveClassifier.set("")
    }

    runServer {
        minecraftVersion("1.20.6")
    }

    register<Copy>("copyDistJar") {
        dependsOn(shadowJar)
        from(shadowJar.flatMap { it.archiveFile })
        into(layout.projectDirectory.dir("dist"))
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(runTaskJavaVersion)
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(langVersion)
    val includeSources = System.getProperty("sources")
    if (includeSources != null && includeSources.toBoolean()) {
        withSourcesJar()
    }
}
