package at.fcordt.cpauth

import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.hsts.*
import java.util.concurrent.TimeUnit

internal fun applicationHstsConfiguration(): HSTSConfig.() -> Unit {
    return {
        maxAgeInSeconds = TimeUnit.DAYS.toSeconds(365)
        includeSubDomains = true
        preload = false
    }
}

/**
 * Application block for [Compression] configuration.
 *
 * This file may be excluded in .openapi-generator-ignore,
 * and application-specific configuration can be applied in this function.
 *
 * See http://ktor.io/features/compression.html
 */
internal fun applicationCompressionConfiguration(): CompressionConfig.() -> Unit {
    return {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
}
