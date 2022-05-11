# DNS Plugin

A Ktor plugin for resolving and caching DNS queries when making a request with ktor.

## Installation

```kotlin
implementation("io.github.jan-tennert.dnsplugin:DnsPlugin:1.1")
```

## Usage

```kotlin
val http = HttpClient(CIO) {
    
    install(DnsPlugin) {
        cacheTime = 8.minutes
        dnsResolver = MiniDnsResolver //also available: JvmDnsResolver
    }
    
}
```