/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    `java-library`
    `maven-publish`
    id("co.uzzu.dotenv.gradle") version "4.0.0"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

publishing {
    repositories {
        maven {
            name = "Gitea"
            url = uri(
                "https://git.mori.space/api/packages/${env.GITEA_USERNAME.value}/maven"
            )

            credentials(HttpHeaderCredentials::class) {
                name = "Authorization"
                value = "token ${env.GITEA_TOKEN.value}"
            }

            authentication {
                create<HttpHeaderAuthentication>("header")
            }
        }
    }
}

dependencies {
    api(libs.org.apache.poi.poi)
    api(libs.org.apache.poi.poi.ooxml)
    api(libs.org.apache.tika.tika.core)
    api(libs.org.springframework.boot.spring.boot.starter.data.jpa)
    api(libs.org.springframework.boot.spring.boot.starter.thymeleaf)
    api(libs.org.springframework.boot.spring.boot.starter.jdbc)
    api(libs.com.h2database.h2)
    api(libs.org.apache.commons.commons.csv)
    api(libs.org.springframework.boot.spring.boot.starter.validation)
    api(libs.org.springframework.boot.spring.boot.starter.web)
    api(libs.org.springframework.boot.spring.boot.configuration.processor)
    api("io.swagger.core.v3:swagger-annotations:2.2.15")

    testImplementation(libs.org.springframework.boot.spring.boot.starter.test)
}

group = "space.mori.dalbodeule"
version = env.VERSION.value
description = "SnapAdmin"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
