plugins {
    id("org.springframework.boot") version "3.1.3"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.spring") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    id("org.graalvm.buildtools.native") version "0.9.25"
}

configurations.all {
    // Logback + the XML infrastructure that it makes reachable are worth to exclude to get a smaller native footprint
    exclude(module = "spring-boot-starter-logging")
    // We use Kotlin Serialization so no need for Jackson and kotlin-reflect
    //exclude(module = "spring-boot-starter-json")
    // We use tomcat-embed-programmatic instead
    exclude(module = "tomcat-embed-core")
    exclude(module = "tomcat-embed-websocket")
}

dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
//    implementation("com.fasterxml.jackson.core:jackson-databind:")
//    implementation("com.fasterxml.jackson.core:jackson-annotations:")
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("org.springframework.boot:spring-boot-starter-web:") {
        exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-core")
        exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-websocket")
    }
    implementation("org.apache.tomcat.experimental:tomcat-embed-programmatic:${dependencyManagement.importedProperties["tomcat.version"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("com.slack.api:bolt-jakarta-servlet:1.30.0")
    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

//tasks.withType<KotlinCompile> {
//    compilerOptions {
//        freeCompilerArgs = listOf("-Xjsr305=strict")
//        jvmTarget = JvmTarget.JVM_17
//    }
//}

tasks.withType<Test> {
    useJUnitPlatform()
}


//application {
//    mainClass.set("com.razielez.app.AppKt")
//}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("chatgpt-kt-bot.jar")
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}

//graalvmNative {
//
//    agent {
//        defaultMode.set("standard")
//
//        modes {
//            conditional {
//                userCodeFilterPath.set("user-code-filter.json")
//            }
//        }
//
//        metadataCopy {
//            mergeWithExisting.set(true)
//            inputTaskNames.add("test")
//            outputDirectories.add("build/native/metadataCopyTest")
//        }
//
//    }
//
//    toolchainDetection.set(true)
//
//    binaries {
//        named("main") {
//            javaLauncher.set(javaToolchains.launcherFor {
//                languageVersion.set(JavaLanguageVersion.of(17))
//                vendor.set(JvmVendorSpec.ORACLE)
//            })
//            useFatJar.set(true)
//        }
//    }
//    testSupport.set(false)
//}

repositories {
    mavenCentral()
}