package chatgpt.kt.bot.app.infra.gpt

import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.InetSocketAddress
import java.net.Proxy
import java.time.Duration

@Configuration
open class ChatgptCfg {

    @Bean
    open fun chatgptOkHttpClient(properties: ChatGptProperties): OkHttpClient {
        val b = OkHttpClient().newBuilder()
            .callTimeout(Duration.ofMinutes(2))
            .readTimeout(Duration.ofMinutes(2))
            .writeTimeout(Duration.ofMinutes(2))
            .connectTimeout(Duration.ofMinutes(2))
        properties.proxyAddr?.also {
            val proxy = it.split(":")
            b.proxy(Proxy(Proxy.Type.SOCKS, InetSocketAddress(proxy[0], proxy[1].toInt())))
        }
        return b.build()
    }
}