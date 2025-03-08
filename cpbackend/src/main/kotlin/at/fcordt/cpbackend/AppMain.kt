package at.fcordt.cpbackend

import at.fcordt.cpbackend.apis.defaultApi
import at.fcordt.cpbackend.services.AccessControlList
import at.fcordt.cpbackend.services.InMemoryWhiteList
import com.codahale.metrics.Slf4jReporter
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.metrics.dropwizard.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.hsts.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("unused")
fun Application.main() {
    val appModule = module {
        //for now only a single impl class - maybe later on we want different backend queues, so let's DI it as Interface
        factory<AccessControlList> {
            InMemoryWhiteList(
                environment.config.property("whitelist.allowed_users").getString().split(','),
                environment.config.property("whitelist.allowed_stations").getString().split(',').map { x -> UUID.fromString(x) }
            )
        }
    }


    install(DefaultHeaders)
    install(DropwizardMetrics) {
        val reporter = Slf4jReporter.forRegistry(registry)
            .outputTo(this@main.log)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
        reporter.start(10, TimeUnit.SECONDS)
    }
    install(ContentNegotiation) {
        json()
    }
    install(AutoHeadResponse) // see https://ktor.io/docs/autoheadresponse.html
    install(Compression, applicationCompressionConfiguration()) // see https://ktor.io/docs/compression.html
    install(HSTS, applicationHstsConfiguration()) // see https://ktor.io/docs/hsts.html
    install(Resources)
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    routing {
        defaultApi()
    }

}
