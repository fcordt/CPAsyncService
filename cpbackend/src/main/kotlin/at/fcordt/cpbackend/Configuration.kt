package at.fcordt.cpbackend

// Use this file to hold package-level internal functions that return receiver object passed to the `install` method.
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

