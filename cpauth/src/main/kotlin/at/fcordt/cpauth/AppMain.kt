package at.fcordt.cpauth

import at.fcordt.cpauth.apis.DefaultApi
import at.fcordt.cpauth.services.AuthQueueProvider
import at.fcordt.cpauth.services.AuthQueueProviderImpl
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
import java.util.concurrent.TimeUnit

fun Application.main() {
    val appModule = module {
        //for now only a single impl class - maybe later on we want different backend queues, so let's DI it as Interface
        factory<AuthQueueProvider> {
            AuthQueueProviderImpl(
                environment.config.propertyOrNull("kafka.bootstrap_server")?.getString() ?: "localhost:9092",
                environment.config.propertyOrNull("kafka.topic")?.getString() ?: "cpauth",
                environment.config.propertyOrNull("kafka.insertion_timeout_ms")?.getString()?.toLong() ?: 1000L,
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
    install(Compression, ApplicationCompressionConfiguration()) // see https://ktor.io/docs/compression.html
    install(HSTS, ApplicationHstsConfiguration()) // see https://ktor.io/docs/hsts.html
    install(Resources)
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
    routing {
        DefaultApi()
    }

}
