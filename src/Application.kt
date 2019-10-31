package pl.senordeveloper

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.features.HttpsRedirect
import io.ktor.network.tls.certificates.*
import kotlinx.coroutines.time.delay
import java.io.File

fun main(args: Array<String>): Unit {
    val file = File("build/temporary.jks")
    if (!file.exists()) {
        file.parentFile.mkdirs()
        generateCertificate(
            file, 
            keyAlias = "alias",
            keyPassword = "changeit",
            jksPassword = "changeit"
        )
    }

    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Authentication) {
    }
    install(HttpsRedirect) {
        sslPort = System.getenv("PORT_SSL")?.toInt() ?: 8443
    }

    routing {
        get("/hello") {
            val user = call.parameters["user"] ?: "World"
            val timeout = call.parameters["timeout"]?.toInt() ?: 5
            kotlinx.coroutines.delay(timeout * 1000L)
            call.respondText("Hello $user!", contentType = ContentType.Text.Plain)
        }
    }
}

