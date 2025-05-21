plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.spring") version "2.1.10"
    id("org.hibernate.orm") version "6.5.2.Final"
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.5"
}

group = "space.mori.dalbodeule"
version = "0.5.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

hibernate {
    enhancement {
        enableAssociationManagement.set(false)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-batch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    implementation("io.swagger.core.v3:swagger-core:2.2.30")
    implementation("io.swagger.core.v3:swagger-annotations:2.2.30")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("org.postgresql:postgresql:42.7.4")

    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("javax.xml.bind:jaxb-api:2.3.1")

    // HTTP 클라이언트
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation(rootProject)

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.batch:spring-batch-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(kotlin("test"))
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    systemProperty("spring.profiles.active", "dev")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
