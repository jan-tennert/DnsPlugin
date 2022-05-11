import io.github.jan.dnsplugin.DnsResolver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.net.InetAddress

object JvmDnsResolver: DnsResolver {

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun resolve(hostname: String) = suspendCancellableCoroutine<String> {
        it.resume(InetAddress.getByName(hostname).hostAddress) { error ->
            error.printStackTrace()
        }
    }

}