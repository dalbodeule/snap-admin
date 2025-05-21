package space.mori.dalbodeule.debug

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import space.mori.dalbodeule.snapadmin.external.annotations.SnapAdminEnabled

val dotenv = dotenv {
    ignoreIfMissing = true
}

@SnapAdminEnabled
@SpringBootApplication
@EnableJpaRepositories(basePackages = ["space.mori.dalbodeule.debug.repository"])
@EntityScan(basePackages = ["space.mori.dalbodeule.debug.model"])
class DebugApplication

fun main(args: Array<String>) {
    val envVars = mapOf(
        "DB_HOST" to dotenv["DB_HOST"],
        "DB_PORT" to dotenv["DB_PORT"],
        "DB_NAME" to dotenv["DB_NAME"],
        "DB_USER" to dotenv["DB_USER"],
        "DB_PASSWORD" to dotenv["DB_PASSWORD"]
    )
    
    runApplication<DebugApplication>(*args) {
        setDefaultProperties(envVars)
    }
}