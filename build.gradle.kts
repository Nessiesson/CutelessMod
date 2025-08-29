plugins {
	id("java")
	id("xyz.wagyourtail.unimined") version "1.4.1"
}

val groupName: String by project
val version: String by project
val modName: String by project
val modId = modName.lowercase()
val loadingPlugin = "$groupName.$modId.${modName}LoadingPlugin"

project.version = version
project.group = "$groupName.$modId"
base.archivesName = modName

tasks.withType<JavaCompile>().configureEach {
	options.release = 8
}

val jarLibs: Configuration by configurations.creating {
	configurations.implementation.get().extendsFrom(this)
}

repositories {
	unimined.wagYourMaven("releases")
	unimined.spongeMaven()
	unimined.curseMaven()
	flatDir { dir(file("libs")); content { includeGroup("extra-mods") } }
	mavenCentral()
}

unimined.minecraft {
	version("1.12.2")
	mappings {
		searge()
		mcp("stable", "39-1.12")
	}

	minecraftForge {
		loader("14.23.5.2860")
		mixinConfig(
			"mixins.$modId.json",
			"mixins.$modId.chunk_display.json",
			"mixins.$modId.clientcommands.json",
			"mixins.$modId.nothirium.json",
			"mixins.$modId.optifine.json",
			"mixins.$modId.xray.json",
		)
		accessTransformer(file("src/main/resources/at.conf"))
	}

	runs.config("client") {
		jvmArgs(
			"-Dfml.coreMods.load=$loadingPlugin",
			"-Dmixin.debug.export=true",
			"-Dmixin.debug.verbose=true"
		)
	}

	runs.config("server") {
		enabled = false
	}
}

dependencies {
	implementation("org.spongepowered:mixin:0.8.7")
	//"modImplementation"("curse.maven:renderlib-624967:4168831")

	// runtime remapping at home - stolen from embeddedt
	for (extraModJar in fileTree(mapOf("dir" to "libs", "include" to "*.jar"))) {
		val basename = extraModJar.name.substring(0, extraModJar.name.length - ".jar".length)
		val versionSep = basename.lastIndexOf("-")
		assert(versionSep != -1)
		val artifactId = basename.take(versionSep)
		val version = basename.substring(versionSep + 1)
		"modImplementation"("extra-mods:$artifactId:$version")
	}
}

tasks {
	jar {
		manifest.attributes(
			mapOf(
				"TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
				"FMLCorePluginContainsFMLMod" to true,
				"FMLCorePlugin" to loadingPlugin,
				"ForceLoadAsMod" to true,
			)
		)

		from(jarLibs.map { if (it.isDirectory()) it else zipTree(it) }) {
			exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
		}
	}

	processResources {
		duplicatesStrategy = DuplicatesStrategy.INCLUDE
		inputs.property("version", project.version)

		filesMatching("mcmod.info") {
			expand("version" to project.version)
		}
	}

	withType<JavaCompile>().configureEach { options.compilerArgs.add("-Xlint:-options") }

	withType<JavaExec>().configureEach {
		environment("LD_LIBRARY_PATH", "${System.getenv("LD_LIBRARY_PATH").orEmpty()}:/run/current-system/sw/lib")
		environment("__GL_THREADED_OPTIMIZATIONS", "0")
	}
}
