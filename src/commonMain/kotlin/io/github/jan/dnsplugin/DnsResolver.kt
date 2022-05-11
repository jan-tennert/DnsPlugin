package io.github.jan.dnsplugin

fun interface DnsResolver {

    /**
     * Resolves the given hostname to an IP address.
     */
    suspend fun resolve(hostname: String): String

}