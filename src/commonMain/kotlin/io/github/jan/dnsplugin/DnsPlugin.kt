package io.github.jan.dnsplugin

import io.github.reactivecircus.cache4k.Cache
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.util.AttributeKey
import io.ktor.util.KtorDsl
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class DnsPlugin(private val config: Config) {

    val dnsCache = Cache.Builder()
        .expireAfterWrite(config.cacheTime)
        .build<String, String>()

    @KtorDsl
    class Config(var cacheTime: Duration = 7.minutes, var dnsResolver: DnsResolver)

    companion object : HttpClientPlugin<Config, DnsPlugin> {

        override val key: AttributeKey<DnsPlugin> = AttributeKey("DnsPlugin")

        override fun prepare(block: Config.() -> Unit): DnsPlugin {
            return DnsPlugin(
                Config(
                    dnsResolver = { throw IllegalStateException("No DnsResolver set") }
                ).apply(block)
            )
        }

        override fun install(plugin: DnsPlugin, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                context.url.host = plugin.dnsCache.get(context.url.host) {
                    plugin.config.dnsResolver.resolve(context.url.host)
                }
            }
        }

    }

}