buildscript {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
    @Suppress("UNUSED_VARIABLE")
    val pluginVersion by extra("0.1.4")
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.1")
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
        classpath("icu.nullptr.stringfuck:gradle-plugin:$pluginVersion")
    }
}

val pluginGroup by extra("icu.nullptr.stringfuck")

val androidMinSdk by extra(24)
val androidTargetSdk by extra(32)

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.register("publishLocal") {
    dependsOn(":library:publishToMavenLocal")
    dependsOn(":plugin:publishToMavenLocal")
}
