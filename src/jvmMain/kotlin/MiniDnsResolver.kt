import io.github.jan.dnsplugin.DnsResolver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.minidns.hla.DnssecResolverApi
import org.minidns.hla.ResolverResult
import org.minidns.record.A


object MiniDnsResolver : DnsResolver {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun resolve(hostname: String) = suspendCancellableCoroutine<String> {
        val result: ResolverResult<A> = DnssecResolverApi.INSTANCE.resolve(hostname, A::class.java)
        it.resume(result.answers.first().inetAddress.hostAddress) { error ->
            error.printStackTrace()
        }
    }

}