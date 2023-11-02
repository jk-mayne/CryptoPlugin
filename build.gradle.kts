import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "org.jkmayne"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //implementation("net.portswigger.burp.extensions:montoya-api:2023.3")
    
    //you're gonna have to replace these
    //implementation(files("REDACTED/montoya-api-2023.10.3.jar"))
    //implementation(files("REDACTED/bc-noncert-1.0.2.4.jar")) 
    
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("org.bouncycastle:bcprov-jdk16:1.45")
   // implementation("com.fasterxml.jackson.core:jackson-databind:2.12.71")
   // implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
   // implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
   // implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.15.2")
   // implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}