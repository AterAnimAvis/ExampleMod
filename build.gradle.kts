/*================================================================================================== BuildScript ==== */
buildscript {
    repositories {
        maven(url = "https://files.minecraftforge.net/maven")
        jcenter()
        mavenCentral()
        maven(url = "https://repo.spongepowered.org/repository/maven-public/")
    }

    dependencies {
        classpath(group = "net.minecraftforge.gradle", name = "ForgeGradle", version = "3.+") { isChanging = true }
        classpath(group = "org.spongepowered", name = "mixingradle", version = "0.7-SNAPSHOT")
    }
}

/*========================================================================================== Config -> Minecraft ==== */
val forgeVersion: String by extra
val mappingsChannel: String by extra
val mappingsVersion: String by extra
val minecraftVersion: String by extra

/*================================================================================================ Config -> Mod ==== */
val modId: String by extra
val modVersion: String by extra
val modGroup: String by extra
val vendor: String by extra

/*========================================================================================= Config -> Run Config ==== */
val level: String by extra
val markers: String by extra

/*====================================================================================================== Plugins ==== */
plugins {
    `java-library`
}

apply(plugin = "net.minecraftforge.gradle")
apply(plugin = "org.spongepowered.mixin")

/*===================================================================================================== JVM Info ==== */

println("Java: ${System.getProperty("java.version")}" +
        " JVM: ${System.getProperty("java.vm.version")}(${System.getProperty("java.vendor")})" +
        " Arch: ${System.getProperty("os.arch")}"
)

/*========================================================================================= Minecraft Dependency ==== */

/* Note: Due to the way kotlin gradle works we need to define the minecraft dependency before we configure Minecraft */
dependencies {
    "minecraft"(group = "net.minecraftforge", name = "forge", version = "$minecraftVersion-$forgeVersion")
}

/*==================================================================================================== Minecraft ==== */

minecraft {
    mappingChannel = mappingsChannel
    mappingVersion = mappingsVersion

    runs {
        config("client")

        config("server")

        config("data") {
            args("--mod", modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
        }
    }
}

/*======================================================================================================== Setup ==== */
project.group = modGroup
project.version = "$minecraftVersion-$modVersion"

/* Java 8 Target */
tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

/* Mixins */
the<MixinExtension>().add(sourceSets["main"], "$modId.refmap.json")

/* Finalize the jar by Reobf */
tasks.named<Jar>("jar") { finalizedBy("reobfJar") }

/* Manifest */
tasks.withType<Jar> {
    manifest {
        attributes(
                "Specification-Title" to modId,
                "Specification-Vendor" to vendor,
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Implementation-Vendor" to vendor,
                "Implementation-Timestamp" to Date().format("yyyy-MM-dd'T'HH:mm:ssZ"),
                "MixinConfigs" to "$modId.mixins.json"
        )
    }
}

/* Generated Resources */
sourceSets["main"].resources.srcDir("src/generated/resources")

/*==================================================================================================== Utilities ==== */

typealias Date = java.util.Date
typealias SimpleDateFormat = java.text.SimpleDateFormat

fun Date.format(format: String) = SimpleDateFormat(format).format(this)

typealias RunConfig = net.minecraftforge.gradle.common.util.RunConfig
typealias UserDevExtension = net.minecraftforge.gradle.userdev.UserDevExtension

typealias MixinExtension = org.spongepowered.asm.gradle.plugins.MixinExtension

typealias RunConfiguration = RunConfig.() -> Unit

fun minecraft(configuration: UserDevExtension.() -> Unit) =
        configuration(extensions.getByName("minecraft") as UserDevExtension)

fun NamedDomainObjectContainerScope<RunConfig>.config(name: String, additionalConfiguration: RunConfiguration = {}) {
    val runDirectory = project.file("run")
    val sourceSet = the<JavaPluginConvention>().sourceSets["main"]

    create(name) {
        workingDirectory(runDirectory)
        property("forge.logging.markers", markers)
        property("forge.logging.console.level", level)
        property("mixin.env.disableRefMap", "true")
        arg("--mixin.config=$modId.mixins.json")

        additionalConfiguration(this)

        mods { create(modId) { source(sourceSet) } }
    }
}